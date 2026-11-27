package org.humanjourney.core.wrap;
import java.util.*;
import org.spongepowered.asm.mixin.extensibility.*;
import org.objectweb.asm.tree.ClassNode;
public final class WrapMixinPlugin implements IMixinConfigPlugin {
 public static boolean supported(String mc,String forge){return "1.21.8".equals(mc)&&"58.1.18".equals(forge);}
 public static boolean supported(){return supported(net.minecraftforge.versions.mcp.MCPVersion.getMCVersion(),net.minecraftforge.versions.forge.ForgeVersion.getVersion());}
 public boolean shouldApplyMixin(String target,String mixin){return supported();}
 public void onLoad(String p){}public String getRefMapperConfig(){return null;}public void acceptTargets(Set<String>a,Set<String>b){}public List<String> getMixins(){return null;}public void preApply(String n,ClassNode c,String m,IMixinInfo i){}public void postApply(String n,ClassNode c,String m,IMixinInfo i){}
}
