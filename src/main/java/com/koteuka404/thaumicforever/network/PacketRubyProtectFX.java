package com.koteuka404.thaumicforever.network;

import com.koteuka404.thaumicforever.client.fx.FXRubyRunes;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRubyProtectFX implements IMessage {
    private int entityId;

    public PacketRubyProtectFX() {
    }

    public PacketRubyProtectFX(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public static class Handler implements IMessageHandler<PacketRubyProtectFX, IMessage> {
        @Override
        public IMessage onMessage(PacketRubyProtectFX msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) {
                    return;
                }

                Entity target = world.getEntityByID(msg.entityId);
                if (target == null) {
                    return;
                }

                Minecraft.getMinecraft().effectRenderer.addEffect(
                    new FXRubyRunes(
                        world,
                        target.posX,
                        target.posY + target.height / 2.0D,
                        target.posZ,
                        target,
                        30,
                        target.rotationYaw,
                        target.rotationPitch
                    )
                );
            });
            return null;
        }
    }
}
