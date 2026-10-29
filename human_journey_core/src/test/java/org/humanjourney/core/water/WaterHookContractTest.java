package org.humanjourney.core.water;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import static org.junit.jupiter.api.Assertions.*;
/** Check exact compiled 1.21.8/Forge/TAN targets without launching/executing a mod. */
class WaterHookContractTest {
    @Test void purifierFillingAndSoundTargetsMatchExactTan()throws Exception{
        String p="toughasnails/block/entity/WaterPurifierBlockEntity";
        method(p,"serverTick","(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ltoughasnails/block/entity/WaterPurifierBlockEntity;)V");
        method(p,"getTotalPurifyTime","()I");method(p,"getFilterDuration","(Lnet/minecraft/world/item/ItemStack;)I");
        for(String field:new String[]{"filterTimeRemaining","filterDuration","purifyProgress","purifyTotalTime"})assertTrue(read(p).fields.stream().anyMatch(f->f.name.equals(field)&&f.desc.equals("I")));
        method("toughasnails/item/EmptyCanteenItem","fillCanteen","(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/InteractionResult;");
        method("toughasnails/container/WaterPurifierMenu","canPurify","(Lnet/minecraft/world/item/ItemStack;)Z");
        method("toughasnails/thirst/ThirstHandler","onPlayerUseItem","(Lglitchcore/event/player/PlayerInteractEvent$UseItem;)V");
        assertTrue(read("toughasnails/item/DrinkItem").methods.stream().noneMatch(m->m.name.equals("onUseTick")),"exact TAN bottle hierarchy omits sound tick");
        method("toughasnails/item/FilledCanteenItem","onUseTick","(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V");
    }
    ClassNode read(String name)throws Exception{try(var in=getClass().getClassLoader().getResourceAsStream(name+".class")){assertNotNull(in,name);var c=new ClassNode();new ClassReader(in).accept(c,0);return c;}}
    MethodNode method(String type,String name,String descriptor)throws Exception{return read(type).methods.stream().filter(m->m.name.equals(name)&&m.desc.equals(descriptor)).findFirst().orElseThrow(()->new AssertionError(type+"."+name+descriptor));}
    @Test void furnaceTargetsMatchForgePatchedClasses()throws Exception{
        String type="net/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity";
        for(String name:new String[]{"canBurn","burn"})method(type,name,"(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/NonNullList;I)Z");
        method(type,"getTotalCookTime","(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)I");
        assertTrue(read(type).fields.stream().anyMatch(f->f.name.equals("cookingTimer")&&f.desc.equals("I")));
    }
    @Test void campfireTargetsAndRedirectExist()throws Exception{
        var c=read("net/minecraft/world/level/block/entity/CampfireBlockEntity");
        for(String name:new String[]{"items","cookingProgress","cookingTime"})assertTrue(c.fields.stream().anyMatch(f->f.name.equals(name)),name);
        var cook=method(c.name,"cookTick","(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/CampfireBlockEntity;Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;)V");
        int invokes=0;for(var insn:cook.instructions)if(insn instanceof MethodInsnNode call&&call.owner.equals("net/minecraft/world/item/ItemStack")&&call.name.equals("isEmpty")&&call.desc.equals("()Z"))invokes++;
        assertTrue(invokes>0,"water-skip redirect must have a target");
    }
    @Test void exactTanDrinkingAndCanteenHooksExist()throws Exception{
        method("toughasnails/thirst/ThirstHandler","onItemUseFinish","(Lglitchcore/event/entity/LivingEntityUseItemEvent$Finish;)V");
        method("toughasnails/network/DrinkInWorldPacket","handle","(Ltoughasnails/network/DrinkInWorldPacket;Lglitchcore/network/CustomPacket$Context;)V");
        assertTrue(read("toughasnails/network/DrinkInWorldPacket").fields.stream().anyMatch(f->f.name.equals("pos")&&f.desc.equals("Lnet/minecraft/core/BlockPos;")));
        method("toughasnails/item/FilledCanteenItem","inventoryTick","(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EquipmentSlot;)V");
    }
    @Test void craftingGuardTargetExists()throws Exception{method("net/minecraft/world/inventory/CraftingMenu","slotChangedCraftingGrid","(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;Lnet/minecraft/world/item/crafting/RecipeHolder;)V");}
}
