package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAbandonedChest extends Container {
    private final IInventory tileEntity;

    public ContainerAbandonedChest(InventoryPlayer playerInventory, IInventory tileEntity) {
        this.tileEntity = tileEntity;

        // Слоти для сундука (без лівого і правого ряду)
        int chestSlotStartX = 46;  // Зсунули на 2 слота вправо (18 пікселів * 2)
        int chestSlotStartY = 32;  // Зсуваємо слоти на 2 пікселі вниз

        for (int i = 0; i < 3; ++i) {
            for (int j = 1; j < 8; ++j) {  // Починаємо з 1 і закінчуємо на 8 (щоб пропустити перший і останній слот)
                this.addSlotToContainer(new Slot(tileEntity, (j - 1) + i * 7, chestSlotStartX + (j - 1) * 18, chestSlotStartY + i * 18));
            }
        }

        // Слоти інвентаря гравця
        int playerInventoryStartY = 100 + 8;  // Зсуваємо інвентар гравця ще на 10 пікселів вниз

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, chestSlotStartX + j * 18 - 18, playerInventoryStartY + i * 18)); // зміщуємо на -18 для центрування
            }
        }

        // Слоти гарячої панелі
        int hotbarY = 160 + 6;  // Зсуваємо гарячу панель теж на 10 пікселів вниз

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, chestSlotStartX + i * 18 - 18, hotbarY)); // зміщуємо на -18 для центрування
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
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.tileEntity.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.tileEntity.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.tileEntity.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}