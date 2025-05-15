package com.koteuka404.thaumicforever;

import java.util.List;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ContainerMysticTabExample extends Container {
    private static final int COLUMNS              = 5;
    private static final int X_OFFSET             = 77;
    private static final int Y_OFFSET             = 8;
    private static final int SLOT_SIZE            = 18;
    private static final int VANILLA_ARMOR_SLOTS  = 4;
    private static final int VANILLA_MAIN_ROWS    = 3;
    private static final int VANILLA_HOTBAR_SLOTS = 9;

    public static final int VANILLA_SLOT_COUNT = VANILLA_ARMOR_SLOTS + VANILLA_MAIN_ROWS * 9 + VANILLA_HOTBAR_SLOTS;
    public static final int ORIGINAL_BAUBLE_SLOTS = 7;

    private final EntityPlayer player;
    private final BaublesContainer handler;

    public ContainerMysticTabExample(InventoryPlayer inv) {
        this.player = inv.player;

        IBaublesItemHandler raw = BaublesApi.getBaublesHandler(player);

        if (raw instanceof BaublesContainer) {
            this.handler = (BaublesContainer) raw;

        } else {
            this.handler = new BaublesContainer();

            int slots = raw.getSlots();
            for (int i = 0; i < slots; i++) {
                ItemStack stack = raw.getStackInSlot(i);
                handler.setStackInSlot(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            }
        }

        for (int i = 0; i < VANILLA_ARMOR_SLOTS; i++) {
            addSlotToContainer(new Slot(inv, 39 - i, 8, 8 + i * SLOT_SIZE));
        }
        for (int row = 0; row < VANILLA_MAIN_ROWS; row++) {
            for (int col = 0; col < 9; col++) {
                int idx = col + row * 9 + 9;
                addSlotToContainer(new Slot(inv, idx,
                    8 + col * SLOT_SIZE,
                    84 + row * SLOT_SIZE
                ));
            }
        }
        for (int col = 0; col < VANILLA_HOTBAR_SLOTS; col++) {
            addSlotToContainer(new Slot(inv, col,
                8 + col * SLOT_SIZE,
                142
            ));
        }

        List<BaubleType> types = MysticBaubleSlots.getForPlayer(player);
        for (int i = 0; i < types.size(); i++) {
            int physIdx     = ORIGINAL_BAUBLE_SLOTS + i;
            int col         = i % COLUMNS;
            int row         = i / COLUMNS;
            int x           = X_OFFSET + col * SLOT_SIZE;
            int y           = Y_OFFSET + row * SLOT_SIZE;
            BaubleType type = types.get(i);

            addSlotToContainer(new SlotMystic(
                (IItemHandlerModifiable) handler,
                physIdx, x, y,
                player,
                type
            ));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack original = slot.getStack();
        ItemStack copy     = original.copy();

        int startMystic = VANILLA_SLOT_COUNT;
        int endMystic   = startMystic + MysticBaubleSlots.getForPlayer(player).size();

        if (index < VANILLA_SLOT_COUNT) {
            if (!mergeItemStack(original, startMystic, endMystic, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= startMystic && index < endMystic) {
            int mainStart   = VANILLA_ARMOR_SLOTS;
            int hotbarStart = VANILLA_ARMOR_SLOTS + VANILLA_MAIN_ROWS * 9;
            if (!mergeItemStack(original, mainStart, hotbarStart, false)) {
                if (!mergeItemStack(original, hotbarStart, VANILLA_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (original.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return copy;
    }
}