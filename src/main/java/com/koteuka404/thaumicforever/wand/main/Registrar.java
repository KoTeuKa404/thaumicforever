package com.koteuka404.thaumicforever.wand.main;

import com.koteuka404.thaumicforever.wand.entity.TW_Entities;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import com.koteuka404.thaumicforever.ThaumicForever;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@EventBusSubscriber(modid = ThaumicForever.MODID)
public class Registrar {

    @SubscribeEvent
    public static void registerItems(Register<Item> r) {
        TW_Items.registerItems(r);
    }

    @SubscribeEvent
    public static void registerRecipes(Register<IRecipe> r) {
        TW_Recipes.registerRecipes(r);
    }

    @SubscribeEvent
    public static void registerEntities(Register<EntityEntry> r) {
        TW_Entities.registerEntities(r);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        TW_Items.registerRenders(e);
    }

}
