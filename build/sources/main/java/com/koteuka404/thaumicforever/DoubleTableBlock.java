package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DoubleTableBlock extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public DoubleTableBlock() {
        super(Material.WOOD);
        setUnlocalizedName(ThaumicForever.MODID + ".double_table");
        setHardness(2.5F);
        setResistance(12.5F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing horizontalFacing = placer.getHorizontalFacing();
        BlockPos secondPartPos = pos.offset(horizontalFacing.rotateYCCW());

        if (!worldIn.isAirBlock(secondPartPos) && !worldIn.getBlockState(secondPartPos).getMaterial().isReplaceable()) {
            worldIn.scheduleUpdate(pos, this, 1); 
            return this.getDefaultState().withProperty(FACING, horizontalFacing.getOpposite());        
        }

        if (!worldIn.isRemote) {
            worldIn.setBlockState(secondPartPos, ModBlocks.INVISIBLE_PART.getDefaultState(), 3);
        }

        return this.getDefaultState().withProperty(FACING, horizontalFacing.getOpposite()); 
       }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, java.util.Random rand) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos secondPartPos = pos.offset(facing.rotateYCCW());

        if (!worldIn.getBlockState(secondPartPos).getBlock().equals(ModBlocks.INVISIBLE_PART)) {
            worldIn.setBlockToAir(pos);
            spawnAsEntity(worldIn, pos, new ItemStack(this));
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos secondPartPos = pos.offset(facing.rotateYCCW()); 

        if (worldIn.getBlockState(secondPartPos).getBlock().equals(ModBlocks.INVISIBLE_PART)) {
            worldIn.setBlockToAir(secondPartPos);
        } else {
            for (EnumFacing direction : EnumFacing.HORIZONTALS) {
                BlockPos possibleMainBlockPos = pos.offset(direction);
                IBlockState possibleMainBlockState = worldIn.getBlockState(possibleMainBlockPos);

                if (possibleMainBlockState.getBlock() instanceof DoubleTableBlock) {
                    worldIn.setBlockToAir(possibleMainBlockPos); 
                    break;
                }
            }
        }

        super.breakBlock(worldIn, pos, state); 
    }



    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.util.BlockRenderLayer getBlockLayer() {
        return net.minecraft.util.BlockRenderLayer.CUTOUT; 
    }




    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
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
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DoubleTableTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof DoubleTableTileEntity) {
                player.openGui(ThaumicForever.instance, ModGuiHandler.DOUBLE_TABLE_GUI, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
 


}