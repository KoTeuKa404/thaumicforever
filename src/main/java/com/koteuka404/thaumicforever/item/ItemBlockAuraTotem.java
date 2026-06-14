package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.block.BlockAuraTotem;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockAuraTotem extends ItemBlock {
    public ItemBlockAuraTotem(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + BlockAuraTotem.TotemType.byMeta(stack.getMetadata()).getName();
    }
}
