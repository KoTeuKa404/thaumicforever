package com.koteuka404.thaumicforever.wand.crafting;

import com.koteuka404.thaumicforever.wand.api.recipe.IPlayerDependentArcaneRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IArcaneRecipe;

import java.util.ArrayList;
import java.util.List;

public class ThaumicWandsCraftingManager {

    public static List<IArcaneRecipe> findMatchingArcaneRecipes(InventoryCrafting matrix, EntityPlayer player) {
        List<IArcaneRecipe> matches = new ArrayList<>();
        for (ResourceLocation key : CraftingManager.REGISTRY.getKeys()) {
            IRecipe recipe = CraftingManager.REGISTRY.getObject(key);
            if (recipe == null) continue;
            if (recipe instanceof IPlayerDependentArcaneRecipe) {
                if (recipe instanceof IArcaneRecipe && ((IPlayerDependentArcaneRecipe) recipe).matches(matrix, player.world, player)) {
                    IArcaneRecipe ar = (IArcaneRecipe) recipe;
                    if (ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(ar.getResearch())) {
                        matches.add(ar);
                    }
                }
            } else if (recipe instanceof IArcaneRecipe && recipe.matches(matrix, player.world)) {
                IArcaneRecipe ar = (IArcaneRecipe) recipe;
                if (ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(ar.getResearch())) {
                    matches.add(ar);
                }
            }
        }
        return matches;
    }

    public static IArcaneRecipe findMatchingArcaneRecipe(InventoryCrafting matrix, EntityPlayer player) {
        List<IArcaneRecipe> matches = findMatchingArcaneRecipes(matrix, player);
        return matches.isEmpty() ? null : matches.get(0);
    }
}
