package org.humanjourney.core.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModList;
import org.humanjourney.core.HumanJourneyCore;
import org.humanjourney.core.config.TemperaturePolicyStore;
import org.humanjourney.core.config.TemperatureRules;
import org.humanjourney.core.data.TemperatureBalance;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.thirst.ThirstHelper;
import java.util.Locale;
import org.humanjourney.core.water.*;

public final class CoreCommands {
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("hjcore").requires(source -> source.hasPermission(2))
            .then(Commands.literal("status")
                .executes(context -> show(context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> show(context.getSource(), EntityArgument.getPlayer(context, "player")))))
            .then(Commands.literal("water").executes(context->{
                var player=context.getSource().getPlayerOrException();
                context.getSource().sendSuccess(()->Component.literal(WaterContainers.describe(player.getMainHandItem())+" | name="+player.getMainHandItem().getHoverName().getString()),false);
                return 1;
            })));
    }
    private static int show(CommandSourceStack source, ServerPlayer player) throws CommandSyntaxException {
        String version = ModList.get().getModContainerById(HumanJourneyCore.MOD_ID).orElseThrow().getModInfo().getVersion().toString();
        var temperature = TemperatureHelper.getTemperatureData(player);
        var thirst = ThirstHelper.getThirst(player);
        TemperaturePolicyStore.Snapshot policy = TemperatureBalance.STORE.snapshot();
        TemperatureRules rules = TemperatureBalance.current();
        String text = String.join("\n",
            "Human Journey Core " + version + " | player=" + player.getScoreboardName(),
            "dimension=" + player.level().dimension().location(),
            "TAN temperature=" + temperature.getLevel() + " | target=" + temperature.getTargetLevel() + " | enabled=" + TemperatureHelper.isTemperatureEnabled(),
            String.format(Locale.ROOT, "thirst=%d/20 | hydration=%.4f | exhaustion=%.4f | enabled=%s", thirst.getThirst(), thirst.getHydration(), thirst.getExhaustion(), ThirstHelper.isThirstEnabled()),
            "hyperthermia exposure=" + temperature.getHyperthermiaTicks() + "/" + TemperatureHelper.getTicksRequiredForHyperthermia() + " ticks",
            "Climate Clemency=" + player.hasEffect(TANEffects.CLIMATE_CLEMENCY),
            "policy source=" + policy.source() + " | revision=" + policy.revision() + " | last reload=" + policy.lastReload(),
            String.format(Locale.ROOT, "extra exhaustion/sec: WARM=%.4f HOT=%.4f Nether HOT=%.4f", rules.warmExhaustion(), rules.hotExhaustion(), rules.netherHotExhaustion()),
            "heat damage: Overworld=" + rules.overworld() + " | Nether=" + rules.nether(),
            "remove Nether Clemency=" + rules.removeNetherClemency() + " | water relief=" + rules.waterRelief() + " | cooling interval=" + rules.waterCoolingInterval() + " ticks");
        var water=WaterBalance.state();var w=water.policy();
        text += "\nwater policy="+water.source()+" | last successful revision="+water.revision()+" | reload="+water.lastReload()
            +"\nwater: normal="+w.normal().chance()+" dirty="+w.dirty().chance()+" | illness="+(w.dirty().illness()?w.dirty().effect()+" "+w.dirty().illnessTicks()+"t amp="+w.dirty().amplifier():"off")
            +" | filter="+w.filtering()+" furnace="+w.furnace()+" campfire="+w.campfire();
        String message=text;
        source.sendSuccess(() -> Component.literal(message), false);
        return 1;
    }
    private CoreCommands() {}
}
