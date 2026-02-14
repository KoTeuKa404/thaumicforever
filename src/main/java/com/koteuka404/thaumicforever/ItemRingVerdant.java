package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.List;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;

public class ItemRingVerdant extends Item implements IBauble {

    private static final int COOLDOWN_TICKS = 800;

    public ItemRingVerdant() {
        setUnlocalizedName("ring_verdant");
        setRegistryName("ring_verdant");
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        if (player.world.isRemote) return;

        if (player.getCooldownTracker().hasCooldown(this)) return;

        List<Potion> toRemove = new ArrayList<>();
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            if (effect != null && effect.getPotion() != null && effect.getPotion().isBadEffect()) {
                toRemove.add(effect.getPotion());
            }
        }

        if (!toRemove.isEmpty()) {
            for (Potion p : toRemove) {
                player.removePotionEffect(p);
            }

            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            player.world.playSound(
                null,
                player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS,
                0.5F, 1.0F
            );
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
