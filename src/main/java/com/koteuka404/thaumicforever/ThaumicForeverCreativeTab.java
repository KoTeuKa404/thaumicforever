package com.koteuka404.thaumicforever;

import com.koteuka404.thaumicforever.registry.ModItems;

 
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import com.koteuka404.thaumicforever.item.ItemBrassGear;

public class ThaumicForeverCreativeTab extends CreativeTabs {

    public ThaumicForeverCreativeTab() {
        super("thaumicforever_tab"); 
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.ItemBrassGear);  
    }
}
