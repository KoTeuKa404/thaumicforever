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
        this.setMaxStackSize(1); 
        this.setUnlocalizedName("raven_cloak_bauble");
        this.setRegistryName("raven_cloak_bauble");
        this.setCreativeTab(CreativeTabs.MISC); 
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BODY; 
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        player.world.playSound(null, player.posX, player.posY, player.posZ, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, player.getSoundCategory(), 1.0F, 1.0F);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    }
}
