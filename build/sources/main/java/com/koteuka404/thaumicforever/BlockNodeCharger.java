package com.koteuka404.thaumicforever;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNodeCharger extends BlockContainer {
    public BlockNodeCharger() {
        super(Material.ROCK);
        setRegistryName("node_transducer ");
        setUnlocalizedName("node_transducer");
        setHardness(2.0F);
        setResistance(20.0F);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this); 
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos,
                                            EnumFacing side, float hitX,
                                            float hitY, float hitZ,
                                            int meta, EntityLivingBase placer,
                                            EnumHand hand) {
        return getDefaultState();
    }

    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state)   { return false; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileInvertedBuffNodeStabilizer();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileInvertedBuffNodeStabilizer();
    }
}
