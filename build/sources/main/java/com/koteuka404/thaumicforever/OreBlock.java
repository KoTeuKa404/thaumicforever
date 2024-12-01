package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class OreBlock extends Block {

    public OreBlock(String name, float hardness, float resistance, int lightLevel) {
        super(Material.ROCK);
        setUnlocalizedName(ThaumicForever.MODID + "." + name);
        setRegistryName(ThaumicForever.MODID, name);
        setHardness(hardness);
        setResistance(resistance);
        setLightLevel(lightLevel / 15.0F);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.STONE);
        setCreativeTab(ThaumicForever.CREATIVE_TAB);  
    }
}

