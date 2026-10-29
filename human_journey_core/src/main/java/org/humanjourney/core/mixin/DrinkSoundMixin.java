package org.humanjourney.core.mixin;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import toughasnails.item.DrinkItem;
/** TAN DrinkItem lacks onUseTick; add its canteen cadence only for this bottle/juice hierarchy. */
@Mixin(value=DrinkItem.class,remap=false)
public abstract class DrinkSoundMixin extends Item {
    protected DrinkSoundMixin(Properties properties){super(properties);}
    @Override public void onUseTick(Level level,LivingEntity entity,ItemStack stack,int remaining){
        // No client prediction: the server broadcasts once, including the drinker's client.
        // Consumable already supplies sounds if another mod adds that component.
        if(!level.isClientSide()&&!stack.has(net.minecraft.core.component.DataComponents.CONSUMABLE)&&32-remaining>7&&remaining%4==0)
            entity.playSound(SoundEvents.GENERIC_DRINK.value(),.5F,Mth.randomBetween(level.getRandom(),.9F,1F));
    }
}
