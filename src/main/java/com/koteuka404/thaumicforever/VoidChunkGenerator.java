package com.koteuka404.thaumicforever;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

public class VoidChunkGenerator implements IChunkGenerator {
    private final Set<Long> populatedChunks = new HashSet<>();
    private final World world;
    private boolean isPopulating = false;

    public VoidChunkGenerator(World worldIn) {
        this.world = worldIn;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        // Створюємо порожній чанк
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        return new Chunk(this.world, chunkPrimer, x, z);
    }

    @Override
    public void populate(int x, int z) {
        if (isPopulating) {
            return;  
        }
        
        isPopulating = true; 
        
        try {
            long chunkPos = ChunkPos.asLong(x, z);

            if (populatedChunks.contains(chunkPos)) {
                return; 
            }

            populatedChunks.add(chunkPos);

            BlockPos spawnPos = new BlockPos(x * 16, 64, z * 16);  // Позиція спавну
            generateBarrierCube(this.world, spawnPos);
        } finally {
            isPopulating = false;  
        }
    }

    private void generateBarrierCube(World world, BlockPos centerPos) {
        int chunkWidth = 16;
        int structureSize = 3 * chunkWidth; 
        int height = 25;
        
        int halfWidth = structureSize / 2; 
        
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = -halfWidth; z <= halfWidth; z++) {
                    BlockPos pos = centerPos.add(x, y, z);
                    
                    if (x == -halfWidth || x == halfWidth || z == -halfWidth || z == halfWidth || y == 0 || y == height) {
                        if (world.getBlockState(pos).getBlock() != Blocks.BARRIER) {
                            world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2 | 4 | 16);
                        }
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2 | 4 | 16);
                    }
                }
            }
        }
        
        BlockPos spawnPos = centerPos.add(0, 2, 0);
        world.setSpawnPoint(spawnPos); 
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
    public void recreateStructures(Chunk chunkIn, int x, int z) {
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return Collections.emptyList();
    }
}
