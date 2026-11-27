package org.humanjourney.core.advancement;
import java.util.*;
import org.spongepowered.asm.mixin.extensibility.*;
import org.objectweb.asm.tree.ClassNode;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
public final class NativeMixinPlugin implements IMixinConfigPlugin {
 public static boolean supported(String minecraft,String forge){return "1.21.8".equals(minecraft)&&"58.1.18".equals(forge);}
 public static boolean supported(){try{return supported(net.minecraftforge.versions.mcp.MCPVersion.getMCVersion(),net.minecraftforge.versions.forge.ForgeVersion.getVersion());}catch(RuntimeException|LinkageError e){return false;}}
 public boolean shouldApplyMixin(String target,String mixin){return supported()&&(target.equals("net.minecraft.server.PlayerAdvancements")||FMLLoader.getDist()==Dist.CLIENT);}
 public void onLoad(String p){}public String getRefMapperConfig(){return null;}public void acceptTargets(Set<String>a,Set<String>b){}public List<String> getMixins(){return null;}public void preApply(String n,ClassNode c,String m,IMixinInfo i){}public void postApply(String n,ClassNode c,String m,IMixinInfo i){}
}
