package com.koteuka404.thaumicforever.wand.compat;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandCap;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandRod;
import com.koteuka404.thaumicforever.wand.compat.TW_Compat.ICompat;
import com.koteuka404.thaumicforever.wand.item.ItemBaseMeta;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import com.koteuka404.thaumicforever.wand.main.TW_Recipes;
import com.koteuka404.thaumicforever.wand.wand.TW_Wands;
import com.koteuka404.thaumicforever.wand.wand.WandCap;
import com.koteuka404.thaumicforever.wand.wand.WandRod;
import com.koteuka404.thaumicforever.wand.wand.updates.UpdateArchaicPolymancy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class EmbersCompatTF implements ICompat {

    public static Item itemWandCapEmbers = new ItemBaseMeta("item_wand_cap_embers", "dawnstone_inert", "dawnstone");
    public static Item itemWandRodEmbers = new ItemBaseMeta("item_wand_rod_embers", "archaic_inert", "archaic");

    @Override
    public void init() {
        if (!hasCapTag("dawnstone")) {
            new WandCap("dawnstone", 0.95F,
                    new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1),
                    new ItemStack(itemWandCapEmbers, 1, 1), 24, "CAP_MATERIAL_ALL");
        }
        if (!hasRodTag("archaic")) {
            new WandRod("archaic", 700, new ItemStack(itemWandRodEmbers, 1, 1), 30, new UpdateArchaicPolymancy(), "ROD_SILVERWOOD");
        }
    }

    @Override
    public void initRecipes() {
        String dawnstone = resolveOre("nuggetDawnstone", "ingotDawnstone", "dawnstone");
        ItemStack emberCrystal = getAny("embers:crystal_ember", "embers:ember_crystal");
        ItemStack emberShard = getAny("embers:shard_ember");

        if (dawnstone != null && !TW_Recipes.recipes.containsKey("CAP_DAWNSTONE.1")) {
            AspectList crystals = new AspectList().add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.ORDER, 4);
            TW_Recipes.addShapedArcaneRecipe("CAP_DAWNSTONE.1", "CAP_MATERIAL_ALL",
                    new ItemStack(itemWandCapEmbers, 1, 0), 28, crystals, "nnn", "n n", 'n', dawnstone);
        }

        if (isValid(emberCrystal) && !TW_Recipes.recipes.containsKey("CAP_DAWNSTONE.2")) {
            AspectList aspects = new AspectList().add(Aspect.FIRE, 45).add(Aspect.ENERGY, 35).add(Aspect.MAGIC, 25);
            TW_Recipes.addInfusionRecipe("CAP_DAWNSTONE.2", "CAP_MATERIAL_ALL",
                    new ItemStack(itemWandCapEmbers, 1, 1), 4, new ItemStack(itemWandCapEmbers, 1, 0), aspects,
                    emberCrystal, new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (isValid(emberShard) && !TW_Recipes.recipes.containsKey("ROD_ARCHAIC.1")) {
            AspectList inert = new AspectList().add(Aspect.FIRE, 70).add(Aspect.ENERGY, 50).add(Aspect.EARTH, 40).add(Aspect.MECHANISM, 30);
            TW_Recipes.addInfusionRecipe("ROD_ARCHAIC.1", "ROD_SILVERWOOD",
                    new ItemStack(itemWandRodEmbers, 1, 0), 7, new ItemStack(TW_Items.itemWandRod, 1, 4), inert,
                    emberShard, emberShard, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (isValid(emberCrystal) && !TW_Recipes.recipes.containsKey("ROD_ARCHAIC.2")) {
            AspectList awake = new AspectList().add(Aspect.FIRE, 90).add(Aspect.ENERGY, 70).add(Aspect.MAGIC, 45).add(Aspect.DARKNESS, 25);
            TW_Recipes.addInfusionRecipe("ROD_ARCHAIC.2", "ROD_SILVERWOOD",
                    new ItemStack(itemWandRodEmbers, 1, 1), 7, new ItemStack(itemWandRodEmbers, 1, 0), awake,
                    emberCrystal, emberCrystal, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }
    }

    @Override
    public void initResearch() {
    }

    private static String resolveOre(String preferredNugget, String preferredIngot, String tokenFallback) {
        if (OreDictionary.doesOreNameExist(preferredNugget) && !OreDictionary.getOres(preferredNugget).isEmpty()) return preferredNugget;
        if (OreDictionary.doesOreNameExist(preferredIngot) && !OreDictionary.getOres(preferredIngot).isEmpty()) return preferredIngot;
        for (String oreName : OreDictionary.getOreNames()) {
            String lower = oreName.toLowerCase();
            if (lower.contains(tokenFallback.toLowerCase()) && !OreDictionary.getOres(oreName).isEmpty()) return oreName;
        }
        return null;
    }

    private static ItemStack getAny(String... ids) {
        for (String id : ids) {
            ItemStack stack = GameRegistry.makeItemStack(id, 0, 1, null);
            if (isValid(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isValid(ItemStack stack) {
        return stack != null && !stack.isEmpty();
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
}
