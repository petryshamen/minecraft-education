package org.humanjourney.core.npc.economy;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.entity.living.LivingEvent;
public final class EconomyBootstrap {
 public static void register(FMLJavaModLoadingContext context){if(!org.humanjourney.core.npc.NpcDependency.problem().isEmpty())return;LivingEvent.LivingTickEvent.BUS.addListener((java.util.function.Predicate<LivingEvent.LivingTickEvent>)event->{if(!event.getEntity().level().isClientSide()&&event.getEntity() instanceof net.minecraft.world.entity.PathfinderMob mob)EconomyMovement.ensure(mob);return false;});if(Boolean.getBoolean("humanjourney.waterSmoke"))EconomySmoke.register(context.getModBusGroup());}
 private EconomyBootstrap(){}
}
