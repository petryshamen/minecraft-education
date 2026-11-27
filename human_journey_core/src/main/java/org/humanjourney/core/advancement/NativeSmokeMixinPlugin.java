package org.humanjourney.core.advancement;
import java.util.*;
import org.spongepowered.asm.mixin.extensibility.*;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.objectweb.asm.tree.ClassNode;
/** Datagen-only UNKNOWN-side verification. Never listed in the runtime manifest. */
public final class NativeSmokeMixinPlugin implements IMixinConfigPlugin {
 public boolean shouldApplyMixin(String target,String mixin){return Boolean.getBoolean("humanjourney.waterSmoke")&&NativeMixinPlugin.supported()&&net.minecraftforge.fml.loading.FMLLoader.getDist()==net.minecraftforge.api.distmarker.Dist.CLIENT&&MixinEnvironment.getCurrentEnvironment().getSide()==MixinEnvironment.Side.UNKNOWN;}
 public void onLoad(String p){}public String getRefMapperConfig(){return null;}public void acceptTargets(Set<String>a,Set<String>b){}public List<String> getMixins(){return null;}public void preApply(String n,ClassNode c,String m,IMixinInfo i){}public void postApply(String n,ClassNode c,String m,IMixinInfo i){}
}
