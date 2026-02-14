package com.koteuka404.thaumicforever.wand.main;

import com.koteuka404.thaumicforever.wand.item.TW_Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class CreativeTabTW extends CreativeTabs {
    public CreativeTabTW(int par1, String par2Str) {
        super(par1, par2Str);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(TW_Items.itemWandCap, 1, 0);
    }
}
