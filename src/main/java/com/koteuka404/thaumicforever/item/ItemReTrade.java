package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.entity.EntityVoidTraider;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ItemReTrade extends Item {
    public ItemReTrade() {
        setUnlocalizedName("re_trade");
        setRegistryName("re_trade");
        setMaxStackSize(16);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player,
                                            EntityLivingBase target, EnumHand hand) {
        if (!(target instanceof EntityVoidTraider) || target.world.isRemote) {
            return false;
        }

        ((EntityVoidTraider) target).rerollSaleItems();
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return true;
    }
}
