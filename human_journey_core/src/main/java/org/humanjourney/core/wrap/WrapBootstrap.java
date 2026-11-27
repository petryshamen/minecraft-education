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
  org.humanjourney.core.wrap.safety.ExistingDestination.register(context.getModBusGroup());
  net.minecraftforge.event.server.ServerAboutToStartEvent.BUS.addListener(e->org.humanjourney.core.wrap.safety.EarthProtection.start(e.getServer()));
  TickEvent.ServerTickEvent.Post.BUS.addListener(e->org.humanjourney.core.wrap.safety.SafetyRuntime.tick(e.getServer()));
  net.minecraftforge.event.server.ServerStoppingEvent.BUS.addListener(e->org.humanjourney.core.wrap.safety.SafetyRuntime.closeAll());
  ServerStoppedEvent.BUS.addListener(e->{org.humanjourney.core.wrap.safety.SafetyRuntime.closeAll();org.humanjourney.core.wrap.safety.EarthProtection.clear();WrapRuntime.clear();});
  RegisterCommandsEvent.BUS.addListener(e->e.getDispatcher().register(Commands.literal("hj").then(Commands.literal("wrap").requires(s->s.hasPermission(2)).then(Commands.literal("status").executes(c->{c.getSource().sendSuccess(()->Component.literal(org.humanjourney.core.wrap.safety.SafetyRuntime.status()),false);return 1;})))));
  if(Boolean.getBoolean("humanjourney.waterSmoke"))WrapSmoke.register(context.getModBusGroup());
  if(Boolean.getBoolean("humanjourney.safetyServer")&&!net.minecraftforge.fml.loading.FMLLoader.isProduction())try{Class.forName("org.humanjourney.core.wrap.safety.SafetyServerValidation").getMethod("register").invoke(null);}catch(ReflectiveOperationException e){throw new IllegalStateException("Isolated safety harness missing",e);}
 }
 private WrapBootstrap(){}
}
