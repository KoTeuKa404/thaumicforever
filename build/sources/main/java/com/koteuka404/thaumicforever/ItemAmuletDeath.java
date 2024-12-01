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
        setMaxStackSize(1); 
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET; 
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer && !player.world.isRemote) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            entityPlayer.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        }
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; 
    }
}
