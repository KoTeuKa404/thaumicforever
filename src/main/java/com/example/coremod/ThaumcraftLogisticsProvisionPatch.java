package com.example.coremod;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.ProvisionRequest;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

public final class ThaumcraftLogisticsProvisionPatch {

    private static final AtomicInteger NEXT_UI = new AtomicInteger(4096);
    private static final Queue<PendingRequest> PENDING = new ConcurrentLinkedQueue<PendingRequest>();
    private static volatile boolean TICKER_REGISTERED = false;

    private ThaumcraftLogisticsProvisionPatch() {}

    private static final class PendingRequest {
        World world;
        Entity entity;
        BlockPos pos;
        EnumFacing side;
        ItemStack stack;
        long runAt;
    }

    public static void requestRemainderForEntity(World world, Entity entity, ItemStack stack) {
        if (world == null || entity == null || stack == null || stack.isEmpty()) {
            return;
        }

        ThaumcraftLogisticsRequestLifetimePatch.queuePriorityRemainderForEntity(world, entity, stack);
    }

    public static void requestRemainderForPos(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        if (world == null || pos == null || side == null || stack == null || stack.isEmpty()) {
            return;
        }

        ThaumcraftLogisticsRequestLifetimePatch.queuePriorityRemainderForPos(world, pos, side, stack);
    }

    public static void releaseOverbooked(World world, ISealEntity seal) {
        // Requests are serialized by ThaumcraftLogisticsRequestLifetimePatch now.
        // Cancelling a pickup when a chest has only a partial stack breaks vanilla's
        // remainder path and can leave the last chunk undelivered.
        if (isOverbookReleaseDisabled()) {
            return;
        }

        if (world == null || seal == null || seal.getSealPos() == null) {
            return;
        }

        ArrayList<ProvisionRequest> list = GolemHelper.provisionRequests.get(world.provider.getDimension());
        if (list == null || list.isEmpty()) {
            return;
        }

        IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face);
        if (inv == null) {
            return;
        }

        for (int i = 0; i < list.size(); ++i) {
            ProvisionRequest request = list.get(i);
            Task task = request != null ? request.getLinkedTask() : null;
            if (!isPickupTaskForSeal(task, seal)) {
                continue;
            }

            ItemStack stack = request.getStack();
            int available = ThaumcraftInvHelper.countTotalItemsIn(inv, stack, ThaumcraftInvHelper.InvFilter.STRICT);
            int reservedBefore = countReservedBefore(list, i, seal, stack);
            if (reservedBefore + stack.getCount() > available && !task.isReserved()) {
                task.setSuspended(true);
                request.setLinkedTask(null);
            }
        }
    }

    private static boolean isOverbookReleaseDisabled() {
        return true;
    }

    private static void queueRequest(World world, Entity entity, BlockPos pos, EnumFacing side, ItemStack stack) {
        ensureTicker();
        PendingRequest pending = new PendingRequest();
        pending.world = world;
        pending.entity = entity;
        pending.pos = pos;
        pending.side = side;
        pending.stack = stack.copy();
        pending.runAt = world.getTotalWorldTime() + 1L;
        PENDING.add(pending);
    }

    private static void addRequest(World world, Entity entity, BlockPos pos, EnumFacing side, ItemStack stack) {
        if (world == null || stack == null || stack.isEmpty()) {
            return;
        }

        int remaining = stack.getCount();
        int chunkSize = Math.max(1, stack.getMaxStackSize());
        while (remaining > 0) {
            ItemStack request = stack.copy();
            request.setCount(Math.min(chunkSize, remaining));

            int ui = nextUi();
            if (entity != null && !entity.isDead) {
                GolemHelper.requestProvisioning(world, entity, request, ui);
                extendNewRequest(world, entity, null, null, request);
            } else if (pos != null && side != null) {
                GolemHelper.requestProvisioning(world, pos, side, request, ui);
                extendNewRequest(world, null, pos, side, request);
            }
            remaining -= request.getCount();
        }
    }

    private static int nextUi() {
        int value = NEXT_UI.getAndIncrement();
        if (value == Integer.MAX_VALUE) {
            NEXT_UI.compareAndSet(Integer.MAX_VALUE, 4096);
        }
        return value;
    }

    private static void ensureTicker() {
        if (TICKER_REGISTERED) {
            return;
        }
        synchronized (ThaumcraftLogisticsProvisionPatch.class) {
            if (TICKER_REGISTERED) {
                return;
            }
            MinecraftForge.EVENT_BUS.register(new PendingTicker());
            TICKER_REGISTERED = true;
        }
    }

    public static final class PendingTicker {
        @SubscribeEvent
        public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.world == null || event.world.isRemote) {
                return;
            }

            long now = event.world.getTotalWorldTime();
            ArrayList<PendingRequest> retry = new ArrayList<PendingRequest>();
            PendingRequest pending;
            while ((pending = PENDING.poll()) != null) {
                if (pending.world == null || pending.world.provider.getDimension() != event.world.provider.getDimension()) {
                    retry.add(pending);
                    continue;
                }
                if (pending.runAt > now) {
                    retry.add(pending);
                    continue;
                }
                addRequest(event.world, pending.entity, pending.pos, pending.side, pending.stack);
            }
            PENDING.addAll(retry);
        }
    }

    private static void extendNewRequest(World world, Entity entity, BlockPos pos, EnumFacing side, ItemStack stack) {
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

    private static int countReservedBefore(ArrayList<ProvisionRequest> list, int endExclusive, ISealEntity seal, ItemStack stack) {
        int count = 0;
        for (int i = 0; i < endExclusive; ++i) {
            ProvisionRequest request = list.get(i);
            Task task = request != null ? request.getLinkedTask() : null;
            if (isPickupTaskForSeal(task, seal) && sameItem(request.getStack(), stack)) {
                count += request.getStack().getCount();
            }
        }
        return count;
    }

    private static boolean sameItem(ItemStack first, ItemStack second) {
        return first != null
            && second != null
            && !first.isEmpty()
            && !second.isEmpty()
            && ItemStack.areItemsEqual(first, second)
            && ItemStack.areItemStackTagsEqual(first, second);
    }

    private static boolean isPickupTaskForSeal(Task task, ISealEntity seal) {
        return task != null
            && task.getData() == 0
            && task.getSealPos() != null
            && task.getSealPos().equals(seal.getSealPos())
            && !task.isSuspended()
            && !task.isCompleted();
    }
}
