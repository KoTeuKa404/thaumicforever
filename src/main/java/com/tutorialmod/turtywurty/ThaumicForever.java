package com.tutorialmod.turtywurty;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ThaumicForever.MODID, name = ThaumicForever.NAME, version = ThaumicForever.VERSION)
public class ThaumicForever {
    public static final String MODID = "thaumicforever";
    public static final String NAME = "Thaumic Forever";
    public static final String VERSION = "1.0";

    public static ThaumicForever instance;
    @SidedProxy(clientSide = "com.tutorialmod.turtywurty.ClientProxy", serverSide = "com.tutorialmod.turtywurty.CommonProxy")
    public static CommonProxy proxy;
    public static final CreativeTabs CREATIVE_TAB = new ThaumicForeverCreativeTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new WorldGenHilltopStones(), 0);


        instance = this;
        GameRegistry.registerTileEntity(DeconstructionTableTileEntity.class, MODID + ":deconstruction_table");
        GameRegistry.registerTileEntity(TileEntityAbandonedChest.class, MODID + ":abandoned_chest");

        proxy.preInit(event);
        new OreGeneration(); // Реєстрація генератора руд
        ModFocuses.registerFocuses();
        Mixins.addConfiguration("mixins.thaumicforever.json");
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "guardian_mannequin"), EntityGuardianMannequin.class, "GuardianMannequin", 1, this, 64, 1, true);
        GameRegistry.registerWorldGenerator(new WorldGenUnderloot(), 0);
        // GameRegistry.registerWorldGenerator(new WorldGenObsidianTotemWithChest(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenEldritchRing(), 0);

        GameRegistry.registerWorldGenerator(new WorldGenObsidianTotem(), 0);
        WorldGenObsidianTotem.registerLootTables();
        MinecraftForge.EVENT_BUS.register(RemoveRecipes.class);
        AspectRegistryEvent.registerAspects();
        // EnchantmentsFM.registerEnchantments();

        
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        new ModGuiHandler();
        ResearchHandler.init();
        
        // ModInfusionRecipes.init();
        MinecraftForge.EVENT_BUS.register(new VoidRepairHandler());

        ModRecipes.init();
        FurnaceRecipes.init();  // Ініціалізація рецептів переплавки
        CraftingRecipes.init();
        
        InitRecipes.initInfusionRecipes();
        // InitRecipes.registerRecipes();

        InfusionRecipes.init();

        OreDictionaryRegistration.registerOreDictionary();  // Реєстрація руд у OreDictionary
        MinecraftForge.EVENT_BUS.register(new ArmorStandToMannequinHandler());
        MinecraftForge.EVENT_BUS.register(new MannequinInteractionHandler());
        WorldGenHilltopStones.registerLootTables();  // Реєстрація луттейблу
        WorldGenUnderloot.registerLootTables();
        registerRecipeOverride();


    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RecipeCrucible.addCrucibleRecipes();

    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ThaumicForever.MODID)) {
            ConfigManager.sync(ThaumicForever.MODID, Config.Type.INSTANCE);
        }
    }
    private void registerRecipeOverride() {
        // Реєструємо перевизначення рецепту
        RecipeOverride.onRecipeRegister(null);
    }







   
} 