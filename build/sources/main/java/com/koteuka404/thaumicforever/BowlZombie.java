package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BowlZombie extends ItemFood {

    public BowlZombie() {
        super(3, 0.3F, false);
        setRegistryName("bowl_zombie");
        setUnlocalizedName("bowl_zombie");
        setAlwaysEdible(); 
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 14 * 20, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 10 * 20, 0)); 
        }

        if (!player.inventory.addItemStackToInventory(new ItemStack(Items.BOWL))) {
            player.dropItem(new ItemStack(Items.BOWL), false);
        }
    }
}
