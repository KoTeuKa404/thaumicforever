package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

public class DoubleTableTileEntity extends TileEntity implements IInventory {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(7, ItemStack.EMPTY);
    private AspectList storedAspects = new AspectList(); 

    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(inventory, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return !this.isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public AspectList getStoredAspects() {
        return storedAspects;
    }

  
    public void updateAspectsFromInventory() {
        storedAspects = new AspectList();

        ItemStack paperStack = inventory.get(6);
        if (paperStack.isEmpty() || paperStack.getCount() <= 0) {
            return;
        }

        boolean[] isSlotUsed = new boolean[5];

        for (int i = 0; i < 5; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                AspectList itemAspects = AspectHelper.getObjectAspects(stack);
                if (itemAspects != null && itemAspects.size() > 0) {
                    for (Aspect aspect : itemAspects.getAspects()) {
                        // System.out.println(" - " + aspect.getName() + ": " + itemAspects.getAmount(aspect));
                        storedAspects.add(aspect, itemAspects.getAmount(aspect));
                    }
                }
            }
        }

        boolean hasMatchedAspects = processScrollDecryption(isSlotUsed);

        for (int i = 0; i < 5; i++) {
            if (!isSlotUsed[i] && !hasMatchedAspects) { 
                ItemStack stack = inventory.get(i);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                    if (stack.getCount() <= 0) {
                        inventory.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }

        paperStack.shrink(1);
        if (paperStack.getCount() <= 0) {
            inventory.set(6, ItemStack.EMPTY);
        }

        markDirty();
    }

 
    public boolean processScrollDecryption(boolean[] isSlotUsed) {
        ItemStack scroll = inventory.get(5); // Слот для скролу
        if (!scroll.isEmpty() && scroll.getItem() instanceof ItemScroll && "_o".equals(((ItemScroll) scroll.getItem()).getType())) {
            NBTTagCompound tag = scroll.getTagCompound();
            if (tag != null && tag.hasKey("aspects")) {
                NBTTagCompound aspectTag = tag.getCompoundTag("aspects");
                AspectList scrollAspects = new AspectList();
                for (String aspectKey : aspectTag.getKeySet()) {
                    Aspect aspect = Aspect.getAspect(aspectKey);
                    if (aspect != null) {
                        scrollAspects.add(aspect, aspectTag.getInteger(aspectKey));
                    }
                }
    
                // System.out.println("Аспекти у скролі перед обробкою:");
                // for (Aspect aspect : scrollAspects.getAspects()) {
                //     System.out.println(" - " + aspect.getName() + ": " + scrollAspects.getAmount(aspect));
                // }
    
                boolean aspectsMatched = false;
                for (Aspect aspect : scrollAspects.getAspects()) {
                    int scrollAmount = scrollAspects.getAmount(aspect);
                    if (scrollAmount > 0) {
                        for (int i = 0; i < 5; i++) {
                            ItemStack stack = inventory.get(i);
                            if (!stack.isEmpty()) {
                                AspectList itemAspects = AspectHelper.getObjectAspects(stack);
                                if (itemAspects != null && itemAspects.getAmount(aspect) > 0) {
                                    scrollAspects.reduce(aspect, 1);
    
                                    stack.shrink(1);
                                    if (stack.getCount() <= 0) {
                                        inventory.set(i, ItemStack.EMPTY);
                                    }
    
                                    isSlotUsed[i] = true;
    
                                    aspectsMatched = true;
                                    // System.out.println("Оброблено аспект: " + aspect.getName() + " | Використано: 1");
                                    break; 
                                }
                            }
                        }
                    }
                }
    
                NBTTagCompound updatedTag = new NBTTagCompound();
                boolean allAspectsDepleted = true;
                for (Aspect aspect : scrollAspects.getAspects()) {
                    int amount = scrollAspects.getAmount(aspect);
                    updatedTag.setInteger(aspect.getTag(), amount);
                    if (amount > 0) {
                        allAspectsDepleted = false;
                    }
                }
                tag.setTag("aspects", updatedTag);
                scroll.setTagCompound(tag);
    
                if (allAspectsDepleted && aspectsMatched) {
                    ItemStack completedScroll = new ItemStack(ModItems.SCROLL_C, 1, scroll.getMetadata());
                    completedScroll.setTagCompound(tag);
                    inventory.set(5, completedScroll);
                    // System.out.println("Скрол успішно дешифровано!");
                }
    
                // System.out.println("Оновлені аспекти у скролі:");
                for (Aspect aspect : scrollAspects.getAspects()) {
                    // System.out.println(" - " + aspect.getName() + ": " + scrollAspects.getAmount(aspect));
                }
    
                markDirty(); 
                return aspectsMatched;
            }
        }
        return false;
    }
    


    
    
     public void processScrollDecryption() {
        ItemStack scroll = inventory.get(5); 
        if (!scroll.isEmpty() && scroll.getItem() instanceof ItemScroll && "_o".equals(((ItemScroll) scroll.getItem()).getType())) {
            NBTTagCompound tag = scroll.getTagCompound();
            if (tag != null && tag.hasKey("aspects")) {
                NBTTagCompound aspectTag = tag.getCompoundTag("aspects");
                AspectList scrollAspects = new AspectList();
                for (String aspectKey : aspectTag.getKeySet()) {
                    Aspect aspect = Aspect.getAspect(aspectKey);
                    if (aspect != null) {
                        scrollAspects.add(aspect, aspectTag.getInteger(aspectKey));
                    }
                }
                // System.out.println("Aspects in the scroll before processing:");
                for (Aspect aspect : scrollAspects.getAspects()) {
                    // System.out.println(" - " + aspect.getName() + ": " + scrollAspects.getAmount(aspect));
                }
    
                boolean aspectReduced = false; 
    
                for (int i = 0; i < 5 && !aspectReduced; i++) {
                    ItemStack stack = inventory.get(i);
                    if (!stack.isEmpty()) {
                        AspectList itemAspects = AspectHelper.getObjectAspects(stack);
                        if (itemAspects != null) {
                            for (Aspect aspect : itemAspects.getAspects()) {
                                int scrollAmount = scrollAspects.getAmount(aspect);
                                int itemAmount = itemAspects.getAmount(aspect);
    
                                if (scrollAmount > 0 && itemAmount > 0) {
                                    scrollAspects.reduce(aspect, 1);
    
                                    stack.shrink(1);
                                    if (stack.getCount() <= 0) {
                                        inventory.set(i, ItemStack.EMPTY);
                                    }
    
                                    aspectReduced = true;
                                    // System.out.println("Reduced 1 aspect: " + aspect.getName());
                                    break; 
                                }
                            }
                        }
                    }
                }
    
                NBTTagCompound updatedTag = new NBTTagCompound();
                boolean allAspectsDepleted = true; // Перевіряємо, чи всі аспекти вичерпані
                for (Aspect aspect : scrollAspects.getAspects()) {
                    int amount = scrollAspects.getAmount(aspect);
                    updatedTag.setInteger(aspect.getTag(), amount);
                    if (amount > 0) {
                        allAspectsDepleted = false;
                    }
                }
                tag.setTag("aspects", updatedTag);
                scroll.setTagCompound(tag);
    
                if (allAspectsDepleted) {
                    ItemStack completedScroll = new ItemStack(ModItems.SCROLL_C, 1, scroll.getMetadata());
                    completedScroll.setTagCompound(tag);
                    inventory.set(5, completedScroll);
                    // System.out.println("Scroll successfully decrypted!");
                }
    
                // System.out.println("Updated aspects in the scroll:");
                for (Aspect aspect : scrollAspects.getAspects()) {
                    // System.out.println(" - " + aspect.getName() + ": " + scrollAspects.getAmount(aspect));
                }
    
                markDirty(); 
            }
        }
    }
    
    
    
    
    
    /**
     * Matches and reduces aspects between storedAspects and the scroll.
     *
     * @param scrollAspects The aspects present in the scroll.
     * @return true if any aspects were matched and reduced; false otherwise.
     */
    private boolean matchAndReduceAspects(AspectList scrollAspects) {
        boolean aspectsMatched = false;
    
        for (int i = 0; i < 5; i++) { // Перевіряємо слоти 0-4
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                AspectList itemAspects = AspectHelper.getObjectAspects(stack);
                if (itemAspects != null) {
                    // System.out.println("Аспекти у слоті " + i + ":");
                    for (Aspect aspect : itemAspects.getAspects()) {
                        // System.out.println(" - " + aspect.getName() + ": " + itemAspects.getAmount(aspect));
                    }
    
                    for (Aspect aspect : itemAspects.getAspects()) {
                        int scrollAmount = scrollAspects.getAmount(aspect);
    
                        if (scrollAmount > 0) { 
                            // System.out.println("Обробка аспекту: " + aspect.getName());
                            // System.out.println(" - Кількість у скролі до: " + scrollAmount);
    
                            scrollAspects.reduce(aspect, 1); 
                            stack.shrink(1);
                            if (stack.getCount() <= 0) {
                                inventory.set(i, ItemStack.EMPTY); 
                            }
    
                            aspectsMatched = true;
    
                            // System.out.println(" - Кількість у скролі після: " + scrollAspects.getAmount(aspect));
                            break; 
                        }
                    }
    
                    if (aspectsMatched) {
                        break;
                    }
                } else {
                    // System.out.println("Слот " + i + " не містить аспектів.");
                }
            } else {
                // System.out.println("Слот " + i + " порожній.");
            }
        }
    
        return aspectsMatched;
    }
    
    
    
    /**
     * Updates the scroll's NBT based on remaining aspects or completes it if all are matched.
     *
     * @param scroll         The ItemStack representing the scroll.
     * @param scrollAspects  The aspects present in the scroll after processing.
     * @param aspectsMatched Indicates whether any aspects were matched.
     * @param tag            The NBT tag of the scroll.
     */
    private void updateScrollNBT(ItemStack scroll, AspectList scrollAspects, boolean aspectsMatched, NBTTagCompound tag) {
        // System.out.println("==== ОНОВЛЕННЯ СКРОЛУ ====");
    
        if (aspectsMatched) {
            if (scrollAspects.size() > 0) {
                // System.out.println("Залишкові аспекти у скролі:");
                for (Aspect aspect : scrollAspects.getAspects()) {
                    // System.out.println(" - " + aspect.getName() + ": " + scrollAspects.getAmount(aspect));
                }
    
                NBTTagCompound updatedTag = new NBTTagCompound();
                scrollAspects.writeToNBT(updatedTag);
                tag.setTag("aspects", updatedTag); // Зберігаємо залишкові аспекти
                scroll.setTagCompound(tag);
    
            } else {
                // System.out.println("Всі аспекти витрачені. Перетворення скролу.");
                ItemStack completedScroll = new ItemStack(ModItems.SCROLL_C, 1, scroll.getMetadata());
                completedScroll.setTagCompound(tag);
                inventory.set(5, completedScroll);
            }
        } else {
            // System.out.println("Жоден аспект не збігся. Скрол не змінено.");
        }
    
        // System.out.println("==== КІНЕЦЬ ОНОВЛЕННЯ СКРОЛУ ====");
        markDirty(); 
    }
    
    
    
    
    
    
    

    public void setStoredAspects(AspectList aspects) {
    this.storedAspects = aspects;
    markDirty(); 
    }

    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        storedAspects.writeToNBT(compound, "StoredAspects");
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, inventory);
        storedAspects.readFromNBT(compound, "StoredAspects");
    }

    @Override
    public String getName() {
        return "container.double_table";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }
}
