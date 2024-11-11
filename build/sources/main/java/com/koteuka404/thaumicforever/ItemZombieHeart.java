package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemZombieHeart extends ItemFood {

    public ItemZombieHeart() {
        super(2, 0.1F, false); // 2 одиниці голоду (1 повний "голодний м'ясний ніж") і низька насиченість
        setUnlocalizedName("zombie_heart");  // Локалізоване ім'я
        setRegistryName("zombie_heart");     // Реєстраційне ім'я

        // Додавання ефектів при споживанні
        setAlwaysEdible(); // Робить предмет завжди їстівним
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (!world.isRemote) {
            // Додаємо ефекти при споживанні
            player.addPotionEffect(new PotionEffect(MobEffects.POISON, 80, 1)); // Отруєння II на 4 секунди (80 тік)
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0)); // Тошнота I на 10 секунд (200 тік)
        }
    }
}
