package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

public class PrimalBlock extends Block {

    public PrimalBlock() {
        super(Material.IRON);
        setUnlocalizedName("primal_block");
        setRegistryName("primal_block");
        setHardness(15.0F);
        setResistance(40.0F);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this); 
    }

    @Override
    public int quantityDropped(Random random) {
        return 1; 
    }
}
