package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRingRegeneration extends Item implements IBauble {

    public ItemRingRegeneration() {
        setUnlocalizedName("ring_regeneration");
        setRegistryName("ring_regeneration");
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) { }

    @Override public void onEquipped(ItemStack stack, EntityLivingBase player) { }
    @Override public void onUnequipped(ItemStack stack, EntityLivingBase player) { }
    @Override public boolean canEquip(ItemStack stack, EntityLivingBase player) { return true; }
    @Override public boolean canUnequip(ItemStack stack, EntityLivingBase player) { return true; }
}
