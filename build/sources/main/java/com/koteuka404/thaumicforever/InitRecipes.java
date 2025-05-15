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
        EnumInfusionEnchantment.addInfusionEnchantment(diamondChestplate, EnumInfusionEnchantment.VOIDREPAIR, 1);

        ThaumcraftApi.addInfusionCraftingRecipe(
            new ResourceLocation("thaumicforever:void_repair_diamond_chestplate"),
            new InfusionRecipe(
                "NEWINFUSION", 
                diamondChestplate,
                6, 
                new AspectList()
                    .add(Aspect.ELDRITCH, 50)
                    .add(Aspect.DARKNESS, 50)
                    .add(Aspect.VOID, 50)
                    .add(Aspect.MAGIC, 30), 
                new ItemStack(Items.DIAMOND_CHESTPLATE),  
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
      
        InfusionEnchantmentRecipeFM IEPOISON = new InfusionEnchantmentRecipeFM(
            EnumInfusionEnchantment.POISON, 
            new AspectList()
                .add(Aspect.DEATH, 40)
                .add(Aspect.AVERSION, 40)
                .add(Aspect.MAGIC, 30),
            new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)), 
            new ItemStack(Items.SPIDER_EYE), 
            new ItemStack(ModItems.MAGIC_DUST), 
            new ItemStack(Items.NETHER_WART)
        );

        ThaumcraftApi.addInfusionCraftingRecipe(
            new ResourceLocation("thaumicforever:IEPOISON"), 
            IEPOISON
        );
        ItemStack poisonedSword = new ItemStack(Items.IRON_SWORD);
        EnumInfusionEnchantment.addInfusionEnchantment(poisonedSword, EnumInfusionEnchantment.POISON, 1);
    
        ThaumcraftApi.addInfusionCraftingRecipe(
            new ResourceLocation("thaumicforever:poison_sword"),
            new InfusionRecipe(
                "NEWINFUSION", 
                poisonedSword,
                4, 
                new AspectList()
                    .add(Aspect.DEATH, 40)
                    .add(Aspect.AVERSION, 40)
                    .add(Aspect.MAGIC, 30), 
                new ItemStack(Items.IRON_SWORD),  
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Items.SPIDER_EYE), 
                    new ItemStack(ModItems.MAGIC_DUST),
                    new ItemStack(Items.NETHER_WART)
                }
            )
        );






        ItemStack rubyChestplate = new ItemStack(Items.DIAMOND_LEGGINGS);
        EnumInfusionEnchantment.addInfusionEnchantment(rubyChestplate, EnumInfusionEnchantment.RUBYPROTECT, 1);

        ThaumcraftApi.addInfusionCraftingRecipe(
            new ResourceLocation("thaumicforever:ruby_protect_diamond_chestplate"),
            new InfusionRecipe(
                "NEWINFUSION", 
                rubyChestplate, 
                5, 
                new AspectList()
                    .add(Aspect.PROTECT, 40)
                    .add(Aspect.FIRE, 30)
                    .add(Aspect.MAGIC, 30),
                new ItemStack(Items.DIAMOND_LEGGINGS), 
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ModItems.ruby_gem),
                    new ItemStack(ModItems.MAGIC_DUST),
                    new ItemStack(ModItems.ruby_gem)
                }
            )
        );
        ItemStack rubyInfused = new ItemStack(Items.IRON_CHESTPLATE); 
        EnumInfusionEnchantment.addInfusionEnchantment(rubyInfused, EnumInfusionEnchantment.RUBYPROTECT, 1);
        InfusionEnchantmentRecipeFM IERUBYPROTECT = new InfusionEnchantmentRecipeFM(
            EnumInfusionEnchantment.RUBYPROTECT,
            new AspectList().add(Aspect.PROTECT, 40).add(Aspect.FIRE, 30).add(Aspect.MAGIC, 30),
            new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)),new ItemStack(ModItems.ruby_gem),new ItemStack(ModItems.MAGIC_DUST),new ItemStack(ModItems.ruby_gem));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:IERUBYPROTECT"),IERUBYPROTECT);

        
    }
}      
