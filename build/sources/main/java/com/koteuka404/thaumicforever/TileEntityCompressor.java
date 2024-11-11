package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityCompressor extends TileEntity implements ITickable {

    private ItemStackHandler inventory = new ItemStackHandler(2); // Вхід і вихід
    private int compressTime = 0;
    private ItemStack selectedPlate = ItemStack.EMPTY;  // Вибрана пластина
    private ItemStack lastInput = ItemStack.EMPTY;  // Останній вхідний матеріал

    @Override
    public void update() {
        if (!world.isRemote) {
            ItemStack input = inventory.getStackInSlot(0);   // Вхідний слот
            ItemStack output = inventory.getStackInSlot(1);  // Вихідний слот

            // Перевірка, чи змінився вхідний матеріал
            if (!ItemStack.areItemsEqual(input, lastInput)) {
                // Якщо вхідний матеріал змінився, скидаємо вибір пластини
                selectedPlate = ItemStack.EMPTY;
                lastInput = input.copy();
                markDirty();
            }

            // Додаємо перевірку на вибір пластини або виключення для Thauminite
            if (!input.isEmpty() && (isValidInput(input) && !selectedPlate.isEmpty() || isThauminite(input))) {
                if (!output.isEmpty() && !isSameMaterial(selectedPlate, output)) {
                    return; // Якщо вихідний слот заповнений іншим матеріалом, нічого не робимо
                }

                compressTime++;

                if (compressTime >= 50) { // 5 секунд
                    compressItem(input);
                    compressTime = 0;
                }
            } else {
                compressTime = 0; // Скидаємо час, якщо умови не виконуються
            }
        }
    }

    // Метод для компресії предмета
    // Метод для компресії предмета
    private void compressItem(ItemStack input) {
        if (!selectedPlate.isEmpty() || isThauminite(input)) {
            // Якщо це Thauminite Ingot, створюємо Thauminite Plate без перевірки вибору пластини
            ItemStack output = inventory.getStackInSlot(1);
            ItemStack plateToCreate = selectedPlate;

            if (isThauminite(input)) {
                plateToCreate = new ItemStack(Item.getByNameOrId("thaumicbases:thauminite_plate"));
            }

            // Якщо у вихідному слоті вже є пластина цього типу, збільшуємо її кількість
            if (!output.isEmpty() && output.isItemEqual(plateToCreate)) {
                output.grow(1); // Збільшуємо кількість існуючої пластини
                inventory.setStackInSlot(1, output); // Оновлюємо слот з оновленою кількістю
            } else if (output.isEmpty()) {
                // Якщо слот порожній, вставляємо нову пластину
                inventory.setStackInSlot(1, plateToCreate.copy());
            }

            inventory.extractItem(0, 1, false); // Видаляємо один вхідний предмет
            markDirty(); // Оновлюємо TileEntity
        }
    }

    // Метод для перевірки, чи це Thauminite Ingot
    private boolean isThauminite(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        String itemName = stack.getItem().getRegistryName().toString();
        return itemName.equals("thaumicbases:thauminite_ingot") || itemName.equals("thaumicbases:thauminite_plate");
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("container.compressor");
    }

    // Метод для перевірки, чи є предмет допустимим вхідним матеріалом (злиток)
    public static boolean isValidInput(ItemStack stack) {
        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int id : oreIds) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.startsWith("ingot")) { // Перевіряємо, чи це злиток
                return true;
            }
        }
        return false;
    }

    // Перевірка, чи матеріали однакові
    private boolean isSameMaterial(ItemStack input, ItemStack output) {
        // Додана перевірка на валідність предметів
        if (input == null || output == null || input.isEmpty() || output.isEmpty()) {
            return false;
        }
    
        // Спеціальна обробка для Thauminite
        if (isThauminite(input) && isThauminite(output)) {
            return true; // Вважаємо, що Thauminite Ingot і Thauminite Plate мають один і той же матеріал
        }
    
        int[] inputOreIds = OreDictionary.getOreIDs(input);
        int[] outputOreIds = OreDictionary.getOreIDs(output);
    
        for (int inputId : inputOreIds) {
            for (int outputId : outputOreIds) {
                if (inputId == outputId) {
                    return true; // Якщо вхідний і вихідний матеріал однакові
                }
            }
        }
        return false; // Якщо матеріали різні
    }
    

    // Отримуємо варіанти пластин на основі вхідного матеріалу
    public List<ItemStack> getPlateOptions() {
        ItemStack input = inventory.getStackInSlot(0);  // Вхідний слот
        List<ItemStack> plateOptions = new ArrayList<>();

        if (!input.isEmpty()) {
            int[] oreIds = OreDictionary.getOreIDs(input);
            for (int id : oreIds) {
                String oreName = OreDictionary.getOreName(id);
                if (oreName.startsWith("ingot")) {
                    String plateName = "plate" + oreName.substring(5);
                    plateOptions.addAll(OreDictionary.getOres(plateName));  // Додаємо всі варіанти пластин
                }
            }
        }

        return plateOptions;
    }

    // Вибір пластини
    public void setSelectedPlate(ItemStack plate) {
        this.selectedPlate = plate;
        System.out.println("Plate selected: " + plate); // Додаткове логування
        markDirty(); // Синхронізуємо після вибору
    }

    public ItemStack getSelectedPlate() {
        return this.selectedPlate;
    }

    // Метод для збереження стану через NBT
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        // Збереження інвентаря
        compound.setTag("Inventory", inventory.serializeNBT());

        if (!selectedPlate.isEmpty()) {
            NBTTagCompound selectedPlateTag = new NBTTagCompound();
            selectedPlate.writeToNBT(selectedPlateTag);
            compound.setTag("SelectedPlate", selectedPlateTag);
        }

        if (!lastInput.isEmpty()) {
            NBTTagCompound lastInputTag = new NBTTagCompound();
            lastInput.writeToNBT(lastInputTag);
            compound.setTag("LastInput", lastInputTag);
        }

        return compound;
    }

    // Метод для завантаження стану з NBT
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // Завантаження інвентаря
        if (compound.hasKey("Inventory")) {
            inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        }

        if (compound.hasKey("SelectedPlate")) {
            NBTTagCompound selectedPlateTag = compound.getCompoundTag("SelectedPlate");
            selectedPlate = new ItemStack(selectedPlateTag);
        }

        if (compound.hasKey("LastInput")) {
            NBTTagCompound lastInputTag = compound.getCompoundTag("LastInput");
            lastInput = new ItemStack(lastInputTag);
        }

        markDirty(); // Оновлюємо стан після завантаження
    }

    // Метод для доступу до інвентаря компресора
    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    // Перевірка чи може гравець взаємодіяти з компресором
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (world.getTileEntity(pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
        }
    }
}
