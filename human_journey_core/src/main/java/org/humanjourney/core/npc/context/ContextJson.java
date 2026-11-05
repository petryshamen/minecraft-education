package org.humanjourney.core.npc.context;
import com.google.gson.*;
import java.util.*;
final class ContextJson {
    static JsonObject object(JsonElement e,String name){if(e==null||!e.isJsonObject())throw new IllegalArgumentException(name+" must be an object");return e.getAsJsonObject();}
    static JsonObject section(JsonObject o,String key){return o.has(key)?object(o.get(key),key):new JsonObject();}
    static void keys(JsonObject o,String... allowed){var set=Set.of(allowed);for(String key:o.keySet())if(!set.contains(key))throw new IllegalArgumentException("unknown NPC context field: "+key);}
    static String text(JsonObject o,String key,String fallback,int max){if(!o.has(key)){if(fallback!=null)return fallback;throw new IllegalArgumentException("missing "+key);}var e=o.get(key);if(!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isString())throw new IllegalArgumentException(key+" must be a string");String s=e.getAsString();if(s.length()>max||(fallback==null&&s.isBlank())||s.codePoints().anyMatch(Character::isISOControl))throw new IllegalArgumentException("invalid "+key);return s;}
    static int integer(JsonObject o,String key,int fallback,int min,int max){if(!o.has(key))return fallback;var e=o.get(key);if(!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isNumber())throw new IllegalArgumentException(key+" must be integer");double n=e.getAsDouble();if(!Double.isFinite(n)||n!=Math.rint(n)||n<min||n>max)throw new IllegalArgumentException(key+" outside "+min+".."+max);return (int)n;}
    static JsonArray array(JsonObject o,String key,int min,int max){if(!o.has(key)){if(min==0)return new JsonArray();throw new IllegalArgumentException("missing "+key);}var e=o.get(key);if(!e.isJsonArray()||e.getAsJsonArray().size()<min||e.getAsJsonArray().size()>max)throw new IllegalArgumentException(key+" must have "+min+".."+max+" entries");return e.getAsJsonArray();}
    static String logical(String s){if(!s.matches("[a-z0-9_][a-z0-9_.-]{0,63}")||Set.of("none","unknown").contains(s))throw new IllegalArgumentException("invalid/reserved logical ID: "+s);return s;}
    static String resource(String s){if(!s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))throw new IllegalArgumentException("invalid resource ID: "+s);return s;}
    static void schema(JsonObject o){if(integer(o,"schema_version",1,1,1)!=1)throw new IllegalArgumentException("schema_version must be 1");}
    private ContextJson(){}
}
