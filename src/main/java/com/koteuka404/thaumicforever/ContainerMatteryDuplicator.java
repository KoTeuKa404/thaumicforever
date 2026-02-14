package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMatteryDuplicator extends Container {

    private final TileEntityMatteryDuplicator tileEntity;
    private final EntityPlayer player;

    public ContainerMatteryDuplicator(InventoryPlayer playerInventory, TileEntityMatteryDuplicator tileEntity) {
        this.tileEntity = tileEntity;
        this.player = playerInventory.player;

        this.tileEntity.setLastUser(this.player);

        // 3x3 input (0..8)
        addSlotToContainer(new Slot(tileEntity, 0, 36, 8));
        addSlotToContainer(new Slot(tileEntity, 1, 56, 8));
        addSlotToContainer(new Slot(tileEntity, 2, 76, 8));

        addSlotToContainer(new Slot(tileEntity, 3, 36, 28));
        addSlotToContainer(new Slot(tileEntity, 4, 56, 28));
        addSlotToContainer(new Slot(tileEntity, 5, 76, 28));

        addSlotToContainer(new Slot(tileEntity, 6, 36, 48));
        addSlotToContainer(new Slot(tileEntity, 7, 56, 48));
        addSlotToContainer(new Slot(tileEntity, 8, 76, 48));

        // output (9)
        addSlotToContainer(new Slot(tileEntity, 9, 132, 28) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
                tileEntity.setLastUser(playerIn);
                tileEntity.onCraftTaken(playerIn);
                return super.onTake(playerIn, stack);
            }
        });

        bindPlayerInventory(playerInventory);
    }

    private void bindPlayerInventory(InventoryPlayer inv) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 22));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(inv, k, 8 + k * 18, 142 + 22));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (index == 9) {
                if (!this.mergeItemStack(stackInSlot, 10, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stackInSlot, itemstack);
            }
            else if (index < 9) {
                if (!this.mergeItemStack(stackInSlot, 10, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stackInSlot);
        }

        return itemstack;
    }


    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    
        for (int i = 0; i < tileEntity.getFieldCount(); i++) {
            int v = tileEntity.getField(i);
            for (int j = 0; j < this.listeners.size(); j++) {
                this.listeners.get(j).sendWindowProperty(this, i, v);
            }
        }
    }
    
    @Override
    public void updateProgressBar(int id, int data) {
        tileEntity.setField(id, data);
    }
    
}