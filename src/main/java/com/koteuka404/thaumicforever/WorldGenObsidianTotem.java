package com.koteuka404.thaumicforever;

import java.util.Collections;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;

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
    
    
    private void spawnSinisterNodeInTotemFromTemplate(World world, BlockPos origin, Template template) {
        Block obsidianTotem = ModBlocks.OBSIDIAN_TOTEM;
        for (int x = 0; x < template.getSize().getX(); x++) {
            for (int y = 0; y < template.getSize().getY(); y++) {
                for (int z = 0; z < template.getSize().getZ(); z++) {
                    BlockPos blockPosInWorld = origin.add(x, y, z);
                    Block block = world.getBlockState(blockPosInWorld).getBlock();
                    if (block == obsidianTotem) {
                        java.util.Random rand = world.rand;
                        int base = 100 / 3;
                        int size = 2 + base + rand.nextInt(2 + base);
                        EntityAuraNode node = new EntityAuraNode(world);
                        node.setPosition(blockPosInWorld.getX() + 0.5, blockPosInWorld.getY() + 0.5, blockPosInWorld.getZ() + 0.5);
                        node.setNodeSize(size);
                        node.setNodeType(1);
                        node.getNodeAspects().aspects.clear();
                        Aspect aspect = rand.nextBoolean() ? Aspect.DARKNESS : Aspect.UNDEAD;
                        node.getNodeAspects().add(aspect, size);
                        node.setFixedAspectOrder(Collections.singletonList(aspect));
                        node.updateSyncAspects();
                        node.enforceAspectLimit();
                        if (!world.isRemote) world.spawnEntity(node);
                        return; 
                    }
                }
            }
        }
    }
    
    

    private void buryChest(World world, BlockPos chestPos) {
    BlockPos[] around = new BlockPos[] {
        // chestPos.up(),
        chestPos.down(),
        chestPos.north(),
        chestPos.south(),
        chestPos.west(),
        chestPos.east()
    };

    Block replacement = null;
    for (BlockPos adj : around) {
        Block b = world.getBlockState(adj).getBlock();
        if (
            b != Blocks.AIR && b != Blocks.WATER && b != Blocks.FLOWING_WATER &&
            b != Blocks.SNOW_LAYER && b != Blocks.TALLGRASS && b != Blocks.GRASS &&
            b != Blocks.LEAVES && b != Blocks.LEAVES2 && b != Blocks.LOG && b != Blocks.LOG2 &&
            b != ModBlocks.OBSIDIAN_TOTEM && b != BlocksTC.stoneAncient 
        ) {
            replacement = b;
            break;
        }
    }
    if (replacement == null) replacement = Blocks.GRASS;

    BlockPos[] fillAround = new BlockPos[] {
        // chestPos.up(),      
        chestPos.down(),
        chestPos.north(),
        chestPos.south(),
        chestPos.west(),
        chestPos.east()
    };
    for (BlockPos adj : fillAround) {
        Block b = world.getBlockState(adj).getBlock();
        if (
            b == Blocks.AIR || b == Blocks.SNOW_LAYER || b == Blocks.TALLGRASS ||
            b == Blocks.GRASS || b == Blocks.LEAVES || b == Blocks.LEAVES2
        ) {
            world.setBlockState(adj, replacement.getDefaultState());
        }
    }
}

        @Override
        public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
            if (world.provider.getDimension() == 0 && random.nextInt(ModConfig.obsidianTotemChance) == 0) {
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = world.getHeight(x, z);
                BlockPos pos = new BlockPos(x, y, z);

                if (isSolidBlockBelow(world, pos.down()) && isAreaClear(world, pos, 5, 10)) {
                    int totemType = random.nextInt(6);

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
            spawnSinisterNodeInTotemFromTemplate(world, pos, template); 

        } 
    }

    private void generateStructureWithChest(World world, BlockPos pos, ResourceLocation templateLocation) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), templateLocation);

        if (template != null) {
            template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));
            generateLootInChest(world, pos); 
            spawnSinisterNodeInTotemFromTemplate(world, pos, template); 
        } else {
        }
    }

    private void generateLootInChest(World world, BlockPos pos) {
        for (int x = -5; x < 5; x++) {
            for (int y = -5; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (world.getBlockState(checkPos).getBlock() == Blocks.CHEST) {
                        buryChest(world, checkPos);

                        boolean isCovered = true;
                        for (int i = 1; i <= 2; i++) { 
                            BlockPos above = checkPos.up(i);
                            Block aboveBlock = world.getBlockState(above).getBlock();
                            if (!aboveBlock.getMaterial(world.getBlockState(above)).isSolid()) {
                                isCovered = false;
                                break;
                            }
                        }
                        if (isCovered) {
                            TileEntity tileEntity = world.getTileEntity(checkPos);
                            if (tileEntity instanceof TileEntityLockableLoot) {
                                ((TileEntityLockableLoot) tileEntity).setLootTable(BASIC_LOOT_TABLE, world.rand.nextLong());
                            }
                        } else {
                                
                        }
                    }
                    
                }
            }
        }
    }

    private boolean isSolidBlockBelow(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos).getBlock();
        if (
            blockBelow == Blocks.LOG || blockBelow == Blocks.LOG2 ||
            blockBelow == Blocks.LEAVES || blockBelow == Blocks.LEAVES2
        ) {
            return false;
        }
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
