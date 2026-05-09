package com.koteuka404.thaumicforever.container.slot;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import com.koteuka404.thaumicforever.research.MysticBaubleSlots;

public class SlotMystic extends SlotItemHandler {
    private final EntityPlayer   player;
    private final BaubleType     type;
    private final String         categoryKey;
    private final boolean        legacyStrictValidation;
    private       ItemStack      lastStack = ItemStack.EMPTY;

    public SlotMystic(net.minecraftforge.items.IItemHandler handler, int index, int x, int y, EntityPlayer player, BaubleType type, String categoryKey) {
        super(handler, index, x, y);
        this.player      = player;
        this.type        = type;
        this.categoryKey = categoryKey;
        this.legacyStrictValidation = MysticBaubleSlots.shouldUseLegacyStrictValidation(handler);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (!MysticBaubleSlots.isUnlocked(player, categoryKey)) {
            return false;
        }
        if (stack.isEmpty()) return false;
        if (!stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null))
            return false;

        IBauble ba = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        if (ba == null || !ba.canEquip(stack, player)) {
            return false;
        }

        if (legacyStrictValidation) {
            BaubleType itemType = ba.getBaubleType(stack);
            if (itemType != BaubleType.TRINKET && itemType != this.type) {
                return false;
            }
            return true;
        }

        // Let Baubles container decide final slot validity (critical for newer slot/type mechanics).
        return super.isItemValid(stack);
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

    public String getCategoryKey() {
        return categoryKey;
    }
}
