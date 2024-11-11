package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;

public class ResearchHandler {

    public static void init() {
        ResearchCategories.registerCategory("THAUMICFOREVER","FIRSTSTEPS", 
            new AspectList().add(Aspect.WATER, 5).add(Aspect.AIR, 5), 
            new ResourceLocation("thaumcraft", "textures/research/r_celestial.png"), 
            new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_2.jpg"), 
            new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation("thaumicforever", "research/forever_research.json"));

    }
}