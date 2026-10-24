package org.humanjourney.core.engine.temperature;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.humanjourney.core.engine.temperature.HeatPolicy.*;

class HeatPolicyTest {
    @Test void warmAndHotAccumulateAtConfiguredRate() {
        assertEquals(0.10, 20 * extraExhaustionPerTick(Dimension.OVERWORLD, Stage.WARM, .10, .50), 1e-9);
        assertEquals(0.50, 20 * extraExhaustionPerTick(Dimension.OVERWORLD, Stage.HOT, .10, .50), 1e-9);
        assertEquals(0.50, 20 * extraExhaustionPerTick(Dimension.NETHER, Stage.HOT, .10, .50), 1e-9);
        assertEquals(0.10, 20 * extraExhaustionPerTick(Dimension.NETHER, Stage.WARM, .10, .50), 1e-9);
        assertEquals(0, extraExhaustionPerTick(Dimension.OTHER, Stage.HOT, .10, .50));
    }
    @Test void coldAndNeutralAddNoHeatExhaustion() {
        for (Dimension d : Dimension.values()) {
            for (Stage s : new Stage[]{Stage.ICY, Stage.COLD, Stage.NEUTRAL})
                assertEquals(0, extraExhaustionPerTick(d, s, .10, .50));
        }
    }
    @Test void damagePolicyDoesNotMakeNetherSafe() {
        assertTrue(blockHeatDamage(Dimension.OVERWORLD, false, true));
        assertFalse(blockHeatDamage(Dimension.NETHER, false, true));
        assertFalse(blockHeatDamage(Dimension.OTHER, false, true));
        assertFalse(blockHeatDamage(Dimension.OVERWORLD, true, true));
        assertTrue(blockHeatDamage(Dimension.NETHER, false, false));
    }
    @Test void waterCoolingCannotPushIntoCold() {
        assertEquals(Stage.WARM, coolOneStep(Stage.HOT));
        assertEquals(Stage.NEUTRAL, coolOneStep(Stage.WARM));
        for (Stage s : new Stage[]{Stage.ICY, Stage.COLD, Stage.NEUTRAL}) assertEquals(s, coolOneStep(s));
        assertEquals(0, relieveExposure(12, 20));
        assertEquals(120, relieveExposure(140, 20));
        assertEquals(140, relieveExposure(140, 0));
    }

    @Test void zeroRatesDisableOnlyAdditionalExhaustion() {
        for (Dimension d : Dimension.values())
            for (Stage s : Stage.values())
                assertEquals(0, extraExhaustionPerTick(d, s, 0, 0));
    }

    @Test void exposureReliefSaturatesAndDoesNotIncreaseExposure() {
        for (int ticks = 0; ticks <= 140; ticks++) {
            for (int relief = 0; relief <= 140; relief++) {
                int result = relieveExposure(ticks, relief);
                assertTrue(result >= 0 && result <= ticks);
                if (relief >= ticks) assertEquals(0, result);
                else assertEquals(ticks - relief, result);
            }
        }
    }
}
