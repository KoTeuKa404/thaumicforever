package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.tile.TileEntityVoidSingularity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class BlockVoidSingularity extends Block {

    public BlockVoidSingularity() {
        super(Material.ROCK);
        setUnlocalizedName("void_singularity");
        setRegistryName("void_singularity");
        setHardness(1.5F);
        setResistance(120.0F);
        setLightLevel(0.1F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityVoidSingularity();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }
}
