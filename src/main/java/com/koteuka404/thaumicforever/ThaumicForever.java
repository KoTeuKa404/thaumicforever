package com.koteuka404.thaumicforever;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import baubles.api.BaubleType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.golems.GolemHelper;

@Mod(modid = ThaumicForever.MODID, name = ThaumicForever.NAME, version = ThaumicForever.VERSION,dependencies = "required-after:forge@[14.23.5.2820,);required-after:thaumcraft@[6.1.BETA26,);")
// required-after:mixinbooter@[0.8,);
public class ThaumicForever {
    public static final String MODID = "thaumicforever";
    public static final String NAME = "Thaumic Forever";
    public static final String VERSION = "5.1.8";

    @SidedProxy(clientSide = "com.koteuka404.thaumicforever.ClientProxy", serverSide = "com.koteuka404.thaumicforever.ServerProxy")
    public static CommonProxy proxy;
    private AspectAdder aspectAdder;

    public static ThaumicForever instance;
    public static final CreativeTabs CREATIVE_TAB = new ThaumicForeverCreativeTab();
    private static int id = 0;
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        ModConfig.loadConfig(event);

        ResearchList.initializeFromConfig();

        ResearchBaubleMapping.reloadFromConfig();

        MinecraftForge.EVENT_BUS.register(new MysticEventHandler());

       
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        NetworkHandler.INSTANCE = network;
        NetworkHandler.registerPackets();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

  
        try {
            setupBubblesSlots();
        } catch (Throwable t) {
            System.err.println("[ThaumicForever] setupBubblesSlots() failed:");
        }              
        
        // TConstructFix.fixModifierConflict();
        MinecraftForge.EVENT_BUS.register(new ModRecipes());

        GameRegistry.registerTileEntity(DeconstructionTableTileEntity.class,new ResourceLocation(ThaumicForever.MODID, ":deconstruction_table"));
        GameRegistry.registerTileEntity(TileEntityAbandonedChest.class,new ResourceLocation(ThaumicForever.MODID, ":abandoned_chest"));
        GameRegistry.registerTileEntity(TileEntityMatteryDuplicator.class,new ResourceLocation(ThaumicForever.MODID, ":repurposer"));
        GameRegistry.registerTileEntity(TileEntityRepurposer.class,new ResourceLocation(ThaumicForever.MODID, ":mattery_duplicator"));
        GameRegistry.registerTileEntity(TileEntityCompressor.class,new ResourceLocation(ThaumicForever.MODID, "compressor"));
        GameRegistry.registerTileEntity(TileEntityTimeStone.class,new ResourceLocation(ThaumicForever.MODID, "time_stone"));
        GameRegistry.registerTileEntity(TileEntityTimeSlow.class,new ResourceLocation(ThaumicForever.MODID, "time_slow"));
        GameRegistry.registerTileEntity(TileMechanismAmplifier.class,new ResourceLocation(ThaumicForever.MODID, "mechanism_amplifier_tile"));
        GameRegistry.registerTileEntity(DoubleTableTileEntity.class,new ResourceLocation(ThaumicForever.MODID, "double_table_tile_entity"));
        GameRegistry.registerTileEntity(TileEntityImmortalizer.class,new ResourceLocation(ThaumicForever.MODID, "immortalizer"));
        GameRegistry.registerTileEntity(TileEntityAntiFlightStone.class,new ResourceLocation(ThaumicForever.MODID, "anti_flight_stone"));
        // GameRegistry.registerTileEntity(TileEntityMysticTab.class,new ResourceLocation(ThaumicForever.MODID, "mystic_tab"));
        
        // MinecraftForge.EVENT_BUS.register(AirCurrentManager.class);
        // MinecraftForge.EVENT_BUS.register(AirCurrentHandler.class);
        new OreGeneration();
        ModFocuses.registerFocuses();

        proxy.preInit(event);
        GameRegistry.registerWorldGenerator(new AuraNodeWorldGen(), 0);

        // Реєстрація сутностей
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "guardian_mannequin"),EntityGuardianMannequin.class, "GuardianMannequin", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "time_freeze_projectile"),EntityTimeFreezeProjectile.class, "TimeFreezeProjectile", id++, this, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "aura_node"), AuraNodeEntity.class, "AuraNode",id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "revive_skeleton"), ReviveSkeletonEntity.class,"ReviveSkeleton", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "skeleton_angry"), EntitySkeletonAngry.class,"SkeletonAngry", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:bottle_clean"), EntityBottleClean.class,"bottle_clean", id++, ThaumicForever.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:bottle_vis"), EntityBottleVis.class,"bottle_vis", id++, ThaumicForever.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:bvilager"), EntityBVilager.class,"bvilager", id++, ThaumicForever.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:coinvilager"), CoinVillager.class,"coinvilager", id++, ThaumicForever.instance, 64, 4, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:wizard"), WizardVillager.class, "wizard",id++, ThaumicForever.instance, 64, 4, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:gorilla"), EntityGorilla.class, "gorilla",id++, ThaumicForever.instance, 64, 4, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "watcher_guard"), WatcherEntity.class,"watcher_guard", id++, ThaumicForever.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever", "WindCharge"), EntityWindCharge.class,"WindCharge", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever", "gorilla_hand"),EntityGorillaHand.class, "gorilla_hand", id++, this, 64, 1, true);
        // EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever",
        // "air_current"), EntityAirCurrent.class, "air_current", id++,
        // ThaumicForever.instance, 64, 1, true);
        // MinecraftForge.EVENT_BUS.register(new CrashPreventTooltipPatch());


        GameRegistry.registerWorldGenerator(new WorldGenUnderloot(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenEldritchRing(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenObsidianTotem(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenThaumicHouse(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenHilltopStones(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenMazeInTaiga(), 0);
        MinecraftForge.EVENT_BUS.register(RemoveRecipes.class);
        AspectRegistry.registerAspects();

        network.registerMessage(PacketSelectPlate.Handler.class, PacketSelectPlate.class, 0, Side.SERVER);
        network.registerMessage(PacketClickLupa.Handler.class, PacketClickLupa.class, 1, Side.SERVER);
        network.registerMessage(PacketSyncAspects.Handler.class, PacketSyncAspects.class, 2, Side.CLIENT);
        network.registerMessage(PacketSyncAspects.Handler.class, PacketSyncAspects.class, 3, Side.SERVER);
        network.registerMessage(PacketOpenMysticTab.Handler.class,PacketOpenMysticTab.class,4,Side.SERVER);
        network.registerMessage(PacketOpenNormalInventory.Handler.class,PacketOpenNormalInventory.class,5,Side.SERVER);

        MinecraftForge.EVENT_BUS.register(new RainCauldronFiller());
        FlowerGenerator.register();
        ResearchList.initializeFromConfig();
        MinecraftForge.EVENT_BUS.register(WorldTickHandler.getInstance());
        if (ModConfig.enableThaumicEventHandler) {
            MinecraftForge.EVENT_BUS.register(new ThaumicEventHandler());
        }
        ModConfig.loadConfig(event);

        // new VoidChestSpawner();
        MinecraftForge.EVENT_BUS.register(ModSounds.class);

        
        // try {
        // Class.forName("org.spongepowered.asm.launch.MixinBootstrap");
        // MixinBootstrap.init();
        // Mixins.addConfiguration("mixins.thaumicforever.json");

        // } catch (ClassNotFoundException e) {}


        

        
        ForgeRegistries.VILLAGER_PROFESSIONS.register(CoinVillagerProfession.COIN_VILLAGER_PROFESSION);
        CoinVillagerProfession.registerTrades();

        ForgeRegistries.VILLAGER_PROFESSIONS.register(WizardVillagerProfession.WIZARD_PROFESSION);
        WizardVillagerProfession.registerTrades();
        MinecraftForge.EVENT_BUS.register(WizardVillagerProfession.class);
        GolemHelper.registerSeal(new SealGolemCoreFish());
        MinecraftForge.EVENT_BUS.register(new SealUseUpdater());
        GolemHelper.registerSeal(new SealRefill());
        // GolemHelper.registerSeal(new SealProviderAdvanced());
        // MinecraftForge.EVENT_BUS.register(MysticEntityEventHandler.class);

        new ReviveRingHandler();
        new CasterCooldownReducer();

     
        }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // SealHandler.registerSeal(new SealGolemCoreFish());
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        ResearchHandler.init();
        ModDimensions.init(event);
        MinecraftForge.EVENT_BUS.register(new VoidRepairHandler());
        MinecraftForge.EVENT_BUS.register(new PoisonEnchantmentHandler());


        ModRecipes.init();
        FurnaceRecipes.init();
        CraftingRecipes.init();
        InitRecipes.initInfusionRecipes();
        InfusionRecipes.init();
        OreDictionaryRegistration.registerOreDictionary();

        MinecraftForge.EVENT_BUS.register(new ArmorStandToMannequinHandler());
        MinecraftForge.EVENT_BUS.register(new MannequinInteractionHandler());

        WorldGenHilltopStones.registerLootTables();
        WorldGenUnderloot.registerLootTables();

        registerRecipeOverride();
        IDustTrigger.registerDustTrigger(new SalisMundusDoubleTableTrigger());
        proxy.init(event);
        registerCustomRecipes();
        new ScanObjects();

        ModSpawnEggs.registerEggs();
        new CustomEventHandler();
        MinecraftForge.EVENT_BUS.register(new CustomDrops());
        WatcherWorldGen.register();

        new RegenRingHandler();

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RecipeCrucible.addCrucibleRecipes();
        proxy.postInit(event);

        aspectAdder = new AspectAdder();
        aspectAdder.registerAspects();

    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ThaumicForever.MODID)) {
            ConfigManager.sync(ThaumicForever.MODID, Config.Type.INSTANCE);
        }
    }

    private void registerRecipeOverride() {
        RecipeOverride.onRecipeRegister(null);
    }

    private void registerCustomRecipes() {
        GameRegistry.findRegistry(IRecipe.class).register(new CustomSalisMundusRecipe());
    }

    public static void registerEntitySpawns() {
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null && biome.getSpawnableList(EnumCreatureType.MONSTER) != null) {
        EntityRegistry.addSpawn(EntitySkeletonAngry.class, 8, 1, 3, EnumCreatureType.MONSTER, biome);
            }
        }
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null && biome.getSpawnableList(EnumCreatureType.MONSTER) != null && biome.getSpawnableList(EnumCreatureType.MONSTER).size() > 0) {
                EntityRegistry.addSpawn(EntityBVilager.class, 10, 1, 1, EnumCreatureType.MONSTER, biome);
            }
        }
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null && BiomeDictionary.hasType(biome, Type.JUNGLE)) {
                EntityRegistry.addSpawn(EntityGorilla.class, 2, 1, 2, EnumCreatureType.CREATURE, biome);
            }
        }

    }
    

  // та реальної реалізації setupBubblesSlots():

private void setupBubblesSlots() {
    JsonArray slotsArray = new JsonArray();
    // базові слоти
    for (String s : new String[]{"amulet","ring","ring","belt","head","body","charm"}) {
        slotsArray.add(s);
    }
    // динамічні
    for (BaubleType type : ResearchBaubleMapping.IMMEDIATE) {
        slotsArray.add(type.name().toLowerCase());
    }
    for (Map.Entry<String, BaubleType> e : ResearchBaubleMapping.BY_CATEGORY.entrySet()) {
        slotsArray.add(e.getValue().name().toLowerCase());
    }

    File configDir = Loader.instance().getConfigDir();
    File slotsJson = new File(configDir, "baubles/slots.json");
    slotsJson.getParentFile().mkdirs();

    try (FileWriter writer = new FileWriter(slotsJson)) {
        new GsonBuilder().setPrettyPrinting().create().toJson(slotsArray, writer);
    } catch (IOException e) {
        System.err.println("[ThaumicForever] Failed writing bubbles slots.json!");
        e.printStackTrace();
    }

    // спробуємо рефлексією включити expandedMode, якщо воно є
    try {
        Class<?> cfg = Class.forName("baubles.common.Config");
        Field fld = cfg.getDeclaredField("expandedMode");
        fld.setAccessible(true);
        fld.setBoolean(null, true);
    } catch (ClassNotFoundException cnf) {
        System.err.println("[ThaumicForever] Bubbles Config class not found, skipping expandedMode.");
    } catch (NoSuchFieldException nsf) {
        System.err.println("[ThaumicForever] expandedMode field missing in Bubbles Config, skipping.");
    } catch (Exception ex) {
        System.err.println("[ThaumicForever] Error setting expandedMode in Bubbles Config:");
        ex.printStackTrace();
    }
}

}
