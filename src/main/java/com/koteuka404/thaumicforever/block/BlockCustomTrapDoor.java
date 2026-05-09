package com.koteuka404.thaumicforever.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockCustomTrapDoor extends BlockTrapDoor {

    public BlockCustomTrapDoor(String name) {
        super(Material.WOOD);
        setRegistryName(name);
        setUnlocalizedName(name);
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.WOOD);
    }
}
