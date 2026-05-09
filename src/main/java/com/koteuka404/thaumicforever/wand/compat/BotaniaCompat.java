package com.koteuka404.thaumicforever.wand.compat;

import com.koteuka404.thaumicforever.wand.compat.TW_Compat.ICompat;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandCap;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandRod;
import com.koteuka404.thaumicforever.wand.item.ItemBaseMeta;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import com.koteuka404.thaumicforever.wand.main.TW_Recipes;
import com.koteuka404.thaumicforever.wand.wand.TW_Wands;
import com.koteuka404.thaumicforever.wand.wand.WandCap;
import com.koteuka404.thaumicforever.wand.wand.WandRod;
import com.koteuka404.thaumicforever.wand.wand.updates.UpdateDreamwood;
import com.koteuka404.thaumicforever.wand.wand.updates.UpdateLivingwood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class BotaniaCompat implements ICompat {

    public static Item itemWandCapBotania = new ItemBaseMeta("item_wand_cap_botania",
            "manasteel_inert", "manasteel",
            "elementium_inert", "elementium",
            "terrasteel");

    public static Item itemWandRodBotania = new ItemBaseMeta("item_wand_rod_botania",
            "livingwood_inert", "livingwood",
            "dreamwood_inert", "dreamwood");

    private static final String[] ORE_MANASTEEL_NUGGET = new String[]{"nuggetManasteel"};
    private static final String[] ORE_MANASTEEL_INGOT = new String[]{"ingotManasteel"};
    private static final String[] ORE_ELEMENTIUM_NUGGET = new String[]{"nuggetElementium"};
    private static final String[] ORE_ELEMENTIUM_INGOT = new String[]{"ingotElementium"};
    private static final String[] ORE_TERRASTEEL_NUGGET = new String[]{"nuggetTerrasteel"};
    private static final String[] ORE_TERRASTEEL_INGOT = new String[]{"ingotTerrasteel"};
    private static final String[] ORE_LIVINGWOOD = new String[]{"logLivingwood", "woodLivingwood", "livingwood"};
    private static final String[] ORE_DREAMWOOD = new String[]{"logDreamwood", "woodDreamwood", "dreamwood"};

    @Override
    public void init() {
        String manasteel = resolveMetalOre(ORE_MANASTEEL_NUGGET, ORE_MANASTEEL_INGOT, "manasteel");
        String elementium = resolveMetalOre(ORE_ELEMENTIUM_NUGGET, ORE_ELEMENTIUM_INGOT, "elementium");
        String terrasteel = resolveMetalOre(ORE_TERRASTEEL_NUGGET, ORE_TERRASTEEL_INGOT, "terrasteel");
        String livingwood = resolveAnyOre(ORE_LIVINGWOOD, "livingwood");
        String dreamwood = resolveAnyOre(ORE_DREAMWOOD, "dreamwood");

        if (manasteel != null) {
            if (!hasCapTag("manasteel")) {
                new WandCap("manasteel", 0.90F,
                        new AspectList().add(Aspect.MAGIC, 1).add(Aspect.AURA, 1),
                        new ItemStack(itemWandCapBotania, 1, 1), 20, "CAP_MATERIAL_ALL");
            }
        }
        if (elementium != null) {
            if (!hasCapTag("elementium")) {
                new WandCap("elementium", 0.82F,
                        new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1).add(Aspect.WATER, 1).add(Aspect.EARTH, 1),
                        new ItemStack(itemWandCapBotania, 1, 3), 28, "CAP_MATERIAL_ALL");
            }
        }
        if (terrasteel != null) {
            if (!hasCapTag("terrasteel")) {
                new WandCap("terrasteel", 0.75F,
                        new AspectList().add(Aspect.MAGIC, 2).add(Aspect.AURA, 2).add(Aspect.PLANT, 1),
                        new ItemStack(itemWandCapBotania, 1, 4), 38, "CAP_MATERIAL_ALL");
            }
        }

        if (livingwood != null) {
            if (!hasRodTag("livingwood")) {
                new WandRod("livingwood", 500, new ItemStack(itemWandRodBotania, 1, 1), 18, new UpdateLivingwood(), "ROD_GREATWOOD");
            }
        }
        if (dreamwood != null) {
            if (!hasRodTag("dreamwood")) {
                new WandRod("dreamwood", 800, new ItemStack(itemWandRodBotania, 1, 3), 30, new UpdateDreamwood(), "ROD_SILVERWOOD");
            }
        }
    }

    @Override
    public void initRecipes() {
        String manasteel = resolveMetalOre(ORE_MANASTEEL_NUGGET, ORE_MANASTEEL_INGOT, "manasteel");
        String elementium = resolveMetalOre(ORE_ELEMENTIUM_NUGGET, ORE_ELEMENTIUM_INGOT, "elementium");
        String terrasteel = resolveMetalOre(ORE_TERRASTEEL_NUGGET, ORE_TERRASTEEL_INGOT, "terrasteel");
        String livingwood = resolveAnyOre(ORE_LIVINGWOOD, "livingwood");
        String dreamwood = resolveAnyOre(ORE_DREAMWOOD, "dreamwood");
        ItemStack manaDiamond = getManaDiamond();

        if (manasteel != null && !TW_Recipes.recipes.containsKey("CAP_MANASTEEL.1")) {
            AspectList crystals = new AspectList().add(Aspect.AIR, 5).add(Aspect.WATER, 5).add(Aspect.ORDER, 4);
            TW_Recipes.addShapedArcaneRecipe("CAP_MANASTEEL.1", "CAP_MATERIAL_ALL",
                    new ItemStack(itemWandCapBotania, 1, 0), 28, crystals, "nnn", "n n", 'n', manasteel);
        }

        if (manasteel != null && !TW_Recipes.recipes.containsKey("CAP_MANASTEEL.2")) {
            AspectList aspects = new AspectList().add(Aspect.MAGIC, 40).add(Aspect.AURA, 30).add(Aspect.METAL, 25);
            TW_Recipes.addInfusionRecipe("CAP_MANASTEEL.2", "CAP_MATERIAL_ALL",
                    new ItemStack(itemWandCapBotania, 1, 1), 4, new ItemStack(itemWandCapBotania, 1, 0), aspects,
                    manaDiamond,
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (elementium != null && !TW_Recipes.recipes.containsKey("CAP_ELEMENTIUM.1")) {
            AspectList crystals = new AspectList()
                    .add(Aspect.AIR, 7).add(Aspect.FIRE, 7).add(Aspect.WATER, 7).add(Aspect.EARTH, 7);
            TW_Recipes.addShapedArcaneRecipe("CAP_ELEMENTIUM.1", "CAP_MATERIAL_ALL",
                    new ItemStack(itemWandCapBotania, 1, 2), 44, crystals, "nnn", "n n", 'n', elementium);
        }

        if (elementium != null && !TW_Recipes.recipes.containsKey("CAP_ELEMENTIUM.2")) {
            AspectList aspects = new AspectList().add(Aspect.MAGIC, 60).add(Aspect.AURA, 50).add(Aspect.FLIGHT, 35);
            TW_Recipes.addInfusionRecipe("CAP_ELEMENTIUM.2", "CAP_MATERIAL_ALL",
                    new ItemStack(itemWandCapBotania, 1, 3), 6, new ItemStack(itemWandCapBotania, 1, 2), aspects,
                    manaDiamond, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus));
        }

        if (terrasteel != null && !TW_Recipes.recipes.containsKey("CAP_TERRASTEEL.1")) {
            AspectList crystals = new AspectList()
                    .add(Aspect.AIR, 10).add(Aspect.FIRE, 10).add(Aspect.WATER, 10).add(Aspect.EARTH, 10).add(Aspect.ORDER, 10);
            TW_Recipes.addShapedArcaneRecipe("CAP_TERRASTEEL.1", "CAP_MATERIAL_ALL",
                    new ItemStack(itemWandCapBotania, 1, 4), 80, crystals, "nnn", "n n", 'n', terrasteel);
        }

        if (livingwood != null && !TW_Recipes.recipes.containsKey("ROD_LIVINGWOOD.1")) {
            AspectList aspects = new AspectList().add(Aspect.PLANT, 60).add(Aspect.MAGIC, 40).add(Aspect.LIFE, 30);
            TW_Recipes.addInfusionRecipe("ROD_LIVINGWOOD.1", "ROD_GREATWOOD",
                    new ItemStack(itemWandRodBotania, 1, 0), 4, new ItemStack(TW_Items.itemWandRod, 1, 0), aspects,
                    livingwood, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (livingwood != null && !TW_Recipes.recipes.containsKey("ROD_LIVINGWOOD.2")) {
            AspectList awaken = new AspectList().add(Aspect.AURA, 35).add(Aspect.LIFE, 35).add(Aspect.MAGIC, 30);
            TW_Recipes.addInfusionRecipe("ROD_LIVINGWOOD.2", "ROD_GREATWOOD",
                    new ItemStack(itemWandRodBotania, 1, 1), 3, new ItemStack(itemWandRodBotania, 1, 0), awaken,
                    livingwood, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (dreamwood != null && !TW_Recipes.recipes.containsKey("ROD_DREAMWOOD.1")) {
            AspectList aspects = new AspectList().add(Aspect.AURA, 90).add(Aspect.MAGIC, 70).add(Aspect.PLANT, 50).add(Aspect.ELDRITCH, 20);
            TW_Recipes.addInfusionRecipe("ROD_DREAMWOOD.1", "ROD_SILVERWOOD",
                    new ItemStack(itemWandRodBotania, 1, 2), 6, new ItemStack(itemWandRodBotania, 1, 1), aspects,
                    dreamwood, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (dreamwood != null && !TW_Recipes.recipes.containsKey("ROD_DREAMWOOD.2")) {
            AspectList awaken = new AspectList().add(Aspect.AURA, 60).add(Aspect.MAGIC, 60).add(Aspect.SENSES, 40);
            TW_Recipes.addInfusionRecipe("ROD_DREAMWOOD.2", "ROD_SILVERWOOD",
                    new ItemStack(itemWandRodBotania, 1, 3), 4, new ItemStack(itemWandRodBotania, 1, 2), awaken,
                    dreamwood, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }
    }

    @Override
    public void initResearch() {
    }

    private static String resolveMetalOre(String[] preferredNuggets, String[] preferredIngots, String tokenFallback) {
        String exact = firstExistingOre(preferredNuggets);
        if (exact != null) return exact;
        exact = firstExistingOre(preferredIngots);
        if (exact != null) return exact;
        return findOreContaining(tokenFallback);
    }

    private static String resolveAnyOre(String[] preferred, String tokenFallback) {
        String exact = firstExistingOre(preferred);
        if (exact != null) return exact;
        return findOreContaining(tokenFallback);
    }

    private static String firstExistingOre(String[] names) {
        for (String name : names) {
            if (OreDictionary.doesOreNameExist(name) && !OreDictionary.getOres(name).isEmpty()) {
                return name;
            }
        }
        return null;
    }

    private static String findOreContaining(String token) {
        for (String oreName : OreDictionary.getOreNames()) {
            String lower = oreName.toLowerCase();
            if (lower.contains(token.toLowerCase())
                    && OreDictionary.doesOreNameExist(oreName)
                    && !OreDictionary.getOres(oreName).isEmpty()) {
                return oreName;
            }
        }
        return null;
    }

    private static boolean hasCapTag(String tag) {
        for (IWandCap cap : TW_Wands.CAPS) {
            if (tag.equals(cap.getTag())) return true;
        }
        return false;
    }

    private static boolean hasRodTag(String tag) {
        for (IWandRod rod : TW_Wands.RODS) {
            if (tag.equals(rod.getTag())) return true;
        }
        return false;
    }

    private static ItemStack getManaDiamond() {
        ItemStack stack = GameRegistry.makeItemStack("botania:manaresource", 2, 1, null);
        if (stack != null && !stack.isEmpty()) return stack;
        return new ItemStack(ItemsTC.salisMundus);
    }
}
