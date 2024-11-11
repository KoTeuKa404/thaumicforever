package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTimeStop extends Block {

    public BlockTimeStop() {
        super(Material.GLASS);
        setHardness(20.0F); 
        setResistance(100.0F); 
        setLightOpacity(1); 
        setUnlocalizedName("time_stop_block");
        setRegistryName("time_stop_block");
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    @Override
    public void updateTick(World world, BlockPos pos, net.minecraft.block.state.IBlockState state, java.util.Random rand) {
        world.setBlockToAir(pos);
    }
}

