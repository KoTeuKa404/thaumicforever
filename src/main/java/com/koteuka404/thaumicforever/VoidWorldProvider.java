package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class VoidWorldProvider extends WorldProvider {

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.getById(0);
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new VoidChunkGenerator(world);
    }

    @Override
    public boolean canRespawnHere() {
        return false; 
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public Biome getBiomeForCoords(BlockPos pos) {
        return Biome.REGISTRY.getObject(new ResourceLocation("minecraft:void"));
    }

}
