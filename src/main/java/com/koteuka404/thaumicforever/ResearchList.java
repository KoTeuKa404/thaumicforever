package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResearchList {

    private static final List<String> HARD_CODED_RESEARCHES = Arrays.asList(
        "ETERNAL_BLADE",
        "TELEPORT",
        "STUFF"
    );

    private static List<String> RESEARCHES = new ArrayList<>();

    /**
     * Ініціалізація списку досліджень із конфігурації та додавання закодованих
     */
    public static void initializeFromConfig() {
        RESEARCHES = new ArrayList<>(Arrays.asList(ModConfig.general.researchList));

        for (String research : HARD_CODED_RESEARCHES) {
            if (!RESEARCHES.contains(research)) {
                RESEARCHES.add(research);
            }
        }

        System.out.println("Ініціалізовано дослідження: " + RESEARCHES);
    }

    /**
     * Отримати випадкове дослідження зі списку
     *
     * @return Назва випадкового дослідження
     */
    public static String getRandomResearch() {
        if (RESEARCHES.isEmpty()) {
            return "Unknown Research";
        }
        return RESEARCHES.get((int) (Math.random() * RESEARCHES.size()));
    }

    /**
     * Додати нове дослідження до списку
     *
     * @param research Назва дослідження
     */
    public static void addResearch(String research) {
        if (!RESEARCHES.contains(research)) {
            RESEARCHES.add(research);
            updateConfig();
        }
    }

    /**
     * Видалити дослідження зі списку
     *
     * @param research Назва дослідження
     */
    public static void removeResearch(String research) {
        if (RESEARCHES.contains(research)) {
            RESEARCHES.remove(research);
            updateConfig();
        }
    }

    /**
     * Отримати всі доступні дослідження
     *
     * @return Список досліджень
     */
    public static List<String> getAllResearches() {
        return new ArrayList<>(RESEARCHES);
    }

    /**
     * Оновлює конфігурацію після додавання або видалення дослідження
     */
    private static void updateConfig() {
        ModConfig.general.researchList = RESEARCHES.toArray(new String[0]);
        System.out.println("Оновлено конфігурацію досліджень: " + Arrays.toString(ModConfig.general.researchList));
    }
}
