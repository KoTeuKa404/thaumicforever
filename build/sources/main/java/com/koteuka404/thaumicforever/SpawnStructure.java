package com.koteuka404.thaumicforever;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnStructure {

    // Метод для генерації структури (коробка з бар'єрів)
    public static void generateStructure(World world, BlockPos pos) {
        // Створюємо коробку з бар'єрів
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = 0; y <= 25; y++) {
                    BlockPos blockPos = pos.add(x, y, z);
                    IBlockState blockState = Blocks.BARRIER.getDefaultState();
                    world.setBlockState(blockPos, blockState, 2);
                }
            }
        }

        // Встановлюємо точку появи для гравця
        BlockPos spawnPos = pos.add(0, 1, 0);
        world.setBlockState(spawnPos, Blocks.STONE.getDefaultState(), 2);
    }
}
