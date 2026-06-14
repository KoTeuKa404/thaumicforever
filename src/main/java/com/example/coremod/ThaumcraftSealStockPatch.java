package com.example.coremod;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.ProvisionRequest;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.lib.utils.InventoryUtils;

public final class ThaumcraftSealStockPatch {

    private ThaumcraftSealStockPatch() {}

    public static int countStockedAndRequested(IItemHandler inventory, ItemStack stack, ThaumcraftInvHelper.InvFilter filter, World world, ISealEntity seal) {
        int count = ThaumcraftInvHelper.countTotalItemsIn(inventory, stack, filter);
        if (world == null || seal == null || seal.getSealPos() == null || stack == null || stack.isEmpty()) {
            return count;
        }

        ArrayList<ProvisionRequest> requests = GolemHelper.provisionRequests.get(world.provider.getDimension());
        if (requests == null || requests.isEmpty()) {
            return count;
        }

        long now = System.currentTimeMillis();
        for (ProvisionRequest request : requests) {
            if (!isActiveRequest(request, now)) {
                continue;
            }
            if (!seal.getSealPos().pos.equals(request.getPos()) || seal.getSealPos().face != request.getSide()) {
                continue;
            }
            ItemStack requested = request.getStack();
            if (requested == null || requested.isEmpty() || !InventoryUtils.areItemStacksEqual(stack, requested, filter)) {
                continue;
            }
            count += requested.getCount();
        }
        return count;
    }

    private static boolean isActiveRequest(ProvisionRequest request, long now) {
        if (request == null || request.isInvalid()) {
            return false;
        }

        Task linked = request.getLinkedTask();
        if (linked != null) {
            return !linked.isCompleted() && !linked.isSuspended();
        }

        return request.getTimeout() >= now;
    }
}
