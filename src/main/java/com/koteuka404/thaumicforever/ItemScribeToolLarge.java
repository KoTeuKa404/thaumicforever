
package com.koteuka404.thaumicforever;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;
import thaumcraft.api.items.IScribeTools;

public class ItemScribeToolLarge extends Item implements IScribeTools {

    public ItemScribeToolLarge() {
        this.setMaxStackSize(1);
        this.setMaxDamage(800);
        this.setUnlocalizedName("scribing_tools_large");
        this.setRegistryName("scribing_tools_large");

    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

   
}
