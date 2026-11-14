package org.humanjourney.core.geography.map.client;
import org.humanjourney.core.geography.map.*;
import org.humanjourney.core.geography.client.GeographyClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
public final class MapClient {
 private static final ClientMapState STATE=new ClientMapState();private static long lastRequest;
 public static ClientMapState state(){return STATE;}
 public static void role(boolean participant){STATE.role(participant);if(participant&&STATE.needsResync())request();}
 public static void request(){long now=System.nanoTime();if(lastRequest==0||now-lastRequest>=3000000000L){lastRequest=now;MapNetwork.request();}}
 public static void clear(){STATE.clear();lastRequest=0;}
 public static void register(FMLJavaModLoadingContext context){MapNetwork.receiver((packet,connection)->{var current=Minecraft.getInstance().getConnection();if(current==null||current.getConnection()!=connection)return;STATE.accept(packet);if(STATE.needsResync())request();});ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(e->clear());ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(e->clear());
  RegisterClientCommandsEvent.BUS.addListener(e->e.getDispatcher().register(Commands.literal("hjmap").then(Commands.literal("status").executes(c->{var s=org.humanjourney.core.integration.xaero.XaeroStatus.state();var map=STATE.snapshot();var h=map.header();c.getSource().sendSuccess(()->Component.literal("Xaero detected="+s.detected()+" version="+s.version()+" supported="+s.supported()+" active="+s.active()+" reason="+s.reason()+"\nMap macros="+map.count(MapArea.Kind.MACRO)+" locals="+map.count(MapArea.Kind.LOCAL)+" revision="+(h==null?"none":h.sequence()+" discovery="+h.discovery()+" data="+h.data()+" presentation="+h.presentation())+"\nOverlay enabled="+GeographyClientConfig.MAP_ENABLED.get()+" | sync="+STATE.problem()),false);return 1;})).then(Commands.literal("resync").executes(c->{request();c.getSource().sendSuccess(()->Component.literal("Requested shared discovered map snapshot; server participant/rate checks apply."),false);return 1;}))));
  try{Class.forName("org.humanjourney.core.integration.xaero.client.XaeroClient").getMethod("register").invoke(null);}catch(ReflectiveOperationException|RuntimeException|LinkageError error){org.humanjourney.core.integration.xaero.XaeroStatus.disable(error);}
 }
 private MapClient(){}
}
