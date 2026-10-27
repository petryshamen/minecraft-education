package org.humanjourney.core.config;

/** One immutable snapshot per reload; malformed data never partially replaces a policy. */
public final class TemperaturePolicyStore {
    public record Snapshot(TemperatureOverrides overrides, String source, long revision, String lastReload) {
        public TemperatureRules resolve(TemperatureRules fallback) { return overrides.resolve(fallback); }
    }
    private volatile Snapshot snapshot = initial();
    private static Snapshot initial() { return new Snapshot(TemperatureOverrides.empty(), "server config / defaults", 0, "not loaded"); }
    public Snapshot snapshot() { return snapshot; }
    public void accept(TemperatureOverrides overrides, String source) {
        snapshot = new Snapshot(overrides, source, snapshot.revision() + 1, "OK");
    }
    public void missing() { accept(TemperatureOverrides.empty(), "server config / defaults (file missing)"); }
    public void reject(String error) {
        Snapshot previous = snapshot;
        snapshot = new Snapshot(previous.overrides(), previous.source(), previous.revision(), "REJECTED: " + error);
    }
    public void reset() { snapshot = initial(); }
}
