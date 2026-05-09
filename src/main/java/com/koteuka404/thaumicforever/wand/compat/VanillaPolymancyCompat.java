package com.koteuka404.thaumicforever.wand.compat;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandRod;
import com.koteuka404.thaumicforever.wand.compat.TW_Compat.ICompat;
import com.koteuka404.thaumicforever.wand.item.ItemBaseMeta;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import com.koteuka404.thaumicforever.wand.main.TW_Recipes;
import com.koteuka404.thaumicforever.wand.wand.TW_Wands;
import com.koteuka404.thaumicforever.wand.wand.WandRod;
import com.koteuka404.thaumicforever.wand.wand.updates.UpdateChorusPolymancy;
import com.koteuka404.thaumicforever.wand.wand.updates.UpdateInfernalPolymancy;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class VanillaPolymancyCompat implements ICompat {

    public static Item itemWandRodPoly = new ItemBaseMeta("item_wand_rod_poly", "infernal", "chorus");

    @Override
    public void init() {
        if (!hasRodTag("infernal")) {
            new WandRod("infernal", 1200, new ItemStack(itemWandRodPoly, 1, 0), 28, new UpdateInfernalPolymancy(), "ROD_ELEMENTAL");
        }
        if (!hasRodTag("chorus")) {
            new WandRod("chorus", 1200, new ItemStack(itemWandRodPoly, 1, 1), 28, new UpdateChorusPolymancy(), "ROD_ELEMENTAL");
        }
    }

    @Override
    public void initRecipes() {
        if (!TW_Recipes.recipes.containsKey("ROD_INFERNAL.1")) {
            AspectList infernal = new AspectList().add(Aspect.FIRE, 80).add(Aspect.ENERGY, 60).add(Aspect.MAGIC, 40);
            TW_Recipes.addInfusionRecipe("ROD_INFERNAL.1", "ROD_ELEMENTAL", new ItemStack(itemWandRodPoly, 1, 0), 6,
                    new ItemStack(TW_Items.itemWandRod, 1, 2), infernal,
                    Items.NETHER_STAR, new ItemStack(ItemsTC.salisMundus), Items.BLAZE_POWDER, Items.BLAZE_POWDER);
        }

        if (!TW_Recipes.recipes.containsKey("ROD_CHORUS.1")) {
            AspectList chorus = new AspectList().add(Aspect.ELDRITCH, 70).add(Aspect.ENTROPY, 50).add(Aspect.MAGIC, 40).add(Aspect.DARKNESS, 30);
            TW_Recipes.addInfusionRecipe("ROD_CHORUS.1", "ROD_ELEMENTAL", new ItemStack(itemWandRodPoly, 1, 1), 6,
                    new ItemStack(TW_Items.itemWandRod, 1, 4), chorus,
                    Items.CHORUS_FRUIT, Items.CHORUS_FRUIT, Items.ENDER_EYE, new ItemStack(ItemsTC.salisMundus));
        }
    }

    @Override
    public void initResearch() {
    }

    private static boolean hasRodTag(String tag) {
        for (IWandRod rod : TW_Wands.RODS) {
            if (tag.equals(rod.getTag())) return true;
        }
        return false;
    }
}
