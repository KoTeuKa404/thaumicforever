package com.koteuka404.thaumicforever.recipe;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public enum EnumInfusionEnchantment {
    VOIDREPAIR(ImmutableSet.of("any"), 1, "NEWINFUSION"),
    POISON(ImmutableSet.of("weapon", "axe", "pickaxe", "shovel"), 7, "NEWINFUSION"),
    RUBYPROTECT(ImmutableSet.of("armor"), 15, "NEWINFUSION"),
    BLEEDING(ImmutableSet.of("weapon", "axe", "pickaxe", "shovel"), 5, "NEWINFUSION"),
    UNBREAKABLE(ImmutableSet.of("any"), 1, "NEWINFUSION"); 


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
            return new NBTTagList(); 
        }
        return stack.getTagCompound().getTagList("infenchtf", 10);
    }
    
    public static void addInfusionEnchantment(ItemStack stack, EnumInfusionEnchantment enchantment, int level) {
        if (stack == null || stack.isEmpty() || level > enchantment.maxLevel) {
            return;
        }
    
        NBTTagList nbttaglist = getInfusionEnchantmentTagList(stack);
        boolean found = false;
    
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
            if (tag.getShort("id") == (short) enchantment.ordinal()) {
                int currentLevel = tag.getShort("lvl");
                tag.setShort("lvl", (short) Math.min(level, enchantment.maxLevel)); 
                found = true;
                break;
            }
        }
    
        if (!found) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short) enchantment.ordinal());
            nbttagcompound.setShort("lvl", (short) level);
            nbttaglist.appendTag(nbttagcompound);
        }
    
        stack.setTagInfo("infenchtf", nbttaglist);

        if (enchantment == UNBREAKABLE && level > 0) {
            NBTTagCompound root = stack.getTagCompound();
            if (root == null) {
                root = new NBTTagCompound();
            }
            root.setBoolean("Unbreakable", true);
            int hideFlags = root.hasKey("HideFlags") ? root.getInteger("HideFlags") : 0;
            // Hide vanilla "Unbreakable" tooltip line, keep custom infusion tooltip line.
            root.setInteger("HideFlags", hideFlags | 4);
            stack.setTagCompound(root);
        }

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
