package org.humanjourney.core.npc.context;
import net.minecraft.server.packs.resources.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.logging.LogUtils;
import java.util.*;
import java.util.concurrent.*;
public final class NpcContextReloadListener implements PreparableReloadListener {
    private record Prepared(NpcContextStore.Data data,String error){}
    private static String read(Resource resource)throws java.io.IOException{
        try(var reader=resource.openAsReader()){char[] buffer=new char[8192];StringBuilder result=new StringBuilder();int n;while((n=reader.read(buffer))!=-1){result.append(buffer,0,n);if(result.length()>262144)throw new IllegalArgumentException("NPC context resource exceeds 256 KiB characters");}return result.toString();}
    }
    private static List<NpcContextStore.Source> list(ResourceManager manager,String prefix)throws java.io.IOException{
        List<NpcContextStore.Source> result=new ArrayList<>();for(var e:manager.listResources(prefix,id->id.getPath().endsWith(".json")).entrySet()){
            String path=e.getKey().getPath();String id=e.getKey().getNamespace()+":"+path.substring(prefix.length()+1,path.length()-5);result.add(new NpcContextStore.Source(id,e.getValue().sourcePackId(),read(e.getValue())));
        }return result;
    }
    public static int itemLimit(String id){var key=ResourceLocation.parse(id);return BuiltInRegistries.ITEM.containsKey(key)?BuiltInRegistries.ITEM.getValue(key).getDefaultInstance().getMaxStackSize():0;}
    private Prepared prepare(ResourceManager manager){
        try{var config=manager.getResource(ResourceLocation.parse("human_journey:core/npc_context.json"));NpcContextStore.Source source=config.isEmpty()?null:new NpcContextStore.Source("human_journey:core/npc_context",config.get().sourcePackId(),read(config.get()));
            return new Prepared(NpcContextStore.prepare(source,list(manager,"npc_geography"),list(manager,"npc_archetype"),NpcContextReloadListener::itemLimit),null);
        }catch(Exception e){return new Prepared(null,e.getMessage()==null?e.toString():e.getMessage());}
    }
    @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(p->{
        if(p.error()!=null){NpcContextStore.reject(p.error());LogUtils.getLogger().error("[HJ Core] NPC context/archetype reload REJECTED: {}. Keeping last valid context set.",p.error());}
        else{NpcContextStore.accept(p.data());LogUtils.getLogger().info("[HJ Core] NPC context loaded: {} eras, {} archetypes, {} validated variants, revision {}",p.data().eraRules().rules().size(),p.data().archetypes().size(),p.data().validatedVariants(),NpcContextStore.state().revision());}
    },game);}
}
