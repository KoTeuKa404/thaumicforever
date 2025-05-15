package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenMazeInTaiga implements IWorldGenerator {

    private static final ResourceLocation MAZE_STRUCTURE = new ResourceLocation("thaumicforever", "saved_structure");

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.isRemote) {
            return; 
        }
    
        if (world.provider.getDimension() == 0) {
            if (random.nextInt(700) != 0) {
                return;
            }
    
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = 10 + random.nextInt(31);
            BlockPos pos = new BlockPos(x, y, z);
            Biome biome = world.getBiome(pos);
    
            if (biome instanceof BiomeTaiga) {
                if (generateMaze(world, pos)) {
                    TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
                    Template template = templateManager.getTemplate(world.getMinecraftServer(), MAZE_STRUCTURE);
                    if (template != null) {
                        BlockPos size = template.getSize();
                        BlockPos endPos = pos.add(size);
    
                        WorldTickHandler.getInstance().addDungeonBounds(pos, endPos);
                    }
                }
            }
        }
    }
    

    private boolean generateMaze(World world, BlockPos pos) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), MAZE_STRUCTURE);

        if (template != null) {
            PlacementSettings settings = new PlacementSettings()
                    .setIgnoreEntities(false)
                    .setReplacedBlock(Blocks.STRUCTURE_VOID); 

            template.addBlocksToWorldChunk(world, pos, settings); 
            return true;
        } else {
            return false;
        }
    }
}
