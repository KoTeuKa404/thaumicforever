package com.koteuka404.thaumicforever;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfig {

    private static Configuration config;

    public static boolean enableMechanismComplexRecipe;
    public static boolean enableAncientOreGeneration;
    public static boolean enableAquareiaOreGeneration;
    public static boolean enableStandardOreGeneration;
    public static boolean enableThaumicEventHandler;
    public static boolean enableFunnyStaff;
    public static String[] researchList;
    public static boolean enableThaumicHouseGeneration;
    public static int thaumicHouseSpawnChance;
    public static String[] immediateBaubles;
    public static String[] categoryBaubles;

    public static void loadConfig(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "thaumicforever.cfg");
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig() {
        enableMechanismComplexRecipe = config.getBoolean(
            "Enable mechanismComplex Recipe", "General", true,
            "If true, the mechanismComplex recipe will be enabled."
        );

        enableAncientOreGeneration = config.getBoolean(
            "Enable Ancient Ore Generation", "General", true,
            "If true, Ancient ores will be generated."
        );

        enableAquareiaOreGeneration = config.getBoolean(
            "Enable Aquareia Ore Generation", "General", true,
            "If true, Aquareia ore will be generated."
        );

        enableStandardOreGeneration = config.getBoolean(
            "Enable Standard Ore Generation", "General", true,
            "If true, standard ores like Copper, Tin, Lead, etc. will be generated."
        );

        enableThaumicEventHandler = config.getBoolean(
            "Enable Thaumic Event Handler", "General", true,
            "If true, the ThaumicEventHandler will remove event with mini stone (garden of glass)."
        );

        enableFunnyStaff = config.getBoolean(
            "Enable Funny Staff", "General", true,
            "If true, enables funny staff to be added to the mod."
        );

        researchList = config.getStringList(
            "Research List", "General", new String[]{},
            "List of researches that can be added dynamically."
        );

    

        enableThaumicHouseGeneration = config.getBoolean(
            "Enable Thaumic House Generation", "General", true,
            "If true, the Thaumic House structure will spawn in the Magical Forest biome."
        );

        thaumicHouseSpawnChance = config.getInt(
            "Thaumic House Spawn Chance", "General", 700, 1, Integer.MAX_VALUE,
            "Chance per chunk for a Thaumic House: 1 in N."
        );

        immediateBaubles = config.getStringList(
            "Immediate Slots",
            "Mystic Slots",
            new String[]{ "RING" },
            "BaubleTypes unlocked immediately on first join"
        );

        categoryBaubles = config.getStringList(
            "Category Slots",
            "Mystic Slots",
            new String[]{
                "BASICS=HEAD",
                "ALCHEMY=BELT",
                "AUROMANCY=AMULET",
                "ARTIFICE=BODY",
                "INFUSION=RING",
                "GOLEMANCY=RING",
                "ELDRITCH=CHARM"
            },
            "Mappings CATEGORY=BaubleType for slots unlocked at 100% research"
        );

        if (config.hasChanged()) {
            config.save();
        }
    }
}
