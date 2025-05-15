package com.koteuka404.thaumicforever.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.koteuka404.thaumicforever.DeconstructionTableTileEntity;
import com.koteuka404.thaumicforever.ModBlocks;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;

@JEIPlugin
public class JEIPluginHandler implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(new DeconstructionRecipeCategory(guiHelper));
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.DECONSTRUCTION_TABLE), DeconstructionRecipeCategory.UID);

        DeconstructionTableTileEntity dummy = new DeconstructionTableTileEntity();
        Map<Item, ItemStack[]> recipeMap = dummy.getRecipes();
        Map<Item, Float> chanceMap = dummy.getChances();

        List<DeconstructionRecipeWrapper> recipes = new ArrayList<>();

        for (Map.Entry<Item, ItemStack[]> entry : recipeMap.entrySet()) {
            Item inputItem = entry.getKey();
            ItemStack input = new ItemStack(inputItem);
            List<ItemStack> outputs = Arrays.asList(entry.getValue());
            float chance = chanceMap.getOrDefault(inputItem, 1.0f);

            recipes.add(new DeconstructionRecipeWrapper(input, outputs, chance));
        }

        // BOOKSHELF (out oldResearch)
        if (!dummy.isOldResearchLoaded()) {
            for (int meta = 0; meta < 6; meta++) {
                recipes.add(new DeconstructionRecipeWrapper(
                    new ItemStack(Blocks.BOOKSHELF),
                    Collections.singletonList(new ItemStack(ItemsTC.curio, 1, meta)),
                    chanceMap.getOrDefault(Item.getItemFromBlock(Blocks.BOOKSHELF), 0.6f)
                ));
            }
        }

        registry.addRecipes(recipes, DeconstructionRecipeCategory.UID);
    }
}
