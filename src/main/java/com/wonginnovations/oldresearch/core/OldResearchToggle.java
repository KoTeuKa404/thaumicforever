package com.wonginnovations.oldresearch.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class OldResearchToggle {
    private static Boolean enabled;

    private OldResearchToggle() {}

    public static boolean isEnabled() {
        if (enabled != null) {
            return enabled.booleanValue();
        }
        enabled = Boolean.TRUE;
        try {
            File configFile = resolveConfigFile();
            Configuration config = new Configuration(configFile);
            config.load();
            enabled = config.getBoolean(
                "Enable Old Research",
                "Integration",
                true,
                "If false, disables the embedded Old Research mod."
            );
            if (config.hasChanged()) {
                config.save();
            }
        } catch (Throwable ignored) {
            enabled = Boolean.TRUE;
        }
        return enabled.booleanValue();
    }

    private static File resolveConfigFile() {
        String userDir = System.getProperty("user.dir", ".");
        File configDir = new File(userDir, "config");
        return new File(configDir, "thaumicforever.cfg");
    }
}
