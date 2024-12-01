package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.items.ItemsTC;///

public class RecipeCrucible {
    public static final String MODID = "thaumicforever"; // Заміни на свій модифікатор ID

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
                .add(Aspect.LIFE, 10) // Victus
                .add(Aspect.WATER, 5) // Aqua
                .add(Aspect.ALCHEMY, 5) // Alkimia 
                .add(Aspect.EARTH, 3) // Terra
        );

        ThaumcraftApi.addCrucibleRecipe(slimeRecipeKey, slimeRecipe);

        ResourceLocation dyeRecipeKey = new ResourceLocation(ThaumicForever.MODID, "dye_green_recipe");
        CrucibleRecipe dyeRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Items.DYE, 1, 3), 
            new ItemStack(Items.WHEAT_SEEDS), 
            new AspectList()
                .add(Aspect.EXCHANGE, 10) // Permutatio
                .add(Aspect.BEAST, 5) // Bestia ()
                .add(Aspect.PLANT, 5) // Herba ()
                .add(Aspect.EARTH, 3) // Terra ()
        );

        ThaumcraftApi.addCrucibleRecipe(dyeRecipeKey, dyeRecipe);
        
        ResourceLocation waterLilyRecipeKey = new ResourceLocation(ThaumicForever.MODID, "waterlily_recipe");
        CrucibleRecipe waterLilyRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Blocks.WATERLILY), 
            OreDictionary.getOres("treeLeaves").get(0), 
            new AspectList()
                .add(Aspect.WATER, 3) // Aqua
                .add(Aspect.PLANT, 5) // Herba
        );

        ThaumcraftApi.addCrucibleRecipe(waterLilyRecipeKey, waterLilyRecipe);

        ResourceLocation gravelRecipeKey = new ResourceLocation(ThaumicForever.MODID, "gravel_recipe");
        CrucibleRecipe gravelRecipe = new CrucibleRecipe(
            "TERRAALKIMIA",  
            new ItemStack(Blocks.GRAVEL),
            new ItemStack(Blocks.COBBLESTONE),
            new AspectList()
                .add(Aspect.ENTROPY, 1) // Perditio
        );

        ThaumcraftApi.addCrucibleRecipe(gravelRecipeKey, gravelRecipe);


        ResourceLocation voidIngotRecipeKey = new ResourceLocation(ThaumicForever.MODID, "void_ingot_recipe");
        CrucibleRecipe voidIngotRecipe = new CrucibleRecipe(
            "BASEELDRITCH",  
            new ItemStack(ItemsTC.ingots, 1, 1),
            new ItemStack(ItemsTC.voidSeed),
            new AspectList()
                .add(Aspect.ELDRITCH, 5) // Alienis
                .add(Aspect.FLUX, 5) // Vitium
                .add(Aspect.METAL, 15) // Metallum
                .add(Aspect.MAGIC, 5) // Praecantatio
        );
        
        ThaumcraftApi.addCrucibleRecipe(voidIngotRecipeKey, voidIngotRecipe);


        ResourceLocation blacklotusRecipeKey = new ResourceLocation(ThaumicForever.MODID, "blacklotus_recipe");
        CrucibleRecipe blacklotusRecipe = new CrucibleRecipe(
            "BOTANIAALKIMIA",  
            new ItemStack(Item.getByNameOrId("botania:blacklotus")),            
            new ItemStack(Items.ENDER_PEARL),
            new AspectList()
                .add(Aspect.MAGIC, 10) // Aqua
                .add(Aspect.ORDER, 10) // Herba
        );

        ThaumcraftApi.addCrucibleRecipe(blacklotusRecipeKey, blacklotusRecipe);



        ResourceLocation brassIngotRecipeKey = new ResourceLocation(ThaumicForever.MODID, "brass_ingot_recipe");


        // Створюємо рецепт для казана
        CrucibleRecipe brassIngotRecipe = new CrucibleRecipe(
            "BASEALCHEMY", 
            new ItemStack(ItemsTC.ingots, 1, 2),            
            "ingotCopper",
            new AspectList()
                .add(Aspect.TOOL, 5)    // Metallum
                .add(Aspect.FIRE,1)    // Ordo
        );

        ThaumcraftApi.addCrucibleRecipe(brassIngotRecipeKey, brassIngotRecipe);




        


        ResourceLocation reedsRecipeKey = new ResourceLocation(ThaumicForever.MODID, "reeds_recipe");
        CrucibleRecipe reedsRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Items.REEDS), 
            new ItemStack(Items.WHEAT), 
            new AspectList()
                .add(Aspect.AIR, 3) // Aer
                .add(Aspect.WATER, 2) // Aqua
        );
        ThaumcraftApi.addCrucibleRecipe(reedsRecipeKey, reedsRecipe);

        ResourceLocation MAGIC_DUSTRecipeKey = new ResourceLocation(ThaumicForever.MODID, "MAGIC_DUST");
        CrucibleRecipe MAGIC_DUSTRecipe = new CrucibleRecipe(
            "PRAECANTATIOALKIMIA",  
            new ItemStack(ModItems.MAGIC_DUST), 
            new ItemStack(ItemsTC.salisMundus),
            new AspectList()
                .add(Aspect.SOUL, 3) // Aer
                .add(Aspect.MIND, 2) // Aqua
        );

        ThaumcraftApi.addCrucibleRecipe(MAGIC_DUSTRecipeKey, MAGIC_DUSTRecipe);

        ResourceLocation ghastTearRecipeKey = new ResourceLocation(ThaumicForever.MODID, "ghast_tear_recipe");
        CrucibleRecipe ghastTearRecipe = new CrucibleRecipe(
            "TENEBRAEALKIMIA",  
            new ItemStack(Items.GHAST_TEAR),
            new ItemStack(Items.DIAMOND), 
            new AspectList()
                .add(Aspect.FIRE, 10) // Fire 
                .add(Aspect.SOUL, 10) // Spiritus
                .add(Aspect.UNDEAD, 10) // Exanimis
                .add(Aspect.MAGIC, 5) // Praecantatio
        );
        
        ThaumcraftApi.addCrucibleRecipe(ghastTearRecipeKey, ghastTearRecipe);


        ResourceLocation featherRecipeKey = new ResourceLocation(ThaumicForever.MODID, "feather_recipe");
        CrucibleRecipe featherRecipe = new CrucibleRecipe(
            "VICTUSALKIMIA",  
            new ItemStack(Items.FEATHER),
            new ItemStack(Items.STRING), 
            new AspectList()
                .add(Aspect.FLIGHT, 5) // Volatus
                .add(Aspect.AIR, 10) // Aer
        );
        
        ThaumcraftApi.addCrucibleRecipe(featherRecipeKey, featherRecipe);

        ResourceLocation pufferfishRecipeKey = new ResourceLocation(ThaumicForever.MODID, "pufferfish_recipe");
        CrucibleRecipe pufferfishRecipe = new CrucibleRecipe(
            "VICTUSALKIMIA",  
            new ItemStack(Items.FISH, 1, 3),
            new ItemStack(Items.FISH), 
            new AspectList()
                .add(Aspect.AVERSION, 5) // Aversio
                .add(Aspect.DEATH, 5) // Mortuus
        );
        
        ThaumcraftApi.addCrucibleRecipe(pufferfishRecipeKey, pufferfishRecipe);

        ResourceLocation emeraldRecipeKey = new ResourceLocation(ThaumicForever.MODID, "emerald_recipe");
        CrucibleRecipe emeraldRecipe = new CrucibleRecipe(
            "PRAECANTATIOALKIMIA",  
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.DIAMOND), 
            new AspectList()
                .add(Aspect.EXCHANGE, 5) // Permutatio
                .add(Aspect.MAGIC, 5) 
        );
        
        ThaumcraftApi.addCrucibleRecipe(emeraldRecipeKey, emeraldRecipe);

        ResourceLocation netherWartRecipeKey = new ResourceLocation(ThaumicForever.MODID, "nether_wart_recipe");
        CrucibleRecipe netherWartRecipe  = new CrucibleRecipe(
            "TENEBRAEALKIMIA",  
            new ItemStack(Items.NETHER_WART),
            new ItemStack(Items.WHEAT_SEEDS), 
            new AspectList()
                .add(Aspect.ALCHEMY, 3) // Alkimia
                .add(Aspect.FLUX, 5) // Vitium
        );
        
        ThaumcraftApi.addCrucibleRecipe(netherWartRecipeKey, netherWartRecipe);

        ResourceLocation enderPearlRecipeKey = new ResourceLocation(ThaumicForever.MODID, "ender_pearl_recipe");
        CrucibleRecipe enderPearlRecipe = new CrucibleRecipe(
            "TENEBRAEALKIMIA",  
            new ItemStack(Items.ENDER_PEARL),
            new ItemStack(Items.DIAMOND), 
            new AspectList()
                .add(Aspect.MOTION, 10) // Motus
                .add(Aspect.MAGIC, 15) // Praecantatio
                .add(Aspect.DARKNESS, 10) // Tenebrae
        );
        
        ThaumcraftApi.addCrucibleRecipe(enderPearlRecipeKey, enderPearlRecipe);

        ResourceLocation waterBucketRecipeKey = new ResourceLocation(ThaumicForever.MODID, "water_bucket_recipe");
        CrucibleRecipe waterBucketRecipe = new CrucibleRecipe(
            "NEWALKIMIA",  
            new ItemStack(Items.WATER_BUCKET), 
            new ItemStack(Items.BUCKET),
            new AspectList()
                .add(Aspect.WATER, 10) // Aqua
        );
        
        ThaumcraftApi.addCrucibleRecipe(waterBucketRecipeKey, waterBucketRecipe);

        ResourceLocation beetrootSeedsRecipeKey = new ResourceLocation(ThaumicForever.MODID, "beetroot_seeds_recipe");
        CrucibleRecipe beetrootSeedsRecipe = new CrucibleRecipe(
            "HERBAALKIMIA",  
            new ItemStack(Items.BEETROOT_SEEDS),
            new ItemStack(Items.WHEAT_SEEDS),
            new AspectList()
                .add(Aspect.DESIRE, 1) // Desiderium
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
                        .add(DRACO, 5) // Draco
                        .add(Aspect.ALCHEMY, 3) // Alkimia
                        .add(Aspect.DARKNESS, 8) // Tenebrae
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
                    .add(Aspect.MIND, 5) // Cognitio
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
                .add(Aspect.METAL, 30) // Metallum
        );
        
        ThaumcraftApi.addCrucibleRecipe(ironIngotRecipeKey, ironIngotRecipe);




        ResourceLocation iceRecipeKey = new ResourceLocation(MODID, "ice_recipe");
        CrucibleRecipe iceRecipe = new CrucibleRecipe(
            "AQUAALKIMIA", 
            new ItemStack(Blocks.ICE),
            ThaumcraftApiHelper.makeCrystal(Aspect.WATER),
                        new AspectList()
                .add(Aspect.WATER, 3) // Aqua
                .add(Aspect.ORDER, 6) // Ordo
                .add(Aspect.COLD, 1) // Gelum
        );

        ThaumcraftApi.addCrucibleRecipe(iceRecipeKey, iceRecipe);


       

       
        ResourceLocation snowballRecipeKey = new ResourceLocation(MODID, "snowball_recipe");
        CrucibleRecipe snowballRecipe = new CrucibleRecipe(
            "AQUAALKIMIA",
            new ItemStack(Items.SNOWBALL),
            ThaumcraftApiHelper.makeCrystal(Aspect.WATER),
            new AspectList()
                .add(Aspect.WATER, 2) // Aqua
                .add(Aspect.ORDER, 1) // Ordo
        );

        ThaumcraftApi.addCrucibleRecipe(snowballRecipeKey, snowballRecipe);


        ResourceLocation packedIceRecipeKey = new ResourceLocation(MODID, "packed_recipe");
        CrucibleRecipe packedIceRecipe = new CrucibleRecipe(
            "AQUAALKIMIA",
            new ItemStack(Blocks.PACKED_ICE), 
            new ItemStack(Blocks.ICE),
            new AspectList()
                .add(Aspect.WATER, 3) // Aqua
                .add(Aspect.COLD, 7) // Gelum
        );

        ThaumcraftApi.addCrucibleRecipe(packedIceRecipeKey, packedIceRecipe);
        
        
        ResourceLocation chromiumRecipeKey = new ResourceLocation("thaumicforever", "chromium_cluster_recipe");
        CrucibleRecipe chromiumClusterRecipe = new CrucibleRecipe(
            "IUALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 0),
            "oreChromium", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(chromiumRecipeKey, chromiumClusterRecipe);

        // Iridium Cluster Recipe
        ResourceLocation iridiumRecipeKey = new ResourceLocation("thaumicforever", "iridium_cluster_recipe");
        CrucibleRecipe iridiumClusterRecipe = new CrucibleRecipe(
            "IUALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 1),
            "oreIridium", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(iridiumRecipeKey, iridiumClusterRecipe);

        // Quartz Cluster Recipe
        ResourceLocation quartzRecipeKey = new ResourceLocation("thaumicforever", "quartz_cluster_recipe");
        CrucibleRecipe quartzClusterRecipe = new CrucibleRecipe(
            "APPLIDALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 2),
            "oreCertusQuartz", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(quartzRecipeKey, quartzClusterRecipe);

        // Charged Quartz Cluster Recipe
        ResourceLocation chargedQuartzRecipeKey = new ResourceLocation("thaumicforever", "charged_quartz_cluster_recipe");
        CrucibleRecipe chargedQuartzClusterRecipe = new CrucibleRecipe(
            "APPLIDALKIMIA", 
            new ItemStack(ModItems.CLUSTER, 1, 3),
            "oreChargedCertusQuartz", 
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.EARTH, 5)
        );
        ThaumcraftApi.addCrucibleRecipe(chargedQuartzRecipeKey, chargedQuartzClusterRecipe);

    }
}
 