package org.humanjourney.core.npc.context;
import com.google.gson.*;
import java.util.*;
import java.util.function.ToIntFunction;
import org.humanjourney.core.npc.NpcTemplate;
import static org.humanjourney.core.npc.context.ContextJson.*;
/** Five explicit data layers; no scripting, deletion operators, name/skin pools or inferred era availability. */
public final class NpcArchetype {
    public static final Set<String> SEASONS=Set.of("spring","summer","autumn","winter","none");
    private record Era(JsonObject content,Map<String,JsonObject> macros,Map<String,JsonObject> locals,Map<String,JsonObject> seasons){}
    private final String id,label;private final JsonObject base;private final Map<String,Era> eras;
    private NpcArchetype(String id,String label,JsonObject base,Map<String,Era> eras){this.id=id;this.label=label;this.base=base;this.eras=Map.copyOf(eras);}
    public String id(){return id;}public String label(){return label;}public Set<String> eras(){return eras.keySet();}
    public static NpcArchetype parse(String id,JsonElement json,Set<String> eraIds,NpcGeography geography){
        resource(id);var root=object(json,"NPC archetype");keys(root,"schema_version","id","role_label","base","eras");schema(root);
        if(root.has("id")&&!text(root,"id",null,256).equals(id))throw new IllegalArgumentException("archetype id must match its resource path");
        String label=text(root,"role_label",null,48);JsonObject base=patch(section(root,"base"));Map<String,Era> eras=new TreeMap<>();
        var definitions=section(root,"eras");if(definitions.size()==0||definitions.size()>32)throw new IllegalArgumentException("archetype eras must contain 1..32 entries");
        Set<String> macros=new HashSet<>(),locals=new HashSet<>();for(var a:geography.macros())macros.add(a.id());for(var a:geography.locals())locals.add(a.id());
        for(var entry:definitions.entrySet()){
            if(!eraIds.contains(entry.getKey()))throw new IllegalArgumentException("unknown era in archetype: "+entry.getKey());var e=object(entry.getValue(),"era archetype layer");keys(e,"content","macro_regions","local_areas","seasons");
            eras.put(entry.getKey(),new Era(patch(section(e,"content")),patchMap(section(e,"macro_regions"),macros,"macro region"),patchMap(section(e,"local_areas"),locals,"local area"),patchMap(section(e,"seasons"),SEASONS,"season")));
        }return new NpcArchetype(id,label,base,eras);
    }
    private static Map<String,JsonObject> patchMap(JsonObject o,Set<String> known,String kind){Map<String,JsonObject> result=new TreeMap<>();if(o.size()>4096)throw new IllegalArgumentException("too many "+kind+" overrides");for(var e:o.entrySet()){if(!known.contains(e.getKey()))throw new IllegalArgumentException("unknown "+kind+" override: "+e.getKey());result.put(e.getKey(),patch(object(e.getValue(),kind+" patch")));}return Map.copyOf(result);}
    private static JsonObject patch(JsonObject o){keys(o,"role_label","skin_url","behavior","dialogue","trading","economy");noNull(o);return o.deepCopy();}
    private static void noNull(JsonElement e){if(e.isJsonNull())throw new IllegalArgumentException("null/deletion patches are unsupported");if(e.isJsonObject())for(var field:e.getAsJsonObject().entrySet())noNull(field.getValue());if(e.isJsonArray())for(var entry:e.getAsJsonArray())noNull(entry);}
    public static JsonObject merge(JsonObject lower,JsonObject higher){
        var result=lower.deepCopy();for(var e:higher.entrySet()){
            if(e.getValue().isJsonObject()&&result.has(e.getKey())&&result.get(e.getKey()).isJsonObject())result.add(e.getKey(),merge(result.getAsJsonObject(e.getKey()),e.getValue().getAsJsonObject()));
            else result.add(e.getKey(),e.getValue().deepCopy());
        }return result;
    }
    private JsonObject effective(NpcContext context){
        var era=eras.get(context.era());if(era==null)throw new IllegalArgumentException(label+" is not available in "+context.era()+".");
        var effective=new JsonObject();effective.addProperty("role_label",label);effective=merge(effective,base);effective=merge(effective,era.content());
        effective=merge(effective,era.macros().getOrDefault(context.macroRegion(),new JsonObject()));effective=merge(effective,era.locals().getOrDefault(context.localArea(),new JsonObject()));effective=merge(effective,era.seasons().getOrDefault(context.season(),new JsonObject()));
        return effective;
    }
    public org.humanjourney.core.npc.economy.EconomySettings economy(NpcContext context){return org.humanjourney.core.npc.economy.EconomySettings.parse(effective(context).get("economy"));}
    public NpcTemplate compose(NpcContext context,ToIntFunction<String> items){
        var effective=effective(context);org.humanjourney.core.npc.economy.EconomySettings.parse(effective.remove("economy"));
        String visible=text(effective,"role_label",null,48);effective.remove("role_label");String name=context.locationLabel().equals("none")?visible:context.locationLabel()+" "+visible;
        effective.addProperty("name",NpcTemplate.validName(name));effective.addProperty("title","");effective.addProperty("role",id.substring(id.indexOf(':')+1));effective.addProperty("schema_version",1);
        var template=NpcTemplate.parse(id,effective);template.validateItems(items);return template;
    }
}
