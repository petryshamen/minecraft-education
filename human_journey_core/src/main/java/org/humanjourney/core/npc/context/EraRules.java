package org.humanjourney.core.npc.context;
import com.google.gson.*;
import java.util.*;
import static org.humanjourney.core.npc.context.ContextJson.*;
/** Logical eras are predicates over arbitrary scoreboard holders/objectives, never an integer-era convention. */
public record EraRules(List<Rule> rules){
    @FunctionalInterface public interface Scores {OptionalInt read(String objective,String holder);}
    public record Condition(String objective,String holder,String operator,int value){
        boolean matches(Scores scores){var v=scores.read(objective,holder);if(v.isEmpty())return false;return switch(operator){case "EQUALS"->v.getAsInt()==value;case "NOT_EQUALS"->v.getAsInt()!=value;case "GREATER_THAN"->v.getAsInt()>value;case "GREATER_THAN_OR_EQUALS"->v.getAsInt()>=value;case "LESS_THAN"->v.getAsInt()<value;case "LESS_THAN_OR_EQUALS"->v.getAsInt()<=value;default->false;};}
    }
    public record Rule(String id,int priority,List<Condition> conditions){public Rule{conditions=List.copyOf(conditions);}}
    public EraRules{rules=List.copyOf(rules);}
    public static EraRules parse(JsonElement json){
        var root=object(json,"npc_context.json");keys(root,"schema_version","eras");schema(root);List<Rule> rules=new ArrayList<>();Set<String> seen=new HashSet<>();
        for(var e:array(root,"eras",1,32)){
            var r=object(e,"era");keys(r,"id","priority","conditions");String id=logical(text(r,"id",null,64));if(!seen.add(id))throw new IllegalArgumentException("duplicate era ID: "+id);
            List<Condition> conditions=new ArrayList<>();for(var c:array(r,"conditions",1,16)){
                var o=object(c,"era condition");keys(o,"objective","holder","operator","value");String objective=text(o,"objective",null,16),holder=text(o,"holder",null,40),operator=text(o,"operator",null,32);
                if(!objective.matches("[A-Za-z0-9_.-]{1,16}")||holder.chars().anyMatch(Character::isWhitespace))throw new IllegalArgumentException("invalid era objective/holder");
                if(!Set.of("EQUALS","NOT_EQUALS","GREATER_THAN","GREATER_THAN_OR_EQUALS","LESS_THAN","LESS_THAN_OR_EQUALS").contains(operator))throw new IllegalArgumentException("invalid era operator: "+operator);
                if(!o.has("value"))throw new IllegalArgumentException("era condition requires value");conditions.add(new Condition(objective,holder,operator,integer(o,"value",0,Integer.MIN_VALUE,Integer.MAX_VALUE)));
            }rules.add(new Rule(id,integer(r,"priority",0,-1000000,1000000),conditions));
        }return new EraRules(rules);
    }
    public Set<String> ids(){Set<String> ids=new HashSet<>();for(var r:rules)ids.add(r.id());return Set.copyOf(ids);}
    public String resolve(Scores scores){
        var matching=rules.stream().filter(r->r.conditions().stream().allMatch(c->c.matches(scores))).sorted(Comparator.comparingInt(Rule::priority).reversed()).toList();
        if(matching.isEmpty())throw new IllegalArgumentException("Unknown era state: no configured era matches existing scoreboard values");
        if(matching.size()>1&&matching.get(0).priority()==matching.get(1).priority())throw new IllegalArgumentException("Ambiguous era state: "+matching.get(0).id()+" and "+matching.get(1).id());
        return matching.getFirst().id();
    }
}
