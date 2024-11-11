package com.koteuka404.thaumicforever;

import mezz.jei.recipes.RecipeRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.api.crafting.IDustTrigger;

@Mod(
    modid = ThaumicForever.MODID,
    name = ThaumicForever.NAME,
    version = ThaumicForever.VERSION,
    dependencies = "required-after:forge@[14.23.5.2816,);required-after:thaumcraft@[6.1.BETA26,);"
)
// ;required-after:mixinbooter@[0.8,)
public class ThaumicForever {
    public static final String MODID = "thaumicforever";
    public static final String NAME = "Thaumic Forever";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "com.koteuka404.thaumicforever.ClientProxy", serverSide = "com.koteuka404.thaumicforever.ServerProxy")
    public static CommonProxy proxy;
    private AspectAdder aspectAdder;


    public static ThaumicForever instance;
    public static final CreativeTabs CREATIVE_TAB = new ThaumicForeverCreativeTab();
    private static int id = 0;  // Унікальний ідентифікатор для кожної сутності
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        // Реєстрація генерації світу
        GameRegistry.registerTileEntity(DeconstructionTableTileEntity.class, new ResourceLocation(ThaumicForever.MODID,  ":deconstruction_table"));
        GameRegistry.registerTileEntity(TileEntityAbandonedChest.class, new ResourceLocation(ThaumicForever.MODID, ":abandoned_chest"));
        GameRegistry.registerTileEntity(TileEntityMatteryDuplicator.class, new ResourceLocation(ThaumicForever.MODID, ":repurposer"));
        GameRegistry.registerTileEntity(TileEntityRepurposer.class, new ResourceLocation(ThaumicForever.MODID, ":mattery_duplicator"));
        GameRegistry.registerTileEntity(TileEntityCompressor.class, new ResourceLocation(ThaumicForever.MODID, "compressor"));
        new OreGeneration();
        ModFocuses.registerFocuses();
        // Mixins.addConfiguration("mixins.thaumicforever.json");
        proxy.preInit(event);
        GameRegistry.registerWorldGenerator(new AuraNodeWorldGen(), 0);

        // Реєстрація сутностей
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "guardian_mannequin"), EntityGuardianMannequin.class, "GuardianMannequin", id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "time_freeze_projectile"), EntityTimeFreezeProjectile.class, "TimeFreezeProjectile", id++, this, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "aura_node"), AuraNodeEntity.class, "AuraNode",id++, this, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "revive_skeleton"),ReviveSkeletonEntity.class,"ReviveSkeleton",id++,this,64,1,true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "skeleton_angry"), EntitySkeletonAngry.class, "SkeletonAngry", id++, this, 64, 1, true);

        proxy.preInit(event);

        GameRegistry.registerWorldGenerator(new WorldGenUnderloot(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenEldritchRing(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenObsidianTotem(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenThaumicHouse(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenHilltopStones(), 0);

        MinecraftForge.EVENT_BUS.register(RemoveRecipes.class);
        AspectRegistry.registerAspects();

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(PacketSelectPlate.Handler.class, PacketSelectPlate.class, 0, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new RainCauldronFiller());  // Реєстрація обробника події для дощу
        FlowerGenerator.register();
        MinecraftForge.EVENT_BUS.register(RecipeRegistry.class);



    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        ResearchHandler.init();
        ModDimensions.init(event);
        MinecraftForge.EVENT_BUS.register(new VoidRepairHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(ThaumicForever.instance, new ModGuiHandler());

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

        MinecraftForge.EVENT_BUS.register(new RavenCloakRender());

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RecipeCrucible.addCrucibleRecipes();
        proxy.postInit(event);


        aspectAdder = new AspectAdder();
        aspectAdder.registerAspects();

        // Реєстрація аспектів через подію
        MinecraftForge.EVENT_BUS.register(this);
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


    
}
