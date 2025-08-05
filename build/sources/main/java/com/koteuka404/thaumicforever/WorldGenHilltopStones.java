package com.koteuka404.thaumicforever;

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

    public static final ResourceLocation HILLTOP_STONES_LOOT_TABLE = new ResourceLocation("thaumicforever", "chests/hilltop_stones");

    public static void registerLootTables() {
        if (!net.minecraft.world.storage.loot.LootTableList.getAll().contains(HILLTOP_STONES_LOOT_TABLE)) {
            net.minecraft.world.storage.loot.LootTableList.register(HILLTOP_STONES_LOOT_TABLE);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { 
            if (random.nextInt(ModConfig.hilltopStonesChance) == 0) {  
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = world.getHeight(x, z);

                BlockPos pos = new BlockPos(x, y, z);

                if (y >= 85 && isSkyOpen(world, pos) /*&& isValidGround(world, pos.down()) */ ) {
                    if (canPlaceStructureHere(world, pos, 8, 8)) { 
                        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
                        Template template = templateManager.getTemplate(world.getMinecraftServer(), new ResourceLocation("thaumicforever", "hilltop_stones"));

                        if (template != null) {
                            template.addBlocksToWorld(world, pos, new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE));

                            generateLootInChests(world, pos);
                            spawnSinisterNode(world, pos, template); 


                         } 
                    }  
                }  
            }
        }
    }
    private void generateLootInChests(World world, BlockPos pos) {
        for (int x = -5; x < 5; x++) {  
            for (int y = 0; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    TileEntity tileEntity = world.getTileEntity(checkPos);
                    
                    if (tileEntity instanceof TileEntityLockableLoot) {
                        ((TileEntityLockableLoot) tileEntity).setLootTable(HILLTOP_STONES_LOOT_TABLE, world.rand.nextLong());
                    }
                }
            }
        }
    }

    private boolean isSkyOpen(World world, BlockPos pos) {
        return world.canSeeSky(pos);
    }

    private boolean isValidGround(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.STONE
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.DIRT
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.GRASS
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.SNOW
            || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.SNOW_LAYER;
    }
    private void spawnSinisterNode(World world, BlockPos basePos, Template template) {
        int centerX = template.getSize().getX() / 2;
        int centerZ = template.getSize().getZ() / 2;
        int centerY = 2; // по y + 4
    
        BlockPos nodePos = basePos.add(centerX, centerY, centerZ);
    
        java.util.Random rand = world.rand;
        int base = 100 / 3;
        int size = 2 + base + rand.nextInt(2 + base);
    
        EntityAuraNode node = new EntityAuraNode(world);
        node.setPosition(nodePos.getX() + 0.5, nodePos.getY() + 0.5, nodePos.getZ() + 0.5);
        node.setNodeSize(size);
        node.setNodeType(1); // Sinister/Dark
    
        node.getNodeAspects().aspects.clear();
        thaumcraft.api.aspects.Aspect aspect = rand.nextBoolean()
            ? thaumcraft.api.aspects.Aspect.DARKNESS
            : thaumcraft.api.aspects.Aspect.UNDEAD;
        node.getNodeAspects().add(aspect, size);
    
        node.updateSyncAspects();
        node.enforceAspectLimit();
        if (!world.isRemote) world.spawnEntity(node);
    }
    
    private boolean canPlaceStructureHere(World world, BlockPos pos, int width, int depth) {
        for (int x = -width / 2; x < width / 2; x++) {
            for (int z = -depth / 2; z < depth / 2; z++) {
                BlockPos checkPos = pos.add(x, 0, z);
                if (!world.isAirBlock(checkPos) && !world.getBlockState(checkPos).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
