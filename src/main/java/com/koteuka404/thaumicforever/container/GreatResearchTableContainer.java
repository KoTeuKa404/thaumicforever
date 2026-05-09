package com.koteuka404.thaumicforever.container;

import com.wonginnovations.oldresearch.common.items.ItemResearchNote;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.container.slot.SlotLimitedByClass;

public class GreatResearchTableContainer extends Container {
    private final IInventory tileEntity;
    private int lastProgress;
    private int lastEfficiency;

    public GreatResearchTableContainer(InventoryPlayer playerInventory, IInventory tileEntity) {
        this.tileEntity = tileEntity;

        this.addSlotToContainer(new SlotLimitedByClass(IScribeTools.class, tileEntity, 0, 43, 81));

        this.addSlotToContainer(new SlotLimitedByClass(ItemResearchNote.class, tileEntity, 1, 43, 45));

        int playerInventoryStartX = 32;
        int playerInventoryStartY = 150;
        int slotSpacingX = 18;
        int slotSpacingY = 18;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, playerInventoryStartX + j * slotSpacingX, playerInventoryStartY + i * slotSpacingY));
            }
        }

        int hotbarX = 32;
        int hotbarY = 208;
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(playerInventory, i, hotbarX + i * slotSpacingX, hotbarY));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int progress = this.tileEntity.getField(0);
        int efficiency = this.tileEntity.getField(1);
        if (progress == this.lastProgress && efficiency == this.lastEfficiency) return;

        for (IContainerListener listener : this.listeners) {
            if (progress != this.lastProgress) {
                listener.sendWindowProperty(this, 0, progress);
            }
            if (efficiency != this.lastEfficiency) {
                listener.sendWindowProperty(this, 1, efficiency);
            }
        }
        this.lastProgress = progress;
        this.lastEfficiency = efficiency;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        this.tileEntity.setField(id, data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        result = stack.copy();
        int tableSlotCount = 2;

        if (index < tableSlotCount) {
            if (!this.mergeItemStack(stack, tableSlotCount, this.inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof IScribeTools) {
            if (!this.mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof ItemResearchNote) {
            if (!this.mergeItemStack(stack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }

        return result;
    }
}
