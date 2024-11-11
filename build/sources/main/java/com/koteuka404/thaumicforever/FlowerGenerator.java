// FlowerGenerator.java
package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FlowerGenerator implements IWorldGenerator {

    private final Block flower;
    private final int chancePerChunk; // Кількість чанків, в яких буде одна спроба спавну

    public FlowerGenerator(Block flower, int chancePerChunk) {
        this.flower = flower;
        this.chancePerChunk = chancePerChunk;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { // Тільки для Overworld
            // Генеруємо лише якщо випадкове число менше за ймовірність
            if (random.nextInt(chancePerChunk) == 0) {
                // Кількість спроб в межах одного чанка (можна залишити 1 для рідкісної квітки)
                int attempts = 1; 
                for (int i = 0; i < attempts; i++) {
                    int x = chunkX * 16 + random.nextInt(16);
                    int z = chunkZ * 16 + random.nextInt(16);
                    int y = world.getHeight(x, z);
                    BlockPos pos = new BlockPos(x, y, z);
                    if (flower.canPlaceBlockAt(world, pos)) {
                        world.setBlockState(pos, flower.getDefaultState(), 2);
                    }
                }
            }
        }
    }

    public static void register() {
        // Червона роза - з'являється 1 раз на 10 чанків
        GameRegistry.registerWorldGenerator(new FlowerGenerator(ModBlocks.RED_ROSE, 10), 0);

        // Синя роза - з'являється 1 раз на 100 чанків
        GameRegistry.registerWorldGenerator(new FlowerGenerator(ModBlocks.BLUE_ROSE, 100), 0);
    }
}
