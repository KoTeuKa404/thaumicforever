package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotMystic extends SlotItemHandler {
    private final EntityPlayer player;
    private final BaubleType    type;
    private ItemStack lastStack = ItemStack.EMPTY;

    public SlotMystic(IItemHandler handler, int index, int x, int y,
                      EntityPlayer player, BaubleType type) {
        super(handler, index, x, y);
        this.player = player;
        this.type   = type;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null))
            return false;
        IBauble ba = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        BaubleType itemType = ba.getBaubleType(stack);
        if (itemType == BaubleType.TRINKET) {
            return ba.canEquip(stack, player);
        }
        return itemType == this.type
            && ba.canEquip(stack, player);
    }

    @Override
    public void onSlotChanged() {
        ItemStack cur = getStack();
        if (!ItemStack.areItemStacksEqual(lastStack, cur)) {
            if (!lastStack.isEmpty() && lastStack.getItem() instanceof IBauble)
                ((IBauble) lastStack.getItem()).onUnequipped(lastStack, player);
            if (!cur.isEmpty()      && cur.getItem()      instanceof IBauble)
                ((IBauble) cur.getItem()).onEquipped(cur, player);
            lastStack = cur.copy();
        }
        super.onSlotChanged();
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    public BaubleType getType() {
        return type;
    }
}
