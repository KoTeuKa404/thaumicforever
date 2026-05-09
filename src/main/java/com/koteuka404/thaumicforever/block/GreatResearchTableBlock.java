package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.registry.ModGuiHandler;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.tile.TileGreatResearchTable;
import com.koteuka404.thaumicforever.ThaumicForever;

public class GreatResearchTableBlock extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public GreatResearchTableBlock() {
        super(Material.WOOD);
        setUnlocalizedName(ThaumicForever.MODID + ".great_research_table");
        setRegistryName(ThaumicForever.MODID, "great_research_table");
        setHardness(2.5F);
        setResistance(12.5F);
        setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing dir = placer.getHorizontalFacing().getOpposite();
        BlockPos sidePos = pos.offset(dir.rotateY());
        if (!worldIn.isAirBlock(sidePos) && !worldIn.getBlockState(sidePos).getMaterial().isReplaceable()) {
            worldIn.scheduleUpdate(pos, this, 1);
            return getDefaultState().withProperty(FACING, dir);
        }
        if (!worldIn.isRemote) {
            worldIn.setBlockState(sidePos, ModBlocks.GREAT_RESEARCH_TABLE_SIDE.getDefaultState().withProperty(GreatResearchTableSideBlock.FACING, dir), 3);
        }
        return getDefaultState().withProperty(FACING, dir);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, java.util.Random rand) {
        EnumFacing dir = state.getValue(FACING);
        BlockPos sidePos = pos.offset(dir.rotateY());
        if (!(worldIn.getBlockState(sidePos).getBlock() instanceof GreatResearchTableSideBlock)) {
            worldIn.setBlockToAir(pos);
            spawnAsEntity(worldIn, pos, new ItemStack(this));
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileGreatResearchTable) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileGreatResearchTable) te);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        EnumFacing dir = state.getValue(FACING);
        BlockPos sidePos = pos.offset(dir.rotateY());
        if (worldIn.getBlockState(sidePos).getBlock() instanceof GreatResearchTableSideBlock) {
            GreatResearchTableSideBlock.SKIP_MAIN_REMOVAL = true;
            try {
                worldIn.setBlockToAir(sidePos);
            } finally {
                GreatResearchTableSideBlock.SKIP_MAIN_REMOVAL = false;
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGreatResearchTable();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(ThaumicForever.instance, ModGuiHandler.GREAT_RESEARCH_TABLE_GUI, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
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
}
