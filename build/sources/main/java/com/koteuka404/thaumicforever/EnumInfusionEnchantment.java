package com.koteuka404.thaumicforever;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public enum EnumInfusionEnchantment {
    VOIDREPAIR(ImmutableSet.of("weapon", "axe", "pickaxe", "shovel", "armor"), 1, "NEWINFUSION");

    public final Set<String> applicableItems;
    public final int maxLevel;
    public final String researchKey;

    EnumInfusionEnchantment(Set<String> applicableItems, int maxLevel, String researchKey) {
        this.applicableItems = applicableItems;
        this.maxLevel = maxLevel;
        this.researchKey = researchKey;
    }

    public static NBTTagList getInfusionEnchantmentTagList(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return null;
        }
        return stack.getTagCompound().getTagList("infenchtf", 10);
    }

    public static void addInfusionEnchantment(ItemStack stack, EnumInfusionEnchantment enchantment, int level) {
        if (stack == null || stack.isEmpty() || level > enchantment.maxLevel) {
            return;
        }

        NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        if (nbttaglist == null) {
            nbttaglist = new NBTTagList();
        }

        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
            if (tag.getShort("id") == (short) enchantment.ordinal()) {
                return;
            }
        }

        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short) enchantment.ordinal());
        nbttagcompound.setShort("lvl", (short) level);
        nbttaglist.appendTag(nbttagcompound);

        stack.setTagInfo("infenchtf", nbttaglist);
    }

    public static int getInfusionEnchantmentLevel(ItemStack stack, EnumInfusionEnchantment enchantment) {
        NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        if (nbttaglist != null) {
            for (int i = 0; i < nbttaglist.tagCount(); i++) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                short id = nbttagcompound.getShort("id");
                if (id == enchantment.ordinal()) {
                    return nbttagcompound.getShort("lvl");
                }
            }
        }
        return 0;
    }
}
