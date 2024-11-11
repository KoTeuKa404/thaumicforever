// ItemEternalBlade.java
package com.koteuka404.thaumicforever;

import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEternalBlade extends ItemSword {

    private static final float ATTACK_DAMAGE = 23.0F;
    private static final float ATTACK_SPEED = -2.34F; // Відповідає швидкості атаки 1.66

    private final Multimap<String, AttributeModifier> attributeModifiers;

    public ItemEternalBlade() {
        super(ToolMaterial.DIAMOND); // Використовуємо базовий матеріал
        setRegistryName("eternal_blade");
        setUnlocalizedName("eternal_blade");
        setMaxStackSize(1);

        // Налаштовуємо атрибути для урону та швидкості атаки при створенні предмета
        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ATTACK_DAMAGE, 0));
        builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0));
        this.attributeModifiers = builder.build();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            int radius = 3 + world.rand.nextInt(3); // Випадковий радіус від 3 до 5 блоків
            AxisAlignedBB area = new AxisAlignedBB(
                    player.posX - radius, player.posY - 1, player.posZ - radius,
                    player.posX + radius, player.posY + 1, player.posZ + radius
            );

            // Знаходимо всіх ворогів у радіусі
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area, entity -> entity != player);
            for (EntityLivingBase entity : entities) {
                entity.setFire(5); // Підпалюємо кожного ворога на 5 секунд
            }

            // Створюємо частинки вогню навколо гравця
            spawnFireParticles(world, player, radius);

            // Додаємо звук
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, player.getSoundCategory(), 1.0F, 1.0F);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    // Метод для створення частинок вогню
    @SideOnly(Side.CLIENT)
    private void spawnFireParticles(World world, EntityPlayer player, int radius) {
        for (int i = 0; i < 100; i++) {
            double angle = Math.toRadians(i * (360.0 / 100)); // 100 частинок для кола
            double x = player.posX + radius * Math.cos(angle);
            double z = player.posZ + radius * Math.sin(angle);
            double y = player.posY;

            world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0.05, 0);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    // Метод для налаштування червоного імені предмета
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.DARK_RED + "Eternal Blade";
    }

    // Перезаписуємо атрибути для урону та швидкості атаки
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return slot == EntityEquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot, stack);
    }
}
