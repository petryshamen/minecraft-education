package org.humanjourney.core;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.humanjourney.core.config.TemperatureConfig;
import org.humanjourney.core.integration.toughasnails.TemperatureIntegration;
import java.util.function.Predicate;

@Mod(HumanJourneyCore.MOD_ID)
public final class HumanJourneyCore {
    public static final String MOD_ID = "human_journey_core";

    public HumanJourneyCore(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, TemperatureConfig.SPEC,
                "human_journey_core-server.toml");
        TickEvent.PlayerTickEvent.Pre.BUS.addListener(TemperatureIntegration::beforePlayerTick);
        TickEvent.ServerTickEvent.Post.BUS.addListener(TemperatureIntegration::afterServerTick);
        // Forge 58 uses predicate cancellation, not legacy event.setCanceled().
        LivingAttackEvent.BUS.addListener(
                (Predicate<LivingAttackEvent>) TemperatureIntegration::cancelHyperthermia);
    }
}
