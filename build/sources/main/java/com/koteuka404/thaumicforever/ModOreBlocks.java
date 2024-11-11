package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class ModOreBlocks {

    public static final Block AQUAREIA_ORE = new OreBlock("aquareia_ore", 4.0F, 3.0F, 8);
    public static final Block ANCIENT_AMBER = new OreBlock("ancient_amber", 3.0F, 3.0F, 0);
    public static final Block ANCIENT_CINNABAR = new OreBlock("ancient_cinnabar", 3.0F, 3.0F, 0);
    public static final Block COPPER_ORE = new OreBlock("copper_ore", 4.0F, 3.0F, 0);
    public static final Block TIN_ORE = new OreBlock("tin_ore", 4.0F, 3.0F, 0);
    public static final Block LEAD_ORE = new OreBlock("lead_ore", 4.0F, 3.0F, 0);
    public static final Block SILVER_ORE = new OreBlock("silver_ore", 4.0F, 3.0F, 0);

    // Нові руди
    public static final Block ANCIENT_COAL = new OreBlock("ancient_coal", 3.0F, 3.0F, 0);
    public static final Block ANCIENT_GOLD = new OreBlock("ancient_gold", 3.0F, 3.0F, 0);
    public static final Block ANCIENT_IRON = new OreBlock("ancient_iron", 3.0F, 3.0F, 0);
    public static final Block SMOOTH_STONE = new OreBlock("smooth_stone", 2.0F, 3.0F, 0);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.registerAll(
            AQUAREIA_ORE,
            ANCIENT_AMBER,
            ANCIENT_CINNABAR,
            COPPER_ORE,
            TIN_ORE,
            LEAD_ORE,
            SILVER_ORE,
            ANCIENT_COAL,
            ANCIENT_GOLD,
            ANCIENT_IRON,
            SMOOTH_STONE
        );
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
            new ItemBlock(AQUAREIA_ORE).setRegistryName(AQUAREIA_ORE.getRegistryName()),
            new ItemBlock(ANCIENT_AMBER).setRegistryName(ANCIENT_AMBER.getRegistryName()),
            new ItemBlock(ANCIENT_CINNABAR).setRegistryName(ANCIENT_CINNABAR.getRegistryName()),
            new ItemBlock(COPPER_ORE).setRegistryName(COPPER_ORE.getRegistryName()),
            new ItemBlock(TIN_ORE).setRegistryName(TIN_ORE.getRegistryName()),
            new ItemBlock(LEAD_ORE).setRegistryName(LEAD_ORE.getRegistryName()),
            new ItemBlock(SILVER_ORE).setRegistryName(SILVER_ORE.getRegistryName()),
            new ItemBlock(ANCIENT_COAL).setRegistryName(ANCIENT_COAL.getRegistryName()),
            new ItemBlock(ANCIENT_GOLD).setRegistryName(ANCIENT_GOLD.getRegistryName()),
            new ItemBlock(ANCIENT_IRON).setRegistryName(ANCIENT_IRON.getRegistryName()),
            new ItemBlock(SMOOTH_STONE).setRegistryName(SMOOTH_STONE.getRegistryName())

        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(AQUAREIA_ORE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ANCIENT_AMBER), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ANCIENT_CINNABAR), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(COPPER_ORE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(TIN_ORE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(LEAD_ORE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(SILVER_ORE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ANCIENT_COAL), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ANCIENT_GOLD), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ANCIENT_IRON), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(SMOOTH_STONE), 0, "inventory"); 

    }
}
