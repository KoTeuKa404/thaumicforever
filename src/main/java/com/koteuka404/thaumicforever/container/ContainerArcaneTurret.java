package com.koteuka404.thaumicforever.container;

import com.koteuka404.thaumicforever.entity.EntityArcaneTurret;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerArcaneTurret extends Container {
    private static final int LENS_SLOT = 0;
    private static final int PLAYER_INV_START = 1;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final EntityArcaneTurret turret;

    public ContainerArcaneTurret(EntityArcaneTurret turret) {
        this(resolvePlayer(turret), turret);
    }

    public ContainerArcaneTurret(EntityPlayer player, EntityArcaneTurret turret) {
        this.turret = turret;
        this.addSlotToContainer(new SlotItemHandler(turret.getLensHandler(), LENS_SLOT, 42, 29));

        if (player != null) {
            this.addPlayerInventory(player.inventory);
        }
    }

    private static EntityPlayer resolvePlayer(EntityArcaneTurret turret) {
        if (turret == null || turret.world == null) {
            return null;
        }
        return turret.world.getClosestPlayer(turret.posX, turret.posY, turret.posZ, 8.0D, false);
    }

    private void addPlayerInventory(InventoryPlayer playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.turret != null && !this.turret.isDead && playerIn.getDistanceSq(this.turret) <= 64.0D;
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (this.turret == null || this.turret.isDead) {
            return false;
        }
        if (id == 1) {
            this.turret.setTargetAnimal(!this.turret.getTargetAnimal());
            return true;
        }
        if (id == 2) {
            this.turret.setTargetMob(!this.turret.getTargetMob());
            return true;
        }
        if (id == 3) {
            this.turret.setTargetPlayer(!this.turret.getTargetPlayer());
            return true;
        }
        if (id == 4) {
            this.turret.setTargetFriendly(!this.turret.getTargetFriendly());
            return true;
        }
        if (id == 5) {
            this.turret.setForwardFire(!this.turret.getForwardFire());
            return true;
        }
        return super.enchantItem(playerIn, id);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (index == LENS_SLOT) {
                if (!this.mergeItemStack(stack, PLAYER_INV_START, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.inventorySlots.get(LENS_SLOT).isItemValid(stack)) {
                if (!this.mergeItemStack(stack, LENS_SLOT, LENS_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= PLAYER_INV_START && index < PLAYER_INV_END) {
                if (!this.mergeItemStack(stack, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= HOTBAR_START && index < HOTBAR_END) {
                if (!this.mergeItemStack(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
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

            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return result;
    }
}
