package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
            'P', new ItemStack(ItemsTC.primordialPearl), 
            'E', new ItemStack(ModItems.EMPTY_FOCUS) 
        );

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumicforever", "FOCUS_COMPLEX"), FOCUS_COMPLEXRecipe);

        ShapedArcaneRecipe BoneBladeRecipe = new ShapedArcaneRecipe(
            new ResourceLocation("thaumicforever", "BoneBladeRecipe"), 
            "!ANGRY_SKELET", 
            0, 
            new AspectList(),
            new ItemStack(ModItems.ItemBoneBlade),
            "E", 
            "E", 
            "P", 
            'P', new ItemStack(ModItems.SILVER_INGOT), 
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

            'B', new ItemStack(ItemsTC.primordialPearl), 
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

    }
}
