package com.koteuka404.thaumicforever;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class magic_dust extends Item {
    public magic_dust() {
        this.setUnlocalizedName("magic_dust");
        this.setRegistryName("magic_dust");
            }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE; 
    }
}
    