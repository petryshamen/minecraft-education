package org.humanjourney.core.npc.economy;
import com.google.gson.*;
import java.util.*;
import static org.humanjourney.core.npc.economy.JsonData.*;
public record EconomySettings(boolean enabled,boolean wandering,boolean lookAtPlayers,int wanderRadius,Map<String,String> familyOutputs){
 public EconomySettings{familyOutputs=Map.copyOf(familyOutputs);}
 public static EconomySettings off(){return new EconomySettings(false,false,false,10,Map.of());}
 public static EconomySettings parse(JsonElement json){if(json==null)return off();var o=object(json);keys(o,"enabled","wandering","look_at_players","wander_radius","family_outputs");var overrides=new TreeMap<String,String>();if(o.has("family_outputs"))for(var e:object(o.get("family_outputs")).entrySet()){if(!e.getValue().isJsonPrimitive()||!e.getValue().getAsJsonPrimitive().isString())throw new IllegalArgumentException("family output must be item ID");overrides.put(qualify(e.getKey()),id(e.getValue().getAsString()));}return new EconomySettings(bool(o,"enabled",false),bool(o,"wandering",true),bool(o,"look_at_players",true),o.has("wander_radius")?integer(o,"wander_radius",1,64):10,overrides);}
 private static boolean bool(JsonObject o,String key,boolean fallback){if(!o.has(key))return fallback;var e=o.get(key);if(!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isBoolean())throw new IllegalArgumentException(key+" must be boolean");return e.getAsBoolean();}
}
