package org.humanjourney.core.npc;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import static org.junit.jupiter.api.Assertions.*;
class NpcAdapterContractTest {
    ClassNode read(String name)throws Exception{try(var in=getClass().getClassLoader().getResourceAsStream(name+".class")){assertNotNull(in,name);var c=new ClassNode();new ClassReader(in).accept(c,0);return c;}}
    void method(String owner,String name,String descriptor)throws Exception{assertTrue(read(owner).methods.stream().anyMatch(m->m.name.equals(name)&&m.desc.equals(descriptor)),owner+"."+name);}
    @Test void exactEasyNpcMethodsMatchBinary()throws Exception{
        String p="de/markusbordihn/easynpc/";
        method(p+"data/skin/SkinDataEntry","createRemoteSkin","(Ljava/lang/String;)L"+p+"data/skin/SkinDataEntry;");
        method(p+"data/dialog/DialogDataSet","addDialog","(L"+p+"data/dialog/DialogDataEntry;)Z");
        method(p+"entity/easynpc/data/TradingDataCapable","setTradingOffers","(Lnet/minecraft/world/item/trading/MerchantOffers;)V");
        method(p+"entity/easynpc/data/PresetDataCapable","setPresetUUID","(Ljava/util/UUID;)V");
        method(p+"data/action/ActionDataEntry","getId","()Ljava/util/UUID;");
    }
    @Test void adapterUsesNativeFactoryAndSettersWithoutPresetImport()throws Exception{
        var c=read("org/humanjourney/core/npc/easynpc/EasyNpc652Adapter");java.util.Set<String> calls=new java.util.HashSet<>();
        for(var m:c.methods)for(var insn:m.instructions)if(insn instanceof MethodInsnNode call){calls.add(call.name);assertNotEquals("importPresetData",call.name);}
        assertTrue(calls.containsAll(java.util.Set.of("create","setPresetUUID","setTradingOffers","setDialogDataSet","setSkinDataEntry","setPersistenceRequired","addFreshEntity")));
    }
}
