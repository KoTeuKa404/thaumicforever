package com.koteuka404.thaumicforever;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        // Порожній метод, оскільки сервер не потребує рендера
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Серверний код ініціалізації
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // Серверний код ініціалізації
    }
}
