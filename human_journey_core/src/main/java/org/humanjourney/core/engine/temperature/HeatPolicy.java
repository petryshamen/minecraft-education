package org.humanjourney.core.engine.temperature;

/** Pure policy: no lessons, progression IDs, entities, content or network dependencies. */
public final class HeatPolicy {
    public enum Dimension { OVERWORLD, NETHER, OTHER }
    public enum Stage { ICY, COLD, NEUTRAL, WARM, HOT }

    public static double extraExhaustionPerTick(Dimension dimension, Stage stage,
                                               double warmPerSecond, double hotPerSecond) {
        if (dimension == Dimension.OTHER) return 0;
        if (stage == Stage.HOT) return hotPerSecond / 20.0;
        if (stage == Stage.WARM) return warmPerSecond / 20.0;
        return 0;
    }

    public static boolean blockHeatDamage(Dimension dimension, boolean overworldDamage,
                                          boolean netherDamage) {
        return switch (dimension) {
            case OVERWORLD -> !overworldDamage;
            case NETHER -> !netherDamage;
            case OTHER -> false;
        };
    }

    public static int relieveExposure(int currentExposure, int relief) {
        return Math.max(0, currentExposure - Math.max(0, relief));
    }

    public static Stage coolOneStep(Stage stage) {
        return switch (stage) {
            case HOT -> Stage.WARM;
            case WARM -> Stage.NEUTRAL;
            default -> stage;
        };
    }
    private HeatPolicy() {}
}
