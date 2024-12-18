package com.koteuka404.thaumicforever;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemEternalBlade extends ItemSword {

    private static final long COOLDOWN_TIME = 45 * 1000; // 45 секунд у мілісекундах
    private static final float ATTACK_DAMAGE = 23.0F;    // Урон меча
    private static final float ATTACK_SPEED = -2.34F;    // Швидкість атаки меча
    private final Multimap<String, AttributeModifier> attributeModifiers;
    private long lastUseTime = 0;

    public ItemEternalBlade() {
        super(ToolMaterial.DIAMOND);
        setRegistryName("eternal_blade");
        setUnlocalizedName("eternal_blade");
        setMaxStackSize(1);

        // Налаштування атрибутів атаки
        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ATTACK_DAMAGE, 0));
        builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0));
        this.attributeModifiers = builder.build();
    }

    @Override
public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (!world.isRemote) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUseTime >= COOLDOWN_TIME) {
            lastUseTime = currentTime;

            // Викликаємо ефект активації
            CustomEventHandler.startParticleWave(world, player);

            // Відтворення звуку
            world.playSound(null, player.posX, player.posY, player.posZ,
                    net.minecraft.init.SoundEvents.ITEM_FIRECHARGE_USE,
                    net.minecraft.util.SoundCategory.PLAYERS,
                    1.0F, 1.0F); // Гучність 1.0F, висота звуку 1.0F

            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        } else {
            // Повідомлення про те, що меч ще на кулдауні
            long remainingTime = (COOLDOWN_TIME - (currentTime - lastUseTime)) / 1000;
            player.sendStatusMessage(new TextComponentString(
                    TextFormatting.RED + "Eternal Blade is on cooldown. Wait " + remainingTime + " seconds."), true);
        }
    }
    return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
}


    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);

        // Перевірка: чи це гравець, і чи меч знаходиться в головній руці
        if (world.isRemote && entity instanceof EntityPlayer && isSelected) {
            EntityPlayer player = (EntityPlayer) entity;

            // Перевірка: меч у головній руці
            if (player.getHeldItemMainhand() == stack) {
                double handOffsetX = -0.4; // Відкориговане зміщення для правої сторони
                double handOffsetY = 1.0;  // Висота руки
                double handOffsetZ = -0.4;  // Зміщення вперед від гравця

                float yaw = (float) Math.toRadians(player.rotationYaw);

                // Розрахунок координат партиклів біля руки
                double particleX = player.posX + handOffsetZ * Math.sin(yaw) + handOffsetX * Math.cos(yaw);
                double particleY = player.posY + handOffsetY;
                double particleZ = player.posZ - handOffsetZ * Math.cos(yaw) + handOffsetX * Math.sin(yaw);

                // Генерація частинок
                for (int i = 0; i < 2; i++) {
                    double offsetX = (world.rand.nextDouble() - 0.5) * 0.1;
                    double offsetY = (world.rand.nextDouble() - 0.5) * 0.1;
                    double offsetZ = (world.rand.nextDouble() - 0.5) * 0.1;

                    world.spawnParticle(EnumParticleTypes.FLAME,
                            particleX + offsetX,
                            particleY + offsetY,
                            particleZ + offsetZ,
                            0, 0, 0);
                }
            }
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        // Повертаємо модифікатори атрибутів лише для головної руки
        return slot == EntityEquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker); // Зменшення міцності предмета

        // Підпалюємо ціль
        target.setFire(5); // Підпал триває 5 секунд

        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.DARK_RED + "Eternal Blade";
    }
}
