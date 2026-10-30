package org.humanjourney.core.npc;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
public final class NpcCommands {
    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("hj").then(Commands.literal("npc").requires(s->s.hasPermission(2))
            .then(Commands.literal("status").executes(c->{var s=NpcTemplates.state();String dependency=NpcDependency.problem();c.getSource().sendSuccess(()->Component.literal("NPC templates="+s.templates().size()+" revision="+s.revision()+" | Easy NPC="+(dependency.isEmpty()?"6.5.2 ready":dependency)+" | last success="+s.lastSuccess()+(s.error().isEmpty()?"":" | REJECTED: "+s.error())),false);return 1;}))
            .then(Commands.literal("spawn").then(Commands.argument("template",ResourceLocationArgument.id())
                .suggests((c,b)->SharedSuggestionProvider.suggest(NpcTemplates.suggestions(),b))
                .executes(c->spawn(c.getSource(),ResourceLocationArgument.getId(c,"template").toString(),null))
                .then(Commands.argument("name",StringArgumentType.greedyString()).executes(c->spawn(c.getSource(),ResourceLocationArgument.getId(c,"template").toString(),StringArgumentType.getString(c,"name"))))))));
    }
    private static int spawn(CommandSourceStack source,String requested,String name){
        try{
            var player=source.getPlayerOrException();NpcDependency.require();String id=requested;
            if(id.startsWith("minecraft:")&&!NpcTemplates.state().templates().containsKey(id))id="human_journey:"+id.substring(10);
            var template=NpcTemplates.state().templates().get(id);if(template==null)throw new IllegalArgumentException("template not found: "+id+(NpcTemplates.state().error().isEmpty()?"":"; last reload rejected: "+NpcTemplates.state().error()));
            if(name!=null)NpcTemplate.validName(name);
            String spawned=org.humanjourney.core.npc.easynpc.EasyNpc652Adapter.spawn(player,template,name);String selected=id;
            source.sendSuccess(()->Component.literal("Spawned NPC "+selected+" as "+spawned+"."),true);return 1;
        }catch(Exception|LinkageError error){LogUtils.getLogger().error("[HJ Core] NPC spawn failed",error);source.sendFailure(Component.literal("NPC spawn failed: "+(error.getMessage()==null?error.getClass().getSimpleName():error.getMessage())));return 0;}
    }
    private NpcCommands(){}
}
