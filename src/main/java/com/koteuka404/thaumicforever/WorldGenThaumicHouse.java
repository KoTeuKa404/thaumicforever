package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
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


public class WorldGenThaumicHouse implements IWorldGenerator {

    public static final ResourceLocation HOUSE_LOOT_TABLE = new ResourceLocation("thaumicforever", "chests/house");

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { 
            Biome targetBiome = Biome.REGISTRY.getObject(new ResourceLocation("thaumcraft", "magical_forest"));
            Biome biome = world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16));

            if (biome == targetBiome && random.nextInt(2) == 0) {  
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = world.getHeight(x, z);

                BlockPos pos = new BlockPos(x, y, z);

                if (isValidGround(world, pos.down()) && world.canSeeSky(pos)) {
                    TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
                    Template template = templateManager.getTemplate(world.getMinecraftServer(), new ResourceLocation("thaumicforever", "thaumic_house"));

                    if (template != null) {
                        template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));

                        generateLootInChests(world, pos);
                    }
                }
            }
        }
    }

    private boolean isValidGround(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.GRASS
                || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.DIRT
                || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.STONE
                || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.SAND
                || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.SNOW_LAYER;
    }
    
    private void generateStructure(World world, BlockPos pos, Random random) {

        generateLootInChests(world, pos);
    }

    private void generateLootInChests(World world, BlockPos pos) {
        for (int x = -5; x < 5; x++) {  
            for (int y = 0; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    TileEntity tileEntity = world.getTileEntity(checkPos);

                    if (tileEntity instanceof TileEntityLockableLoot) {
                        ((TileEntityLockableLoot) tileEntity).setLootTable(HOUSE_LOOT_TABLE, world.rand.nextLong());
                    }
                }
            }
        }
    }
}
