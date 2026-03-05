package com.wonginnovations.oldresearch.common.lib.network;

import com.wonginnovations.oldresearch.common.OldResearchUtils;
import com.wonginnovations.oldresearch.common.lib.research.OldResearchManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;

public class PacketGivePlayerNoteToServer implements IMessage, IMessageHandler<PacketGivePlayerNoteToServer, IMessage> {
    private String key;

    public PacketGivePlayerNoteToServer() {
    }

    public PacketGivePlayerNoteToServer(String key) {
        this.key = key;
    }

    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.key);
    }

    public void fromBytes(ByteBuf buffer) {
        this.key = ByteBufUtils.readUTF8String(buffer);
    }

    public IMessage onMessage(PacketGivePlayerNoteToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            public void run() {
                World world = ctx.getServerHandler().player.world;
                EntityPlayer player = ctx.getServerHandler().player;
                if (world != null && player != null) {
                    String researchKey = message.key;
                    int first = researchKey != null ? researchKey.indexOf('_') : -1;
                    int last = researchKey != null ? researchKey.lastIndexOf('_') : -1;
                    if (first >= 0 && last > first) {
                        researchKey = researchKey.substring(first + 1, last);
                    }
                    if (!ResearchManager.doesPlayerHaveRequisites(player, researchKey)) {
                        sendError(player, "tc.researcherror");
                        return;
                    }
                    if (!OldResearchUtils.isPlayerCarryingScribeTools(player)) {
                        sendError(player, "researchnote.missing.tools");
                        return;
                    }
                    if (!OldResearchUtils.isPlayerCarrying(player, Items.PAPER)) {
                        sendError(player, "researchnote.missing.paper");
                        return;
                    }
                    if (!OldResearchManager.consumeInkFromPlayer(player, false)) {
                        sendError(player, "tile.researchtable.noink.0");
                        sendError(player, "tile.researchtable.noink.1");
                        return;
                    }
                    if (OldResearchManager.givePlayerResearchNote(world, player, message.key)) {
                        world.playSound(player.posX, player.posY, player.posZ, SoundsTC.learn, SoundCategory.MASTER, 0.75F, 1.0F, false);
                    }
                }
            }
        });
        return null;
    }

    private void sendError(EntityPlayer player, String key) {
        TextComponentTranslation message = new TextComponentTranslation(key);
        message.getStyle().setColor(TextFormatting.RED);
        player.sendMessage(message);
    }
}
