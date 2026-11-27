package org.humanjourney.core.wrap.safety;
import java.util.*;
import java.util.concurrent.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.nbt.CompoundTag;
public interface EarthAccess {
 ServerLevel hjEarth$level();
 java.util.concurrent.Executor hjEarth$executor();
 CompletableFuture<Optional<CompoundTag>> hjEarth$read(ChunkPos pos);
}
