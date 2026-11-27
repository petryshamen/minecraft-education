package org.humanjourney.core.advancement;
import java.util.concurrent.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
public final class NativeReloadListener implements PreparableReloadListener {
 public static final String PATH="human_journey:core/advancement_presentation.json";
 private record Prepared(NativePresentation model,String source,String error){}
 private Prepared prepare(ResourceManager manager){String source=PATH;try{var resource=manager.getResource(ResourceLocation.parse(PATH));if(resource.isEmpty())return new Prepared(NativePresentation.empty(),"missing",null);source=resource.get().sourcePackId()+" / "+PATH;try(var reader=resource.get().openAsReader()){var text=new StringBuilder();char[] buffer=new char[8192];int n;while((n=reader.read(buffer))!=-1){text.append(buffer,0,n);if(text.length()>NativePresentation.MAX_JSON)throw new IllegalArgumentException("native presentation exceeds512KiB characters");}var model=NativePresentation.read(text.toString(),id->{var key=ResourceLocation.parse(id);return manager.getResource(ResourceLocation.fromNamespaceAndPath(key.getNamespace(),"advancement/"+key.getPath()+".json")).isPresent();});return new Prepared(model,source,null);}}catch(Exception error){return new Prepared(NativePresentation.empty(),source,source+": "+error.getMessage());}}
 public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(p->{if(p.error!=null){NativeStore.reject(p.source,p.error);com.mojang.logging.LogUtils.getLogger().error("[HJ Core] Native advancement presentation rejected; vanilla fallback: {}",p.error);}else NativeStore.accept(p.model,p.source);},game);}
}
