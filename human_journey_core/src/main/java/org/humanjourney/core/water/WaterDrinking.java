package org.humanjourney.core.water;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.init.ModConfig;
import toughasnails.init.ModTags;
import java.util.*;

public final class WaterDrinking {
    private static final Map<ServerPlayer,Long> LAST_HAND_DRINK=new WeakHashMap<>();
    public static void penalties(ServerPlayer player,WaterPolicy.Quality quality){
        if(quality==WaterPolicy.Quality.SALT){salt(player);return;}
        if(quality==WaterPolicy.Quality.PURIFIED) return; // Never add penalties; never erase unrelated existing effects.
        var policy=WaterBalance.current();var hazard=quality==WaterPolicy.Quality.DIRTY?policy.dirty():policy.normal();
        if(WaterPolicy.unsafe(hazard,player.getRandom().nextDouble()) && hazard.thirstTicks()>0) player.addEffect(new MobEffectInstance(TANEffects.THIRST,hazard.thirstTicks()));
        if(quality==WaterPolicy.Quality.DIRTY && hazard.illness() && hazard.illnessTicks()>0){
            var key=ResourceKey.create(Registries.MOB_EFFECT,ResourceLocation.parse(hazard.effect()));
            player.addEffect(new MobEffectInstance(player.registryAccess().lookupOrThrow(Registries.MOB_EFFECT).getOrThrow(key),hazard.illnessTicks(),hazard.amplifier()));
        }
    }
    /** Invoked instead of TAN's water-only Finish handler, preserving its hydration/thirst tags. */
    public static boolean item(ServerPlayer player,ItemStack drink){
        var kind=WaterContainers.kind(drink);if(kind==null) return false;
        if(kind.quality()==WaterPolicy.Quality.SALT){if(ThirstHelper.isThirstEnabled())salt(player);return true;}
        if(ThirstHelper.isThirstEnabled() && drink.is(ModTags.Items.DRINKS)){
            float hydration=0;
            for(int percent=10;percent<=100;percent+=10) if(drink.is(ModTags.Items.create(ResourceLocation.fromNamespaceAndPath("toughasnails","hydration/"+percent+"_hydration_drinks")))) hydration=percent/100F;
            ThirstHelper.getThirst(player).drink(ModTags.Items.getThirstRestored(drink),hydration);
        }
        if(ThirstHelper.isThirstEnabled())penalties(player,kind.quality());
        return true;
    }
    /** Replaces the actual TAN hand-drinking packet handler, not a second unrelated right-click action. */
    public static void hand(ServerPlayer player,BlockPos pos){
        if(!ThirstHelper.isThirstEnabled() || !ModConfig.thirst.enableHandDrinking || !player.isAlive() || player.isSpectator()
                || !player.isCrouching() || !player.getMainHandItem().isEmpty() || !ThirstHelper.canDrink(player,false)) return;
        var level=player.level();double reach=player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        if(!level.hasChunkAt(pos) || player.getEyePosition().distanceToSqr(pos.getCenter())>(reach+1)*(reach+1) || !level.mayInteract(player,pos) || !level.getFluidState(pos).is(FluidTags.WATER)) return;
        var hit=level.clip(new ClipContext(player.getEyePosition(),player.getEyePosition().add(player.getViewVector(1).scale(reach)),ClipContext.Block.OUTLINE,ClipContext.Fluid.SOURCE_ONLY,player));
        if(hit.getType()!=HitResult.Type.BLOCK || !hit.getBlockPos().equals(pos)) return;
        long tick=level.getGameTime();Long last=LAST_HAND_DRINK.get(player);
        if(last!=null && tick-last<WaterBalance.current().handCooldown()) return;
        LAST_HAND_DRINK.put(player,tick);
        var quality=NaturalWater.at(level,pos);
        if(quality!=WaterPolicy.Quality.SALT)ThirstHelper.getThirst(player).drink(ModConfig.thirst.handDrinkingThirst,(float)ModConfig.thirst.handDrinkingHydration);
        penalties(player,quality);
    }
    
    public static void salt(ServerPlayer player){
        var p=WaterBalance.current().extensions().salt();applySalt(ThirstHelper.getThirst(player),p);
        if(p.nausea()&&p.nauseaTicks()>0)player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.NAUSEA,p.nauseaTicks(),p.amplifier()));
    }
    public static void applySalt(toughasnails.api.thirst.IThirst thirst,WaterExtensions.Salt p){
        thirst.setThirst(Math.clamp(thirst.getThirst()+p.thirst(),0,20));
        thirst.setHydration(Math.max(0,Math.min(thirst.getThirst(),thirst.getHydration()+p.hydration())));
        thirst.addExhaustion(p.exhaustion());
    }
    public static void reset(){LAST_HAND_DRINK.clear();}
    private WaterDrinking(){}
}
