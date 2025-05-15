package com.koteuka404.thaumicforever;

import java.util.UUID;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemHand extends Item implements IBauble {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("5f3c3a10-1e9a-4b3d-9f2d-9c2b5a1a6e2f");
    private static final UUID TOUGHNESS_MODIFIER_UUID = UUID.fromString("d4a1b2f3-6c7d-4e8f-9a0b-1c2d3e4f5a6b");

    private static final AttributeModifier ARMOR_MOD = new AttributeModifier(
            ARMOR_MODIFIER_UUID, "HandKnightArmor", 2.0, 0);
    private static final AttributeModifier TOUGHNESS_MOD = new AttributeModifier(
            TOUGHNESS_MODIFIER_UUID, "HandKnightToughness", 1.0, 0);

    public ItemHand() {
        setUnlocalizedName("hand_knight");
        setRegistryName("hand_knight");
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.TRINKET;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR).getModifier(ARMOR_MODIFIER_UUID) == null) {
            player.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(ARMOR_MOD);
        }
        if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getModifier(TOUGHNESS_MODIFIER_UUID) == null) {
            player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).applyModifier(TOUGHNESS_MOD);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR).getModifier(ARMOR_MODIFIER_UUID) != null) {
            player.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MOD);
        }
        if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getModifier(TOUGHNESS_MODIFIER_UUID) != null) {
            player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).removeModifier(TOUGHNESS_MOD);
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }
}
