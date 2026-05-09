package com.example.coremod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.ProvisionRequest;

public final class ThaumcraftLogisticsRequestLifetimePatch {

    private static final AtomicInteger NEXT_UI = new AtomicInteger(8192);
    private static final Queue<QueuedRequest> PRIORITY_QUEUE = new ConcurrentLinkedQueue<QueuedRequest>();
    private static final Queue<QueuedRequest> QUEUE = new ConcurrentLinkedQueue<QueuedRequest>();
    private static final Queue<QueuedRequest> ACTIVE = new ConcurrentLinkedQueue<QueuedRequest>();
    private static final Queue<Integer> DELIVERED_UI = new ConcurrentLinkedQueue<Integer>();
    private static volatile boolean TICKER_REGISTERED = false;
    private static final int DISPATCH_INTERVAL_TICKS = 8;
    private static final int ACTIVE_STALL_TICKS = 80;
    private static final int MAX_PARALLEL_ITEM_REQUESTS = 1;

    private ThaumcraftLogisticsRequestLifetimePatch() {}

    private static final class QueuedRequest {
        int dim;
        Entity entity;
        BlockPos pos;
        EnumFacing side;
        ItemStack stack;
        long dispatchAt;
        long activeSince;
        int ui;
        int retries;
    }

    public static IMessage handle(Object message, MessageContext ctx) {
        if (message == null || ctx == null || ctx.getServerHandler() == null) {
            return null;
        }

        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                process(message, player);
            }
        });
        return null;
    }

    private static void process(Object message, EntityPlayerMP player) {
        try {
            World world = player.getServerWorld();
            BlockPos pos = (BlockPos) getField(message, "pos");
            EnumFacing side = (EnumFacing) getField(message, "side");
            ItemStack template = (ItemStack) getField(message, "stack");
            Integer totalObj = (Integer) getField(message, "stacksize");

            if (world == null || template == null || template.isEmpty() || totalObj == null || totalObj.intValue() <= 0) {
                return;
            }

            ensureTicker();
            int remaining = totalObj.intValue();
            int chunkSize = Math.max(1, template.getMaxStackSize());
            long dispatchAt = world.getTotalWorldTime();
            while (remaining > 0) {
                ItemStack request = template.copy();
                request.setCount(Math.min(chunkSize, remaining));
                queueRequest(world, player, pos, side, request, dispatchAt);
                dispatchAt += DISPATCH_INTERVAL_TICKS;
                remaining -= request.getCount();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void queueRequest(World world, EntityPlayerMP player, BlockPos pos, EnumFacing side, ItemStack request, long dispatchAt) {
        queueRequest(world, pos == null ? player : null, pos, side, request, dispatchAt, false);
    }

    public static void queuePriorityRemainderForEntity(World world, Entity entity, ItemStack stack) {
        queuePriorityRemainder(world, entity, null, null, stack);
    }

    public static void queuePriorityRemainderForPos(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        queuePriorityRemainder(world, null, pos, side, stack);
    }

    private static void queuePriorityRemainder(World world, Entity entity, BlockPos pos, EnumFacing side, ItemStack stack) {
        if (world == null || stack == null || stack.isEmpty()) {
            return;
        }
        ensureTicker();
        queueRequest(world, entity, pos, side, stack, world.getTotalWorldTime() + 1L, true);
    }

    private static void queueRequest(World world, Entity entity, BlockPos pos, EnumFacing side, ItemStack request, long dispatchAt, boolean priority) {
        QueuedRequest qr = new QueuedRequest();
        qr.dim = world.provider.getDimension();
        qr.entity = pos == null ? entity : null;
        qr.pos = pos;
        qr.side = side;
        qr.stack = request.copy();
        qr.dispatchAt = dispatchAt;
        if (priority) {
            PRIORITY_QUEUE.add(qr);
        } else {
            QUEUE.add(qr);
        }
    }

    private static boolean dispatch(World world, QueuedRequest qr) {
        if (world == null || qr == null || qr.stack == null || qr.stack.isEmpty()) {
            return false;
        }

        int ui = nextUi();
        qr.ui = ui;
        qr.activeSince = world.getTotalWorldTime();
        if (qr.pos != null && qr.side != null) {
            GolemHelper.requestProvisioning(world, qr.pos, qr.side, qr.stack, ui);
            extendNewRequest(world, null, qr.pos, qr.side, qr.stack, ui);
            return true;
        } else if (qr.entity != null && !qr.entity.isDead) {
            GolemHelper.requestProvisioning(world, qr.entity, qr.stack, ui);
            extendNewRequest(world, qr.entity, null, null, qr.stack, ui);
            return true;
        }
        return false;
    }

    private static void ensureTicker() {
        if (TICKER_REGISTERED) {
            return;
        }
        synchronized (ThaumcraftLogisticsRequestLifetimePatch.class) {
            if (TICKER_REGISTERED) {
                return;
            }
            MinecraftForge.EVENT_BUS.register(new QueueTicker());
            TICKER_REGISTERED = true;
        }
    }

    public static final class QueueTicker {
        @SubscribeEvent
        public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.world == null || event.world.isRemote || (QUEUE.isEmpty() && PRIORITY_QUEUE.isEmpty() && ACTIVE.isEmpty())) {
                return;
            }

            long now = event.world.getTotalWorldTime();
            int dim = event.world.provider.getDimension();
            cleanupActive(event.world);
            boolean targetBusy = drainQueue(event.world, now, dim, hasActiveForDimension(dim), PRIORITY_QUEUE);
            drainQueue(event.world, now, dim, targetBusy || hasActiveForDimension(dim), QUEUE);
        }
    }

    private static boolean drainQueue(World world, long now, int dim, boolean targetBusy, Queue<QueuedRequest> queue) {
        if (queue.isEmpty()) {
            return targetBusy;
        }

        ArrayList<QueuedRequest> retry = new ArrayList<QueuedRequest>();
        QueuedRequest qr;
        while ((qr = queue.poll()) != null) {
            if (qr.dim != dim || qr.dispatchAt > now) {
                retry.add(qr);
                continue;
            }

            if (!targetBusy || canDispatchWithActive(dim, qr)) {
                if (dispatch(world, qr)) {
                    ACTIVE.add(qr);
                    targetBusy = true;
                }
            } else {
                retry.add(qr);
            }
        }
        queue.addAll(retry);
        return targetBusy;
    }

    private static boolean canDispatchWithActive(int dim, QueuedRequest next) {
        if (next == null || next.stack == null || next.stack.isEmpty()) {
            return false;
        }

        int matchingActive = 0;
        for (QueuedRequest active : ACTIVE) {
            if (active == null || active.dim != dim) {
                continue;
            }
            if (!sameQueuedTarget(active, next) || !sameItem(active.stack, next.stack)) {
                return false;
            }
            matchingActive++;
        }
        return matchingActive > 0 && matchingActive < MAX_PARALLEL_ITEM_REQUESTS;
    }

    private static void cleanupActive(World world) {
        if (world == null || ACTIVE.isEmpty()) {
            return;
        }

        ArrayList<QueuedRequest> keep = new ArrayList<QueuedRequest>();
        QueuedRequest active;
        while ((active = ACTIVE.poll()) != null) {
            if (active.dim != world.provider.getDimension()) {
                keep.add(active);
                continue;
            }

            if (consumeDelivered(active)) {
                continue;
            }

            if (requestStillExists(world, active)) {
                keep.add(active);
            } else {
                requeueStalled(world, active);
            }
        }
        ACTIVE.addAll(keep);
    }

    public static void markDelivered(ProvisionRequest request, boolean invalid, World world) {
        if (request == null) {
            return;
        }

        int ui = getIntField(request, "ui", -1);
        request.setInvalid(invalid);
        if (invalid && ui >= 8192) {
            DELIVERED_UI.add(Integer.valueOf(ui));
        }
    }

    public static void cancelForPlayer(EntityPlayerMP player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return;
        }

        int dim = player.getServerWorld().provider.getDimension();
        cancelQueued(PRIORITY_QUEUE, player, dim, stack);
        cancelQueued(QUEUE, player, dim, stack);
        cancelActive(player.getServerWorld(), player, stack);
    }

    private static boolean consumeDelivered(QueuedRequest active) {
        return active != null && active.ui >= 8192 && DELIVERED_UI.remove(Integer.valueOf(active.ui));
    }

    private static void cancelQueued(Queue<QueuedRequest> queue, EntityPlayerMP player, int dim, ItemStack stack) {
        if (queue == null || queue.isEmpty()) {
            return;
        }

        ArrayList<QueuedRequest> keep = new ArrayList<QueuedRequest>();
        QueuedRequest request;
        while ((request = queue.poll()) != null) {
            if (request.dim == dim && sameItem(request.stack, stack) && sameCancelTarget(request, player)) {
                continue;
            }
            keep.add(request);
        }
        queue.addAll(keep);
    }

    private static void cancelActive(World world, EntityPlayerMP player, ItemStack stack) {
        if (world == null || ACTIVE.isEmpty()) {
            return;
        }

        ArrayList<QueuedRequest> keep = new ArrayList<QueuedRequest>();
        QueuedRequest active;
        while ((active = ACTIVE.poll()) != null) {
            if (active.dim == world.provider.getDimension() && sameItem(active.stack, stack) && sameCancelTarget(active, player)) {
                invalidateProvisionRequest(world, active);
                continue;
            }
            keep.add(active);
        }
        ACTIVE.addAll(keep);
    }

    private static boolean sameCancelTarget(QueuedRequest request, EntityPlayerMP player) {
        return request != null && (request.entity == null || request.entity == player);
    }

    private static void invalidateProvisionRequest(World world, QueuedRequest active) {
        ArrayList<ProvisionRequest> list = GolemHelper.provisionRequests.get(world.provider.getDimension());
        if (list == null) {
            return;
        }

        for (ProvisionRequest request : list) {
            if (request == null || request.isInvalid()) {
                continue;
            }
            if (active.ui >= 8192 && getIntField(request, "ui", -1) == active.ui) {
                request.setInvalid(true);
                return;
            }
        }
    }

    private static boolean hasActiveForDimension(int dim) {
        for (QueuedRequest active : ACTIVE) {
            if (active != null && active.dim == dim) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameQueuedTarget(QueuedRequest first, QueuedRequest second) {
        if (first == null || second == null) {
            return false;
        }
        if (first.entity != null || second.entity != null) {
            return first.entity != null && first.entity == second.entity;
        }
        return first.pos != null
            && first.pos.equals(second.pos)
            && first.side == second.side;
    }

    private static boolean sameItem(ItemStack first, ItemStack second) {
        return first != null
            && second != null
            && !first.isEmpty()
            && !second.isEmpty()
            && ItemStack.areItemsEqual(first, second)
            && ItemStack.areItemStackTagsEqual(first, second);
    }

    private static boolean requestStillExists(World world, QueuedRequest active) {
        ArrayList<ProvisionRequest> list = GolemHelper.provisionRequests.get(world.provider.getDimension());
        if (list == null) {
            return false;
        }

        for (ProvisionRequest request : list) {
            if (request == null || request.isInvalid()) {
                continue;
            }
            if (!sameTarget(request, active.entity, active.pos, active.side)) {
                continue;
            }
            if (!ItemStack.areItemStacksEqual(request.getStack(), active.stack)) {
                continue;
            }
            if (active.ui != 0 && getIntField(request, "ui", -1) != active.ui) {
                continue;
            }
            if (isStalled(world, request, active)) {
                request.setInvalid(true);
                return false;
            }
            return true;
        }
        return false;
    }

    private static boolean isStalled(World world, ProvisionRequest request, QueuedRequest active) {
        if (request.getLinkedTask() != null) {
            return false;
        }
        return world.getTotalWorldTime() - active.activeSince > ACTIVE_STALL_TICKS;
    }

    private static void requeueStalled(World world, QueuedRequest active) {
        QueuedRequest retry = copyForRetry(active);
        retry.retries = active.retries < 1000 ? active.retries + 1 : active.retries;
        retry.dispatchAt = world.getTotalWorldTime() + DISPATCH_INTERVAL_TICKS;
        PRIORITY_QUEUE.add(retry);
    }

    private static QueuedRequest copyForRetry(QueuedRequest source) {
        QueuedRequest copy = new QueuedRequest();
        copy.dim = source.dim;
        copy.entity = source.entity;
        copy.pos = source.pos;
        copy.side = source.side;
        copy.stack = source.stack.copy();
        return copy;
    }

    private static void extendNewRequest(World world, Entity entity, BlockPos pos, EnumFacing side, ItemStack stack, int ui) {
        ArrayList<ProvisionRequest> list = GolemHelper.provisionRequests.get(world.provider.getDimension());
        if (list == null) {
            return;
        }

        for (int i = list.size() - 1; i >= 0; --i) {
            ProvisionRequest request = list.get(i);
            if (request == null || request.isInvalid()) {
                continue;
            }
            if (!sameTarget(request, entity, pos, side)) {
                continue;
            }
            if (!ItemStack.areItemStacksEqual(request.getStack(), stack)) {
                continue;
            }

            request.setUI(ui);
            request.setLinkedTask(null);
            return;
        }
    }

    private static boolean sameTarget(ProvisionRequest request, Entity entity, BlockPos pos, EnumFacing side) {
        if (entity != null) {
            return request.getEntity() == entity;
        }
        return pos != null && pos.equals(request.getPos()) && side == request.getSide();
    }

    private static int nextUi() {
        int value = NEXT_UI.getAndIncrement();
        if (value == Integer.MAX_VALUE) {
            NEXT_UI.compareAndSet(Integer.MAX_VALUE, 8192);
        }
        return value;
    }

    private static Object getField(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static int getIntField(Object target, String name, int def) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            Object value = field.get(target);
            return value instanceof Integer ? (Integer) value : def;
        } catch (Throwable ignored) {
            return def;
        }
    }
}
