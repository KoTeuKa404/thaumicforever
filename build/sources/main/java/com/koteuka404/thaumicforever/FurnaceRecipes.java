package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.items.ItemsTC;
public class FurnaceRecipes {

    public static void init() {
        // Додавання рецептів переплавки для руд ANCIENT

        // Переплавка ANCIENT_IRON дає 3 залізних слитка
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_IRON, new ItemStack(net.minecraft.init.Items.IRON_INGOT, 3), 0.7F);

        // Переплавка ANCIENT_GOLD дає 3 золотих слитка
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_GOLD, new ItemStack(net.minecraft.init.Items.GOLD_INGOT, 3), 1.0F);

        // Переплавка ANCIENT_COAL дає 4 шматка вугілля
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_COAL, new ItemStack(net.minecraft.init.Items.COAL, 4), 0.7F);

        // Переплавка ANCIENT_CINNABAR дає 3 ртуті (Mercury) з Thaumcraft
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_CINNABAR, new ItemStack(ItemsTC.quicksilver, 3), 1.0F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_AMBER, new ItemStack(ItemsTC.amber, 3), 1.0F);

        GameRegistry.addSmelting(ModOreBlocks.LEAD_ORE, new ItemStack(ModItems.LEAD_INGOT), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.SILVER_ORE, new ItemStack(ModItems.SILVER_INGOT), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.TIN_ORE, new ItemStack(ModItems.TIN_INGOT), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.COPPER_ORE, new ItemStack(ModItems.COPPER_INGOT), 0.7F);

        // Переплавка ANCIENT руд у відповідні матеріали
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_IRON, new ItemStack(Items.IRON_INGOT, 3), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_GOLD, new ItemStack(Items.GOLD_INGOT, 3), 1.0F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_COAL, new ItemStack(Items.COAL, 4), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_CINNABAR, new ItemStack(ItemsTC.quicksilver, 3), 1.0F);

        // Переплавка AQUAREIA_ORE в Aquareia Gem
        GameRegistry.addSmelting(ModOreBlocks.AQUAREIA_ORE, new ItemStack(ModItems.AQUAREIA_GEM), 1.0F);
        GameRegistry.addSmelting(new ItemStack(Blocks.STONE, 1, 0), new ItemStack(ModOreBlocks.SMOOTH_STONE), 0.0F);


        
    }
}
