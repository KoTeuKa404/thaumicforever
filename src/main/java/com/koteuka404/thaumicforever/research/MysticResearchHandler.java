package com.koteuka404.thaumicforever.research;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchEvent;
import com.koteuka404.thaumicforever.ThaumicForever;


@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class MysticResearchHandler {

    public static final String NBT_PREFIX = "MysticCat_";

    @SubscribeEvent
    public static void onResearchComplete(ResearchEvent.Research ev) {
        if (ev == null || ev.getPlayer() == null || !(ev.getPlayer() instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) ev.getPlayer();
        if (player.world == null || player.world.isRemote) {
            return;
        }
        String researchKey = ev.getResearchKey();
        if (researchKey == null || researchKey.isEmpty()) {
            return;
        }

        ResearchEntry entry = ResearchCategories.getResearch(researchKey);
        if (entry == null) return;

        String catKey = entry.getCategory();
        ResearchCategory category = ResearchCategories.getResearchCategory(catKey);
        if (category == null) return;

        IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(player);
        if (know == null) return;

        for (String key : category.research.keySet()) {
            if (!know.isResearchComplete(key)) {
                return;
            }
        }

        NBTTagCompound pers = player.getEntityData()
            .getCompoundTag(EntityPlayerMP.PERSISTED_NBT_TAG);
        String flag = NBT_PREFIX + catKey;
        if (!pers.getBoolean(flag)) {
            pers.setBoolean(flag, true);
            player.getEntityData()
                  .setTag(EntityPlayerMP.PERSISTED_NBT_TAG, pers);
        }
    }
}
