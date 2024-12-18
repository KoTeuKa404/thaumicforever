package com.koteuka404.thaumicforever;

import net.minecraftforge.common.config.Config;

@Config(modid = ThaumicForever.MODID)
public class ModConfig {

    @Config.Name("General Settings")
    public static final GeneralSettings general = new GeneralSettings();

    public static class GeneralSettings {

        @Config.Name("Enable mechanismComplex Recipe")
        @Config.Comment("If true, the mechanismComplex recipe will be enabled.")
        public boolean enableMechanismComplexRecipe = true;

        @Config.Name("Enable Ancient Ore Generation")
        @Config.Comment("If true, Ancient ores will be generated.")
        public boolean enableAncientOreGeneration = true;

        @Config.Name("Enable Aquareia Ore Generation")
        @Config.Comment("If true, Aquareia ore will be generated.")
        public boolean enableAquareiaOreGeneration = true;

        @Config.Name("Enable Standard Ore Generation")
        @Config.Comment("If true, standard ores like Copper, Tin, Lead, etc. will be generated.")
        public boolean enableStandardOreGeneration = true;

        @Config.Name("Enable Thaumic Event Handler")
        @Config.Comment("If true, the ThaumicEventHandler will remove event with mini stone(garden of glass).")
        public boolean enableThaumicEventHandler = true;

        @Config.Name("Research List")
        @Config.Comment("List of researches that can be added dynamically.")
        public String[] researchList = {
            // "ETERNAL_BLADE",
            // "TELEPORT",
            // "STUFF"
            // ^^^ add like this 
        };
        
    }
}
