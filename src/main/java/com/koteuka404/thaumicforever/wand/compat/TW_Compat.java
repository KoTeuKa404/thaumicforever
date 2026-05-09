package com.koteuka404.thaumicforever.wand.compat;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

public class TW_Compat {

    public static ArrayList<CompatEntry> compats = Lists.newArrayList();

    public static void preInit(FMLPreInitializationEvent e) {
        // Items from compat classes should always register, but recipes/research
        // are enabled only when the target mod is loaded.
        addCompat("thaumadditions", new ThaumicAdditionsCompat());
        addCompat("thaumicbases", new ThaumicBasesCompat());
        addCompat("planarartifice", new PlanarArtificeCompat());
        addCompat(new VanillaPolymancyCompat());
        addCompat("botania", new BotaniaCompat());
        addCompat("bloodmagic", new BloodMagicCompatTF());
        addCompat("embers", new EmbersCompatTF());
        addCompat(new IchorCompat());
    }

    public static void init() {
        for (CompatEntry entry : compats)
            entry.compat.init();
    }

    public static void initRecipes() {
        for (CompatEntry entry : compats) {
            if (entry.isEnabled()) {
                entry.compat.initRecipes();
            }
        }
    }

    public static void initResearch() {
        for (CompatEntry entry : compats) {
            if (entry.isEnabled()) {
                entry.compat.initResearch();
            }
        }
    }

    public static void loadComplete() {
        for (CompatEntry entry : compats) {
            if (entry.isEnabled()) {
                entry.compat.loadComplete();
            }
        }
    }


    public static interface ICompat {

        public void init();

        public void initRecipes();

        public void initResearch();

        public default void loadComplete() {
        }

    }

    public static ItemStack getItem(String name, int meta) {
        return GameRegistry.makeItemStack(name, meta, 1, null);
    }

    private static void addCompat(ICompat compat) {
        compats.add(new CompatEntry(compat, null));
    }

    private static void addCompat(String requiredModId, ICompat compat) {
        compats.add(new CompatEntry(compat, requiredModId));
    }

    private static class CompatEntry {
        final ICompat compat;
        final String requiredModId;

        CompatEntry(ICompat compat, String requiredModId) {
            this.compat = compat;
            this.requiredModId = requiredModId;
        }

        boolean isEnabled() {
            return requiredModId == null || Loader.isModLoaded(requiredModId);
        }
    }

}
