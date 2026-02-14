package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
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
            new ResourceLocation("thaumicforever", "textures/gui/gui_research_back_over.png"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicForever.MODID, "research/forever_research"));
        
        ResearchCategories.registerCategory("PRIMAL","RESEARCHPRIMAL",
            new AspectList().add(Aspect.ENTROPY, 45).add(Aspect.ORDER, 45),
            new ResourceLocation("thaumicforever", "textures/misc/creative_node.png"),
            new ResourceLocation("thaumicforever", "textures/gui/test.png"),
            new ResourceLocation("thaumicforever", "textures/gui/test.png"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicForever.MODID, "research/primal_research"));
        
        if (Loader.isModLoaded("oldresearch")) {ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicForever.MODID, "research/add_bases"));}
    }
}