package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemZombieHeart extends ItemFood {

    public ItemZombieHeart() {
        super(2, 0.1F, false);
        setUnlocalizedName("zombie_heart");  
        setRegistryName("zombie_heart");    

        setAlwaysEdible(); 
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.POISON, 80, 1)); 
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
        }
    }
}
