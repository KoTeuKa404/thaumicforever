package com.koteuka404.thaumicforever.block;

import java.util.Random;

import com.koteuka404.thaumicforever.tile.TileEntityTemporalAccelerator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTemporalAccelerator extends Block {

    public BlockTemporalAccelerator() {
        super(Material.ROCK);
        setUnlocalizedName("temporal_accelerator");
        setRegistryName("temporal_accelerator");
        setHardness(2.0F);
        setResistance(10.0F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTemporalAccelerator();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }
}
