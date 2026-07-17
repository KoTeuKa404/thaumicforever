package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.golemcore.ArcaneGolemCore;
import com.koteuka404.thaumicforever.golemcore.GoliathGolemCore;
import com.koteuka404.thaumicforever.golemcore.IronSkinGolemCore;
import com.koteuka404.thaumicforever.golemcore.PrimalGolemCore;
import com.koteuka404.thaumicforever.golemcore.SwiftGolemCore;
import com.koteuka404.thaumicforever.item.BowlTreatMeat;
import com.koteuka404.thaumicforever.item.BowlZombie;
import com.koteuka404.thaumicforever.item.EldritchEyeAmulet;
import com.koteuka404.thaumicforever.item.GorillaHand;
import com.koteuka404.thaumicforever.item.IronRing;
import com.koteuka404.thaumicforever.item.ItemAmuletDeath;
import com.koteuka404.thaumicforever.item.ItemAquareiaGoggles;
import com.koteuka404.thaumicforever.item.ItemArcaneTurret;
import com.koteuka404.thaumicforever.item.ItemArtifactRubyRing;
import com.koteuka404.thaumicforever.item.ItemAuraPhial;
import com.koteuka404.thaumicforever.item.ItemBigOakGrower;
import com.koteuka404.thaumicforever.item.ItemBoneBlade;
import com.koteuka404.thaumicforever.item.ItemBottleClean;
import com.koteuka404.thaumicforever.item.ItemBottleVis;
import com.koteuka404.thaumicforever.item.ItemBrassGear;
import com.koteuka404.thaumicforever.item.ItemBrokenAmulet;
import com.koteuka404.thaumicforever.item.ItemBrokenFutureLens;
import com.koteuka404.thaumicforever.item.ItemCelestialNotes;
import com.koteuka404.thaumicforever.item.ItemCleanMind;
import com.koteuka404.thaumicforever.item.ItemCluster;
import com.koteuka404.thaumicforever.item.ItemCompassMaze;
import com.koteuka404.thaumicforever.item.ItemCrimsonBook;
import com.koteuka404.thaumicforever.item.ItemCustomCaster;
import com.koteuka404.thaumicforever.item.ItemDebugStick;
import com.koteuka404.thaumicforever.item.ItemDecoyMannequin;
import com.koteuka404.thaumicforever.item.ItemEternalBlade;
import com.koteuka404.thaumicforever.item.ItemFocus4;
import com.koteuka404.thaumicforever.item.ItemFocusComplex;
import com.koteuka404.thaumicforever.item.ItemFluxLamp;
import com.koteuka404.thaumicforever.item.ItemGoldPlate;
import com.koteuka404.thaumicforever.item.ItemGoldenFish;
import com.koteuka404.thaumicforever.item.ItemGolemCore;
import com.koteuka404.thaumicforever.item.ItemGreedyRing;
import com.koteuka404.thaumicforever.item.ItemHand;
import com.koteuka404.thaumicforever.item.ItemKatana;
import com.koteuka404.thaumicforever.item.ItemKnowledgeEpiphany;
import com.koteuka404.thaumicforever.item.ItemKnowledgeFragment;
import com.koteuka404.thaumicforever.item.ItemMask;
import com.koteuka404.thaumicforever.item.ItemNodeMagnet;
import com.koteuka404.thaumicforever.item.ItemPotionGun;
import com.koteuka404.thaumicforever.item.ItemReTrade;
import com.koteuka404.thaumicforever.item.ItemPouch;
import com.koteuka404.thaumicforever.item.ItemPrimalAxe;
import com.koteuka404.thaumicforever.item.ItemPrimalBlade;
import com.koteuka404.thaumicforever.item.ItemPrimalCharm;
import com.koteuka404.thaumicforever.item.ItemPrimalPickaxe;
import com.koteuka404.thaumicforever.item.ItemPrimalScribeTool;
import com.koteuka404.thaumicforever.item.ItemPrimalShovel;
import com.koteuka404.thaumicforever.item.ItemRingCooldown;
import com.koteuka404.thaumicforever.item.ItemRingEnder;
import com.koteuka404.thaumicforever.item.ItemRingRegeneration;
import com.koteuka404.thaumicforever.item.ItemRingRevive;
import com.koteuka404.thaumicforever.item.ItemRingRunicCharge;
import com.koteuka404.thaumicforever.item.ItemRingVerdant;
import com.koteuka404.thaumicforever.item.ItemRubyGem;
import com.koteuka404.thaumicforever.item.ItemScribeToolLarge;
import com.koteuka404.thaumicforever.item.ItemScroll;
import com.koteuka404.thaumicforever.item.ItemStructureSaver;
import com.koteuka404.thaumicforever.item.ItemStructureSpawner;
import com.koteuka404.thaumicforever.item.ItemTaintAmulet;
import com.koteuka404.thaumicforever.item.ItemThaumiumGear;
import com.koteuka404.thaumicforever.item.ItemTimeFreeze;
import com.koteuka404.thaumicforever.item.ItemTuningForkRifle;
import com.koteuka404.thaumicforever.item.ItemVisModule;
import com.koteuka404.thaumicforever.item.ItemVisRing;
import com.koteuka404.thaumicforever.item.ItemVoidEraser;
import com.koteuka404.thaumicforever.item.ItemVoidGear;
import com.koteuka404.thaumicforever.item.ItemVoidSingularityMinecart;
import com.koteuka404.thaumicforever.item.ItemWand;
import com.koteuka404.thaumicforever.item.ItemWarpBlade;
import com.koteuka404.thaumicforever.item.ItemWindCharge;
import com.koteuka404.thaumicforever.item.ItemZombieHeart;
import com.koteuka404.thaumicforever.item.ItemZombieHeartAmulet;
import com.koteuka404.thaumicforever.item.Itembanana;
import com.koteuka404.thaumicforever.item.LootBag;
import com.koteuka404.thaumicforever.item.MagicDust;
import com.koteuka404.thaumicforever.item.OldBone;
import com.koteuka404.thaumicforever.item.DarkCloakItem;
import com.koteuka404.thaumicforever.item.DarkCloakItemH;
import com.koteuka404.thaumicforever.item.RingIron;
import com.koteuka404.thaumicforever.item.RingMaster;
import com.koteuka404.thaumicforever.item.VoidTeleportItem;
import com.koteuka404.thaumicforever.item.nodessummoncreative;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModItems {

    public static final Item LEAD_INGOT = new Item().setUnlocalizedName("lead_ingot").setRegistryName("lead_ingot").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SILVER_INGOT = new Item().setUnlocalizedName("silver_ingot").setRegistryName("silver_ingot").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item TIN_INGOT = new Item().setUnlocalizedName("tin_ingot").setRegistryName("tin_ingot").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item COPPER_INGOT = new Item().setUnlocalizedName("copper_ingot").setRegistryName("copper_ingot").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    // public static final Item LEAD_NUGGET = new Item().setUnlocalizedName("lead_nugget").setRegistryName("lead_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    // public static final Item SILVER_NUGGET = new Item().setUnlocalizedName("silver_nugget").setRegistryName("silver_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    // public static final Item TIN_NUGGET = new Item().setUnlocalizedName("tin_nugget").setRegistryName("tin_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    // public static final Item COPPER_NUGGET = new Item().setUnlocalizedName("copper_nugget").setRegistryName("copper_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item AQUAREIA_GEM = new Item().setUnlocalizedName("aquareia_gem").setRegistryName("aquareia_gem").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CLUSTER = new ItemCluster().setUnlocalizedName("cluster").setRegistryName("cluster").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item PRIMAL_CHARM = new ItemPrimalCharm().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RING_VERDANT = new ItemRingVerdant().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RING_RUNIC_CHARGE = new ItemRingRunicCharge().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ARTIFACT_RUBY_RING = new ItemArtifactRubyRing().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item FOCUS_COMPLEX = new ItemFocusComplex().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item FOCUS_4 = new ItemFocus4().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item IRONRING = new IronRing().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ItemVoidGear = new ItemVoidGear().setUnlocalizedName("void_gear").setRegistryName("void_gear").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemThaumiumGear = new ItemThaumiumGear().setUnlocalizedName("thaumium_gear").setRegistryName("thaumium_gear").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemBrassGear = new ItemBrassGear().setUnlocalizedName("brass_gear").setRegistryName("brass_gear").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ItemZombieHeart = new ItemZombieHeart().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ZOMBIE_HEART_AMULET = new ItemZombieHeartAmulet().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BROKEN_AMULET = new ItemBrokenAmulet().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item AMULET_DEATH = new ItemAmuletDeath().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item mechanism_improved = new Item().setUnlocalizedName("mechanism_improved").setRegistryName("mechanism_improved");
    public static final Item MAGIC_DUST = new MagicDust().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ItemTimeFreeze  = new ItemTimeFreeze().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RING_ENDER = new ItemRingEnder().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    
    public static final Item VOID_TELEPORT_ITEM = new VoidTeleportItem().setUnlocalizedName("void_teleport_item").setRegistryName("void_teleport_item").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CRIMSON_BOOK = new ItemCrimsonBook().setUnlocalizedName("crimson_book").setRegistryName("crimson_book");
    public static final Item ItemAquareiaGoggles = new ItemAquareiaGoggles("aquareia_goggles", ArmorMaterial.DIAMOND).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item Bone = new Item().setUnlocalizedName("strong_bone").setRegistryName("strong_bone").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item OldBone = new OldBone().setUnlocalizedName("oldbone").setRegistryName("oldbone").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item EMPTY_FOCUS = new Item().setUnlocalizedName("empty_focus").setRegistryName("empty_focus").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ETERNAL_BLADE = new ItemEternalBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BOWL_TREATMEAT = new BowlTreatMeat().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BOWL_ZOMBIE = new BowlZombie().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ELDTRITCH_EYE_AMULET = new EldritchEyeAmulet().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemStructureSaver = new ItemStructureSaver();
    public static final Item ItemStructureSpawner = new ItemStructureSpawner();
    public static final Item DarkCloakBauble = new DarkCloakItem().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemBoneBlade = new ItemBoneBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item KATANA = new ItemKatana().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SCROLL_P = new ItemScroll("_p").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SCROLL_O = new ItemScroll("_o").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SCROLL_C = new ItemScroll("_c").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    
    public static final Item ItemBottleClean = new ItemBottleClean().setUnlocalizedName("purifying_bottle").setRegistryName("purifying_bottle").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemBottleVis = new ItemBottleVis().setUnlocalizedName("vis_bottle").setRegistryName("vis_bottle").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CLEAN_MIND = new ItemCleanMind().setUnlocalizedName("clean_mind").setRegistryName("clean_mind").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item AuraPhial = new ItemAuraPhial().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item primalingot = new Item().setUnlocalizedName("primalingot").setRegistryName("primalingot").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item PRIMAL_PICKAXE = new ItemPrimalPickaxe().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item PRIMAL_BLADE = new ItemPrimalBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item PRIMAL_AXE = new ItemPrimalAxe().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item PRIMAL_SHOVEL = new ItemPrimalShovel().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CONDENSED_PRIMAL_PEARL = new Item().setUnlocalizedName("condensed_primal_pearl").setRegistryName("condensed_primal_pearl").setMaxStackSize(1).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item POTION_GUN = new ItemPotionGun().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item taint_slime = new Item().setUnlocalizedName("taint_slime").setRegistryName("taint_slime").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item skull = new Item().setUnlocalizedName("skull").setRegistryName("skull").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item end_dust = new Item().setUnlocalizedName("end_dust").setRegistryName("end_dust").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item lootbag = new LootBag().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RE_TRADE = new ItemReTrade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item soul = new Item().setUnlocalizedName("soul").setRegistryName("soul").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item coin = new Item().setUnlocalizedName("coin").setRegistryName("coin").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item stone = new Item().setUnlocalizedName("stone").setRegistryName("stone").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item wand = new ItemWand().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ring_master = new RingMaster().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item banana = new Itembanana().setUnlocalizedName("banana").setRegistryName("banana").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item hand = ModConfig.enableFunnyStaff ? new GorillaHand().setCreativeTab(ThaumicForever.CREATIVE_TAB) : null;
    public static final Item holywater = new Item().setUnlocalizedName("holywater").setRegistryName("holywater").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item taint_tendril = new Item().setUnlocalizedName("taint_tendril").setRegistryName("taint_tendril").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemTaintAmulet = new ItemTaintAmulet().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item MASK = new ItemMask().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item FLUX_LAMP = new ItemFluxLamp().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item orb_of_taint = new Item().setUnlocalizedName("orb_of_taint").setRegistryName("orb_of_taint").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item orb_of_crimson = new Item().setUnlocalizedName("orb_of_crimson").setRegistryName("orb_of_crimson").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item orb_of_dark = new Item().setUnlocalizedName("orb_of_dark").setRegistryName("orb_of_dark").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemGoldenFish = new ItemGoldenFish().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item celestial_notes = new ItemCelestialNotes().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ruby_gem = new ItemRubyGem().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item orb_of_soul = new Item().setUnlocalizedName("orb_of_soul").setRegistryName("orb_of_soul").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item WIND_CHARGE = new ItemWindCharge();
    public static final Item DarkCloakItemH = new DarkCloakItemH().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemVisModule = new ItemVisModule();
    public static final Item CUSTOM_CASTER = new ItemCustomCaster("caster_advanced", 1).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemCompassMaze = new ItemCompassMaze().setUnlocalizedName("compass_maze").setRegistryName("compass_maze").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item bicon = new Item().setUnlocalizedName("bicon").setRegistryName("bicon");
    public static final Item RingIron = new RingIron().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item POUCH = new ItemPouch().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item RegenRing = new ItemRingRegeneration().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ReviveRing = new ItemRingRevive().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CdRing = new ItemRingCooldown().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item VIS_RING = new ItemVisRing().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RING_GREEDY = new ItemGreedyRing().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BROKEN_FUTURE_LENS = new ItemBrokenFutureLens().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemHand = new ItemHand().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item bigOakGrower = new ItemBigOakGrower().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item void_core = new Item().setUnlocalizedName("void_core").setRegistryName("void_core").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item thaumium_core = new Item().setUnlocalizedName("thaumium_core").setRegistryName("thaumium_core").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemPrimalScribeTool = new ItemPrimalScribeTool().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemScribeToolLarge = new ItemScribeToolLarge().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item itemNodeMagnet = new ItemNodeMagnet().setUnlocalizedName("turret_magnet").setRegistryName("turret_magnet").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item itemArcaneTurret = new ItemArcaneTurret().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item debstick = new ItemDebugStick().setUnlocalizedName("debstick").setRegistryName("debstick");
    public static final Item ItembugStick = new ItemVoidEraser().setUnlocalizedName("void_stick").setRegistryName("void_stick");

    public static final Item nodessummoncreative = new nodessummoncreative().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item KNOWLEDGE_FRAGMENT = new ItemKnowledgeFragment().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item KNOWLEDGE_EPIPHANY = new ItemKnowledgeEpiphany().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item brain = new Item().setUnlocalizedName("purified_brain").setRegistryName("purified_brain").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item GOLD_PLATE = new ItemGoldPlate().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item GOLEM_CORE_GOLIATH = new ItemGolemCore("golem_core_goliath", GoliathGolemCore.ID).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item GOLEM_CORE_IRONSKIN = new ItemGolemCore("golem_core_ironskin", IronSkinGolemCore.ID).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item GOLEM_CORE_ARCANE = new ItemGolemCore("golem_core_arcane", ArcaneGolemCore.ID).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item GOLEM_CORE_SWIFT = new ItemGolemCore("golem_core_swift", SwiftGolemCore.ID).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item GOLEM_CORE_PRIMAL = new ItemGolemCore("golem_core_primal", PrimalGolemCore.ID).setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item TUNING_FORK_RIFLE = new ItemTuningForkRifle();
    public static final Item DECOY_MANNEQUIN = new ItemDecoyMannequin().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item WARP_BLADE = new ItemWarpBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item VOID_SINGULARITY_MINECART = new ItemVoidSingularityMinecart().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
            primalingot,
            PRIMAL_PICKAXE,
            PRIMAL_BLADE,
            PRIMAL_AXE,
            PRIMAL_SHOVEL,
            CONDENSED_PRIMAL_PEARL,
            POTION_GUN,
            CLUSTER,
            AQUAREIA_GEM,
            ruby_gem,
            end_dust,
            MAGIC_DUST,
            GOLD_PLATE,

            // LEAD_NUGGET,
            // SILVER_NUGGET,
            // TIN_NUGGET,
            // COPPER_NUGGET,
            PRIMAL_CHARM,
            RING_VERDANT,
            RING_RUNIC_CHARGE,
            ARTIFACT_RUBY_RING,
            IRONRING,
            ZOMBIE_HEART_AMULET,
            BROKEN_AMULET,
            AMULET_DEATH,
            RING_ENDER,
            ItemAquareiaGoggles,
            ELDTRITCH_EYE_AMULET,
            DarkCloakBauble,
            DarkCloakItemH,
            ItemTaintAmulet,
            MASK,
            FLUX_LAMP,
            ring_master,
            RingIron,
            RegenRing,
            ReviveRing,
            CdRing,
            VIS_RING,
            RING_GREEDY,
            BROKEN_FUTURE_LENS,

            ItemVoidGear,
            ItemThaumiumGear,
            ItemBrassGear,
            mechanism_improved,
            void_core,
            thaumium_core,
            GOLEM_CORE_PRIMAL,

            skull,
            soul,
            stone,
            wand,
            banana,
            holywater,
            ItemGoldenFish,
            BOWL_TREATMEAT,
            BOWL_ZOMBIE,
            brain,
            celestial_notes,
            bigOakGrower,


            // CRIMSON_BOOK,
            SCROLL_P,
            SCROLL_O,
            SCROLL_C,
            ItemBottleClean,
            ItemBottleVis,
            CLEAN_MIND,
            AuraPhial,
            ItemVisModule,
            ItemCompassMaze,
            POUCH,


            Bone,
            OldBone,
            ItemZombieHeart,
            taint_slime,
            taint_tendril,
            coin,
            lootbag,
            RE_TRADE,


            ETERNAL_BLADE,
            ItemBoneBlade,
            KATANA,
            ItemTimeFreeze,
            WIND_CHARGE,
            CUSTOM_CASTER,
            FOCUS_COMPLEX,
            FOCUS_4,
            EMPTY_FOCUS,
            ItemHand,
            VOID_TELEPORT_ITEM,
            itemNodeMagnet,
            itemArcaneTurret,
            ItemPrimalScribeTool,
            ItemScribeToolLarge,


            ItemStructureSaver,
            ItemStructureSpawner,


            orb_of_taint,
            orb_of_crimson,
            orb_of_dark,
            orb_of_soul,


            bicon,
            ItembugStick,
            nodessummoncreative,
            KNOWLEDGE_FRAGMENT,
            KNOWLEDGE_EPIPHANY,
            debstick,
            GOLEM_CORE_GOLIATH,
            GOLEM_CORE_IRONSKIN,
            GOLEM_CORE_ARCANE,
            GOLEM_CORE_SWIFT,
            TUNING_FORK_RIFLE,
            DECOY_MANNEQUIN,
            WARP_BLADE,
            VOID_SINGULARITY_MINECART

        );
        if (ModConfig.enableStandardOreGeneration) {
            registry.registerAll(
                LEAD_INGOT,
                SILVER_INGOT,
                TIN_INGOT,
                COPPER_INGOT
            );
        }
        if (hand != null) {
            registry.register(hand);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        if (ModConfig.enableStandardOreGeneration) {
            ModelLoader.setCustomModelResourceLocation(ModItems.LEAD_INGOT, 0, new ModelResourceLocation(ModItems.LEAD_INGOT.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ModItems.SILVER_INGOT, 0, new ModelResourceLocation(ModItems.SILVER_INGOT.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ModItems.TIN_INGOT, 0, new ModelResourceLocation(ModItems.TIN_INGOT.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ModItems.COPPER_INGOT, 0, new ModelResourceLocation(ModItems.COPPER_INGOT.getRegistryName(), "inventory"));
        }
        // ModelLoader.setCustomModelResourceLocation(ModItems.LEAD_NUGGET, 0, new ModelResourceLocation(ModItems.LEAD_NUGGET.getRegistryName(), "inventory"));
        // ModelLoader.setCustomModelResourceLocation(ModItems.SILVER_NUGGET, 0, new ModelResourceLocation(ModItems.SILVER_NUGGET.getRegistryName(), "inventory"));
        // ModelLoader.setCustomModelResourceLocation(ModItems.TIN_NUGGET, 0, new ModelResourceLocation(ModItems.TIN_NUGGET.getRegistryName(), "inventory"));
        // ModelLoader.setCustomModelResourceLocation(ModItems.COPPER_NUGGET, 0, new ModelResourceLocation(ModItems.COPPER_NUGGET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.AQUAREIA_GEM, 0, new ModelResourceLocation(ModItems.AQUAREIA_GEM.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RE_TRADE, 0, new ModelResourceLocation(ModItems.RE_TRADE.getRegistryName(), "inventory"));
        for (int i = 0; i < ItemCluster.CLUSTER_TYPES.length; i++) {
            ModelLoader.setCustomModelResourceLocation(ModItems.CLUSTER, i,
                new ModelResourceLocation(ModItems.CLUSTER.getRegistryName() + "_" + ItemCluster.CLUSTER_TYPES[i], "inventory"));
        }
        ModelLoader.setCustomModelResourceLocation(ModItems.PRIMAL_CHARM, 0, new ModelResourceLocation(ModItems.PRIMAL_CHARM.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RING_VERDANT, 0, new ModelResourceLocation(ModItems.RING_VERDANT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RING_RUNIC_CHARGE, 0, new ModelResourceLocation(ModItems.RING_RUNIC_CHARGE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ARTIFACT_RUBY_RING, 0, new ModelResourceLocation(ModItems.ARTIFACT_RUBY_RING.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.FOCUS_COMPLEX, 0, new ModelResourceLocation(ModItems.FOCUS_COMPLEX.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.FOCUS_4, 0, new ModelResourceLocation(ModItems.FOCUS_4.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.IRONRING, 0, new ModelResourceLocation(ModItems.IRONRING.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemVoidGear, 0, new ModelResourceLocation(ModItems.ItemVoidGear.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemThaumiumGear, 0, new ModelResourceLocation(ModItems.ItemThaumiumGear.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemBrassGear, 0, new ModelResourceLocation(ModItems.ItemBrassGear.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemZombieHeart, 0, new ModelResourceLocation(ModItems.ItemZombieHeart.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ZOMBIE_HEART_AMULET, 0, new ModelResourceLocation(ModItems.ZOMBIE_HEART_AMULET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.BROKEN_AMULET, 0, new ModelResourceLocation(ModItems.BROKEN_AMULET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.AMULET_DEATH, 0, new ModelResourceLocation(ModItems.AMULET_DEATH.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.mechanism_improved, 0, new ModelResourceLocation(ModItems.mechanism_improved.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.MAGIC_DUST, 0, new ModelResourceLocation(ModItems.MAGIC_DUST.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemTimeFreeze, 0, new ModelResourceLocation(ModItems.ItemTimeFreeze.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RING_ENDER, 0, new ModelResourceLocation(ModItems.RING_ENDER.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.VOID_TELEPORT_ITEM, 0, new ModelResourceLocation(ModItems.VOID_TELEPORT_ITEM.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.CRIMSON_BOOK, 0, new ModelResourceLocation(ModItems.CRIMSON_BOOK.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemAquareiaGoggles, 0, new ModelResourceLocation(ModItems.ItemAquareiaGoggles.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.Bone, 0, new ModelResourceLocation(ModItems.Bone.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.OldBone, 0, new ModelResourceLocation(ModItems.OldBone.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ETERNAL_BLADE, 0, new ModelResourceLocation(ModItems.ETERNAL_BLADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.EMPTY_FOCUS, 0, new ModelResourceLocation(ModItems.EMPTY_FOCUS.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.BOWL_TREATMEAT, 0, new ModelResourceLocation(ModItems.BOWL_TREATMEAT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.BOWL_ZOMBIE, 0, new ModelResourceLocation(ModItems.BOWL_ZOMBIE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ELDTRITCH_EYE_AMULET, 0, new ModelResourceLocation(ModItems.ELDTRITCH_EYE_AMULET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemStructureSaver, 0, new ModelResourceLocation(ModItems.ItemStructureSaver.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemStructureSpawner, 0, new ModelResourceLocation(ModItems.ItemStructureSpawner.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.DarkCloakBauble, 0, new ModelResourceLocation(ModItems.DarkCloakBauble.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemBoneBlade, 0, new ModelResourceLocation(ModItems.ItemBoneBlade.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.KATANA, 0, new ModelResourceLocation(ModItems.KATANA.getRegistryName(), "inventory"));
        for (int meta = 0; meta <= 3; meta++) {
            ModelLoader.setCustomModelResourceLocation(SCROLL_P, meta,new ModelResourceLocation(SCROLL_P.getRegistryName() + "_" + meta, "inventory"));
            ModelLoader.setCustomModelResourceLocation(SCROLL_O, meta,new ModelResourceLocation(SCROLL_O.getRegistryName() + "_" + meta, "inventory"));
            ModelLoader.setCustomModelResourceLocation(SCROLL_C, meta,new ModelResourceLocation(SCROLL_C.getRegistryName() + "_" + meta, "inventory"));
        }

        ModelLoader.setCustomModelResourceLocation(ModItems.ItemBottleClean, 0, new ModelResourceLocation(ModItems.ItemBottleClean.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemBottleVis, 0, new ModelResourceLocation(ModItems.ItemBottleVis.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.CLEAN_MIND, 0, new ModelResourceLocation(ModItems.CLEAN_MIND.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.AuraPhial, 0, new ModelResourceLocation(ModItems.AuraPhial.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.primalingot, 0, new ModelResourceLocation(ModItems.primalingot.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.PRIMAL_PICKAXE, 0, new ModelResourceLocation(ModItems.PRIMAL_PICKAXE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.PRIMAL_BLADE, 0, new ModelResourceLocation(ModItems.PRIMAL_BLADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.PRIMAL_AXE, 0, new ModelResourceLocation(ModItems.PRIMAL_AXE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.PRIMAL_SHOVEL, 0, new ModelResourceLocation(ModItems.PRIMAL_SHOVEL.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.CONDENSED_PRIMAL_PEARL, 0, new ModelResourceLocation(ModItems.CONDENSED_PRIMAL_PEARL.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.POTION_GUN, 0, new ModelResourceLocation(ModItems.POTION_GUN.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.taint_slime, 0, new ModelResourceLocation(ModItems.taint_slime.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.skull, 0, new ModelResourceLocation(ModItems.skull.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.end_dust, 0, new ModelResourceLocation(ModItems.end_dust.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.lootbag, 0, new ModelResourceLocation(ModItems.lootbag.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.soul, 0, new ModelResourceLocation(ModItems.soul.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.coin, 0, new ModelResourceLocation(ModItems.coin.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.stone, 0, new ModelResourceLocation(ModItems.stone.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.wand, 0, new ModelResourceLocation(ModItems.wand.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.banana, 0, new ModelResourceLocation(ModItems.banana.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ring_master, 0, new ModelResourceLocation(ModItems.ring_master.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.holywater, 0, new ModelResourceLocation(ModItems.holywater.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.taint_tendril, 0, new ModelResourceLocation(ModItems.taint_tendril.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemTaintAmulet, 0, new ModelResourceLocation(ModItems.ItemTaintAmulet.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.MASK, 0, new ModelResourceLocation(ModItems.MASK.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.FLUX_LAMP, 0, new ModelResourceLocation(ModItems.FLUX_LAMP.getRegistryName(), "inventory"));
        if (hand != null) {
            ModelLoader.setCustomModelResourceLocation(hand, 0, new ModelResourceLocation(hand.getRegistryName(), "inventory"));
        }
        ModelLoader.setCustomModelResourceLocation(ModItems.orb_of_taint, 0, new ModelResourceLocation(ModItems.orb_of_taint.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.orb_of_crimson, 0, new ModelResourceLocation(ModItems.orb_of_crimson.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.orb_of_dark, 0, new ModelResourceLocation(ModItems.orb_of_dark.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemGoldenFish, 0, new ModelResourceLocation(ModItems.ItemGoldenFish.getRegistryName(), "inventory"));
        for (int i = 0; i < ItemCelestialNotes.VARIANTS.length; i++) {
            ModelLoader.setCustomModelResourceLocation(ModItems.celestial_notes, i,
                new ModelResourceLocation("thaumicforever:celestial_notes_" + ItemCelestialNotes.VARIANTS[i], "inventory"));
        }
            ModelLoader.setCustomModelResourceLocation(ModItems.ruby_gem, 0, new ModelResourceLocation(ModItems.ruby_gem.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.orb_of_soul, 0, new ModelResourceLocation(ModItems.orb_of_soul.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.WIND_CHARGE, 0, new ModelResourceLocation(ModItems.WIND_CHARGE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.DarkCloakItemH, 0, new ModelResourceLocation(ModItems.DarkCloakItemH.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemVisModule, 0, new ModelResourceLocation(ModItems.ItemVisModule.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(CUSTOM_CASTER, 0, new ModelResourceLocation(CUSTOM_CASTER.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemCompassMaze, 0,new ModelResourceLocation("thaumicforever:compass_maze", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.POUCH, 0, new ModelResourceLocation(ModItems.POUCH.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.bicon, 0, new ModelResourceLocation(ModItems.bicon.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RingIron, 0, new ModelResourceLocation(ModItems.RingIron.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RegenRing, 0, new ModelResourceLocation(ModItems.RegenRing.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ReviveRing, 0, new ModelResourceLocation(ModItems.ReviveRing.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.CdRing, 0, new ModelResourceLocation(ModItems.CdRing.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.VIS_RING, 0, new ModelResourceLocation(ModItems.VIS_RING.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RING_GREEDY, 0, new ModelResourceLocation(ModItems.RING_GREEDY.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.BROKEN_FUTURE_LENS, 0, new ModelResourceLocation(ModItems.BROKEN_FUTURE_LENS.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemHand, 0, new ModelResourceLocation(ModItems.ItemHand.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.bigOakGrower, 0, new ModelResourceLocation(ModItems.bigOakGrower.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.thaumium_core, 0, new ModelResourceLocation(ModItems.thaumium_core.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.void_core, 0, new ModelResourceLocation(ModItems.void_core.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemPrimalScribeTool, 0, new ModelResourceLocation(ModItems.ItemPrimalScribeTool.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemScribeToolLarge, 0, new ModelResourceLocation(ModItems.ItemScribeToolLarge.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.itemNodeMagnet, 0, new ModelResourceLocation(ModItems.itemNodeMagnet.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.itemArcaneTurret, 0, new ModelResourceLocation(ModItems.itemArcaneTurret.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.debstick, 0, new ModelResourceLocation(ModItems.debstick.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItembugStick, 0, new ModelResourceLocation(ModItems.ItembugStick.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.nodessummoncreative, 0, new ModelResourceLocation(ModItems.nodessummoncreative.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.KNOWLEDGE_FRAGMENT, 0, new ModelResourceLocation(ModItems.KNOWLEDGE_FRAGMENT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.KNOWLEDGE_EPIPHANY, 0, new ModelResourceLocation(ModItems.KNOWLEDGE_EPIPHANY.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.brain, 0, new ModelResourceLocation(ModItems.brain.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.GOLD_PLATE, 0, new ModelResourceLocation(ModItems.GOLD_PLATE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.GOLEM_CORE_GOLIATH, 0, new ModelResourceLocation(ModItems.GOLEM_CORE_GOLIATH.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.GOLEM_CORE_IRONSKIN, 0, new ModelResourceLocation(ModItems.GOLEM_CORE_IRONSKIN.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.GOLEM_CORE_ARCANE, 0, new ModelResourceLocation(ModItems.GOLEM_CORE_ARCANE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.GOLEM_CORE_SWIFT, 0, new ModelResourceLocation(ModItems.GOLEM_CORE_SWIFT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.TUNING_FORK_RIFLE, 0, new ModelResourceLocation(ModItems.TUNING_FORK_RIFLE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.DECOY_MANNEQUIN, 0, new ModelResourceLocation(ModItems.DECOY_MANNEQUIN.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.WARP_BLADE, 0, new ModelResourceLocation(ModItems.WARP_BLADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.VOID_SINGULARITY_MINECART, 0, new ModelResourceLocation(ModItems.VOID_SINGULARITY_MINECART.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.GOLEM_CORE_PRIMAL, 0, new ModelResourceLocation(ModItems.GOLEM_CORE_PRIMAL.getRegistryName(), "inventory"));
    }
    
}
