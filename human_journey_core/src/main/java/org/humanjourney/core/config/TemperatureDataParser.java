package org.humanjourney.core.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Set;

/** Strict types and field names prevent typoed balance settings silently taking effect. */
public final class TemperatureDataParser {
    public static TemperatureOverrides parse(JsonElement json) {
        if (json == null || !json.isJsonObject()) throw new IllegalArgumentException("temperature.json must be an object");
        JsonObject root = json.getAsJsonObject();
        keys(root, Set.of("schema_version", "thirst", "overworld", "nether", "water"));
        Integer schema = integer(root, "schema_version");
        if (schema != null && schema != 1) throw new IllegalArgumentException("Unsupported schema_version: " + schema);
        JsonObject thirst = section(root, "thirst", Set.of("warm_exhaustion_per_second", "hot_exhaustion_per_second", "nether_hot_exhaustion_per_second"));
        JsonObject overworld = section(root, "overworld", Set.of("hyperthermia_damage", "heat_damage_interval_ticks", "heat_damage_amount"));
        JsonObject nether = section(root, "nether", Set.of("hyperthermia_damage", "heat_damage_interval_ticks", "heat_damage_amount", "remove_climate_clemency"));
        JsonObject water = section(root, "water", Set.of("heat_relief_ticks_per_tick", "cooling_interval_ticks"));
        TemperatureOverrides result = new TemperatureOverrides(
            number(thirst, "warm_exhaustion_per_second"), number(thirst, "hot_exhaustion_per_second"), number(thirst, "nether_hot_exhaustion_per_second"),
            bool(overworld, "hyperthermia_damage"), integer(overworld, "heat_damage_interval_ticks"), number(overworld, "heat_damage_amount"),
            bool(nether, "hyperthermia_damage"), integer(nether, "heat_damage_interval_ticks"), number(nether, "heat_damage_amount"), bool(nether, "remove_climate_clemency"),
            integer(water, "heat_relief_ticks_per_tick"), integer(water, "cooling_interval_ticks"));
        result.resolve(TemperatureRules.defaults()); // Validate every supplied value before publication.
        return result;
    }
    private static void keys(JsonObject object, Set<String> allowed) {
        for (String key : object.keySet()) if (!allowed.contains(key)) throw new IllegalArgumentException("Unknown temperature field: " + key);
    }
    private static JsonObject section(JsonObject root, String key, Set<String> allowed) {
        if (!root.has(key)) return new JsonObject();
        if (!root.get(key).isJsonObject()) throw new IllegalArgumentException(key + " must be an object");
        JsonObject result = root.getAsJsonObject(key);
        keys(result, allowed);
        return result;
    }
    private static Double number(JsonObject object, String key) {
        if (!object.has(key)) return null;
        JsonElement value = object.get(key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException(key + " must be a number");
        double result = value.getAsDouble();
        if (!Double.isFinite(result)) throw new IllegalArgumentException(key + " must be finite");
        return result;
    }
    private static Integer integer(JsonObject object, String key) {
        Double value = number(object, key);
        if (value == null) return null;
        if (value != Math.rint(value) || value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) throw new IllegalArgumentException(key + " must be an integer");
        return value.intValue();
    }
    private static Boolean bool(JsonObject object, String key) {
        if (!object.has(key)) return null;
        JsonElement value = object.get(key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException(key + " must be a boolean");
        return value.getAsBoolean();
    }
    private TemperatureDataParser() {}
}
