package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

public class CoinVillagerProfession {
    public static final VillagerRegistry.VillagerProfession COIN_VILLAGER_PROFESSION = new VillagerRegistry.VillagerProfession(
    "thaumicforever:coin_villager",
    "thaumicforever:textures/entity/moneychanger.png",
    "thaumicforever:textures/entity/zombie_villager.png"
);


    public static void registerTrades() {
        VillagerRegistry.VillagerCareer career = new VillagerRegistry.VillagerCareer(COIN_VILLAGER_PROFESSION, "coin_trader");

        career.addTrade(1, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 2),
                    new ItemStack(Items.GOLD_INGOT, 3)
                ));
            }
        });
        career.addTrade(1, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 1),
                    new ItemStack(BlocksTC.saplingGreatwood,4)
                ));
            }
        });
        career.addTrade(1, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 1),
                    new ItemStack(BlocksTC.saplingSilverwood,1)
                ));
            }
        });
        career.addTrade(2, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 2),
                    new ItemStack(BlocksTC.vishroom, 1)
                ));
            }
        });
        career.addTrade(2, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 4),
                    new ItemStack(ItemsTC.baubles,1,3)
                ));
            }
        });
        career.addTrade(2, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 3),
                    new ItemStack(ItemsTC.pechWand, 1)
                ));
            }
        });
        career.addTrade(3, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 3),
                    new ItemStack(ItemsTC.morphicResonator, 1)
                ));
            }
        });
        career.addTrade(3, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 2),
                    new ItemStack(ItemsTC.visResonator, 1)
                ));
            }
        });
        career.addTrade(3, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 4),
                    new ItemStack(ItemsTC.amuletVis, 1)
                ));
            }
        });
        career.addTrade(3, new EntityVillager.ITradeList() {
            @Override
            public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
                recipeList.add(new MerchantRecipe(
                    new ItemStack(ModItems.coin, 1),
                    new ItemStack(ModBlocks.RED_ROSE, 1)
                ));
            }
        });
    }
}
