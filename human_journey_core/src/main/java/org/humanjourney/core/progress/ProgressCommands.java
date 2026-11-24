package org.humanjourney.core.progress;
import static net.minecraft.commands.Commands.literal;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraft.network.chat.Component;
public final class ProgressCommands {
 public static void register(RegisterCommandsEvent event){event.getDispatcher().register(literal("hj").then(literal("progress").requires(s->s.hasPermission(2)).then(literal("status").executes(c->{var s=ProgressStore.state();c.getSource().sendSuccess(()->Component.literal("Civilization graph: "+s.graph().nodes().size()+" nodes / "+s.graph().connections().size()+" connections; source: "+s.source()+"; revision "+s.revision()+"; unresolved references: "+ProgressServer.unresolved().size()+(s.error().isEmpty()?"":"; last reload rejected: "+s.error())),false);return 1;}))));}
 private ProgressCommands(){}
}
