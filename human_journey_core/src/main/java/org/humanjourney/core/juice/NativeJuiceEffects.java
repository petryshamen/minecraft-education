package org.humanjourney.core.juice;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import glitchcore.event.entity.LivingEntityUseItemEvent;
import toughasnails.thirst.ThirstHandler;
/** Calls the exact TAN 21.8.0.2 handler, including native tag precedence/effects and thirst-disabled guard. */
public final class NativeJuiceEffects {
 public static void apply(ServerPlayer player,ItemStack bottle){ThirstHandler.onItemUseFinish(new LivingEntityUseItemEvent.Finish(player,bottle,ItemStack.EMPTY));}
 private NativeJuiceEffects(){}
}
