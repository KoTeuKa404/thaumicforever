package com.wonginnovations.oldresearch.common.lib.network;

import com.wonginnovations.oldresearch.client.lib.PlayerNotifications;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public class PacketAspectDiscoveryError implements IMessage, IMessageHandler<PacketAspectDiscoveryError, IMessage> {

    private String aspect;

    public PacketAspectDiscoveryError() {
    }

    public PacketAspectDiscoveryError(String aspect) {
        this.aspect = aspect;
    }

    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.aspect);
    }

    public void fromBytes(ByteBuf buffer) {
        this.aspect = ByteBufUtils.readUTF8String(buffer);
    }

    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketAspectDiscoveryError message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            public void run() {
                Aspect aspect = Aspect.getAspect(message.aspect);
                if (aspect != null) {
                    String helpKey = "tc.aspect.help." + message.aspect;
                    String helpText = I18n.hasKey(helpKey) ? I18n.format(helpKey) : aspect.getName();
                    String discoveryText = I18n.format("tc.discoveryerror", helpText);
                    PlayerNotifications.addNotification(discoveryText);
                }
            }
        });
        return null;
    }

}
