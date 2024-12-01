package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class ItemRingRunicCharge extends Item implements IBauble {

    public ItemRingRunicCharge() {
        this.setUnlocalizedName("ring_runic_charge");
        this.setRegistryName("ring_runic_charge");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.TOOLS); 
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            entityPlayer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 0, true, true));
            entityPlayer.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 0, true, true));
        }
    }
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}
