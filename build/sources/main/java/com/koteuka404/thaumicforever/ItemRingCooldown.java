
package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRingCooldown extends Item implements IBauble {

    public ItemRingCooldown() {
        setUnlocalizedName("ring_cooldown");
        setRegistryName("ring_cooldown");
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
