package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

public class RubyOre extends Block {

    public RubyOre() {
        super(Material.ROCK);
        setUnlocalizedName("ruby_ore");
        setRegistryName("ruby_ore");
        setHardness(3.5F);
        setResistance(6.0F);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.STONE);
        setLightLevel(0.5F); 
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.ruby_gem;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1 + random.nextInt(2); 
    }
}
