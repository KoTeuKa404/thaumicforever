package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;

public class ItemRingVerdant extends Item implements IBauble {

    private static final int COOLDOWN_TICKS = 800;

    public ItemRingVerdant() {
        this.setUnlocalizedName("ring_verdant");
        this.setRegistryName("ring_verdant");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.TOOLS); 
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getCooldownTracker().hasCooldown(this)) {
                return;
            }

            boolean hadBadEffects = false;
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getPotion().isBadEffect()) {
                    player.removePotionEffect(effect.getPotion());
                    hadBadEffects = true;
                }
            }

            if (hadBadEffects) {
                player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS); 
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC; 
    }
}
