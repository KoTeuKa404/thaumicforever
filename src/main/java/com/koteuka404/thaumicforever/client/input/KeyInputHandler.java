package com.koteuka404.thaumicforever.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import com.koteuka404.thaumicforever.network.PacketOpenPouch;
import com.koteuka404.thaumicforever.ThaumicForever;

public class KeyInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!KeyBindings.OPEN_POUCH.isPressed()) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        ThaumicForever.network.sendToServer(new PacketOpenPouch());
    }
}
