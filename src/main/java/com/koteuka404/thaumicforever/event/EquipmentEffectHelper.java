package com.koteuka404.thaumicforever.event;

import java.util.ArrayList;
import java.util.List;

import com.koteuka404.thaumicforever.entity.EntityGuardianMannequin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

final class EquipmentEffectHelper {
    private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {
        EntityEquipmentSlot.MAINHAND,
        EntityEquipmentSlot.OFFHAND,
        EntityEquipmentSlot.HEAD,
        EntityEquipmentSlot.CHEST,
        EntityEquipmentSlot.LEGS,
        EntityEquipmentSlot.FEET
    };

    private EquipmentEffectHelper() {
    }

    static boolean supportsEquipmentEffects(EntityLivingBase entity) {
        return entity instanceof EntityPlayer || entity instanceof EntityGuardianMannequin;
    }

    static Iterable<ItemStack> armor(EntityLivingBase entity) {
        return entity.getArmorInventoryList();
    }

    static Iterable<ItemStack> equippedItems(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            List<ItemStack> stacks = new ArrayList<>();
            stacks.addAll(player.inventory.mainInventory);
            stacks.addAll(player.inventory.offHandInventory);
            stacks.addAll(player.inventory.armorInventory);
            return stacks;
        }

        List<ItemStack> stacks = new ArrayList<>();
        for (EntityEquipmentSlot slot : EQUIPMENT_SLOTS) {
            stacks.add(entity.getItemStackFromSlot(slot));
        }
        return stacks;
    }
}
