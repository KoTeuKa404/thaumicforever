package com.koteuka404.thaumicforever;

 
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ThaumicForeverCreativeTab extends CreativeTabs {

    public ThaumicForeverCreativeTab() {
        super("thaumicforever_tab"); 
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.ItemBrassGear);  
    }
}
