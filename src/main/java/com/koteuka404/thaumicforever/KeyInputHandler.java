package com.koteuka404.thaumicforever;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.TOGGLE_HITBOX.isPressed()) {
            Minecraft mc = Minecraft.getMinecraft();

            Keyboard.enableRepeatEvents(true);
            Keyboard.getEventKeyState();

            mc.gameSettings.advancedItemTooltips = !mc.gameSettings.advancedItemTooltips;
        }
    }
}
