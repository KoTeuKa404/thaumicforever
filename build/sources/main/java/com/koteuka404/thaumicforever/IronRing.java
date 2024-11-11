package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IronRing extends Item implements IBauble {


    public IronRing() {
        this.setUnlocalizedName("iron_ring");
        this.setRegistryName("iron_ring");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.TOOLS); // або інший таб
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    
}
