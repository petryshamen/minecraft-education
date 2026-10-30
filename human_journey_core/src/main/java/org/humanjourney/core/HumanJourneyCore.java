package org.humanjourney.core;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.humanjourney.core.config.TemperatureConfig;
import org.humanjourney.core.integration.toughasnails.TemperatureIntegration;
import org.humanjourney.core.command.CoreCommands;
import org.humanjourney.core.data.TemperatureBalance;
import org.humanjourney.core.data.TemperatureReloadListener;
import java.util.function.Predicate;
import org.humanjourney.core.water.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Mod(HumanJourneyCore.MOD_ID)
public final class HumanJourneyCore {
    public static final String MOD_ID = "human_journey_core";

    public HumanJourneyCore(FMLJavaModLoadingContext context) {
        if (Boolean.getBoolean("humanjourney.waterSmoke")) {
            // Datagen smoke run: force actual ModLauncher/Mixin validation without starting a server/world.
            for (String target : new String[]{"net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity", "net.minecraft.world.level.block.entity.CampfireBlockEntity", "net.minecraft.world.inventory.CraftingMenu", "toughasnails.thirst.ThirstHandler", "toughasnails.network.DrinkInWorldPacket", "toughasnails.item.FilledCanteenItem", "toughasnails.item.EmptyCanteenItem", "toughasnails.item.DrinkItem", "toughasnails.block.entity.WaterPurifierBlockEntity", "toughasnails.container.WaterPurifierMenu"}) {
                try { Class.forName(target, false, HumanJourneyCore.class.getClassLoader()); }
                catch (ClassNotFoundException error) { throw new IllegalStateException("Water hook smoke target missing: " + target, error); }
            }
            if (!CampfireAccess.class.isAssignableFrom(net.minecraft.world.level.block.entity.CampfireBlockEntity.class))
                throw new IllegalStateException("Water mixins were not applied in smoke run");
            WaterSmoke.register(context.getModBusGroup());
            com.mojang.logging.LogUtils.getLogger().info("[HJ Core] WATER_MIXIN_SMOKE_OK: all ten water targets loaded under ModLauncher");
        }
        org.humanjourney.core.npc.NpcBootstrap.register(context);
        context.registerConfig(ModConfig.Type.SERVER, TemperatureConfig.SPEC,
                "human_journey_core-server.toml");
        TickEvent.PlayerTickEvent.Pre.BUS.addListener(TemperatureIntegration::beforePlayerTick);
        TickEvent.ServerTickEvent.Post.BUS.addListener(TemperatureIntegration::afterServerTick);
        AddReloadListenerEvent.BUS.addListener(event -> event.addListener(new TemperatureReloadListener()));
        AddReloadListenerEvent.BUS.addListener(event -> event.addListener(new WaterReloadListener()));
        PlayerInteractEvent.RightClickItem.BUS.addListener((Predicate<PlayerInteractEvent.RightClickItem>)WaterTreatment::rightClickItem);
        PlayerInteractEvent.RightClickBlock.BUS.addListener((Predicate<PlayerInteractEvent.RightClickBlock>)WaterTreatment::rightClickBlock);
        
        net.minecraftforge.event.entity.player.ItemTooltipEvent.BUS.addListener(event->{
            var kind=WaterContainers.kind(event.getItemStack());
            if(kind!=null&&kind.quality()==WaterPolicy.Quality.SALT)
                event.getToolTip().add(1,net.minecraft.network.chat.Component.translatable("tooltip.human_journey_core.salt_water").withStyle(net.minecraft.ChatFormatting.AQUA));
        });
        RegisterCommandsEvent.BUS.addListener(CoreCommands::register);
        ServerStoppedEvent.BUS.addListener(event -> {
            TemperatureBalance.STORE.reset();
            TemperatureIntegration.reset();
            WaterBalance.reset();
            WaterDrinking.reset();
            NaturalWater.reset();
        });
        // Forge 58 uses predicate cancellation, not legacy event.setCanceled().
        LivingAttackEvent.BUS.addListener(
                (Predicate<LivingAttackEvent>) TemperatureIntegration::cancelHyperthermia);
    }
}
