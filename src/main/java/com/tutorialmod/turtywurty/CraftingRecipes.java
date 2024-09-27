package com.tutorialmod.turtywurty;

import com.tutorialmod.turtywurty.ModBlocks;
import com.tutorialmod.turtywurty.ModItems;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingRecipes {
    public static void init() {
        // Реєстрація рецепту для Greatwood Table
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "greatwood_table"), // Ідентифікатор рецепту
            null, // Група рецепту (null, якщо не використовується)
            new ItemStack(ModBlocks.GREATWOOD_TABLE),  // Результат рецепту
            "PPP",
            " S ",
            'P', Blocks.WOODEN_SLAB, // Дерев'яні плити
            'S', Blocks.PLANKS // Дошки
        );

        // Слиток -> Блок
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "lead_block"),
            null,
            new ItemStack(ModBlocks.LEAD_BLOCK),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.LEAD_INGOT)
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "silver_block"),
            null,
            new ItemStack(ModBlocks.SILVER_BLOCK),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.SILVER_INGOT)
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "tin_block"),
            null,
            new ItemStack(ModBlocks.TIN_BLOCK),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.TIN_INGOT)
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "copper_block"),
            null,
            new ItemStack(ModBlocks.COPPER_BLOCK),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.COPPER_INGOT)
        );

        // Блок -> Слитки
        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "lead_ingot_from_block"),
            null,
            new ItemStack(ModItems.LEAD_INGOT, 9),
            Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.LEAD_BLOCK))
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "silver_ingot_from_block"),
            null,
            new ItemStack(ModItems.SILVER_INGOT, 9),
            Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.SILVER_BLOCK))
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "tin_ingot_from_block"),
            null,
            new ItemStack(ModItems.TIN_INGOT, 9),
            Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.TIN_BLOCK))
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "copper_ingot_from_block"),
            null,
            new ItemStack(ModItems.COPPER_INGOT, 9),
            Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.COPPER_BLOCK))
        );

        // Слиток -> Нагетси
        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "lead_nugget"),
            null,
            new ItemStack(ModItems.LEAD_NUGGET, 9),
            Ingredient.fromItems(ModItems.LEAD_INGOT)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "silver_nugget"),
            null,
            new ItemStack(ModItems.SILVER_NUGGET, 9),
            Ingredient.fromItems(ModItems.SILVER_INGOT)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "tin_nugget"),
            null,
            new ItemStack(ModItems.TIN_NUGGET, 9),
            Ingredient.fromItems(ModItems.TIN_INGOT)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "copper_nugget"),
            null,
            new ItemStack(ModItems.COPPER_NUGGET, 9),
            Ingredient.fromItems(ModItems.COPPER_INGOT)
        );

        // Нагетси -> Слиток
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "lead_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.LEAD_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.LEAD_NUGGET)
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "silver_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.SILVER_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.SILVER_NUGGET)
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "tin_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.TIN_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.TIN_NUGGET)
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "copper_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.COPPER_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.COPPER_NUGGET)
        );
    }
}
