package org.humanjourney.core.npc;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import java.util.*;
import java.util.concurrent.*;
public final class NpcReloadListener implements PreparableReloadListener {
    private record Result(NpcTemplates.Prepared prepared,String error){}
    private Result prepare(ResourceManager manager){
        try{
            List<NpcTemplates.Source> sources=new ArrayList<>();
            for(var entry:manager.listResources("npc",id->id.getPath().endsWith(".json")).entrySet()){
                String path=entry.getKey().getPath();String id=entry.getKey().getNamespace()+":"+path.substring(4,path.length()-5);
                try(var reader=entry.getValue().openAsReader()){
                    char[] buffer=new char[8192];StringBuilder json=new StringBuilder();int n;
                    while((n=reader.read(buffer))!=-1){json.append(buffer,0,n);if(json.length()>262144)throw new IllegalArgumentException(id+": template exceeds 256 KiB");}
                    sources.add(new NpcTemplates.Source(id,entry.getValue().sourcePackId(),json.toString()));
                }
            }
            return new Result(NpcTemplates.prepare(sources,id->{var key=ResourceLocation.parse(id);return BuiltInRegistries.ITEM.containsKey(key)?BuiltInRegistries.ITEM.getValue(key).getDefaultInstance().getMaxStackSize():0;}),null);
        }catch(Exception e){return new Result(null,e.getMessage()==null?e.toString():e.getMessage());}
    }
    @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){
        return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(result->{
            if(result.error()!=null){NpcTemplates.reject(result.error());LogUtils.getLogger().error("[HJ Core] NPC template reload REJECTED: {}. Keeping last valid set.",result.error());}
            else{NpcTemplates.accept(result.prepared());LogUtils.getLogger().info("[HJ Core] Loaded {} NPC templates, revision {}",NpcTemplates.state().templates().size(),NpcTemplates.state().revision());}
        },game);
    }
}
