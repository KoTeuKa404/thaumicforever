package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import com.koteuka404.thaumicforever.tile.TileBigJarPart;

public class BlockBigJarPart extends BlockContainer {
    private static final AxisAlignedBB BIG_JAR_PART_AABB = FULL_BLOCK_AABB;

    public BlockBigJarPart() {
        super(Material.GLASS);
        setUnlocalizedName("big_jar_part");
        setRegistryName("big_jar_part");
        setHardness(0.4F);
        setResistance(1.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BIG_JAR_PART_AABB;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBigJarPart();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileBigJarPart();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
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
    public String getUnlocalizedName() {
        return ModBlocks.BIG_JAR.getUnlocalizedName();
    }

    @Override
    public String getLocalizedName() {
        return ModBlocks.BIG_JAR.getLocalizedName();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        BlockPos master = te instanceof TileBigJarPart ? ((TileBigJarPart) te).getMaster() : null;
        if (master == null || worldIn.getBlockState(master).getBlock() != ModBlocks.BIG_JAR) {
            master = findMaster(worldIn, pos);
        }
        if (master == null) {
            return isBrainJar(player.getHeldItem(hand));
        }
        return BlockBigJar.tryAddBrain(worldIn, master, player, hand);
    }

    private static BlockPos findMaster(World worldIn, BlockPos pos) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dy = -1; dy <= 0; dy++) {
                for (int dz = -1; dz <= 0; dz++) {
                    BlockPos candidate = pos.add(dx, dy, dz);
                    if (worldIn.getBlockState(candidate).getBlock() == ModBlocks.BIG_JAR) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isBrainJar(ItemStack stack) {
        return !stack.isEmpty()
            && stack.getItem() instanceof ItemBlock
            && ((ItemBlock) stack.getItem()).getBlock() == BlocksTC.jarBrain;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileBigJarPart) {
            BlockPos master = ((TileBigJarPart) te).getMaster();
            if (master != null) {
                BlockBigJar.removeStructure(worldIn, master, true);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canDropFromExplosion(net.minecraft.world.Explosion explosion) {
        return false;
    }

    @Override
    public int quantityDropped(java.util.Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        return null;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ModBlocks.BIG_JAR);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(ModBlocks.BIG_JAR);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }
}
