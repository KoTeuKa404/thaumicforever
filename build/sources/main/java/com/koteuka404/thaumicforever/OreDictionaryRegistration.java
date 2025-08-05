package com.koteuka404.thaumicforever;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

        // OreDictionary.registerOre("nuggetLead", ModItems.LEAD_NUGGET);
        // OreDictionary.registerOre("nuggetSilver", ModItems.SILVER_NUGGET);
        // OreDictionary.registerOre("nuggetTin", ModItems.TIN_NUGGET);
        // OreDictionary.registerOre("nuggetCopper", ModItems.COPPER_NUGGET);

        OreDictionary.registerOre("blockLead", ModBlocks.LEAD_BLOCK);
        OreDictionary.registerOre("blockSilver", ModBlocks.SILVER_BLOCK);
        OreDictionary.registerOre("blockTin", ModBlocks.TIN_BLOCK);
        OreDictionary.registerOre("blockCopper", ModBlocks.COPPER_BLOCK);

        OreDictionary.registerOre("oreRuby", ModBlocks.RubyOre);
        OreDictionary.registerOre("gemRuby", ModItems.ruby_gem);
        OreDictionary.registerOre("blockRuby", ModBlocks.RubyBlock);


        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_yellow")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_white")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_cyan")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_green")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_purple")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_red")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_black")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_gray")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_blue")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_brown")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_light_blue")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_lime")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_magenta")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_orange")));
        OreDictionary.registerOre("nitor", new ItemStack(Item.getByNameOrId("thaumcraft:nitor_pink")));

    }
}
