package org.humanjourney.core.water;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import toughasnails.init.ModTags;
public final class NaturalWater {
    public static WaterPolicy.Quality at(Level level,BlockPos pos){
        if(!level.hasChunkAt(pos)||!level.getFluidState(pos).is(FluidTags.WATER))return null;
        var biome=level.getBiome(pos);
        return WaterGeography.classify(true,level.dimension().location().toString(),pos.getX()+.5,pos.getZ()+.5,biome.is(ModTags.Biomes.PURIFIED_WATER_BIOMES),biome.is(ModTags.Biomes.DIRTY_WATER_BIOMES),tag->biome.is(TagKey.create(Registries.BIOME,ResourceLocation.parse(tag))),WaterBalance.current().extensions().classification());
    }
    /** Free-standing source water only: collectors, cauldrons and waterlogged blocks are not intakes. */
    public static boolean source(Level level,BlockPos pos){return level.hasChunkAt(pos)&&level.getBlockState(pos).is(Blocks.WATER)&&level.getFluidState(pos).isSource();}
    private NaturalWater(){}
}
