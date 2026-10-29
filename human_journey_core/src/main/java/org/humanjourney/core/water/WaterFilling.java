package org.humanjourney.core.water;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import toughasnails.init.ModConfig;
public final class WaterFilling {
    public static BlockPos target(Player player){
        double reach=player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);var start=player.getEyePosition();
        var hit=player.level().clip(new ClipContext(start,start.add(player.getViewVector(1).scale(reach)),ClipContext.Block.OUTLINE,ClipContext.Fluid.SOURCE_ONLY,player));
        return hit.getType()==HitResult.Type.BLOCK?hit.getBlockPos():null;
    }
    /** null delegates non-natural containers (rain collector, cauldron) to native TAN. */
    public static InteractionResult natural(Player player,InteractionHand hand){
        var input=player.getItemInHand(hand);var pos=target(player);var level=player.level();
        if(pos==null||!NaturalWater.source(level,pos))return null;
        if(!level.mayInteract(player,pos))return InteractionResult.FAIL;
        if(level.isClientSide())return InteractionResult.SUCCESS; // Only server classifies/publishes item state.
        var quality=NaturalWater.at(level,pos);var result=WaterContainers.fill(input,quality);if(result.isEmpty())return InteractionResult.FAIL;
        player.awardStat(Stats.ITEM_USED.get(input.getItem()));
        // Server sends one sound to everyone including initiator; no predicted sound on client.
        level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.BOTTLE_FILL,SoundSource.PLAYERS,1,1);
        if(!input.is(Items.GLASS_BOTTLE)&&ModConfig.thirst.removeSourceBlocks)level.setBlock(pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL_IMMEDIATE);
        return InteractionResult.SUCCESS.heldItemTransformedTo(ItemUtils.createFilledResult(input,player,result));
    }
    private WaterFilling(){}
}
