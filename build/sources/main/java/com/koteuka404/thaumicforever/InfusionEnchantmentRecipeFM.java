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
            return input; 
        }

        ItemStack output = input.copy();

        int currentLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(output, this.enchantment);

        if (currentLevel >= this.enchantment.maxLevel) {
            return input;
        }

        EnumInfusionEnchantment.addInfusionEnchantment(output, this.enchantment, currentLevel + 1);

        return output;
    }
}
