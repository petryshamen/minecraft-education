package org.humanjourney.core.wrap;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
public final class WrapBootstrap {
 public static void register(FMLJavaModLoadingContext context){
  context.registerConfig(ModConfig.Type.SERVER,WrapConfig.SPEC,"human_journey_core-world-wrap.toml");
  TickEvent.ServerTickEvent.Post.BUS.addListener(e->WrapRuntime.tick(e.getServer()));
  ServerStoppedEvent.BUS.addListener(e->WrapRuntime.clear());
  RegisterCommandsEvent.BUS.addListener(e->e.getDispatcher().register(Commands.literal("hj").then(Commands.literal("wrap").requires(s->s.hasPermission(2)).then(Commands.literal("status").executes(c->{c.getSource().sendSuccess(()->Component.literal(WrapRuntime.status()),false);return 1;})))));
  if(Boolean.getBoolean("humanjourney.waterSmoke"))WrapSmoke.register(context.getModBusGroup());
 }
 private WrapBootstrap(){}
}
