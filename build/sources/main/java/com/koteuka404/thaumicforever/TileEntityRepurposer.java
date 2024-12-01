package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityRepurposer extends TileEntity implements ITickable {

    private ItemStackHandler inventory = new ItemStackHandler(2); 
    
    @Override
    public void update() {
        if (!world.isRemote) {
            System.out.println("Виконується метод update()");
            handleJewelryTransformation();
        }
    }

    private void handleJewelryTransformation() {
        ItemStack leftSlot = inventory.getStackInSlot(0);  // Лівий слот
        ItemStack rightSlot = inventory.getStackInSlot(1); // Правий слот

        if (!leftSlot.isEmpty() && !rightSlot.isEmpty() && isJewelryValid(rightSlot)) {
            System.out.println("Починаємо трансформацію біжутерії...");
            System.out.println("Лівий слот: " + leftSlot.getDisplayName() + ", Правий слот: " + rightSlot.getDisplayName());

            // Міняємо тип предмета у правому слоті на той, що в лівому
            Item rightItem = rightSlot.getItem();
            Item leftItem = leftSlot.getItem();

            if (rightItem instanceof IBauble && leftItem instanceof IBauble) {
                if (leftItem instanceof MutableBaubleItem) {
                    BaubleType newType = ((IBauble) leftItem).getBaubleType(leftSlot);
                    System.out.println("Новий тип біжутерії: " + newType);

                    if (rightItem instanceof MutableBaubleItem) {
                        MutableBaubleItem mutableBauble = (MutableBaubleItem) rightItem;
                        mutableBauble.setBaubleType(rightSlot, newType);
                        System.out.println("Змінено тип Bauble на: " + newType);

                        // Оновлюємо предмет у правому слоті після зміни типу
                        inventory.setStackInSlot(1, rightSlot);
                        markDirty(); // Оновлюємо TileEntity для збереження змін

                        // Вилучаємо предмет з лівого слоту
                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                        System.out.println("Предмет у лівому слоті вилучено.");
                    } else {
                        System.out.println("Предмет у правому слоті не підтримує зміну типу");
                    }
                } else {
                    System.out.println("Предмет у лівому слоті не підтримує зміну типу");
                }
            } else {
                System.out.println("Один із предметів не є біжутерією");
            }
        } else {
            System.out.println("Слоти не підходять для трансформації або є порожніми");
        }
    }

    private boolean isJewelryValid(ItemStack stack) {
        if (stack.getItem() instanceof IBauble) {
            System.out.println("Предмет " + stack.getDisplayName() + " є дійсною біжутерією.");
            return true;
        } else {
            System.out.println("Предмет " + stack.getDisplayName() + " не є біжутерією.");
            return false;
        }
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }
    
    public boolean isUsableByPlayer(EntityPlayer playerIn) {
        return this.world.getTileEntity(this.pos) == this && playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }
}

// Новий клас, що реалізує IBauble і дозволяє змінювати тип BaubleType
class MutableBaubleItem extends Item implements IBauble {
    private BaubleType baubleType;

    public MutableBaubleItem(BaubleType defaultType) {
        this.baubleType = defaultType;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return this.baubleType;
    }

    public void setBaubleType(ItemStack itemstack, BaubleType newType) {
        this.baubleType = newType;
        System.out.println("Тип Bauble змінено на: " + newType);
    }
}
