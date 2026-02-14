package com.koteuka404.thaumicforever.wand.main;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.wand.compat.TW_Compat;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import com.koteuka404.thaumicforever.wand.tile.TW_Tiles;
import com.koteuka404.thaumicforever.wand.wand.TW_Wands;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        TW_Tiles.registerTiles();
        TW_Compat.preInit(e);
    }

    public void init(FMLInitializationEvent e) {
        TW_Compat.init();
        TW_Research.init();
        TW_Wands.registerWandParts();

    }

    public void postInit(FMLPostInitializationEvent e) {
        TW_Research.postInit();
        ThaumcraftApi.registerObjectTag(new ItemStack(TW_Items.itemWand), new AspectList());
        ThaumcraftApi.registerObjectTag(new ItemStack(TW_Items.itemScepter), new AspectList());
        ThaumcraftApi.registerObjectTag(new ItemStack(TW_Items.itemStaff), new AspectList());
    }

    public void loadComplete(FMLLoadCompleteEvent e) {

    }

}
