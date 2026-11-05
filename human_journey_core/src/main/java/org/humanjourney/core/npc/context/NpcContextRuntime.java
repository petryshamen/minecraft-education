package org.humanjourney.core.npc.context;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.scores.ScoreHolder;
import org.humanjourney.core.npc.*;
import java.util.*;
public final class NpcContextRuntime {
    public record Resolution(NpcContext context,String eraProblem,String seasonDetail){}
    public static Resolution resolve(NpcContextStore.Data data,EraRules.Scores scores,String dimension,double x,double z,NpcSeasons.Result season){
        String era,problem="";try{era=data.eraRules().resolve(scores);}catch(IllegalArgumentException e){era="unknown";problem=e.getMessage();}
        var location=data.geography().resolve(dimension,x,z,era);return new Resolution(NpcContext.of(era,season.season(),location),problem,season.detail());
    }
    public static Resolution current(CommandSourceStack source){
        var level=source.getLevel();var board=level.getScoreboard();var position=source.getPosition();
        return resolve(NpcContextStore.state().data(),(objective,holder)->{var o=board.getObjective(objective);if(o==null)return OptionalInt.empty();var score=board.getPlayerScoreInfo(ScoreHolder.forNameOnly(holder),o);return score==null?OptionalInt.empty():OptionalInt.of(score.value());},level.dimension().location().toString(),position.x,position.z,NpcSeasons.current(level));
    }
    public static List<String> suggestions(NpcContextStore.Data data,NpcTemplates.State statics,String era){
        Set<String> ids=new TreeSet<>();for(String id:statics.templates().keySet())if(!data.archetypes().containsKey(id))ids.add(id);
        for(var role:data.archetypes().values())if(role.eras().contains(era))ids.add(role.id());
        return ids.stream().flatMap(id->id.startsWith("human_journey:")?java.util.stream.Stream.of(id,id.substring(14)):java.util.stream.Stream.of(id)).sorted().toList();
    }
    public static List<String> suggestions(CommandSourceStack source){var data=NpcContextStore.state().data();if(data.archetypes().isEmpty())return NpcTemplates.suggestions();try{return suggestions(data,NpcTemplates.state(),current(source).context().era());}catch(RuntimeException error){return suggestions(data,NpcTemplates.state(),"unknown");}}
    public static NpcTemplate archetype(String id,NpcContext context){var role=NpcContextStore.state().data().archetypes().get(id);if(role==null)throw new IllegalArgumentException("Unknown NPC archetype: "+id);if(context.era().equals("unknown"))throw new IllegalArgumentException("Unknown era state; inspect /hj npc context");return role.compose(context,NpcContextReloadListener::itemLimit);}
    private NpcContextRuntime(){}
}
