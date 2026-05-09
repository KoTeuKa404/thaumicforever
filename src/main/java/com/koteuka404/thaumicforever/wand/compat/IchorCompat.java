package com.koteuka404.thaumicforever.wand.compat;

import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.wand.compat.TW_Compat.ICompat;
import com.koteuka404.thaumicforever.wand.item.ItemBaseMeta;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import com.koteuka404.thaumicforever.wand.main.TW_Recipes;
import com.koteuka404.thaumicforever.wand.wand.WandCap;
import com.koteuka404.thaumicforever.wand.wand.WandRod;
import com.koteuka404.thaumicforever.wand.wand.updates.UpdateIchor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class IchorCompat implements ICompat {

    public static Item itemWandCapIchor = new ItemBaseMeta("item_wand_cap_ichor", "ichorium_inert", "ichorium");
    public static Item itemWandRodIchor = new ItemBaseMeta("item_wand_rod_ichor", "ichorcloth");

    private static final String[] ORE_ICHOR_NUGGET = new String[]{"nuggetIchorium", "nuggetIchor"};
    private static final String[] ORE_ICHOR_INGOT = new String[]{"ingotIchorium", "ingotIchor"};
    private static final String[] ORE_ICHOR_CLOTH = new String[]{"ichorcloth", "clothIchor", "itemIchorcloth", "fabricIchor", "materialIchorCloth"};

    @Override
    public void init() {
        Object cloth = resolveClothIngredient();

        AspectList capDiscount = new AspectList()
                .add(Aspect.AIR, 2)
                .add(Aspect.FIRE, 2)
                .add(Aspect.WATER, 2)
                .add(Aspect.EARTH, 2)
                .add(Aspect.ORDER, 2)
                .add(Aspect.ENTROPY, 2);
        new WandCap("ichorium", 0.65F, capDiscount, new ItemStack(itemWandCapIchor, 1, 1), 45);

        if (cloth != null) {
            new WandRod("ichorcloth", 900, new ItemStack(itemWandRodIchor, 1, 0), 35, new UpdateIchor());
        }
    }

    @Override
    public void initRecipes() {
        Object metal = resolveMetalIngredient();
        Object cloth = resolveClothIngredient();
        if (metal == null) return;

        AspectList crystals = new AspectList()
                .add(Aspect.AIR, 12)
                .add(Aspect.FIRE, 12)
                .add(Aspect.WATER, 12)
                .add(Aspect.EARTH, 12)
                .add(Aspect.ORDER, 12)
                .add(Aspect.ENTROPY, 12);
        if (!TW_Recipes.recipes.containsKey("CAP_ICHORIUM.1")) {
            TW_Recipes.addShapedArcaneRecipe("CAP_ICHORIUM.1", "CAP_VOID", new ItemStack(itemWandCapIchor, 1, 0), 180, crystals, "nnn", "n n", 'n', metal);
        }

        AspectList capAspects = new AspectList()
                .add(Aspect.ELDRITCH, 80)
                .add(Aspect.AURA, 60)
                .add(Aspect.MAGIC, 60)
                .add(Aspect.VOID, 40);
        if (!TW_Recipes.recipes.containsKey("CAP_ICHORIUM.2")) {
            TW_Recipes.addInfusionRecipe("CAP_ICHORIUM.2", "CAP_VOID", new ItemStack(itemWandCapIchor, 1, 1), 7, new ItemStack(itemWandCapIchor, 1, 0), capAspects,
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }

        AspectList rodAspects = new AspectList()
                .add(Aspect.ELDRITCH, 80)
                .add(Aspect.AURA, 80)
                .add(Aspect.MAGIC, 60)
                .add(Aspect.ENERGY, 40)
                .add(Aspect.VOID, 40);
        if (cloth != null && !TW_Recipes.recipes.containsKey("ROD_ICHORCLOTH.1")) {
            TW_Recipes.addInfusionRecipe("ROD_ICHORCLOTH.1", "ROD_SILVERWOOD", new ItemStack(itemWandRodIchor, 1, 0), 8, new ItemStack(TW_Items.itemWandRod, 1, 7), rodAspects,
                    cloth, cloth, metal, metal, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
        }
    }

    @Override
    public void initResearch() {
    }

    private static String firstExistingOre(String[] names) {
        for (String name : names) {
            if (OreDictionary.doesOreNameExist(name) && !OreDictionary.getOres(name).isEmpty()) {
                return name;
            }
        }
        return null;
    }

    private static String resolveMetalOre() {
        String exact = firstExistingOre(ORE_ICHOR_NUGGET);
        if (exact != null) return exact;
        exact = firstExistingOre(ORE_ICHOR_INGOT);
        if (exact != null) return exact;
        String byNugget = findOreContaining("nugget", "ichor");
        if (byNugget != null) return byNugget;
        return findOreContaining("ingot", "ichor");
    }

    private static String resolveClothOre() {
        String exact = firstExistingOre(ORE_ICHOR_CLOTH);
        if (exact != null) return exact;
        String byToken = findOreContaining("ichor", "cloth");
        if (byToken != null) return byToken;
        return findOreContaining("ichor", "fabric");
    }

    private static String findOreContaining(String... tokens) {
        for (String oreName : OreDictionary.getOreNames()) {
            String lower = oreName.toLowerCase();
            boolean allMatch = true;
            for (String token : tokens) {
                if (!lower.contains(token)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch && OreDictionary.doesOreNameExist(oreName) && !OreDictionary.getOres(oreName).isEmpty()) {
                return oreName;
            }
        }
        return null;
    }

    private static Object resolveMetalIngredient() {
        ItemStack configuredNugget = parseConfiguredStack(ModConfig.ichorNuggetItem);
        if (!configuredNugget.isEmpty()) return configuredNugget;

        String ore = resolveMetalOre();
        if (ore != null && ore.toLowerCase().contains("nugget")) return ore;
        ItemStack tinkererNugget = findKamiresourceByOreTokens("nugget", "ichor");
        if (!tinkererNugget.isEmpty()) return tinkererNugget;
        ItemStack nugget = findRegistryItem("ichor", "nugget");
        if (!nugget.isEmpty()) return nugget;
        if (ore != null) return ore;
        ItemStack tinkererIngot = GameRegistry.makeItemStack("thaumictinkerer:kamiresource", 3, 1, null);
        if (tinkererIngot != null && !tinkererIngot.isEmpty()) return tinkererIngot;
        ItemStack ingot = findRegistryItem("ichor", "ingot");
        if (!ingot.isEmpty()) return ingot;
        return null;
    }

    private static Object resolveClothIngredient() {
        ItemStack configuredCloth = parseConfiguredStack(ModConfig.ichorClothItem);
        if (!configuredCloth.isEmpty()) return configuredCloth;

        ItemStack tinkererCloth = GameRegistry.makeItemStack("thaumictinkerer:kamiresource", 4, 1, null);
        if (tinkererCloth != null && !tinkererCloth.isEmpty()) return tinkererCloth;
        String ore = resolveClothOre();
        if (ore != null) return ore;
        ItemStack cloth = findRegistryItem("ichor", "cloth");
        if (!cloth.isEmpty()) return cloth;
        return findRegistryItem("ichor", "fabric");
    }

    private static ItemStack findRegistryItem(String... tokens) {
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            if (item == null || item.getRegistryName() == null) continue;
            String ns = item.getRegistryName().getResourceDomain().toLowerCase();
            String path = item.getRegistryName().getResourcePath().toLowerCase();
            String full = ns + ":" + path;
            boolean ok = true;
            for (String t : tokens) {
                if (!full.contains(t.toLowerCase())) {
                    ok = false;
                    break;
                }
            }
            if (ok) return new ItemStack(item, 1, 0);
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack findKamiresourceByOreTokens(String... tokens) {
        Item item = ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("thaumictinkerer", "kamiresource"));
        if (item == null) return ItemStack.EMPTY;
        for (int meta = 0; meta < 16; meta++) {
            ItemStack stack = new ItemStack(item, 1, meta);
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int id : ids) {
                String ore = OreDictionary.getOreName(id);
                if (ore == null) continue;
                String lower = ore.toLowerCase();
                boolean ok = true;
                for (String t : tokens) {
                    if (!lower.contains(t.toLowerCase())) {
                        ok = false;
                        break;
                    }
                }
                if (ok) return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack parseConfiguredStack(String raw) {
        if (raw == null) return ItemStack.EMPTY;
        String spec = raw.trim();
        if (spec.isEmpty()) return ItemStack.EMPTY;

        String id = spec;
        int meta = 0;

        int atIdx = spec.indexOf('@');
        if (atIdx >= 0) {
            id = spec.substring(0, atIdx).trim();
            String metaPart = spec.substring(atIdx + 1).trim();
            if (!metaPart.isEmpty()) {
                try {
                    meta = Integer.parseInt(metaPart);
                } catch (NumberFormatException ignored) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (id.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = GameRegistry.makeItemStack(id, meta, 1, null);
        return stack != null ? stack : ItemStack.EMPTY;
    }
}
