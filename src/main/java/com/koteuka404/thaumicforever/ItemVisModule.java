package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thaumcraft.api.items.IRechargable;

public class ItemVisModule extends Item implements IRechargable {
    private static final int MAX_VIS_STORAGE = 500; 

    public ItemVisModule() {
        this.setUnlocalizedName("vis_module");
        this.setRegistryName("vis_module");
        this.setMaxStackSize(1);
    }

    public static boolean hasVisModule(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("HasVisModule");
    }

    public static void addVisModule(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean("HasVisModule", true);
        if (!stack.getTagCompound().hasKey("VisStored")) {
            stack.getTagCompound().setInteger("VisStored", 0);
        }
    }

    public static int getStoredVis(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 0;
        }
        return stack.getTagCompound().getInteger("VisStored");
    }

    public static void chargeItem(ItemStack stack, int amount) {
        if (!hasVisModule(stack)) return;

        int currentVis = getStoredVis(stack);
        int newVis = Math.min(currentVis + amount, MAX_VIS_STORAGE);
        stack.getTagCompound().setInteger("VisStored", newVis);
    }

    public static boolean drainVis(ItemStack stack, int amount) {
        if (!hasVisModule(stack)) return false;

        int currentVis = getStoredVis(stack);
        if (currentVis >= amount) {
            stack.getTagCompound().setInteger("VisStored", currentVis - amount);
            return true;
        }
        return false;
    }

    @Override
    public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
        return MAX_VIS_STORAGE;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
        return EnumChargeDisplay.NORMAL;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isRemote && hasVisModule(stack)) {
            int rechargeAmount = 5;
            chargeItem(stack, rechargeAmount);
        }
    }
}
