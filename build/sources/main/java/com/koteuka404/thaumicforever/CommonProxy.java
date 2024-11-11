package com.koteuka404.thaumicforever;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // Проксі виклики для ініціалізації
    }

    public void init(FMLInitializationEvent event) {
        // Порожній метод або додай логіку, якщо потрібно
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Проксі виклики для пост-ініціалізації
    }

    public void registerItemRenderer(Item item, int meta, String id) {
        // Порожній метод, оскільки сервер не потребує рендера
    }
}
