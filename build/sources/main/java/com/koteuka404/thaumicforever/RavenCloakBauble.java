package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RavenCloakBauble extends Item implements IBauble {

    public RavenCloakBauble() {
        super();
        this.setMaxStackSize(1); // Задаємо максимальний розмір стека для предмета
        this.setUnlocalizedName("raven_cloak_bauble"); // Встановлюємо ім'я для локалізації
        this.setRegistryName("raven_cloak_bauble"); // Встановлюємо ім'я для реєстрації
        this.setCreativeTab(CreativeTabs.MISC); // Встановлюємо вкладку, у якій буде предмет
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BODY;  // Задаємо тип "BODY" для відображення на тілі
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        // Можна додати додаткову логіку для дії предмета, якщо потрібно
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        // Логіка при екіпіруванні предмета
        player.world.playSound(null, player.posX, player.posY, player.posZ, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, player.getSoundCategory(), 1.0F, 1.0F);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        // Логіка при знятті предмета
    }
}
