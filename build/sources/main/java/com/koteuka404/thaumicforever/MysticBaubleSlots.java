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

    public static List<BaubleType> getForPlayer(EntityPlayer player) {
        IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(player);
        List<BaubleType> slots = new ArrayList<>();

        slots.addAll(ResearchBaubleMapping.IMMEDIATE);

        for (Map.Entry<String, BaubleType> e : ResearchBaubleMapping.BY_CATEGORY.entrySet()) {
            ResearchCategory rc = ResearchCategories.getResearchCategory(e.getKey());
            if (rc==null) continue;

            boolean done = true;
            for (String rkey : rc.research.keySet()) {
                if (!know.isResearchComplete(rkey)) {
                    done = false;
                    break;
                }
            }
            if (done) {
                slots.add(e.getValue());
            }
        }

        return slots;
    }
}
