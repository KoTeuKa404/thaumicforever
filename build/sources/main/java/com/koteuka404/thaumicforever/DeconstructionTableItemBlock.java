package com.koteuka404.thaumicforever;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class DeconstructionTableItemBlock extends ItemBlock {
    public DeconstructionTableItemBlock() {
        super(ModBlocks.DECONSTRUCTION_TABLE);
        setRegistryName(ModBlocks.DECONSTRUCTION_TABLE.getRegistryName());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + ThaumicForever.MODID + ".deconstruction_table";
    }
}
