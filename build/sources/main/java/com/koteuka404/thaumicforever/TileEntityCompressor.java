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

    private ItemStackHandler inventory = new ItemStackHandler(2); 
    private int compressTime = 0;
    private ItemStack selectedPlate = ItemStack.EMPTY;  
    private ItemStack lastInput = ItemStack.EMPTY;  

    @Override
    public void update() {
        if (!world.isRemote) {
            ItemStack input = inventory.getStackInSlot(0);  
            ItemStack output = inventory.getStackInSlot(1);  

            if (!ItemStack.areItemsEqual(input, lastInput)) {
                selectedPlate = ItemStack.EMPTY;
                lastInput = input.copy();
                markDirty();
            }

            if (!input.isEmpty() && (isValidInput(input) && !selectedPlate.isEmpty() || isThauminite(input))) {
                if (!output.isEmpty() && !isSameMaterial(selectedPlate, output)) {
                    return; 
                }

                compressTime++;

                if (compressTime >= 50) { 
                    compressItem(input);
                    compressTime = 0;
                }
            } else {
                compressTime = 0;
            }
        }
    }

    private void compressItem(ItemStack input) {
        if (!selectedPlate.isEmpty() || isThauminite(input)) {
            ItemStack output = inventory.getStackInSlot(1);
            ItemStack plateToCreate = selectedPlate;

            if (isThauminite(input)) {
                plateToCreate = new ItemStack(Item.getByNameOrId("thaumicbases:thauminite_plate"));
            }

            if (!output.isEmpty() && output.isItemEqual(plateToCreate)) {
                output.grow(1); 
                inventory.setStackInSlot(1, output); 
            } else if (output.isEmpty()) {
                inventory.setStackInSlot(1, plateToCreate.copy());
            }

            inventory.extractItem(0, 1, false); 
            markDirty(); 
        }
    }

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

    public static boolean isValidInput(ItemStack stack) {
        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int id : oreIds) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.startsWith("ingot")) { 
                return true;
            }
        }
        return false;
    }

    private boolean isSameMaterial(ItemStack input, ItemStack output) {
        if (input == null || output == null || input.isEmpty() || output.isEmpty()) {
            return false;
        }
    
        if (isThauminite(input) && isThauminite(output)) {
            return true; 
        }
    
        int[] inputOreIds = OreDictionary.getOreIDs(input);
        int[] outputOreIds = OreDictionary.getOreIDs(output);
    
        for (int inputId : inputOreIds) {
            for (int outputId : outputOreIds) {
                if (inputId == outputId) {
                    return true; 
                }
            }
        }
        return false; 
    }
    

    public List<ItemStack> getPlateOptions() {
        ItemStack input = inventory.getStackInSlot(0); 
        List<ItemStack> plateOptions = new ArrayList<>();

        if (!input.isEmpty()) {
            int[] oreIds = OreDictionary.getOreIDs(input);
            for (int id : oreIds) {
                String oreName = OreDictionary.getOreName(id);
                if (oreName.startsWith("ingot")) {
                    String plateName = "plate" + oreName.substring(5);
                    plateOptions.addAll(OreDictionary.getOres(plateName));  
                }
            }
        }

        return plateOptions;
    }

    public void setSelectedPlate(ItemStack plate) {
        this.selectedPlate = plate;
        markDirty(); 
    }

    public ItemStack getSelectedPlate() {
        return this.selectedPlate;
    }

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

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

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

        markDirty(); 
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        if (world.getTileEntity(pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
        }
    }
}
