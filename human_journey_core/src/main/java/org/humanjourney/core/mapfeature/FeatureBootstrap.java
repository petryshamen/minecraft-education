package org.humanjourney.core.mapfeature;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.*;
import net.minecraftforge.event.server.ServerStoppedEvent;
public final class FeatureBootstrap {
 public static void register(FMLJavaModLoadingContext context){FeatureNetwork.register();AddReloadListenerEvent.BUS.addListener(e->e.addListener(new FeatureReloadListener()));TickEvent.ServerTickEvent.Post.BUS.addListener(e->FeatureServer.tick(e.getServer()));RegisterCommandsEvent.BUS.addListener(FeatureCommands::register);ServerStoppedEvent.BUS.addListener(e->{FeatureServer.clear();FeatureCatalog.reset();});if(Boolean.getBoolean("humanjourney.waterSmoke")){FeatureSmoke.register(context.getModBusGroup());FeatureV2Smoke.register(context.getModBusGroup());}}
 private FeatureBootstrap(){}
}
