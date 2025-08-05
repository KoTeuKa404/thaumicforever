package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenUnderloot implements IWorldGenerator {

    public static final ResourceLocation UNDERLOOT_LOOT_TABLE = new ResourceLocation("thaumicforever", "chests/dan");

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { 
            if (random.nextInt(250) == 0) {  
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = 10 + random.nextInt(21); 
                BlockPos pos = new BlockPos(x, y, z);

                if (isWithinHeightRange(pos.getY()) && isUnderground(world, pos)) {
                    generateStructure(world, pos);
                }
            }
        }
    }

    private void generateStructure(World world, BlockPos pos) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), new ResourceLocation("thaumicforever", "underloot"));

        if (template != null) {
            template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));
            generateLootInChest(world, pos);
        } else {
        }
    }

    private void generateLootInChest(World world, BlockPos pos) {
        for (int x = -5; x < 5; x++) {
            for (int y = -5; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (world.getBlockState(checkPos).getBlock() == Blocks.TRAPPED_CHEST) {
                        TileEntity tileEntity = world.getTileEntity(checkPos);
                        if (tileEntity instanceof TileEntityLockableLoot) {
                            ((TileEntityLockableLoot) tileEntity).setLootTable(UNDERLOOT_LOOT_TABLE, world.rand.nextLong());
                        }
                    }
                }
            }
        }
    }

    private boolean isWithinHeightRange(int y) {
        return y >= 10 && y <= 40;
    }

    private boolean isUnderground(World world, BlockPos pos) {
        int requiredSolidBlocks = 20;

        for (int y = pos.getY() + 1; y < pos.getY() + requiredSolidBlocks; y++) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());

            if (world.isAirBlock(checkPos) || !world.getBlockState(checkPos).isOpaqueCube()) {
                return false;
            }
        }
        return true; 
    }

    public static void registerLootTables() {
        if (!LootTableList.getAll().contains(UNDERLOOT_LOOT_TABLE)) {
            LootTableList.register(UNDERLOOT_LOOT_TABLE);
        }
    }
}
