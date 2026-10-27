package org.humanjourney.core.data;

import org.humanjourney.core.config.TemperatureConfig;
import org.humanjourney.core.config.TemperaturePolicyStore;
import org.humanjourney.core.config.TemperatureRules;

public final class TemperatureBalance {
    public static final TemperaturePolicyStore STORE = new TemperaturePolicyStore();
    public static TemperatureRules current() {
        // SERVER config is read only during active server ticks/commands, not asynchronous resource preparation.
        return STORE.snapshot().resolve(TemperatureConfig.snapshot());
    }
    private TemperatureBalance() {}
}
