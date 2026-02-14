package com.koteuka404.thaumicforever.wand.api.item.wand;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IWandUpdate {

    public void onUpdate(ItemStack stack, EntityPlayer player);

}
