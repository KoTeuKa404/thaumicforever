package com.koteuka404.thaumicforever.network;

import com.koteuka404.thaumicforever.compat.ResearchBaubleMapping;
import com.koteuka404.thaumicforever.config.ModConfig;
import com.wonginnovations.oldresearch.common.lib.research.OldResearchManager;
import com.wonginnovations.oldresearch.core.OldResearchToggle;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketServerConfigSync implements IMessage {
    private boolean enableOldResearch;
    private boolean enableOldResearchOnly;
    private boolean enableMysticGuiButton;
    private String[] immediateBaubles = new String[0];
    private String[] categoryBaubles = new String[0];

    public PacketServerConfigSync() {}

    public PacketServerConfigSync(boolean enableOldResearch, boolean enableOldResearchOnly, boolean enableMysticGuiButton, String[] immediateBaubles, String[] categoryBaubles) {
        this.enableOldResearch = enableOldResearch;
        this.enableOldResearchOnly = enableOldResearchOnly;
        this.enableMysticGuiButton = enableMysticGuiButton;
        this.immediateBaubles = copyOrEmpty(immediateBaubles);
        this.categoryBaubles = copyOrEmpty(categoryBaubles);
    }

    public static PacketServerConfigSync fromServerConfig() {
        return new PacketServerConfigSync(
            ModConfig.enableOldResearch,
            ModConfig.enableOldResearchOnly,
            ModConfig.enableMysticGuiButton,
            ModConfig.immediateBaubles,
            ModConfig.categoryBaubles
        );
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        enableOldResearch = buf.readBoolean();
        enableOldResearchOnly = buf.readBoolean();
        enableMysticGuiButton = buf.readBoolean();
        immediateBaubles = readStringArray(buf);
        categoryBaubles = readStringArray(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(enableOldResearch);
        buf.writeBoolean(enableOldResearchOnly);
        buf.writeBoolean(enableMysticGuiButton);
        writeStringArray(buf, immediateBaubles);
        writeStringArray(buf, categoryBaubles);
    }

    private static String[] copyOrEmpty(String[] values) {
        return values == null ? new String[0] : values.clone();
    }

    private static void writeStringArray(ByteBuf buf, String[] values) {
        values = copyOrEmpty(values);
        buf.writeInt(values.length);
        for (String value : values) {
            ByteBufUtils.writeUTF8String(buf, value == null ? "" : value);
        }
    }

    private static String[] readStringArray(ByteBuf buf) {
        int length = Math.max(0, Math.min(buf.readInt(), 256));
        String[] values = new String[length];
        for (int i = 0; i < length; i++) {
            values[i] = ByteBufUtils.readUTF8String(buf);
        }
        return values;
    }

    public static class Handler implements IMessageHandler<PacketServerConfigSync, IMessage> {
        @Override
        public IMessage onMessage(PacketServerConfigSync msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                OldResearchToggle.setServerOverride(msg.enableOldResearch);
                OldResearchManager.applyServerResearchConfig(msg.enableOldResearch, msg.enableOldResearchOnly);
                ModConfig.enableMysticGuiButton = msg.enableMysticGuiButton;
                ModConfig.immediateBaubles = copyOrEmpty(msg.immediateBaubles);
                ModConfig.categoryBaubles = copyOrEmpty(msg.categoryBaubles);
                ResearchBaubleMapping.reloadFromConfig();
            });
            return null;
        }
    }
}
