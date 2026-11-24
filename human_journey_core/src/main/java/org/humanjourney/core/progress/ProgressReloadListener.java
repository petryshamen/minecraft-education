package org.humanjourney.core.progress;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.*;
import java.util.concurrent.*;
public final class ProgressReloadListener implements PreparableReloadListener {
 public static final String PATH="human_journey:core/civilization_progress.json";
 private record Prepared(ProgressGraph graph,String source,String error){}
 private Prepared prepare(ResourceManager manager){String source=PATH;try{var resource=manager.getResource(ResourceLocation.parse(PATH));if(resource.isEmpty())return new Prepared(null,"missing",null);source=resource.get().sourcePackId()+" / "+PATH;try(var reader=resource.get().openAsReader()){var text=new StringBuilder();char[] buffer=new char[8192];int n;while((n=reader.read(buffer))!=-1){text.append(buffer,0,n);if(text.length()>ProgressGraph.MAX_JSON)throw new IllegalArgumentException("graph exceeds1MiB characters");}var graph=ProgressGraph.parse(ProgressJson.read(text.toString()),id->BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id)));return new Prepared(graph,source,null);}}catch(Exception error){return new Prepared(null,source,source+": "+error.getMessage());}}
 @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(p->{if(p.error!=null){ProgressStore.reject(p.error);LogUtils.getLogger().error("[HJ Core] Civilization graph REJECTED: {}. Retaining complete previous graph; progression untouched.",p.error);}else if(p.graph==null)ProgressStore.missing();else{ProgressStore.accept(p.graph,p.source);LogUtils.getLogger().info("[HJ Core] Civilization graph loaded: {} nodes, {} connections from {}",p.graph.nodes().size(),p.graph.connections().size(),p.source);}},game);}
}
