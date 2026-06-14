package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;

import com.koteuka404.thaumicforever.block.*;
import com.koteuka404.thaumicforever.item.ItemBlockJarredNode;
import com.koteuka404.thaumicforever.item.ItemBlockBigJar;
import com.koteuka404.thaumicforever.item.ItemBlockAuraTotem;
import com.koteuka404.thaumicforever.item.ItemBlockAuraTotemPole;
import com.koteuka404.thaumicforever.item.ItemBlockPort;
import com.koteuka404.thaumicforever.wand.block.BlockArcaneWorkbenchWandCharger;
import com.koteuka404.thaumicforever.wand.block.BlockWandWorkbench;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
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

    public static final Block DECONSTRUCTION_TABLE = new DeconstructionTableBlock().setUnlocalizedName(ThaumicForever.MODID + ".deconstruction_table").setRegistryName(ThaumicForever.MODID, "deconstruction_table").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block GREATWOOD_TABLE = new GreatwoodTableBlock().setUnlocalizedName(ThaumicForever.MODID + ".greatwood_table").setRegistryName(ThaumicForever.MODID, "greatwood_table").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block DOUBLE_TABLE = new DoubleTableBlock().setUnlocalizedName(ThaumicForever.MODID + ".double_table").setRegistryName(ThaumicForever.MODID, "double_table").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block GREAT_RESEARCH_TABLE = new GreatResearchTableBlock().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block GREAT_RESEARCH_TABLE_SIDE = new GreatResearchTableSideBlock();

    public static final Block LEAD_BLOCK = new BlockBase(Material.IRON, "lead_block").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block SILVER_BLOCK = new BlockBase(Material.IRON, "silver_block").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block TIN_BLOCK = new BlockBase(Material.IRON, "tin_block").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block COPPER_BLOCK = new BlockBase(Material.IRON, "copper_block").setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block OBSIDIAN_TOTEM = new BlockBase(Material.IRON, "obsidian_totem").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block AURA_TOTEM = new BlockAuraTotem().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block AURA_TOTEM_POLE = new BlockAuraTotemPole().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block TIME_STOP = new BlockTimeStop().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block ABANDONED_CHEST = new BlockAbandonedChest().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block VOID_CHEST = new BlockVoidChest().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block ANTI_FLIGHT_STONE = new BlockAntiFlightStone().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block FLIGHT_STONE = new BlockFlightStone().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block FLUX_SCRAPER = new BlockFluxScraper().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block Duplicator = new BlockMatteryDuplicator().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block COMPRESSOR = new BlockCompressor().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block CRYSTALLIZER = new BlockCrystallizer().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block PRIMAL_AURA_CONVERTER = new BlockPrimalAuraConverter().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block REDSTONE_TICKER = new BlockRedstoneTicker().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block RED_ROSE = new BlockRedRose().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block BLUE_ROSE = new BlockBlueRose();
    public static final Block BlockRepurposer = new BlockRepurposer().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block BlockTimeStone = new BlockTimeStone().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block BlockTimeSlow = new BlockTimeSlow().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    // public static final Block INVISIBLE_BLOCK = new InvisibleBlock();
    public static final Block BlockMechanismAmplifier = new BlockMechanismAmplifier().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block INVISIBLE_PART = new InvisiblePartBlock().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block PRIMALBLOCK = new PrimalBlock().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block EndOreBlock = new EndOreBlock().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block Port = new PortBlock().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block VisPlant = new VisPlantBlock().setCreativeTab(ThaumicForever.CREATIVE_TAB);

    public static final Block BlockImmortalizer = Loader.isModLoaded("thaumadditions") ? new BlockImmortalizer() : null;
    public static final Block RubyOre = new RubyOre().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block RubyBlock = new RubyBlock().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block OldPlank = new OldPlank().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block TRAPDOOR_SILVERWOOD = new BlockCustomTrapDoor("trapdoor_silverwood").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block TRAPDOOR_GREATWOOD = new BlockCustomTrapDoor("trapdoor_greatwood").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block DOOR_SILVERWOOD = new BlockCustomDoor("door_silverwood").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block DOOR_GREATWOOD = new BlockCustomDoor("door_greatwood").setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block DOOR_ARCANE = new BlockCustomDoor("door_arcane", true).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static ItemDoor DOOR_SILVERWOOD_ITEM;
    public static ItemDoor DOOR_GREATWOOD_ITEM;
    public static ItemDoor DOOR_ARCANE_ITEM;
    public static final Block BIG_JAR = new BlockBigJar().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static ItemBlock BIG_JAR_ITEM;
    public static final Block BIG_JAR_PART = new BlockBigJarPart();
    public static final Block FLOWER_POT_CUSTOM = new BlockCustomFlowerPot();
    public static final Block nodeStabilizer = new BlockNodeStabilizer().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block buffnodeStabilizer = new BlockBuffNodeStabilizer().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block JARRED_NODE = new BlockJarredNode().setCreativeTab(ThaumicForever.CREATIVE_TAB).setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static ItemBlockJarredNode ITEMBLOCK_JARRED_NODE;

    public static final Block STRUCTURE_MARKER_HOLDER = new BlockStructureMarker();
    public static final Block BlockNodeCharger = new BlockNodeTransducer().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block WAND_WORKBENCH = new BlockWandWorkbench().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block ARCANE_WORKBENCH_WAND_CHARGER = new BlockArcaneWorkbenchWandCharger().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    public static final Block RECHARGE_PEDESTAL = new BlockRechargePedestal().setCreativeTab(ThaumicForever.CREATIVE_TAB);
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.registerAll(
            DECONSTRUCTION_TABLE,
            GREATWOOD_TABLE,
            DOUBLE_TABLE,
            GREAT_RESEARCH_TABLE,
            GREAT_RESEARCH_TABLE_SIDE,
            LEAD_BLOCK,
            SILVER_BLOCK,
            TIN_BLOCK,
            COPPER_BLOCK,
            OBSIDIAN_TOTEM,
            AURA_TOTEM,
            AURA_TOTEM_POLE,
            ABANDONED_CHEST,
            VOID_CHEST,
            ANTI_FLIGHT_STONE,
            FLIGHT_STONE,
            FLUX_SCRAPER,
            TIME_STOP,
            Duplicator,
            COMPRESSOR,
            CRYSTALLIZER,
            PRIMAL_AURA_CONVERTER,
            REDSTONE_TICKER,
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
            // OldPlank,
            TRAPDOOR_SILVERWOOD,
            TRAPDOOR_GREATWOOD,
            DOOR_SILVERWOOD,
            DOOR_GREATWOOD,
            DOOR_ARCANE,
            BIG_JAR,
            BIG_JAR_PART,
            FLOWER_POT_CUSTOM,
            nodeStabilizer,
            buffnodeStabilizer,
            JARRED_NODE,
            STRUCTURE_MARKER_HOLDER        ,
            BlockNodeCharger,
            WAND_WORKBENCH,
            ARCANE_WORKBENCH_WAND_CHARGER,
            RECHARGE_PEDESTAL
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
            new ItemBlock(GREAT_RESEARCH_TABLE).setRegistryName(GREAT_RESEARCH_TABLE.getRegistryName()),
            new ItemBlock(LEAD_BLOCK).setRegistryName(LEAD_BLOCK.getRegistryName()),
            new ItemBlock(SILVER_BLOCK).setRegistryName(SILVER_BLOCK.getRegistryName()),
            new ItemBlock(TIN_BLOCK).setRegistryName(TIN_BLOCK.getRegistryName()),
            new ItemBlock(COPPER_BLOCK).setRegistryName(COPPER_BLOCK.getRegistryName()),
            new ItemBlock(OBSIDIAN_TOTEM).setRegistryName(OBSIDIAN_TOTEM.getRegistryName()),
            new ItemBlockAuraTotem(AURA_TOTEM).setRegistryName(AURA_TOTEM.getRegistryName()),
            new ItemBlockAuraTotemPole(AURA_TOTEM_POLE).setRegistryName(AURA_TOTEM_POLE.getRegistryName()),
            new ItemBlock(ABANDONED_CHEST).setRegistryName(ABANDONED_CHEST.getRegistryName()),
            new ItemBlock(VOID_CHEST).setRegistryName(VOID_CHEST.getRegistryName()),
            new ItemBlock(ANTI_FLIGHT_STONE).setRegistryName(ANTI_FLIGHT_STONE.getRegistryName()),
            new ItemBlock(FLIGHT_STONE).setRegistryName(FLIGHT_STONE.getRegistryName()),
            new ItemBlock(FLUX_SCRAPER).setRegistryName(FLUX_SCRAPER.getRegistryName()),
            new ItemBlock(TIME_STOP).setRegistryName(TIME_STOP.getRegistryName()),
            new ItemBlock(Duplicator).setRegistryName(Duplicator.getRegistryName()),
            new ItemBlock(RED_ROSE).setRegistryName(RED_ROSE.getRegistryName()),
            new ItemBlock(BLUE_ROSE).setRegistryName(BLUE_ROSE.getRegistryName()),
            new ItemBlock(COMPRESSOR).setRegistryName(COMPRESSOR.getRegistryName()),
            new ItemBlock(CRYSTALLIZER).setRegistryName(CRYSTALLIZER.getRegistryName()),
            new ItemBlock(PRIMAL_AURA_CONVERTER).setRegistryName(PRIMAL_AURA_CONVERTER.getRegistryName()),
            new ItemBlock(REDSTONE_TICKER).setRegistryName(REDSTONE_TICKER.getRegistryName()),
            new ItemBlock(BlockRepurposer).setRegistryName(BlockRepurposer.getRegistryName()),
            new ItemBlock(BlockTimeStone).setRegistryName(BlockTimeStone.getRegistryName()),
            new ItemBlock(BlockTimeSlow).setRegistryName(BlockTimeSlow.getRegistryName()),
            new ItemBlock(BlockMechanismAmplifier).setRegistryName(BlockMechanismAmplifier.getRegistryName()),
            new ItemBlockPort(Port).setRegistryName(Port.getRegistryName()),
            new ItemBlock(PRIMALBLOCK).setRegistryName(PRIMALBLOCK.getRegistryName()),
            new ItemBlock(EndOreBlock).setRegistryName(EndOreBlock.getRegistryName()),
            new ItemBlock(VisPlant).setRegistryName(VisPlant.getRegistryName()),
            new ItemBlock(RubyOre).setRegistryName(RubyOre.getRegistryName()),
            new ItemBlock(RubyBlock).setRegistryName(RubyBlock.getRegistryName()),
            // new ItemBlock(OldPlank).setRegistryName(OldPlank.getRegistryName()),
            new ItemBlock(TRAPDOOR_SILVERWOOD).setRegistryName(TRAPDOOR_SILVERWOOD.getRegistryName()),
            new ItemBlock(TRAPDOOR_GREATWOOD).setRegistryName(TRAPDOOR_GREATWOOD.getRegistryName()),
            new ItemBlock(FLOWER_POT_CUSTOM).setRegistryName(FLOWER_POT_CUSTOM.getRegistryName()),
            new ItemBlock(nodeStabilizer).setRegistryName(nodeStabilizer.getRegistryName()),
            new ItemBlock(buffnodeStabilizer).setRegistryName(buffnodeStabilizer.getRegistryName()),
            new ItemBlock(STRUCTURE_MARKER_HOLDER).setRegistryName(STRUCTURE_MARKER_HOLDER.getRegistryName()),
            new ItemBlock(BlockNodeCharger).setRegistryName(BlockNodeCharger.getRegistryName()),
            new ItemBlock(WAND_WORKBENCH).setRegistryName(WAND_WORKBENCH.getRegistryName()),
            new ItemBlock(ARCANE_WORKBENCH_WAND_CHARGER).setRegistryName(ARCANE_WORKBENCH_WAND_CHARGER.getRegistryName()),
            new ItemBlock(RECHARGE_PEDESTAL).setRegistryName(RECHARGE_PEDESTAL.getRegistryName()),

            

            new ItemBlock(INVISIBLE_PART).setRegistryName(INVISIBLE_PART.getRegistryName())
        );

        if (BIG_JAR_ITEM == null) {
            BIG_JAR_ITEM = new ItemBlockBigJar(BIG_JAR);
            BIG_JAR_ITEM.setRegistryName(BIG_JAR.getRegistryName());
            registry.register(BIG_JAR_ITEM);
        }

        DOOR_SILVERWOOD_ITEM = new ItemDoor(DOOR_SILVERWOOD);
        DOOR_SILVERWOOD_ITEM.setRegistryName(DOOR_SILVERWOOD.getRegistryName());
        DOOR_SILVERWOOD_ITEM.setUnlocalizedName("door_silverwood");
        DOOR_SILVERWOOD_ITEM.setCreativeTab(ThaumicForever.CREATIVE_TAB);
        registry.register(DOOR_SILVERWOOD_ITEM);
        ((BlockCustomDoor) DOOR_SILVERWOOD).setDoorItem(DOOR_SILVERWOOD_ITEM);

        DOOR_GREATWOOD_ITEM = new ItemDoor(DOOR_GREATWOOD);
        DOOR_GREATWOOD_ITEM.setRegistryName(DOOR_GREATWOOD.getRegistryName());
        DOOR_GREATWOOD_ITEM.setUnlocalizedName("door_greatwood");
        DOOR_GREATWOOD_ITEM.setCreativeTab(ThaumicForever.CREATIVE_TAB);
        registry.register(DOOR_GREATWOOD_ITEM);
        ((BlockCustomDoor) DOOR_GREATWOOD).setDoorItem(DOOR_GREATWOOD_ITEM);

        DOOR_ARCANE_ITEM = new ItemDoor(DOOR_ARCANE);
        DOOR_ARCANE_ITEM.setRegistryName(DOOR_ARCANE.getRegistryName());
        DOOR_ARCANE_ITEM.setUnlocalizedName("door_arcane");
        DOOR_ARCANE_ITEM.setCreativeTab(ThaumicForever.CREATIVE_TAB);
        registry.register(DOOR_ARCANE_ITEM);
        ((BlockCustomDoor) DOOR_ARCANE).setDoorItem(DOOR_ARCANE_ITEM);

        if (ITEMBLOCK_JARRED_NODE == null) { 
            ITEMBLOCK_JARRED_NODE = new ItemBlockJarredNode(JARRED_NODE);
            ITEMBLOCK_JARRED_NODE.setRegistryName(JARRED_NODE.getRegistryName());
            registry.register(ITEMBLOCK_JARRED_NODE); 
        }    

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
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(GREAT_RESEARCH_TABLE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(LEAD_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(SILVER_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(TIN_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(COPPER_BLOCK), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(OBSIDIAN_TOTEM), 0, "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(AURA_TOTEM), 0, new ModelResourceLocation("thaumicforever:aura_totem", "type=push"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(AURA_TOTEM), 1, new ModelResourceLocation("thaumicforever:aura_totem", "type=pull"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(AURA_TOTEM_POLE), 0, new ModelResourceLocation("thaumicforever:aura_totem_pole", "type=pole_outer"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(AURA_TOTEM_POLE), 1, new ModelResourceLocation("thaumicforever:aura_totem_pole", "type=pole_inner"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(AURA_TOTEM_POLE), 2, new ModelResourceLocation("thaumicforever:aura_totem_pole", "type=pole_pure"));
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ABANDONED_CHEST), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(VOID_CHEST), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ANTI_FLIGHT_STONE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(FLIGHT_STONE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(FLUX_SCRAPER), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(TIME_STOP), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(Duplicator), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(COMPRESSOR), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(CRYSTALLIZER), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(PRIMAL_AURA_CONVERTER), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(REDSTONE_TICKER), 0, "inventory");
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
        // ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(OldPlank), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(TRAPDOOR_SILVERWOOD), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(TRAPDOOR_GREATWOOD), 0, "inventory");
        if (DOOR_SILVERWOOD_ITEM != null) {
            ThaumicForever.proxy.registerItemRenderer(DOOR_SILVERWOOD_ITEM, 0, "inventory");
        }
        if (DOOR_GREATWOOD_ITEM != null) {
            ThaumicForever.proxy.registerItemRenderer(DOOR_GREATWOOD_ITEM, 0, "inventory");
        }
        if (DOOR_ARCANE_ITEM != null) {
            ThaumicForever.proxy.registerItemRenderer(DOOR_ARCANE_ITEM, 0, "inventory");
        }
        if (BIG_JAR_ITEM != null) {
            ThaumicForever.proxy.registerItemRenderer(BIG_JAR_ITEM, 0, "inventory");
        }
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(FLOWER_POT_CUSTOM), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(JARRED_NODE), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(STRUCTURE_MARKER_HOLDER), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(WAND_WORKBENCH), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(ARCANE_WORKBENCH_WAND_CHARGER), 0, "inventory");
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(RECHARGE_PEDESTAL), 0, "inventory");

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockNodeCharger), 0, new ModelResourceLocation("thaumicforever:node_transducer", "inventory"));

        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Port), 0, new ModelResourceLocation("thaumicforever:port", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(nodeStabilizer), 0, new ModelResourceLocation("thaumicforever:node_stabilizer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(buffnodeStabilizer), 0, new ModelResourceLocation("thaumicforever:buff_node_stabilizer", "inventory"));
        if (BlockImmortalizer != null) {
            ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(BlockImmortalizer), 0, "inventory");
        }
    }
    
}
