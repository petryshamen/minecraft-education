package org.humanjourney.core.juice;
import java.util.*;
public record JuiceCatalog(Map<String,JuiceDefinition> definitions,Map<String,JuiceDefinition> bottles,Map<String,JuiceDefinition> ingredients){
 public JuiceCatalog{definitions=Map.copyOf(definitions);bottles=Map.copyOf(bottles);ingredients=Map.copyOf(ingredients);}
 public static JuiceCatalog empty(){return of(List.of());}
 public static JuiceCatalog of(Collection<JuiceDefinition> entries){if(entries.size()>256)throw new IllegalArgumentException("at most 256 juice definitions");var ids=new TreeMap<String,JuiceDefinition>();var bottles=new TreeMap<String,JuiceDefinition>();var ingredients=new TreeMap<String,JuiceDefinition>();for(var d:entries){if(ids.putIfAbsent(d.id(),d)!=null)throw new IllegalArgumentException("duplicate juice ID: "+d.id());if(bottles.putIfAbsent(d.bottleItem(),d)!=null)throw new IllegalArgumentException("duplicate juice bottle_item: "+d.bottleItem());for(String item:d.ingredient().members()){var previous=ingredients.putIfAbsent(item,d);if(previous!=null)throw new IllegalArgumentException("ambiguous juice ingredient "+item+": "+previous.id()+" / "+d.id());}}return new JuiceCatalog(ids,bottles,ingredients);}
}
