package com.koteuka404.thaumicforever.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

public class ItemPrimalShovel extends ItemSpade {

    private static final float ATTACK_DAMAGE = 11.0F;
    private static final float ATTACK_SPEED = -2.8F;
    private static final ToolMaterial PRIMAL_SHOVEL_MATERIAL =
        EnumHelper.addToolMaterial("TF_PRIMAL_SHOVEL", 10, 4096, 14.0F, 9.5F, 30);
    private final Multimap<String, AttributeModifier> attributeModifiers;

    public ItemPrimalShovel() {
        super(PRIMAL_SHOVEL_MATERIAL);
        ImmutableMultimap.Builder<String, AttributeModifier> modifiers = ImmutableMultimap.builder();
        modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
            new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ATTACK_DAMAGE, 0));
        modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
            new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0));
        this.attributeModifiers = modifiers.build();
        setRegistryName("primal_shovel");
        setUnlocalizedName("primal_shovel");
        setMaxStackSize(1);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return slot == EntityEquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.LIGHT_PURPLE + super.getItemStackDisplayName(stack);
    }
}
