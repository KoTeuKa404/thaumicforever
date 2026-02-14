package com.koteuka404.thaumicforever.wand.item;

import com.koteuka404.thaumicforever.ThaumicForever;
import net.minecraft.item.Item;

public class ItemBase extends Item {

    public ItemBase(String name) {
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(ThaumicForever.CREATIVE_TAB);
        TW_Items.ITEMS.add(this);
    }

}
