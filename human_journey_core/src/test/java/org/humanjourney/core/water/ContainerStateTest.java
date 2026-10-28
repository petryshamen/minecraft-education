package org.humanjourney.core.water;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import net.minecraftforge.fml.ModLoader;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;
class ContainerStateTest {
    private static MockedStatic<ModLoader> loader;
    @BeforeAll static void init(){
        // Only the absent launcher is stubbed; ItemStack, component maps, codecs and registries are real.
        loader=mockStatic(ModLoader.class);var emptyLoader=mock(ModLoader.class);loader.when(ModLoader::get).thenReturn(emptyLoader);
        SharedConstants.tryDetectVersion();Bootstrap.bootStrap();
    }
    @AfterAll static void close(){if(loader!=null)loader.close();}
    @Test void realComponentsSurviveEveryCapacityAndPartialFill(){
        var lookup=VanillaRegistries.createLookup();var ops=RegistryOps.create(JsonOps.INSTANCE,lookup);
        var enchant=lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
        for(int capacity:new int[]{5,6,7,10,15,25})for(int used:new int[]{0,capacity/2,capacity-1}){
            ItemStack input=new ItemStack(Items.WOODEN_PICKAXE);input.set(DataComponents.MAX_DAMAGE,capacity);input.setDamageValue(used);
            input.set(DataComponents.CUSTOM_NAME,Component.literal("Student canteen"));input.enchant(enchant,2);
            CompoundTag extra=new CompoundTag();extra.putString("owner","student");extra.putInt("marker",42);input.set(DataComponents.CUSTOM_DATA,CustomData.of(extra));
            ItemStack result=WaterContainers.preserve(input,Items.STONE_PICKAXE);
            assertEquals(input.getComponentsPatch(),result.getComponentsPatch());assertEquals(used,result.getDamageValue());assertEquals(capacity,result.getMaxDamage());assertEquals(1,result.getCount());assertEquals(used,input.getDamageValue());
            // Registry-bound serialization is tested with actual registered TAN items in the Forge datagen smoke run.
            assertEquals(capacity-used,result.getMaxDamage()-result.getDamageValue());
        }
    }
    @Test void bottlesMustBePlainWaterNotOtherPotions(){assertEquals(WaterPolicy.Quality.NORMAL,WaterContainers.kind(PotionContents.createItemStack(Items.POTION,Potions.WATER)).quality());assertNull(WaterContainers.kind(PotionContents.createItemStack(Items.POTION,Potions.HEALING)));assertNull(WaterContainers.kind(new ItemStack(Items.GLASS_BOTTLE)));}
}
