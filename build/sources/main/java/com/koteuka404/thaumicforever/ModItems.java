package com.koteuka404.thaumicforever;


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
    public static final Item mechanism_improved = new Item().setUnlocalizedName("mechanism_improved").setRegistryName("mechanism_improved").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item MAGIC_DUST = new MagicDust().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ItemTimeFreeze  = new ItemTimeFreeze().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RING_ENDER = new ItemRingEnder().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    
    public static final Item VOID_TELEPORT_ITEM = new VoidTeleportItem().setUnlocalizedName("void_teleport_item").setRegistryName("void_teleport_item").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CRIMSON_BOOK = new ItemCrimsonBook().setUnlocalizedName("crimson_book").setRegistryName("crimson_book");
    public static final Item ItemAquareiaGoggles = new ItemAquareiaGoggles("aquareia_goggles", ArmorMaterial.DIAMOND).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item Bone = new Item().setUnlocalizedName("stront_bone").setRegistryName("stront_bone").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item OldBone = new OldBone().setUnlocalizedName("oldbone").setRegistryName("oldbone").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item EMPTY_FOCUS = new Item().setUnlocalizedName("empty_focus").setRegistryName("empty_focus").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ETERNAL_BLADE = new ItemEternalBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BOWL_TREATMEAT = new BowlTreatMeat().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BOWL_ZOMBIE = new BowlZombie().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ELDTRITCH_EYE_AMULET = new EldritchEyeAmulet().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemStructureSaver = new ItemStructureSaver();
    public static final Item ItemStructureSpawner = new ItemStructureSpawner();
    public static final Item RavenCloakBauble = new RavenCloakItem().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemBoneBlade = new ItemBoneBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SCROLL_P = new ItemScroll("_p").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SCROLL_O = new ItemScroll("_o").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SCROLL_C = new ItemScroll("_c").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    
    public static final Item ItemBottleClean = new ItemBottleClean().setUnlocalizedName("purifying_bottle").setRegistryName("purifying_bottle").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemBottleVis = new ItemBottleVis().setUnlocalizedName("vis_bottle").setRegistryName("vis_bottle").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item AuraPhial = new ItemAuraPhial().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item primalingot = new Item().setUnlocalizedName("primalingot").setRegistryName("primalingot").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item taint_slime = new Item().setUnlocalizedName("taint_slime").setRegistryName("taint_slime").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item skull = new Item().setUnlocalizedName("skull").setRegistryName("skull").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item end_dust = new Item().setUnlocalizedName("end_dust").setRegistryName("end_dust").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item lootbag = new LootBag().setCreativeTab(ThaumicForever.CREATIVE_TAB);
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

    public static final Item orb_of_taint = new Item().setUnlocalizedName("orb_of_taint").setRegistryName("orb_of_taint").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item orb_of_crimson = new Item().setUnlocalizedName("orb_of_crimson").setRegistryName("orb_of_crimson").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item orb_of_dark = new Item().setUnlocalizedName("orb_of_dark").setRegistryName("orb_of_dark").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemGoldenFish = new ItemGoldenFish().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item celestial_notes = new ItemCelestialNotes().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ruby_gem = new ItemRubyGem().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item orb_of_soul = new Item().setUnlocalizedName("orb_of_soul").setRegistryName("orb_of_soul").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item WIND_CHARGE = new ItemWindCharge();
    public static final Item RavenCloakItemH = new RavenCloakItemH().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemVisModule = new ItemVisModule().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CUSTOM_CASTER = new ItemCustomCaster("custom_caster", 1).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemCompassMaze = new ItemCompassMaze().setUnlocalizedName("compass_maze").setRegistryName("compass_maze").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item bicon = new Item().setUnlocalizedName("bicon").setRegistryName("bicon").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RingIron = new RingIron().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item RegenRing = new ItemRingRegeneration().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ReviveRing = new ItemRingRevive().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CdRing = new ItemRingCooldown().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemHand = new ItemHand().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item bigOakGrower = new ItemBigOakGrower().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item void_core = new Item().setUnlocalizedName("void_core").setRegistryName("void_core").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item thaumium_core = new Item().setUnlocalizedName("thaumium_core").setRegistryName("thaumium_core").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemPrimalScribeTool = new ItemPrimalScribeTool().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemScribeToolLarge = new ItemScribeToolLarge().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item itemNodeMagnet = new ItemNodeMagnet().setUnlocalizedName("turret_magnet").setRegistryName("turret_magnet").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item debstick = new ItemDebugStick().setUnlocalizedName("debstick").setRegistryName("debstick");
    public static final Item ItembugStick = new ItemVoidEraser().setUnlocalizedName("void_stick").setRegistryName("void_stick");


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
            LEAD_INGOT,
            SILVER_INGOT,
            TIN_INGOT,
            COPPER_INGOT,
            // LEAD_NUGGET,
            // SILVER_NUGGET,
            // TIN_NUGGET,
            // COPPER_NUGGET,
            AQUAREIA_GEM,
            CLUSTER,
            PRIMAL_CHARM,
            RING_VERDANT,
            RING_RUNIC_CHARGE,
            FOCUS_COMPLEX,
            FOCUS_4,
            IRONRING,
            ItemVoidGear,
            ItemThaumiumGear,   
            ItemBrassGear,
            ItemZombieHeart,
            ZOMBIE_HEART_AMULET,
            BROKEN_AMULET,
            AMULET_DEATH,
            mechanism_improved,
            MAGIC_DUST,
            ItemTimeFreeze,
            RING_ENDER,
            VOID_TELEPORT_ITEM ,
            CRIMSON_BOOK,
            ItemAquareiaGoggles,
            Bone,
            OldBone,
            ETERNAL_BLADE,
            EMPTY_FOCUS,
            BOWL_TREATMEAT,
            BOWL_ZOMBIE,
            ELDTRITCH_EYE_AMULET,
            ItemStructureSaver,
            ItemStructureSpawner,
            RavenCloakBauble,
            ItemBoneBlade,
            SCROLL_P,
            SCROLL_O,
            SCROLL_C,
            ItemBottleClean,
            ItemBottleVis,
            AuraPhial,
            primalingot,
            taint_slime,
            skull,
            end_dust,
            lootbag,
            soul,
            coin,
            stone,
            wand,
            banana,
            ring_master,
            holywater,
            taint_tendril,
            ItemTaintAmulet,
            orb_of_taint,
            orb_of_crimson,
            orb_of_dark,
            ItemGoldenFish,
            celestial_notes,
            ruby_gem,
            orb_of_soul,
            WIND_CHARGE,
            RavenCloakItemH,
            ItemVisModule,
            CUSTOM_CASTER,
            ItemCompassMaze,
            bicon,
            RingIron,
            RegenRing,
            ReviveRing,
            CdRing,
            ItemHand,
            bigOakGrower,
            void_core,
            thaumium_core,
            ItemPrimalScribeTool,
            ItemScribeToolLarge,
            itemNodeMagnet,
            debstick,
            ItembugStick
        );
        if (hand != null) {
            registry.register(hand);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(ModItems.LEAD_INGOT, 0, new ModelResourceLocation(ModItems.LEAD_INGOT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SILVER_INGOT, 0, new ModelResourceLocation(ModItems.SILVER_INGOT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.TIN_INGOT, 0, new ModelResourceLocation(ModItems.TIN_INGOT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.COPPER_INGOT, 0, new ModelResourceLocation(ModItems.COPPER_INGOT.getRegistryName(), "inventory"));
        // ModelLoader.setCustomModelResourceLocation(ModItems.LEAD_NUGGET, 0, new ModelResourceLocation(ModItems.LEAD_NUGGET.getRegistryName(), "inventory"));
        // ModelLoader.setCustomModelResourceLocation(ModItems.SILVER_NUGGET, 0, new ModelResourceLocation(ModItems.SILVER_NUGGET.getRegistryName(), "inventory"));
        // ModelLoader.setCustomModelResourceLocation(ModItems.TIN_NUGGET, 0, new ModelResourceLocation(ModItems.TIN_NUGGET.getRegistryName(), "inventory"));
        // ModelLoader.setCustomModelResourceLocation(ModItems.COPPER_NUGGET, 0, new ModelResourceLocation(ModItems.COPPER_NUGGET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.AQUAREIA_GEM, 0, new ModelResourceLocation(ModItems.AQUAREIA_GEM.getRegistryName(), "inventory"));
        for (int i = 0; i < ItemCluster.CLUSTER_TYPES.length; i++) {
            ModelLoader.setCustomModelResourceLocation(ModItems.CLUSTER, i, 
                new ModelResourceLocation(ModItems.CLUSTER.getRegistryName() + "_" + ItemCluster.CLUSTER_TYPES[i], "inventory"));
        }        
        ModelLoader.setCustomModelResourceLocation(ModItems.PRIMAL_CHARM, 0, new ModelResourceLocation(ModItems.PRIMAL_CHARM.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RING_VERDANT, 0, new ModelResourceLocation(ModItems.RING_VERDANT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RING_RUNIC_CHARGE, 0, new ModelResourceLocation(ModItems.RING_RUNIC_CHARGE.getRegistryName(), "inventory"));
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
        ModelLoader.setCustomModelResourceLocation(ModItems.RavenCloakBauble, 0, new ModelResourceLocation(ModItems.RavenCloakBauble.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemBoneBlade, 0, new ModelResourceLocation(ModItems.ItemBoneBlade.getRegistryName(), "inventory"));
        for (int meta = 0; meta <= 3; meta++) {
            ModelLoader.setCustomModelResourceLocation(SCROLL_P, meta,new ModelResourceLocation(SCROLL_P.getRegistryName() + "_" + meta, "inventory"));
            ModelLoader.setCustomModelResourceLocation(SCROLL_O, meta,new ModelResourceLocation(SCROLL_O.getRegistryName() + "_" + meta, "inventory"));
            ModelLoader.setCustomModelResourceLocation(SCROLL_C, meta,new ModelResourceLocation(SCROLL_C.getRegistryName() + "_" + meta, "inventory"));
        }
 
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemBottleClean, 0, new ModelResourceLocation(ModItems.ItemBottleClean.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemBottleVis, 0, new ModelResourceLocation(ModItems.ItemBottleVis.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.AuraPhial, 0, new ModelResourceLocation(ModItems.AuraPhial.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.primalingot, 0, new ModelResourceLocation(ModItems.primalingot.getRegistryName(), "inventory"));
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
        ModelLoader.setCustomModelResourceLocation(ModItems.RavenCloakItemH, 0, new ModelResourceLocation(ModItems.RavenCloakItemH.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemVisModule, 0, new ModelResourceLocation(ModItems.ItemVisModule.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(CUSTOM_CASTER, 0, new ModelResourceLocation(CUSTOM_CASTER.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemCompassMaze, 0,new ModelResourceLocation("thaumicforever:compass_maze", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.bicon, 0, new ModelResourceLocation(ModItems.bicon.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RingIron, 0, new ModelResourceLocation(ModItems.RingIron.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.RegenRing, 0, new ModelResourceLocation(ModItems.RegenRing.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ReviveRing, 0, new ModelResourceLocation(ModItems.ReviveRing.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.CdRing, 0, new ModelResourceLocation(ModItems.CdRing.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemHand, 0, new ModelResourceLocation(ModItems.ItemHand.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.bigOakGrower, 0, new ModelResourceLocation(ModItems.bigOakGrower.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.thaumium_core, 0, new ModelResourceLocation(ModItems.thaumium_core.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.void_core, 0, new ModelResourceLocation(ModItems.void_core.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemPrimalScribeTool, 0, new ModelResourceLocation(ModItems.ItemPrimalScribeTool.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItemScribeToolLarge, 0, new ModelResourceLocation(ModItems.ItemScribeToolLarge.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.itemNodeMagnet, 0, new ModelResourceLocation(ModItems.itemNodeMagnet.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.debstick, 0, new ModelResourceLocation(ModItems.debstick.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.ItembugStick, 0, new ModelResourceLocation(ModItems.ItembugStick.getRegistryName(), "inventory"));

    } 
    
}            
