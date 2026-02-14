package com.koteuka404.thaumicforever.wand.main;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.wand.util.DummyRecipe;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

public class ThaumicWands {

    public static final String modID = ThaumicForever.MODID;
    public static final String modName = "Thaumic Wands";
    public static final String version = "1.8.1";

    public static final Logger logger = LogManager.getLogger("Thaumic Wands");

    private static final Field TRIGGER_RESEARCH_SIMPLE;
    private static final Field TRIGGER_RESEARCH_ORE;
    private static CommonProxy proxy;

    static {
        Field f = null;
        try {
            f = DustTriggerSimple.class.getDeclaredField("target");
            f.setAccessible(true);
        } catch (Exception ex) {
            FMLCommonHandler.instance().raiseException(ex, "Failed to access Thaumcraft's DustTriggerSimple#research", true);
        }

        TRIGGER_RESEARCH_SIMPLE = f;

        Field t = null;
        try {
            t = DustTriggerOre.class.getDeclaredField("target");
            t.setAccessible(true);
        } catch (Exception ex) {
            FMLCommonHandler.instance().raiseException(ex, "Failed to access Thaumcraft's DustTriggerSimple#research", true);
        }

        TRIGGER_RESEARCH_ORE = t;
    }

    public static Block getDustTriggerSimpleResearch(DustTriggerSimple trigger) {
        try {
            return (Block) TRIGGER_RESEARCH_SIMPLE.get(trigger);
        } catch (Exception ex) {
            FMLCommonHandler.instance().raiseException(ex, "Failed to invoke Thaumcraft's DustTriggerSimple#research", true);
            return null;
        }
    }

    public static String getDustTriggerOreResearch(DustTriggerOre trigger) {
        try {
            return (String) TRIGGER_RESEARCH_ORE.get(trigger);
        } catch (Exception ex) {
            FMLCommonHandler.instance().raiseException(ex, "Failed to invoke Thaumcraft's DustTriggerOre#research", true);
            return null;
        }
    }

    public static void preInit(FMLPreInitializationEvent e) {
        proxy = createProxy(e.getSide());
        proxy.preInit(e);
    }

    public static void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    public static void removeRecipeByName(@Nonnull ResourceLocation location) {
        ForgeRegistries.RECIPES.register(new DummyRecipe().setRegistryName(location));
    }

    public static void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        // for (int i = 0; i < IDustTrigger.triggers.size(); ++i) {
        //     IDustTrigger trigger = IDustTrigger.triggers.get(i);
        //     if (trigger instanceof DustTriggerSimple && (getDustTriggerSimpleResearch((DustTriggerSimple) trigger) == Blocks.CRAFTING_TABLE)) {
        //         IDustTrigger.triggers.remove(i);
        //         break;
        //     }
        // }
        // for (int i = 0; i < IDustTrigger.triggers.size(); ++i) {
        //     IDustTrigger trigger = IDustTrigger.triggers.get(i);
        //     if (trigger instanceof DustTriggerOre && (getDustTriggerOreResearch((DustTriggerOre) trigger).equals("workbench"))) {
        //         IDustTrigger.triggers.remove(i);
        //         break;
        //     }
        // }

        // IDustTrigger.registerDustTrigger(new DustTriggerSimple("", Blocks.BOOKSHELF, new ItemStack(ItemsTC.thaumonomicon)));
        // IDustTrigger.registerDustTrigger(new DustTriggerOre("", "bookshelf", new ItemStack(ItemsTC.thaumonomicon)));

        // GameRegistry.addSmelting(TW_Items.itemBalancedCluster, new ItemStack(ItemsTC.salisMundus, 1), 1.0F);
    }

    public static void loadComplete(FMLLoadCompleteEvent e) {
        proxy.loadComplete(e);
    }

    private static CommonProxy createProxy(Side side) {
        if (side.isClient()) {
            try {
                return (CommonProxy) Class.forName("com.koteuka404.thaumicforever.wand.client.ClientProxy")
                        .getDeclaredConstructor()
                        .newInstance();
            } catch (Exception ex) {
                logger.warn("Failed to create client proxy for wand module, falling back to common proxy.", ex);
            }
        }
        return new CommonProxy();
    }

}
