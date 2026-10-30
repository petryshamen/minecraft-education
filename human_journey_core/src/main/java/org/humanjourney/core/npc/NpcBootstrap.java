package org.humanjourney.core.npc;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
public final class NpcBootstrap {
    public static void register(FMLJavaModLoadingContext context){
        AddReloadListenerEvent.BUS.addListener(event->event.addListener(new NpcReloadListener()));
        RegisterCommandsEvent.BUS.addListener(NpcCommands::register);
        ServerStoppedEvent.BUS.addListener(event->NpcTemplates.reset());
        if(Boolean.getBoolean("humanjourney.waterSmoke"))org.humanjourney.core.npc.easynpc.NpcSmoke.register(context.getModBusGroup());
    }
    private NpcBootstrap(){}
}
