package org.humanjourney.core.npc.economy;
import java.util.*;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.extensibility.*;
import org.objectweb.asm.tree.ClassNode;
/** Gate the optional native adapter before target classes load; ItemCost extension has no Easy NPC types. */
public final class EconomyMixinPlugin implements IMixinConfigPlugin {
 public static boolean supported(String version){return "6.5.2".equals(version);}
 public boolean shouldApplyMixin(String target,String mixin){if(!target.equals("de.markusbordihn.easynpc.entity.easynpc.data.TradingDataCapable"))return true;var mods=FMLLoader.getLoadingModList();return mods!=null&&mods.getMods().stream().anyMatch(m->m.getModId().equals("easy_npc")&&supported(m.getVersion().toString()));}
 public void onLoad(String pkg){}public String getRefMapperConfig(){return null;}public void acceptTargets(Set<String> own,Set<String> other){}public List<String> getMixins(){return null;}public void preApply(String target,ClassNode node,String mixin,IMixinInfo info){}public void postApply(String target,ClassNode node,String mixin,IMixinInfo info){}
}
