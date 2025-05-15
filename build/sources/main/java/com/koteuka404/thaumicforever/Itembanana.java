package com.koteuka404.thaumicforever;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class Itembanana extends ItemFood {

    public Itembanana() {
        super(4, 0.3F, false);  
        
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32; 
    } 
}
