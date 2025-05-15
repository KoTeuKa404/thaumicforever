package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.items.ItemsTC;
public class FurnaceRecipes {

    public static void init() {

        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_IRON, new ItemStack(net.minecraft.init.Items.IRON_INGOT, 3), 0.7F);

        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_GOLD, new ItemStack(net.minecraft.init.Items.GOLD_INGOT, 3), 1.0F);

        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_COAL, new ItemStack(net.minecraft.init.Items.COAL, 4), 0.7F);

        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_CINNABAR, new ItemStack(ItemsTC.quicksilver, 3), 1.0F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_AMBER, new ItemStack(ItemsTC.amber, 3), 1.0F);

        GameRegistry.addSmelting(ModOreBlocks.LEAD_ORE, new ItemStack(ModItems.LEAD_INGOT), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.SILVER_ORE, new ItemStack(ModItems.SILVER_INGOT), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.TIN_ORE, new ItemStack(ModItems.TIN_INGOT), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.COPPER_ORE, new ItemStack(ModItems.COPPER_INGOT), 0.7F);

        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_IRON, new ItemStack(Items.IRON_INGOT, 3), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_GOLD, new ItemStack(Items.GOLD_INGOT, 3), 1.0F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_COAL, new ItemStack(Items.COAL, 4), 0.7F);
        GameRegistry.addSmelting(ModOreBlocks.ANCIENT_CINNABAR, new ItemStack(ItemsTC.quicksilver, 3), 1.0F);

        GameRegistry.addSmelting(ModOreBlocks.AQUAREIA_ORE, new ItemStack(ModItems.AQUAREIA_GEM), 1.0F);
        GameRegistry.addSmelting(new ItemStack(Blocks.STONE, 1, 0), new ItemStack(ModOreBlocks.SMOOTH_STONE), 0.0F);

        GameRegistry.addSmelting(new ItemStack(ModItems.CLUSTER, 1, 4), new ItemStack(ItemsTC.amber,3), 0.0F);


        ThaumcraftApi.addSmeltingBonus(new ItemStack(Item.getByNameOrId("industrialupgrade:raw_metals"), 1, 18), new ItemStack(Items.IRON_NUGGET, 3));

        ThaumcraftApi.addSmeltingBonus(new ItemStack(ModItems.CLUSTER, 1, 4),new ItemStack(ItemsTC.amber, 1));
        
    }
}
