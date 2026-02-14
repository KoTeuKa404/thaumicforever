package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
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

    private static final ResourceLocation MAZE_STRUCTURE = new ResourceLocation("thaumicforever", "mazee");
    private static final ResourceLocation[] SMALL_STRUCTURES = new ResourceLocation[] {
        new ResourceLocation("thaumicforever", "vase"),
        new ResourceLocation("thaumicforever", "web"),
        new ResourceLocation("thaumicforever", "bone"),
        new ResourceLocation("thaumicforever", "box"),
        new ResourceLocation("thaumicforever", "head"),
        new ResourceLocation("thaumicforever", "iron"),
        new ResourceLocation("thaumicforever", "gravel")
    };
    private static final Block MOB_MARKER_BLOCK = Blocks.MAGENTA_GLAZED_TERRACOTTA;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.isRemote) return;

        if (world.provider.getDimension() == 0) {
            if (random.nextInt(ModConfig.mazeChance) != 0) return;

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
                
                        BoundingBox box = new BoundingBox(pos, endPos);
                        WorldTickHandler.getInstance().addDungeonBounds(pos, endPos);
                        DungeonBoundsData data = DungeonBoundsData.get(world);
                        if (data != null) data.addBox(box);
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

            BlockPos size = template.getSize();
            for (int x = 0; x < size.getX(); x++) {
                for (int y = 0; y < size.getY(); y++) {
                    for (int z = 0; z < size.getZ(); z++) {
                        BlockPos markerPos = pos.add(x, y, z);
                        if (world.getBlockState(markerPos).getBlock() == ModBlocks.STRUCTURE_MARKER_HOLDER) {
                            ResourceLocation selected;
                            if (world.rand.nextInt(3) == 0) {
                                do {
                                    selected = SMALL_STRUCTURES[world.rand.nextInt(SMALL_STRUCTURES.length)];
                                } while (selected.equals(SMALL_STRUCTURES[0])); // [0] = vase
                            } else {
                                selected = SMALL_STRUCTURES[world.rand.nextInt(SMALL_STRUCTURES.length)];
                            }
                        
                            spawnMiniStructure(world, markerPos, selected, world.rand);
                        }
                        
                        if (world.getBlockState(markerPos).getBlock() == MOB_MARKER_BLOCK) {
                            spawnRandomMannequin(world, markerPos, world.rand);
                            world.setBlockToAir(markerPos);
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void spawnRandomMannequin(World world, BlockPos pos, Random rand) {
        EntityGuardianMannequin mannequin = new EntityGuardianMannequin(world);
        mannequin.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        mannequin.setHostile(true);
        mannequin.initEntityAI();

        List<ItemStack> headArmor = new ArrayList<>();
        List<ItemStack> chestArmor = new ArrayList<>();
        List<ItemStack> legsArmor = new ArrayList<>();
        List<ItemStack> feetArmor = new ArrayList<>();
        List<ItemStack> allWeapons = new ArrayList<>();
    
        for (Object obj : Item.REGISTRY) {
            Item item = (Item) obj;
            if (item instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) item;
                switch (armor.armorType) {
                    case HEAD: headArmor.add(new ItemStack(item)); break;
                    case CHEST: chestArmor.add(new ItemStack(item)); break;
                    case LEGS: legsArmor.add(new ItemStack(item)); break;
                    case FEET: feetArmor.add(new ItemStack(item)); break;
                }
            }
            if (item instanceof ItemSword || item instanceof ItemTool) {
                allWeapons.add(new ItemStack(item));
            }
        }
    
        mannequin.setItemStackToSlot(EntityEquipmentSlot.HEAD, headArmor.get(rand.nextInt(headArmor.size())));
        mannequin.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestArmor.get(rand.nextInt(chestArmor.size())));
        mannequin.setItemStackToSlot(EntityEquipmentSlot.LEGS, legsArmor.get(rand.nextInt(legsArmor.size())));
        mannequin.setItemStackToSlot(EntityEquipmentSlot.FEET, feetArmor.get(rand.nextInt(feetArmor.size())));
        mannequin.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, allWeapons.get(rand.nextInt(allWeapons.size())));
    
        world.spawnEntity(mannequin);

    }
    
    private void spawnMiniStructure(World world, BlockPos pos, ResourceLocation structure, Random random) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template mini = templateManager.getTemplate(world.getMinecraftServer(), structure);

        if (mini != null) {
            BlockPos adjustedPos = pos.add(-1, 0, -1);
            mini.addBlocksToWorld(world, adjustedPos, new PlacementSettings().setIgnoreEntities(false));
        }
        world.setBlockToAir(pos);
    }
}
