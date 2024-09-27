package com.tutorialmod.turtywurty;

import java.util.Random;

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
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenHilltopStones implements IWorldGenerator {

    // Луттейбл для звичайного сундука
    public static final ResourceLocation HILLTOP_STONES_LOOT_TABLE = new ResourceLocation("thaumicforever", "chests/hilltop_stones");

    // Метод для реєстрації луттейблів
    public static void registerLootTables() {
        // Реєструємо луттейбл, якщо він ще не зареєстрований
        if (!net.minecraft.world.storage.loot.LootTableList.getAll().contains(HILLTOP_STONES_LOOT_TABLE)) {
            net.minecraft.world.storage.loot.LootTableList.register(HILLTOP_STONES_LOOT_TABLE);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { // Генерація тільки в Overworld
            // Шанс генерації структури (1 з 4)
            if (random.nextInt(400) == 0) {  
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = world.getHeight(x, z);

                BlockPos pos = new BlockPos(x, y, z);

                // Перевірка висоти, відкритого неба та правильного ґрунту
                if (y >= 65 && isSkyOpen(world, pos) && isValidGround(world, pos.down())) {
                    // Перевірка, чи є достатньо вільного місця для структури
                    if (canPlaceStructureHere(world, pos, 8, 8, 8)) {  // Розмір структури 8x8x8 (можна налаштувати)
                        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
                        Template template = templateManager.getTemplate(world.getMinecraftServer(), new ResourceLocation("thaumicforever", "hilltop_stones"));

                        if (template != null) {
                            template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));

                            // Генерація лута в звичайних сундуках
                            generateLootInChests(world, pos);

                         } 
                    }  
                }  
            }
        }
    }

    // Генерація лута в звичайних сундуках після генерації структури
    private void generateLootInChests(World world, BlockPos pos) {
        for (int x = -5; x < 5; x++) {  // Діапазон залежить від розміру структури
            for (int y = 0; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    TileEntity tileEntity = world.getTileEntity(checkPos);
                    
                    if (tileEntity instanceof TileEntityLockableLoot) {
                        // Призначаємо лут-таблицю звичайному сундуку
                        ((TileEntityLockableLoot) tileEntity).setLootTable(HILLTOP_STONES_LOOT_TABLE, world.rand.nextLong());
                    }
                }
            }
        }
    }

    // Перевірка наявності відкритого неба
    private boolean isSkyOpen(World world, BlockPos pos) {
        return world.canSeeSky(pos);
    }

    // Перевірка наявності правильного ґрунту для генерації структури
    private boolean isValidGround(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.STONE
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.DIRT
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.GRASS
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.SNOW
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.SNOW_LAYER;
    }

    // Перевірка, чи є вільне місце для генерації структури
    private boolean canPlaceStructureHere(World world, BlockPos pos, int width, int height, int depth) {
        for (int x = -width / 2; x < width / 2; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = -depth / 2; z < depth / 2; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (!world.isAirBlock(checkPos)) {
                        return false;  
                    }
                }
            }
        }
        return true;   
    }
}
