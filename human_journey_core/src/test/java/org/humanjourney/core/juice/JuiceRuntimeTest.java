package org.humanjourney.core.juice;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import java.util.*;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.world.item.component.CustomData;
import org.humanjourney.core.water.*;
class JuiceRuntimeTest {
 static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void init(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @AfterAll static void close(){loader.close();}
 MockedStatic<WaterContainers> water;
 JuiceDefinition def;
 @BeforeEach void setup(){def=new JuiceDefinition("synthetic","minecraft:glass_bottle",new JuiceDefinition.Ingredient("minecraft:sand",null,Set.of("minecraft:sand")),"Synthetic Juice Canteen",1);JuiceStore.accept(JuiceCatalog.of(List.of(def)),"unit fixture");water=mockStatic(WaterContainers.class,CALLS_REAL_METHODS);water.when(()->WaterContainers.kind(any(ItemStack.class))).thenAnswer(i->{ItemStack s=i.getArgument(0);if(s==null)return null;if(JuiceState.marked(s))return null;if(s.is(Items.WOODEN_PICKAXE)||s.is(Items.STONE_PICKAXE)){String q=s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("fixture_quality").orElse("PURIFIED");return new WaterContainers.Kind(SaltWater.marked(s)?WaterPolicy.Quality.SALT:WaterPolicy.Quality.valueOf(q),s.is(Items.WOODEN_PICKAXE)?"leather":"iron");}return i.callRealMethod();});water.when(()->WaterContainers.emptied(any(ItemStack.class))).thenAnswer(i->{ItemStack s=i.getArgument(0);var e=s.transmuteCopy(s.is(Items.WOODEN_PICKAXE)?Items.LEATHER:Items.IRON_INGOT,1);e.remove(DataComponents.DAMAGE);return e;});}
 @AfterEach void reset(){water.close();JuiceStore.reset();}
 ItemStack can(Item item){var s=new ItemStack(item);s.set(DataComponents.MAX_DAMAGE,5);s.setDamageValue(0);return s;}
 CraftingInput grid(ItemStack can,Item material){return CraftingInput.of(2,2,List.of(can,new ItemStack(material),ItemStack.EMPTY,ItemStack.EMPTY));}
 @Test void fullLeatherFixtureCraftsSameTier(){var c=can(Items.WOODEN_PICKAXE);var result=JuiceRecipe.result(grid(c,Items.SAND));assertTrue(result.is(c.getItem()));assertEquals("synthetic",JuiceState.id(result));assertEquals(0,result.getDamageValue());assertFalse(JuiceState.marked(c));}
 @Test void anotherTierIsPreserved(){var c=can(Items.STONE_PICKAXE);assertTrue(JuiceRecipe.result(grid(c,Items.SAND)).is(c.getItem()));}
 @Test void partialRejected(){var c=can(Items.WOODEN_PICKAXE);c.setDamageValue(1);assertTrue(JuiceRecipe.result(grid(c,Items.SAND)).isEmpty());}
 @Test void normalRejected(){var c=can(Items.WOODEN_PICKAXE);var d=new CompoundTag();d.putString("fixture_quality","NORMAL");c.set(DataComponents.CUSTOM_DATA,CustomData.of(d));assertTrue(JuiceRecipe.result(grid(c,Items.SAND)).isEmpty());}
 @Test void dirtyRejected(){var c=can(Items.WOODEN_PICKAXE);var d=new CompoundTag();d.putString("fixture_quality","DIRTY");c.set(DataComponents.CUSTOM_DATA,CustomData.of(d));assertTrue(JuiceRecipe.result(grid(c,Items.SAND)).isEmpty());}
 @Test void saltRejected(){var c=can(Items.WOODEN_PICKAXE);SaltWater.set(c,true);assertTrue(JuiceRecipe.result(grid(c,Items.SAND)).isEmpty());}
 @Test void unrelatedIngredientRejected(){assertTrue(JuiceRecipe.result(grid(can(Items.WOODEN_PICKAXE),Items.BONE)).isEmpty());}
 @Test void extraIngredientAndJuiceRemixingRejected(){var c=can(Items.WOODEN_PICKAXE);assertTrue(JuiceRecipe.result(CraftingInput.of(2,2,List.of(c,new ItemStack(Items.SAND),new ItemStack(Items.BONE),ItemStack.EMPTY))).isEmpty());JuiceState.set(c,def);assertTrue(JuiceRecipe.result(grid(c,Items.SAND)).isEmpty());}
 @Test void oneSipConsumesExactlyOneUseAndRetainsIdentity(){var c=JuiceContainers.create(can(Items.WOODEN_PICKAXE),def);assertSame(c,JuiceContainers.consume(c,false));assertEquals(1,c.getDamageValue());assertEquals(def.id(),JuiceState.id(c));}
 @Test void finalSipEmptiesCorrectTierAndClearsOnlyJuice(){var c=can(Items.STONE_PICKAXE);var data=new CompoundTag();data.putString("owner","fixture");c.set(DataComponents.CUSTOM_DATA,CustomData.of(data));JuiceState.set(c,def);c.setDamageValue(4);var empty=JuiceContainers.consume(c,false);assertTrue(empty.is(Items.IRON_INGOT));assertFalse(JuiceState.marked(empty));assertEquals("fixture",empty.get(DataComponents.CUSTOM_DATA).copyTag().getString("owner").orElseThrow());}
 @Test void displayReloadRefreshesWithoutChangingIdentityOrUses(){var c=JuiceContainers.create(can(Items.WOODEN_PICKAXE),def);c.setDamageValue(2);var updated=new JuiceDefinition(def.id(),def.bottleItem(),def.ingredient(),"Updated Fixture Name",2);JuiceStore.accept(JuiceCatalog.of(List.of(updated)),"reload");JuiceState.refreshLabel(c);assertEquals("Updated Fixture Name",JuiceState.display(c));assertEquals(2,c.getDamageValue());assertEquals(def.id(),JuiceState.id(c));}
 @Test void creativeFollowsNativeNoDepletion(){var c=JuiceContainers.create(can(Items.WOODEN_PICKAXE),def);assertSame(c,JuiceContainers.consume(c,true));assertEquals(0,c.getDamageValue());}
 @Test void markerAndRemainingUsesSurviveNativeSaveLoad(){var c=JuiceContainers.create(can(Items.WOODEN_PICKAXE),def);c.setDamageValue(2);var lookup=net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);var ops=net.minecraft.resources.RegistryOps.create(NbtOps.INSTANCE,lookup);var restored=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,c).getOrThrow()).getOrThrow();assertEquals(2,restored.getDamageValue());assertEquals(def.id(),JuiceState.id(restored));assertEquals(def.canteenDisplayName(),JuiceState.display(restored));assertTrue(ItemStack.isSameItemSameComponents(c,restored));}
 @Test void unknownAndMalformedStateAreSafe(){var c=can(Items.WOODEN_PICKAXE);var removed=new JuiceDefinition("removed",def.bottleItem(),def.ingredient(),"Removed",1);JuiceState.set(c,removed);assertNull(JuiceContainers.definition(c));assertNull(WaterContainers.kind(c));var data=new CompoundTag();data.putString(JuiceState.KEY,"malformed");c.set(DataComponents.CUSTOM_DATA,CustomData.of(data));assertTrue(JuiceState.marked(c));assertNull(JuiceContainers.definition(c));assertEquals("Unresolved Juice Canteen",JuiceState.display(c));}
 @Test void healthClampSupportsHalfHeartAndNeverOverheals(){assertEquals(1,JuiceDrinking.cappedHeal(10,20,1));assertEquals(.5F,JuiceDrinking.cappedHeal(19.5F,20,1));assertEquals(0,JuiceDrinking.cappedHeal(20,20,1));assertEquals(10,JuiceDrinking.cappedHeal(10,20,1e300));}
 ServerPlayer player(){var p=mock(ServerPlayer.class);when(p.getAbilities()).thenReturn(new Abilities());when(p.getHealth()).thenReturn(10F);when(p.getMaxHealth()).thenReturn(20F);return p;}
 @Test void successfulCanteenCompletionCallsBottleEffectsAndHealingOnce(){var c=JuiceContainers.create(can(Items.WOODEN_PICKAXE),def);var p=player();var level=mock(ServerLevel.class);try(var bridge=mockStatic(NativeJuiceEffects.class)){assertSame(c,JuiceDrinking.canteen(c,level,p));bridge.verify(()->NativeJuiceEffects.apply(eq(p),argThat(s->s.is(Items.GLASS_BOTTLE))),times(1));verify(p,times(1)).heal(1F);assertEquals(1,c.getDamageValue());}}
 @Test void unknownJuiceDoesNotDrinkHealOrConsume(){var c=can(Items.WOODEN_PICKAXE);JuiceState.set(c,new JuiceDefinition("removed",def.bottleItem(),def.ingredient(),"Removed",1));var p=player();try(var bridge=mockStatic(NativeJuiceEffects.class)){assertSame(c,JuiceDrinking.canteen(c,mock(ServerLevel.class),p));bridge.verifyNoInteractions();verify(p,never()).heal(anyFloat());assertEquals(0,c.getDamageValue());}}
 @Test void configuredBottleHealsOnceAndUnrelatedDrinkDoesNot(){var p=player();var level=mock(ServerLevel.class);JuiceDrinking.bottle(Items.GLASS_BOTTLE,level,p);verify(p,times(1)).heal(1F);JuiceDrinking.bottle(Items.POTION,level,p);verify(p,times(1)).heal(anyFloat());}
 @Test void clientSideBottleDoesNotHeal(){var p=player();var level=mock(ServerLevel.class);when(level.isClientSide()).thenReturn(true);JuiceDrinking.bottle(Items.GLASS_BOTTLE,level,p);verify(p,never()).heal(anyFloat());}
 @Test void genericRecipeDoesNotDuplicateCanteenRemainder(){var input=grid(can(Items.WOODEN_PICKAXE),Items.SAND);assertTrue(new JuiceRecipe(CraftingBookCategory.MISC).getRemainingItems(input).get(0).isEmpty());}
 @Test void customCanteenUsesConfiguredSourceAndOneHealingBonus(){var custom=new JuiceDefinition(2,"custom",new JuiceDefinition.Bottle(true,null,"Fixture Bottle","minecraft:potion"),def.ingredient(),2,8,"Fixture Custom Canteen",1);JuiceStore.accept(JuiceCatalog.of(List.of(custom)),"custom");var c=JuiceContainers.create(can(Items.WOODEN_PICKAXE),custom);var p=player();try(var bridge=mockStatic(NativeJuiceEffects.class)){JuiceDrinking.canteen(c,mock(ServerLevel.class),p);bridge.verify(()->NativeJuiceEffects.apply(eq(p),argThat(s->s.is(Items.POTION))),times(1));verify(p,times(1)).heal(1F);assertEquals(1,c.getDamageValue());assertEquals(custom.id(),JuiceState.id(c));}}
 @Test void customFinalSipReturnsSameTierEmpty(){var custom=new JuiceDefinition(2,"custom",new JuiceDefinition.Bottle(true,null,"Fixture Bottle","minecraft:potion"),def.ingredient(),2,8,"Fixture Custom Canteen",1);var c=JuiceContainers.create(can(Items.STONE_PICKAXE),custom);c.setDamageValue(4);assertTrue(JuiceContainers.consume(c,false).is(Items.IRON_INGOT));}

}
