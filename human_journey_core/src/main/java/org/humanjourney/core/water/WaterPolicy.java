package org.humanjourney.core.water;

import com.google.gson.*;
import java.util.*;

/** Reloadable policy; defaults are fallback data, not fixed balance rules. */
public record WaterPolicy(Hazard normal, Hazard dirty, boolean filtering, Set<String> filters, int filterCount,
                          boolean furnace, boolean campfire, int furnaceTicks, int campfireTicks, int handCooldown) {
    public enum Quality { DIRTY, NORMAL, PURIFIED }
    public record Hazard(double chance, int thirstTicks, boolean illness, String effect, int illnessTicks, int amplifier) {
        public Hazard {
            if (!Double.isFinite(chance) || chance < 0 || chance > 1) throw new IllegalArgumentException("unsafe chance must be 0..1");
            if (thirstTicks < 0 || thirstTicks > 72000 || illnessTicks < 0 || illnessTicks > 72000 || amplifier < 0 || amplifier > 4) throw new IllegalArgumentException("invalid effect duration/amplifier");
        }
    }
    public WaterPolicy {
        filters = Set.copyOf(filters);
        if (filters.isEmpty() || filters.size() > 64 || filterCount < 1 || filterCount > 64) throw new IllegalArgumentException("invalid filter materials/count");
        if (furnaceTicks < 1 || furnaceTicks > 72000 || campfireTicks < 1 || campfireTicks > 72000 || handCooldown < 1 || handCooldown > 1200) throw new IllegalArgumentException("invalid process/cooldown ticks");
    }
    public static WaterPolicy defaults() {
        return new WaterPolicy(new Hazard(.60,600,false,"minecraft:poison",0,0),new Hazard(1,600,true,"minecraft:poison",160,0),
            true,Set.of("minecraft:sand","minecraft:charcoal"),1,true,true,500,500,60);
    }
    public static boolean canFilter(Quality quality) { return quality == Quality.DIRTY; }
    public static boolean canBoil(Quality quality) { return quality == Quality.NORMAL; }
    public static boolean unsafe(Hazard hazard, double roll) { return roll < hazard.chance(); }
    public static WaterPolicy parse(JsonElement json) {
        if (!json.isJsonObject()) throw new IllegalArgumentException("water.json must be an object");
        JsonObject r=json.getAsJsonObject(); keys(r,"schema_version","normal","dirty","filtration","boiling","hand_drinking");
        if (integer(r,"schema_version",1)!=1) throw new IllegalArgumentException("unsupported water schema_version");
        WaterPolicy d=defaults();
        JsonObject n=section(r,"normal"), dirty=section(r,"dirty"), f=section(r,"filtration"), b=section(r,"boiling"), h=section(r,"hand_drinking");
        keys(n,"unsafe_chance","thirst_duration_ticks"); keys(dirty,"unsafe_chance","thirst_duration_ticks","illness");
        JsonObject illness=section(dirty,"illness"); keys(illness,"enabled","effect","duration_ticks","amplifier");
        keys(f,"enabled","materials","material_count","from","to"); keys(b,"furnace_enabled","campfire_enabled","furnace_ticks","campfire_ticks","from","to"); keys(h,"cooldown_ticks");
        if (!string(f,"from","dirty").equals("dirty") || !string(f,"to","normal").equals("normal") || !string(b,"from","normal").equals("normal") || !string(b,"to","purified").equals("purified")) throw new IllegalArgumentException("schema 1 supports filtration dirty->normal and boiling normal->purified only");
        Set<String> materials=d.filters;
        if(f.has("materials")) {
            if(!f.get("materials").isJsonArray()) throw new IllegalArgumentException("materials must be an item-ID array");
            Set<String> parsed=new LinkedHashSet<>();
            for(JsonElement e:f.getAsJsonArray("materials")) { if(!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) throw new IllegalArgumentException("filter material must be an item ID"); if(!parsed.add(e.getAsString())) throw new IllegalArgumentException("duplicate filter material"); }
            materials=parsed;
        }
        return new WaterPolicy(new Hazard(number(n,"unsafe_chance",d.normal.chance),integer(n,"thirst_duration_ticks",d.normal.thirstTicks),false,"minecraft:poison",0,0),
            new Hazard(number(dirty,"unsafe_chance",d.dirty.chance),integer(dirty,"thirst_duration_ticks",d.dirty.thirstTicks),bool(illness,"enabled",true),string(illness,"effect","minecraft:poison"),integer(illness,"duration_ticks",160),integer(illness,"amplifier",0)),
            bool(f,"enabled",d.filtering),materials,integer(f,"material_count",1),bool(b,"furnace_enabled",true),bool(b,"campfire_enabled",true),integer(b,"furnace_ticks",500),integer(b,"campfire_ticks",500),integer(h,"cooldown_ticks",60));
    }
    private static JsonObject section(JsonObject o,String k) { if(!o.has(k)) return new JsonObject(); if(!o.get(k).isJsonObject()) throw new IllegalArgumentException(k+" must be an object"); return o.getAsJsonObject(k); }
    private static void keys(JsonObject o,String... allowed) { var set=Set.of(allowed); for(String k:o.keySet()) if(!set.contains(k)) throw new IllegalArgumentException("unknown water field: "+k); }
    private static JsonPrimitive primitive(JsonObject o,String k) { if(!o.get(k).isJsonPrimitive()) throw new IllegalArgumentException(k+" has invalid type"); return o.getAsJsonPrimitive(k); }
    private static double number(JsonObject o,String k,double d) { if(!o.has(k)) return d; var v=primitive(o,k); if(!v.isNumber() || !Double.isFinite(v.getAsDouble())) throw new IllegalArgumentException(k+" must be a finite number"); return v.getAsDouble(); }
    private static int integer(JsonObject o,String k,int d) { double v=number(o,k,d); if(v!=Math.rint(v) || v<Integer.MIN_VALUE || v>Integer.MAX_VALUE) throw new IllegalArgumentException(k+" must be an integer"); return (int)v; }
    private static boolean bool(JsonObject o,String k,boolean d) { if(!o.has(k)) return d; var v=primitive(o,k); if(!v.isBoolean()) throw new IllegalArgumentException(k+" must be boolean"); return v.getAsBoolean(); }
    private static String string(JsonObject o,String k,String d) { if(!o.has(k)) return d; var v=primitive(o,k); if(!v.isString()) throw new IllegalArgumentException(k+" must be a string"); return v.getAsString(); }
}
