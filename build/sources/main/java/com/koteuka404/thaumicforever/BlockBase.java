package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

public class BlockBase extends Block {

    public BlockBase(Material material, String name) {
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);
        setHardness(5.0F); // Міцність на рівні заліза
        setResistance(10.0F); // Опір вибухам на рівні заліза
        setHarvestLevel("pickaxe", 2); // Необхідний рівень інструменту - залізна кирка
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // Повертає предмет, який випадає при зламуванні блоку
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1; // Кількість предметів, які випадають при ламанні
    }
}
