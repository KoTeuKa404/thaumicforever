package com.koteuka404.thaumicforever.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import com.koteuka404.thaumicforever.potion.PotionVampirism;

public class ItemArtifactRubyRing extends Item implements IBauble {

    public ItemArtifactRubyRing() {
        setUnlocalizedName("artifact_ruby_ring");
        setRegistryName("artifact_ruby_ring");
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (player.world.isRemote) {
            return;
        }
        // Keeps vampirism active while the ring is worn.
        player.addPotionEffect(new PotionEffect(PotionVampirism.INSTANCE, 220, 0, true, true));
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote) {
            player.removePotionEffect(PotionVampirism.INSTANCE);
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
