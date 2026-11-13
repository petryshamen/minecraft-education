package org.humanjourney.core.juice;
import com.google.gson.JsonParser;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.server.packs.resources.*;
import java.util.*;
import java.util.concurrent.*;
public final class JuiceReloadListener implements PreparableReloadListener {
 public static JuiceDefinition.References references(HolderLookup.Provider lookup){return new JuiceDefinition.References(){
  public boolean bottleSupported(String id){var key=ResourceLocation.parse(id);if(!BuiltInRegistries.ITEM.containsKey(key))return false;var item=BuiltInRegistries.ITEM.getValue(key);return item.getClass()==toughasnails.item.JuiceItem.class&&!item.components().has(net.minecraft.core.component.DataComponents.CONSUMABLE)&&!item.components().has(net.minecraft.core.component.DataComponents.FOOD);}
  public Set<String> ingredient(String item,String tag){if(item!=null){var key=ResourceLocation.parse(item);if(!BuiltInRegistries.ITEM.containsKey(key)||key.equals(ResourceLocation.parse("minecraft:air")))throw new IllegalArgumentException("unknown/invalid ingredient item: "+item);return Set.of(item);}var key=TagKey.create(Registries.ITEM,ResourceLocation.parse(tag));var holders=lookup.lookupOrThrow(Registries.ITEM).get(key).orElseThrow(()->new IllegalArgumentException("unknown ingredient tag: #"+tag));var result=new TreeSet<String>();holders.stream().forEach(h->{var value=h.value();if(value==Items.AIR)throw new IllegalArgumentException("air in ingredient tag: #"+tag);result.add(BuiltInRegistries.ITEM.getKey(value).toString());});if(result.size()>4096)throw new IllegalArgumentException("ingredient tag too large: #"+tag);return result;}
 };}
 private final HolderLookup.Provider lookup;
 public JuiceReloadListener(HolderLookup.Provider lookup){this.lookup=lookup;}
 private record Prepared(JuiceCatalog catalog,String source,String error){}
 private Prepared prepare(ResourceManager manager){try{var entries=new ArrayList<JuiceDefinition>();var sources=new TreeSet<String>();var resources=new TreeMap<>(manager.listResources("core/juice",p->p.getNamespace().equals("human_journey")&&p.getPath().endsWith(".json")));if(resources.size()>256)throw new IllegalArgumentException("at most 256 juice resources");for(var entry:resources.entrySet())try(var reader=entry.getValue().openAsReader()){entries.add(JuiceDefinition.parse(JsonParser.parseReader(reader),references(lookup)));sources.add(entry.getValue().sourcePackId());}catch(Exception e){throw new IllegalArgumentException(entry.getKey()+": "+e.getMessage(),e);}return new Prepared(JuiceCatalog.of(entries),resources.isEmpty()?"absent":String.join(", ",sources),null);}catch(Exception e){return new Prepared(null,null,e.getMessage());}}
 @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(p->{if(p.error()!=null){JuiceStore.reject(p.error());com.mojang.logging.LogUtils.getLogger().error("[HJ Core] Juice definitions rejected: {}. Keeping last valid catalog",p.error());}else{JuiceStore.accept(p.catalog(),p.source());com.mojang.logging.LogUtils.getLogger().info("[HJ Core] Loaded {} juice definitions from {}",p.catalog().definitions().size(),p.source());}},game);}
}
