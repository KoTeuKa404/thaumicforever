package com.koteuka404.thaumicforever;

import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryRegistration {

    public static void registerOreDictionary() {
        OreDictionary.registerOre("oreLead", ModOreBlocks.LEAD_ORE);
        OreDictionary.registerOre("oreSilver", ModOreBlocks.SILVER_ORE);
        OreDictionary.registerOre("oreTin", ModOreBlocks.TIN_ORE);
        OreDictionary.registerOre("oreCopper", ModOreBlocks.COPPER_ORE);

        OreDictionary.registerOre("oreAncientIron", ModOreBlocks.ANCIENT_IRON);
        OreDictionary.registerOre("oreAncientGold", ModOreBlocks.ANCIENT_GOLD);
        OreDictionary.registerOre("oreAncientCoal", ModOreBlocks.ANCIENT_COAL);
        OreDictionary.registerOre("oreAncientCinnabar", ModOreBlocks.ANCIENT_CINNABAR);
        OreDictionary.registerOre("oreAquareia", ModOreBlocks.AQUAREIA_ORE);
        OreDictionary.registerOre("oreAncientAmber", ModOreBlocks.ANCIENT_AMBER);        

        OreDictionary.registerOre("ingotLead", ModItems.LEAD_INGOT);
        OreDictionary.registerOre("ingotSilver", ModItems.SILVER_INGOT);
        OreDictionary.registerOre("ingotTin", ModItems.TIN_INGOT);
        OreDictionary.registerOre("ingotCopper", ModItems.COPPER_INGOT);

        OreDictionary.registerOre("nuggetLead", ModItems.LEAD_NUGGET);
        OreDictionary.registerOre("nuggetSilver", ModItems.SILVER_NUGGET);
        OreDictionary.registerOre("nuggetTin", ModItems.TIN_NUGGET);
        OreDictionary.registerOre("nuggetCopper", ModItems.COPPER_NUGGET);

        OreDictionary.registerOre("blockLead", ModBlocks.LEAD_BLOCK);
        OreDictionary.registerOre("blockSilver", ModBlocks.SILVER_BLOCK);
        OreDictionary.registerOre("blockTin", ModBlocks.TIN_BLOCK);
        OreDictionary.registerOre("blockCopper", ModBlocks.COPPER_BLOCK);
    }
}
