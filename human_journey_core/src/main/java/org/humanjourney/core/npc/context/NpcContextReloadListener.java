package org.humanjourney.core.npc.context;
import net.minecraft.server.packs.resources.*;
import net.minecraft.core.HolderLookup;
import org.humanjourney.core.npc.economy.*;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.logging.LogUtils;
import java.util.*;
import java.util.concurrent.*;
public final class NpcContextReloadListener implements PreparableReloadListener {
    private final HolderLookup.Provider registries;
    public NpcContextReloadListener(HolderLookup.Provider registries){this.registries=registries;}
    private int readTotal;
    private record Prepared(NpcContextStore.Data data,String error){}
    private String read(Resource resource)throws java.io.IOException{
        try(var reader=resource.openAsReader()){char[] buffer=new char[8192];StringBuilder result=new StringBuilder();int n;while((n=reader.read(buffer))!=-1){result.append(buffer,0,n);if(result.length()>262144)throw new IllegalArgumentException("NPC context resource exceeds 256 KiB characters");}readTotal+=result.length();if(readTotal>16777216)throw new IllegalArgumentException("NPC context/economy data exceeds 16 MiB total");return result.toString();}
    }
    private List<NpcContextStore.Source> list(ResourceManager manager,String prefix)throws java.io.IOException{
        List<NpcContextStore.Source> result=new ArrayList<>();for(var e:manager.listResources(prefix,id->id.getPath().endsWith(".json")).entrySet()){
            String path=e.getKey().getPath();String id=e.getKey().getNamespace()+":"+path.substring(prefix.length()+1,path.length()-5);result.add(new NpcContextStore.Source(id,e.getValue().sourcePackId(),read(e.getValue())));
        }return result;
    }
    private List<EconomyCatalog.Source> economyList(ResourceManager manager,String prefix)throws java.io.IOException{return list(manager,prefix).stream().map(s->new EconomyCatalog.Source(s.id(),s.json())).toList();}
    public static int itemLimit(String id){var key=ResourceLocation.parse(id);return BuiltInRegistries.ITEM.containsKey(key)?BuiltInRegistries.ITEM.getValue(key).getDefaultInstance().getMaxStackSize():0;}
    private Prepared prepare(ResourceManager manager){
        readTotal=0;try{var config=manager.getResource(ResourceLocation.parse("human_journey:core/npc_context.json"));NpcContextStore.Source source=config.isEmpty()?null:new NpcContextStore.Source("human_journey:core/npc_context",config.get().sourcePackId(),read(config.get()));
            var vocabulary=manager.getResource(ResourceLocation.parse("human_journey:core/economy_vocabulary.json"));
            return new Prepared(NpcContextStore.prepare(source,list(manager,"npc_geography"),list(manager,"npc_archetype"),NpcContextReloadListener::itemLimit,economyList(manager,"npc_commodity"),economyList(manager,"npc_family"),vocabulary.isEmpty()?null:JsonParser.parseString(read(vocabulary.get())),(id,components)->EconomyItems.stack(id,1,components,registries).getMaxStackSize()),null);
        }catch(Exception e){return new Prepared(null,e.getMessage()==null?e.toString():e.getMessage());}
    }
    @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(p->{
        if(p.error()!=null){NpcContextStore.reject(p.error());LogUtils.getLogger().error("[HJ Core] NPC context/archetype reload REJECTED: {}. Keeping last valid context set.",p.error());}
        else{NpcContextStore.accept(p.data());LogUtils.getLogger().info("[HJ Core] NPC context loaded: {} eras, {} archetypes, {} validated variants, revision {}",p.data().eraRules().rules().size(),p.data().archetypes().size(),p.data().validatedVariants(),NpcContextStore.state().revision());}
    },game);}
}
