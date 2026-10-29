package org.humanjourney.core.water;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import toughasnails.init.ModTags;
import java.util.*;
public final class NaturalWater {
    private record Cached(boolean connected,long tick){}
    private static final class Cache {
        WaterBalance.State policy;
        final Map<Long,Cached> entries=new LinkedHashMap<>(32,.75F,true){
            protected boolean removeEldestEntry(Map.Entry<Long,Cached> eldest){return size()>256;}
        };
    }
    private static final Map<Level,Cache> CACHES=new WeakHashMap<>();
    public static WaterPolicy.Quality at(Level level,BlockPos pos){
        if(!level.hasChunkAt(pos)||!level.getFluidState(pos).is(FluidTags.WATER))return null;
        var biome=level.getBiome(pos);
        return WaterGeography.classify(true,level.dimension().location().toString(),pos.getX()+.5,pos.getZ()+.5,biome.is(ModTags.Biomes.PURIFIED_WATER_BIOMES),biome.is(ModTags.Biomes.DIRTY_WATER_BIOMES),tag->biome.is(TagKey.create(Registries.BIOME,ResourceLocation.parse(tag))),WaterBalance.current().extensions().classification(),()->connected(level,pos));
    }
    private static boolean connected(Level level,BlockPos origin){
        var snapshot=WaterBalance.state();var policy=snapshot.policy().extensions().classification();int radius=policy.oceanConnectionRadius();
        if(radius==0||policy.saltTags().isEmpty())return false;
        var cache=CACHES.computeIfAbsent(level,ignored->new Cache());
        if(cache.policy!=snapshot){cache.entries.clear();cache.policy=snapshot;}
        long tick=level.getGameTime();var previous=cache.entries.get(origin.asLong());
        if(previous!=null&&tick>=previous.tick()&&tick-previous.tick()<20)return previous.connected();
        boolean result=OceanConnection.reachesOcean(new OceanConnection.Cell(origin.getX(),origin.getY(),origin.getZ()),radius,
            p->{var at=new BlockPos(p.x(),p.y(),p.z());return level.hasChunkAt(at)&&level.getFluidState(at).is(FluidTags.WATER);},
            p->{var at=new BlockPos(p.x(),p.y(),p.z());var biome=level.getBiome(at);
                // Only direct salt-tagged ocean water seeds this search; polygons never expand by adjacency.
                return policy.saltTags().stream().anyMatch(tag->biome.is(TagKey.create(Registries.BIOME,ResourceLocation.parse(tag))))
                    &&WaterGeography.classify(true,level.dimension().location().toString(),p.x()+.5,p.z()+.5,biome.is(ModTags.Biomes.PURIFIED_WATER_BIOMES),biome.is(ModTags.Biomes.DIRTY_WATER_BIOMES),tag->biome.is(TagKey.create(Registries.BIOME,ResourceLocation.parse(tag))),policy)==WaterPolicy.Quality.SALT;
            });
        cache.entries.put(origin.asLong(),new Cached(result,tick));return result;
    }
    public static void reset(){CACHES.clear();}
    /** Free-standing source water only: collectors, cauldrons and waterlogged blocks are not intakes. */
    public static boolean source(Level level,BlockPos pos){return level.hasChunkAt(pos)&&level.getBlockState(pos).is(Blocks.WATER)&&level.getFluidState(pos).isSource();}
    public static boolean collectable(Level level,BlockPos pos){return level.hasChunkAt(pos)&&level.getFluidState(pos).is(FluidTags.WATER)&&level.getFluidState(pos).isSource();}
    private NaturalWater(){}
}
