package com.koteuka404.thaumicforever;

import net.minecraft.item.Item;

public class ItemBrokenAmulet extends Item {

    public ItemBrokenAmulet() {
        setUnlocalizedName("broken_amulet");
        setRegistryName("broken_amulet");
        setMaxStackSize(1);  // Теж унікальний предмет, тому 1 у стеку
    }
}
