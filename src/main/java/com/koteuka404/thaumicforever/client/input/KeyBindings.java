package com.koteuka404.thaumicforever.client.input;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
    private static final String LEGACY_OPEN_POUCH_KEY = "key.open_pouch";
    private static final String OPEN_POUCH_KEY = "key.thaumicforever.open_pouch";
    public static final KeyBinding OPEN_POUCH = new KeyBinding(OPEN_POUCH_KEY, Keyboard.KEY_K, "key.categories.thaumicforever");
    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        ClientRegistry.registerKeyBinding(OPEN_POUCH);
        migrateLegacyOpenPouchKey();
        registered = true;
    }

    private static void migrateLegacyOpenPouchKey() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null || mc.gameSettings.keyBindings == null) return;

        int currentCode = OPEN_POUCH.getKeyCode();
        for (KeyBinding kb : mc.gameSettings.keyBindings) {
            if (kb == null || kb == OPEN_POUCH) continue;
            if (!LEGACY_OPEN_POUCH_KEY.equals(kb.getKeyDescription())) continue;

            int legacyCode = kb.getKeyCode();
            if (legacyCode != currentCode) {
                OPEN_POUCH.setKeyCode(legacyCode);
                KeyBinding.resetKeyBindingArrayAndHash();
                mc.gameSettings.saveOptions();
            }
            break;
        }
    }
}
