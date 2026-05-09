package com.koteuka404.thaumicforever.item;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ItemPrimalPickaxe extends ItemPickaxe {

    // Harvest level 10 as requested.
    private static final ToolMaterial PRIMAL_PICKAXE_MATERIAL =
        EnumHelper.addToolMaterial("TF_PRIMAL_PICKAXE", 10, 4096, 14.0F, 10.0F, 30);

    public ItemPrimalPickaxe() {
        super(PRIMAL_PICKAXE_MATERIAL);
        setRegistryName("primal_pickaxe");
        setUnlocalizedName("primal_pickaxe");
        setMaxStackSize(1);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }
}
