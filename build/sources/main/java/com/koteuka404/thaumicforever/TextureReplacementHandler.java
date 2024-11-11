package com.koteuka404.thaumicforever;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class TextureReplacementHandler {

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        // Отримання карти текстур для заміни текстури
        TextureMap map = event.getMap();

        // Заміна текстури
        ResourceLocation newTexture = new ResourceLocation("thaumicforever", "blocks/gemcutter");
        map.registerSprite(newTexture);
    }
}