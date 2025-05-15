package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModBlocks {

    public static final Block DECONSTRUCTION_TABLE = new DeconstructionTableBlock()
        .setUnlocalizedName(ThaumicForever.MODID + ".deconstruction_table")
        .setRegistryName(ThaumicForever.MODID, "deconstruction_table").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block GREATWOOD_TABLE = new GreatwoodTableBlock()
        .setUnlocalizedName(ThaumicForever.MODID + ".greatwood_table")
        .setRegistryName(ThaumicForever.MODID, "greatwood_table").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block DOUBLE_TABLE = new DoubleTableBlock()
        .setUnlocalizedName(ThaumicForever.MODID + ".double_table")
        .setRegistryName(ThaumicForever.MODID, "double_table").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block LEAD_BLOCK = new BlockBase(Material.IRON, "lead_block")
        .setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block SILVER_BLOCK = new BlockBase(Material.IRON, "silver_block")
        .setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block TIN_BLOCK = new BlockBase(Material.IRON, "tin_block")
        .setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block COPPER_BLOCK = new BlockBase(Material.IRON, "copper_block")
        .setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block OBSIDIAN_TOTEM = new BlockBase(Material.IRON, "obsidian_totem")
        .setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block TIME_STOP = new BlockTimeStop().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block ABANDONED_CHEST = new BlockAbandonedChest().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block ANTI_FLIGHT_STONE = new BlockAntiFlightStone().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block Duplicator = new BlockMatteryDuplicator().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block COMPRESSOR = new BlockCompressor().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block RED_ROSE = new BlockRedRose();
    public static final Block BLUE_ROSE = new BlockBlueRose();
    public static final Block BlockRepurposer = new BlockRepurposer();
    public static final Block BlockTimeStone = new BlockTimeStone();
    public static final Block BlockTimeSlow = new BlockTimeSlow();
    public static final Block INVISIBLE_BLOCK = new InvisibleBlock();
    public static final Block BlockMechanismAmplifier = new BlockMechanismAmplifier();
    public static final Block INVISIBLE_PART = new InvisiblePartBlock();
    public static final Block PRIMALBLOCK = new PrimalBlock();
    public static final Block EndOreBlock = new EndOreBlock();
    public static final Block Port = new PortBlock();
    public static final Block VisPlant = new VisPlantBlock();

    public static final Block BlockImmortalizer = Loader.isModLoaded("thaumadditions") ? new BlockImmortalizer() : null;
    public static final Block RubyOre = new RubyOre();
    public static final Block RubyBlock = new RubyBlock();
    public static final Block OldPlank = new OldPlank();

    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.registerAll(
            DECONSTRUCTION_TABLE,
            GREATWOOD_TABLE,
            DOUBLE_TABLE,
            LEAD_BLOCK,
            SILVER_BLOCK,
            TIN_BLOCK,
            COPPER_BLOCK,
            OBSIDIAN_TOTEM,
            ABANDONED_CHEST,
            ANTI_FLIGHT_STONE,
            TIME_STOP,
            Duplicator,
            COMPRESSOR,
            RED_ROSE,
            BLUE_ROSE,
            BlockRepurposer,
            BlockTimeStone,
            BlockTimeSlow,
            BlockMechanismAmplifier,
            INVISIBLE_PART,
            PRIMALBLOCK,
            EndOreBlock,
            Port,
            VisPlant,
            RubyOre,
            RubyBlock,
            OldPlank
        );

        if (BlockImmortalizer != null) {
            registry.register(BlockImmortalizer);
        }
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
            new ItemBlock(DECONSTRUCTION_TABLE).setRegistryName(DECONSTRUCTION_TABLE.getRegistryName()),
            new ItemBlock(GREATWOOD_TABLE).setRegistryName(GREATWOOD_TABLE.getRegistryName()),
            new ItemBlock(DOUBLE_TABLE).setRegistryName(DOUBLE_TABLE.getRegistryName()),
            new ItemBlock(LEAD_BLOCK).setRegistryName(LEAD_BLOCK.getRegistryName()),
            new ItemBlock(SILVER_BLOCK).setRegistryName(SILVER_BLOCK.getRegistryName()),
            new ItemBlock(TIN_BLOCK).setRegistryName(TIN_BLOCK.getRegistryName()),
            new ItemBlock(COPPER_BLOCK).setRegistryName(COPPER_BLOCK.getRegistryName()),
            new ItemBlock(OBSIDIAN_TOTEM).setRegistryName(OBSIDIAN_TOTEM.getRegistryName()),
            new ItemBlock(ABANDONED_CHEST).setRegistryName(ABANDONED_CHEST.getRegistryName()),
            new ItemBlock(ANTI_FLIGHT_STONE).setRegistryName(ANTI_FLIGHT_STONE.getRegistryName()),
            new ItemBlock(TIME_STOP).setRegistryName(TIME_STOP.getRegistryName()),
            new ItemBlock(Duplicator).setRegistryName(Duplicator.getRegistryName()),
            new ItemBlock(RED_ROSE).setRegistryName(RED_ROSE.getRegistryName()),
            new ItemBlock(BLUE_ROSE).setRegistryName(BLUE_ROSE.getRegistryName()),
            new ItemBlock(COMPRESSOR).setRegistryName(COMPRESSOR.getRegistryName()),
            new ItemBlock(BlockRepurposer).setRegistryName(BlockRepurposer.getRegistryName()),
            new ItemBlock(BlockTimeStone).setRegistryName(BlockTimeStone.getRegistryName()),
            new ItemBlock(BlockTimeSlow).setRegistryName(BlockTimeSlow.getRegistryName()),
            new ItemBlock(BlockMechanismAmplifier).setRegistryName(BlockMechanismAmplifier.getRegistryName()),
            new ItemBlock(Port).setRegistryName(Port.getRegistryName()),
            new ItemBlock(PRIMALBLOCK).setRegistryName(PRIMALBLOCK.getRegistryName()),
            new ItemBlock(EndOreBlock).setRegistryName(EndOreBlock.getRegistryName()),
            new ItemBlock(VisPlant).setRegistryName(VisPlant.getRegistryName()),
            new ItemBlock(RubyOre).setRegistryName(RubyOre.getRegistryName()),
            new ItemBlock(RubyBlock).setRegistryName(RubyBlock.getRegistryName()),
            new ItemBlock(OldPlank).setRegistryName(OldPlank.getRegistryName()),
             
            

            new ItemBlock(INVISIBLE_PART).setRegistryName(INVISIBLE_PART.getRegistryName())
        );

        if (BlockImmortalizer != null) {
            registry.register(new ItemBlock(BlockImmortalizer).setRegistryName(BlockImmortalizer.getRegistryName()));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(DECONSTRUCTION_TABLE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(GREATWOOD_TABLE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(DOUBLE_TABLE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(LEAD_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(SILVER_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(TIN_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(COPPER_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(OBSIDIAN_TOTEM), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ABANDONED_CHEST), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ANTI_FLIGHT_STONE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(TIME_STOP), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(Duplicator), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(COMPRESSOR), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(RED_ROSE), 0, "thaumicforever:red_rose");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(BLUE_ROSE), 0, "thaumicforever:blue_rose");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(BlockRepurposer), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(BlockTimeStone), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(BlockTimeSlow), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(BlockMechanismAmplifier), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(INVISIBLE_PART), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(PRIMALBLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(EndOreBlock), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(VisPlant), 0, "thaumicforever:vis_plant");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(RubyOre), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(RubyBlock), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(OldPlank), 0, "inventory");

        
        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Port), 0, new ModelResourceLocation("thaumicforever:port", "inventory"));
        if (BlockImmortalizer != null) {
            ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(BlockImmortalizer), 0, "inventory");
        }
    }
}
