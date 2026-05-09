package com.koteuka404.thaumicforever.jei;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.koteuka404.thaumicforever.tile.DeconstructionTableTileEntity;
import com.koteuka404.thaumicforever.wand.container.ContainerArcaneWorkbenchNew;

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
    // Different TC/JEI builds have used slightly different UIDs for arcane workbench recipes.
    // Register against all common ones so our custom workbench works across packs.
    private static final List<String> ARCANE_WORKBENCH_UIDS = Arrays.asList(
        // ThaumicJEI 1.12.2 uses this exact uppercase UID in some builds.
        "THAUMCRAFT_ARCANE_WORKBENCH",
        "thaumcraft.arcane_workbench",
        "thaumcraft.arcaneworkbench",
        "thaumcraft.arcane_workbench_recipe",
        "thaumcraft.arcane",
        "thaumcraft:arcane_workbench",
        "thaumcraft:arcaneworkbench",
        "thaumcraft:arcane_workbench_recipe",
        "thaumicjei.arcane_workbench",
        "thaumicjei.arcaneworkbench",
        "thaumicjei.arcane",
        "thaumicjei:arcane_workbench",
        "thaumicjei:arcaneworkbench",
        "thaumicjei:arcane",
        "minecraft.crafting"
    );

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(new DeconstructionRecipeCategory(guiHelper));
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.DECONSTRUCTION_TABLE), DeconstructionRecipeCategory.UID);

        for (String uid : ARCANE_WORKBENCH_UIDS) {
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.WAND_WORKBENCH), uid);
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.ARCANE_WORKBENCH_WAND_CHARGER), uid);
            registry.getRecipeTransferRegistry().addRecipeTransferHandler(
                ContainerArcaneWorkbenchNew.class,
                uid,
                0, 15,     // 3x3 matrix (0-8) + crystal slots (9-14)
                17, 36     // player inventory + hotbar
            );
        }

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
