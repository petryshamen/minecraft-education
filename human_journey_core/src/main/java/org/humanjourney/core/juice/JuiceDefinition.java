package org.humanjourney.core.juice;
import com.google.gson.*;
import java.util.*;
/** Balance/content only. Registry and staged-tag access is injected for deterministic validation. */
public record JuiceDefinition(String id,String bottleItem,Ingredient ingredient,String canteenDisplayName,double healPerDrink){
 public record Ingredient(String item,String tag,Set<String> members){public Ingredient{members=Set.copyOf(members);}public String describe(){return tag==null?item:"#"+tag;}}
 public interface References{boolean bottleSupported(String id);Set<String> ingredient(String item,String tag);}
 private static String text(JsonObject o,String key,int max){if(!o.has(key)||!o.get(key).isJsonPrimitive()||!o.getAsJsonPrimitive(key).isString())throw new IllegalArgumentException("missing/invalid "+key);String s=o.get(key).getAsString();if(s.isBlank()||s.length()>max)throw new IllegalArgumentException("empty/too long "+key);return s;}
 private static void keys(JsonObject o,String... allowed){var names=Set.of(allowed);for(String k:o.keySet())if(!names.contains(k))throw new IllegalArgumentException("unknown juice field: "+k);}
 public static boolean logical(String id){return id!=null&&id.matches("[a-z0-9_][a-z0-9_.-]{0,63}");}
 private static String resource(String id){if(!id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))throw new IllegalArgumentException("invalid resource ID: "+id);return id;}
 public static JuiceDefinition parse(JsonElement json,References refs){
  if(!json.isJsonObject())throw new IllegalArgumentException("juice definition must be an object");var o=json.getAsJsonObject();keys(o,"schema_version","id","bottle_item","ingredient","canteen_display_name","heal_per_drink");
  if(!o.has("schema_version")||!o.get("schema_version").isJsonPrimitive()||!o.getAsJsonPrimitive("schema_version").isNumber()||!o.get("schema_version").getAsString().equals("1"))throw new IllegalArgumentException("juice schema_version must be 1");
  String id=text(o,"id",64);if(!logical(id))throw new IllegalArgumentException("invalid juice id: "+id);String bottle=resource(text(o,"bottle_item",256));if(!refs.bottleSupported(bottle))throw new IllegalArgumentException("unknown or unsupported bottle_item: "+bottle+" (requires unmodified TAN JuiceItem)");
  if(!o.has("ingredient")||!o.get("ingredient").isJsonObject())throw new IllegalArgumentException("ingredient must contain item OR tag");var ing=o.getAsJsonObject("ingredient");keys(ing,"item","tag");if(ing.size()!=1)throw new IllegalArgumentException("ingredient requires exactly one item OR tag");String item=ing.has("item")?resource(text(ing,"item",256)):null,tag=ing.has("tag")?resource(text(ing,"tag",256)):null;var members=refs.ingredient(item,tag);if(members==null||members.isEmpty())throw new IllegalArgumentException("unknown/empty ingredient: "+(item==null?"#"+tag:item));
  String name=text(o,"canteen_display_name",128);if(name.chars().anyMatch(Character::isISOControl))throw new IllegalArgumentException("canteen_display_name contains control characters");
  if(!o.has("heal_per_drink")||!o.get("heal_per_drink").isJsonPrimitive()||!o.getAsJsonPrimitive("heal_per_drink").isNumber())throw new IllegalArgumentException("heal_per_drink must be a finite non-negative number");double heal=o.get("heal_per_drink").getAsDouble();if(!Double.isFinite(heal)||heal<0)throw new IllegalArgumentException("heal_per_drink must be finite and non-negative");
  return new JuiceDefinition(id,bottle,new Ingredient(item,tag,members),name,heal);
 }
}
