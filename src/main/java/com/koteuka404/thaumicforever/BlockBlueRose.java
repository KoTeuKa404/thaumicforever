package com.koteuka404.thaumicforever;

import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;

public class BlockBlueRose extends BlockBush {
    public BlockBlueRose() {
        super(Material.PLANTS);
        setRegistryName("blue_rose");
        setUnlocalizedName("blue_rose");
    }
}