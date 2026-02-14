package com.koteuka404.thaumicforever;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class PacketPrimalAuraSync implements IMessage {

    public int dim;
    public int cx;
    public int cz;
    public float[] vis;

    public PacketPrimalAuraSync() {}

    public PacketPrimalAuraSync(int dim, int cx, int cz, float[] vis) {
        this.dim = dim;
        this.cx = cx;
        this.cz = cz;
        this.vis = vis;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        cx = buf.readInt();
        cz = buf.readInt();
        vis = new float[Primal.COUNT];
        for (int i = 0; i < Primal.COUNT; i++) {
            vis[i] = buf.readFloat();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeInt(cx);
        buf.writeInt(cz);
        for (int i = 0; i < Primal.COUNT; i++) {
            buf.writeFloat(vis[i]);
        }
    }

    public static final class Handler implements IMessageHandler<PacketPrimalAuraSync, IMessage> {
        @Override
        public IMessage onMessage(PacketPrimalAuraSync msg, MessageContext ctx) {

            if (!ctx.side.isClient()) return null;

            IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
            thread.addScheduledTask(() -> {
                ClientPrimalAuraCache.put(msg.dim, msg.cx, msg.cz, msg.vis);
            });

            return null;
        }
    }
}
