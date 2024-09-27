package com.tutorialmod.turtywurty;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenEldritchRing implements IWorldGenerator {

    // Шаблон структури
    private static final ResourceLocation ELDRITCH_RING_TEMPLATE = new ResourceLocation("thaumicforever", "eldritch_ring");

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { // Генерація тільки в Overworld
            if (random.nextInt(66) == 0) { // Частота генерації 1/66
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = world.getHeight(x, z); // Генерація на поверхні
                BlockPos pos = new BlockPos(x, y, z);

                // Перевіряємо, чи структура спавниться в темному лісі та чи немає води
                if (isDarkForestBiome(world, pos) && isSolidBlockBelow(world, pos.down())) {
                    generateStructure(world, pos, ELDRITCH_RING_TEMPLATE);
                }
            }
        }
    }

    // Генерація структури
    private void generateStructure(World world, BlockPos pos, ResourceLocation templateLocation) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), templateLocation);

        if (template != null) {
            template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));
        } else {
            System.out.println("Шаблон структури " + templateLocation + " не знайдено!");
        }
    }

    // Перевірка, чи біом є темним лісом
    private boolean isDarkForestBiome(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        return biome instanceof BiomeForest && biome.getBiomeName().toLowerCase().contains("dark");
    }

    // Перевіряємо, чи блок під структурою не вода
    private boolean isSolidBlockBelow(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().getMaterial(world.getBlockState(pos)).isSolid()
            && world.getBlockState(pos).getBlock() != Blocks.WATER
            && world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER;
    }
}
