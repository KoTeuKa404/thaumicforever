package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMatteryDuplicator extends Container {

    private final TileEntityMatteryDuplicator tileEntity;

    public ContainerMatteryDuplicator(InventoryPlayer playerInventory, TileEntityMatteryDuplicator tileEntity) {
        this.tileEntity = tileEntity;

        // Adding slots for the duplicator inventory
        addSlotToContainer(new Slot(tileEntity, 0, 36, 8));
        addSlotToContainer(new Slot(tileEntity, 1, 56, 8));
        addSlotToContainer(new Slot(tileEntity, 2, 76, 8));

        addSlotToContainer(new Slot(tileEntity, 3, 36, 28));
        addSlotToContainer(new Slot(tileEntity, 4, 56, 28));
        addSlotToContainer(new Slot(tileEntity, 5, 76, 28));

        addSlotToContainer(new Slot(tileEntity, 6, 36, 48));
        addSlotToContainer(new Slot(tileEntity, 7, 56, 48));
        addSlotToContainer(new Slot(tileEntity, 8, 76, 48));

        // Output slot for the crafted result
        addSlotToContainer(new Slot(tileEntity, 9, 132, 28) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false; // Prevent placing items into the output slot
            }

            @Override
            public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
                // Витрачаємо есенсію після забору результату
                if (tileEntity.hasEnoughEssentia()) {
                    tileEntity.consumeEssentia();
                }
                return super.onTake(playerIn, stack); // Викликаємо базову логіку після забору предмету
            }
        });

        // Adding player inventory
        bindPlayerInventory(playerInventory);
    }

    private void bindPlayerInventory(InventoryPlayer playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 22));
            }
        }

        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142 + 22));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            // Перевірка, чи слот є вихідним (індекс 9)
            if (index == 9) {
                if (this.tileEntity.hasEnoughEssentia()) {
                    // Переносимо предмет у інвентар гравця
                    if (!this.mergeItemStack(stackInSlot, 10, this.inventorySlots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                    // Витрачаємо есенсію тільки після успішного перенесення предмету
                    this.tileEntity.consumeEssentia();
                    slot.onSlotChange(stackInSlot, itemstack);
                }
            } else {
                // Забезпечуємо, щоб предмети не переміщувалися у слот крафту (0-8)
                if (!this.mergeItemStack(stackInSlot, 10, this.inventorySlots.size(), false)) {
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

        for (int i = 0; i < this.inventorySlots.size(); i++) {
            Slot slot = this.inventorySlots.get(i);
            ItemStack stackInSlot = slot.getStack();

            // Перевіряємо, чи є стек пустим і приводимо його до ItemStack.EMPTY замість null
            if (stackInSlot == null) {
                stackInSlot = ItemStack.EMPTY;
            }

            // Перевірка чи існує стек у слоті
            ItemStack currentStack = slot.getStack();
            if (currentStack == null) {
                currentStack = ItemStack.EMPTY;
            }

            // Перевірка перед викликом areItemStacksEqual
            if (!ItemStack.areItemStacksEqual(currentStack, stackInSlot)) {
                for (int j = 0; j < this.listeners.size(); ++j) {
                    this.listeners.get(j).sendSlotContents(this, i, stackInSlot);
                }
            }
        }
    }
}
