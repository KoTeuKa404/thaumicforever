package com.koteuka404.thaumicforever.wand.main;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

@EventBusSubscriber
public class TW_Research {

    // public static final ResourceLocation iconThaumaturgy = new ResourceLocation(ThaumicWands.modID, "textures/research/icon_thaumaturgy.png");
    // public static final ResourceLocation backThaumaturgy = new ResourceLocation(ThaumicWands.modID, "textures/research/tab_thaumaturgy.jpg");
    // public static final ResourceLocation backOverlay = new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png");
    // public static final AspectList tabAspects = new AspectList().add(Aspect.MAGIC, 20)
    //         .add(Aspect.CRAFT, 15)
    //         .add(Aspect.AURA, 15)
    //         .add(Aspect.PLANT, 15)
    //         .add(Aspect.ENERGY, 10);

    public static void init() {
        // ResearchCategories.registerCategory("THAUMATURGY", "FIRSTSTEPS", tabAspects, iconThaumaturgy, backThaumaturgy, backOverlay);
        // ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWands.modID, "research/basics_override.json"));
        // ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWands.modID, "research/thaumaturgy.json"));
    }

    public static ResearchCategory getCategory(String cat) {
        return ResearchCategories.getResearchCategory(cat);
    }

    public static void postInit() {
    //     ConfigResearch.TCCategories = new String[]{"BASICS", "THAUMATURGY", "ALCHEMY", "AUROMANCY", "ARTIFICE", "INFUSION", "GOLEMANCY", "ELDRITCH"};
    //     LinkedHashMap<String, ResearchCategory> map = new LinkedHashMap<>();
    //     map.put("BASICS", ResearchCategories.researchCategories.get("BASICS"));
    //     map.put("THAUMATURGY", ResearchCategories.researchCategories.get("THAUMATURGY"));
    //     for (String s : ResearchCategories.researchCategories.keySet()) {
    //         if (!map.containsKey(s))
    //             map.put(s, ResearchCategories.researchCategories.get(s));
    //     }

    //     ResearchCategories.researchCategories = map;
    }

}
