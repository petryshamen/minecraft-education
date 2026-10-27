package org.humanjourney.core.integration.toughasnails;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.humanjourney.core.config.TemperatureRules;
import org.humanjourney.core.data.TemperatureBalance;
import org.humanjourney.core.engine.temperature.HeatPolicy;
import toughasnails.api.damagesource.TANDamageTypes;
import toughasnails.api.temperature.ITemperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.api.potion.TANEffects;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** Adapter for exactly Minecraft 1.21.8 / Forge 58.1.18 / TAN 21.8.0.2.
 * Only public TAN APIs are used; TAN JAR and temperature config are untouched.
 */
public final class TemperatureIntegration {
    // Server-thread-only, weak keys. No persistent progression or lesson state.
    private static final Map<ServerPlayer, Integer> WATER_EXPOSURE_BEFORE_TICK = new WeakHashMap<>();
    private static final Set<ServerPlayer> CORE_HEAT_DAMAGE = Collections.newSetFromMap(new IdentityHashMap<>());

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
                && TemperatureBalance.current().waterRelief() > 0 && inWater(player);
    }

    public static void beforePlayerTick(TickEvent.PlayerTickEvent.Pre event) {
        if (!(event.player instanceof ServerPlayer player)) return;
        WATER_EXPOSURE_BEFORE_TICK.remove(player);
        if (!eligible(player)) return;
        if (dimension(player) == HeatPolicy.Dimension.NETHER && TemperatureBalance.current().removeNetherClemency()) {
            player.removeEffect(TANEffects.CLIMATE_CLEMENCY);
        }
        if (!waterReliefEnabled(player)) return;
        ITemperature data = TemperatureHelper.getTemperatureData(player);
        if (data.getLevel().ordinal() >= TemperatureLevel.NEUTRAL.ordinal()) {
            WATER_EXPOSURE_BEFORE_TICK.put(player, data.getHyperthermiaTicks());
            // Remove exposure before TAN can perform its health-damage check this tick.
            data.setHyperthermiaTicks(HeatPolicy.relieveExposure(
                    data.getHyperthermiaTicks(), TemperatureBalance.current().waterRelief()));
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
        TemperatureRules rules = TemperatureBalance.current();
        ITemperature data = TemperatureHelper.getTemperatureData(player);
        if (waterReliefEnabled(player) && data.getLevel().ordinal() >= TemperatureLevel.NEUTRAL.ordinal()) {
            int basis = before == null ? data.getHyperthermiaTicks() : before;
            int relieved = HeatPolicy.relieveExposure(basis, rules.waterRelief());
            // Discard any TAN heat increment during the same tick; do not double-apply relief.
            data.setHyperthermiaTicks(Math.min(data.getHyperthermiaTicks(), relieved));
            // Clamp only hot targets: TAN remains free to cool into COLD/ICY.
            if (data.getTargetLevel().ordinal() > TemperatureLevel.NEUTRAL.ordinal()) {
                data.setTargetLevel(TemperatureLevel.NEUTRAL);
            }
            if (player.tickCount % rules.waterCoolingInterval() == 0) {
                HeatPolicy.Stage stage = HeatPolicy.Stage.valueOf(data.getLevel().name());
                TemperatureLevel cooled = TemperatureLevel.valueOf(HeatPolicy.coolOneStep(stage).name());
                if (cooled != data.getLevel()) data.setLevel(cooled);
            }
        }
        if (ThirstHelper.isThirstEnabled()) {
            HeatPolicy.Stage stage = HeatPolicy.Stage.valueOf(data.getLevel().name());
            double extra = rules.exhaustionPerTick(dimension(player), stage);
            if (extra > 0) ThirstHelper.getThirst(player).addExhaustion((float) extra);
        }
        applyHeatDamage(player, rules);
    }

    private static void applyHeatDamage(ServerPlayer player, TemperatureRules rules) {
        TemperatureRules.Damage damage = rules.damage(dimension(player));
        if (!TemperatureRules.damageDue(damage, TemperatureHelper.isFullyHyperthermic(player), player.tickCount)) return;
        CORE_HEAT_DAMAGE.add(player);
        try {
            // Same TAN damage type and normal Minecraft protection/invulnerability handling.
            player.hurtServer((ServerLevel) player.level(), player.damageSources().source(TANDamageTypes.HYPERTHERMIA), (float) damage.amount());
        } finally {
            CORE_HEAT_DAMAGE.remove(player);
        }
    }

    /** Predicate return true cancels this exact damage event in Forge 58. */
    public static boolean cancelHyperthermia(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !event.getSource().is(TANDamageTypes.HYPERTHERMIA)) return false;
        TemperatureRules.Damage damage = TemperatureBalance.current().damage(dimension(player));
        if (damage == null) return false; // Other dimensions retain TAN's native behavior.
        if (!damage.enabled()) return true;
        // Water may protect against heat only; never grant generic fire/ice/dehydration immunity.
        if (eligible(player) && waterReliefEnabled(player)) {
            ITemperature data = TemperatureHelper.getTemperatureData(player);
            if (data.getLevel().ordinal() >= TemperatureLevel.NEUTRAL.ordinal()) return true;
        }
        // Suppress native TAN pulses; only our scheduled pulse is admitted, avoiding double hits.
        return !CORE_HEAT_DAMAGE.contains(player);
    }
    public static void reset() {
        WATER_EXPOSURE_BEFORE_TICK.clear();
        CORE_HEAT_DAMAGE.clear();
    }
    private TemperatureIntegration() {}
}
