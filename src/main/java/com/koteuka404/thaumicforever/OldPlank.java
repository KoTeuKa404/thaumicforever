package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

class OldPlank extends Block {

    public OldPlank() {
        super(Material.WOOD);
        setUnlocalizedName("old_plank");
        setRegistryName("old_plank");
        setHardness(4.0F); 
        setResistance(3.0F);
        setHarvestLevel("axe", 0);
        setSoundType(SoundType.WOOD);
    }
}
