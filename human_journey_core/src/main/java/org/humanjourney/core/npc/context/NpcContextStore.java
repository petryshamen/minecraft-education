package org.humanjourney.core.npc.context;
import com.google.gson.*;
import java.util.*;
import java.util.function.ToIntFunction;
public final class NpcContextStore {
    public record Source(String id,String pack,String json){}
    public record Data(EraRules eraRules,NpcGeography geography,Map<String,NpcArchetype> archetypes,Map<String,String> sources,int validatedVariants){public Data{archetypes=Map.copyOf(archetypes);sources=Map.copyOf(sources);}}
    public record State(Data data,long revision,String lastSuccess,String error){}
    private static volatile State state=initial();
    private static State initial(){return new State(new Data(new EraRules(List.of()),NpcGeography.empty(),Map.of(),Map.of(),0),0,"never","");}
    public static State state(){return state;}
    public static Data prepare(Source config,List<Source> geography,List<Source> archetypes,ToIntFunction<String> items){
        if(config==null&&(!archetypes.isEmpty()||!geography.isEmpty()))throw new IllegalArgumentException("npc_context.json era configuration is required for archetypes/geography");
        EraRules eras=config==null?new EraRules(List.of()):EraRules.parse(parse(config));Map<String,String> sources=new TreeMap<>();if(config!=null)sources.put(config.id(),config.pack());
        if(geography.size()>64||archetypes.size()>64)throw new IllegalArgumentException("at most 64 geography resources and 64 archetypes supported");
        List<JsonElement> locations=new ArrayList<>();for(var s:geography){locations.add(parse(s));if(sources.putIfAbsent(s.id(),s.pack())!=null)throw new IllegalArgumentException("duplicate context resource: "+s.id());}
        var geo=NpcGeography.parse(locations,eras.ids());Map<String,NpcArchetype> roles=new TreeMap<>();int checked=0;
        for(var s:archetypes)try{
            if(roles.containsKey(s.id()))throw new IllegalArgumentException("duplicate archetype ID: "+s.id());var role=NpcArchetype.parse(s.id(),parse(s),eras.ids(),geo);
            for(String era:role.eras())for(var location:geo.validationLocations(era))for(String season:new TreeSet<>(NpcArchetype.SEASONS)){
                if(++checked>16384)throw new IllegalArgumentException("NPC composition validation exceeds 16384 variants; reduce MVP data combinations");role.compose(NpcContext.of(era,season,location),items);
            }roles.put(s.id(),role);sources.put(s.id(),s.pack());
        }catch(Exception e){throw new IllegalArgumentException(s.id()+" ["+s.pack()+"]: "+e.getMessage(),e);}
        return new Data(eras,geo,roles,sources,checked);
    }
    private static JsonElement parse(Source source){try{return JsonParser.parseString(source.json());}catch(Exception e){throw new IllegalArgumentException(source.id()+" ["+source.pack()+"]: "+e.getMessage(),e);}}
    public static void accept(Data data){state=new State(data,state.revision()+1,java.time.Instant.now().toString(),"");}
    public static void reject(String error){var old=state;state=new State(old.data(),old.revision(),old.lastSuccess(),error);}
    public static void reset(){state=initial();}
    private NpcContextStore(){}
}
