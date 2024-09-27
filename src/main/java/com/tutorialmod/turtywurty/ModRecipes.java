package com.tutorialmod.turtywurty;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

public class ModRecipes {

    public static void init() {
        // Створення безформленного арканного рецепту для створення скляної пляшки з блоку скла
        ShapelessArcaneRecipe glassBottleRecipe = new ShapelessArcaneRecipe(
            new ResourceLocation("thaumicforever", "glass_bottle_from_glass"), // Ідентифікатор рецепту
            "NEWALKIMIA", // Ключ для ресерчу
            0, // Вартість Vis
            new AspectList(), // Потрібні аспекти (в даному випадку 0 аспектів)
            new ItemStack(Items.GLASS_BOTTLE), // Результат рецепту
            new Object[] {new ItemStack(Blocks.GLASS)} // Інгредієнт для крафту (блок скла)
        );

        // Реєстрація рецепту для скляної пляшки
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "glass_bottle_from_glass"), glassBottleRecipe);

        // Створення рецепту для "Deconstruction Table"
        ShapelessArcaneRecipe deconstructionTableRecipe = new ShapelessArcaneRecipe(
            new ResourceLocation("thaumicforever", "deconstruction_table"), // Ідентифікатор рецепту
            "FIRSTSTEPS", // Вимога дослідження
            50, // Вартість Vis
            new AspectList().add(Aspect.ENTROPY, 20),
            new ItemStack(ModBlocks.DECONSTRUCTION_TABLE),  // Результат рецепту
            new Object[] {
                new ItemStack(Items.GOLDEN_AXE), // Золотий Лопата
                new ItemStack(Items.GOLDEN_PICKAXE), // Золотий Кірка
                new ItemStack(BlocksTC.tableWood), // Дерев'яний стіл з Thaumcraft
            }
        );

        // Реєстрація рецепту для Deconstruction Table
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "deconstruction_table"), deconstructionTableRecipe);

        // Рецепт для Brass Gear
        ShapedArcaneRecipe brassGearRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "ItemBrassGear"), // Ім'я рецепту
            "BASEARTIFICE", // Назва дослідження
            30, // Потрібно 25 одиниць vis
            new AspectList().add(Aspect.FIRE, 10), // Потрібні кристали аспекту FIRE (10 одиниць)
            new ItemStack(ModItems.ItemBrassGear), // Результат (ItemBrassGear)
            " B ", // Верхній ряд: [ ] [brass] [ ]
            "BIB", // Середній ряд: [brass] [iron] [brass]
            " B ", // Нижній ряд: [ ] [brass] [ ]
            'B', new ItemStack(ItemsTC.ingots, 1, 2), // 'B' — це злиток латуні
            'I', new ItemStack(ItemsTC.plate,1,1)
        );

        // Реєстрація рецепту
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "ItemBrassGear"), brassGearRecipe);

        // Рецепт для Thaumium Gear
        ShapedArcaneRecipe ItemThaumiumGearRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "ItemThaumiumGear"), // Ім'я рецепту
            "BASEARTIFICE", // Назва дослідження
            30, // Потрібно 25 одиниць vis
            new AspectList().add(Aspect.AIR, 10), // Потрібні кристали аспекту FIRE (10 одиниць)
            new ItemStack(ModItems.ItemThaumiumGear), // Результат (ItemThaumiumGear)
            " B ", // Верхній ряд: [ ] [brass] [ ]
            "BIB", // Середній ряд: [brass] [iron] [brass]
            " B ", // Нижній ряд: [ ] [brass] [ ]
            'B', new ItemStack(ItemsTC.ingots, 1, 0), // 'B' — це злиток Thaumium
            'I', new ItemStack(ItemsTC.plate,1,1)
        );

        // Реєстрація рецепту
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "ItemThaumiumGear"), ItemThaumiumGearRecipe);

        // Рецепт для Void Gear
        ShapedArcaneRecipe ItemVoidGearRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "ItemVoidGear"), // Ім'я рецепту
            "BASEARTIFICE", // Назва дослідження
            30, // Потрібно 25 одиниць vis
            new AspectList().add(Aspect.ORDER, 10), // Потрібні кристали аспекту FIRE (10 одиниць)
            new ItemStack(ModItems.ItemVoidGear), // Результат (ItemVoidGear)
            " B ", // Верхній ряд: [ ] [void] [ ]
            "BIB", // Середній ряд: [void] [iron] [void]
            " B ", // Нижній ряд: [ ] [void] [ ]
            'B', new ItemStack(ItemsTC.ingots, 1, 1), // 'B' — це Void Ingot
            'I', new ItemStack(ItemsTC.plate,1,1)
        );

        // Реєстрація рецепту
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "ItemVoidGear"), ItemVoidGearRecipe);



        ShapedArcaneRecipe mechanism_improvedRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "mechanism_improved"), // Ім'я рецепту
            "NEWARTIFICE", // Назва дослідження
            100, // Потрібно 25 одиниць vis
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.FIRE, 10).add(Aspect.AIR, 10), // Потрібні кристали аспекту FIRE (10 одиниць)
            new ItemStack(ModItems.mechanism_improved), // Результат (ItemVoidGear)
            " B ", // Верхній ряд: [ ] [void] [ ]
            " I ", // Середній ряд: [void] [iron] [void]
            " C ", // Нижній ряд: [ ] [void] [ ]
            'B', new ItemStack(ItemsTC.mechanismSimple), // 'B' — це Void Ingot
            'I', new ItemStack(ItemsTC.mechanismComplex),
            'C', new ItemStack(ModItems.ItemBrassGear)

        );

        // Реєстрація рецепту
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "mechanism_improved"), mechanism_improvedRecipe);

        
    }
}
