package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.block.BlockAuraTotemPole;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockAuraTotemPole extends ItemBlock {
    public ItemBlockAuraTotemPole(Block block) {
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
        return "tile.aura_totem." + BlockAuraTotemPole.PoleType.byMeta(stack.getMetadata()).getName();
    }
}
