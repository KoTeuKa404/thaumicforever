package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.research.MysticBaubleSlots;
import com.koteuka404.thaumicforever.ThaumicForever;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
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
import com.koteuka404.thaumicforever.compat.BaublesCompat;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class MysticEventHandler {
    private static final Map<UUID, ItemStack[]> mysticSync = new HashMap<>();

    @SubscribeEvent
    public static void onClone(Clone ev) {
        // Baubles/BaublesEX already handles capability clone internally.
        // Avoid duplicate deserialize/copy here - it can corrupt respawn task flow.
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
        List<MysticBaubleSlots.BoundSlot> bound = MysticBaubleSlots.getBoundMysticSlots(player, handler);

        // onWornTick
        for (MysticBaubleSlots.BoundSlot bs : bound) {
            ItemStack stack = handler.getStackInSlot(bs.physicalIndex);
            if (!stack.isEmpty() &&
                stack.hasCapability(baubles.api.cap.BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
                stack.getCapability(baubles.api.cap.BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)
                     .onWornTick(stack, player);
            }
        }

        syncChanged(player, handler, bound);
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
        List<MysticBaubleSlots.BoundSlot> bound = MysticBaubleSlots.getBoundMysticSlots(player, h);

        for (MysticBaubleSlots.BoundSlot bs : bound) {
            int phys = bs.physicalIndex;
            for (EntityPlayerMP r : receivers) {
                BaublesCompat.sendSlotSync(player, phys, h.getStackInSlot(phys), r);
            }
        }

        ItemStack[] arr = new ItemStack[bound.size()];
        for (int i = 0; i < bound.size(); i++) {
            int phys = bound.get(i).physicalIndex;
            arr[i] = h.getStackInSlot(phys).copy();
        }
        mysticSync.put(player.getUniqueID(), arr);
    }

    private static void syncChanged(EntityPlayerMP player, IBaublesItemHandler handler, List<MysticBaubleSlots.BoundSlot> bound) {
        UUID id = player.getUniqueID();
        ItemStack[] old = mysticSync.get(id);
        if (old == null || old.length != bound.size()) {
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

        for (int i = 0; i < bound.size(); i++) {
            int phys = bound.get(i).physicalIndex;
            ItemStack cur = handler.getStackInSlot(phys);
            if (!ItemStack.areItemStacksEqual(cur, old[i])) {
                for (EntityPlayerMP r : receivers) {
                    BaublesCompat.sendSlotSync(player, phys, cur, r);
                }
                old[i] = cur.copy();
            }
        }
    }

    private static void dropMystic(EntityPlayerMP player,List<EntityItem> drops, Entity source) {
        IBaublesItemHandler h = BaublesApi.getBaublesHandler(player);
        List<MysticBaubleSlots.BoundSlot> bound = MysticBaubleSlots.getBoundMysticSlots(player, h);

        for (MysticBaubleSlots.BoundSlot bs : bound) {
            int phys = bs.physicalIndex;
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
