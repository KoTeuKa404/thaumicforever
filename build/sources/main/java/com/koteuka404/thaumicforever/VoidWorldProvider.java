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
        // Встановлюємо біом для світу на пустотний
        this.biomeProvider = new BiomeProviderSingle(Biomes.VOID);
        this.hasSkyLight = false; // Вимкнення освітлення неба
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        // Повертаємо наш кастомний генератор чанків
        return new VoidChunkGenerator(this.world);
    }

    @Override
    public DimensionType getDimensionType() {
        // Повертаємо тип виміру для нашого модифікованого світу
        return ModDimensions.VOID_DIMENSION;
    }

    @Override
    public boolean canRespawnHere() {
        return true; // Гравці можуть респавнитися тут
    }

    @Override
    public boolean isSurfaceWorld() {
        return false; // Це не поверхневий світ
    }

    @Override
    public int getAverageGroundLevel() {
        return 64; // Рівень поверхні (позиція спавну)
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean doesXZShowFog(int x, int z) {
        return false; // Без туману
    }
}
