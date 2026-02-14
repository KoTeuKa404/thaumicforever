package com.koteuka404.thaumicforever.wand.api.item.wand;

import com.koteuka404.thaumicforever.wand.api.item.IFractionalVis;
import net.minecraft.item.ItemStack;

public interface IWand extends IFractionalVis {
    public IWandCap getCap(ItemStack stack);

    public IWandRod getRod(ItemStack stack);
}
