package org.humanjourney.core.progress;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.*;
import net.minecraftforge.event.server.ServerStoppedEvent;
/** Only the physical-client branch resolves client presentation classes. */
public final class ProgressBootstrap {
 public static void register(FMLJavaModLoadingContext context){ProgressNetwork.register();AddReloadListenerEvent.BUS.addListener(e->e.addListener(new ProgressReloadListener()));TickEvent.ServerTickEvent.Post.BUS.addListener(e->ProgressServer.tick(e.getServer()));RegisterCommandsEvent.BUS.addListener(ProgressCommands::register);ServerStoppedEvent.BUS.addListener(e->{ProgressServer.clear();ProgressStore.reset();});if(FMLLoader.getDist()==Dist.CLIENT)try{Class.forName("org.humanjourney.core.progress.client.ProgressClient").getMethod("register",FMLJavaModLoadingContext.class).invoke(null,context);}catch(ReflectiveOperationException error){throw new IllegalStateException("Civilization Progress client registration failed",error);}if(Boolean.getBoolean("humanjourney.waterSmoke"))ProgressSmoke.register(context.getModBusGroup());}
 private ProgressBootstrap(){}
}
