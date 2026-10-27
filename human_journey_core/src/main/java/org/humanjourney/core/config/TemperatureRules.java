package org.humanjourney.core.config;

import org.humanjourney.core.engine.temperature.HeatPolicy;

/** Immutable effective policy. Numbers here are compatibility defaults, not balance locks. */
public record TemperatureRules(double warmExhaustion, double hotExhaustion, double netherHotExhaustion,
                               Damage overworld, Damage nether, boolean removeNetherClemency,
                               int waterRelief, int waterCoolingInterval) {
    public record Damage(boolean enabled, int intervalTicks, double amount) {
        public Damage {
            if (intervalTicks < 20 || intervalTicks > 72000) throw new IllegalArgumentException("Damage interval must be 20..72000 ticks");
            if (!Double.isFinite(amount) || amount < 0 || amount > 20) throw new IllegalArgumentException("Damage amount must be 0..20");
        }
    }
    public TemperatureRules {
        for (double rate : new double[]{warmExhaustion, hotExhaustion, netherHotExhaustion})
            if (!Double.isFinite(rate) || rate < 0 || rate > 20) throw new IllegalArgumentException("Exhaustion must be 0..20 per second");
        if (overworld == null || nether == null) throw new IllegalArgumentException("Missing dimension policy");
        if (waterRelief < 0 || waterRelief > 140) throw new IllegalArgumentException("Water relief must be 0..140");
        if (waterCoolingInterval < 1 || waterCoolingInterval > 200) throw new IllegalArgumentException("Water cooling interval must be 1..200 ticks");
    }
    public static TemperatureRules defaults() {
        return new TemperatureRules(.10, .50, .50, new Damage(false, 40, 1), new Damage(true, 40, 1), false, 20, 10);
    }
    public double exhaustionPerTick(HeatPolicy.Dimension dimension, HeatPolicy.Stage stage) {
        double hot = dimension == HeatPolicy.Dimension.NETHER ? netherHotExhaustion : hotExhaustion;
        return HeatPolicy.extraExhaustionPerTick(dimension, stage, warmExhaustion, hot);
    }
    public Damage damage(HeatPolicy.Dimension dimension) {
        return switch (dimension) { case OVERWORLD -> overworld; case NETHER -> nether; case OTHER -> null; };
    }
    public static boolean damageDue(Damage damage, boolean fullyHyperthermic, int playerTick) {
        return damage != null && damage.enabled && damage.amount > 0 && fullyHyperthermic
                && playerTick % damage.intervalTicks == 0;
    }
}
