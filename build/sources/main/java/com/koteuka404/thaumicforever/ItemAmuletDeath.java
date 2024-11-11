package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemAmuletDeath extends Item implements IBauble {

    public ItemAmuletDeath() {
        setUnlocalizedName("amulet_death");
        setRegistryName("amulet_death");
        setMaxStackSize(1); // Це амулет, тому лише один у стеку
        setCreativeTab(ThaumicForever.CREATIVE_TAB); // Вибираємо правильну вкладку
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET; // Визначаємо, що це амулет
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        // Метод, який викликається, коли амулет знято
        if (player instanceof EntityPlayer && !player.world.isRemote) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            // Наносимо гравцеві смертельну шкоду
            entityPlayer.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        }
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        // Тут можна додати логіку для роботи амулета, поки він надітий
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; // Амулет буде мати візуальний ефект (як наче він зачарований)
    }
}
