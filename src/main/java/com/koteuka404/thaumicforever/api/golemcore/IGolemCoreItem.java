package com.koteuka404.thaumicforever.api.golemcore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;

/**
 * Implement this on an item to allow right-click installation onto golems.
 */
public interface IGolemCoreItem {
    ResourceLocation getGolemCoreId(ItemStack stack);

    default boolean canInstallCore(EntityPlayer player, IGolemAPI golem, ItemStack stack) {
        return true;
    }

    default void onCoreInstalled(EntityPlayer player, IGolemAPI golem, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
    }
}
