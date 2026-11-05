package org.humanjourney.core.npc;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.humanjourney.core.npc.context.*;
public final class NpcCommands {
    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("hj").then(Commands.literal("npc").requires(s->s.hasPermission(2))
            .then(Commands.literal("context").executes(c->context(c.getSource())))
            .then(Commands.literal("status").executes(c->{var s=NpcTemplates.state();var context=NpcContextStore.state();String dependency=NpcDependency.problem();c.getSource().sendSuccess(()->Component.literal("NPC templates="+s.templates().size()+" revision="+s.revision()+" | Easy NPC="+(dependency.isEmpty()?"6.5.2 ready":dependency)+" | last success="+s.lastSuccess()+(s.error().isEmpty()?"":" | REJECTED: "+s.error())+"\nArchetypes="+context.data().archetypes().size()+" context revision="+context.revision()+" | last success="+context.lastSuccess()+(context.error().isEmpty()?"":" | REJECTED: "+context.error())),false);return 1;}))
            .then(Commands.literal("spawn").then(Commands.argument("template",ResourceLocationArgument.id())
                .suggests((c,b)->SharedSuggestionProvider.suggest(NpcContextRuntime.suggestions(c.getSource()),b))
                .executes(c->spawn(c.getSource(),ResourceLocationArgument.getId(c,"template").toString(),null))
                .then(Commands.argument("name",StringArgumentType.greedyString()).executes(c->spawn(c.getSource(),ResourceLocationArgument.getId(c,"template").toString(),StringArgumentType.getString(c,"name"))))))));
    }
    private static int spawn(CommandSourceStack source,String requested,String name){
        try{
            var player=source.getPlayerOrException();NpcDependency.require();String id=requested;
            if(id.startsWith("minecraft:")&&!NpcTemplates.state().templates().containsKey(id)&&!NpcContextStore.state().data().archetypes().containsKey(id))id="human_journey:"+id.substring(10);
            NpcTemplate template;
            if(NpcContextStore.state().data().archetypes().containsKey(id)){
                if(name!=null)throw new IllegalArgumentException("economic archetypes use geographic role names; custom names are supported only for static/story templates");
                var resolved=NpcContextRuntime.current(source);if(!resolved.eraProblem().isEmpty())throw new IllegalArgumentException(resolved.eraProblem());template=NpcContextRuntime.archetype(id,resolved.context());
            }else{
                template=NpcTemplates.state().templates().get(id);if(template==null)throw new IllegalArgumentException("template/archetype not found: "+id+(NpcTemplates.state().error().isEmpty()?"":"; static reload rejected: "+NpcTemplates.state().error())+(NpcContextStore.state().error().isEmpty()?"":"; context reload rejected: "+NpcContextStore.state().error()));
            }
            if(name!=null)NpcTemplate.validName(name);
            String spawned=org.humanjourney.core.npc.easynpc.EasyNpc652Adapter.spawn(player,template,name);String selected=id;
            source.sendSuccess(()->Component.literal("Spawned NPC "+selected+" as "+spawned+"."),true);return 1;
        }catch(Exception|LinkageError error){LogUtils.getLogger().error("[HJ Core] NPC spawn failed",error);source.sendFailure(Component.literal("NPC spawn failed: "+(error.getMessage()==null?error.getClass().getSimpleName():error.getMessage())));return 0;}
    }

    private static int context(CommandSourceStack source){
        try{source.getPlayerOrException();var r=NpcContextRuntime.current(source);var c=r.context();var state=NpcContextStore.state();String message="Era: "+c.era()+"\nSeason: "+c.season()+"\nMacro region: "+c.macroRegion()+"\nLocal area: "+c.localArea()+"\nLocation label: "+c.locationLabel()+"\nContext revision: "+state.revision()+" | "+r.seasonDetail()+(r.eraProblem().isEmpty()?"":"\n"+r.eraProblem())+(state.error().isEmpty()?"":"\nLast reload REJECTED: "+state.error());source.sendSuccess(()->Component.literal(message),false);return r.eraProblem().isEmpty()?1:0;}
        catch(Exception error){source.sendFailure(Component.literal("NPC context failed: "+error.getMessage()));return 0;}
    }
    private NpcCommands(){}
}
