package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

class RubyBlock extends Block {

    public RubyBlock() {
        super(Material.IRON);
        setUnlocalizedName("ruby_block");
        setRegistryName("ruby_block");
        setHardness(5.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.METAL);
    }
}