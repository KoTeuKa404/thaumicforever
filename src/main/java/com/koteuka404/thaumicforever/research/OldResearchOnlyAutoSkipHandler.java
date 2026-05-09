package com.koteuka404.thaumicforever.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;

import java.util.HashSet;
import java.util.Set;
import com.koteuka404.thaumicforever.config.ModConfig;

public class OldResearchOnlyAutoSkipHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        if (player == null || player.world == null || player.world.isRemote) return;
        if (!ModConfig.enableOldResearch || !ModConfig.enableOldResearchOnly) return;
        if (player.ticksExisted % 20 != 0) return;

        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge == null) return;

        Set<String> knownResearch = new HashSet<>(knowledge.getResearchList());
        for (String key : knownResearch) {
            if (key == null || key.isEmpty()) continue;
            if (knowledge.getResearchStatus(key) != IPlayerKnowledge.EnumResearchStatus.IN_PROGRESS) continue;

            int stageNum = knowledge.getResearchStage(key);
            if (stageNum <= 0) continue;

            ResearchEntry entry = ResearchCategories.getResearch(key);
            if (entry == null || entry.getStages() == null || stageNum > entry.getStages().length) continue;

            ResearchStage stage = entry.getStages()[stageNum - 1];
            if (stage == null) continue;
            if (!isKnowledgeOnlyStage(stage)) continue;

            knowledge.setResearchStage(key, stageNum + 1);
            if (player instanceof EntityPlayerMP) {
                knowledge.sync((EntityPlayerMP) player);
            }
        }
    }

    private static boolean isKnowledgeOnlyStage(ResearchStage stage) {
        if (stage.getCraft() != null && stage.getCraft().length > 0) return false;
        if (stage.getObtain() != null && stage.getObtain().length > 0) return false;
        if (stage.getResearch() != null && stage.getResearch().length > 0) return false;

        ResearchStage.Knowledge[] know = stage.getKnow();
        // In Old Research Only mode, OBSERVATION may already be stripped,
        // leaving a stage with only text and no formal requirements.
        if (know == null || know.length == 0) return true;

        for (ResearchStage.Knowledge k : know) {
            if (k == null) continue;
            if (k.type != IPlayerKnowledge.EnumKnowledgeType.OBSERVATION) {
                return false;
            }
        }
        return true;
    }
}
