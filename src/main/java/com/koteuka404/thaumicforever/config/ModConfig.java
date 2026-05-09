package com.koteuka404.thaumicforever.config;

import com.koteuka404.thaumicforever.event.ThaumicEventHandler;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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
    public static boolean enableMysticGuiButton;
    public static String[] immediateBaubles;
    public static String[] categoryBaubles;
    public static boolean enableSilkTouchFix;
    public static int obsidianTotemChance;
    public static int hilltopStonesChance;
    public static int focus4MaxComplexity;
    public static int focus5UltimateComplexity;
    public static int mazeChance;
    public static int mazeMaxAliveSkeletonsPerBox;
    public static int mazeMaxTotalSkeletonSpawnsPerBox;
    public static boolean enableOldResearch;
    public static boolean enableOldResearchOnly;
    public static boolean debugCrucible;
    public static boolean debugAspectDump;
    public static boolean sinisterNodeSpreadsEerieBiome;
    public static boolean enableEerieDayMobSpawns;
    public static String aspectDumpFile;
    public static String[] vanillaPotAllowedPlants;
    public static String ichorNuggetItem;
    public static String ichorClothItem;

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
            "Enable Thaumic Event Handler", "General", false,
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
            "Thaumic House Spawn Chance", "General", 300, 1, Integer.MAX_VALUE,
            "Chance per chunk for a Thaumic House: 1 in N."
        );

        enableMysticGuiButton = config.getBoolean(
            "Enable Mystic GUI Button",
            "Mystic Slots",
            true,
            "If false, hides the Mystic slots toggle button from inventory GUIs and blocks opening the Mystic GUI through that button."
        );

        immediateBaubles = config.getStringList(
            "Immediate Slots",
            "Mystic Slots",
            new String[]{ "RING","RING" },
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

        enableSilkTouchFix = config.getBoolean(
            "Enable Silk Touch Fix", "General", false,
            "If true, Silk Touch will drop correct iron/gold ore when industrialupgrade breaks it."
        );

        enableOldResearch = config.getBoolean(
            "Enable Old Research", "Integration", true,
            "If false, disables the embedded Old Research mod."
        );

        enableOldResearchOnly = config.getBoolean(
            "Enable Old Research Only", "Integration", false,
            "If true, removes Thaumcraft observation/theory requirements and uses OldResearch notes only."
        );

        debugCrucible = config.getBoolean(
            "Enable Crucible Debug", "Debug", false,
            "If true, logs crucible recipe owners and overrides at load complete."
        );

        debugAspectDump = config.getBoolean(
            "Enable Aspect Dump", "Debug", false,
            "If true, writes a list of all item stacks and their aspects to a file at load complete."
        );

        sinisterNodeSpreadsEerieBiome = config.getBoolean(
            "Sinister Node Spreads Eerie Biome", "Nodes", true,
            "If true, sinister nodes convert nearby biome to Eerie over time."
        );

        enableEerieDayMobSpawns = config.getBoolean(
            "Enable Eerie Day Mob Spawns", "World",
            true,
            "If true, hostile mobs can also spawn during daytime in Eerie biome."
        );

        aspectDumpFile = config.getString(
            "Aspect Dump File", "Debug", "aspect_dump.txt",
            "Output filename (relative to config folder) for the aspect dump."
        );

        vanillaPotAllowedPlants = config.getStringList(
            "Vanilla Flower Pot Allowed Plants",
            "Integration",
            new String[] {
                "thaumicforever:red_rose",
                "thaumicforever:blue_rose",
                "thaumcraft:sapling_silverwood",
                "thaumcraft:sapling_greatwood",
                "thaumcraft:shimmerleaf",
                "thaumcraft:cinderpearl",
                "thaumcraft:vishroom"
            },
            "Extra plants allowed in vanilla flower pot via ThaumicForever handler. Format: modid:block or modid:block@meta"
        );

        ichorNuggetItem = config.getString(
            "Ichor Nugget Item",
            "Wand Compat",
            "thaumictinkerer:kamiresource@3",
            "Preferred ichor nugget item for Ichor cap recipes. Format: modid:item@meta (meta optional)."
        );

        ichorClothItem = config.getString(
            "Ichor Cloth Item",
            "Wand Compat",
            "thaumictinkerer:kamiresource@4",
            "Preferred ichor cloth item for Ichor rod recipes. Format: modid:item@meta (meta optional)."
        );

        obsidianTotemChance = config.getInt(
            "Obsidian Totem Spawn Chance","WorldGen",300, 1,   Integer.MAX_VALUE,
            "Chance per chunk for a Obsidian Totem(default 300): 1 in N."
        );

        hilltopStonesChance = config.getInt(
            "Hilltop Stones Spawn Chance","WorldGen", 40, 1,   Integer.MAX_VALUE,
            "Chance per chunk for a Hilltop Stones(default  40): 1 in N."
        );

        focus4MaxComplexity = config.getInt(
            "Focus4 Complexity", "Foci", 80, 1, 1000,
            "Complexity (cost) for focus_4 (Focus Max)."
        );

        focus5UltimateComplexity = config.getInt(
            "Focus5 Complexity", "Foci", 120, 1, 1000,
            "Complexity  for focus_5 (Focus Ultimate)."
        );

        mazeChance = config.getInt(
            "Maze Chance", "Maze", 450, 1, 1000,
            "Chance Maze(in Taiga) spawn."
        );

        mazeMaxAliveSkeletonsPerBox = config.getInt(
            "Maze Max Alive Skeletons Per Labyrinth", "Maze", 80, 1, 5000,
            "Maximum number of maze skeletons alive at the same time in one labyrinth."
        );

        mazeMaxTotalSkeletonSpawnsPerBox = config.getInt(
            "Maze Max Total Skeleton Spawns Per Labyrinth", "Maze", 100, 0, 500000,
            "Hard cap of total maze skeleton spawns per labyrinth. 0 disables the cap."
        );

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static boolean isAllowedVanillaPotPlant(Block block, int meta) {
        if (block == null) {
            return false;
        }
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
        if (key == null) {
            return false;
        }
        String blockId = key.toString();
        if (vanillaPotAllowedPlants == null) {
            return false;
        }

        for (String raw : vanillaPotAllowedPlants) {
            if (raw == null) {
                continue;
            }
            String entry = raw.trim();
            if (entry.isEmpty()) {
                continue;
            }

            int at = entry.indexOf('@');
            if (at >= 0) {
                String idPart = entry.substring(0, at).trim();
                String metaPart = entry.substring(at + 1).trim();
                if (!blockId.equals(idPart)) {
                    continue;
                }
                try {
                    if (Integer.parseInt(metaPart) == meta) {
                        return true;
                    }
                } catch (NumberFormatException ignored) {
                }
                continue;
            }

            if (blockId.equals(entry)) {
                return true;
            }
        }
        return false;
    }
}
