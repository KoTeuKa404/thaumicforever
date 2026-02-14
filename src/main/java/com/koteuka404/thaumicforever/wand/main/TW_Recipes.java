package com.koteuka404.thaumicforever.wand.main;

import java.util.HashMap;

import com.koteuka404.thaumicforever.ModItems;
import com.koteuka404.thaumicforever.wand.compat.TW_Compat;
import com.koteuka404.thaumicforever.wand.crafting.recipe.RecipeScepter;
import com.koteuka404.thaumicforever.wand.crafting.recipe.RecipeStaff;
import com.koteuka404.thaumicforever.wand.crafting.recipe.RecipeWand;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

public class TW_Recipes {

    public static HashMap<String, ResourceLocation> recipes = new HashMap<>();

    public static void registerRecipes(Register<IRecipe> r) {
        addCraftingRecipes();
        addArcaneRecipes(r);
        addCrucibleRecipes();
        addInfusionRecipes();

        TW_Compat.initRecipes();

        RegistryManager.ACTIVE.getRegistry(GameData.RECIPES).remove(new ResourceLocation("thaumcraft:thaumometer"));
    }

    private static void addCraftingRecipes() {
        addShapedOreRecipe("BASETHAUMATURGY.1", new ItemStack(TW_Items.itemWandCap, 1, 0), "NNN", "N N", 'N', "nuggetIron");
        addShapedOreRecipe("BASETHAUMATURGY.2", WandHelper.getWandWithParts("wood", "iron"), "  C", " R ", "C  ", 'C', new ItemStack(TW_Items.itemWandCap), 'R', Items.STICK);

        ShapedOreRecipe fakeWandRecipe = new ShapedOreRecipe(
                new ResourceLocation(""),
                WandHelper.getWandWithParts("wood", "iron"),
                "  C",
                " R ",
                "C  ",
                'C', new ItemStack(TW_Items.itemWandCap),
                'R', Items.STICK
        );
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation(ThaumicWands.modID, "BASETHAUMATURGY.2_DISPLAY"), fakeWandRecipe);

    }

    private static void addArcaneRecipes(Register<IRecipe> r) {
        AspectList crystals;

        crystals = new AspectList();
        addShapedArcaneRecipe("FIRSTSTEPS.1", "FIRSTSTEPS@1", new ItemStack(ItemsTC.thaumometer), 20, crystals, " G ", "GPG", " G ", 'G', "ingotGold", 'P', Blocks.GLASS_PANE);

        //crystals = new AspectList().add(Aspect.AIR,2).add(Aspect.FIRE,2).add(Aspect.ORDER,2);
        //addShapedArcaneRecipe("CAP_COPPER.1","CAP_COPPER@1", new ItemStack(TW_Items.itemWandCap, 1, 1), 10, crystals, "NNN","N N",'N',"nuggetCopper");

        crystals = new AspectList().add(Aspect.AIR, 2).add(Aspect.FIRE, 2).add(Aspect.ORDER, 2);
        addShapedArcaneRecipe("CAP_GOLD.1", "CAP_GOLD@1", new ItemStack(TW_Items.itemWandCap, 1, 1), 10, crystals, "NNN", "N N", 'N', "nuggetGold");

        crystals = new AspectList().add(Aspect.AIR, 3).add(Aspect.FIRE, 3).add(Aspect.ORDER, 3);
        addShapedArcaneRecipe("CAP_BRASS.1", "CAP_BRASS@1", new ItemStack(TW_Items.itemWandCap, 1, 2), 15, crystals, "NNN", "N N", 'N', "nuggetBrass");

        //crystals = new AspectList().add(Aspect.AIR,4).add(Aspect.FIRE,4).add(Aspect.ORDER,4);
        //addShapedArcaneRecipe("CAP_SILVER.1","CAP_SILVER@1", new ItemStack(TW_Items.itemWandCap, 1, 3), 10, crystals, "NNN","N N",'N',"nuggetSilver");

        crystals = new AspectList().add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.ORDER, 5);
        addShapedArcaneRecipe("CAP_THAUMIUM.1", "CAP_THAUMIUM@1", new ItemStack(TW_Items.itemWandCap, 1, 3), 10, crystals, "NNN", "N N", 'N', "nuggetThaumium");

        crystals = new AspectList().add(Aspect.AIR, 7).add(Aspect.FIRE, 7).add(Aspect.ORDER, 7).add(Aspect.ENTROPY, 6);
        addShapedArcaneRecipe("CAP_VOID.1", "CAP_VOID@1", new ItemStack(TW_Items.itemWandCap, 1, 5), 10, crystals, "NNN", "N N", 'N', "nuggetVoid");

        crystals = new AspectList().add(Aspect.ENTROPY, 2);
        addShapedArcaneRecipe("ROD_GREATWOOD.1", "ROD_GREATWOOD@1", new ItemStack(TW_Items.itemWandRod, 1, 0), 10, crystals, "  L", " L ", "L  ", 'L', new ItemStack(BlocksTC.logGreatwood));

        crystals = new AspectList().add(Aspect.ORDER, 2);
        addShapedArcaneRecipe("CORE_GREATWOOD.1", "STAFF@1", new ItemStack(TW_Items.itemStaffCore, 1, 0), 10, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 0), 'P', ModItems.AQUAREIA_GEM);

        addShapedArcaneRecipe("CORE_ELEMENTAL.1", "CORE_ELEMENTAL@1", new ItemStack(TW_Items.itemStaffCore, 1, 1), 7, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 1), 'P', ModItems.AQUAREIA_GEM);
        addShapedArcaneRecipe("CORE_ELEMENTAL.2", "CORE_ELEMENTAL@1", new ItemStack(TW_Items.itemStaffCore, 1, 2), 7, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 2), 'P', ModItems.AQUAREIA_GEM);
        addShapedArcaneRecipe("CORE_ELEMENTAL.3", "CORE_ELEMENTAL@1", new ItemStack(TW_Items.itemStaffCore, 1, 3), 7, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 3), 'P', ModItems.AQUAREIA_GEM);
        addShapedArcaneRecipe("CORE_ELEMENTAL.4", "CORE_ELEMENTAL@1", new ItemStack(TW_Items.itemStaffCore, 1, 4), 7, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 4), 'P', ModItems.AQUAREIA_GEM);
        addShapedArcaneRecipe("CORE_ELEMENTAL.5", "CORE_ELEMENTAL@1", new ItemStack(TW_Items.itemStaffCore, 1, 5), 7, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 5), 'P', ModItems.AQUAREIA_GEM);
        addShapedArcaneRecipe("CORE_ELEMENTAL.6", "CORE_ELEMENTAL@1", new ItemStack(TW_Items.itemStaffCore, 1, 6), 7, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 6), 'P', ModItems.AQUAREIA_GEM);

        addShapedArcaneRecipe("CORE_SILVERWOOD.1", "CORE_SILVERWOOD@1", new ItemStack(TW_Items.itemStaffCore, 1, 7), 20, crystals, "  P", " L ", "L  ", 'L', new ItemStack(TW_Items.itemWandRod, 1, 7), 'P', ModItems.AQUAREIA_GEM);

        r.getRegistry().register(new RecipeWand(getNameForRecipe(new ItemStack(TW_Items.itemWand))));
        r.getRegistry().register(new RecipeStaff(getNameForRecipe(new ItemStack(TW_Items.itemStaff))));
        r.getRegistry().register(new RecipeScepter(getNameForRecipe(new ItemStack(TW_Items.itemScepter))));

        ShapedOreRecipe recipeFakeScepter = new ShapedOreRecipe(new ResourceLocation(""), WandHelper.getScepterWithParts("greatwood", "gold"),
                " CP",
                " RC",
                "C  ",
                'C', new ItemStack(TW_Items.itemWandCap, 1, 1),
                'R', new ItemStack(TW_Items.itemWandRod, 1, 0),
                'P', ModItems.AQUAREIA_GEM);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation(ThaumicWands.modID, "fake_scepter"), recipeFakeScepter);

        ShapedOreRecipe recipeFakeStaff = new ShapedOreRecipe(new ResourceLocation(""), WandHelper.getStaffWithParts("greatwood", "gold"),
                "  C",
                " R ",
                "C  ",
                'C', new ItemStack(TW_Items.itemWandCap, 1, 1),
                'R', new ItemStack(TW_Items.itemStaffCore, 1, 0));
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation(ThaumicWands.modID, "fake_staff"), recipeFakeStaff);

    }

    private static void addCrucibleRecipes() {
    }

    private static void addInfusionRecipes() {
        AspectList aspects;

        //aspects = new AspectList().add(Aspect.ENERGY, 30).add(Aspect.AURA, 15).add(Aspect.MAGIC, 15);
        //addInfusionRecipe("CAP_SILVER.2","CAP_SILVER@1", new ItemStack(TW_Items.itemWandCap,1,4), 3, new ItemStack(TW_Items.itemWandCap,1,3), aspects, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));

        aspects = new AspectList().add(Aspect.ENERGY, 50).add(Aspect.AURA, 25).add(Aspect.MAGIC, 25);
        addInfusionRecipe("CAP_THAUMIUM.2", "CAP_THAUMIUM@1", new ItemStack(TW_Items.itemWandCap, 1, 4), 3, new ItemStack(TW_Items.itemWandCap, 1, 3), aspects, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));

        aspects = new AspectList().add(Aspect.ENERGY, 50).add(Aspect.VOID, 50).add(Aspect.AURA, 25).add(Aspect.ELDRITCH, 25);
        addInfusionRecipe("CAP_VOID.2", "CAP_VOID@1", new ItemStack(TW_Items.itemWandCap, 1, 6), 3, new ItemStack(TW_Items.itemWandCap, 1, 5), aspects, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));


        aspects = new AspectList().add(Aspect.AIR, 50).add(Aspect.MAGIC, 25).add(Aspect.MOTION, 25);
        addInfusionRecipe("ROD_ELEMENTAL.1", "ROD_ELEMENTAL@1", new ItemStack(TW_Items.itemWandRod, 1, 1), 3, new ItemStack(Items.REEDS), aspects, ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.MOTION));

        aspects = new AspectList().add(Aspect.FIRE, 50).add(Aspect.MAGIC, 25).add(Aspect.BEAST, 25);
        addInfusionRecipe("ROD_ELEMENTAL.2", "ROD_ELEMENTAL@1", new ItemStack(TW_Items.itemWandRod, 1, 2), 3, new ItemStack(Items.BLAZE_ROD), aspects, ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.BEAST));

        aspects = new AspectList().add(Aspect.WATER, 50).add(Aspect.MAGIC, 25).add(Aspect.COLD, 25);
        addInfusionRecipe("ROD_ELEMENTAL.3", "ROD_ELEMENTAL@1", new ItemStack(TW_Items.itemWandRod, 1, 3), 3, new ItemStack(Blocks.ICE), aspects, ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.COLD));

        aspects = new AspectList().add(Aspect.EARTH, 50).add(Aspect.MAGIC, 25).add(Aspect.DARKNESS, 25);
        addInfusionRecipe("ROD_ELEMENTAL.4", "ROD_ELEMENTAL@1", new ItemStack(TW_Items.itemWandRod, 1, 4), 3, new ItemStack(Blocks.OBSIDIAN), aspects, ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.DARKNESS));

        aspects = new AspectList().add(Aspect.ORDER, 50).add(Aspect.MAGIC, 25).add(Aspect.CRYSTAL, 25);
        addInfusionRecipe("ROD_ELEMENTAL.5", "ROD_ELEMENTAL@1", new ItemStack(TW_Items.itemWandRod, 1, 5), 3, new ItemStack(Blocks.QUARTZ_BLOCK), aspects, ThaumcraftApiHelper.makeCrystal(Aspect.ORDER), ThaumcraftApiHelper.makeCrystal(Aspect.CRYSTAL));

        aspects = new AspectList().add(Aspect.ENTROPY, 50).add(Aspect.MAGIC, 25).add(Aspect.UNDEAD, 25);
        addInfusionRecipe("ROD_ELEMENTAL.6", "ROD_ELEMENTAL@1", new ItemStack(TW_Items.itemWandRod, 1, 6), 3, new ItemStack(Items.BONE), aspects, ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY), ThaumcraftApiHelper.makeCrystal(Aspect.UNDEAD));

        aspects = new AspectList().add(Aspect.AIR, 25).add(Aspect.FIRE, 25).add(Aspect.WATER, 25).add(Aspect.EARTH, 25).add(Aspect.ORDER, 25).add(Aspect.ENTROPY, 25).add(Aspect.MAGIC, 25).add(Aspect.AURA, 25);
        addInfusionRecipe("ROD_SILVERWOOD.1", "ROD_SILVERWOOD@1", new ItemStack(TW_Items.itemWandRod, 1, 7), 5, new ItemStack(BlocksTC.logSilverwood), aspects, ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER), ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY), ThaumcraftApiHelper.makeCrystal(Aspect.MAGIC));

        aspects = new AspectList().add(Aspect.AIR, 50).add(Aspect.FIRE, 50).add(Aspect.WATER, 50).add(Aspect.EARTH, 50).add(Aspect.ORDER, 50).add(Aspect.ENTROPY, 50).add(Aspect.MAGIC, 100).add(Aspect.AURA, 50);
        addInfusionRecipe("CORE_PRIMAL.1", "CORE_PRIMAL@1", new ItemStack(TW_Items.itemStaffCore, 1, 8), 7, new ItemStack(TW_Items.itemWandRod, 1, 7), aspects, new ItemStack(TW_Items.itemWandRod, 1, 1), new ItemStack(TW_Items.itemWandRod, 1, 2), new ItemStack(TW_Items.itemWandRod, 1, 3), new ItemStack(ModItems.AQUAREIA_GEM), new ItemStack(TW_Items.itemWandRod, 1, 4), new ItemStack(TW_Items.itemWandRod, 1, 5), new ItemStack(TW_Items.itemWandRod, 1, 6), new ItemStack(ModItems.AQUAREIA_GEM));
    }

    public static void addShapedOreRecipe(String name, ItemStack output, Object... params) {
        ResourceLocation location = new ResourceLocation(ThaumicWands.modID, name);
        ShapedOreRecipe recipe = new ShapedOreRecipe(location, output, params);
        recipe.setRegistryName(location);
        ForgeRegistries.RECIPES.register(recipe);
        recipes.put(name, location);
    }

    public static void addShapedArcaneRecipe(String name, String research, ItemStack output, int vis, AspectList crystals, Object... input) {
        ResourceLocation location = new ResourceLocation(ThaumicWands.modID, name);
        ShapedArcaneRecipe recipe = new ShapedArcaneRecipe(location, research, vis, crystals, output, input);
        ThaumcraftApi.addArcaneCraftingRecipe(location, recipe);
        recipes.put(name, location);
    }

    public static void addCrucibleRecipe(String name, String research, ItemStack output, Object input, AspectList aspects) {
        ResourceLocation location = new ResourceLocation(ThaumicWands.modID, name);
        CrucibleRecipe recipe = new CrucibleRecipe(research, output, input, aspects);
        ThaumcraftApi.addCrucibleRecipe(location, recipe);
        recipes.put(name, location);
    }

    public static void addInfusionRecipe(String name, String research, ItemStack output, int instability, ItemStack catalyst, AspectList aspects, Object... input) {
        ResourceLocation location = new ResourceLocation(ThaumicWands.modID, name);
        InfusionRecipe recipe = new InfusionRecipe(research, output, instability, aspects, catalyst, input);
        ThaumcraftApi.addInfusionCraftingRecipe(location, recipe);
        recipes.put(name, location);
    }

    public static ResourceLocation getNameForRecipe(ItemStack output) {
        ModContainer activeContainer = Loader.instance().activeModContainer();
        ResourceLocation baseLoc = new ResourceLocation(activeContainer.getModId(), output.getItem().getUnlocalizedName());
        ResourceLocation recipeLoc = baseLoc;
        int index = 0;
        while (CraftingManager.REGISTRY.containsKey(recipeLoc)) {
            index++;
            recipeLoc = new ResourceLocation(activeContainer.getModId(), baseLoc.getResourcePath() + "_" + index);
        }
        return recipeLoc;
    }

}
