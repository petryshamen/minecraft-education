package org.humanjourney.core.water;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import java.util.concurrent.*;

public final class WaterReloadListener implements PreparableReloadListener {
    private record Prepared(WaterPolicy policy,String source,String error){}
    private Prepared prepare(ResourceManager manager){
        try{
            var resource=manager.getResource(ResourceLocation.fromNamespaceAndPath("human_journey","core/water.json"));
            if(resource.isEmpty()) return new Prepared(null,null,null);
            try(var reader=resource.get().openAsReader()){
                WaterPolicy p=WaterPolicy.parse(JsonParser.parseReader(reader));
                for(String item:p.filters()) {
                    var id=ResourceLocation.tryParse(item);
                    if(id==null || !BuiltInRegistries.ITEM.containsKey(id) || item.equals("minecraft:air")) throw new IllegalArgumentException("unknown/invalid filter item: "+item);
                }
                var effect=ResourceLocation.tryParse(p.dirty().effect());
                if(effect==null || !BuiltInRegistries.MOB_EFFECT.containsKey(effect)) throw new IllegalArgumentException("unknown illness effect: "+p.dirty().effect());
                return new Prepared(p,resource.get().sourcePackId(),null);
            }
        }catch(Exception e){return new Prepared(null,null,e.getMessage()==null?e.toString():e.getMessage());}
    }
    @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){
        return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(p->{
            if(p.error!=null){WaterBalance.reject(p.error);LogUtils.getLogger().error("[HJ Core] water.json rejected: {}. Keeping last valid policy.",p.error);}
            else if(p.policy==null){WaterBalance.missing();LogUtils.getLogger().info("[HJ Core] water.json absent; safety defaults active");}
            else{WaterBalance.accept(p.policy,p.source);LogUtils.getLogger().info("[HJ Core] water.json loaded from {} revision {}",p.source,WaterBalance.state().revision());}
        },game);
    }
}
