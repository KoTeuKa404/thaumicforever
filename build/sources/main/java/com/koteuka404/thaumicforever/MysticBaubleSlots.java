package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import baubles.api.BaubleType;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

public final class MysticBaubleSlots {
    private MysticBaubleSlots() {}

    public static class SlotInfo {
        public final BaubleType type;
        public final String    categoryKey;  
        public SlotInfo(BaubleType t, String key) {
            this.type       = t;
            this.categoryKey = key;
        }
    }

    public static List<SlotInfo> getAllSlots(EntityPlayer player) {
        List<SlotInfo> out = new ArrayList<>();
        for (BaubleType t : ResearchBaubleMapping.IMMEDIATE) {
            out.add(new SlotInfo(t, null));
        }
        for (Map.Entry<String, BaubleType> e : ResearchBaubleMapping.BY_CATEGORY.entrySet()) {
            out.add(new SlotInfo(e.getValue(), e.getKey()));
        }
        return out;
    }

    public static boolean isUnlocked(EntityPlayer player, String categoryKey) {
        // IMMEDIATE
        if (categoryKey == null) return true;
        IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(player);
        ResearchCategory rc = ResearchCategories.getResearchCategory(categoryKey);
        if (rc == null) return false;
        if (rc.research.isEmpty()) return false;
        for (String key : rc.research.keySet()) {
            if (!know.isResearchComplete(key)) {
                return false;
            }
        }
        return true;
    }
}
