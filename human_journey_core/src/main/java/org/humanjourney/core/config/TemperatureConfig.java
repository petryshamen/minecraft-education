package org.humanjourney.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TemperatureConfig {
    public static final ForgeConfigSpec.DoubleValue WARM_THIRST_EXHAUSTION;
    public static final ForgeConfigSpec.DoubleValue HOT_THIRST_EXHAUSTION;
    public static final ForgeConfigSpec.BooleanValue OVERWORLD_HYPERTHERMIA_DAMAGE;
    public static final ForgeConfigSpec.BooleanValue NETHER_HYPERTHERMIA_DAMAGE;
    public static final ForgeConfigSpec.IntValue WATER_HEAT_RELIEF;
    public static final ForgeConfigSpec.IntValue WATER_COOLING_INTERVAL_TICKS;
    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("temperature");
        WARM_THIRST_EXHAUSTION = b.comment(
                "Extra exhaustion per second at WARM in the Overworld and Nether. Not a thirst-point decrement.")
                .defineInRange("warm_thirst_exhaustion", 0.10, 0.0, 20.0);
        HOT_THIRST_EXHAUSTION = b.comment(
                "Extra exhaustion per second at HOT in the Overworld and Nether.")
                .defineInRange("hot_thirst_exhaustion", 0.50, 0.0, 20.0);
        OVERWORLD_HYPERTHERMIA_DAMAGE = b.comment(
                "Allow only TAN hyperthermia health damage in the Overworld; cold/dehydration unaffected.")
                .define("overworld_hyperthermia_damage", false);
        NETHER_HYPERTHERMIA_DAMAGE = b.comment(
                "Allow TAN hyperthermia health damage in the Nether.")
                .define("nether_hyperthermia_damage", true);
        WATER_HEAT_RELIEF = b.comment(
                "Minimum net hyperthermia exposure ticks removed per tick while contacting Overworld water.",
                "Zero disables all Core water heat relief. Does not change cold or lingering wetness.")
                .defineInRange("water_heat_relief", 20, 0, 140);
        WATER_COOLING_INTERVAL_TICKS = b.comment(
                "Ticks per body-temperature step toward NEUTRAL in Overworld water while overheated.",
                "Cold/ICY levels are never changed by Core.")
                .defineInRange("water_cooling_interval_ticks", 10, 1, 200);
        b.pop();
        SPEC = b.build();
    }
    public static TemperatureRules snapshot() {
        return new TemperatureRules(WARM_THIRST_EXHAUSTION.get(), HOT_THIRST_EXHAUSTION.get(), HOT_THIRST_EXHAUSTION.get(),
            new TemperatureRules.Damage(OVERWORLD_HYPERTHERMIA_DAMAGE.get(), 40, 1),
            new TemperatureRules.Damage(NETHER_HYPERTHERMIA_DAMAGE.get(), 40, 1), false,
            WATER_HEAT_RELIEF.get(), WATER_COOLING_INTERVAL_TICKS.get());
    }
    private TemperatureConfig() {}
}
