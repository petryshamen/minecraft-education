package org.humanjourney.core.npc.economy;
import com.google.gson.*;
import java.util.*;
public final class JsonData {
 public static JsonObject object(JsonElement e){if(e==null||!e.isJsonObject())throw new IllegalArgumentException("expected object");return e.getAsJsonObject();}
 public static void keys(JsonObject o,String... names){var allowed=Set.of(names);for(var k:o.keySet())if(!allowed.contains(k))throw new IllegalArgumentException("unknown economy field: "+k);}
 public static String text(JsonObject o,String key){var e=o.get(key);if(e==null||!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isString()||e.getAsString().isBlank())throw new IllegalArgumentException("required string: "+key);return e.getAsString();}
 public static String id(String s){if(!s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))throw new IllegalArgumentException("invalid resource ID: "+s);return s;}
 public static String qualify(String s){return id(s.contains(":")?s:"human_journey:"+s);}
 public static int integer(JsonObject o,String key,int min,int max){try{var e=o.get(key);if(e==null||!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isNumber())throw new Exception();int v=e.getAsBigDecimal().intValueExact();if(v<min||v>max)throw new Exception();return v;}catch(Exception e){throw new IllegalArgumentException("invalid integer "+key+" ("+min+".."+max+")");}}
 public static void schema(JsonObject o){if(integer(o,"schema_version",1,1)!=1)throw new IllegalArgumentException("unsupported schema");}
 public static List<String> strings(JsonElement e){if(e==null||!e.isJsonArray())throw new IllegalArgumentException("expected string array");List<String> out=new ArrayList<>();for(var n:e.getAsJsonArray()){if(!n.isJsonPrimitive()||!n.getAsJsonPrimitive().isString()||n.getAsString().isBlank()||out.contains(n.getAsString()))throw new IllegalArgumentException("invalid/duplicate string in array");out.add(n.getAsString());}return List.copyOf(out);}
 private JsonData(){}
}
