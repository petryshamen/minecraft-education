package org.humanjourney.core.geography.map;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
public final class MapBootstrap {
 public static void register(FMLJavaModLoadingContext context){MapNetwork.register();if(Boolean.getBoolean("humanjourney.waterSmoke"))MapSmoke.register(context.getModBusGroup());}
 private MapBootstrap(){}
}
