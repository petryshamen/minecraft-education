package org.humanjourney.core.advancement.client;
import org.humanjourney.core.advancement.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
public final class NativeClient {
 public static final NativeMirror STATE=new NativeMirror();
 public static void register(FMLJavaModLoadingContext context){NativeNetwork.receiver(STATE::accept);ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(e->{NativeAdapter.restoreAll();STATE.clear();});ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(e->{NativeAdapter.restoreAll();STATE.clear();});}
 private NativeClient(){}
}
