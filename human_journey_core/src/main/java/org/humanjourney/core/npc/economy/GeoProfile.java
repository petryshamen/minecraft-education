package org.humanjourney.core.npc.economy;
import com.google.gson.*;
import java.util.*;
import static org.humanjourney.core.npc.economy.JsonData.*;
/** Complete, replace-only environmental data. No biome inference or extra geometry. */
public record GeoProfile(Map<String,Set<String>> values){
 public static final List<String> DIMENSIONS=List.of("temperature","moisture","vegetation","water","relief","special");
 public GeoProfile{var copy=new LinkedHashMap<String,Set<String>>();for(String d:DIMENSIONS)copy.put(d,Set.copyOf(values.getOrDefault(d,Set.of())));values=Collections.unmodifiableMap(copy);}
 public static Map<String,Set<String>> vocabulary(JsonElement extensions){
  String[] raw={"polar cold cool temperate warm hot","arid dry moderate humid wet","barren tundra grassland shrubland woodland forest rainforest wetland","inland river lake coast fjord ocean","lowland plain hills upland mountain plateau valley island","cave_access deep_water_access mountain_access"};
  var v=new LinkedHashMap<String,Set<String>>();for(int i=0;i<raw.length;i++)v.put(DIMENSIONS.get(i),new TreeSet<>(List.of(raw[i].split(" "))));
  if(extensions!=null){var o=object(extensions);keys(o,"schema_version","additions");schema(o);var additions=object(o.get("additions"));keys(additions,DIMENSIONS.toArray(String[]::new));for(var entry:additions.entrySet())for(String value:strings(entry.getValue())){if(!value.matches("[a-z0-9_]{1,64}")||value.equals("any"))throw new IllegalArgumentException("invalid vocabulary addition: "+value);v.get(entry.getKey()).add(value);}}
  var result=new LinkedHashMap<String,Set<String>>();v.forEach((k,s)->result.put(k,Set.copyOf(s)));return Map.copyOf(result);
 }
 public static GeoProfile parse(JsonElement json,Map<String,Set<String>> vocabulary){var o=object(json);keys(o,DIMENSIONS.toArray(String[]::new));var result=new LinkedHashMap<String,Set<String>>();for(String d:DIMENSIONS){if(!o.has(d))throw new IllegalArgumentException("profile missing dimension: "+d);var values=strings(o.get(d));if(!d.equals("special")&&values.isEmpty())throw new IllegalArgumentException("empty profile dimension: "+d);for(String value:values)if(!vocabulary.get(d).contains(value))throw new IllegalArgumentException("unknown "+d+" value: "+value);result.put(d,Set.copyOf(values));}return new GeoProfile(result);}
 public static GeoProfile none(){return new GeoProfile(Map.of());}
 public record Availability(Map<String,Set<String>> allowed){
  public Availability{var copy=new LinkedHashMap<String,Set<String>>();allowed.forEach((k,v)->copy.put(k,Set.copyOf(v)));allowed=Map.copyOf(copy);}
  public Map<String,Boolean> explain(GeoProfile profile){var result=new LinkedHashMap<String,Boolean>();for(String d:DIMENSIONS)result.put(d,!allowed.containsKey(d)||!Collections.disjoint(allowed.get(d),profile.values().get(d)));return Collections.unmodifiableMap(result);}
  public boolean matches(GeoProfile p){return explain(p).values().stream().allMatch(Boolean::booleanValue);}
 }
 public static Availability availability(JsonElement json,Map<String,Set<String>> vocabulary){var o=object(json);keys(o,DIMENSIONS.toArray(String[]::new));var result=new LinkedHashMap<String,Set<String>>();for(String d:DIMENSIONS){if(!o.has(d))throw new IllegalArgumentException("sellAvailability missing dimension: "+d);var e=o.get(d);if(e!=null&&e.isJsonPrimitive()&&e.getAsJsonPrimitive().isString()&&e.getAsString().equals("any"))continue;var values=strings(e);if(values.isEmpty())throw new IllegalArgumentException("empty sellAvailability dimension: "+d);for(String value:values)if(!vocabulary.get(d).contains(value))throw new IllegalArgumentException("unknown sellAvailability "+d+": "+value);result.put(d,Set.copyOf(values));}return new Availability(result);}
}
