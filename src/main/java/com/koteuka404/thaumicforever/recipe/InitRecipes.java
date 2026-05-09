package com.koteuka404.thaumicforever.recipe;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.items.ItemsTC;
public class InitRecipes {

    public static void initInfusionRecipes() {
        InfusionEnchantmentRecipeFM IEVOIDREPAIR = new InfusionEnchantmentRecipeFM(EnumInfusionEnchantment.VOIDREPAIR, (new AspectList()).add(Aspect.ELDRITCH, 50).add(Aspect.DARKNESS, 50).add(Aspect.VOID, 50).add(Aspect.MAGIC, 30),
        new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)), new ItemStack(ItemsTC.ingots,1,1), new ItemStack(ModItems.MAGIC_DUST), new ItemStack(ItemsTC.ingots,1,1));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:IEVOIDREPAIR"), IEVOIDREPAIR);
        ThaumcraftApi.addFakeCraftingRecipe(
            new ResourceLocation("thaumicforever:FAKE_VOIDREPAIR"),
            new InfusionRecipe(
                "NEWINFUSION",
                makePreviewOutput(Items.DIAMOND_CHESTPLATE, EnumInfusionEnchantment.VOIDREPAIR),
                6,
                new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.DARKNESS, 50).add(Aspect.VOID, 50).add(Aspect.MAGIC, 30),
                makeFakeCentral(Items.DIAMOND_CHESTPLATE),
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.ingots,1,1),
                    new ItemStack(ModItems.MAGIC_DUST),
                    new ItemStack(ItemsTC.ingots,1,1)
                }
            )
        );
      
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
        ThaumcraftApi.addFakeCraftingRecipe(
            new ResourceLocation("thaumicforever:FAKE_POISON"),
            new InfusionRecipe(
                "NEWINFUSION",
                makePreviewOutput(Items.IRON_SWORD, EnumInfusionEnchantment.POISON),
                4,
                new AspectList().add(Aspect.DEATH, 40).add(Aspect.AVERSION, 40).add(Aspect.MAGIC, 30),
                makeFakeCentral(Items.IRON_SWORD),
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Items.SPIDER_EYE),
                    new ItemStack(ModItems.MAGIC_DUST),
                    new ItemStack(Items.NETHER_WART)
                }
            )
        );
        InfusionEnchantmentRecipeFM IERUBYPROTECT = new InfusionEnchantmentRecipeFM(
            EnumInfusionEnchantment.RUBYPROTECT,
            new AspectList().add(Aspect.PROTECT, 40).add(Aspect.FIRE, 30).add(Aspect.MAGIC, 30),
            new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)),new ItemStack(ModItems.ruby_gem),new ItemStack(ModItems.MAGIC_DUST),new ItemStack(ModItems.ruby_gem));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:IERUBYPROTECT"),IERUBYPROTECT);
        ThaumcraftApi.addFakeCraftingRecipe(
            new ResourceLocation("thaumicforever:FAKE_RUBYPROTECT"),
            new InfusionRecipe(
                "NEWINFUSION",
                makePreviewOutput(Items.DIAMOND_LEGGINGS, EnumInfusionEnchantment.RUBYPROTECT),
                5,
                new AspectList().add(Aspect.PROTECT, 40).add(Aspect.FIRE, 30).add(Aspect.MAGIC, 30),
                makeFakeCentral(Items.DIAMOND_LEGGINGS),
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ModItems.ruby_gem),
                    new ItemStack(ModItems.MAGIC_DUST),
                    new ItemStack(ModItems.ruby_gem)
                }
            )
        );

        InfusionEnchantmentRecipeFM IEBLEEDING = new InfusionEnchantmentRecipeFM(
            EnumInfusionEnchantment.BLEEDING,
            new AspectList()
                .add(Aspect.DEATH, 45)
                .add(Aspect.AVERSION, 35)
                .add(Aspect.LIFE, 20)
                .add(Aspect.MAGIC, 25),
            new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)),
            new ItemStack(ModBlocks.RED_ROSE),
            new ItemStack(ItemsTC.quicksilver),
            new ItemStack(ModItems.MAGIC_DUST),
            new ItemStack(Items.GHAST_TEAR)
        );
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:IEBLEEDING"), IEBLEEDING);
        ThaumcraftApi.addFakeCraftingRecipe(
            new ResourceLocation("thaumicforever:FAKE_BLEEDING"),
            new InfusionRecipe(
                "NEWINFUSION",
                makePreviewOutput(Items.DIAMOND_SWORD, EnumInfusionEnchantment.BLEEDING),
                5,
                new AspectList().add(Aspect.DEATH, 45).add(Aspect.AVERSION, 35).add(Aspect.LIFE, 20).add(Aspect.MAGIC, 25),
                makeFakeCentral(Items.DIAMOND_SWORD),
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ModBlocks.RED_ROSE),
                    new ItemStack(ItemsTC.quicksilver),
                    new ItemStack(ModItems.MAGIC_DUST),
                    new ItemStack(Items.GHAST_TEAR)
                }
            )
        );

        Item avaritiaPearlItem = Loader.isModLoaded("avaritia") ? Item.getByNameOrId("avaritia:extremely_primordial_pearl") : null;
        Ingredient unbreakablePearl = avaritiaPearlItem != null
            ? Ingredient.fromStacks(new ItemStack(avaritiaPearlItem))
            : Ingredient.fromItem(ItemsTC.primordialPearl);

        InfusionEnchantmentRecipeFM IEUNBREAKABLE = new InfusionEnchantmentRecipeFM(
            EnumInfusionEnchantment.UNBREAKABLE,
            new AspectList()
                .add(Aspect.PROTECT, 120)
                .add(Aspect.ORDER, 100)
                .add(Aspect.EARTH, 80)
                .add(Aspect.MAGIC, 80),
            new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)),
            new ItemStack(ModItems.primalingot),
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl,
            unbreakablePearl
        );
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:IEUNBREAKABLE"), IEUNBREAKABLE);
        ThaumcraftApi.addFakeCraftingRecipe(
            new ResourceLocation("thaumicforever:FAKE_UNBREAKABLE"),
            new InfusionRecipe(
                "NEWINFUSION",
                makePreviewOutput(Items.DIAMOND_PICKAXE, EnumInfusionEnchantment.UNBREAKABLE),
                10,
                new AspectList().add(Aspect.PROTECT, 120).add(Aspect.ORDER, 100).add(Aspect.EARTH, 80).add(Aspect.MAGIC, 80),
                makeFakeCentral(Items.DIAMOND_PICKAXE),
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ModItems.primalingot),
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl,
                    unbreakablePearl
                }
            )
        );

    }

    private static ItemStack makeFakeCentral(net.minecraft.item.Item item) {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("tf_fake_recipe_only", true);
        stack.setTagCompound(tag);
        return stack;
    }

    private static ItemStack makePreviewOutput(net.minecraft.item.Item item, EnumInfusionEnchantment ench) {
        ItemStack out = new ItemStack(item);
        EnumInfusionEnchantment.addInfusionEnchantment(out, ench, 1);
        return out;
    }
}      
