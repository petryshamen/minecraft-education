package org.humanjourney.core.npc.context;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
class NpcSeasonTest {
    @Test void absentAndUnsupportedSeasonDependencyNeverCallsAdapter(){
        assertEquals("none",NpcSeasons.resolve(false,"",()->{fail("absent API invoked");return "winter";}).season());assertEquals("none",NpcSeasons.resolve(true,"99",()->{fail("unsupported API invoked");return "winter";}).season());
    }
    @Test void seasonStateErrorsAndNullFallBackGracefully(){assertEquals("none",NpcSeasons.resolve(true,"21.8.0.0",()->null).season());assertEquals("none",NpcSeasons.resolve(true,"21.8.0.0",()->{throw new NoClassDefFoundError("optional");}).season());assertEquals("none",NpcSeasons.resolve(true,"21.8.0.0",()->{throw new IllegalStateException("unloaded");}).season());for(String season:new String[]{"spring","summer","autumn","winter"})assertEquals(season,NpcSeasons.resolve(true,"21.8.0.0",()->season).season());}
    ClassNode read(String name)throws Exception{try(var in=getClass().getClassLoader().getResourceAsStream(name+".class")){assertNotNull(in);var c=new ClassNode();new ClassReader(in).accept(c,0);return c;}}
    @Test void exactInstalledSeasonApiDescriptorsMatchPublicArtifact()throws Exception{
        assertTrue(read("sereneseasons/api/season/SeasonHelper").methods.stream().anyMatch(m->m.name.equals("getSeasonState")&&m.desc.equals("(Lnet/minecraft/world/level/Level;)Lsereneseasons/api/season/ISeasonState;")));
        assertTrue(read("sereneseasons/api/season/ISeasonState").methods.stream().anyMatch(m->m.name.equals("getSeason")&&m.desc.equals("()Lsereneseasons/api/season/Season;")));
    }
    @Test void contextRuntimeOnlyReadsScoresAndNeverMutatesWorld()throws Exception{
        var c=read("org/humanjourney/core/npc/context/NpcContextRuntime");boolean read=false;for(var m:c.methods)for(var i:m.instructions)if(i instanceof MethodInsnNode call){assertNotEquals("getOrCreatePlayerScore",call.name);assertNotEquals("addObjective",call.name);assertNotEquals("setBlock",call.name);if(call.name.equals("getPlayerScoreInfo"))read=true;}assertTrue(read);
    }
}
