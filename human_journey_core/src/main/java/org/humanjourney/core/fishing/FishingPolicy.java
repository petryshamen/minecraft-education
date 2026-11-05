package org.humanjourney.core.fishing;
import com.google.gson.*;
import java.util.Set;
/** Whole seconds in data; native fishing effect is already expressed in ticks. */
public record FishingPolicy(int minimumSeconds,int maximumSeconds,int lureReductionSecondsPerLevel){
    public FishingPolicy{if(minimumSeconds<1||maximumSeconds>3600||minimumSeconds>maximumSeconds||lureReductionSecondsPerLevel<0||lureReductionSecondsPerLevel>3600)throw new IllegalArgumentException("bite time requires 1 <= minimum <= maximum <= 3600; Lure reduction must be 0..3600 seconds");}
    public record Bounds(int minimumTicks,int maximumTicks){}
    public Bounds bounds(int nativeLureTicks){
        // Vanilla Lure contributes 100 ticks (5 seconds) per level. Scale its actual native effect,
        // preserving other enchantment-provided fishing-time effects instead of guessing rod levels.
        long reduction=(long)Math.max(0,nativeLureTicks)*lureReductionSecondsPerLevel/5;
        return new Bounds((int)Math.max(1,minimumSeconds*20L-reduction),(int)Math.max(1,maximumSeconds*20L-reduction));
    }
    public static FishingPolicy parse(JsonElement json){
        JsonObject root=object(json,"fishing policy");keys(root,"schema_version","bite_time");
        if(integer(root,"schema_version")!=1)throw new IllegalArgumentException("schema_version must be 1");
        JsonObject bite=object(root.get("bite_time"),"bite_time");keys(bite,"minimum_seconds","maximum_seconds","lure_reduction_seconds_per_level");
        return new FishingPolicy(integer(bite,"minimum_seconds"),integer(bite,"maximum_seconds"),integer(bite,"lure_reduction_seconds_per_level"));
    }
    private static JsonObject object(JsonElement e,String field){if(e==null||!e.isJsonObject())throw new IllegalArgumentException(field+" must be an object");return e.getAsJsonObject();}
    private static void keys(JsonObject o,String... allowed){var keys=Set.of(allowed);for(String key:o.keySet())if(!keys.contains(key))throw new IllegalArgumentException("unknown fishing field: "+key);}
    private static int integer(JsonObject o,String key){var e=o.get(key);if(e==null||!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isNumber())throw new IllegalArgumentException(key+" must be an integer");try{return e.getAsBigDecimal().intValueExact();}catch(ArithmeticException|NumberFormatException ex){throw new IllegalArgumentException(key+" must be an exact integer",ex);}}
}
