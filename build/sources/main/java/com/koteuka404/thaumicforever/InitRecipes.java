package com.koteuka404.thaumicforever;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.items.ItemsTC;

public class InitRecipes {

    public static void initInfusionRecipes() {
        ItemStack diamondChestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);

        // Add the VOIDREPAIR infusion enchantment to the diamond chestplate
        EnumInfusionEnchantment.addInfusionEnchantment(diamondChestplate, EnumInfusionEnchantment.VOIDREPAIR, 1);

        // Create the infusion recipe for VOIDREPAIR on a diamond chestplate
        ThaumcraftApi.addInfusionCraftingRecipe(
            new ResourceLocation("thaumicforever:void_repair_diamond_chestplate"),
            new InfusionRecipe(
                "NEWINFUSION", // Research key
                diamondChestplate, // Central item to enchant with VOIDREPAIR
                6, // Instability level
                new AspectList()
                    .add(Aspect.ELDRITCH, 50)
                    .add(Aspect.DARKNESS, 50)
                    .add(Aspect.VOID, 50)
                    .add(Aspect.MAGIC, 30),  // Aspects for the infusion
                new ItemStack(Items.DIAMOND_CHESTPLATE),  // Base central ingredient
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.ingots,1,1), 
                    new ItemStack(ModItems.MAGIC_DUST), 
                    new ItemStack(ItemsTC.ingots,1,1)
      
                }
            )
        );

        
        ItemStack voidu = new ItemStack(Items.GOLDEN_SWORD);
        EnumInfusionEnchantment.addInfusionEnchantment(voidu, EnumInfusionEnchantment.VOIDREPAIR, 1);
        InfusionEnchantmentRecipeFM IEVOIDREPAIR = new InfusionEnchantmentRecipeFM(EnumInfusionEnchantment.VOIDREPAIR, (new AspectList()).add(Aspect.ELDRITCH, 50).add(Aspect.DARKNESS, 50).add(Aspect.VOID, 50).add(Aspect.MAGIC, 30),
        new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)), new ItemStack(ItemsTC.ingots,1,1), new ItemStack(ModItems.MAGIC_DUST), new ItemStack(ItemsTC.ingots,1,1));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:IEVOIDREPAIR"), IEVOIDREPAIR);
    }
}


        // ItemStack greedy = new ItemStack(Items.GOLDEN_SWORD);
        // EnumInfusionEnchantmentFM.addInfusionEnchantment(greedy, EnumInfusionEnchantmentFM.GREEDY, 1);
        // InfusionEnchantmentRecipeFM IEGREEDY = new InfusionEnchantmentRecipeFM(EnumInfusionEnchantmentFM.GREEDY, (new AspectList()).add(Aspect.EARTH, 20).add(Aspect.ORDER, 20).add(Aspect.ENTROPY, 50), 
        // new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)), new ItemStack(BlocksTC.hungryChest), new ItemStack(Items.EMERALD));
        // ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("forbiddenmagicre:IEGREEDY"), IEGREEDY);
        // ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("forbiddenmagicre:IEGREEDYFAKE"), new InfusionEnchantmentRecipeFM(IEGREEDY, greedy));

