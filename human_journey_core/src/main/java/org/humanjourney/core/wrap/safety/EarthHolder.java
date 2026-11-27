package org.humanjourney.core.wrap.safety;
import java.util.concurrent.*;
import net.minecraft.server.level.*;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.util.StaticCache2D;
public interface EarthHolder {
 CompletableFuture<ChunkResult<ChunkAccess>> hjEarth$apply(ChunkStep step,GeneratingChunkMap map,StaticCache2D<GenerationChunkHolder> cache);
 void hjEarth$fail();
}
