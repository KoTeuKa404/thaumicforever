package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

public class CraftingRecipes {
    public static void init() {
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "greatwood_table"),
            null,
            new ItemStack(ModBlocks.GREATWOOD_TABLE),
            "PPP",
            " S ",
            'P', Blocks.WOODEN_SLAB,
            'S', Blocks.PLANKS
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "RubyOre"),
            null,
            new ItemStack(ModBlocks.RubyBlock),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.ruby_gem)
        );
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "Rubyuncraft"),
            null,
            new ItemStack(ModItems.ruby_gem,9),
            "I",
            'I', Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.RubyBlock))
        );

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
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "gold_plate"),
            null,
            new ItemStack(ModItems.GOLD_PLATE,3),
            "III",
            'I', Ingredient.fromItems(Items.GOLD_INGOT)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "lead_ingot_from_block"),
            null,
            new ItemStack(ModItems.LEAD_INGOT, 9),
            Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.LEAD_BLOCK))
        );
        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "bowl_treatmeat"),
            null,
            new ItemStack(ModItems.BOWL_TREATMEAT),
            Ingredient.fromItems(Items.BOWL),
            Ingredient.fromItems(ItemsTC.tripleMeatTreat),
            Ingredient.fromItems(ItemsTC.tripleMeatTreat),
            Ingredient.fromItems(ItemsTC.tripleMeatTreat)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "bowl_zombie"),
            null,
            new ItemStack(ModItems.BOWL_ZOMBIE),
            Ingredient.fromItems(Items.BOWL),
            Ingredient.fromItems(ModItems.ItemZombieHeart),
            Ingredient.fromItems(Items.ROTTEN_FLESH)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "dye_from_red"),
            null,
            new ItemStack(Items.DYE, 1, 1),
            Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.RED_ROSE))
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "dye_from_blue"),
            null,
            new ItemStack(Items.DYE, 1, 12),
            Ingredient.fromItems(Item.getItemFromBlock(ModBlocks.BLUE_ROSE))
        );
// // // // // // // // // // // // // // // // // // // // // // // // // // // //
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

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "lead_nugget"),
            null,
            new ItemStack(ItemsTC.nuggets, 9,4),
            Ingredient.fromItems(ModItems.LEAD_INGOT)
        );
// // // // // // // // // // // // // // // // // // // // // // // // // // // //

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "silver_nugget"),
            null,
            new ItemStack(ItemsTC.nuggets, 9,3),
            Ingredient.fromItems(ModItems.SILVER_INGOT)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "tin_nugget"),
            null,
            new ItemStack(ItemsTC.nuggets, 9,2),
            Ingredient.fromItems(ModItems.TIN_INGOT)
        );

        GameRegistry.addShapelessRecipe(
            new ResourceLocation("thaumicforever", "copper_nugget"),
            null,
            new ItemStack(ItemsTC.nuggets, 9,1),
            Ingredient.fromItems(ModItems.COPPER_INGOT)
        );
        

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "lead_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.LEAD_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromStacks(new ItemStack(ItemsTC.nuggets, 1, 4))
            );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "silver_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.SILVER_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromStacks(new ItemStack(ItemsTC.nuggets, 1, 3))
        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "tin_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.TIN_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromStacks(new ItemStack(ItemsTC.nuggets, 1, 2))
            );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "copper_ingot_from_nuggets"),
            null,
            new ItemStack(ModItems.COPPER_INGOT),
            "III", "III", "III",
            'I', Ingredient.fromStacks(new ItemStack(ItemsTC.nuggets, 1, 1))
            );
// // // // // // // // // // // // // // // // // // // // // // // // // // // //       

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "iron_ring"),
            null,
            new ItemStack(ModItems.IRONRING),
            " I ", "I I", " I ",
            'I', Ingredient.fromItems(Items.IRON_NUGGET)
        );
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "ring_iron"),
            null,
            new ItemStack(ModItems.RingIron),
            "III", "I I", "III",
            'I', Ingredient.fromItems(Items.IRON_NUGGET)
        );
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "amulet_fix"),
            null,
            new ItemStack(ModItems.ZOMBIE_HEART_AMULET),
            "IA",
            'I', Ingredient.fromItems(ModItems.BROKEN_AMULET),
            'A', Ingredient.fromItems(ModItems.ItemZombieHeart)

        );

        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "aquareiagoggles"),
            null,
            new ItemStack(ModItems.ItemAquareiaGoggles),
            "IAI",
            'I', Ingredient.fromItems(ModItems.AQUAREIA_GEM),
            'A', Ingredient.fromItems(ItemsTC.goggles)
        );
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "primal_block"),
            null,
            new ItemStack(ModBlocks.PRIMALBLOCK),
            "III", "III", "III",
            'I', Ingredient.fromItems(ModItems.primalingot)
        );
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "taintFeature"),
            null,
            new ItemStack(BlocksTC.taintFeature),
            "II", "II", 
            'I', Ingredient.fromItems(ModItems.taint_slime)
        );
        GameRegistry.addShapedRecipe(
            new ResourceLocation("thaumicforever", "wood_pressure_plate_from_any_log"),
            null,
            new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 4),
            "LL",
            'L',"logWood"
        );

    }
}
