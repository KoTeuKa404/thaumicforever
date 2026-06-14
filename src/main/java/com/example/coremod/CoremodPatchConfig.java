package com.example.coremod;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class CoremodPatchConfig {

    private static final String CATEGORY = "coremod";
    private static Boolean sealStockPatchEnabled;

    private CoremodPatchConfig() {}

    public static boolean isSealStockPatchEnabled() {
        if (sealStockPatchEnabled != null) {
            return sealStockPatchEnabled.booleanValue();
        }

        Configuration config = new Configuration(resolveConfigFile());
        try {
            config.load();
            sealStockPatchEnabled = Boolean.valueOf(config.getBoolean(
                "enableSealStockInTransitPatch",
                CATEGORY,
                true,
                "Enable the Thaumcraft Stock seal patch that counts in-transit provisioning requests. Disable if another coremod changes SealStock."
            ));
        } catch (Throwable t) {
            sealStockPatchEnabled = Boolean.TRUE;
            t.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }

        return sealStockPatchEnabled.booleanValue();
    }

    private static File resolveConfigFile() {
        File configDir = new File(System.getProperty("user.dir", "."), "config");
        return new File(configDir, "thaumicforever.cfg");
    }
}
