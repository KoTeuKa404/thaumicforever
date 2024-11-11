package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BowlTreatMeat extends ItemFood {

    public BowlTreatMeat() {
        super(20, 1.0F, false); // 20 одиниць голоду, повне насичення
        setRegistryName("bowl_treatmeat");
        setUnlocalizedName("bowl_treatmeat");
        setAlwaysEdible(); // Завжди можна з'їсти, навіть при повному голоді
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        // Додаємо ефект регенерації 3 на 7 секунд
        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 7 * 20, 2)); // Регенерація III
        }

        // Повертаємо порожню дерев'яну миску
        if (!player.inventory.addItemStackToInventory(new ItemStack(Items.BOWL))) {
            player.dropItem(new ItemStack(Items.BOWL), false);
        }
    }
}
