package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import com.koteuka404.thaumicforever.tile.TileBigJar;
import com.koteuka404.thaumicforever.tile.TileBigJarPart;

public class BlockBigJar extends BlockContainer {
    private static final AxisAlignedBB BIG_JAR_AABB = FULL_BLOCK_AABB;
    private static boolean removing = false;

    public BlockBigJar() {
        super(Material.GLASS);
        setUnlocalizedName("big_jar");
        setRegistryName("big_jar");
        setHardness(0.4F);
        setResistance(1.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BIG_JAR_AABB;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return canPlaceAt(worldIn, pos, false);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (worldIn.isRemote) {
            return;
        }
        if (!canPlaceAt(worldIn, pos, true)) {
            worldIn.setBlockToAir(pos);
            return;
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileBigJar) {
            ((TileBigJar) te).setOrigin(pos);
        }
        placeParts(worldIn, pos);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBigJar();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileBigJar();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (removing) {
            super.breakBlock(worldIn, pos, state);
            return;
        }
        removeStructure(worldIn, pos, true);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return tryAddBrain(worldIn, pos, player, hand);
    }

    public static void removeStructure(World worldIn, BlockPos origin, boolean drop) {
        if (removing) {
            return;
        }
        removing = true;
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos p = origin.add(dx, dy, dz);
                    if (worldIn.getBlockState(p).getBlock() == ModBlocks.BIG_JAR
                            || worldIn.getBlockState(p).getBlock() == ModBlocks.BIG_JAR_PART) {
                        worldIn.setBlockToAir(p);
                    }
                }
            }
        }
        if (drop && !worldIn.isRemote) {
            spawnAsEntity(worldIn, origin, new ItemStack(ModBlocks.BIG_JAR));
        }
        removing = false;
    }

    private static boolean canPlaceAt(World worldIn, BlockPos origin, boolean ignoreOrigin) {
        if (origin.getY() < 0 || origin.getY() + 1 >= worldIn.getHeight()) {
            return false;
        }
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos p = origin.add(dx, dy, dz);
                    if (ignoreOrigin && p.equals(origin)) {
                        continue;
                    }
                    IBlockState state = worldIn.getBlockState(p);
                    if (!state.getBlock().isReplaceable(worldIn, p)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeParts(World worldIn, BlockPos origin) {
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                for (int dz = 0; dz < 2; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    BlockPos p = origin.add(dx, dy, dz);
                    worldIn.setBlockState(p, ModBlocks.BIG_JAR_PART.getDefaultState(), 3);
                    TileEntity te = worldIn.getTileEntity(p);
                    if (te instanceof TileBigJarPart) {
                        ((TileBigJarPart) te).setMaster(origin);
                    }
                }
            }
        }
    }

    static boolean tryAddBrain(World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) {
            return false;
        }
        if (!(held.getItem() instanceof ItemBlock)) {
            return false;
        }
        ItemBlock itemBlock = (ItemBlock) held.getItem();
        if (itemBlock.getBlock() != BlocksTC.jarBrain) {
            return false;
        }

        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileBigJar)) {
            return false;
        }
        TileBigJar jar = (TileBigJar) te;
        if (jar.getBrainCount() >= TileBigJar.MAX_BRAINS) {
            if (!worldIn.isRemote) {
                jar.sync();
            }
            return true;
        }

        if (!worldIn.isRemote) {
            if (!jar.addBrain()) {
                return true;
            }
            if (!player.capabilities.isCreativeMode) {
                ItemStack normalJar = new ItemStack(BlocksTC.jarNormal);
                if (held.getCount() == 1) {
                    player.setHeldItem(hand, normalJar);
                } else {
                    held.shrink(1);
                    if (!player.inventory.addItemStackToInventory(normalJar)) {
                        player.dropItem(normalJar, false);
                    }
                }
                player.inventory.markDirty();
                player.inventoryContainer.detectAndSendChanges();
            }
        }
        return true;
    }
}
