package org.humanjourney.core.wrap;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

/** Bytecode structural evidence, not a repeated-map rendering test. */
class XaeroWrapFeasibilityTest {
 ClassNode read(String name)throws Exception{try(var in=getClass().getResourceAsStream("/"+name+".class")){assertNotNull(in);var c=new ClassNode();new ClassReader(in).accept(c,0);return c;}}
 MethodNode method(ClassNode c,String name){return c.methods.stream().filter(m->m.name.equals(name)).findFirst().orElseThrow();}
 boolean calls(MethodNode m,String owner,String name){for(var n:m.instructions)if(n instanceof MethodInsnNode i&&i.owner.equals(owner)&&i.name.equals(name))return true;return false;}
 @Test void leafLookupCanCreateCoordinateKeyedRegionAndUpdateDetection()throws Exception{
  var m=method(read("xaero/map/MapProcessor"),"getLeafMapRegion");assertTrue(calls(m,"xaero/map/region/LayeredRegionManager","getLeaf"));assertTrue(calls(m,"xaero/map/region/LayeredRegionManager","putLeaf"));assertTrue(calls(m,"xaero/map/region/MapRegion","<init>"));assertTrue(calls(m,"xaero/map/region/MapRegion","getRegionFile"));
 }
 @Test void mapRenderingIncludesLoadingAndCoordinateBasedTileDrawing()throws Exception{
  var m=method(read("xaero/map/gui/GuiMap"),"render");assertTrue(calls(m,"xaero/map/region/MapRegion","getRegionX"));assertTrue(calls(m,"xaero/map/region/MapRegion","getRegionZ"));assertTrue(calls(m,"xaero/map/file/MapSaveLoad","requestLoad"));assertTrue(calls(m,"xaero/map/gui/GuiMap","renderTexturedModalRectWithLighting3"));
 }
 @Test void cameraCursorAndRegionStorageRemainSeparateFields()throws Exception{
  var gui=read("xaero/map/gui/GuiMap");var fields=new HashSet<String>();gui.fields.forEach(f->fields.add(f.name));assertTrue(fields.containsAll(Set.of("cameraX","cameraZ","mouseBlockPosX","mouseBlockPosZ","regionBuffer","branchRegionBuffer")));
  var region=read("xaero/map/region/LeveledRegion");var names=new HashSet<String>();region.fields.forEach(f->names.add(f.name));assertTrue(names.containsAll(Set.of("regionX","regionZ","level","cacheFile","worldId","dimId")));
 }
}
