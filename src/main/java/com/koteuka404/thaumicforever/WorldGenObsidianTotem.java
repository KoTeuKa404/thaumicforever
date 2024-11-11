package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.Block;
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

public class WorldGenObsidianTotem implements IWorldGenerator {

    private static final ResourceLocation TOTEM_TEMPLATE = new ResourceLocation("thaumicforever", "obsidian_totem");
    private static final ResourceLocation TOTEM_LEED_TEMPLATE = new ResourceLocation("thaumicforever", "obsidian_totem_leed");
    private static final ResourceLocation TOTEM_HIGH_TEMPLATE = new ResourceLocation("thaumicforever", "obsidian_totem_high");

    private static final ResourceLocation TOTEM_WITH_CHEST_TEMPLATE = new ResourceLocation("thaumicforever", "obsidian_totem_ch");
    private static final ResourceLocation TOTEM_LEED_WITH_CHEST_TEMPLATE = new ResourceLocation("thaumicforever", "obsidian_totem_leed_ch");
    private static final ResourceLocation TOTEM_HIGH_WITH_CHEST_TEMPLATE = new ResourceLocation("thaumicforever", "obsidian_totem_high_ch");

    public static final ResourceLocation BASIC_LOOT_TABLE = new ResourceLocation("thaumicforever", "chests/basic_loot");
    public static final ResourceLocation TOTEM_CHEST_TABLE = new ResourceLocation("thaumicforever", "chests/basic_loot");
    public static final ResourceLocation TOTEM_HIGH_CHEST_TABLE = new ResourceLocation("thaumicforever", "chests/basic_loot");
    public static final ResourceLocation TOTEM_LEED_CHEST_TABLE = new ResourceLocation("thaumicforever", "chests/basic_loot");

    public static void registerLootTables() {
        LootTableList.register(BASIC_LOOT_TABLE);
 
    }
    
    



    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0 && random.nextInt(360) == 0) { // Генерація з шансом 1/360
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = world.getHeight(x, z); // Знайти висоту на поверхні
            BlockPos pos = new BlockPos(x, y, z);

            if (isSolidBlockBelow(world, pos.down()) && isAreaClear(world, pos, 5, 10)) {
                int totemType = random.nextInt(6); // 0 - def, 1 - leed, 2 - high, 3 - із сундуком, 4 - leed_ch, 5 - high_ch

                switch (totemType) {
                    case 0:
                        generateStructure(world, pos, TOTEM_TEMPLATE);
                        break;
                    case 1:
                        generateStructure(world, pos, TOTEM_LEED_TEMPLATE);
                        break;
                    case 2:
                        generateStructure(world, pos, TOTEM_HIGH_TEMPLATE);
                        break;
                    case 3:
                        generateStructureWithChest(world, pos.down(), TOTEM_WITH_CHEST_TEMPLATE);
                        break;
                    case 4:
                        generateStructureWithChest(world, pos.down(), TOTEM_LEED_WITH_CHEST_TEMPLATE);
                        break;
                    case 5:
                        generateStructureWithChest(world, pos.down(), TOTEM_HIGH_WITH_CHEST_TEMPLATE);
                        break;
                }
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

    private void generateStructureWithChest(World world, BlockPos pos, ResourceLocation templateLocation) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), templateLocation);

        if (template != null) {
            template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));
            generateLootInChest(world, pos); 
        } else {
            System.out.println("Шаблон структури " + templateLocation + " не знайдено!");
        }
    }

    private void generateLootInChest(World world, BlockPos pos) {
        for (int x = -5; x < 5; x++) {
            for (int y = -5; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (world.getBlockState(checkPos).getBlock() == Blocks.CHEST) {
                        TileEntity tileEntity = world.getTileEntity(checkPos);
                        if (tileEntity instanceof TileEntityLockableLoot) {
                            ((TileEntityLockableLoot) tileEntity).setLootTable(BASIC_LOOT_TABLE, world.rand.nextLong());
                        }
                    }
                }
            }
        }
    }

    private boolean isSolidBlockBelow(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos).getBlock();
        return blockBelow != Blocks.WATER && blockBelow != Blocks.FLOWING_WATER && blockBelow.getMaterial(world.getBlockState(pos)).isSolid();
    }

    private boolean isAreaClear(World world, BlockPos pos, int width, int height) {
        for (int x = -width / 2; x < width / 2; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = -width / 2; z < width / 2; z++) {
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
