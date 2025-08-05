package com.koteuka404.thaumicforever;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class AspectAdder {
    
    private static Aspect CAELES;
    static {
        try {
            Class<?> knowledgeTarClass = Class.forName("org.zeith.thaumicadditions.init.KnowledgeTAR");
            CAELES = (Aspect) knowledgeTarClass.getField("CAELES").get(null);
        } catch (Exception e) {
        }
    }

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
    

        ItemStack snoweStack = new ItemStack(Items.SNOWBALL);
        Aspect weaterAspect = AspectRegistry.WEATHER;
        AspectList gelumAspects = new AspectList(snoweStack);
        AspectList snowAspects = new AspectList();
        snowAspects.add(weaterAspect, 1);
        snowAspects.add(gelumAspects);
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SNOWBALL), snowAspects);

        Aspect victus = Aspect.LIFE;
        Aspect aqua = Aspect.WATER;
        Aspect vitium = Aspect.FLUX;
        Aspect alik = Aspect.ALCHEMY;
        AspectList taint_slime = new AspectList();
        taint_slime.add(vitium, 5);
        taint_slime.add(aqua, 5);
        taint_slime.add(victus, 5);
        taint_slime.add(alik, 1);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.taint_slime), taint_slime);



        Aspect spiriAspect = Aspect.SOUL;
        Aspect death = Aspect.DEATH;
        Aspect alienAspect = Aspect.ELDRITCH;
        AspectList soul = new AspectList();
        soul.add(spiriAspect, 10);
        soul.add(death, 5);
        soul.add(alienAspect, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.soul), soul);

        Aspect terra = Aspect.EARTH;
        Aspect magic = Aspect.MAGIC;
        AspectList stone = new AspectList();
        stone.add(terra, 5);
        stone.add(magic, 5);
        stone.add(alienAspect, 5);
        stone.add(alienAspect, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.stone), stone);

        Aspect need = Aspect.DESIRE;
        AspectList lootbag = new AspectList();
        lootbag.add(need, 15);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.lootbag), lootbag);

        AspectList endDustAspects = new AspectList();
        endDustAspects.add(Aspect.MAGIC, 5);
        endDustAspects.add(Aspect.ENERGY, 5);
        if (CAELES != null) {endDustAspects.add(CAELES, 3);}
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.end_dust), endDustAspects);



        AspectList endore = new AspectList();
        endore.add(Aspect.MAGIC, 10);
        endore.add(Aspect.ENERGY, 1);
        if (CAELES != null) {endore.add(CAELES, 5);}
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.EndOreBlock), endore);



        AspectList coin = new AspectList();
        coin.add(need, 2);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.coin), coin);


        Aspect aura = Aspect.AURA;
        Aspect herba = Aspect.PLANT;
        Aspect ordo = Aspect.ORDER;
        AspectList VisPlant = new AspectList();
        VisPlant.add(aura, 15);
        VisPlant.add(herba, 5);
        VisPlant.add(ordo, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.VisPlant), VisPlant);

        AspectList banana = new AspectList();
        banana.add(need, 1);
        banana.add(victus, 5);
        banana.add(herba, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.banana), banana);

        Aspect metall = Aspect.METAL;
        AspectList ring_master = new AspectList();
        ring_master.add(magic, 40);
        ring_master.add(aura, 15);
        ring_master.add(metall, 15);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.ring_master), ring_master);

        Aspect alien = Aspect.ELDRITCH;
        Aspect brain = Aspect.MIND;
        AspectList wand = new AspectList();
        wand.add(magic, 90);
        wand.add(alien, 25);
        wand.add(need, 20);
        wand.add(brain, 20);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.wand), wand);

        Aspect ignis = Aspect.FIRE;
        AspectList SMOOTH_STONE = new AspectList();
        SMOOTH_STONE.add(ignis, 1);
        SMOOTH_STONE.add(terra, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModOreBlocks.SMOOTH_STONE), SMOOTH_STONE);


        Aspect eldr = Aspect.ELDRITCH;
        AspectList ItemTaintAmulet = new AspectList();
        ItemTaintAmulet.add(victus, 40);
        ItemTaintAmulet.add(eldr, 40);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.ItemTaintAmulet), ItemTaintAmulet);

        Aspect best = Aspect.BEAST;
        AspectList ItemGoldenFish = new AspectList();
        ItemGoldenFish.add(victus, 5);
        ItemGoldenFish.add(aqua, 5);
        ItemGoldenFish.add(best, 5);
        ItemGoldenFish.add(magic, 5);
        ItemGoldenFish.add(need, 25);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.ItemGoldenFish), ItemGoldenFish);

        Aspect taint = Aspect.FLUX;
        AspectList taint_tendril = new AspectList();
        taint_tendril.add(taint, 5);
        taint_tendril.add(victus, 1);

        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.taint_tendril), taint_tendril);

        
        Aspect cogn = Aspect.MIND;
        Aspect teneb = Aspect.DARKNESS;
        Aspect lux = Aspect.LIGHT;
        AspectList celestial_notes_blood_moon = new AspectList();
        celestial_notes_blood_moon.add(cogn, 5);
        celestial_notes_blood_moon.add(teneb, 5);
        celestial_notes_blood_moon.add(lux, 5);


        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.celestial_notes), celestial_notes_blood_moon);

        Aspect vitr = Aspect.CRYSTAL;
        Aspect poten = Aspect.ENERGY;
        AspectList ruby_gem = new AspectList();
        ruby_gem.add(vitr, 10);
        ruby_gem.add(need, 10);
        ruby_gem.add(poten, 5);
        ruby_gem.add(ignis, 5);

        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.ruby_gem), ruby_gem);



        AspectList RubyOre = new AspectList();
        RubyOre.add(vitr, 13);
        RubyOre.add(need, 13);
        RubyOre.add(poten, 5);
        RubyOre.add(terra, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.RubyOre), RubyOre);


        AspectList OldPlank = new AspectList();
        OldPlank.add(herba, 3);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.OldPlank), OldPlank);

        AspectList holywater = new AspectList();
        holywater.add(need, 7);
        ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.holywater), holywater);


    }
}
