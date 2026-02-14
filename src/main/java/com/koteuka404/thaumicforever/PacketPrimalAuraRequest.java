package com.koteuka404.thaumicforever;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class PacketPrimalAuraRequest implements IMessage {

    private int dim;
    private int cx;
    private int cz;

    public PacketPrimalAuraRequest() {}

    public PacketPrimalAuraRequest(int dim, int cx, int cz) {
        this.dim = dim;
        this.cx = cx;
        this.cz = cz;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        cx = buf.readInt();
        cz = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeInt(cx);
        buf.writeInt(cz);
    }

    public static final class Handler implements IMessageHandler<PacketPrimalAuraRequest, IMessage> {
        @Override
        public IMessage onMessage(PacketPrimalAuraRequest msg, MessageContext ctx) {
            EntityPlayerMP pl = ctx.getServerHandler().player;

            pl.getServerWorld().addScheduledTask(() -> {
                WorldServer ws = pl.getServer().getWorld(msg.dim);
                if (ws == null) return;

                // don't force-generate chunks
                if (!ws.getChunkProvider().isChunkGeneratedAt(msg.cx, msg.cz)) return;

                BlockPos pos = new BlockPos((msg.cx << 4) + 8, 64, (msg.cz << 4) + 8);

                float[] vis = new float[Primal.COUNT];
                for (Primal p : Primal.values()) {
                    vis[p.id] = PrimalAuraHandler.get(ws, pos, p);
                }

                // SERVER DEBUG
                ThaumicForever.LOGGER.info("[AuraReq] dim={} cx={} cz={} send vis={},{},{},{},{},{}",
                    msg.dim, msg.cx, msg.cz,
                    (int) vis[0], (int) vis[1], (int) vis[2],
                    (int) vis[3], (int) vis[4], (int) vis[5]
                );

                ThaumicForever.network.sendTo(new PacketPrimalAuraSync(msg.dim, msg.cx, msg.cz, vis), pl);
            });

            return null;
        }
    }
}
