package com.koteuka404.thaumicforever.wand.item;

import com.koteuka404.thaumicforever.wand.main.ThaumicWands;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;

import java.util.ArrayList;

public class TW_Items {

    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    public static final Item itemWand = new ItemWand("item_wand");
    public static final Item itemStaff = new ItemStaff("item_staff");
    public static final Item itemScepter = new ItemScepter("item_scepter");
    public static final Item itemWandCap = new ItemBaseMeta("item_wand_cap", "iron", "gold", "brass", "thaumium_inert", "thaumium", "void_inert", "void");
    public static final Item itemWandRod = new ItemBaseMeta("item_wand_rod", "greatwood", "reed", "blaze", "ice", "obsidian", "quartz", "bone", "silverwood");
    public static final Item itemStaffCore = new ItemBaseMeta("item_staff_rod", "greatwood", "reed", "blaze", "ice", "obsidian", "quartz", "bone", "silverwood", "primal");

    public static void registerItems(Register<Item> r) {
        for (Item item : ITEMS)
            registerItem(r, item);
    }

    public static void registerRenders(ModelRegistryEvent event) {
        for (Item item : ITEMS)
            registerRender(item);
    }

    private static void registerItem(Register<Item> r, Item item) {
        r.getRegistry().register(item);
    }

    private static void registerRender(Item item) {
        if (item instanceof ItemBaseMeta) {
            ItemBaseMeta it = (ItemBaseMeta) item;
            for (int i = 0; !(i == ((ItemBaseMeta) item).getVariants().length); i++)
                ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(ThaumicWands.modID + ":items/" + it.getBaseName(), it.getVariants()[i]));
        } else {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ThaumicWands.modID + ":wand/" + item.getRegistryName().getResourcePath(), "inventory"));
        }
    }

}
