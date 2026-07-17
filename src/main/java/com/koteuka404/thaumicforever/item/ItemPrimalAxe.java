package com.koteuka404.thaumicforever.item;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

public class ItemPrimalAxe extends ItemAxe {

    private static final ToolMaterial PRIMAL_AXE_MATERIAL =
        EnumHelper.addToolMaterial("TF_PRIMAL_AXE", 10, 4096, 14.0F, 10.0F, 30);

    public ItemPrimalAxe() {
        super(PRIMAL_AXE_MATERIAL, 17.0F, -3.2F);
        setRegistryName("primal_axe");
        setUnlocalizedName("primal_axe");
        setMaxStackSize(1);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.LIGHT_PURPLE + super.getItemStackDisplayName(stack);
    }
}
