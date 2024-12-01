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
            // new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_2.jpg"), 
            new ResourceLocation("thaumicforever", "textures/gui/back1.png"), 
            new ResourceLocation("thaumicforever", "textures/gui/back1.png"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation("thaumicforever", "research/forever_research.json"));
        
        ResearchCategories.registerCategory("PRIMAL","RESEARCHPRIMAL", 
            new AspectList().add(Aspect.WATER, 15).add(Aspect.AIR, 15).add(Aspect.EARTH, 15).add(Aspect.FIRE, 15).add(Aspect.ENTROPY, 15).add(Aspect.ORDER, 15), 
            new ResourceLocation("thaumicforever", "textures/misc/creative_node.png"), 
            new ResourceLocation("thaumicforever", "textures/gui/test.jpg"), 
            new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation("thaumicforever", "research/primal_research.json"));

    }
}