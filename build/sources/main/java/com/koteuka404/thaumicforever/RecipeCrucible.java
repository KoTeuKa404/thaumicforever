package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.items.ItemsTC;

public class RecipeCrucible {
    public static final String MODID = "thaumicforever";

    public static void addCrucibleRecipes() {

        ResourceLocation recipeKey = new ResourceLocation(MODID, "netherrack_recipe");
        CrucibleRecipe netherrackRecipe = new CrucibleRecipe(
            "TWOND_NETHER_HEDGE", 
            new ItemStack(Blocks.NETHERRACK),
            new ItemStack(Blocks.COBBLESTONE), 
            new AspectList()
                .add(Aspect.FIRE, 5)       
        );

        ThaumcraftApi.addCrucibleRecipe(recipeKey, netherrackRecipe);

        ResourceLocation anotherRecipeKey = new ResourceLocation(MODID, "diamond_recipe");
        CrucibleRecipe diamondRecipe = new CrucibleRecipe(
            "PRAECANTATIOALKIMIA", 
            new ItemStack(Items.DIAMOND), 
            new ItemStack(Blocks.COAL_BLOCK), 
            new AspectList()
                .add(Aspect.CRYSTAL, 25) 
                .add(Aspect.DESIRE, 20) 
                .add(Aspect.EXCHANGE, 5) 

        );

        ThaumcraftApi.addCrucibleRecipe(anotherRecipeKey, diamondRecipe);

        ResourceLocation DirtRecipeKey = new ResourceLocation(MODID, "dirt_recipe");
        CrucibleRecipe DirtRecipe = new CrucibleRecipe(
            "TERRAALKIMIA", 
            new ItemStack(Blocks.DIRT), 
            new ItemStack(Blocks.SAND), 
            new AspectList()
                .add(Aspect.WATER, 3) 
                .add(Aspect.EARTH, 2) 

        );

        ThaumcraftApi.addCrucibleRecipe(DirtRecipeKey, DirtRecipe);

        ResourceLocation slimeRecipeKey = new ResourceLocation(ThaumicForever.MODID, "slime_ball_recipe");
        CrucibleRecipe slimeRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Items.SLIME_BALL), 
            new ItemStack(Items.SNOWBALL), 
            new AspectList()
                .add(Aspect.LIFE, 10)
                .add(Aspect.WATER, 5)
                .add(Aspect.ALCHEMY, 5) 
                .add(Aspect.EARTH, 3) 
        );

        ThaumcraftApi.addCrucibleRecipe(slimeRecipeKey, slimeRecipe);

        ResourceLocation dyeRecipeKey = new ResourceLocation(ThaumicForever.MODID, "dye_green_recipe");
        CrucibleRecipe dyeRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Items.DYE, 1, 3), 
            new ItemStack(Items.WHEAT_SEEDS), 
            new AspectList()
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.BEAST, 5)
                .add(Aspect.PLANT, 5)
                .add(Aspect.EARTH, 3)
        );

        ThaumcraftApi.addCrucibleRecipe(dyeRecipeKey, dyeRecipe);
        
        ResourceLocation waterLilyRecipeKey = new ResourceLocation(ThaumicForever.MODID, "waterlily_recipe");
        CrucibleRecipe waterLilyRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Blocks.WATERLILY), 
            OreDictionary.getOres("treeLeaves").get(0), 
            new AspectList()
                .add(Aspect.WATER, 3)
                .add(Aspect.PLANT, 5) 
        );

        ThaumcraftApi.addCrucibleRecipe(waterLilyRecipeKey, waterLilyRecipe);

        ResourceLocation gravelRecipeKey = new ResourceLocation(ThaumicForever.MODID, "gravel_recipe");
        CrucibleRecipe gravelRecipe = new CrucibleRecipe(
            "TERRAALKIMIA",  
            new ItemStack(Blocks.GRAVEL),
            new ItemStack(Blocks.COBBLESTONE),
            new AspectList()
                .add(Aspect.ENTROPY, 1) 
        );

        ThaumcraftApi.addCrucibleRecipe(gravelRecipeKey, gravelRecipe);


        ResourceLocation voidIngotRecipeKey = new ResourceLocation(ThaumicForever.MODID, "void_ingot_recipe");
        CrucibleRecipe voidIngotRecipe = new CrucibleRecipe(
            "BASEELDRITCH",  
            new ItemStack(ItemsTC.ingots, 1, 1),
            new ItemStack(ItemsTC.voidSeed),
            new AspectList()
                .add(Aspect.ELDRITCH, 5)
                .add(Aspect.FLUX, 5)
                .add(Aspect.METAL, 15) 
                .add(Aspect.MAGIC, 5) 
        );
        
        ThaumcraftApi.addCrucibleRecipe(voidIngotRecipeKey, voidIngotRecipe);


        ResourceLocation blacklotusRecipeKey = new ResourceLocation(ThaumicForever.MODID, "blacklotus_recipe");
        CrucibleRecipe blacklotusRecipe = new CrucibleRecipe(
            "BOTANIAALKIMIA",  
            new ItemStack(Item.getByNameOrId("botania:blacklotus")),            
            new ItemStack(Items.ENDER_PEARL),
            new AspectList()
                .add(Aspect.MAGIC, 10) 
                .add(Aspect.ORDER, 10) 
        );

        ThaumcraftApi.addCrucibleRecipe(blacklotusRecipeKey, blacklotusRecipe);



        ResourceLocation brassIngotRecipeKey = new ResourceLocation(ThaumicForever.MODID, "brass_ingot_recipe");


        CrucibleRecipe brassIngotRecipe = new CrucibleRecipe(
            "BASEALCHEMY", 
            new ItemStack(ItemsTC.ingots, 1, 2),            
            "ingotCopper",
            new AspectList()
                .add(Aspect.TOOL, 5)   
                .add(Aspect.FIRE,1)   
        );

        ThaumcraftApi.addCrucibleRecipe(brassIngotRecipeKey, brassIngotRecipe);


        ResourceLocation boneRecipeKey = new ResourceLocation(ThaumicForever.MODID, "reeds_recipe");
        CrucibleRecipe boneRecipe = new CrucibleRecipe(
            "ELDRITCHALKIMIA",  
            new ItemStack(ModItems.OldBone), 
            new ItemStack(Items.BONE), 
            new AspectList()
                .add(Aspect.ELDRITCH, 3) 
                .add(Aspect.UNDEAD, 2) 
        );
        ThaumcraftApi.addCrucibleRecipe(boneRecipeKey, boneRecipe);


        ResourceLocation reedsRecipeKey = new ResourceLocation(ThaumicForever.MODID, "reeds_recipe");
        CrucibleRecipe reedsRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Items.REEDS), 
            new ItemStack(Items.WHEAT), 
            new AspectList()
                .add(Aspect.AIR, 3) 
                .add(Aspect.WATER, 2) 
        );
        ThaumcraftApi.addCrucibleRecipe(reedsRecipeKey, reedsRecipe);

       
        ResourceLocation MAGIC_DUSTRecipeKey = new ResourceLocation(ThaumicForever.MODID, "MAGIC_DUST");
        CrucibleRecipe MAGIC_DUSTRecipe = new CrucibleRecipe(
            "PRAECANTATIOALKIMIA",  
            new ItemStack(ModItems.MAGIC_DUST), 
            new ItemStack(ItemsTC.salisMundus),
            new AspectList()
                .add(Aspect.SOUL, 3) 
                .add(Aspect.MIND, 2) 
        );

        ThaumcraftApi.addCrucibleRecipe(MAGIC_DUSTRecipeKey, MAGIC_DUSTRecipe);

        ResourceLocation ghastTearRecipeKey = new ResourceLocation(ThaumicForever.MODID, "ghast_tear_recipe");
        CrucibleRecipe ghastTearRecipe = new CrucibleRecipe(
            "TENEBRAEALKIMIA",  
            new ItemStack(Items.GHAST_TEAR),
            new ItemStack(Items.DIAMOND), 
            new AspectList()
                .add(Aspect.FIRE, 10)
                .add(Aspect.SOUL, 10) 
                .add(Aspect.UNDEAD, 10) 
                .add(Aspect.MAGIC, 5)
        );
        
        ThaumcraftApi.addCrucibleRecipe(ghastTearRecipeKey, ghastTearRecipe);


        ResourceLocation featherRecipeKey = new ResourceLocation(ThaumicForever.MODID, "feather_recipe");
        CrucibleRecipe featherRecipe = new CrucibleRecipe(
            "VICTUSALKIMIA",  
            new ItemStack(Items.FEATHER),
            new ItemStack(Items.STRING), 
            new AspectList()
                .add(Aspect.FLIGHT, 5) 
                .add(Aspect.AIR, 10)
        );
        
        ThaumcraftApi.addCrucibleRecipe(featherRecipeKey, featherRecipe);

        ResourceLocation pufferfishRecipeKey = new ResourceLocation(ThaumicForever.MODID, "pufferfish_recipe");
        CrucibleRecipe pufferfishRecipe = new CrucibleRecipe(
            "VICTUSALKIMIA",  
            new ItemStack(Items.FISH, 1, 3),
            new ItemStack(Items.FISH), 
            new AspectList()
                .add(Aspect.AVERSION, 5) 
                .add(Aspect.DEATH, 5)
        );
        
        ThaumcraftApi.addCrucibleRecipe(pufferfishRecipeKey, pufferfishRecipe);

        ResourceLocation emeraldRecipeKey = new ResourceLocation(ThaumicForever.MODID, "emerald_recipe");
        CrucibleRecipe emeraldRecipe = new CrucibleRecipe(
            "PRAECANTATIOALKIMIA",  
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.DIAMOND), 
            new AspectList()
                .add(Aspect.EXCHANGE, 5) 
                .add(Aspect.MAGIC, 5) 
        );
        
        ThaumcraftApi.addCrucibleRecipe(emeraldRecipeKey, emeraldRecipe);

        ResourceLocation netherWartRecipeKey = new ResourceLocation(ThaumicForever.MODID, "nether_wart_recipe");
        CrucibleRecipe netherWartRecipe  = new CrucibleRecipe(
            "TENEBRAEALKIMIA",  
            new ItemStack(Items.NETHER_WART),
            new ItemStack(Items.WHEAT_SEEDS), 
            new AspectList()
                .add(Aspect.ALCHEMY, 3)
                .add(Aspect.FLUX, 5) 
        );
        
        ThaumcraftApi.addCrucibleRecipe(netherWartRecipeKey, netherWartRecipe);

        ResourceLocation enderPearlRecipeKey = new ResourceLocation(ThaumicForever.MODID, "ender_pearl_recipe");
        CrucibleRecipe enderPearlRecipe = new CrucibleRecipe(
            "TENEBRAEALKIMIA",  
            new ItemStack(Items.ENDER_PEARL),
            new ItemStack(Items.DIAMOND), 
            new AspectList()
                .add(Aspect.MOTION, 10) 
                .add(Aspect.MAGIC, 15) 
                .add(Aspect.DARKNESS, 10) 
        );
        
        ThaumcraftApi.addCrucibleRecipe(enderPearlRecipeKey, enderPearlRecipe);

        ResourceLocation waterBucketRecipeKey = new ResourceLocation(ThaumicForever.MODID, "water_bucket_recipe");
        CrucibleRecipe waterBucketRecipe = new CrucibleRecipe(
            "NEWALKIMIA",  
            new ItemStack(Items.WATER_BUCKET), 
            new ItemStack(Items.BUCKET),
            new AspectList()
                .add(Aspect.WATER, 10) 
        );
        
        ThaumcraftApi.addCrucibleRecipe(waterBucketRecipeKey, waterBucketRecipe);

        ResourceLocation beetrootSeedsRecipeKey = new ResourceLocation(ThaumicForever.MODID, "beetroot_seeds_recipe");
        CrucibleRecipe beetrootSeedsRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Items.BEETROOT_SEEDS),
            new ItemStack(Items.WHEAT_SEEDS),
            new AspectList()
                .add(Aspect.DESIRE, 1)
        );
        
        ThaumcraftApi.addCrucibleRecipe(beetrootSeedsRecipeKey, beetrootSeedsRecipe);        




        if (Loader.isModLoaded("thaumicadditions")) {
            Aspect DRACO = Aspect.getAspect("draco");
        
            if (DRACO == null) {
            } else {
                ResourceLocation dragonBreathRecipeKey = new ResourceLocation(MODID, "dragon_breath_recipe");
        
                CrucibleRecipe dragonBreathRecipe = new CrucibleRecipe(
                    "PRAECANTATIOALKIMIA",
                    new ItemStack(Items.DRAGON_BREATH),
                    new ItemStack(Items.GLASS_BOTTLE),
                    new AspectList()
                        .add(DRACO, 5)
                        .add(Aspect.ALCHEMY, 3)
                        .add(Aspect.DARKNESS, 8)
                );
        
                ThaumcraftApi.addCrucibleRecipe(dragonBreathRecipeKey, dragonBreathRecipe);
            }
        }


        if (Loader.isModLoaded("oldresearch")) {
            ResourceLocation curioRecipeKey = new ResourceLocation(MODID, "curio_recipe");
            
            CrucibleRecipe curioRecipe = new CrucibleRecipe(
                "FIRSTSTEPS",  
                new ItemStack(Item.getByNameOrId("thaumcraft:curio"), 1, 7),
                new ItemStack(Items.BOOK),
                new AspectList()
                    .add(Aspect.MIND, 5) 
            );

            ThaumcraftApi.addCrucibleRecipe(curioRecipeKey, curioRecipe);
        }



        ResourceLocation ironIngotRecipeKey = new ResourceLocation(ThaumicForever.MODID, "iron_ingot_recipe");
        ItemStack ironIngotOutput = new ItemStack(Items.IRON_INGOT, 2);
        ItemStack oreIronInput = OreDictionary.getOres("ingotIron").get(0);
        
        CrucibleRecipe ironIngotRecipe = new CrucibleRecipe(
            "TERRAALKIMIA",  
            ironIngotOutput,
            oreIronInput, 
            new AspectList()
                .add(Aspect.METAL, 30) 
        );
        
        ThaumcraftApi.addCrucibleRecipe(ironIngotRecipeKey, ironIngotRecipe);




        ResourceLocation iceRecipeKey = new ResourceLocation(MODID, "ice_recipe");
        CrucibleRecipe iceRecipe = new CrucibleRecipe(
            "AQUAALKIMIA", 
            new ItemStack(Blocks.ICE),
            ThaumcraftApiHelper.makeCrystal(Aspect.WATER),
                        new AspectList()
                .add(Aspect.WATER, 3)
                .add(Aspect.ORDER, 6) 
                .add(Aspect.COLD, 1) 
        );

        ThaumcraftApi.addCrucibleRecipe(iceRecipeKey, iceRecipe);


       

       
        ResourceLocation snowballRecipeKey = new ResourceLocation(MODID, "snowball_recipe");
        CrucibleRecipe snowballRecipe = new CrucibleRecipe(
            "AQUAALKIMIA",
            new ItemStack(Items.SNOWBALL),
            ThaumcraftApiHelper.makeCrystal(Aspect.WATER),
            new AspectList()
                .add(Aspect.WATER, 2)
                .add(Aspect.ORDER, 1) 
        );

        ThaumcraftApi.addCrucibleRecipe(snowballRecipeKey, snowballRecipe);


        ResourceLocation packedIceRecipeKey = new ResourceLocation(MODID, "packed_recipe");
        CrucibleRecipe packedIceRecipe = new CrucibleRecipe(
            "AQUAALKIMIA",
            new ItemStack(Blocks.PACKED_ICE), 
            new ItemStack(Blocks.ICE),
            new AspectList()
                .add(Aspect.WATER, 3) 
                .add(Aspect.COLD, 7)
        );

        ThaumcraftApi.addCrucibleRecipe(packedIceRecipeKey, packedIceRecipe);

        ResourceLocation quarzRecipeKey = new ResourceLocation(MODID, "quarz_recipe");
        CrucibleRecipe quarzIceRecipe = new CrucibleRecipe(
            "TERRAALKIMIA",
            new ItemStack(ItemsTC.nuggets,1,9), 
            new ItemStack(Blocks.SAND),
            new AspectList()
                .add(Aspect.CRYSTAL, 3)
                .add(Aspect.FIRE, 1) 

        );

        ThaumcraftApi.addCrucibleRecipe(quarzRecipeKey, quarzIceRecipe);

        ResourceLocation amberRecipeKey = new ResourceLocation(MODID, "amber_recipe");
        CrucibleRecipe amberRecipe = new CrucibleRecipe(
            "ADDITIONALALKIMIA",
            new ItemStack(ModItems.CLUSTER,1,4), 
            new ItemStack(BlocksTC.oreAmber),
            new AspectList()
                .add(Aspect.CRYSTAL, 5)
                .add(Aspect.ORDER, 5) 

        );

        ThaumcraftApi.addCrucibleRecipe(amberRecipeKey, amberRecipe);

        ResourceLocation enddust_recipeRecipeKey = new ResourceLocation(MODID, "enddust_recipe");
        CrucibleRecipe enddust_recipeRecipe = new CrucibleRecipe(
            "ELDRITCHALKIMIA",
            new ItemStack(ModItems.soul), 
            new ItemStack(ModItems.end_dust),
            new AspectList()
                .add(Aspect.SOUL, 5)
                .add(Aspect.ORDER, 5) 

        );

        ThaumcraftApi.addCrucibleRecipe(enddust_recipeRecipeKey, enddust_recipeRecipe);
        
        ResourceLocation chromiumRecipeKey = new ResourceLocation("thaumicforever", "chromium_cluster_recipe");
        CrucibleRecipe chromiumClusterRecipe = new CrucibleRecipe(
            "IUALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 0),
            "oreChromium", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(chromiumRecipeKey, chromiumClusterRecipe);

       
        ResourceLocation iridiumRecipeKey = new ResourceLocation("thaumicforever", "iridium_cluster_recipe");
        CrucibleRecipe iridiumClusterRecipe = new CrucibleRecipe(
            "IUALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 1),
            "oreIridium", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(iridiumRecipeKey, iridiumClusterRecipe);

       
        ResourceLocation quartzRecipeKey = new ResourceLocation("thaumicforever", "quartz_cluster_recipe");
        CrucibleRecipe quartzClusterRecipe = new CrucibleRecipe(
            "APPLIDALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 2),
            "oreCertusQuartz", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(quartzRecipeKey, quartzClusterRecipe);

        
        ResourceLocation chargedQuartzRecipeKey = new ResourceLocation("thaumicforever", "charged_quartz_cluster_recipe");
        CrucibleRecipe chargedQuartzClusterRecipe = new CrucibleRecipe(
            "APPLIDALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 3),
            "oreChargedCertusQuartz", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(chargedQuartzRecipeKey, chargedQuartzClusterRecipe);

        ResourceLocation cherRecipeKey = new ResourceLocation("thaumicforever", "appliedenergistics2material");
        CrucibleRecipe charRecipe = new CrucibleRecipe(
            "APPLIDALKIMIA", 
            new ItemStack(Item.getByNameOrId("appliedenergistics2:material"), 1, 0), 
            new ItemStack(Items.QUARTZ),
            new AspectList().add(Aspect.ORDER, 3).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(cherRecipeKey, charRecipe);

        ResourceLocation uranRecipeKey = new ResourceLocation("thaumicforever", "uran_recipes");
        CrucibleRecipe uranRecipe = new CrucibleRecipe(
            "IUALKIMIA", 
            new ItemStack(Item.getByNameOrId("industrialupgradeclassiccore:uranium_ore"), 1, 3),
            new ItemStack(BlocksTC.oreCinnabar),
            new AspectList().add(Aspect.DEATH, 6).add(Aspect.ENERGY, 6)
        );
        ThaumcraftApi.addCrucibleRecipe(uranRecipeKey, uranRecipe);

        ResourceLocation aquamarinRecipeKey = new ResourceLocation("thaumicforever", "aquamarin");
        CrucibleRecipe aquamarinRecipe = new CrucibleRecipe(
            "AQUAALKIMIA", 
            new ItemStack(Item.getByNameOrId("astralsorcery:itemcraftingcomponent"), 1, 0),
            new ItemStack(Item.getByNameOrId("minecraft:dye"), 1, 4),
            new AspectList().add(Aspect.CRYSTAL, 4).add(Aspect.WATER, 4)
        );
        ThaumcraftApi.addCrucibleRecipe(aquamarinRecipeKey, aquamarinRecipe);
        
     
        ResourceLocation gorRecipeKey = new ResourceLocation("thaumicforever", "gor_recipes");
        CrucibleRecipe gorRecipe = new CrucibleRecipe(
            "AQUAALKIMIA", 
            new ItemStack(Item.getByNameOrId("astralsorcery:blockcustomore"), 1, 0), 
            new ItemStack(Blocks.QUARTZ_BLOCK),
            new AspectList().add(Aspect.DEATH, 6).add(Aspect.ENERGY, 6)
        );
        ThaumcraftApi.addCrucibleRecipe(gorRecipeKey, gorRecipe);

        ResourceLocation CleanRecipeKey = new ResourceLocation(MODID, "Clean_recipe");
        CrucibleRecipe CleanRecipe = new CrucibleRecipe(
            "STUFF",
            new ItemStack(ModItems.ItemBottleClean),
            FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.getFluid("purifying_fluid"), Fluid.BUCKET_VOLUME)),
            new AspectList()
                .add(Aspect.ORDER, 15)
                .add(Aspect.WATER, 10)
        );

        ThaumcraftApi.addCrucibleRecipe(CleanRecipeKey, CleanRecipe);

        ResourceLocation VisRecipeKey = new ResourceLocation(MODID, "Vis_recipe");
        CrucibleRecipe VisRecipe = new CrucibleRecipe(
            "STUFF",
            new ItemStack(ModItems.ItemBottleVis), 
            new ItemStack(ModItems.AuraPhial),
            new AspectList()
                .add(Aspect.AURA, 15)
                .add(Aspect.MAGIC, 10) 

        );

        ThaumcraftApi.addCrucibleRecipe(VisRecipeKey, VisRecipe);

        ResourceLocation BoneRecipeKey = new ResourceLocation(MODID, "BodeRecipe");
        CrucibleRecipe BodeRecipe = new CrucibleRecipe(
            "ELDRITCHALKIMIA",
            new ItemStack(ModItems.OldBone), 
            new ItemStack(Items.BONE),
            new AspectList()
                .add(Aspect.ELDRITCH, 6)
                .add(Aspect.SOUL, 3) 

        );

        ThaumcraftApi.addCrucibleRecipe(BoneRecipeKey, BodeRecipe);


        ResourceLocation fishRecipeKey = new ResourceLocation(MODID, "fishRecipeKey");
        CrucibleRecipe gishRecipe = new CrucibleRecipe(
            "golem_core_fish", 
            GolemHelper.getSealStack("thaumicforever:golem_core_fish"), 
            new ItemStack(ItemsTC.seals), 
            new AspectList()
            .add(Aspect.LIFE, 5)
            .add(Aspect.BEAST, 10)
            .add(Aspect.WATER, 15));

        ThaumcraftApi.addCrucibleRecipe(fishRecipeKey, gishRecipe);

        ResourceLocation crystalRecipeKey = new ResourceLocation(MODID, "crystalhRecipe");
        CrucibleRecipe crystalhRecipe = new CrucibleRecipe(
            "seal_golem_core_essentia", 
            GolemHelper.getSealStack("thaumicforever:golem_core_essentia"), 
            new ItemStack(ItemsTC.seals), 
            new AspectList()
            .add(Aspect.EARTH, 3)
            .add(Aspect.AIR, 3)
            .add(Aspect.FIRE, 3)
            .add(Aspect.ORDER, 3)
            .add(Aspect.ENTROPY, 3)
            .add(Aspect.WATER, 3));

        ThaumcraftApi.addCrucibleRecipe(crystalRecipeKey, crystalhRecipe);




        ResourceLocation windRecipeKey = new ResourceLocation(MODID, "wind_recipe");
        CrucibleRecipe windRecipe = new CrucibleRecipe(
            "FLIGHT", 
            new ItemStack(ModItems.WIND_CHARGE),
            ThaumcraftApiHelper.makeCrystal(Aspect.AIR),
                        new AspectList()
                .add(Aspect.AIR, 6)
                .add(Aspect.ORDER, 3) 
        );

        ThaumcraftApi.addCrucibleRecipe(windRecipeKey, windRecipe);





        ResourceLocation ruby_d_RecipeKey = new ResourceLocation(MODID, "ruby_dubl_recipe");
        CrucibleRecipe ruby_d_Recipe = new CrucibleRecipe(
            "IGNISALKIMIA", 
            new ItemStack(ModItems.ruby_gem,2 ),
            new ItemStack(ModItems.ruby_gem),
            new AspectList()
                .add(Aspect.CRYSTAL, 10)
                .add(Aspect.DESIRE, 10) 
                .add(Aspect.ENERGY, 5) 
                .add(Aspect.FIRE, 5) 


        );

        ThaumcraftApi.addCrucibleRecipe(ruby_d_RecipeKey, ruby_d_Recipe);


        ResourceLocation rubyRecipeKey = new ResourceLocation(MODID, "ruby_recipe");
        CrucibleRecipe rubyRecipe = new CrucibleRecipe(
            "IGNISALKIMIA", 
            new ItemStack(ModItems.ruby_gem),
            new ItemStack(Items.DIAMOND),
            new AspectList()
                .add(Aspect.FIRE, 15)
                .add(Aspect.EXCHANGE, 10) 
        );

        ThaumcraftApi.addCrucibleRecipe(rubyRecipeKey, rubyRecipe);
   


    ResourceLocation nethrecipeKey = new ResourceLocation(MODID, "netherrack_c_recipe");
    CrucibleRecipe netherRecipe = new CrucibleRecipe(
        "IGNISALKIMIA", 
        new ItemStack(Blocks.NETHERRACK),
        new ItemStack(Blocks.COBBLESTONE), 
        new AspectList()
            .add(Aspect.FIRE, 3)       
    );
    ThaumcraftApi.addCrucibleRecipe(nethrecipeKey, netherRecipe);



    ResourceLocation magmaRecipeKey = new ResourceLocation(MODID, "magma_recipe");
    CrucibleRecipe magmaRecipe = new CrucibleRecipe(
        "IGNISALKIMIA", 
        new ItemStack(Items.MAGMA_CREAM),
        new ItemStack(Items.SLIME_BALL), 
        new AspectList()
            .add(Aspect.FIRE, 3)       
    );
    ThaumcraftApi.addCrucibleRecipe(magmaRecipeKey, magmaRecipe);

    ResourceLocation woolRecipeKey = new ResourceLocation(MODID, "wool_recipe");
    CrucibleRecipe woolRecipe = new CrucibleRecipe(
        "ORDOOALKIMIA", 
        new ItemStack(Blocks.WOOL),
        new ItemStack(Items.STRING), 
        new AspectList()
            .add(Aspect.BEAST, 9)      
            .add(Aspect.CRAFT, 3)       
 
    );
    ThaumcraftApi.addCrucibleRecipe(woolRecipeKey, woolRecipe);



    ResourceLocation woolDRecipeKey = new ResourceLocation(MODID, "wool_d_recipe");
    CrucibleRecipe woolDRecipe = new CrucibleRecipe(
        "PERDITIOALKIMIA", 
        new ItemStack(Items.STRING,4), 
        new ItemStack(Blocks.WOOL),
        new AspectList()
            .add(Aspect.ENTROPY, 4)      
 
    );
    ThaumcraftApi.addCrucibleRecipe(woolDRecipeKey, woolDRecipe);
    



    ResourceLocation nethbDRecipeKey = new ResourceLocation(MODID, "nethb_d_recipe");
    CrucibleRecipe nethbDRecipe = new CrucibleRecipe(
        "PERDITIOALKIMIA", 
        new ItemStack(Items.NETHERBRICK,4), 
        new ItemStack(Blocks.NETHER_BRICK),
        new AspectList()
            .add(Aspect.ENTROPY, 4)      
 
    );
    ThaumcraftApi.addCrucibleRecipe(nethbDRecipeKey, nethbDRecipe);
    

    ResourceLocation quarzbDRecipeKey = new ResourceLocation(MODID, "quarzb_d_recipe");
    CrucibleRecipe quaezbDRecipe = new CrucibleRecipe(
        "PERDITIOALKIMIA", 
        new ItemStack(Items.QUARTZ,4), 
        new ItemStack(Blocks.QUARTZ_BLOCK),
        new AspectList()
            .add(Aspect.ENTROPY, 4)      
 
    );
    ThaumcraftApi.addCrucibleRecipe(quarzbDRecipeKey, quaezbDRecipe);


    ResourceLocation magmabDRecipeKey = new ResourceLocation(MODID, "magma_d_recipe");
    CrucibleRecipe magmazbDRecipe = new CrucibleRecipe(
        "PERDITIOALKIMIA", 
        new ItemStack(Items.MAGMA_CREAM,4), 
        new ItemStack(Blocks.MAGMA),
        new AspectList()
            .add(Aspect.ENTROPY, 4)      
 
    );
    ThaumcraftApi.addCrucibleRecipe(magmabDRecipeKey, magmazbDRecipe);

    ResourceLocation nethrbDRecipeKey = new ResourceLocation(MODID, "netrhbv_d_recipe");
    CrucibleRecipe nethrbDRecipe = new CrucibleRecipe(
        "PERDITIOALKIMIA", 
        new ItemStack(Items.NETHER_WART,9), 
        new ItemStack(Blocks.NETHER_WART_BLOCK),
        new AspectList()
            .add(Aspect.ENTROPY, 9)      
 
    );
    ThaumcraftApi.addCrucibleRecipe(nethrbDRecipeKey, nethrbDRecipe);

    }
}
 