package org.humanjourney.core.npc;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
public final class NpcBootstrap {
    public static void register(FMLJavaModLoadingContext context){
        AddReloadListenerEvent.BUS.addListener(event->event.addListener(new NpcReloadListener()));
        AddReloadListenerEvent.BUS.addListener(event->event.addListener(new org.humanjourney.core.npc.context.NpcContextReloadListener()));
        RegisterCommandsEvent.BUS.addListener(NpcCommands::register);
        ServerStoppedEvent.BUS.addListener(event->{NpcTemplates.reset();org.humanjourney.core.npc.context.NpcContextStore.reset();});
        if(Boolean.getBoolean("humanjourney.waterSmoke")){org.humanjourney.core.npc.easynpc.NpcSmoke.register(context.getModBusGroup());org.humanjourney.core.npc.context.NpcContextSmoke.register(context.getModBusGroup());}
    }
    private NpcBootstrap(){}
}
