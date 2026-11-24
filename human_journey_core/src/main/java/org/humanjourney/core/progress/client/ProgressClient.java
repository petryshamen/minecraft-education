package org.humanjourney.core.progress.client;
import org.humanjourney.core.progress.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;
/** Physical-client entry only; optional mods are deliberately absent. */
public final class ProgressClient {
 public static final ProgressMirror STATE=new ProgressMirror();private static KeyMapping key;
 public static void register(FMLJavaModLoadingContext context){ProgressNetwork.receiver(STATE::accept);ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(e->STATE.clear());ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(e->STATE.clear());RegisterKeyMappingsEvent.getBus(context.getModBusGroup()).addListener(e->{key=new KeyMapping("key.human_journey_core.civilization_progress",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_UNKNOWN,"key.categories.human_journey_core");e.register(key);});TickEvent.ClientTickEvent.Post.BUS.addListener(e->{if(key==null)return;var mc=Minecraft.getInstance();while(key.consumeClick())if(mc.player!=null&&mc.screen==null){if(STATE.view().participant())mc.setScreen(new ProgressScreen(null));else mc.player.displayClientMessage(Component.literal("Civilization Progress is available to Human Journey participants."),true);}});
  ScreenEvent.Init.Post.BUS.addListener(e->{if(!(e.getScreen() instanceof AdvancementsScreen screen)||!STATE.view().participant()||screen.width<320||screen.height<220)return;int x=screen.width-104,y=6;boolean blocked=screen.children().stream().anyMatch(child->child instanceof AbstractWidget w&&w.visible&&x<w.getX()+w.getWidth()+4&&x+100>w.getX()&&y<w.getY()+w.getHeight()+4&&y+24>w.getY());if(blocked)return;var button=Button.builder(Component.literal("HJ Progress"),b->Minecraft.getInstance().setScreen(new ProgressScreen(screen))).bounds(x,y,96,20).build();button.setTooltip(Tooltip.create(Component.literal("Human Journey Civilization Progress")));e.addListener(button);});
 }
 private ProgressClient(){}
}
