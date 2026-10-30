package org.humanjourney.core.npc;
import com.google.gson.*;
import java.net.URI;
import java.util.*;
import java.util.function.ToIntFunction;

/** Immutable, version-independent content. Easy NPC types never enter this format. */
public record NpcTemplate(String id,String name,String role,String title,String skinUrl,boolean stationary,boolean invulnerable,
                          String main,List<Page> pages,List<Trade> trades,int resetMinutes){
    public record Item(String id,int count){}
    public record Trade(Item input,Item second,Item output,int uses,int maxUses){}
    public record Condition(String objective,String operator,int value){}
    public record Action(String type,String argument,String operation,int value,int permission,boolean asPlayer,List<Condition> conditions){public Action{conditions=List.copyOf(conditions);}}
    public record Button(String id,String text,List<Condition> conditions,List<Action> actions){public Button{conditions=List.copyOf(conditions);actions=List.copyOf(actions);}}
    public record Page(String id,String text,List<Condition> conditions,List<Button> buttons){public Page{conditions=List.copyOf(conditions);buttons=List.copyOf(buttons);}}
    public NpcTemplate{pages=List.copyOf(pages);trades=List.copyOf(trades);}
    public String displayName(String override){return (override==null?name:validName(override))+(title.isBlank()?"":" — "+title);}
    public static String validName(String value){if(value==null||value.isBlank()||value.length()>64||value.codePoints().anyMatch(Character::isISOControl))throw new IllegalArgumentException("name must contain 1..64 visible characters");return value.trim();}
    public static NpcTemplate parse(String resourceId,JsonElement json){
        id(resourceId);JsonObject r=object(json,"template");keys(r,"schema_version","id","name","role","title","skin_url","behavior","dialogue","trading");
        if(integer(r,"schema_version",1,1,1)!=1)throw new IllegalArgumentException("unsupported schema_version");
        if(r.has("id")&&!text(r,"id",null,256).equals(resourceId))throw new IllegalArgumentException("template id must match its resource path: "+resourceId);
        String name=validName(text(r,"name",null,64)),role=text(r,"role","",64),title=text(r,"title","",64),skin=text(r,"skin_url",null,2048);
        try{URI u=URI.create(skin);if(!Set.of("https","http").contains(u.getScheme())||u.getHost()==null||u.getUserInfo()!=null||u.getFragment()!=null)throw new Exception();}catch(Exception e){throw new IllegalArgumentException("skin_url must be a direct http(s) URL without credentials/fragment");}
        JsonObject b=section(r,"behavior"),d=section(r,"dialogue"),t=section(r,"trading");keys(b,"stationary","invulnerable");keys(d,"main","pages");keys(t,"reset_minutes","offers");
        String main=label(text(d,"main","main",32));List<Page> pages=new ArrayList<>();Set<String> ids=new HashSet<>();
        for(JsonElement element:array(d,"pages",true,16)){
            var p=object(element,"dialogue page");keys(p,"id","text","conditions","buttons");String pageId=label(text(p,"id",null,32));if(!ids.add(pageId))throw new IllegalArgumentException("duplicate dialogue id: "+pageId);
            List<Button> buttons=new ArrayList<>();Set<String> buttonsSeen=new HashSet<>();
            for(JsonElement entry:array(p,"buttons",false,12)){
                var button=object(entry,"button");keys(button,"id","text","conditions","actions");String buttonId=label(text(button,"id",null,32));if(!buttonsSeen.add(buttonId))throw new IllegalArgumentException("duplicate button id: "+buttonId);
                List<Action> actions=new ArrayList<>();for(JsonElement a:array(button,"actions",true,16))actions.add(action(object(a,"action")));
                if(actions.isEmpty())throw new IllegalArgumentException("button must have an action");
                buttons.add(new Button(buttonId,text(button,"text",null,128),conditions(button),actions));
            }
            pages.add(new Page(pageId,text(p,"text",null,8192),conditions(p),buttons));
        }
        if(pages.isEmpty()||!ids.contains(main))throw new IllegalArgumentException("dialogue.main must refer to an existing page");
        for(var page:pages)for(var button:page.buttons())for(var action:button.actions())if(action.type().equals("OPEN_NAMED_DIALOG")&&!ids.contains(action.argument()))throw new IllegalArgumentException("dialogue reference not found: "+action.argument());
        List<Trade> trades=new ArrayList<>();for(JsonElement element:array(t,"offers",false,64)){
            var offer=object(element,"trade");keys(offer,"input","second_input","output","uses","maxUses");int max=integer(offer,"maxUses",null,1,1000000),uses=integer(offer,"uses",0,0,max);
            trades.add(new Trade(item(offer,"input"),offer.has("second_input")?item(offer,"second_input"):null,item(offer,"output"),uses,max));
        }
        return new NpcTemplate(resourceId,name,role,title,skin,bool(b,"stationary",true),bool(b,"invulnerable",true),main,pages,trades,integer(t,"reset_minutes",0,0,525600));
    }
    public void validateItems(ToIntFunction<String> maxStackSize){for(var trade:trades){validateItem(trade.input(),maxStackSize);if(trade.second()!=null)validateItem(trade.second(),maxStackSize);validateItem(trade.output(),maxStackSize);}}
    private static void validateItem(Item item,ToIntFunction<String> lookup){int max=lookup.applyAsInt(item.id());if(max<1)throw new IllegalArgumentException("invalid/unknown item ID: "+item.id());if(item.count()>max)throw new IllegalArgumentException("count exceeds maximum stack size for "+item.id());}
    private static Item item(JsonObject parent,String key){var o=object(required(parent,key),key);keys(o,"item","count");String item=text(o,"item",null,256);id(item);if(item.equals("minecraft:air"))throw new IllegalArgumentException("air is not a trade item");return new Item(item,integer(o,"count",1,1,64));}
    private static Action action(JsonObject a){
        String type=text(a,"type",null,32);String argument="",operation="";int value=0,permission=2;boolean asPlayer=true;
        switch(type){
            case "COMMAND"->{keys(a,"type","command","permission_level","execute_as_player","conditions");argument=text(a,"command",null,2048);if(argument.contains("\n")||argument.contains("\r"))throw new IllegalArgumentException("command must be one line");permission=integer(a,"permission_level",2,0,2);asPlayer=bool(a,"execute_as_player",true);}
            case "SCOREBOARD"->{keys(a,"type","objective","operation","value","conditions");argument=objective(text(a,"objective",null,16));operation=text(a,"operation",null,16);if(!Set.of("SET","INCREASE","DECREASE").contains(operation))throw new IllegalArgumentException("unknown scoreboard operation");value=integer(a,"value",1,-1000000,1000000);}
            case "OPEN_NAMED_DIALOG"->{keys(a,"type","dialogue","conditions");argument=label(text(a,"dialogue",null,32));}
            case "OPEN_TRADING_SCREEN"->keys(a,"type","conditions");
            default->throw new IllegalArgumentException("unsupported action type: "+type);
        }return new Action(type,argument,operation,value,permission,asPlayer,conditions(a));
    }
    private static List<Condition> conditions(JsonObject parent){List<Condition> list=new ArrayList<>();for(JsonElement e:array(parent,"conditions",false,16)){var o=object(e,"condition");keys(o,"type","objective","operator","value");if(!text(o,"type",null,32).equals("SCOREBOARD"))throw new IllegalArgumentException("MVP supports SCOREBOARD conditions only");String op=text(o,"operator",null,32);if(!Set.of("EQUALS","NOT_EQUALS","GREATER_THAN","GREATER_THAN_OR_EQUALS","LESS_THAN","LESS_THAN_OR_EQUALS").contains(op))throw new IllegalArgumentException("invalid condition operator");list.add(new Condition(objective(text(o,"objective",null,16)),op,integer(o,"value",0,-1000000,1000000)));}return list;}
    private static String objective(String s){if(!s.matches("[A-Za-z0-9_.-]{1,16}"))throw new IllegalArgumentException("invalid scoreboard objective");return s;}
    private static String label(String s){if(!s.matches("[a-z0-9_]{1,32}"))throw new IllegalArgumentException("dialogue/button IDs must be 1..32 lowercase letters, digits or underscores");return s;}
    private static void id(String s){if(!s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))throw new IllegalArgumentException("invalid resource ID: "+s);}
    private static JsonObject section(JsonObject o,String k){return o.has(k)?object(o.get(k),k):new JsonObject();}
    private static JsonObject object(JsonElement e,String k){if(e==null||!e.isJsonObject())throw new IllegalArgumentException(k+" must be an object");return e.getAsJsonObject();}
    private static JsonElement required(JsonObject o,String k){if(!o.has(k))throw new IllegalArgumentException("missing field: "+k);return o.get(k);}
    private static void keys(JsonObject o,String... allowed){var set=Set.of(allowed);for(String k:o.keySet())if(!set.contains(k))throw new IllegalArgumentException("unknown NPC field: "+k);}
    private static String text(JsonObject o,String k,String fallback,int max){if(!o.has(k)){if(fallback!=null)return fallback;throw new IllegalArgumentException("missing field: "+k);}var e=o.get(k);if(!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isString())throw new IllegalArgumentException(k+" must be string");String s=e.getAsString();if(s.length()>max||(fallback==null&&s.isBlank()))throw new IllegalArgumentException("invalid length for "+k);return s;}
    private static int integer(JsonObject o,String k,Integer fallback,int min,int max){if(!o.has(k)){if(fallback!=null)return fallback;throw new IllegalArgumentException("missing field: "+k);}var e=o.get(k);if(!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isNumber())throw new IllegalArgumentException(k+" must be integer");double n=e.getAsDouble();if(!Double.isFinite(n)||n!=Math.rint(n)||n<min||n>max)throw new IllegalArgumentException(k+" must be "+min+".."+max);return (int)n;}
    private static boolean bool(JsonObject o,String k,boolean fallback){if(!o.has(k))return fallback;var e=o.get(k);if(!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isBoolean())throw new IllegalArgumentException(k+" must be boolean");return e.getAsBoolean();}
    private static JsonArray array(JsonObject o,String k,boolean required,int max){if(!o.has(k)){if(required)throw new IllegalArgumentException("missing array: "+k);return new JsonArray();}var e=o.get(k);if(!e.isJsonArray()||e.getAsJsonArray().size()>max)throw new IllegalArgumentException(k+" must be an array of at most "+max);return e.getAsJsonArray();}
}
