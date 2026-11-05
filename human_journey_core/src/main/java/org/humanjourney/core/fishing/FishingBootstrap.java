package org.humanjourney.core.fishing;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
public final class FishingBootstrap {
    public static void register(){
        AddReloadListenerEvent.BUS.addListener(event->event.addListener(new FishingReloadListener()));
        ServerStoppedEvent.BUS.addListener(event->FishingBalance.reset());
        if(Boolean.getBoolean("humanjourney.waterSmoke")){
            try{Class.forName("net.minecraft.world.entity.projectile.FishingHook",false,FishingBootstrap.class.getClassLoader());}
            catch(ClassNotFoundException e){throw new IllegalStateException("FishingHook smoke target missing",e);}
            com.mojang.logging.LogUtils.getLogger().info("[HJ Core] FISHING_MIXIN_SMOKE_OK: exact initial wait redirect applied by ModLauncher; no bobber/world created");
        }
    }
    private FishingBootstrap(){}
}
