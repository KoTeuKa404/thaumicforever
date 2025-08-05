package com.koteuka404.thaumicforever;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class MysticEventHandler {
    private static final Map<UUID, ItemStack[]> mysticSync = new HashMap<>();

    @SubscribeEvent
    public static void onClone(Clone ev) {
        if (!(ev.getEntityPlayer() instanceof EntityPlayerMP)) return;
        IBaublesItemHandler oldH = BaublesApi.getBaublesHandler(ev.getOriginal());
        IBaublesItemHandler newH = BaublesApi.getBaublesHandler(ev.getEntityPlayer());
        if (oldH instanceof BaublesContainer && newH instanceof BaublesContainer) {
            ((BaublesContainer)newH)
              .deserializeNBT(((BaublesContainer)oldH).serializeNBT());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent ev) {
        mysticSync.remove(ev.player.getUniqueID());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END || ev.player.world.isRemote) return;
        if (!(ev.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) ev.player;
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);

        int originalSlots = handler.getSlots();
        List<MysticBaubleSlots.SlotInfo> slots = MysticBaubleSlots.getAllSlots(player);

        // onWornTick
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = handler.getStackInSlot(originalSlots + i);
            if (!stack.isEmpty() &&
                stack.hasCapability(baubles.api.cap.BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
                stack.getCapability(baubles.api.cap.BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)
                     .onWornTick(stack, player);
            }
        }

        syncChanged(player, handler, originalSlots, slots.size());
    }

    @SubscribeEvent
    public static void onPlayerDrops(PlayerDropsEvent ev) {
        if (!(ev.getEntity() instanceof EntityPlayerMP)) return;
        if (ev.getEntity().world.isRemote) return;
        if (ev.getEntity().world.getGameRules().getBoolean("keepInventory")) return;
        dropMystic((EntityPlayerMP) ev.getEntity(), ev.getDrops(), ev.getEntity());
    }

    @SubscribeEvent
    public static void onStartTracking(StartTracking ev) {
        if (ev.getTarget() instanceof EntityPlayerMP &&
            ev.getEntityPlayer() instanceof EntityPlayerMP) {
            syncSlots((EntityPlayerMP) ev.getTarget(),
                Collections.singletonList((EntityPlayerMP) ev.getEntityPlayer()));
        }
    }

    @SubscribeEvent
    public static void onJoin(net.minecraftforge.event.entity.EntityJoinWorldEvent ev) {
        if (ev.getEntity() instanceof EntityPlayerMP) {
            syncSlots((EntityPlayerMP) ev.getEntity(),
                Collections.singletonList((EntityPlayerMP) ev.getEntity()));
        }
    }

    private static void syncSlots(EntityPlayerMP player, Collection<EntityPlayerMP> receivers) {
        IBaublesItemHandler h = BaublesApi.getBaublesHandler(player);
        int originalSlots = h.getSlots();
        int count        = MysticBaubleSlots.getAllSlots(player).size();

        for (int i = 0; i < count; i++) {
            int phys = originalSlots + i;
            PacketSync pkt = new PacketSync(player, phys, h.getStackInSlot(phys));
            for (EntityPlayerMP r : receivers) {
                PacketHandler.INSTANCE.sendTo(pkt, r);
            }
        }

        ItemStack[] arr = new ItemStack[count];
        for (int i = 0; i < count; i++) {
            arr[i] = h.getStackInSlot(originalSlots + i).copy();
        }
        mysticSync.put(player.getUniqueID(), arr);
    }

    private static void syncChanged(EntityPlayerMP player, IBaublesItemHandler handler, int originalSlots, int count) {
        UUID id = player.getUniqueID();
        ItemStack[] old = mysticSync.get(id);
        if (old == null || old.length != count) {
            syncSlots(player, Collections.singletonList(player));
            return;
        }

        WorldServer ws = (WorldServer) player.world;
        List<EntityPlayerMP> receivers = ws.getEntityTracker()
            .getTrackingPlayers(player).stream()
            .filter(p -> p instanceof EntityPlayerMP)
            .map(p -> (EntityPlayerMP)p)
            .collect(Collectors.toList());
        receivers.add(player);

        for (int i = 0; i < count; i++) {
            int phys = originalSlots + i;
            ItemStack cur = handler.getStackInSlot(phys);
            if (!ItemStack.areItemStacksEqual(cur, old[i])) {
                PacketSync pkt = new PacketSync(player, phys, cur);
                for (EntityPlayerMP r : receivers) {
                    PacketHandler.INSTANCE.sendTo(pkt, r);
                }
                old[i] = cur.copy();
            }
        }
    }

    private static void dropMystic(EntityPlayerMP player,
                                   List<EntityItem> drops,
                                   Entity source) {
        IBaublesItemHandler h = BaublesApi.getBaublesHandler(player);
        int originalSlots = h.getSlots();
        int count        = MysticBaubleSlots.getAllSlots(player).size();

        for (int i = 0; i < count; i++) {
            int phys = originalSlots + i;
            ItemStack stack = h.getStackInSlot(phys);
            if (!stack.isEmpty()) {
                EntityItem ei = new EntityItem(
                    source.world,
                    source.posX, source.posY + source.getEyeHeight(), source.posZ,
                    stack.copy()
                );
                ei.setPickupDelay(40);
                drops.add(ei);
                h.setStackInSlot(phys, ItemStack.EMPTY);
            }
        }
    }
}
