package org.humanjourney.core.progress.client;
import org.humanjourney.core.progress.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
/** Dormant dashboard foundation. No entry, keybinding, screen opening or statistics. */
public final class ProgressClient {
 public static final ProgressMirror STATE=new ProgressMirror();
 public static void register(FMLJavaModLoadingContext context){ProgressNetwork.receiver(STATE::accept);ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(e->STATE.clear());ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(e->STATE.clear());}
 private ProgressClient(){}
}
