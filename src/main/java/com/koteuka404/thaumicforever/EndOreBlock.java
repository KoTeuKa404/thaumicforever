package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

public class EndOreBlock extends Block {

    public EndOreBlock() {
        super(Material.ROCK);
        setHardness(3.0F);
        setResistance(5.0F); 
        setHarvestLevel("pickaxe", 3); 
        setUnlocalizedName("end_ore"); 
        setRegistryName("end_ore"); 
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.end_dust; 
    }

    @Override
    public int quantityDropped(Random random) {
        return 1 + random.nextInt(2);
    }
}
