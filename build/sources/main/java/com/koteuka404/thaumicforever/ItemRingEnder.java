package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRingEnder extends Item implements IBauble {

    private static final int TELEPORT_COOLDOWN_TICKS = 1600; // 80 секунд
    private float lastHealth = -1.0f; // Для збереження попереднього стану здоров'я гравця

    public ItemRingEnder() {
        this.setUnlocalizedName("ring_ender");
        this.setRegistryName("ring_ender");
        this.setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            // Перевіряємо чи працює перезарядка
            if (player.getCooldownTracker().hasCooldown(this)) {
                return; // Якщо кільце на перезарядці, нічого не робимо
            }

            // Перевіряємо, чи гравець отримав урон і чи його здоров'я менше 4 HP
            if (lastHealth > 0 && player.getHealth() < lastHealth && player.getHealth() <= 4.0f) {
                // Телепортуємо гравця
                if (teleportRandomly(player)) {
                    // Встановлюємо перезарядку
                    player.getCooldownTracker().setCooldown(this, TELEPORT_COOLDOWN_TICKS);

                    // Додаємо неуязвимість на 1 секунду
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20, 5, false, false));
                }
            }

            // Оновлюємо останній стан здоров'я
            lastHealth = player.getHealth();
        }
    }

    private boolean teleportRandomly(EntityPlayer player) {
        World world = player.world;
        double x = player.posX + (player.getRNG().nextDouble() - 0.5D) * 64.0D;
        double y = player.posY + (double)(player.getRNG().nextInt(64) - 32);
        double z = player.posZ + (player.getRNG().nextDouble() - 0.5D) * 64.0D;

        return teleportTo(player, x, y, z);
    }

    private boolean teleportTo(EntityPlayer player, double x, double y, double z) {
        World world = player.world;
        BlockPos blockpos = new BlockPos(x, y, z);

        while (y > 0.0D && !world.getBlockState(blockpos).getMaterial().blocksMovement()) {
            y--;
            blockpos = new BlockPos(x, y, z);
        }

        // Якщо позиція телепортації не валідна, повертаємо false
        if (!world.getBlockState(blockpos).getMaterial().blocksMovement()) {
            return false;
        }

        // Телепортація гравця
        player.setPositionAndUpdate(x, y, z);
        world.playSound(null, player.prevPosX, player.prevPosY, player.prevPosZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);
        player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);

        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC; // Встановлення рідкості на EPIC
    }
}
