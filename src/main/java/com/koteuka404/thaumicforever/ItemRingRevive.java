package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRingRevive extends Item implements IBauble {
    public ItemRingRevive() {
        setUnlocalizedName("ring_revive");
        setRegistryName("ring_revive");
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override public void onWornTick(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {}
    @Override public void onEquipped(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {}
    @Override public void onUnequipped(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {}
    @Override public boolean canEquip(ItemStack stack, net.minecraft.entity.EntityLivingBase player) { return true; }
    @Override public boolean canUnequip(ItemStack stack, net.minecraft.entity.EntityLivingBase player) { return true; }
}
