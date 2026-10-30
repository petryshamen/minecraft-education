package org.humanjourney.core.npc;
import com.google.gson.*;
import java.util.*;
import java.util.function.ToIntFunction;
public final class NpcTemplates {
    public record Source(String id,String pack,String json){}
    public record State(Map<String,NpcTemplate> templates,Map<String,String> sources,long revision,String lastSuccess,String error){public State{templates=Map.copyOf(templates);sources=Map.copyOf(sources);}}
    public record Prepared(Map<String,NpcTemplate> templates,Map<String,String> sources){}
    private static volatile State state=empty();
    private static State empty(){return new State(Map.of(),Map.of(),0,"never","");}
    public static State state(){return state;}
    public static Prepared prepare(Collection<Source> files,ToIntFunction<String> itemLookup){
        if(files.size()>256)throw new IllegalArgumentException("at most 256 NPC templates supported");
        Map<String,NpcTemplate> templates=new TreeMap<>();Map<String,String> sources=new TreeMap<>();
        for(Source f:files)try{
            if(templates.containsKey(f.id()))throw new IllegalArgumentException("duplicate template ID: "+f.id());
            var t=NpcTemplate.parse(f.id(),JsonParser.parseString(f.json()));t.validateItems(itemLookup);templates.put(f.id(),t);sources.put(f.id(),f.pack());
        }catch(Exception error){throw new IllegalArgumentException(f.id()+" ["+f.pack()+"]: "+error.getMessage(),error);}
        return new Prepared(Map.copyOf(templates),Map.copyOf(sources));
    }
    public static void accept(Prepared p){state=new State(p.templates(),p.sources(),state.revision()+1,java.time.Instant.now().toString(),"");}
    public static void reject(String error){var old=state;state=new State(old.templates(),old.sources(),old.revision(),old.lastSuccess(),error);}
    public static String resolve(String id){return id.indexOf(':')<0?"human_journey:"+id:id;}
    public static List<String> suggestions(){return suggestions(state);}
    public static List<String> suggestions(State s){return s.templates().keySet().stream().flatMap(id->id.startsWith("human_journey:")?java.util.stream.Stream.of(id,id.substring(14)):java.util.stream.Stream.of(id)).sorted().toList();}
    public static void reset(){state=empty();}
    private NpcTemplates(){}
}
