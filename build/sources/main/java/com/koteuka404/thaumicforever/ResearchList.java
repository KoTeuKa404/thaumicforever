package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResearchList {

    private static final List<String> HARD_CODED_RESEARCHES = Arrays.asList(
        "ETERNAL_BLADE",
        "TELEPORT",
        "STUFF",
        "PrimordialGuardian",
        "TAINT_AMULET"
    );

    private static List<String> RESEARCHES = new ArrayList<>();

    public static void initializeFromConfig() {
        RESEARCHES = new ArrayList<>(Arrays.asList(ModConfig.researchList));

        for (String research : HARD_CODED_RESEARCHES) {
            if (!RESEARCHES.contains(research)) {
                RESEARCHES.add(research);
            }
        }

        System.out.println("Ініціалізовано дослідження: " + RESEARCHES);
    }

    public static String getRandomResearch() {
        if (RESEARCHES.isEmpty()) {
            return "Unknown Research";
        }
        return RESEARCHES.get((int) (Math.random() * RESEARCHES.size()));
    }

    public static void addResearch(String research) {
        if (!RESEARCHES.contains(research)) {
            RESEARCHES.add(research);
            updateConfig();
        }
    }

    public static void removeResearch(String research) {
        if (RESEARCHES.contains(research)) {
            RESEARCHES.remove(research);
            updateConfig();
        }
    }

    public static List<String> getAllResearches() {
        return new ArrayList<>(RESEARCHES);
    }
    
    private static void updateConfig() {
        ModConfig.researchList = RESEARCHES.toArray(new String[0]);
        System.out.println("Оновлено конфігурацію досліджень: " + Arrays.toString(ModConfig.researchList));
    }
}
