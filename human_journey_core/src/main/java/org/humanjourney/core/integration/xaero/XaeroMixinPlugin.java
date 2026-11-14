package org.humanjourney.core.integration.xaero;
import java.util.*;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
import org.spongepowered.asm.mixin.extensibility.*;
import org.objectweb.asm.tree.ClassNode;
/** Optional before-load gate. Dedicated/common paths never link a Xaero GUI class. */
public final class XaeroMixinPlugin implements IMixinConfigPlugin {
 public boolean shouldApplyMixin(String target,String mixin){if(FMLLoader.getDist()!=Dist.CLIENT)return false;var mods=FMLLoader.getLoadingModList();return mods!=null&&mods.getMods().stream().anyMatch(m->m.getModId().equals("xaeroworldmap")&&XaeroStatus.supported(true,m.getVersion().toString()));}
 public void onLoad(String p){}public String getRefMapperConfig(){return null;}public void acceptTargets(Set<String>a,Set<String>b){}public List<String> getMixins(){return null;}public void preApply(String n,ClassNode c,String m,IMixinInfo i){}public void postApply(String n,ClassNode c,String m,IMixinInfo i){}
}
