package org.humanjourney.core.juice;
import com.google.gson.*;
import java.util.*;
/** Content-only v1/v2 contract. Native effects remain sourced from TAN, not copied constants. */
public record JuiceDefinition(int schemaVersion,String id,Bottle bottle,Ingredient ingredient,int bottleIngredientCount,int canteenIngredientCount,String canteenDisplayName,double healPerDrink){
 public record Bottle(boolean custom,String item,String displayName,String effectSourceItem){}
 public record Ingredient(String item,String tag,Set<String> members){public Ingredient{members=Set.copyOf(members);}public String describe(){return tag==null?item:"#"+tag;}}
 public interface References{boolean bottleSupported(String id);Set<String> ingredient(String item,String tag);}
 /** Source compatibility for existing v1 fixtures/adapters. */
 public JuiceDefinition(String id,String bottleItem,Ingredient ingredient,String name,double heal){this(1,id,new Bottle(false,bottleItem,null,bottleItem),ingredient,1,1,name,heal);}
 public String bottleItem(){return bottle.custom()?"human_journey_core:juice_bottle":bottle.item();}
 public String effectSourceItem(){return bottle.effectSourceItem();}
 private static String text(JsonObject o,String key,int max){if(!o.has(key)||!o.get(key).isJsonPrimitive()||!o.getAsJsonPrimitive(key).isString())throw new IllegalArgumentException("missing/invalid "+key);String s=o.get(key).getAsString();if(s.isBlank()||s.length()>max)throw new IllegalArgumentException("empty/too long "+key);return s;}
 private static String label(JsonObject o,String key){String s=text(o,key,128);if(s.chars().anyMatch(Character::isISOControl))throw new IllegalArgumentException(key+" contains control characters");return s;}
 private static void keys(JsonObject o,String... allowed){var names=Set.of(allowed);for(String k:o.keySet())if(!names.contains(k))throw new IllegalArgumentException("unknown juice field: "+k);}
 public static boolean logical(String id){return id!=null&&id.matches("[a-z0-9_][a-z0-9_.-]{0,63}");}
 private static String resource(String id){if(!id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))throw new IllegalArgumentException("invalid resource ID: "+id);return id;}
 private static String source(JsonObject o,String key,References refs){String id=resource(text(o,key,256));if(!refs.bottleSupported(id))throw new IllegalArgumentException("unknown or unsupported "+key+": "+id+" (requires unmodified TAN JuiceItem)");return id;}
 private static int count(JsonObject o,String key){if(!o.has(key)||!o.get(key).isJsonPrimitive()||!o.getAsJsonPrimitive(key).isNumber())throw new IllegalArgumentException(key+" must be an integer 1..64");try{int n=o.get(key).getAsBigDecimal().intValueExact();if(n<1||n>64)throw new ArithmeticException();return n;}catch(ArithmeticException|NumberFormatException e){throw new IllegalArgumentException(key+" must be an integer 1..64");}}
 public static JuiceDefinition parse(JsonElement json,References refs){
  if(!json.isJsonObject())throw new IllegalArgumentException("juice definition must be an object");var o=json.getAsJsonObject();if(!o.has("schema_version")||!o.get("schema_version").isJsonPrimitive()||!o.getAsJsonPrimitive("schema_version").isNumber()||!Set.of("1","2").contains(o.get("schema_version").getAsString()))throw new IllegalArgumentException("juice schema_version must be 1 or 2");int version=o.get("schema_version").getAsInt();
  if(version==1)keys(o,"schema_version","id","bottle_item","ingredient","canteen_display_name","heal_per_drink");else keys(o,"schema_version","id","bottle","ingredient","bottle_ingredient_count","canteen_ingredient_count","canteen_display_name","heal_per_drink");
  String id=text(o,"id",64);if(!logical(id))throw new IllegalArgumentException("invalid juice id: "+id);Bottle bottle;
  if(version==1){String item=source(o,"bottle_item",refs);bottle=new Bottle(false,item,null,item);}else{if(!o.has("bottle")||!o.get("bottle").isJsonObject())throw new IllegalArgumentException("missing/invalid bottle mode");var b=o.getAsJsonObject("bottle");String type=text(b,"type",16);if(type.equals("native")){keys(b,"type","item");String item=source(b,"item",refs);bottle=new Bottle(false,item,null,item);}else if(type.equals("custom")){keys(b,"type","display_name","effect_source_item");bottle=new Bottle(true,null,label(b,"display_name"),source(b,"effect_source_item",refs));}else throw new IllegalArgumentException("bottle.type must be native or custom");}
  int bottleCount=version==1?1:count(o,"bottle_ingredient_count"),canteenCount=version==1?1:count(o,"canteen_ingredient_count");
  if(!o.has("ingredient")||!o.get("ingredient").isJsonObject())throw new IllegalArgumentException("ingredient must contain item OR tag");var ing=o.getAsJsonObject("ingredient");keys(ing,"item","tag");if(ing.size()!=1)throw new IllegalArgumentException("ingredient requires exactly one item OR tag");String item=ing.has("item")?resource(text(ing,"item",256)):null,tag=ing.has("tag")?resource(text(ing,"tag",256)):null;var members=refs.ingredient(item,tag);if(members==null||members.isEmpty())throw new IllegalArgumentException("unknown/empty ingredient: "+(item==null?"#"+tag:item));
  String name=label(o,"canteen_display_name");if(!o.has("heal_per_drink")||!o.get("heal_per_drink").isJsonPrimitive()||!o.getAsJsonPrimitive("heal_per_drink").isNumber())throw new IllegalArgumentException("heal_per_drink must be a finite non-negative number");double heal=o.get("heal_per_drink").getAsDouble();if(!Double.isFinite(heal)||heal<0)throw new IllegalArgumentException("heal_per_drink must be finite and non-negative");return new JuiceDefinition(version,id,bottle,new Ingredient(item,tag,members),bottleCount,canteenCount,name,heal);
 }
}
