package org.humanjourney.core.integration.toughasnails;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.humanjourney.core.config.TemperatureConfig;
import org.humanjourney.core.engine.temperature.HeatPolicy;
import toughasnails.api.damagesource.TANDamageTypes;
import toughasnails.api.temperature.ITemperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.api.thirst.ThirstHelper;

import java.util.Map;
import java.util.WeakHashMap;

/** Adapter for exactly Minecraft 1.21.8 / Forge 58.1.18 / TAN 21.8.0.2.
 * Only public TAN APIs are used; TAN JAR and temperature config are untouched.
 */
public final class TemperatureIntegration {
    // Server-thread-only, weak keys. No persistent progression or lesson state.
    private static final Map<ServerPlayer, Integer> WATER_EXPOSURE_BEFORE_TICK = new WeakHashMap<>();

    private static boolean eligible(ServerPlayer player) {
        return player.isAlive() && !player.isCreative() && !player.isSpectator()
                && TemperatureHelper.isTemperatureEnabled();
    }

    private static HeatPolicy.Dimension dimension(ServerPlayer player) {
        if (player.level().dimension().equals(Level.OVERWORLD)) return HeatPolicy.Dimension.OVERWORLD;
        if (player.level().dimension().equals(Level.NETHER)) return HeatPolicy.Dimension.NETHER;
        return HeatPolicy.Dimension.OTHER;
    }

    private static boolean inWater(ServerPlayer player) {
        return player.isInWater() || player.level().getFluidState(player.blockPosition()).is(FluidTags.WATER);
    }

    private static boolean waterReliefEnabled(ServerPlayer player) {
        return dimension(player) == HeatPolicy.Dimension.OVERWORLD
                && TemperatureConfig.WATER_HEAT_RELIEF.get() > 0 && inWater(player);
    }

    public static void beforePlayerTick(TickEvent.PlayerTickEvent.Pre event) {
        if (!(event.player instanceof ServerPlayer player)) return;
        WATER_EXPOSURE_BEFORE_TICK.remove(player);
        if (!eligible(player) || !waterReliefEnabled(player)) return;
        ITemperature data = TemperatureHelper.getTemperatureData(player);
        if (data.getLevel().ordinal() >= TemperatureLevel.NEUTRAL.ordinal()) {
            WATER_EXPOSURE_BEFORE_TICK.put(player, data.getHyperthermiaTicks());
            // Remove exposure before TAN can perform its health-damage check this tick.
            data.setHyperthermiaTicks(HeatPolicy.relieveExposure(
                    data.getHyperthermiaTicks(), TemperatureConfig.WATER_HEAT_RELIEF.get()));
        }
    }

    public static void afterServerTick(TickEvent.ServerTickEvent.Post event) {
        // This is after every player's TAN tail injection, independent of Player Post ordering.
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            finishPlayerTick(player);
        }
    }

    private static void finishPlayerTick(ServerPlayer player) {
        Integer before = WATER_EXPOSURE_BEFORE_TICK.remove(player);
        if (!eligible(player)) return;
        ITemperature data = TemperatureHelper.getTemperatureData(player);
        if (waterReliefEnabled(player) && data.getLevel().ordinal() >= TemperatureLevel.NEUTRAL.ordinal()) {
            int basis = before == null ? data.getHyperthermiaTicks() : before;
            int relieved = HeatPolicy.relieveExposure(basis, TemperatureConfig.WATER_HEAT_RELIEF.get());
            // Discard any TAN heat increment during the same tick; do not double-apply relief.
            data.setHyperthermiaTicks(Math.min(data.getHyperthermiaTicks(), relieved));
            // Clamp only hot targets: TAN remains free to cool into COLD/ICY.
            if (data.getTargetLevel().ordinal() > TemperatureLevel.NEUTRAL.ordinal()) {
                data.setTargetLevel(TemperatureLevel.NEUTRAL);
            }
            if (player.tickCount % TemperatureConfig.WATER_COOLING_INTERVAL_TICKS.get() == 0) {
                HeatPolicy.Stage stage = HeatPolicy.Stage.valueOf(data.getLevel().name());
                TemperatureLevel cooled = TemperatureLevel.valueOf(HeatPolicy.coolOneStep(stage).name());
                if (cooled != data.getLevel()) data.setLevel(cooled);
            }
        }
        if (!ThirstHelper.isThirstEnabled()) return;
        HeatPolicy.Stage stage = HeatPolicy.Stage.valueOf(data.getLevel().name());
        double extra = HeatPolicy.extraExhaustionPerTick(dimension(player), stage,
                TemperatureConfig.WARM_THIRST_EXHAUSTION.get(),
                TemperatureConfig.HOT_THIRST_EXHAUSTION.get());
        if (extra > 0) ThirstHelper.getThirst(player).addExhaustion((float) extra);
    }

    /** Predicate return true cancels this exact damage event in Forge 58. */
    public static boolean cancelHyperthermia(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !event.getSource().is(TANDamageTypes.HYPERTHERMIA)) return false;
        // Water may protect against heat only; never grant generic fire/ice/dehydration immunity.
        if (eligible(player) && waterReliefEnabled(player)) {
            ITemperature data = TemperatureHelper.getTemperatureData(player);
            if (data.getLevel().ordinal() >= TemperatureLevel.NEUTRAL.ordinal()) return true;
        }
        return HeatPolicy.blockHeatDamage(dimension(player),
                TemperatureConfig.OVERWORLD_HYPERTHERMIA_DAMAGE.get(),
                TemperatureConfig.NETHER_HYPERTHERMIA_DAMAGE.get());
    }
    private TemperatureIntegration() {}
}
