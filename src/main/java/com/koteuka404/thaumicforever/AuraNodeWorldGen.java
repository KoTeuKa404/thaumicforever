package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class AuraNodeWorldGen implements IWorldGenerator {
    private static final int SPAWN_CHANCE = 100;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (random.nextInt(SPAWN_CHANCE) == 0) { 
            int x = (chunkX << 4) + random.nextInt(16);
            int z = (chunkZ << 4) + random.nextInt(16);
            int y = world.getHeight(new BlockPos(x, 0, z)).getY(); 

            BlockPos pos = new BlockPos(x, y, z);
            spawnAuraNode(world, pos);
        }
    }

    private void spawnAuraNode(World world, BlockPos pos) {
        AuraNodeEntity auraNode = new AuraNodeEntity(world);
        auraNode.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        world.spawnEntity(auraNode);
    }
}
