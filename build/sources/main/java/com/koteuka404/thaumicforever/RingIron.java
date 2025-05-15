package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RingIron extends Item implements IBauble {


    public RingIron() {
        this.setUnlocalizedName("ring_iron");
        this.setRegistryName("ring_iron");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.TOOLS); 
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    
}

    
