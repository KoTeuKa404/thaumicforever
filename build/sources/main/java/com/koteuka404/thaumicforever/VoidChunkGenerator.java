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
            return;  // Уникаємо повторної популяції
        }
        
        isPopulating = true;  // Встановлюємо прапорець, що процес популяції запущений
        
        try {
            long chunkPos = ChunkPos.asLong(x, z);

            if (populatedChunks.contains(chunkPos)) {
                return; // Пропускаємо, якщо цей чанк вже був заселений
            }

            populatedChunks.add(chunkPos);

            // Генерація коробки з бар'єрів 3x3 чанки на 25 блоків у висоту
            BlockPos spawnPos = new BlockPos(x * 16, 64, z * 16);  // Позиція спавну
            generateBarrierCube(this.world, spawnPos);
        } finally {
            isPopulating = false;  // Вимикаємо прапорець після завершення
        }
    }

    private void generateBarrierCube(World world, BlockPos centerPos) {
        int chunkWidth = 16; // ширина одного чанка
        int structureSize = 3 * chunkWidth; // 3x3 чанки = 48 блоків по ширині
        int height = 25; // Висота структури 25 блоків
        
        int halfWidth = structureSize / 2; // Половина ширини для правильного центрованого розміщення
        
        // Проходимо через всі координати куба
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = -halfWidth; z <= halfWidth; z++) {
                    BlockPos pos = centerPos.add(x, y, z);
                    
                    // Створюємо тільки стіни, підлогу та стелю (тобто зовнішній куб)
                    if (x == -halfWidth || x == halfWidth || z == -halfWidth || z == halfWidth || y == 0 || y == height) {
                        if (world.getBlockState(pos).getBlock() != Blocks.BARRIER) {
                            world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2 | 4 | 16);
                        }
                    } else {
                        // Всередині куба все очищаємо (повітря)
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2 | 4 | 16);
                    }
                }
            }
        }
        
        // Спавн гравця в центрі структури (наприклад, на висоті 2 блоки від підлоги)
        BlockPos spawnPos = centerPos.add(0, 2, 0);
        world.setSpawnPoint(spawnPos);  // Встановлюємо точку спавну
    }
    
    
    
    

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false; // Не генеруємо структури
    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean flag) {
        return null; // Не генеруємо структури
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        // Структури не потрібні в цьому вимірі
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false; // Структури не підтримуються
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return Collections.emptyList(); // Порожній список, без спавну істот
    }
}
