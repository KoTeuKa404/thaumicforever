package com.koteuka404.thaumicforever;

import com.koteuka404.thaumicforever.registry.AspectRegistry;

import com.koteuka404.thaumicforever.research.ResearchList;

import com.koteuka404.thaumicforever.registry.OreDictionaryRegistration;

import com.koteuka404.thaumicforever.registry.ModSpawnEggs;

import com.koteuka404.thaumicforever.registry.ModRecipes;

import com.koteuka404.thaumicforever.registry.ModGuiHandler;

import com.koteuka404.thaumicforever.registry.ModFocuses;

import com.koteuka404.thaumicforever.registry.ModEntitySpawns;

import com.koteuka404.thaumicforever.registry.ModSounds;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.registry.ModItems;

import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.debug.DebugCrucible;
import com.koteuka404.thaumicforever.event.ArmorStandToMannequinHandler;
import com.koteuka404.thaumicforever.event.BleedingEnchantmentHandler;
import com.koteuka404.thaumicforever.event.CasterCooldownReducer;
import com.koteuka404.thaumicforever.event.CustomDrops;
import com.koteuka404.thaumicforever.event.CustomEventHandler;
import com.koteuka404.thaumicforever.event.EerieBiomeMobHandler;
import com.koteuka404.thaumicforever.event.InfusionEnchantTooltipHandler;
import com.koteuka404.thaumicforever.event.MannequinInteractionHandler;
import com.koteuka404.thaumicforever.event.MannequinUnfreezeHandler;
import com.koteuka404.thaumicforever.event.MysticEventHandler;
import com.koteuka404.thaumicforever.event.PlayerMazeEvents;
import com.koteuka404.thaumicforever.event.PoisonEnchantmentHandler;
import com.koteuka404.thaumicforever.event.RainCauldronFiller;
import com.koteuka404.thaumicforever.event.RegenRingHandler;
import com.koteuka404.thaumicforever.event.ReviveRingHandler;
import com.koteuka404.thaumicforever.event.RubyProtectEnchantmentHandler;
import com.koteuka404.thaumicforever.event.TaintBottleImpactHandler;
import com.koteuka404.thaumicforever.event.ThaumicEventHandler;
import com.koteuka404.thaumicforever.event.UnbreakableEnchantmentHandler;
import com.koteuka404.thaumicforever.event.VoidRepairHandler;
import com.koteuka404.thaumicforever.event.WorldTickHandler;
import com.koteuka404.thaumicforever.proxy.CommonProxy;
import com.koteuka404.thaumicforever.seal.SealGolemCoreFish;
import com.koteuka404.thaumicforever.seal.SealGolemCoreFishAdvanced;
import com.koteuka404.thaumicforever.seal.SealProviderAdvanced;
import com.koteuka404.thaumicforever.seal.SealRefill;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.koteuka404.thaumicforever.wand.main.ThaumicWands;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.crafting.DustTriggerMultiblock;
import com.koteuka404.thaumicforever.aspect.AspectAdder;
import com.koteuka404.thaumicforever.aspect.AspectDump;
import com.koteuka404.thaumicforever.compat.FlowerPotCompat;
import com.koteuka404.thaumicforever.compat.ResearchBaubleMapping;
import com.koteuka404.thaumicforever.entity.AuraNodeEntity;
import com.koteuka404.thaumicforever.entity.CoinVillager;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.entity.EntityBVilager;
import com.koteuka404.thaumicforever.entity.EntityBottleClean;
import com.koteuka404.thaumicforever.entity.EntityBottleVis;
import com.koteuka404.thaumicforever.entity.EntityGorilla;
import com.koteuka404.thaumicforever.entity.EntityGorillaHand;
import com.koteuka404.thaumicforever.entity.EntityGuardianMannequin;
import com.koteuka404.thaumicforever.entity.EntityNodeMagnet;
import com.koteuka404.thaumicforever.entity.EntitySkeletonAngry;
import com.koteuka404.thaumicforever.entity.EntityTimeFreezeProjectile;
import com.koteuka404.thaumicforever.entity.EntityVampireBat;
import com.koteuka404.thaumicforever.entity.EntityWindCharge;
import com.koteuka404.thaumicforever.entity.ReviveSkeletonEntity;
import com.koteuka404.thaumicforever.entity.WatcherEntity;
import com.koteuka404.thaumicforever.entity.WizardVillager;
import com.koteuka404.thaumicforever.entity.profession.CoinVillagerProfession;
import com.koteuka404.thaumicforever.entity.profession.WizardVillagerProfession;
import com.koteuka404.thaumicforever.interaction.SalisMundusTrigger;
import com.koteuka404.thaumicforever.item.WindCharge;
import com.koteuka404.thaumicforever.network.NetworkHandler;
import com.koteuka404.thaumicforever.network.PacketBleedingFX;
import com.koteuka404.thaumicforever.network.PacketCancelLogisticsRequest;
import com.koteuka404.thaumicforever.network.PacketClickLupa;
import com.koteuka404.thaumicforever.network.PacketExplosionFX;
import com.koteuka404.thaumicforever.network.PacketKatanaSlashFX;
import com.koteuka404.thaumicforever.network.PacketLightningFX;
import com.koteuka404.thaumicforever.network.PacketOpenMysticTab;
import com.koteuka404.thaumicforever.network.PacketOpenNormalInventory;
import com.koteuka404.thaumicforever.network.PacketOpenPouch;
import com.koteuka404.thaumicforever.network.PacketPrimalAuraRequest;
import com.koteuka404.thaumicforever.network.PacketPrimalAuraSync;
import com.koteuka404.thaumicforever.network.PacketRubyProtectFX;
import com.koteuka404.thaumicforever.network.PacketSelectPlate;
import com.koteuka404.thaumicforever.network.PacketSkyBeamFX;
import com.koteuka404.thaumicforever.network.PacketSyncAspects;
import com.koteuka404.thaumicforever.network.TrailMsg;
import com.koteuka404.thaumicforever.node.NodeJarDustTrigger;
import com.koteuka404.thaumicforever.node.NodeJarMultiblockDef;
import com.koteuka404.thaumicforever.node.NodePearlClickHandler;
import com.koteuka404.thaumicforever.potion.PotionFlightHandler;
import com.koteuka404.thaumicforever.potion.VampirismEffectHandler;
import com.koteuka404.thaumicforever.recipe.CraftingRecipes;
import com.koteuka404.thaumicforever.recipe.CustomSalisMundusRecipe;
import com.koteuka404.thaumicforever.recipe.FurnaceRecipes;
import com.koteuka404.thaumicforever.recipe.InfusionRecipes;
import com.koteuka404.thaumicforever.recipe.InitRecipes;
import com.koteuka404.thaumicforever.recipe.RecipeCrucible;
import com.koteuka404.thaumicforever.recipe.RecipeOverride;
import com.koteuka404.thaumicforever.recipe.RemoveRecipes;
import com.koteuka404.thaumicforever.research.OldResearchOnlyAutoSkipHandler;
import com.koteuka404.thaumicforever.research.ResearchHandler;
import com.koteuka404.thaumicforever.research.SafeScanSky;
import com.koteuka404.thaumicforever.research.ScanObjects;
import com.koteuka404.thaumicforever.tile.DeconstructionTableTileEntity;
import com.koteuka404.thaumicforever.tile.DoubleTableTileEntity;
import com.koteuka404.thaumicforever.tile.TileBigJar;
import com.koteuka404.thaumicforever.tile.TileBigJarPart;
import com.koteuka404.thaumicforever.tile.TileBuffNodeStabilizer;
import com.koteuka404.thaumicforever.tile.TileCustomFlowerPot;
import com.koteuka404.thaumicforever.tile.TileEntityAbandonedChest;
import com.koteuka404.thaumicforever.tile.TileEntityAntiFlightStone;
import com.koteuka404.thaumicforever.tile.TileEntityCompressor;
import com.koteuka404.thaumicforever.tile.TileEntityFlightStone;
import com.koteuka404.thaumicforever.tile.TileEntityFluxScraper;
import com.koteuka404.thaumicforever.tile.TileEntityImmortalizer;
import com.koteuka404.thaumicforever.tile.TileEntityJarredNode;
import com.koteuka404.thaumicforever.tile.TileEntityMatteryDuplicator;
import com.koteuka404.thaumicforever.tile.TileEntityRepurposer;
import com.koteuka404.thaumicforever.tile.TileEntityTimeSlow;
import com.koteuka404.thaumicforever.tile.TileEntityTimeStone;
import com.koteuka404.thaumicforever.tile.TileGreatResearchTable;
import com.koteuka404.thaumicforever.tile.TileMechanismAmplifier;
import com.koteuka404.thaumicforever.tile.TileNodeStabilizer;
import com.koteuka404.thaumicforever.tile.TileNodeTransducer;
import com.koteuka404.thaumicforever.tile.TilePort;
import com.koteuka404.thaumicforever.tile.TileRechargePedestal;
import com.koteuka404.thaumicforever.world.ANWorldGenerator;
import com.koteuka404.thaumicforever.world.AuraNodeWorldGen;
import com.koteuka404.thaumicforever.world.FlowerGenerator;
import com.koteuka404.thaumicforever.world.ModDimensions;
import com.koteuka404.thaumicforever.world.OreGeneration;
import com.koteuka404.thaumicforever.world.WatcherWorldGen;
import com.koteuka404.thaumicforever.world.WorldGenEldritchRing;
import com.koteuka404.thaumicforever.world.WorldGenHilltopStones;
import com.koteuka404.thaumicforever.world.WorldGenMazeInTaiga;
import com.koteuka404.thaumicforever.world.WorldGenObsidianTotem;
import com.koteuka404.thaumicforever.world.WorldGenThaumicHouse;
import com.koteuka404.thaumicforever.world.WorldGenUnderloot;

@Mod(modid = ThaumicForever.MODID, name = ThaumicForever.NAME, version = ThaumicForever.VERSION,dependencies = "required-after:forge@[14.23.5.2820,);required-after:thaumcraft@[6.1.BETA26,);")
// required-after:mixinbooter@[0.8,);
public class ThaumicForever {
    public static final String MODID = "thaumicforever";
    public static final String NAME = "Thaumic Forever";
    public static final String VERSION = "6.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.koteuka404.thaumicforever.proxy.ClientProxy", serverSide = "com.koteuka404.thaumicforever.proxy.ServerProxy")
    public static CommonProxy proxy;
    private AspectAdder aspectAdder;

    public static ThaumicForever instance;
    public static final CreativeTabs CREATIVE_TAB = new ThaumicForeverCreativeTab();
    private static int id = 0;

    public static int nextEntityId() {
        return id++;
    }
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        ModConfig.loadConfig(event);

        ResearchList.initializeFromConfig();
        ModDimensions.init(event);

        ResearchBaubleMapping.reloadFromConfig();

        MinecraftForge.EVENT_BUS.register(new MysticEventHandler());
        NodePearlClickHandler.register();
        MinecraftForge.EVENT_BUS.register(new OldResearchOnlyAutoSkipHandler());

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        NetworkHandler.INSTANCE = network;
        NetworkHandler.registerPackets();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        // CapabilityHandler.register();

        
        // TConstructFix.fixModifierConflict();
        MinecraftForge.EVENT_BUS.register(new ModRecipes());
        new OreGeneration();
        ModFocuses.registerFocuses();

        GameRegistry.registerTileEntity(DeconstructionTableTileEntity.class,new ResourceLocation(ThaumicForever.MODID, ":deconstruction_table"));
        GameRegistry.registerTileEntity(TileEntityAbandonedChest.class,new ResourceLocation(ThaumicForever.MODID, ":abandoned_chest"));
        GameRegistry.registerTileEntity(TileEntityMatteryDuplicator.class,new ResourceLocation(ThaumicForever.MODID, ":mattery_duplicator"));
        GameRegistry.registerTileEntity(TileEntityRepurposer.class,new ResourceLocation(ThaumicForever.MODID, ":repurposer"));
        GameRegistry.registerTileEntity(TileEntityCompressor.class,new ResourceLocation(ThaumicForever.MODID, "compressor"));
        GameRegistry.registerTileEntity(TileEntityTimeStone.class,new ResourceLocation(ThaumicForever.MODID, "time_stone"));
        GameRegistry.registerTileEntity(TileEntityTimeSlow.class,new ResourceLocation(ThaumicForever.MODID, "time_slow"));
        GameRegistry.registerTileEntity(TileMechanismAmplifier.class,new ResourceLocation(ThaumicForever.MODID, "mechanism_amplifier_tile"));
        GameRegistry.registerTileEntity(DoubleTableTileEntity.class,new ResourceLocation(ThaumicForever.MODID, "double_table_tile_entity"));
        GameRegistry.registerTileEntity(TileGreatResearchTable.class,new ResourceLocation(ThaumicForever.MODID, "great_research_table_tile_entity"));
        GameRegistry.registerTileEntity(TileEntityImmortalizer.class,new ResourceLocation(ThaumicForever.MODID, "immortalizer"));
        GameRegistry.registerTileEntity(TileEntityAntiFlightStone.class,new ResourceLocation(ThaumicForever.MODID, "anti_flight_stone"));
        GameRegistry.registerTileEntity(TileEntityFlightStone.class,new ResourceLocation(ThaumicForever.MODID, "flight_stone"));
        GameRegistry.registerTileEntity(TileEntityFluxScraper.class,new ResourceLocation(ThaumicForever.MODID, "flux_scraper"));
        GameRegistry.registerTileEntity(TileNodeStabilizer.class,new ResourceLocation(ThaumicForever.MODID, "node_stabilizer"));
        GameRegistry.registerTileEntity(TilePort.class, new ResourceLocation(ThaumicForever.MODID, "tile_port"));
        GameRegistry.registerTileEntity(TileBuffNodeStabilizer.class, new ResourceLocation(ThaumicForever.MODID, "buff_node_stabilizer"));
        GameRegistry.registerTileEntity(TileNodeTransducer.class,new ResourceLocation(ThaumicForever.MODID, "inverted_node_stabilizer"));
        GameRegistry.registerTileEntity(TileEntityJarredNode.class, new ResourceLocation(ThaumicForever.MODID, "jarred_node"));
        GameRegistry.registerTileEntity(TileBigJar.class, new ResourceLocation(ThaumicForever.MODID, "big_jar"));
        GameRegistry.registerTileEntity(TileBigJarPart.class, new ResourceLocation(ThaumicForever.MODID, "big_jar_part"));
        GameRegistry.registerTileEntity(TileCustomFlowerPot.class, new ResourceLocation(ThaumicForever.MODID, "flower_pot_custom"));
        GameRegistry.registerTileEntity(TileRechargePedestal.class, new ResourceLocation(ThaumicForever.MODID, "recharge_pedestal"));
        
        // MinecraftForge.EVENT_BUS.register(AirCurrentManager.class);
        // MinecraftForge.EVENT_BUS.register(AirCurrentHandler.class);

        proxy.preInit(event);
        ThaumicWands.preInit(event);


        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "guardian_mannequin"),EntityGuardianMannequin.class, "GuardianMannequin", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "time_freeze_projectile"),EntityTimeFreezeProjectile.class, "TimeFreezeProjectile", id++, this, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "aura_node"), AuraNodeEntity.class, "AuraNodeEntity",id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "revive_skeleton"), ReviveSkeletonEntity.class,"ReviveSkeleton", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "skeleton_angry"), EntitySkeletonAngry.class,"SkeletonAngry", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:bottle_clean"), EntityBottleClean.class,"bottle_clean", id++, ThaumicForever.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:bottle_vis"), EntityBottleVis.class,"bottle_vis", id++, ThaumicForever.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:bvilager"), EntityBVilager.class,"bvilager", id++, ThaumicForever.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:coinvilager"), CoinVillager.class,"coinvilager", id++, ThaumicForever.instance, 64, 4, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:wizard"), WizardVillager.class, "wizard",id++, ThaumicForever.instance, 64, 4, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever:gorilla"), EntityGorilla.class, "gorilla",id++, ThaumicForever.instance, 64, 4, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "watcher_guard"), WatcherEntity.class,"watcher_guard", id++, ThaumicForever.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "vampire_bat"), EntityVampireBat.class, "vampire_bat", id++, this, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "WindCharge"), EntityWindCharge.class,"WindCharge", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "gorilla_hand"),EntityGorillaHand.class, "gorilla_hand", id++, this, 64, 1, true);
        // EntityRegistry.registerModEntity(new ResourceLocation("thaumicforever","air_current"), EntityAirCurrent.class, "air_current", id++, ThaumicForever.instance, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "AuraNode"), EntityAuraNode.class, "AuraNode", id++, ThaumicForever.instance, 160, 20, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "NodeMagnet"), EntityNodeMagnet.class, "NodeMagnet", id++, ThaumicForever.instance, 64, 3, true);
        WorldGenHilltopStones.registerLootTables();
        WorldGenUnderloot.registerLootTables();
        WorldGenThaumicHouse.registerLootTables();
        
        GameRegistry.registerWorldGenerator(new WorldGenUnderloot(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenEldritchRing(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenObsidianTotem(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenThaumicHouse(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenHilltopStones(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenMazeInTaiga(), 0);
        GameRegistry.registerWorldGenerator(new AuraNodeWorldGen(), 0);
        GameRegistry.registerWorldGenerator(ANWorldGenerator.INSTANCE, 0);

        MinecraftForge.EVENT_BUS.register(new TaintBottleImpactHandler());
        MinecraftForge.EVENT_BUS.register(RemoveRecipes.class);
        AspectRegistry.registerAspects();
        MinecraftForge.EVENT_BUS.register(new PlayerMazeEvents());

        network.registerMessage(PacketSelectPlate.Handler.class, PacketSelectPlate.class, 0, Side.SERVER);
        network.registerMessage(PacketClickLupa.Handler.class, PacketClickLupa.class, 1, Side.SERVER);
        network.registerMessage(PacketSyncAspects.Handler.class, PacketSyncAspects.class, 3, Side.SERVER);
        network.registerMessage(PacketOpenMysticTab.Handler.class,PacketOpenMysticTab.class,4,Side.SERVER);
        network.registerMessage(PacketOpenNormalInventory.Handler.class,PacketOpenNormalInventory.class,5,Side.SERVER);
        network.registerMessage(PacketPrimalAuraRequest.Handler.class, PacketPrimalAuraRequest.class, 8, Side.SERVER);
        network.registerMessage(PacketOpenPouch.Handler.class, PacketOpenPouch.class, 13, Side.SERVER);
        network.registerMessage(PacketCancelLogisticsRequest.Handler.class, PacketCancelLogisticsRequest.class, 15, Side.SERVER);

        if (event.getSide().isClient()) {
            network.registerMessage(PacketSyncAspects.Handler.class, PacketSyncAspects.class, 2, Side.CLIENT);
            network.registerMessage(PacketLightningFX.Handler.class, PacketLightningFX.class, 6, Side.CLIENT);
            network.registerMessage(TrailMsg.Handler.class, TrailMsg.class, 7, Side.CLIENT);
            network.registerMessage(PacketSkyBeamFX.Handler.class, PacketSkyBeamFX.class, 14, Side.CLIENT);
            network.registerMessage(PacketExplosionFX.Handler.class, PacketExplosionFX.class, 10, Side.CLIENT);
            network.registerMessage(PacketKatanaSlashFX.Handler.class, PacketKatanaSlashFX.class, 11, Side.CLIENT);
            network.registerMessage(PacketBleedingFX.Handler.class, PacketBleedingFX.class, 12, Side.CLIENT);
            network.registerMessage(PacketRubyProtectFX.Handler.class, PacketRubyProtectFX.class, 16, Side.CLIENT);

        }
        network.registerMessage(PacketPrimalAuraSync.Handler.class, PacketPrimalAuraSync.class,9, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new RainCauldronFiller());
        FlowerGenerator.register();
        ResearchList.initializeFromConfig();
        MinecraftForge.EVENT_BUS.register(WorldTickHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(new PotionFlightHandler());
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
        GolemHelper.registerSeal(new SealGolemCoreFishAdvanced());
        // MinecraftForge.EVENT_BUS.register(new SealUseUpdater());
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
        FlowerPotCompat.registerVanillaPotPlants();
        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new InfusionEnchantTooltipHandler());
        }
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        ResearchHandler.init();
        MinecraftForge.EVENT_BUS.register(new VoidRepairHandler());
        MinecraftForge.EVENT_BUS.register(new PoisonEnchantmentHandler());
        MinecraftForge.EVENT_BUS.register(new RubyProtectEnchantmentHandler());
        MinecraftForge.EVENT_BUS.register(new BleedingEnchantmentHandler());
        MinecraftForge.EVENT_BUS.register(new UnbreakableEnchantmentHandler());
        MinecraftForge.EVENT_BUS.register(new VampirismEffectHandler());
        MinecraftForge.EVENT_BUS.register(new EerieBiomeMobHandler());
        MinecraftForge.EVENT_BUS.register(new ArmorStandToMannequinHandler());
        MinecraftForge.EVENT_BUS.register(new MannequinInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new MannequinUnfreezeHandler());

        ModRecipes.init();
        FurnaceRecipes.init();
        CraftingRecipes.init();
        InitRecipes.initInfusionRecipes();
        InfusionRecipes.init();
        OreDictionaryRegistration.registerOreDictionary();

        registerRecipeOverride();
        IDustTrigger.registerDustTrigger(new SalisMundusTrigger());
        IDustTrigger.registerDustTrigger(new NodeJarDustTrigger());
        
        proxy.init(event);
        ThaumicWands.init(event);
        registerCustomRecipes();
        new ScanObjects();

        ModSpawnEggs.registerEggs();
        new CustomEventHandler();
        MinecraftForge.EVENT_BUS.register(new CustomDrops());
        WatcherWorldGen.register();

        new RegenRingHandler();
        ModEntitySpawns.registerSpawns();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RecipeCrucible.addCrucibleRecipes();
        proxy.postInit(event);
        ThaumicWands.postInit(event);
        replaceProblematicSkyScan();
        fixBasicsResearchLocations();
        removeFirstStepsObservation();
        if (ModConfig.enableOldResearch && ModConfig.enableOldResearchOnly) {
            removeKnowledgeRequirementsGlobally();
        }
        ThaumcraftApi.addMultiblockRecipeToCatalog(
            new ResourceLocation("thaumicforever", "nodejar"),
            new ThaumcraftApi.BluePrint(
                "NODEJAR",
                new ItemStack(ModBlocks.JARRED_NODE),
                NodeJarMultiblockDef.SHAPE
            )
        );

        aspectAdder = new AspectAdder();
        aspectAdder.registerAspects();

    }

    @SuppressWarnings("unchecked")
    private void replaceProblematicSkyScan() {
        try {
            Field f = ScanningManager.class.getDeclaredField("things");
            f.setAccessible(true);
            Object raw = f.get(null);
            if (!(raw instanceof ArrayList)) return;
            ArrayList<IScanThing> list = (ArrayList<IScanThing>) raw;
            int removed = 0;
            boolean hasSafeScanner = false;
            for (Iterator<IScanThing> it = list.iterator(); it.hasNext();) {
                IScanThing s = it.next();
                if (s != null && "thaumcraft.common.lib.research.ScanSky".equals(s.getClass().getName())) {
                    it.remove();
                    removed++;
                } else if (s instanceof SafeScanSky) {
                    hasSafeScanner = true;
                }
            }
            if (!hasSafeScanner) {
                list.add(new SafeScanSky());
            }
            if (removed > 0 || !hasSafeScanner) {
                LOGGER.warn("Replaced {} ScanSky scanner(s) with SafeScanSky", removed);
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to replace ScanSky scanner", t);
        }
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        DebugCrucible.onLoadComplete();
        if (ModConfig.enableOldResearch && ModConfig.enableOldResearchOnly) {
            removeKnowledgeRequirementsGlobally();
        }
        if (ModConfig.debugAspectDump) {
            AspectDump.dumpAllItemAspects();
        }
    }

    private void fixBasicsResearchLocations() {
        fixResearchLocation("KNOWLEDGETYPES", 3, -2);
        fixResearchLocation("THEORYRESEARCH", 5, -2);
        fixResearchLocation("CELESTIALSCANNING", 7, -2);
    }

    private void fixResearchLocation(String key, int col, int row) {
        ResearchEntry entry = ResearchCategories.getResearch(key);
        if (entry != null) {
            entry.setCategory("BASICS");
            entry.setDisplayColumn(col);
            entry.setDisplayRow(row);
        }
    }

    private void removeFirstStepsObservation() {
        ResearchEntry entry = ResearchCategories.getResearch("FIRSTSTEPS");
        if (entry == null || entry.getStages() == null) {
            return;
        }
        for (ResearchStage stage : entry.getStages()) {
            if (stage == null || stage.getKnow() == null) {
                continue;
            }
            ResearchStage.Knowledge[] know = stage.getKnow();
            int count = 0;
            for (ResearchStage.Knowledge k : know) {
                if (k != null && k.type != IPlayerKnowledge.EnumKnowledgeType.OBSERVATION) {
                    count++;
                }
            }
            if (count == 0) {
                stage.setKnow(null);
                continue;
            }
            ResearchStage.Knowledge[] filtered = new ResearchStage.Knowledge[count];
            int idx = 0;
            for (ResearchStage.Knowledge k : know) {
                if (k != null && k.type != IPlayerKnowledge.EnumKnowledgeType.OBSERVATION) {
                    filtered[idx++] = k;
                }
            }
            stage.setKnow(filtered);
        }
    }

    private void removeKnowledgeRequirementsGlobally() {
        for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
            if (category == null || category.research == null) {
                continue;
            }
            for (ResearchEntry entry : category.research.values()) {
                if (entry == null || entry.getStages() == null) {
                    continue;
                }
                for (ResearchStage stage : entry.getStages()) {
                    if (stage == null || stage.getKnow() == null) {
                        continue;
                    }
                    ResearchStage.Knowledge[] know = stage.getKnow();
                    int count = 0;
                    for (ResearchStage.Knowledge k : know) {
                        if (k != null
                            && k.type != IPlayerKnowledge.EnumKnowledgeType.OBSERVATION) {
                            count++;
                        }
                    }
                    if (count == 0) {
                        stage.setKnow(null);
                        continue;
                    }
                    ResearchStage.Knowledge[] filtered = new ResearchStage.Knowledge[count];
                    int idx = 0;
                    for (ResearchStage.Knowledge k : know) {
                        if (k != null
                            && k.type != IPlayerKnowledge.EnumKnowledgeType.OBSERVATION) {
                            filtered[idx++] = k;
                        }
                    }
                    stage.setKnow(filtered);
                }
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ThaumicForever.MODID)) {
            ConfigManager.sync(ThaumicForever.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
            ModConfig.syncConfig();
        }
    }

    private void registerRecipeOverride() {
        RecipeOverride.onRecipeRegister(null);
    }

    private void registerCustomRecipes() {
        GameRegistry.findRegistry(IRecipe.class).register(new CustomSalisMundusRecipe());
    }
    


}
