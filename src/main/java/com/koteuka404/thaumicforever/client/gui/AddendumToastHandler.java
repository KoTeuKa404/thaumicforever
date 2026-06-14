package com.koteuka404.thaumicforever.client.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;

@SideOnly(Side.CLIENT)
public class AddendumToastHandler {
    private static final int LOGIN_SYNC_WARMUP_TICKS = 100;

    private final Set<String> knownPageFlags = new HashSet<>();
    private final Set<String> shownPageFlags = new HashSet<>();
    private boolean initialized;
    private java.util.UUID playerId;
    private int loginTicks;
    private File shownFlagsFile;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) {
            initialized = false;
            loginTicks = 0;
            shownFlagsFile = null;
            knownPageFlags.clear();
            return;
        }

        java.util.UUID currentPlayerId = mc.player.getUniqueID();
        if (playerId == null || !playerId.equals(currentPlayerId)) {
            playerId = currentPlayerId;
            initialized = false;
            loginTicks = 0;
            knownPageFlags.clear();
            shownPageFlags.clear();
            shownFlagsFile = getShownFlagsFile(mc, currentPlayerId);
            loadShownFlags();
        }

        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(mc.player);
        if (knowledge == null) {
            return;
        }

        if (!initialized && loginTicks++ < LOGIN_SYNC_WARMUP_TICKS) {
            return;
        }

        Set<String> currentPageFlags = collectPageFlags(knowledge);
        if (!initialized) {
            knownPageFlags.clear();
            knownPageFlags.addAll(currentPageFlags);
            shownPageFlags.addAll(currentPageFlags);
            saveShownFlags();
            initialized = true;
            return;
        }

        for (String key : currentPageFlags) {
            if (!knownPageFlags.contains(key) && !shownPageFlags.contains(key)) {
                ResearchEntry entry = ResearchCategories.getResearch(key);
                if (entry != null) {
                    mc.getToastGui().add(new AddendumToast(entry));
                    shownPageFlags.add(key);
                    saveShownFlags();
                }
            }
        }

        knownPageFlags.clear();
        knownPageFlags.addAll(currentPageFlags);
    }

    private static Set<String> collectPageFlags(IPlayerKnowledge knowledge) {
        Set<String> result = new HashSet<>();
        for (String key : knowledge.getResearchList()) {
            if (knowledge.hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                result.add(key);
            }
        }
        return result;
    }

    private static File getShownFlagsFile(Minecraft mc, java.util.UUID playerId) {
        File dir = new File(mc.mcDataDir, "config/thaumicforever");
        return new File(dir, "addendum_toasts_" + playerId + ".properties");
    }

    private void loadShownFlags() {
        if (shownFlagsFile == null || !shownFlagsFile.isFile()) {
            return;
        }

        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(shownFlagsFile)) {
            properties.load(in);
            shownPageFlags.addAll(properties.stringPropertyNames());
        } catch (IOException ignored) {}
    }

    private void saveShownFlags() {
        if (shownFlagsFile == null) {
            return;
        }

        File dir = shownFlagsFile.getParentFile();
        if (dir != null && !dir.isDirectory() && !dir.mkdirs()) {
            return;
        }

        Properties properties = new Properties();
        for (String key : shownPageFlags) {
            properties.setProperty(key, "true");
        }

        try (FileOutputStream out = new FileOutputStream(shownFlagsFile)) {
            properties.store(out, "Thaumic Forever shown addendum toast flags");
        } catch (IOException ignored) {}
    }
}
