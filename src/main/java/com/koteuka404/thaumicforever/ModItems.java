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

    public static final Item LEAD_NUGGET = new Item().setUnlocalizedName("lead_nugget").setRegistryName("lead_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item SILVER_NUGGET = new Item().setUnlocalizedName("silver_nugget").setRegistryName("silver_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item TIN_NUGGET = new Item().setUnlocalizedName("tin_nugget").setRegistryName("tin_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item COPPER_NUGGET = new Item().setUnlocalizedName("copper_nugget").setRegistryName("copper_nugget").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item AQUAREIA_GEM = new Item().setUnlocalizedName("aquareia_gem").setRegistryName("aquareia_gem").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CLUSTER = new ItemCluster().setUnlocalizedName("cluster").setRegistryName("cluster").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item PRIMAL_CHARM = new ItemPrimalCharm().setCreativeTab(ThaumicForever.CREATIVE_TAB); // Тут вже не потрібно викликати setRegistryName
    public static final Item RING_VERDANT = new ItemRingVerdant().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RING_RUNIC_CHARGE = new ItemRingRunicCharge().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item FOCUS_COMPLEX = new ItemFocusComplex().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item FOCUS_4 = new ItemFocus4().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    // public static final Item ItemTimeFreezeProjectile = new ItemTimeFreezeProjectile().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item IRONRING = new IronRing().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ItemVoidGear = new ItemVoidGear().setUnlocalizedName("void_gear").setRegistryName("void_gear").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemThaumiumGear = new ItemThaumiumGear().setUnlocalizedName("thaumium_gear").setRegistryName("thaumium_gear").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemBrassGear = new ItemBrassGear().setUnlocalizedName("brass_gear").setRegistryName("brass_gear").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ItemZombieHeart = new ItemZombieHeart().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ZOMBIE_HEART_AMULET = new ItemZombieHeartAmulet().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BROKEN_AMULET = new ItemBrokenAmulet().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item AMULET_DEATH = new ItemAmuletDeath();
    public static final Item mechanism_improved = new Item().setUnlocalizedName("mechanism_improved").setRegistryName("mechanism_improved").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item MAGIC_DUST = new magic_dust().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ItemTimeFreeze  = new ItemTimeFreeze().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item RING_ENDER = new ItemRingEnder().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    
    public static final Item VOID_TELEPORT_ITEM = new VoidTeleportItem().setUnlocalizedName("void_teleport_item").setRegistryName("void_teleport_item").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item CRIMSON_BOOK = new ItemCrimsonBook().setUnlocalizedName("crimson_book").setRegistryName("crimson_book");
    public static final Item ItemAquareiaGoggles = new ItemAquareiaGoggles("aquareia_goggles", ArmorMaterial.DIAMOND).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    public static final Item Bone = new Item().setUnlocalizedName("bone").setRegistryName("bone").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item OldBone = new Item().setUnlocalizedName("oldbone").setRegistryName("oldbone").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item EMPTY_FOCUS = new Item().setUnlocalizedName("empty_focus").setRegistryName("empty_focus").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Item ETERNAL_BLADE = new ItemEternalBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BOWL_TREATMEAT = new BowlTreatMeat().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item BOWL_ZOMBIE = new BowlZombie().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ELDTRITCH_EYE_AMULET = new EldritchEyeAmulet();
    public static final Item ItemStructureSaver = new ItemStructureSaver();
    public static final Item ItemStructureSpawner = new ItemStructureSpawner();
    public static final Item RavenCloakBauble = new RavenCloakItem().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Item ItemBoneBlade = new ItemBoneBlade().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
            LEAD_INGOT,
            SILVER_INGOT,
            TIN_INGOT,
            COPPER_INGOT,
            LEAD_NUGGET,
            SILVER_NUGGET,
            TIN_NUGGET,
            COPPER_NUGGET,
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
            ItemBoneBlade

        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(ModItems.LEAD_INGOT, 0, new ModelResourceLocation(ModItems.LEAD_INGOT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SILVER_INGOT, 0, new ModelResourceLocation(ModItems.SILVER_INGOT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.TIN_INGOT, 0, new ModelResourceLocation(ModItems.TIN_INGOT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.COPPER_INGOT, 0, new ModelResourceLocation(ModItems.COPPER_INGOT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.LEAD_NUGGET, 0, new ModelResourceLocation(ModItems.LEAD_NUGGET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SILVER_NUGGET, 0, new ModelResourceLocation(ModItems.SILVER_NUGGET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.TIN_NUGGET, 0, new ModelResourceLocation(ModItems.TIN_NUGGET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.COPPER_NUGGET, 0, new ModelResourceLocation(ModItems.COPPER_NUGGET.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.AQUAREIA_GEM, 0, new ModelResourceLocation(ModItems.AQUAREIA_GEM.getRegistryName(), "inventory"));
        // Нові предмети - кластери
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

    } 

}

