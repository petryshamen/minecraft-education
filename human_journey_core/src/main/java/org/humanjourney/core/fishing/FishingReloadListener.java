package org.humanjourney.core.fishing;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import java.util.concurrent.*;
public final class FishingReloadListener implements PreparableReloadListener {
    private record Prepared(FishingPolicy policy,String source,String error){}
    private Prepared prepare(ResourceManager manager){
        String source="human_journey:core/fishing.json";
        try{var resource=manager.getResource(ResourceLocation.parse(source));if(resource.isEmpty())return new Prepared(null,null,null);source=resource.get().sourcePackId()+" / "+source;
            try(var reader=resource.get().openAsReader()){StringBuilder json=new StringBuilder();char[] buffer=new char[1024];int n;while((n=reader.read(buffer))!=-1){json.append(buffer,0,n);if(json.length()>16384)throw new IllegalArgumentException("fishing policy exceeds 16384 characters");}return new Prepared(FishingPolicy.parse(JsonParser.parseString(json.toString())),source,null);}
        }catch(Exception e){return new Prepared(null,source,source+": "+(e.getMessage()==null?e.toString():e.getMessage()));}
    }
    @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager manager,Executor background,Executor game){return CompletableFuture.supplyAsync(()->prepare(manager),background).thenCompose(barrier::wait).thenAcceptAsync(p->{
        if(p.error()!=null){FishingBalance.reject(p.error());LogUtils.getLogger().error("[HJ Core] fishing.json REJECTED: {}. Keeping last valid policy.",p.error());}
        else if(p.policy()==null){FishingBalance.missing();LogUtils.getLogger().info("[HJ Core] fishing.json absent; vanilla timing active");}
        else{FishingBalance.accept(p.policy(),p.source());LogUtils.getLogger().info("[HJ Core] fishing.json loaded from {} revision {}",p.source(),FishingBalance.state().revision());}
    },game);}
}
