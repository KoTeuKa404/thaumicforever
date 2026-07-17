package com.koteuka404.thaumicforever.container;

import com.koteuka404.thaumicforever.entity.EntityVoidTraider;
import com.koteuka404.thaumicforever.entity.VoidTraiderList;
import com.koteuka404.thaumicforever.entity.VoidTraiderPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerVoidTraider extends Container {
    private final EntityVoidTraider voidTraider;
    private final IInventory requirementInventory = new InventoryBasic("void_traider_requirements", false, 3);

    public ContainerVoidTraider(InventoryPlayer playerInventory, EntityVoidTraider voidTraider) {
        this.voidTraider = voidTraider;

        IItemHandler saleItems = voidTraider.getSaleItems();

        for (int i = 0; i < 9; i++) {
            int x = 25 + (i % 3) * 54;
            int y = 28 + (i / 3) * 40;
            this.addSlotToContainer(new SlotVoidTraiderSale(saleItems, i, x, y));
        }

        for (int i = 0; i < 3; i++) {
            this.addSlotToContainer(new Slot(this.requirementInventory, i, -1000, -1000));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(
                    playerInventory,
                    col + row * 9 + 9,
                    8 + col * 18,
                    146 + row * 18
                ));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(
                playerInventory,
                col,
                8 + col * 18,
                204
            ));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.voidTraider != null
            && !this.voidTraider.isDead
            && playerIn.world == this.voidTraider.world
            && playerIn.getDistanceSq(this.voidTraider) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0 && slotId < 9) {
            if (clickType == ClickType.PICKUP) {
                performTrade(player, slotId);
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }

    public void setRequirementSlotsVisible(boolean visible) {
        for (int i = 0; i < 3; i++) {
            Slot slot = this.getSlot(9 + i);
            slot.xPos = visible ? 56 + i * 24 : -1000;
            slot.yPos = visible ? 74 : -1000;
        }
    }

    public List<ItemStack> getRequirements(int saleSlot) {
        if (saleSlot < 0 || saleSlot >= 9) {
            return Collections.emptyList();
        }

        ItemStack output = voidTraider.getSaleItems().getStackInSlot(saleSlot);
        List<ItemStack> requirements = new ArrayList<>();
        for (String rawRequirement : VoidTraiderList.getRequirements(output)) {
            ItemStack requirement = VoidTraiderPool.parseTradeStack(rawRequirement);
            if (!requirement.isEmpty()) {
                requirements.add(requirement);
            }
        }
        return requirements;
    }

    private void performTrade(EntityPlayer player, int saleSlot) {
        ItemStack output = voidTraider.getSaleItems().getStackInSlot(saleSlot);
        if (output.isEmpty()) {
            return;
        }

        List<ItemStack> requirements = getRequirements(saleSlot);
        for (int i = 0; i < requirements.size() && i < 3; i++) {
            ItemStack inserted = this.requirementInventory.getStackInSlot(i);
            if (!matchesRequirement(inserted, requirements.get(i))) {
                return;
            }
        }
        for (int i = requirements.size(); i < 3; i++) {
            if (!this.requirementInventory.getStackInSlot(i).isEmpty()) {
                return;
            }
        }

        for (int i = 0; i < requirements.size() && i < 3; i++) {
            ItemStack inserted = this.requirementInventory.getStackInSlot(i);
            inserted.shrink(requirements.get(i).getCount());
            if (inserted.isEmpty()) {
                this.requirementInventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }

        ItemStack reward = output.copy();
        if (!player.addItemStackToInventory(reward)) {
            player.dropItem(reward, false);
        }

        // Every generated offer can be purchased only once from this trader.
        voidTraider.getSaleItems().setStackInSlot(saleSlot, ItemStack.EMPTY);
    }

    private static boolean matchesRequirement(ItemStack inserted, ItemStack required) {
        return !inserted.isEmpty()
            && sameItem(inserted, required)
            && inserted.getCount() >= required.getCount();
    }

    private static boolean sameItem(ItemStack first, ItemStack second) {
        return !first.isEmpty()
            && ItemStack.areItemsEqual(first, second)
            && ItemStack.areItemStackTagsEqual(first, second);
    }

    private static class SlotVoidTraiderSale extends SlotItemHandler {
        public SlotVoidTraiderSale(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return false;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (player.world.isRemote) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            ItemStack stack = this.requirementInventory.removeStackFromSlot(i);
            if (!stack.isEmpty() && !player.addItemStackToInventory(stack)) {
                player.dropItem(stack, false);
            }
        }
    }
}
