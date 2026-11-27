package org.humanjourney.core.advancement.mixin;
import java.util.*;
import net.minecraft.advancements.*;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.*;
import org.humanjourney.core.advancement.NativeVisibility;
@Mixin(value=PlayerAdvancements.class,remap=false)
public abstract class PlayerAdvancementsMixin implements NativeVisibility {
 @Shadow @Final private Set<AdvancementHolder> visible;
 @Shadow @Final private Map<AdvancementHolder,AdvancementProgress> progress;
 public Set<AdvancementHolder> hjVisible(){return Collections.unmodifiableSet(visible);}
 public Map<AdvancementHolder,AdvancementProgress> hjProgress(){return Collections.unmodifiableMap(progress);}
}
