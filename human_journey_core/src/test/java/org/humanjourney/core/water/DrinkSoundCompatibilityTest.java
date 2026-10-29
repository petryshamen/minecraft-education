package org.humanjourney.core.water;
import org.humanjourney.core.compat.TanDrinkSounds;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import static org.junit.jupiter.api.Assertions.*;
class DrinkSoundCompatibilityTest {
    ClassNode read(String name)throws Exception{try(var in=getClass().getClassLoader().getResourceAsStream(name+".class")){assertNotNull(in);var c=new ClassNode();new ClassReader(in).accept(c,0);return c;}}
    @Test void cadenceHasNoFinalOrClientDuplicates(){
        int sounds=0;for(int remaining=32;remaining>=0;remaining--){
            if(TanDrinkSounds.shouldPlay(false,false,false,remaining))sounds++;
            assertFalse(TanDrinkSounds.shouldPlay(true,false,false,remaining));
            assertFalse(TanDrinkSounds.shouldPlay(false,true,false,remaining));
            assertFalse(TanDrinkSounds.shouldPlay(false,false,true,remaining));
        }assertEquals(6,sounds);assertFalse(TanDrinkSounds.shouldPlay(false,false,false,0));
    }
    @Test void broadcastIncludesDrinkerWhereOldPlayerMethodExcludedIt()throws Exception{
        assertExcludedArgument("net/minecraft/world/entity/player/Player","playSound",Opcodes.ALOAD);
        assertExcludedArgument("org/humanjourney/core/compat/TanDrinkSounds","tick",Opcodes.ACONST_NULL);
    }
    private void assertExcludedArgument(String owner,String name,int expected)throws Exception{
        var c=read(owner);var method=c.methods.stream().filter(m->m.name.equals(name)&&(!name.equals("playSound")||m.desc.equals("(Lnet/minecraft/sounds/SoundEvent;FF)V"))).findFirst().orElseThrow();
        var frames=new Analyzer<SourceValue>(new SourceInterpreter()).analyze(owner,method);boolean found=false;
        for(int i=0;i<method.instructions.size();i++)if(method.instructions.get(i) instanceof MethodInsnNode call&&call.name.equals("playSound")&&call.owner.equals("net/minecraft/world/level/Level")){
            var args=Type.getArgumentTypes(call.desc);assertEquals("net.minecraft.world.entity.Entity",args[0].getClassName());
            var frame=frames[i];var excluded=frame.getStack(frame.getStackSize()-args.length);
            assertTrue(excluded.insns.stream().anyMatch(insn->insn.getOpcode()==expected&&(expected!=Opcodes.ALOAD||((VarInsnNode)insn).var==0)),"wrong excluded listener argument");found=true;
        }assertTrue(found,"Level broadcast must exist");
    }
    @Test void patchTargetsOnlyTanBottleHierarchyAndOnlyUseTicks()throws Exception{
        var c=read("org/humanjourney/core/mixin/DrinkSoundMixin");assertEquals(1,c.methods.stream().filter(m->!m.name.equals("<init>")).count());assertTrue(c.methods.stream().anyMatch(m->m.name.equals("onUseTick")));
        assertEquals("net/minecraft/world/item/Item",read("toughasnails/item/DrinkItem").superName);
        assertEquals("toughasnails/item/EmptyCanteenItem",read("toughasnails/item/FilledCanteenItem").superName);
    }
}
