package com.koteuka404.thaumicforever;

import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VoidWorldProvider extends WorldProvider {

    @Override
    public void init() {
        this.biomeProvider = new BiomeProviderSingle(Biomes.VOID);
        this.hasSkyLight = false; 
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new VoidChunkGenerator(this.world);
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.VOID_DIMENSION;
    }

    @Override
    public boolean canRespawnHere() {
        return true;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public int getAverageGroundLevel() {
        return 64; 
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean doesXZShowFog(int x, int z) {
        return false;
    }
}
