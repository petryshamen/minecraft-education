package org.humanjourney.core.juice;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import toughasnails.api.thirst.*;
import toughasnails.init.ModConfig;
/** Executes the real TAN handler on mocked player/stack dependencies; no world/player is constructed. */
class JuiceNativeEffectsTest {
 static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void init(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @AfterAll static void close(){loader.close();}
 private void verifyNative(boolean enabled)throws Exception{var previous=ModConfig.thirst;var effect=toughasnails.api.potion.TANEffects.THIRST;ModConfig.thirst=mock(toughasnails.config.ThirstConfig.class);ModConfig.thirst.enableThirst=enabled;toughasnails.api.potion.TANEffects.THIRST=net.minecraft.world.effect.MobEffects.HUNGER;try{var player=mock(ServerPlayer.class);var level=mock(ServerLevel.class);when(player.level()).thenReturn(level);var random=net.minecraft.world.level.Level.class.getDeclaredField("random");random.setAccessible(true);random.set(level,RandomSource.create(1));var drink=mock(ItemStack.class);when(drink.is(any(TagKey.class))).thenAnswer(i->{String tag=((TagKey<?>)i.getArgument(0)).location().getPath();return tag.equals("drinks")||tag.equals("thirst/13_thirst_drinks")||tag.equals("hydration/30_hydration_drinks")||tag.equals("poison_chance/100_poison_chance_drinks");});var thirst=mock(IThirst.class);try(var helper=mockStatic(ThirstHelper.class)){helper.when(()->ThirstHelper.getThirst(player)).thenReturn(thirst);NativeJuiceEffects.apply(player,drink);if(enabled){verify(thirst,times(1)).drink(13,.3F);verify(player,times(1)).addEffect(argThat(e->e.getEffect()==net.minecraft.world.effect.MobEffects.HUNGER&&e.getDuration()==600));}else{verifyNoInteractions(thirst);verify(player,never()).addEffect(any());}}}finally{ModConfig.thirst=previous;toughasnails.api.potion.TANEffects.THIRST=effect;}}
 @Test void bridgeUsesExactNativeThirstHydrationAndOtherEffect()throws Exception{verifyNative(true);}
 @Test void nativeThirstDisabledGuardIsPreserved()throws Exception{verifyNative(false);}
 @Test void bottleHealHookIsOnlyPostFinishAndDoesNotReplaceNativeConsumption()throws Exception{try(var in=getClass().getClassLoader().getResourceAsStream("org/humanjourney/core/juice/mixin/BottleJuiceMixin.class")){var c=new org.objectweb.asm.tree.ClassNode();new org.objectweb.asm.ClassReader(in).accept(c,0);var method=c.methods.stream().filter(m->m.name.equals("hjJuiceHeal")).findFirst().orElseThrow();long calls=java.util.stream.StreamSupport.stream(method.instructions.spliterator(),false).filter(i->i instanceof org.objectweb.asm.tree.MethodInsnNode m&&m.owner.equals("org/humanjourney/core/juice/JuiceDrinking")&&m.name.equals("bottle")).count();assertEquals(1,calls);var annotations=new java.util.ArrayList<org.objectweb.asm.tree.AnnotationNode>();if(method.visibleAnnotations!=null)annotations.addAll(method.visibleAnnotations);if(method.invisibleAnnotations!=null)annotations.addAll(method.invisibleAnnotations);var inject=annotations.stream().filter(a->a.desc.endsWith("/Inject;")).findFirst().orElseThrow();assertTrue(inject.values.contains(java.util.List.of("finishUsingItem")));var at=(org.objectweb.asm.tree.AnnotationNode)((java.util.List<?>)inject.values.get(inject.values.indexOf("at")+1)).getFirst();assertTrue(at.values.contains("RETURN"));}}
 @Test void juiceCommonAdaptersAreDedicatedServerSafe()throws Exception{for(String name:new String[]{"CustomJuiceBottleItem","JuiceBottles","JuiceCrafting","JuiceQuantityRecipe","JuiceBottleRecipe","JuiceBootstrap","JuiceCatalog","JuiceCommands","JuiceContainers","JuiceCraftGuard","JuiceDefinition","JuiceDrinking","JuiceRecipe","JuiceReloadListener","JuiceState","JuiceStore","NativeJuiceEffects"})try(var in=getClass().getClassLoader().getResourceAsStream("org/humanjourney/core/juice/"+name+".class")){assertNotNull(in);var c=new org.objectweb.asm.tree.ClassNode();new org.objectweb.asm.ClassReader(in).accept(c,0);for(var m:c.methods){assertFalse(m.desc.contains("net/minecraft/client/"));for(var i:m.instructions)if(i instanceof org.objectweb.asm.tree.MethodInsnNode call)assertFalse(call.owner.startsWith("net/minecraft/client/"));else if(i instanceof org.objectweb.asm.tree.FieldInsnNode f)assertFalse(f.owner.startsWith("net/minecraft/client/"));}}}

}
