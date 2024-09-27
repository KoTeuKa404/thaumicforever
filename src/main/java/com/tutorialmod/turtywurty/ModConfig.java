package com.tutorialmod.turtywurty;

import net.minecraftforge.common.config.Config;

@Config(modid = ThaumicForever.MODID)
public class ModConfig {

    @Config.Name("General Settings")
    public static final GeneralSettings general = new GeneralSettings();

    public static class GeneralSettings {

        @Config.Name("Enable mechanismComplex Recipe")
        @Config.Comment("If true, the mechanismComplex recipe will be enabled.")
        public boolean enableMechanismComplexRecipe = true;

        // Інші налаштування
    }
    
    // Інші класи конфігурації...
}

