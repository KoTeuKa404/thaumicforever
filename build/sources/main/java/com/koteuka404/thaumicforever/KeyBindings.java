package com.koteuka404.thaumicforever;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
    public static final KeyBinding TOGGLE_HITBOX = new KeyBinding("key.toggle_hitbox", Keyboard.KEY_H, "key.categories.thaumicforever");

    public static void register() {
        ClientRegistry.registerKeyBinding(TOGGLE_HITBOX);
    }
}
