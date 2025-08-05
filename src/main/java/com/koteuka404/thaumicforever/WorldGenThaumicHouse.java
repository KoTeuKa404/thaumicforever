package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
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
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenThaumicHouse implements IWorldGenerator {

    public static final ResourceLocation HOUSE_LOOT_TABLE =
        new ResourceLocation("thaumicforever", "chests/house");

  
    public static void registerLootTables() {
        if (!LootTableList.getAll().contains(HOUSE_LOOT_TABLE)) {
            LootTableList.register(HOUSE_LOOT_TABLE);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!ModConfig.enableThaumicHouseGeneration || world.provider.getDimension() != 0) {
            return;
        }

        Biome target = Biome.REGISTRY.getObject(
            new ResourceLocation("thaumcraft", "magical_forest")
        );
        Biome here = world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16));
        if (here != target) return;

        if (random.nextInt(ModConfig.thaumicHouseSpawnChance) != 0) return;

        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = world.getHeight(x, z);
        BlockPos origin = new BlockPos(x, y, z);

        if (!isValidGround(world, origin.down())) return;

        TemplateManager tm = world.getSaveHandler().getStructureTemplateManager();
        Template tpl = tm.getTemplate(world.getMinecraftServer(),
            new ResourceLocation("thaumicforever", "thaumic_house"));
        if (tpl == null) return;

        PlacementSettings settings = new PlacementSettings()
            .setMirror(Mirror.NONE)
            .setRotation(Rotation.NONE)
            .setIgnoreEntities(true);
        tpl.addBlocksToWorld(world, origin, settings);

        generateLootInChests(world, origin, tpl, random);

        spawnWizard(world, origin, random);
    }

    private boolean isValidGround(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.GRASS
            || world.getBlockState(pos).getBlock() == Blocks.DIRT
            || world.getBlockState(pos).getBlock() == Blocks.STONE
            || world.getBlockState(pos).getBlock() == Blocks.SAND
            || world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER;
    }

    private void generateLootInChests(World world, BlockPos origin, Template tpl, Random random) {
        BlockPos size = tpl.getSize();
        for (int dx = 0; dx < size.getX(); dx++) {
            for (int dy = 0; dy < size.getY(); dy++) {
                for (int dz = 0; dz < size.getZ(); dz++) {
                    BlockPos pos = origin.add(dx, dy, dz);
                    if (world.getBlockState(pos).getBlock() instanceof BlockChest) {
                        TileEntity te = world.getTileEntity(pos);
                        if (te instanceof TileEntityLockableLoot) {
                            TileEntityLockableLoot loot = (TileEntityLockableLoot) te;
                            loot.setLootTable(HOUSE_LOOT_TABLE, random.nextLong());
                            loot.fillWithLoot(null);
                        }
                    }
                }
            }
        }
    }

    private void spawnWizard(World world, BlockPos pos, Random random) {
        if (!world.isRemote) {
            WizardVillager wizard = new WizardVillager(world);
            wizard.setLocationAndAngles(
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                random.nextFloat() * 360F, 0F
            );
            world.spawnEntity(wizard);
        }
    }
}
