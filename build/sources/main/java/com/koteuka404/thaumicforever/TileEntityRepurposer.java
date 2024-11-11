package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityRepurposer extends TileEntity implements ITickable {

    private ItemStackHandler inventory = new ItemStackHandler(2); // 0 - лівий слот, 1 - правий слот

    @Override
    public void update() {
        if (!world.isRemote) {
            handleJewelryTransformation();
        }
    }

    private void handleJewelryTransformation() {
        ItemStack leftSlot = inventory.getStackInSlot(0);  // Лівий слот
        ItemStack rightSlot = inventory.getStackInSlot(1); // Правий слот
    
        // Перевірка, чи обидва слоти заповнені і чи предмет у правому слоті є біжутерією
        if (!leftSlot.isEmpty() && !rightSlot.isEmpty() && isJewelryValid(rightSlot)) {
            System.out.println("Лівий слот до трансформації: " + leftSlot);
            System.out.println("Правий слот до трансформації: " + rightSlot);
    
            // Створюємо новий ItemStack на основі предмета з правого слота
            ItemStack transformedJewelry = new ItemStack(rightSlot.getItem(), rightSlot.getCount());
    
            // Копіюємо оригінальні NBT-теги з правого предмета, якщо вони є
            if (rightSlot.hasTagCompound()) {
                transformedJewelry.setTagCompound(rightSlot.getTagCompound().copy());
            }
    
            // Додаємо/об'єднуємо специфічні NBT-теги з лівого предмета
            if (leftSlot.hasTagCompound()) {
                if (transformedJewelry.hasTagCompound()) {
                    transformedJewelry.getTagCompound().merge(leftSlot.getTagCompound());
                } else {
                    transformedJewelry.setTagCompound(leftSlot.getTagCompound().copy());
                }
    
                // Зміна типу біжутерії правого слота на основі лівого
                String leftType = getBaubleTypeFrom(leftSlot);
                transformedJewelry.getTagCompound().setString("BaubleType", leftType);
                System.out.println("Тип біжутерії змінено на: " + leftType);
            }
    
            System.out.println("Створено новий предмет: " + transformedJewelry);
    
            // Вставляємо новий предмет у правий слот
            inventory.setStackInSlot(1, transformedJewelry);
            System.out.println("Правий слот після трансформації: " + inventory.getStackInSlot(1));
    
            // Зменшуємо кількість предметів у лівому слоті на 1, щоб він зникав
            leftSlot.shrink(1);
            if (leftSlot.getCount() <= 0) {
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            }
    
            markDirty(); // Оновлення стану TileEntity
            System.out.println("Оновлено стан TileEntity.");
        }
    }
    
    
    
    
    
    
    
    

    
    
    // Метод для отримання типу біжутерії з лівого слота
    private String getBaubleTypeFrom(ItemStack stack) {
        // Це приклад, змініть цей метод відповідно до ваших потреб, якщо потрібно отримувати тип із NBT
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BaubleType")) {
            return stack.getTagCompound().getString("BaubleType");
        }
        return "unknown"; // Значення за замовчуванням, якщо тип не знайдено
    }
    


    
    

    private boolean isJewelryValid(ItemStack stack) {
        // Перевірка, чи є предмет одним з типів біжутерії
        return stack.getDisplayName().toLowerCase().contains("ring") ||
               stack.getDisplayName().toLowerCase().contains("amulet") ||
               stack.getDisplayName().toLowerCase().contains("head") ||
               stack.getDisplayName().toLowerCase().contains("belt") ||
               stack.getDisplayName().toLowerCase().contains("body") ||
               stack.getDisplayName().toLowerCase().contains("charm");
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }
    public boolean isUsableByPlayer(EntityPlayer player) {
    // Перевіряє, чи гравець знаходиться на допустимій відстані для взаємодії з TileEntity
    if (this.world.getTileEntity(this.pos) != this) {
        return false;
    } else {
        return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }
}

}
