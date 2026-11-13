package org.humanjourney.core.juice;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.*;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.registries.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.network.chat.Component;
public final class JuiceBootstrap {
 private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,"human_journey_core");
 public static final RegistryObject<RecipeSerializer<JuiceRecipe>> RECIPE=SERIALIZERS.register("juice_canteen",()->new CustomRecipe.Serializer<>(JuiceRecipe::new));
 private static final DeferredRegister<net.minecraft.world.item.Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,"human_journey_core");
 public static final RegistryObject<CustomJuiceBottleItem> BOTTLE=ITEMS.register("juice_bottle",()->new CustomJuiceBottleItem(new net.minecraft.world.item.Item.Properties().setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM,net.minecraft.resources.ResourceLocation.parse("human_journey_core:juice_bottle"))).stacksTo(16)));
 public static final RegistryObject<RecipeSerializer<JuiceBottleRecipe>> BOTTLE_RECIPE=SERIALIZERS.register("juice_bottle",()->new CustomRecipe.Serializer<>(JuiceBottleRecipe::new));
 public static void register(FMLJavaModLoadingContext context){SERIALIZERS.register(context.getModBusGroup());ITEMS.register(context.getModBusGroup());AddReloadListenerEvent.BUS.addListener(e->e.addListener(new JuiceReloadListener(e.getRegistries())));RegisterCommandsEvent.BUS.addListener(JuiceCommands::register);ServerStoppedEvent.BUS.addListener(e->JuiceStore.reset());net.minecraftforge.event.entity.player.ItemTooltipEvent.BUS.addListener(e->{if(JuiceState.marked(e.getItemStack()))e.getToolTip().add(Component.literal(JuiceState.display(e.getItemStack())).withStyle(net.minecraft.ChatFormatting.GOLD));});
  if(Boolean.getBoolean("humanjourney.waterSmoke")){for(String target:new String[]{"toughasnails.item.DrinkItem","toughasnails.item.FilledCanteenItem","net.minecraft.world.item.ItemStack","net.minecraft.world.inventory.CraftingMenu","net.minecraft.world.inventory.ResultSlot"})try{Class.forName(target,false,JuiceBootstrap.class.getClassLoader());}catch(ClassNotFoundException ex){throw new IllegalStateException("Juice hook missing "+target,ex);}JuiceSmoke.register(context.getModBusGroup());}
 }
 private JuiceBootstrap(){}
}
