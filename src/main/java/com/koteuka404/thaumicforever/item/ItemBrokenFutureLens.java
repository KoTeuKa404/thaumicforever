package com.koteuka404.thaumicforever.item;

import java.util.List;

import com.koteuka404.thaumicforever.registry.ModItems;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class ItemBrokenFutureLens extends Item implements IBauble {

    public ItemBrokenFutureLens() {
        setUnlocalizedName("broken_future_lens");
        setRegistryName("broken_future_lens");
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.HEAD;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase entity) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    public static boolean isEquipped(EntityPlayer player) {
        if (player == null) {
            return false;
        }

        IItemHandler baubles = BaublesApi.getBaublesHandler(player);

        if (baubles == null) {
            return false;
        }

        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);

            if (!stack.isEmpty() && stack.getItem() == ModItems.BROKEN_FUTURE_LENS) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + "Shows possible drops above nearby mobs");
        tooltip.add(TextFormatting.DARK_GRAY + "A cracked glimpse into the creature's future");
        super.addInformation(stack, world, tooltip, flag);
    }
}