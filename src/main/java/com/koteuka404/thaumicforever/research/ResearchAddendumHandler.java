package com.koteuka404.thaumicforever.research;

import java.util.Arrays;

import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.ResearchAddendum;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;

public final class ResearchAddendumHandler {
    private static final String ADVANCED_USE_TEXT = "research.SEALUSE.addenda.thaumicforever.advanced_use";

    private ResearchAddendumHandler() {}

    public static void postInit() {
        addAdvancedUseSealAddendum();
    }

    private static void addAdvancedUseSealAddendum() {
        ResearchEntry sealUse = ResearchCategories.getResearch("SEALUSE");
        if (sealUse == null) return;

        ResearchAddendum addendum = new ResearchAddendum();
        addendum.setText(ADVANCED_USE_TEXT);
        addendum.setRecipes(new ResourceLocation[]{new ResourceLocation(ThaumicForever.MODID, "seal_use_advanced")});
        addendum.setResearch(new String[]{"NEWGOLEMANCY"});

        addAddendumOnce(sealUse, addendum);
    }

    private static void addAddendumOnce(ResearchEntry entry, ResearchAddendum addendum) {
        ResearchAddendum[] existing = entry.getAddenda();
        if (existing != null) {
            for (ResearchAddendum current : existing) {
                if (current != null && addendum.getText().equals(current.getText())) {
                    return;
                }
            }
        }

        ResearchAddendum[] merged = existing == null
                ? new ResearchAddendum[]{addendum}
                : Arrays.copyOf(existing, existing.length + 1);
        if (existing != null) {
            merged[existing.length] = addendum;
        }
        entry.setAddenda(merged);
    }
}
