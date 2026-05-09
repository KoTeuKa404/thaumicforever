package com.koteuka404.thaumicforever.inventory;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import com.koteuka404.thaumicforever.item.ItemPotionGun;

public class InventoryPotionGun extends InventoryBasic {

    private static final String NBT_ITEMS = "PotionGunItems";
    private final ItemStack gunStack;
    private final EntityPlayer player;
    private final int handId;
    private boolean internalUpdate;

    public InventoryPotionGun(ItemStack stack) {
        this(stack, null, -1);
    }

    public InventoryPotionGun(ItemStack stack, EntityPlayer player, int handId) {
        super("container.potion_gun", false, 1);
        this.gunStack = Objects.requireNonNull(stack, "gunStack");
        this.player = player;
        this.handId = handId;
        readFromNBT();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return !gunStack.isEmpty();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return ItemPotionGun.isValidAmmo(stack);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        writeToNBT();
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0 && !internalUpdate) {
            ItemStack processed = ItemPotionGun.processInputSlot(getLiveGunStack(), stack);
            internalUpdate = true;
            super.setInventorySlotContents(index, processed);
            internalUpdate = false;
            return;
        }
        super.setInventorySlotContents(index, stack);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        super.closeInventory(player);
        writeToNBT();
    }

    private void readFromNBT() {
        if (!gunStack.hasTagCompound()) return;
        NBTTagCompound tag = gunStack.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_ITEMS)) return;

        NonNullList<ItemStack> items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, items);
        internalUpdate = true;
        super.setInventorySlotContents(0, items.get(0));
        internalUpdate = false;
    }

    private void writeToNBT() {
        ItemStack target = getLiveGunStack();
        if (target.isEmpty()) return;

        NBTTagCompound tag = target.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();

        NonNullList<ItemStack> items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        items.set(0, getStackInSlot(0));
        ItemStackHelper.saveAllItems(tag, items);
        target.setTagCompound(tag);
    }

    private ItemStack getLiveGunStack() {
        if (player != null) {
            ItemStack held = handId == 1 ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
            if (!held.isEmpty() && held.getItem() instanceof ItemPotionGun) {
                return held;
            }
        }
        return gunStack;
    }

    public ItemStack getGunStack() {
        return getLiveGunStack();
    }
}
