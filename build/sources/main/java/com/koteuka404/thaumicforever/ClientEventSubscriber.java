package com.koteuka404.thaumicforever;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "thaumicforever")
public class ClientEventSubscriber {

    public static void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventSubscriber());
    }

    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        // Реєстрація моделей тут, якщо потрібно
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        // Код для рендерингу моделі на гравцеві під час події рендерингу
    }
}
