package org.humanjourney.core.config;

/** Nullable entries mean inherit the legacy server config / compatibility default. */
public record TemperatureOverrides(Double warm, Double hot, Double netherHot,
        Boolean overworldDamage, Integer overworldInterval, Double overworldAmount,
        Boolean netherDamage, Integer netherInterval, Double netherAmount, Boolean removeNetherClemency,
        Integer waterRelief, Integer waterInterval) {
    public static TemperatureOverrides empty() {
        return new TemperatureOverrides(null, null, null, null, null, null, null, null, null, null, null, null);
    }
    private static <T> T or(T value, T fallback) { return value == null ? fallback : value; }
    public TemperatureRules resolve(TemperatureRules base) {
        return new TemperatureRules(or(warm, base.warmExhaustion()), or(hot, base.hotExhaustion()),
            or(netherHot, or(hot, base.netherHotExhaustion())),
            new TemperatureRules.Damage(or(overworldDamage, base.overworld().enabled()),
                or(overworldInterval, base.overworld().intervalTicks()), or(overworldAmount, base.overworld().amount())),
            new TemperatureRules.Damage(or(netherDamage, base.nether().enabled()),
                or(netherInterval, base.nether().intervalTicks()), or(netherAmount, base.nether().amount())),
            or(removeNetherClemency, base.removeNetherClemency()), or(waterRelief, base.waterRelief()),
            or(waterInterval, base.waterCoolingInterval()));
    }
}
