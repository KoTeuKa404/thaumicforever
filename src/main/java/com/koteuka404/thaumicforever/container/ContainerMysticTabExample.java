package com.koteuka404.thaumicforever.container;

import java.util.List;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import com.koteuka404.thaumicforever.container.slot.SlotMystic;
import com.koteuka404.thaumicforever.research.MysticBaubleSlots;

public class ContainerMysticTabExample extends Container {
    private static final int COLUMNS              = 5;
    private static final int X_OFFSET             = 77;
    private static final int Y_OFFSET             = 8;
    private static final int SLOT_SIZE            = 18;
    private static final int VANILLA_ARMOR_SLOTS  = 4;
    private static final int VANILLA_MAIN_ROWS    = 3;
    private static final int VANILLA_HOTBAR_SLOTS = 9;

    public static final int VANILLA_SLOT_COUNT = VANILLA_ARMOR_SLOTS
        + VANILLA_MAIN_ROWS * 9
        + VANILLA_HOTBAR_SLOTS;
    private final EntityPlayer     player;
    private final IItemHandlerModifiable handler;
    private final List<MysticBaubleSlots.BoundSlot> mysticSlots;

    public ContainerMysticTabExample(InventoryPlayer inv) {
        this.player = inv.player;

        IBaublesItemHandler raw = BaublesApi.getBaublesHandler(player);
        if (raw instanceof IItemHandlerModifiable) {
            this.handler = (IItemHandlerModifiable) raw;
        } else {
            // Fallback to an empty handler to avoid null issues.
            this.handler = new baubles.api.cap.BaublesContainer();
        }
        for (int i = 0; i < VANILLA_ARMOR_SLOTS; i++) {
            addSlotToContainer(new Slot(inv, 39 - i, 8, 8 + i * SLOT_SIZE));
        }
        for (int row = 0; row < VANILLA_MAIN_ROWS; row++) {
            for (int col = 0; col < 9; col++) {
                int idx = col + row * 9 + 9;
                addSlotToContainer(new Slot(
                    inv, idx,
                    8 + col * SLOT_SIZE,
                    84 + row * SLOT_SIZE
                ));
            }
        }
        for (int col = 0; col < VANILLA_HOTBAR_SLOTS; col++) {
            addSlotToContainer(new Slot(
                inv, col,
                8 + col * SLOT_SIZE,
                142
            ));
        }

        this.mysticSlots = MysticBaubleSlots.getGuiBoundSlots(player, raw);
        for (int i = 0; i < mysticSlots.size(); i++) {
            MysticBaubleSlots.BoundSlot bound = mysticSlots.get(i);
            MysticBaubleSlots.SlotInfo info = bound.slotInfo;
            int physIdx = bound.physicalIndex;
            int col     = i % COLUMNS;
            int row     = i / COLUMNS;
            int x       = X_OFFSET + col * SLOT_SIZE;
            int y       = Y_OFFSET + row * SLOT_SIZE;

            addSlotToContainer(new SlotMystic(
                handler,
                physIdx, x, y,
                player,
                info.type,
                info.categoryKey
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
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack original = slot.getStack();
        ItemStack copy     = original.copy();

        int startMystic = VANILLA_SLOT_COUNT;
        int count       = mysticSlots.size();
        int endMystic   = startMystic + count;

        if (index < VANILLA_SLOT_COUNT) {
            if (!mergeItemStack(original, startMystic, endMystic, false))
                return ItemStack.EMPTY;
        } else if (index < endMystic) {
            int mainStart   = VANILLA_ARMOR_SLOTS;
            int hotbarStart = VANILLA_ARMOR_SLOTS + VANILLA_MAIN_ROWS * 9;
            if (!mergeItemStack(original, mainStart, hotbarStart, false)) {
                if (!mergeItemStack(original, hotbarStart, VANILLA_SLOT_COUNT, false))
                    return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (original.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else                   slot.onSlotChanged();
        return copy;
    }
}
