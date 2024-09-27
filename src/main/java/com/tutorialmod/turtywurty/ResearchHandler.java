package com.tutorialmod.turtywurty;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;

public class ResearchHandler {

    public static void init() {
        // Реєстрація нової категорії досліджень
        ResearchCategories.registerCategory(
            "THAUMICFOREVER",
            null, // Дослідження, яке повинно бути виконане до відкриття цієї категорії (null для відображення завжди)
            new AspectList(), // Формула аспектів для категорії, залиште порожньою, якщо не потрібно
            new ResourceLocation("thaumcraft", "textures/research/r_celestial.png"), // Іконка для вкладки
            new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_2.jpg"), // Фон для вкладки
            new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png") // Перекриття фону (якщо є)
        );

        // Реєстрація файлів з дослідженнями
        ThaumcraftApi.registerResearchLocation(new ResourceLocation("thaumicforever", "research/thaumcraft_research.json"));
    }
}