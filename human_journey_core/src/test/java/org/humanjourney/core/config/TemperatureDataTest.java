package org.humanjourney.core.config;

import com.google.gson.JsonParser;
import org.humanjourney.core.engine.temperature.HeatPolicy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TemperatureDataTest {
    private TemperatureOverrides parse(String json) { return TemperatureDataParser.parse(JsonParser.parseString(json)); }
    private TemperatureRules rules(String json) { return parse(json).resolve(TemperatureRules.defaults()); }

    @Test void emptyFileRetainsCompatibleWaterAndHeatDefaults() {
        assertEquals(TemperatureRules.defaults(), rules("{}"));
        assertEquals(20, rules("{}").waterRelief());
        assertEquals(10, rules("{}").waterCoolingInterval());
        assertFalse(rules("{}").overworld().enabled());
        assertTrue(rules("{}").nether().enabled());
        assertEquals(40, rules("{}").nether().intervalTicks());
        assertFalse(rules("{}").removeNetherClemency());
    }
    @Test void partialDataPreservesExistingServerWaterValues() {
        var base = new TemperatureRules(.2, .7, .7, new TemperatureRules.Damage(false, 40, 1),
            new TemperatureRules.Damage(true, 40, 1), false, 35, 7);
        var resolved = parse("{\"thirst\":{\"warm_exhaustion_per_second\":0.9}}").resolve(base);
        assertEquals(.9, resolved.warmExhaustion());
        assertEquals(35, resolved.waterRelief());
        assertEquals(7, resolved.waterCoolingInterval());
        assertEquals(.7, resolved.hotExhaustion());
    }
    @Test void netherHotCanBeBalancedIndependently() {
        var value = rules("{\"thirst\":{\"hot_exhaustion_per_second\":0.8,\"nether_hot_exhaustion_per_second\":1.6}}");
        assertEquals(.8/20, value.exhaustionPerTick(HeatPolicy.Dimension.OVERWORLD, HeatPolicy.Stage.HOT), 1e-9);
        assertEquals(1.6/20, value.exhaustionPerTick(HeatPolicy.Dimension.NETHER, HeatPolicy.Stage.HOT), 1e-9);
        assertEquals(.1/20, value.exhaustionPerTick(HeatPolicy.Dimension.NETHER, HeatPolicy.Stage.WARM), 1e-9);
        assertEquals(.8, rules("{\"thirst\":{\"hot_exhaustion_per_second\":0.8}}").netherHotExhaustion());
    }
    @Test void waterKnobsAreDataControlledAndZeroDisablesRelief() {
        var value = rules("{\"water\":{\"heat_relief_ticks_per_tick\":0,\"cooling_interval_ticks\":25}}");
        assertEquals(0, value.waterRelief());
        assertEquals(25, value.waterCoolingInterval());
    }
    @Test void coldNeutralAndOtherDimensionsNeverGainHeatExhaustion() {
        var value = rules("{\"thirst\":{\"warm_exhaustion_per_second\":20,\"hot_exhaustion_per_second\":20,\"nether_hot_exhaustion_per_second\":20}}");
        for (var dimension : HeatPolicy.Dimension.values())
            for (var stage : new HeatPolicy.Stage[]{HeatPolicy.Stage.ICY, HeatPolicy.Stage.COLD, HeatPolicy.Stage.NEUTRAL})
                assertEquals(0, value.exhaustionPerTick(dimension, stage));
        assertEquals(0, value.exhaustionPerTick(HeatPolicy.Dimension.OTHER, HeatPolicy.Stage.HOT));
    }
    @Test void damageIntervalsSupportFasterAndSlowerThanTanWithoutDoubleSchedule() {
        var value = rules("{\"overworld\":{\"hyperthermia_damage\":true,\"heat_damage_interval_ticks\":20,\"heat_damage_amount\":2},\"nether\":{\"heat_damage_interval_ticks\":100}}");
        int overworld = 0, nether = 0;
        for (int tick = 1; tick <= 200; tick++) {
            if (TemperatureRules.damageDue(value.overworld(), true, tick)) overworld++;
            if (TemperatureRules.damageDue(value.nether(), true, tick)) nether++;
        }
        assertEquals(10, overworld);
        assertEquals(2, nether);
        assertEquals(2, value.overworld().amount());
        assertFalse(TemperatureRules.damageDue(value.overworld(), false, 100));
        assertFalse(TemperatureRules.damageDue(null, true, 100));
    }
    @Test void disabledAndZeroDamageProduceNoScheduledHits() {
        var value = rules("{\"overworld\":{\"hyperthermia_damage\":false},\"nether\":{\"heat_damage_amount\":0}}");
        assertFalse(TemperatureRules.damageDue(value.overworld(), true, 40));
        assertFalse(TemperatureRules.damageDue(value.nether(), true, 40));
    }
    @Test void netherClemencyRemovalIsAnExplicitDataChoice() {
        assertTrue(rules("{\"nether\":{\"remove_climate_clemency\":true}}").removeNetherClemency());
        assertFalse(rules("{}").removeNetherClemency());
    }
    @Test void invalidTypesRangesAndMisspellingsAreRejected() {
        String[] invalid = {
            "[]", "null", "{\"schema_version\":2}", "{\"schema_version\":1.5}",
            "{\"thirst\":null}", "{\"thirst\":{\"warm_exhaustion_per_second\":\"0.1\"}}",
            "{\"thirst\":{\"hot_exhaustion_per_second\":-1}}", "{\"thirst\":{\"nether_hot_exhaustion_per_second\":21}}",
            "{\"overworld\":{\"hyperthermia_damage\":1}}", "{\"nether\":{\"hyperthermia_damage\":\"true\"}}",
            "{\"nether\":{\"heat_damage_interval_ticks\":19}}", "{\"nether\":{\"heat_damage_interval_ticks\":40.5}}",
            "{\"nether\":{\"heat_damage_interval_ticks\":72001}}", "{\"nether\":{\"heat_damage_amount\":1e1000}}",
            "{\"water\":{\"heat_relief_ticks_per_tick\":141}}", "{\"water\":{\"cooling_interval_ticks\":0}}",
            "{\"water\":{\"cooling_interval_ticks\":201}}", "{\"water\":{\"cooling_interval_ticks\":null}}",
            "{\"warm\":3}", "{\"water\":{\"coolling_interval_ticks\":10}}"
        };
        for (String json : invalid) assertThrows(IllegalArgumentException.class, () -> rules(json), json);
    }
    @Test void reloadReplacesOldValuesAndRemovalRestoresFallback() {
        var store = new TemperaturePolicyStore();
        store.accept(parse("{\"water\":{\"cooling_interval_ticks\":5}}"), "pack-a");
        assertEquals(5, store.snapshot().resolve(TemperatureRules.defaults()).waterCoolingInterval());
        store.accept(parse("{\"thirst\":{\"hot_exhaustion_per_second\":0.3}}"), "pack-b");
        assertEquals(10, store.snapshot().resolve(TemperatureRules.defaults()).waterCoolingInterval());
        assertEquals(.3, store.snapshot().resolve(TemperatureRules.defaults()).hotExhaustion());
        store.missing();
        assertEquals(TemperatureRules.defaults(), store.snapshot().resolve(TemperatureRules.defaults()));
        assertEquals(3, store.snapshot().revision());
    }
    @Test void invalidReloadRetainsPreviousCompletePolicyAndReportsError() {
        var store = new TemperaturePolicyStore();
        store.accept(parse("{\"nether\":{\"remove_climate_clemency\":true},\"water\":{\"cooling_interval_ticks\":4}}"), "valid-pack");
        var previous = store.snapshot().resolve(TemperatureRules.defaults());
        store.reject("bad number");
        assertEquals(previous, store.snapshot().resolve(TemperatureRules.defaults()));
        assertEquals("valid-pack", store.snapshot().source());
        assertEquals(1, store.snapshot().revision());
        assertTrue(store.snapshot().lastReload().contains("REJECTED"));
        store.reset();
        assertEquals(TemperatureRules.defaults(), store.snapshot().resolve(TemperatureRules.defaults()));
        assertEquals(0, store.snapshot().revision());
    }
}
