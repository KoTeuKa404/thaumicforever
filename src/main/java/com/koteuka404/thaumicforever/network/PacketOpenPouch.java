package com.koteuka404.thaumicforever.network;

import com.koteuka404.thaumicforever.registry.ModGuiHandler;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.koteuka404.thaumicforever.item.ItemPouch;
import com.koteuka404.thaumicforever.ThaumicForever;

public class PacketOpenPouch implements IMessage {
    private static final int BAUBLES_ARG_BASE = 1000;

    public PacketOpenPouch() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketOpenPouch, IMessage> {
        @Override
        public IMessage onMessage(PacketOpenPouch msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                int slotArg = findPouchSlotArg(player);
                if (slotArg < 0) return;
                player.openGui(ThaumicForever.instance, ModGuiHandler.GUI_POUCH, player.getServerWorld(), slotArg, 0, 0);
            });
            return null;
        }

        private static int findPouchSlotArg(EntityPlayerMP player) {
            ItemStack main = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            if (!main.isEmpty() && main.getItem() instanceof ItemPouch) return 0;

            ItemStack off = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            if (!off.isEmpty() && off.getItem() instanceof ItemPouch) return 1;

            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSlots(); i++) {
                    ItemStack s = baubles.getStackInSlot(i);
                    if (!s.isEmpty() && s.getItem() instanceof ItemPouch) {
                        return BAUBLES_ARG_BASE + i;
                    }
                }
            }

            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack s = player.inventory.getStackInSlot(i);
                if (!s.isEmpty() && s.getItem() instanceof ItemPouch) {
                    return i + 2; // 0/1 are reserved for hands.
                }
            }
            return -1;
        }
    }
}
