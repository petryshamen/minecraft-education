package org.humanjourney.core.geography.client;
import org.humanjourney.core.geography.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraft.resources.ResourceLocation;
public final class GeographyClient {
 private static final ClientGeographyState STATE=new ClientGeographyState();
 public static ClientGeographyState state(){return STATE;}
 public static void register(FMLJavaModLoadingContext context){context.registerConfig(ModConfig.Type.CLIENT,GeographyClientConfig.SPEC,"human_journey_core-client.toml");
  org.humanjourney.core.geography.map.client.MapClient.register(context);
  GeographyNetwork.receiver(new GeographyNetwork.Receiver(){public void context(GeographyPackets.Context packet){org.humanjourney.core.geography.map.client.MapClient.role(packet.participant());STATE.accept(packet);}public void discovery(GeographyPackets.Discovery packet){STATE.accept(packet);}});
  ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(event->STATE.clear());ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(event->STATE.clear());
  AddGuiOverlayLayersEvent.getBus(context.getModBusGroup()).addListener(event->event.getLayeredDraw().add(ForgeLayeredDraw.POST_SLEEP_STACK,ResourceLocation.parse("human_journey_core:geography_hud"),GeographyHud::render));
 }
 private GeographyClient(){}
}
