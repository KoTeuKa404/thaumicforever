package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

public class ModRecipes {

    public static void init() {
        ShapelessArcaneRecipe glassBottleRecipe = new ShapelessArcaneRecipe(
            new ResourceLocation("thaumicforever", "glass_bottle_from_glass"),
            "NEWALKIMIA",
            0,
            new AspectList(),
            new ItemStack(Items.GLASS_BOTTLE),
            new Object[] {new ItemStack(Blocks.GLASS)}
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "glass_bottle_from_glass"), glassBottleRecipe);

        ShapelessArcaneRecipe deconstructionTableRecipe = new ShapelessArcaneRecipe(
            new ResourceLocation("thaumicforever", "deconstruction_table"),
            "FIRSTSTEPS",
            50,
            new AspectList().add(Aspect.ENTROPY, 20),
            new ItemStack(ModBlocks.DECONSTRUCTION_TABLE),
            new Object[] {
                new ItemStack(Items.GOLDEN_AXE),
                new ItemStack(ItemsTC.thaumometer),
                new ItemStack(Items.GOLDEN_PICKAXE),
                new ItemStack(BlocksTC.tableWood),
            }
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "deconstruction_table"), deconstructionTableRecipe);

        ShapedArcaneRecipe brassGearRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "ItemBrassGear"),
            "BASEARTIFICE",
            30,
            new AspectList().add(Aspect.FIRE, 4),
            new ItemStack(ModItems.ItemBrassGear),
            " B ",
            "BIB",
            " B ",
            'B', "ingotBrass",
            'I', "plateIron"
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "ItemBrassGear"), brassGearRecipe);

        ShapedArcaneRecipe ItemThaumiumGearRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "ItemThaumiumGear"),
            "BASEARTIFICE",
            30,
            new AspectList().add(Aspect.AIR, 4),
            new ItemStack(ModItems.ItemThaumiumGear),
            " B ",
            "BIB",
            " B ",
            'B', new ItemStack(ItemsTC.ingots, 1, 0),
            'I', "plateIron"
            
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "ItemThaumiumGear"), ItemThaumiumGearRecipe);

        ShapedArcaneRecipe ItemVoidGearRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "ItemVoidGear"),
            "BASEARTIFICE",
            30,
            new AspectList().add(Aspect.ORDER, 4),
            new ItemStack(ModItems.ItemVoidGear),
            " B ",
            "BIB",
            " B ",
            'B', new ItemStack(ItemsTC.ingots, 1, 1),
            'I', "plateIron"
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "ItemVoidGear"), ItemVoidGearRecipe);



        ShapedArcaneRecipe mechanism_improvedRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "mechanism_improved"),
            "NEWARTIFICE",
            100,
            new AspectList().add(Aspect.ORDER, 10).add(Aspect.FIRE, 10).add(Aspect.AIR, 10),
            new ItemStack(ModItems.mechanism_improved),
            " B ",
            " I ",
            " C ",
            'B', new ItemStack(ItemsTC.mechanismSimple),
            'I', new ItemStack(ItemsTC.mechanismComplex),
            'C', new ItemStack(ModItems.ItemBrassGear)

        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "mechanism_improved"), mechanism_improvedRecipe);


        ShapedArcaneRecipe stoneRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "anti_flight"),
            "UNSTONE",
            300,
            new AspectList().add(Aspect.ORDER, 30).add(Aspect.EARTH, 15).add(Aspect.AIR, 15),
            new ItemStack(ModBlocks.ANTI_FLIGHT_STONE),
            "SBS",
            "AIA",
            "SCS",

            'B', new ItemStack(Blocks.ANVIL),
            'I', new ItemStack(BlocksTC.pedestalArcane),
            'C', new ItemStack(ModItems.ItemThaumiumGear),
            'S', new ItemStack(BlocksTC.stoneArcane),
            'A', new ItemStack(BlocksTC.levitator)

        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "anti_flight"), stoneRecipe);


        ShapedArcaneRecipe COMPRESSORRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "COMPRESSOR"),
            "COMPRESSOR",
            50,
            new AspectList().add(Aspect.ORDER, 20).add(Aspect.EARTH, 10),
            new ItemStack(ModBlocks.COMPRESSOR),
            "SBS",
            "AIA",
            "SAS",

            'B', new ItemStack(Blocks.PISTON),
            'I', new ItemStack(ModItems.ItemBrassGear),
            'S', new ItemStack(ItemsTC.plate,1,0),
            'A', new ItemStack(BlocksTC.stoneArcane)

        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "COMPRESSOR"), COMPRESSORRecipe);

        ShapedArcaneRecipe FOCUS_COMPLEXRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "FOCUS_COMPLEX"),
            "NEWAUROMANCY",
            50,
            new AspectList().add(Aspect.ORDER, 50).add(Aspect.FIRE, 50).add(Aspect.AIR, 50).add(Aspect.EARTH, 50).add(Aspect.ENTROPY, 50).add(Aspect.WATER, 50),
            new ItemStack(ModItems.FOCUS_COMPLEX),
            " P ",
            " E ",
            " P ",
            'P', Ingredient.fromItem(ItemsTC.primordialPearl),
            'E', new ItemStack(ModItems.EMPTY_FOCUS)
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "FOCUS_COMPLEX"), FOCUS_COMPLEXRecipe);

        ShapedArcaneRecipe BoneBladeRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "BoneBladeRecipe"),
            "!ANGRY_SKELET",
            30,
            new AspectList(),
            new ItemStack(ModItems.ItemBoneBlade),
            "E  ",
            "EG ",
            "P  ",
            'P', new ItemStack(ModItems.SILVER_INGOT),
            'G', new ItemStack(ItemsTC.nuggets, 1, 3),
            'E', new ItemStack(ModItems.Bone)
        );
        
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "BoneBladeRecipe"), BoneBladeRecipe);
        
        ShapedArcaneRecipe CharmRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "Charm_recipe"),
            "STUFF",
            150,
            new AspectList().add(Aspect.ORDER, 20).add(Aspect.ENTROPY, 20),
            new ItemStack(ModItems.PRIMAL_CHARM),
            "SBS",
            "AIA",
            "SAS",

            'B', new ItemStack(Items.NETHER_STAR),
            'I', new ItemStack(ItemsTC.visResonator),
            'S', new ItemStack(ItemsTC.plate,1,3),
            'A', new ItemStack(ItemsTC.plate,1,2)

        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "Charm_recipe"), CharmRecipe);

        ShapedArcaneRecipe primal_ingotRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "primal_ingotRecipe"),
            "PRIMAL",
            150,
            new AspectList().add(Aspect.ORDER, 20).add(Aspect.FIRE, 20).add(Aspect.AIR, 20).add(Aspect.EARTH, 20).add(Aspect.ENTROPY, 20).add(Aspect.WATER, 20),
            new ItemStack(ModItems.primalingot),
            "B",
            "I",
            "B",

            'B', Ingredient.fromItem(ItemsTC.primordialPearl),
            'I', new ItemStack(ItemsTC.ingots,1,1)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "primal_ingotRecipe"), primal_ingotRecipe);

        ShapedArcaneRecipe skuleRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "skuleRecipe"),
            "DECORATION",
            40,
            new AspectList().add(Aspect.ORDER, 20).add(Aspect.ENTROPY, 20),
            new ItemStack(ModItems.skull),
            "BBB",
            "B B",
            " B ",

            'B', new ItemStack(Items.BONE)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "skuleRecipe"), skuleRecipe);

        ShapedArcaneRecipe skeleRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "skeleRecipe"),
            "DECORATION",
            40,
            new AspectList().add(Aspect.ORDER, 15),
            new ItemStack(Items.SKULL,1,0),
            " B ",
            "BSB",
            " B ",

            'S', new ItemStack(ModItems.skull),
            'B', new ItemStack(Items.BONE)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "skeleRecipe"), skeleRecipe);

        ShapedArcaneRecipe witrRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "witrRecipe"),
            "DECORATION",
            40,
            new AspectList().add(Aspect.ENTROPY, 15),
            new ItemStack(Items.SKULL,1,1),
            " A ",
            "BSB",
            " A ",

            'S', new ItemStack(ModItems.skull),
            'A', new ItemStack(Items.BONE),
            'B', new ItemStack(Items.COAL)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "witrRecipe"), witrRecipe);

        ShapedArcaneRecipe zombRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "zombRecipe"),
            "DECORATION",
            40,
            new AspectList().add(Aspect.WATER, 15).add(Aspect.EARTH, 15),
            new ItemStack(Items.SKULL,1,2),
            " A ",
            "BSB",
            " A ",

            'S', new ItemStack(ModItems.skull),
            'A', new ItemStack(Items.ROTTEN_FLESH),
            'B', new ItemStack(Items.ROTTEN_FLESH)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "zombRecipe"), zombRecipe);

        ShapedArcaneRecipe crepRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "crepRecipe"),
            "DECORATION",
            40,
            new AspectList().add(Aspect.ENTROPY, 15).add(Aspect.EARTH, 15),
            new ItemStack(Items.SKULL,1,4),
            " A ",
            "BSB",
            " A ",

            'S', new ItemStack(ModItems.skull),
            'A', new ItemStack(Items.GUNPOWDER),
            'B', new ItemStack(Items.GUNPOWDER)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "crepRecipe"), crepRecipe);

        ShapedArcaneRecipe headRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "headRecipe"),
            "DECORATION",
            40,
            new AspectList().add(Aspect.AIR, 15).add(Aspect.EARTH, 15).add(Aspect.WATER, 15),
            new ItemStack(Items.SKULL,1,3),
            " A ",
            "BSB",
            " A ",

            'S', new ItemStack(ModItems.skull),
            'A', new ItemStack(Items.LEATHER),
            'B', new ItemStack(Items.ROTTEN_FLESH)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "headRecipe"), headRecipe);








        ItemStack solarPanel = new ItemStack(Item.getByNameOrId("industrialupgrade:thaumcraftpanel"), 1, 0);
        ShapedArcaneRecipe panel0 = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "panel0"),
            "IUALKIMIA",
            120,
            new AspectList().add(Aspect.AIR, 15).add(Aspect.EARTH, 15).add(Aspect.ORDER, 15),
            solarPanel,
            " B ",
            "BSB",
            " B ",
            'S', new ItemStack(ModItems.thaumium_core),
            'B', new ItemStack(Item.getByNameOrId("industrialupgrade:machines"), 1, 0)
            );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "panel0"), panel0);




        ItemStack solarPanel1 = new ItemStack(Item.getByNameOrId("industrialupgrade:thaumcraftpanel"), 1, 1);
        ShapedArcaneRecipe panel1 = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "panel1"),
            "IUALKIMIA",
            120,
            new AspectList().add(Aspect.ENTROPY, 25).add(Aspect.ORDER, 25),
            solarPanel1,
            " B ",
            "BSB",
            " B ",
            'S', new ItemStack(ModItems.void_core),
            'B', new ItemStack(Item.getByNameOrId("industrialupgrade:thaumcraftpanel"), 1, 0)
            );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "panel1"), panel1);

        ShapedArcaneRecipe large_tool_tf = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "large_tool_tf"),
            "TFSCRIBINGTOOLS",
            5,
            new AspectList().add(Aspect.WATER, 1),
            new ItemStack(ModItems.ItemScribeToolLarge),
            "III", "III", "IBS",
            'B', Ingredient.fromStacks(new ItemStack(ItemsTC.scribingTools)),
            'I', Ingredient.fromStacks(new ItemStack(Items.DYE,1,0)),
            'S', Ingredient.fromStacks(new ItemStack(Items.GLASS_BOTTLE))
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "large_tool_tf"), large_tool_tf);


        ShapelessArcaneRecipe prime_tool_tf = new ShapelessArcaneRecipe(
            new ResourceLocation("thaumicforever", "prime_tool_tf"),
            "TFSCRIBINGTOOLS",
            50,
            new AspectList().add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.WATER, 5).add(Aspect.EARTH, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5), 
            new ItemStack(ModItems.ItemPrimalScribeTool),
            new Object[] {"feather", new ItemStack(ModItems.ItemScribeToolLarge),Ingredient.fromItem(ItemsTC.primordialPearl)}
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "prime_tool_tf"), prime_tool_tf);

        ShapedArcaneRecipe nodeStabilazer = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "nodeStabilazer"),
            "STABILIZER",
            30,
            new AspectList().add(Aspect.WATER, 32).add(Aspect.ORDER, 32).add(Aspect.EARTH, 32),
            ModBlocks.nodeStabilizer,
            " G ",
            "QPQ",
            "ANA",
            'G', new ItemStack(Items.GOLD_INGOT),
            'Q', new ItemStack(Blocks.QUARTZ_BLOCK),
            'P', new ItemStack(Blocks.PISTON),
            'A', new ItemStack(BlocksTC.stoneArcane),
            'N', "nitor"
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "nodeStabilazer"), nodeStabilazer);

        ShapedArcaneRecipe bonecompass = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "bonecompass"),
            "THAUMICFOREVER",
            15,
            new AspectList().add(Aspect.ORDER, 2).add(Aspect.EARTH, 2),
            ModItems.ItemCompassMaze,
            " B ",
            "BCB",
            " B ",
            'B', new ItemStack(Items.BONE),
            'C', new ItemStack(Items.COMPASS)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "bonecompass"), bonecompass);

        ShapelessArcaneRecipe port = new ShapelessArcaneRecipe(
            new ResourceLocation("thaumicforever", "port"),
            "BUFFSTABILIZER",
            5,
            new AspectList().add(Aspect.WATER, 3).add(Aspect.ORDER, 3).add(Aspect.AIR, 3),
            new ItemStack(ModBlocks.Port),
            new Object[] {Items.QUARTZ, new ItemStack(BlocksTC.stoneArcane),new ItemStack(ItemsTC.visResonator)}
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "port"), port);

        ShapedArcaneRecipe Transducer = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "Transducer"),
            "THAUMICFOREVER",
            130,
            new AspectList().add(Aspect.ENTROPY, 32).add(Aspect.FIRE, 32).add(Aspect.AIR, 32),
            ModBlocks.BlockNodeCharger,
            "RCR",
            "ISI",
            "RNR",
            'R', new ItemStack(Blocks.REDSTONE_BLOCK),
            'C', new ItemStack(Items.COMPARATOR),
            'I', new ItemStack(Items.IRON_INGOT),
            'S', new ItemStack(ModBlocks.nodeStabilizer),
            'N', "nitor"
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "Transducer"), Transducer);


        ShapedArcaneRecipe caster_advanced = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "caster_advanced"),
            "THAUMICFOREVER",
            120,
            new AspectList().add(Aspect.ENTROPY, 2).add(Aspect.FIRE, 2).add(Aspect.AIR, 2).add(Aspect.WATER, 2).add(Aspect.EARTH, 2).add(Aspect.ORDER, 2),
            ModItems.CUSTOM_CASTER,
            "BBB",
            "LVL",
            "LTL",
            'B', new ItemStack(ItemsTC.ingots,1,2),
            'L', new ItemStack(Items.LEATHER),
            'V', new ItemStack(ItemsTC.visResonator),
            'T', new ItemStack(ItemsTC.thaumometer)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "caster_advanced"), caster_advanced);

        ShapedArcaneRecipe pouch = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "pouch"),
            "THAUMICFOREVER",
            25,
            new AspectList().add(Aspect.ENTROPY, 2).add(Aspect.FIRE, 2).add(Aspect.AIR, 2).add(Aspect.WATER, 2).add(Aspect.EARTH, 2).add(Aspect.ORDER, 2),
            ModItems.POUCH,
            "LGL",
            "LPL",
            "LLL",
            'P', new ItemStack(ItemsTC.focusPouch),
            'L', new ItemStack(Items.LEATHER),
            'G', new ItemStack(ModItems.GOLD_PLATE)
        );
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "pouch"), pouch);
    }
}
