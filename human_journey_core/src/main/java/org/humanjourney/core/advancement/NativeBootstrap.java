package org.humanjourney.core.advancement;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.*;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraft.network.chat.Component;
import static net.minecraft.commands.Commands.literal;
public final class NativeBootstrap {
 public static void register(FMLJavaModLoadingContext context){NativeNetwork.register();AddReloadListenerEvent.BUS.addListener(e->e.addListener(new NativeReloadListener()));TickEvent.ServerTickEvent.Post.BUS.addListener(e->NativeServer.tick(e.getServer()));ServerStoppedEvent.BUS.addListener(e->{NativeServer.clear();NativeStore.reset();});RegisterCommandsEvent.BUS.addListener(e->e.getDispatcher().register(literal("hj").then(literal("advancements").requires(s->s.hasPermission(2)).then(literal("status").executes(c->{var s=NativeStore.state();c.getSource().sendSuccess(()->Component.literal("Native advancement presentation: "+s.model().tabs().size()+" tabs; "+s.source()+"; pinned API supported="+NativeMixinPlugin.supported()+"; "+(s.error().isEmpty()?"no reload errors":s.error())+"; old dashboard UI retired"),false);return 1;})))));if(FMLLoader.getDist()==Dist.CLIENT)try{Class.forName("org.humanjourney.core.advancement.client.NativeClient").getMethod("register",FMLJavaModLoadingContext.class).invoke(null,context);}catch(ReflectiveOperationException e){throw new IllegalStateException("Native advancement client registration failed",e);}if(Boolean.getBoolean("humanjourney.waterSmoke"))NativeSmoke.register(context.getModBusGroup());}
 private NativeBootstrap(){}
}
