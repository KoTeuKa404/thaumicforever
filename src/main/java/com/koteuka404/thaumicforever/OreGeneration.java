package com.koteuka404.thaumicforever;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.material.Material;
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
    private static final Logger LOGGER = LogManager.getLogger("ThaumicForever");

    public OreGeneration() {
        GameRegistry.registerWorldGenerator(this, 0);
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, net.minecraft.world.chunk.IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { 

            if (ModConfig.enableAquareiaOreGeneration) {
                generateAquareiaOre(ModOreBlocks.AQUAREIA_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 28, 32, 2, 6, 8, Blocks.STONE.getDefaultState());
            }

            if (ModConfig.enableAncientOreGeneration) {
                generateOre(ModOreBlocks.ANCIENT_AMBER.getDefaultState(), world, rand, chunkX, chunkZ, 4, 16, 4, 12, 2, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_CINNABAR.getDefaultState(), world, rand, chunkX, chunkZ, 6, 24, 4, 12, 2, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_COAL.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 12, 1, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_GOLD.getDefaultState(), world, rand, chunkX, chunkZ, 8, 32, 4, 12, 1, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.ANCIENT_IRON.getDefaultState(), world, rand, chunkX, chunkZ, 8, 64, 4, 12, 1, Blocks.STONE.getDefaultState());
            }

            if (ModConfig.enableStandardOreGeneration) {
                generateOre(ModOreBlocks.COPPER_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.TIN_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.LEAD_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
                generateOre(ModOreBlocks.SILVER_ORE.getDefaultState(), world, rand, chunkX, chunkZ, 8, 40, 4, 10, 16, Blocks.STONE.getDefaultState());
            }
        }

        if (world.provider.getDimension() == 1) {
                generateOre(ModBlocks.EndOreBlock.getDefaultState(), world, rand, chunkX, chunkZ, 8, 48, 5, 8, 10, Blocks.END_STONE.getDefaultState());
            }

        if (world.provider.getDimension() == -1) {
            generateRubyOre(ModBlocks.RubyOre.getDefaultState(), world, rand, chunkX, chunkZ, 10, 118, 5, 9, 10, Blocks.NETHERRACK.getDefaultState());
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

    private void generateRubyOre(IBlockState ore, World world, Random rand,
                                int chunkX, int chunkZ,
                                int minY, int maxY,
                                int minVeinSize, int maxVeinSize,
                                int chancesToSpawn, IBlockState blockToReplace) {
        int heightRange = maxY - minY;

        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunkX * 16 + rand.nextInt(16);
            int y = minY    + rand.nextInt(heightRange);
            int z = chunkZ * 16 + rand.nextInt(16);
            BlockPos center = new BlockPos(x, y, z);

            if (!world.isBlockLoaded(center)) continue;

            int veinSize = minVeinSize + rand.nextInt(maxVeinSize - minVeinSize + 1);
            for (int j = 0; j < veinSize; j++) {
                BlockPos pos = center.add(
                    rand.nextInt(3) - 1,
                    rand.nextInt(2) - 1,
                    rand.nextInt(3) - 1
                );
                if (!world.isBlockLoaded(pos)) continue;

                IBlockState state = world.getBlockState(pos);
                boolean isMagmaHere = state.getBlock() == Blocks.MAGMA;
                boolean isRockHere  = state.getMaterial() == Material.ROCK;
                boolean nearMagma = false;
                for (BlockPos.MutableBlockPos nb : BlockPos.getAllInBoxMutable(
                        pos.add(-1, -1, -1),
                        pos.add( 1,  1,  1))) {
                    if (world.getBlockState(nb).getBlock() == Blocks.MAGMA) {
                        nearMagma = true;
                        break;
                    }
                }

                if (isMagmaHere || (isRockHere && nearMagma)) {
                    world.setBlockState(pos, ore, 2);
                    // LOGGER.info("RubyOre spawned at {} in nether chunk ({}, {})",pos, chunkX, chunkZ);
                }
            }
        }
    }

    
}
