package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import baubles.api.BaubleType;

public final class ResearchBaubleMapping {
    public static final List<BaubleType> IMMEDIATE = new ArrayList<>();
    public static final Map<String, BaubleType> BY_CATEGORY = new LinkedHashMap<>();

    private ResearchBaubleMapping(){}

    public static void reloadFromConfig() {
        IMMEDIATE.clear();
        BY_CATEGORY.clear();

        for (String s : ModConfig.immediateBaubles) {
            try {
                IMMEDIATE.add(BaubleType.valueOf(s.trim().toUpperCase()));
            } catch (IllegalArgumentException x) {
            }
        }

        for (String s : ModConfig.categoryBaubles) {
            String[] a = s.split("=",2);
            if (a.length!=2) continue;
            String cat = a[0].trim().toUpperCase();
            try {
                BaubleType type = BaubleType.valueOf(a[1].trim().toUpperCase());
                BY_CATEGORY.put(cat, type);
            } catch (IllegalArgumentException x) {
            }
        }
    }
}
