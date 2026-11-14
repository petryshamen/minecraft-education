package org.humanjourney.core.geography;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
/** No client type references in bytecode signatures/instructions; physical-client entry is loaded only on CLIENT. */
public final class GeographyBootstrap {
 public static void register(FMLJavaModLoadingContext context){GeographyNetwork.register();org.humanjourney.core.geography.map.MapBootstrap.register(context);TickEvent.ServerTickEvent.Post.BUS.addListener(event->GeographyServer.tick(event.getServer()));RegisterCommandsEvent.BUS.addListener(GeographyCommands::register);ServerStoppedEvent.BUS.addListener(event->GeographyServer.clear());
  if(FMLLoader.getDist()==Dist.CLIENT)try{Class.forName("org.humanjourney.core.geography.client.GeographyClient").getMethod("register",FMLJavaModLoadingContext.class).invoke(null,context);}catch(ReflectiveOperationException e){throw new IllegalStateException("Geography client registration failed",e);}
  if(Boolean.getBoolean("humanjourney.waterSmoke"))GeographySmoke.register(context.getModBusGroup());
 }
 private GeographyBootstrap(){}
}
