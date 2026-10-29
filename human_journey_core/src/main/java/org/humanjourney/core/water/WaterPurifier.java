package org.humanjourney.core.water;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import toughasnails.block.entity.WaterPurifierBlockEntity;
import org.humanjourney.core.water.WaterPolicy.Quality;
public final class WaterPurifier {
    public record Intake(boolean freshwater,boolean salt){public boolean usable(){return freshwater&&!salt;}}
    public static Intake intake(Level level,BlockPos pos){
        boolean fresh=false,salt=false;var policy=WaterBalance.current().extensions().purifier();
        for(Direction d:Direction.values()){
            var adjacent=pos.relative(d);if(!NaturalWater.source(level,adjacent))continue;var q=NaturalWater.at(level,adjacent);
            if(q==Quality.SALT)salt=true;else if(policy.accepts(q))fresh=true;
        }return new Intake(fresh,salt);
    }
    public static boolean charge(WaterPurifierBlockEntity block){
        var p=WaterBalance.current().extensions().purifier();if(!p.enabled())return false;
        var access=(PurifierAccess)block;var filter=block.getItem(1);
        var plan=FilterCharge.plan(access.hjRemaining(),access.hjDuration(),WaterPurifierBlockEntity.getFilterDuration(filter),filter.getCount(),p.cost());
        if(plan==null)return false;
        if(plan.consumed()>0){var remainder=filter.getItem().getCraftingRemainder();filter.shrink(plan.consumed());if(filter.isEmpty()&&!remainder.isEmpty())block.setItem(1,remainder.copy());}
        access.hjCapacity(plan.remaining(),plan.duration());block.setChanged();return true;
    }
    public static ItemStack purifiedFill(ItemStack empty){return WaterContainers.empty(empty)&&!SaltWater.marked(empty)?WaterContainers.fill(empty,Quality.PURIFIED):ItemStack.EMPTY;}
    /** Existing GUI/hopper slots remain usable, but legacy filled-water recipes can never desalinate. */
    public static void tick(Level level,BlockPos pos,WaterPurifierBlockEntity block){
        boolean usable=intake(level,pos).usable();
        if(process(block,usable))level.sendBlockUpdated(pos,block.getBlockState(),block.getBlockState(),3);
        var state=block.getBlockState();boolean active=usable&&((PurifierAccess)block).hjRemaining()>0;
        if(state.getValue(toughasnails.block.WaterPurifierBlock.PURIFYING)!=active)
            level.setBlock(pos,state.setValue(toughasnails.block.WaterPurifierBlock.PURIFYING,active),3);
    }
    /** Same transaction used by GUI/hoppers; testable without a Minecraft world. */
    public static boolean process(WaterPurifierBlockEntity block,boolean usable){
        var input=block.getItem(0);if(!WaterBalance.current().extensions().purifier().enabled()||!usable)return false;
        var result=purifiedFill(input);var output=block.getItem(2);
        if(result.isEmpty()||!fits(output,result,block.getMaxStackSize())||!charge(block))return false;
        if(output.isEmpty())block.setItem(2,result);else output.grow(1);
        input.shrink(1);block.setChanged();return true;
    }
    public static boolean fits(ItemStack output,ItemStack result,int limit){return output.isEmpty()||ItemStack.isSameItemSameComponents(output,result)&&output.getCount()+1<=Math.min(limit,output.getMaxStackSize());}
    public static boolean interact(PlayerInteractEvent.RightClickBlock event){
        Player player=event.getEntity();Level level=player.level();BlockPos pos=event.getPos();
        if(!(level.getBlockEntity(pos) instanceof WaterPurifierBlockEntity block))return false;
        var input=player.getItemInHand(event.getHand());
        // Empty containers fill. Filled/salt water is claimed to prevent the item's natural refill behind the machine.
        if(!WaterContainers.empty(input)&&WaterContainers.kind(input)==null)return false;
        event.setCancellationResult(InteractionResult.SUCCESS);
        if(level.isClientSide())return true;
        if(!level.mayInteract(player,pos)||player.isSpectator())return true;
        String error=null;var p=WaterBalance.current().extensions().purifier();var source=intake(level,pos);var result=purifiedFill(input);
        if(!p.enabled())error="Water Purifier disabled by policy";
        else if(result.isEmpty())error="Use an empty bottle or empty canteen";
        else if(!source.usable())error=source.salt()?"Salt Water cannot be purified; use a freshwater intake":"Water Purifier needs adjacent source freshwater";
        else if(!charge(block))error="Water Purifier needs more filter capacity (cost "+p.cost()+")";
        else {
            player.setItemInHand(event.getHand(),ItemUtils.createFilledResult(input,player,result));
            level.playSound(null,pos,SoundEvents.BOTTLE_FILL,SoundSource.BLOCKS,1,1);player.containerMenu.broadcastChanges();
        }
        if(error!=null)player.displayClientMessage(Component.literal(error),true);return true;
    }
    private WaterPurifier(){}
}
