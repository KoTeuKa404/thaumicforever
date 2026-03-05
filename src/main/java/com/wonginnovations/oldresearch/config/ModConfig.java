package com.wonginnovations.oldresearch.config;

import com.wonginnovations.oldresearch.Tags;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MODID)
public class ModConfig {

    @Config.Comment("Not quite sure yet low key")
    public static int researchDifficulty = 1;

    @Config.Comment("Maximum number of aspects in a research note (0 = unlimited)")
    public static int researchNoteAspectLimit = 22;

    public static int notificationDelay = 2000;

    public static int notificationMax = 10;

    public static int aspectTotalCap = 10000;

    public static boolean instantScans = false;

    public static boolean inventoryScanning = true;

}
