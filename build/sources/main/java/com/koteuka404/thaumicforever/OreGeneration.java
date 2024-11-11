package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class OreGeneration implements IWorldGenerator {

    public OreGeneration() {
        GameRegistry.registerWorldGenerator(this, 0);
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, net.minecraft.world.chunk.IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { // Only generate in the Overworld

            // Перевіряємо налаштування для Aquareia Ore
            if (ModConfig.general.enableAquareiaOreGeneration) {
                generateAquareiaOre(ModOreBlocks.AQUAREIA_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 28, 32, 2, 6, 8, Blocks.STONE.getDefaultState());
            }

            // Перевіряємо налаштування для Ancient Ores
            if (ModConfig.general.enableAncientOreGeneration) {
                generateOre(ModOreBlocks.ANCIENT_AMBER.getDefaultState(), world, rand, chunkX, chunkZ, 4, 16, 3, 7, 8, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_CINNABAR.getDefaultState(), world, rand, chunkX, chunkZ, 6, 24, 2, 8, 12, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_COAL.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 8, 16, 2, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_GOLD.getDefaultState(), world, rand, chunkX, chunkZ, 8, 32, 6, 12, 2, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_IRON.getDefaultState(), world, rand, chunkX, chunkZ, 8, 64, 8, 20, 2, Blocks.STONE.getDefaultState());
            }

            // Перевіряємо налаштування для Standard Ores
            if (ModConfig.general.enableStandardOreGeneration) {
                generateOre(ModOreBlocks.COPPER_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.TIN_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.LEAD_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.SILVER_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
            }
        }
    }

    private void generateAquareiaOre(IBlockState ore, World world, Random rand, int chunkX, int chunkZ, int minY, int maxY, int minVienSize, int maxVienSize, int chancesToSpawn, IBlockState blockToReplace) {
        int heightRange = maxY - minY;
        Biome targetBiome = Biome.REGISTRY.getObject(new ResourceLocation("thaumcraft", "magical_forest"));

        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunkX * 16 + rand.nextInt(16);
            int y = minY + rand.nextInt(heightRange);
            int z = chunkZ * 16 + rand.nextInt(16);

            BlockPos pos = new BlockPos(x, y, z);
            Biome biome = world.getBiome(pos);

            if (biome == targetBiome) {
                WorldGenMinable generator = new WorldGenMinable(ore, minVienSize + rand.nextInt(maxVienSize - minVienSize + 1), blockState -> blockState.getBlock() == blockToReplace.getBlock());
                generator.generate(world, rand, pos);
            }
        }
    }

    private void generateOre(IBlockState ore, World world, Random rand, int chunkX, int chunkZ, int minY, int maxY, int minVienSize, int maxVienSize, int chancesToSpawn, IBlockState blockToReplace) {
        int heightRange = maxY - minY;
        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunkX * 16 + rand.nextInt(16);
            int y = minY + rand.nextInt(heightRange);
            int z = chunkZ * 16 + rand.nextInt(16);

            BlockPos pos = new BlockPos(x, y, z);

            WorldGenMinable generator = new WorldGenMinable(ore, minVienSize + rand.nextInt(maxVienSize - minVienSize + 1), blockState -> blockState.getBlock() == blockToReplace.getBlock());
            generator.generate(world, rand, pos);
        }
    }
}
