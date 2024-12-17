package com.koteuka404.thaumicforever;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

public class VoidChunkGenerator implements IChunkGenerator {
    private final World world;

    public VoidChunkGenerator(World worldIn) {
        this.world = worldIn;
    }

   @Override
public Chunk generateChunk(int x, int z) {
    ChunkPrimer chunkPrimer = new ChunkPrimer();

    // Додаємо платформу у центрі для гравця
    if (x == 0 && z == 0) {
        for (int y = 0; y < 2; y++) {
            for (int dx = 0; dx < 16; dx++) {
                for (int dz = 0; dz < 16; dz++) {
                    chunkPrimer.setBlockState(dx, 64 + y, dz, Blocks.STONE.getDefaultState());
                }
            }
        }
    }
    return new Chunk(this.world, chunkPrimer, x, z);
}


    @Override
    public void populate(int x, int z) {
        // Порожній світ
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean flag) {
        return null;
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        // Нічого не генеруємо
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return Collections.emptyList();
    }
}
