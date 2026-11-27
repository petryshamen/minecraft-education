package org.humanjourney.core.advancement;
import java.util.*;
import net.minecraft.advancements.*;
/** Read-only views of vanilla's own synchronization sets; callers never modify them. */
public interface NativeVisibility {Set<AdvancementHolder> hjVisible();Map<AdvancementHolder,AdvancementProgress> hjProgress();}
