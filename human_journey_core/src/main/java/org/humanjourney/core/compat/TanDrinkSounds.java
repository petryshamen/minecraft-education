package org.humanjourney.core.compat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Audio compatibility only; no dependency on water classification or balance. */
public final class TanDrinkSounds {
    public static boolean shouldPlay(boolean client,boolean consumable,boolean silent,int remaining){
        return !client&&!consumable&&!silent&&remaining>0&&32-remaining>7&&remaining%4==0;
    }
    public static void tick(Level level,LivingEntity entity,ItemStack stack,int remaining){
        if(shouldPlay(level.isClientSide(),stack.has(DataComponents.CONSUMABLE),entity.isSilent(),remaining))
            // Player.playSound excludes that player on the server; broadcast directly with no excluded listener.
            level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.GENERIC_DRINK.value(),SoundSource.PLAYERS,.5F,Mth.randomBetween(level.getRandom(),.9F,1F));
    }
    private TanDrinkSounds(){}
}
