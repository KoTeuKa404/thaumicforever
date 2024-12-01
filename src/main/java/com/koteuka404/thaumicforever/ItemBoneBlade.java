package com.koteuka404.thaumicforever;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemBoneBlade extends ItemSword {

    private static final float BASE_ATTACK_DAMAGE = 5.5F;
    private static final float ATTACK_SPEED = 1.8F;
    private static final float SKELETON_BONUS_DAMAGE = 5.5F;

    private final Multimap<String, AttributeModifier> attributeModifiers;

    public ItemBoneBlade() {
        super(ToolMaterial.IRON);
        setRegistryName("bone_blade");
        setUnlocalizedName("bone_blade");
        setMaxStackSize(1);

        // Налаштування атрибутів меча
        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", BASE_ATTACK_DAMAGE, 0));
        builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0));
        this.attributeModifiers = builder.build();
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        String entityName = target.getName().toLowerCase(); // Отримуємо назву моба
        if (entityName.contains("skelet")) { // Перевірка на "skelet" у назві
            // Додатковий урон для всіх скелетів
            float totalDamage = BASE_ATTACK_DAMAGE + SKELETON_BONUS_DAMAGE;
            target.attackEntityFrom(DamageSource.causeMobDamage(attacker), totalDamage);
        }
        stack.damageItem(1, attacker); // Знос предмета при використанні
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.GRAY + "Bone Blade";
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return slot == EntityEquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.DARK_PURPLE + "Gives special damage to any skeleton"); // Додає текст до опису
    }
}
