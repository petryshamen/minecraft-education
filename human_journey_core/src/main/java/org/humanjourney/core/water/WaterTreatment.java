package org.humanjourney.core.water;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class WaterTreatment {
    public static boolean canOutput(ItemStack input,ItemStack output,int limit){
        if(!WaterBalance.current().furnace())return false;
        var result=WaterContainers.boiled(input);
        return !result.isEmpty() && (output.isEmpty() || ItemStack.isSameItemSameComponents(output,result)
            && output.getCount()+1<=Math.min(limit,output.getMaxStackSize()));
    }
    public static boolean burn(NonNullList<ItemStack> items,int limit){
        if(!canOutput(items.get(0),items.get(2),limit))return false;
        ItemStack result=WaterContainers.boiled(items.get(0));
        if(items.get(2).isEmpty())items.set(2,result);else items.get(2).grow(1);
        items.get(0).shrink(1);return true;
    }
    public static boolean filter(Player player,InteractionHand hand){
        if(player.level().isClientSide())return false; // No client policy prediction; server owns reloadable rules.
        if(hand!=InteractionHand.MAIN_HAND || !player.isShiftKeyDown())return false;
        var p=WaterBalance.current();var input=player.getMainHandItem();var material=player.getOffhandItem();var k=WaterContainers.kind(input);
        if(!p.filtering() || k==null || !WaterPolicy.canFilter(k.quality()) || !p.filters().contains(BuiltInRegistries.ITEM.getKey(material.getItem()).toString()) || material.getCount()<p.filterCount())return false;
        var result=WaterContainers.convert(input,WaterPolicy.Quality.NORMAL);if(result.isEmpty())return false;
        if(!player.level().isClientSide()){
            // Supported bottles/canteens are single containers, not a static recipe output.
            if(input.getCount()!=1)return false;
            player.setItemInHand(hand,result);
            if(!player.getAbilities().instabuild)material.shrink(p.filterCount());
            player.containerMenu.broadcastChanges();
        }
        return true;
    }
    public static boolean rightClickItem(PlayerInteractEvent.RightClickItem event){
        if(filter(event.getEntity(),event.getHand())){event.setCancellationResult(InteractionResult.SUCCESS);return true;}return false;
    }
    public static boolean rightClickBlock(PlayerInteractEvent.RightClickBlock event){
        var player=event.getEntity();var level=player.level();var pos=event.getPos();
        if(level.isClientSide())return false;
        if(!level.mayInteract(player,pos))return false;
        if(filter(player,event.getHand())){event.setCancellationResult(InteractionResult.SUCCESS);return true;}
        if(!(level.getBlockEntity(pos) instanceof CampfireBlockEntity camp))return false;
        var slots=(CampfireAccess)camp;var input=player.getItemInHand(event.getHand());
        if(event.getHand()==InteractionHand.MAIN_HAND && player.isShiftKeyDown() && input.isEmpty()){
            for(int i=0;i<slots.hjItems().size();i++)if(WaterContainers.heatGuard(slots.hjItems().get(i))){
                if(!level.isClientSide()) {player.setItemInHand(event.getHand(),slots.hjItems().get(i));slots.hjItems().set(i,ItemStack.EMPTY);slots.hjProgress()[i]=0;camp.setChanged();level.sendBlockUpdated(pos,camp.getBlockState(),camp.getBlockState(),3);}
                event.setCancellationResult(InteractionResult.SUCCESS);return true;
            }
        }
        var kind=WaterContainers.kind(input);
        if(kind==null)return false;
        // Claim every water interaction, including dirty water, so old fixed campfire recipes cannot bypass the chain.
        if(kind.quality()==WaterPolicy.Quality.NORMAL && WaterBalance.current().campfire() && camp.getBlockState().getValue(CampfireBlock.LIT) && !WaterContainers.boiled(input).isEmpty()){
            for(int i=0;i<slots.hjItems().size();i++)if(slots.hjItems().get(i).isEmpty()){
                if(!level.isClientSide()){
                    slots.hjItems().set(i,input.copyWithCount(1));if(!player.getAbilities().instabuild)input.shrink(1);
                    slots.hjProgress()[i]=0;slots.hjTimes()[i]=WaterBalance.current().campfireTicks();camp.setChanged();level.sendBlockUpdated(pos,camp.getBlockState(),camp.getBlockState(),3);
                }
                break;
            }
        }
        event.setCancellationResult(InteractionResult.SUCCESS);return true;
    }
    /** Validate/replace legacy crafting results before they become takeable in either crafting menu. */
    public static void guardCraft(AbstractContainerMenu menu,Player player,CraftingContainer input,ResultContainer result){
        var outputKind=WaterContainers.kind(result.getItem(0));if(outputKind==null)return;
        ItemStack source=ItemStack.EMPTY;int filters=0;boolean valid=true;boolean unsupportedPotion=false;var policy=WaterBalance.current();
        for(int i=0;i<input.getContainerSize();i++){
            var stack=input.getItem(i);if(stack.isEmpty())continue;
            if(WaterContainers.kind(stack)!=null){if(!source.isEmpty())valid=false;else source=stack;}
            else if(policy.filters().contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()))filters++;
            else {valid=false;if(stack.is(Items.POTION))unsupportedPotion=true;}
        }
        if(source.isEmpty() && !unsupportedPotion)return;
        if(source.isEmpty()){
            result.setItem(0,ItemStack.EMPTY);
            if(player instanceof ServerPlayer server){menu.setRemoteSlot(0,ItemStack.EMPTY);server.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId,menu.incrementStateId(),0,ItemStack.EMPTY));}
            return;
        }
        var sourceKind=WaterContainers.kind(source);
        // Only quality-changing recipes are managed; original empty-canteen crafting and tier upgrades remain TAN's domain.
        if(sourceKind.quality()==outputKind.quality())return;
        ItemStack safe=ItemStack.EMPTY;
        if(valid && policy.filtering() && filters==policy.filterCount() && sourceKind.quality()==WaterPolicy.Quality.DIRTY && outputKind.quality()==WaterPolicy.Quality.NORMAL && result.getItem(0).getCount()==1)
            safe=WaterContainers.convert(source,WaterPolicy.Quality.NORMAL);
        result.setItem(0,safe);
        if(player instanceof ServerPlayer server){menu.setRemoteSlot(0,safe);server.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId,menu.incrementStateId(),0,safe));}
    }
    private WaterTreatment(){}
}
