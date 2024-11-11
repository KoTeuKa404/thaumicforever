package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

public class InfusionEnchantmentRecipeFM extends InfusionRecipe {

    public final EnumInfusionEnchantment enchantment;

    public InfusionEnchantmentRecipeFM(EnumInfusionEnchantment enchantment, AspectList aspects, Object... components) {
        super(enchantment.researchKey, null, 6, aspects, ItemStack.EMPTY, components);
        this.enchantment = enchantment;
    }

    @Override
    public Object getRecipeOutput(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
        if (input == null || input.isEmpty()) {
            return input; // Return the original item if no valid output
        }

        // Copy the input item so we can apply the enchantment to the copy
        ItemStack output = input.copy();

        // Get the current level of the enchantment on the item
        int currentLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(output, this.enchantment);

        // If the current level is already at the maximum, return the original item
        if (currentLevel >= this.enchantment.maxLevel) {
            return input;
        }

        // Apply the enchantment to the item with the new level
        EnumInfusionEnchantment.addInfusionEnchantment(output, this.enchantment, currentLevel + 1);

        // Return the modified item with the NBT tag
        return output;
    }
}
