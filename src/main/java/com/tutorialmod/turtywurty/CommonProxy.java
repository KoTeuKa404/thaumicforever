package com.tutorialmod.turtywurty;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // Загальна ініціалізація
    }

    public void registerItemRenderer(Item item, int meta, String id) {
        // Порожній метод, оскільки сервер не потребує рендера
    }
}
