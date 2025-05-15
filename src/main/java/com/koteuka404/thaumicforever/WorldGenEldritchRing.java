package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenEldritchRing implements IWorldGenerator {

    private static final ResourceLocation ELDRITCH_RING_TEMPLATE = new ResourceLocation("thaumicforever", "eldritch_ring");
    private static final int ALLOWED_DIMENSION = 0;  

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != ALLOWED_DIMENSION) {
            return;
        }

        if (random.nextInt(66) == 0) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = world.getHeight(x, z); 
            BlockPos pos = new BlockPos(x, y, z);

            if (isDarkForestBiome(world, pos) && isSolidBlockBelow(world, pos.down())) {
                generateStructure(world, pos, ELDRITCH_RING_TEMPLATE);
            }
        }
    }

    private void generateStructure(World world, BlockPos pos, ResourceLocation templateLocation) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), templateLocation);

        if (template != null) {
            template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));
        }
    }

    private boolean isDarkForestBiome(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        ResourceLocation biomeRegistryName = Biome.REGISTRY.getNameForObject(biome);

        return biomeRegistryName != null && biomeRegistryName.toString().toLowerCase().contains("dark_forest");
    }

    private boolean isSolidBlockBelow(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().getMaterial(world.getBlockState(pos)).isSolid()
            && world.getBlockState(pos).getBlock() != Blocks.WATER
            && world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER;
    }
}
