package com.koteuka404.thaumicforever.entity.profession;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.api.items.ItemsTC;
import com.koteuka404.thaumicforever.entity.WizardVillager;

public class WizardVillagerProfession {
    public static final VillagerRegistry.VillagerProfession WIZARD_PROFESSION = new VillagerRegistry.VillagerProfession(
        "thaumicforever:wizard",
        "thaumicforever:textures/entity/wizard.png",
        "thaumicforever:textures/entity/zombie_villager.png"
    );

    public static void registerTrades() {
        VillagerRegistry.VillagerCareer wizardCareer = new VillagerRegistry.VillagerCareer(WIZARD_PROFESSION, "wizard");

        // lvl 1
        wizardCareer.addTrade(1, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 2),
                new ItemStack(ModItems.MAGIC_DUST, 1)
            ));
        });
        wizardCareer.addTrade(1, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ItemsTC.salisMundus, 1)
            ));
        });
        wizardCareer.addTrade(1, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ModItems.taint_slime, 1)
            ));
        });
        wizardCareer.addTrade(1, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ItemsTC.amber,3)
            ));
        });
        wizardCareer.addTrade(1, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ItemsTC.quicksilver,3)
            ));
        });
        // lvl 2
        wizardCareer.addTrade(2, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 6),
                new ItemStack(ModItems.ring_master,1)
            ));
        });
        wizardCareer.addTrade(2, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 3),
                new ItemStack(ModItems.BROKEN_AMULET,1)
            ));
        });

        wizardCareer.addTrade(2, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 2),
                new ItemStack(ModItems.AQUAREIA_GEM,1)
            ));
        });
        wizardCareer.addTrade(2, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ModItems.Bone,1)
            ));
        });
        wizardCareer.addTrade(2, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 2),
                new ItemStack(ModBlocks.ARCANE_WORKBENCH_WAND_CHARGER,1)
            ));
        });
        // lvl 3
        wizardCareer.addTrade(3, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ModBlocks.BLUE_ROSE,1)
            ));
        });
        wizardCareer.addTrade(3, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ItemsTC.sanitySoap,1)
            ));
        });
        wizardCareer.addTrade(3, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 1),
                new ItemStack(ItemsTC.bathSalts,1)
            ));
        });
        wizardCareer.addTrade(3, (merchant, recipeList, random) -> {
            recipeList.add(new MerchantRecipe(
                new ItemStack(ModItems.coin, 8),
                new ItemStack(ModItems.wand,1)
            ));
        });
    }

    @SubscribeEvent
    public static void onVillagerJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityVillager && !(event.getEntity() instanceof WizardVillager)) {
            EntityVillager villager = (EntityVillager) event.getEntity();

            if (villager.getProfessionForge() == WizardVillagerProfession.WIZARD_PROFESSION) {
                villager.setDead();
            }
        }
    }


}
