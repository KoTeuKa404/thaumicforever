package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.registry.ModGuiHandler;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.tile.DoubleTableTileEntity;
import com.koteuka404.thaumicforever.ThaumicForever;

public class GreatResearchTableSideBlock extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    static boolean SKIP_MAIN_REMOVAL = false;

    public GreatResearchTableSideBlock() {
        super(Material.WOOD);
        setUnlocalizedName(ThaumicForever.MODID + ".great_research_table");
        setRegistryName(ThaumicForever.MODID, "great_research_table_side");
        setHardness(2.5F);
        setResistance(12.5F);
        setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (findMain(worldIn, pos) == null) {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.scheduleUpdate(pos, this, 1);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (SKIP_MAIN_REMOVAL) {
            super.breakBlock(worldIn, pos, state);
            return;
        }

        BlockPos mainPos = findMain(worldIn, pos);
        if (mainPos != null) {
            worldIn.setBlockToAir(mainPos);
            EntityItem drop = new EntityItem(worldIn, mainPos.getX() + 0.5D, mainPos.getY() + 0.5D, mainPos.getZ() + 0.5D, new ItemStack(ModBlocks.GREAT_RESEARCH_TABLE));
            worldIn.spawnEntity(drop);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DoubleTableTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        BlockPos mainPos = findMain(world, pos);
        if (mainPos != null) {
            player.openGui(ThaumicForever.instance, ModGuiHandler.GREAT_RESEARCH_TABLE_GUI, world, mainPos.getX(), mainPos.getY(), mainPos.getZ());
        }
        return true;
    }

    private BlockPos findMain(World world, BlockPos pos) {
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            BlockPos neighbor = pos.offset(dir);
            IBlockState state = world.getBlockState(neighbor);
            if (state.getBlock() instanceof GreatResearchTableBlock) {
                return neighbor;
            }
        }
        return null;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canDropFromExplosion(net.minecraft.world.Explosion explosion) {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ModBlocks.GREAT_RESEARCH_TABLE);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(ModBlocks.GREAT_RESEARCH_TABLE);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }
}
