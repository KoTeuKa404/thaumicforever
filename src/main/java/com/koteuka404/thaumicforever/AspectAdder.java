package com.koteuka404.thaumicforever;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class AspectAdder {

    public void registerAspects() {
        // DOUBLE_TABLE: PLANT * 8
        AspectList doubleTableAspects = new AspectList();
        doubleTableAspects.add(Aspect.PLANT, 8);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.DOUBLE_TABLE), doubleTableAspects);

        // AQUAREIA_ORE: CRYSTAL * 13, EARTH * 5
        AspectList aquareiaOreAspects = new AspectList();
        aquareiaOreAspects.add(Aspect.CRYSTAL, 13);
        aquareiaOreAspects.add(Aspect.EARTH, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModOreBlocks.AQUAREIA_ORE), aquareiaOreAspects);

        // ANCIENT_AMBER: CRYSTAL * 13, EARTH * 5, TRAP * 13
        AspectList ancientAmberAspects = new AspectList();
        ancientAmberAspects.add(Aspect.CRYSTAL, 13);
        ancientAmberAspects.add(Aspect.EARTH, 5);
        ancientAmberAspects.add(Aspect.TRAP, 13);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModOreBlocks.ANCIENT_AMBER), ancientAmberAspects);

        // ANCIENT_CINNABAR: METAL * 20, EARTH * 5, ALCHEMY * 10, DEATH * 10
        AspectList ancientCinnabarAspects = new AspectList();
        ancientCinnabarAspects.add(Aspect.METAL, 20);
        ancientCinnabarAspects.add(Aspect.EARTH, 5);
        ancientCinnabarAspects.add(Aspect.ALCHEMY, 10);
        ancientCinnabarAspects.add(Aspect.DEATH, 10);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModOreBlocks.ANCIENT_CINNABAR), ancientCinnabarAspects);

        // ItemZombieHeart: UNDEATH * 10, DEATH * 10, ENTROPY * 10
        AspectList zombieHeartAspects = new AspectList();
        zombieHeartAspects.add(Aspect.UNDEAD, 10);
        zombieHeartAspects.add(Aspect.DEATH, 10);
        zombieHeartAspects.add(Aspect.ENTROPY, 10);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.ItemZombieHeart), zombieHeartAspects);

        // BROKEN_AMULET: CRYSTAL * 5, CRAFT * 1, BEAST * 2
        AspectList brokenAmuletAspects = new AspectList();
        brokenAmuletAspects.add(Aspect.CRYSTAL, 5);
        brokenAmuletAspects.add(Aspect.CRAFT, 1);
        brokenAmuletAspects.add(Aspect.BEAST, 2);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.BROKEN_AMULET), brokenAmuletAspects);

        // AQUAREIA_GEM: додаємо MATTERYA аспект
        Aspect matteryaAspect = AspectRegistry.MATTERYA;
        AspectList aquareiaGemAspects = new AspectList();
        aquareiaGemAspects.add(matteryaAspect, 1);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.AQUAREIA_GEM), aquareiaGemAspects);

        // RED_ROSE: 5 SENSUS, 5 HERBA, 1 VICTUS
        AspectList redRoseAspects = new AspectList();
        redRoseAspects.add(Aspect.SENSES, 5);
        redRoseAspects.add(Aspect.PLANT, 5);
        redRoseAspects.add(Aspect.LIFE, 1);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.RED_ROSE), redRoseAspects);

        // BLUE_ROSE: 5 SENSUS, 5 HERBA, 1 VICTUS
        AspectList blueRoseAspects = new AspectList();
        blueRoseAspects.add(Aspect.SENSES, 5);
        blueRoseAspects.add(Aspect.PLANT, 5);
        blueRoseAspects.add(Aspect.LIFE, 1);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.BLUE_ROSE), blueRoseAspects);

        // OBSIDIAN_TOTEM: 5 ALIENIS, 5 TERRA
        AspectList obsidianTotemAspects = new AspectList();
        obsidianTotemAspects.add(Aspect.ELDRITCH, 5);
        obsidianTotemAspects.add(Aspect.EARTH, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.OBSIDIAN_TOTEM), obsidianTotemAspects);

        // ABANDONED_CHEST: 10 VACUOS, 20 HERBA, 5 PRAECANTATIO
        AspectList abandonedChestAspects = new AspectList();
        abandonedChestAspects.add(Aspect.VOID, 10);
        abandonedChestAspects.add(Aspect.PLANT, 20);
        abandonedChestAspects.add(Aspect.MAGIC, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.ABANDONED_CHEST), abandonedChestAspects);

        // EMPTY_FOCUS: 80 ORDO, 60 AURAM, 50 PRAECANTATIO
        AspectList emptyFocusAspects = new AspectList();
        emptyFocusAspects.add(Aspect.ORDER, 80);
        emptyFocusAspects.add(Aspect.AURA, 60);
        emptyFocusAspects.add(Aspect.MAGIC, 50);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.EMPTY_FOCUS), emptyFocusAspects);

        // FOCUS_COMPLEX: 120 ORDO, 120 AURAM, 120 PRAECANTATIO
        AspectList focusComplexAspects = new AspectList();
        focusComplexAspects.add(Aspect.ORDER, 120);
        focusComplexAspects.add(Aspect.AURA, 120);
        focusComplexAspects.add(Aspect.MAGIC, 120);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.FOCUS_COMPLEX), focusComplexAspects);

        // Bone: 10 MORTUUS, 10 VICTUS, 5 AVERSIO
        AspectList boneAspects = new AspectList();
        boneAspects.add(Aspect.DEATH, 10);
        boneAspects.add(Aspect.LIFE, 10);
        boneAspects.add(Aspect.AVERSION, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.Bone), boneAspects);
    
        Aspect weaterAspect = AspectRegistry.WEATHER;
        Aspect gelumAspect = Aspect.COLD;
        AspectList snowAspects = new AspectList();
        snowAspects.add(weaterAspect, 1);
        snowAspects.add(gelumAspect, 1);
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SNOWBALL), snowAspects);
    }
}
