package org.humanjourney.core.juice;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
/** Prevent unrelated fixed recipes from stripping a juice marker or refilling a partly used carrier. */
public final class JuiceCraftGuard {
 public static void guard(AbstractContainerMenu menu,Player player,CraftingContainer input,ResultContainer result){for(int i=0;i<input.getContainerSize();i++)if(JuiceState.marked(input.getItem(i))){result.setItem(0,ItemStack.EMPTY);if(player instanceof ServerPlayer server){menu.setRemoteSlot(0,ItemStack.EMPTY);server.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId,menu.incrementStateId(),0,ItemStack.EMPTY));}return;}}
 private JuiceCraftGuard(){}
}
