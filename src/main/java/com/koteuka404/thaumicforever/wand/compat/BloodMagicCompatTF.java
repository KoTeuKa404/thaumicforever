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
import com.koteuka404.thaumicforever.wand.wand.updates.UpdateBloodPolymancy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class BloodMagicCompatTF implements ICompat {

    public static Item itemWandCapBM = new ItemBaseMeta("item_wand_cap_bm", "alchemical");
    public static Item itemWandRodBM = new ItemBaseMeta("item_wand_rod_bm", "blood_inert", "blood");

    @Override
    public void init() {
        if (!hasCapTag("alchemical")) {
            new WandCap("alchemical", 0.90F,
                    new AspectList().add(Aspect.WATER, 1).add(Aspect.LIFE, 1),
                    new ItemStack(itemWandCapBM, 1, 0), 30, "CAP_MATERIAL_ALL");
        }
        if (!hasRodTag("blood")) {
            new WandRod("blood", 850, new ItemStack(itemWandRodBM, 1, 1), 34, new UpdateBloodPolymancy(), "ROD_SILVERWOOD");
        }
    }

    @Override
    public void initRecipes() {
        Object weakOrb = resolveOrbIngredient(
                new String[]{"weakBloodOrb", "bloodOrbWeak", "orbWeakBlood"},
                "tfWeakBloodOrb",
                bm("bloodmagic:weakbloodorb", 0),
                orbByKey("bloodmagic:weak", 0),
                orbByLevel(0),
                bm("bloodmagic:blood_orb", 0)
        );

        Object apprenticeOrb = resolveOrbIngredient(
                new String[]{"apprenticeBloodOrb", "bloodOrbApprentice", "orbApprenticeBlood"},
                "tfApprenticeBloodOrb",
                bm("bloodmagic:apprenticebloodorb", 0),
                orbByKey("bloodmagic:apprentice", 1),
                orbByLevel(1)
        );

        if (!isValidIngredient(apprenticeOrb)) apprenticeOrb = weakOrb;

        if (isValidIngredient(weakOrb) && !TW_Recipes.recipes.containsKey("CAP_ALCHEMICAL.1")) {
            AspectList capAspects = new AspectList().add(Aspect.LIFE, 40).add(Aspect.MAGIC, 30).add(Aspect.WATER, 25);
            TW_Recipes.addInfusionRecipe("CAP_ALCHEMICAL.1", "CAP_MATERIAL_ALL", new ItemStack(itemWandCapBM, 1, 0), 5,
                    new ItemStack(TW_Items.itemWandCap, 1, 2), capAspects,
                    weakOrb, new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (isValidIngredient(weakOrb) && !TW_Recipes.recipes.containsKey("ROD_BLOOD.1")) {
            AspectList rodInert = new AspectList().add(Aspect.LIFE, 60).add(Aspect.MAGIC, 40).add(Aspect.ENERGY, 25);
            TW_Recipes.addInfusionRecipe("ROD_BLOOD.1", "ROD_SILVERWOOD", new ItemStack(itemWandRodBM, 1, 0), 6,
                    new ItemStack(TW_Items.itemWandRod, 1, 7), rodInert,
                    weakOrb, "dustRedstone", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        if (isValidIngredient(apprenticeOrb) && !TW_Recipes.recipes.containsKey("ROD_BLOOD.2")) {
            AspectList rodAwake = new AspectList().add(Aspect.LIFE, 90).add(Aspect.MAGIC, 60).add(Aspect.ELDRITCH, 25);
            TW_Recipes.addInfusionRecipe("ROD_BLOOD.2", "ROD_SILVERWOOD", new ItemStack(itemWandRodBM, 1, 1), 6,
                    new ItemStack(itemWandRodBM, 1, 0), rodAwake,
                    apprenticeOrb, "dustRedstone", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }
    }

    private static Object resolveOrbIngredient(String[] oreKeys, String fallbackOreKey, ItemStack... fallbacks) {
        for (String ore : oreKeys) {
            if (OreDictionary.doesOreNameExist(ore) && !OreDictionary.getOres(ore).isEmpty()) {
                return ore;
            }
        }

        // Register local ore key with original stacks (with orb-tag) for correct Thaumonomicon display.
        // OreDictionary matching ignores extra runtime NBT on input stacks, so crafting remains stable.
        for (ItemStack stack : fallbacks) {
            if (!isValid(stack)) continue;
            OreDictionary.registerOre(fallbackOreKey, stack.copy());
        }
        if (OreDictionary.doesOreNameExist(fallbackOreKey) && !OreDictionary.getOres(fallbackOreKey).isEmpty()) {
            return fallbackOreKey;
        }

        for (ItemStack stack : fallbacks) {
            if (isValid(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void initResearch() {
    }

    private static ItemStack bm(String name, int meta) {
        return GameRegistry.makeItemStack(name, meta, 1, null);
    }

    private static ItemStack orbByLevel(int orbLevel) {
        Item orbItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("bloodmagic", "blood_orb"));
        if (orbItem == null) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(orbItem, 1, 0);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("orbLevel", orbLevel);
        stack.setTagCompound(tag);
        return stack;
    }

    private static ItemStack orbByKey(String orbKey, int orbLevelFallback) {
        Item orbItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("bloodmagic", "blood_orb"));
        if (orbItem == null) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(orbItem, 1, 0);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("orb", orbKey);
        tag.setInteger("orbLevel", orbLevelFallback);
        stack.setTagCompound(tag);
        return stack;
    }

    private static boolean isValid(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }

    private static boolean isValidIngredient(Object ingredient) {
        if (ingredient instanceof String) return !((String) ingredient).isEmpty();
        if (ingredient instanceof ItemStack) return isValid((ItemStack) ingredient);
        return false;
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
