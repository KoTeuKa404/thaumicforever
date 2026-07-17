package com.koteuka404.thaumicforever.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;

public class ItemPrimalBlade extends ItemSword {
    private static final float ATTACK_DAMAGE = 23.0F;
    private static final float ATTACK_SPEED = -3.2F;

    private final Multimap<String, AttributeModifier> attributeModifiers;

    public ItemPrimalBlade() {
        super(ToolMaterial.DIAMOND);
        this.setRegistryName("primal_blade");
        this.setUnlocalizedName("primal_blade");
        this.setMaxStackSize(1);

        ImmutableMultimap.Builder<String, AttributeModifier> modifiers = ImmutableMultimap.builder();
        modifiers.put(
            SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
            new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ATTACK_DAMAGE, 0)
        );
        modifiers.put(
            SharedMonsterAttributes.ATTACK_SPEED.getName(),
            new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0)
        );
        this.attributeModifiers = modifiers.build();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return slot == EntityEquipmentSlot.MAINHAND
            ? this.attributeModifiers
            : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public float getAttackDamage() {
        return ATTACK_DAMAGE;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.LIGHT_PURPLE + super.getItemStackDisplayName(stack);
    }
}
