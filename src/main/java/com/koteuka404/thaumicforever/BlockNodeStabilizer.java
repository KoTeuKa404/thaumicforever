package com.koteuka404.thaumicforever;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNodeStabilizer extends BlockContainer{
    public BlockNodeStabilizer() {
        super(Material.ROCK);
        this.setRegistryName("node_stabilizer");
        this.setUnlocalizedName("node_stabilizer");
        this.setHardness(2.0F);
        this.setResistance(20.0F);
    }
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNodeStabilizer();
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState stat) {
        return false;
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return null;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            for (int dy = 1; dy <= 3; dy++) {
                TileEntity te = world.getTileEntity(pos.up(dy));
                if (te instanceof TileNodeTransducer) {
                    ((TileNodeTransducer) te).onStabilizerBroken();
                    break;
                }
            }
        }
        super.breakBlock(world, pos, state);
    }
}
