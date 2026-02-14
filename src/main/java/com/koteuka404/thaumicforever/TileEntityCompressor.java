package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileEntityCompressor extends TileEntity implements ITickable, IEssentiaTransport {

    private final ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isValidInput(stack) || isThauminite(stack);
        }
    };

    private final ItemStackHandler outputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }
    };

    private int compressTime = 0;
    private ItemStack selectedPlate = ItemStack.EMPTY;
    private ItemStack lastInput = ItemStack.EMPTY;

    private int mechanismEssentia = 0;
    private static final int MAX_MECHANISM_ESSENTIA = 100;
    private static final int ESSENTIA_COST_PER_PLATE = 10;

    @Override
    public void update() {
        if (!world.isRemote) {
            fillWithEssentia();

            ItemStack input  = inputHandler.getStackInSlot(0);
            ItemStack output = outputHandler.getStackInSlot(0);

            if (!ItemStack.areItemsEqual(input, lastInput)) {
                lastInput = input.isEmpty() ? ItemStack.EMPTY : input.copy();

                if (!input.isEmpty() && !selectedPlate.isEmpty()) {
                    if (!isSelectedPlateCompatibleWithInput(input)) {
                        selectedPlate = ItemStack.EMPTY;
                    }
                }

                markDirty();
            }

            if (input.isEmpty()) {
                compressTime = 0;
                return;
            }

            boolean isThaumIngot  = isThauminite(input);
            boolean isNormalIngot =
                    isValidInput(input)
                    && !selectedPlate.isEmpty()
                    && isSelectedPlateCompatibleWithInput(input);

            if (!(isNormalIngot || isThaumIngot)) {
                compressTime = 0;
                return;
            }

            if (!output.isEmpty()) {
                ItemStack expected = isThaumIngot
                        ? new ItemStack(Item.getByNameOrId("thaumicbases:thauminite_plate"))
                        : selectedPlate;

                if (!output.isItemEqual(expected)) {
                    return;
                }
            }

            int requiredTime = hasEnoughEssentia() ? 20 : 80;
            compressTime++;

            if (compressTime >= requiredTime) {
                compressItem(input);
                compressTime = 0;
                if (hasEnoughEssentia()) {
                    consumeEssentia();
                }
            }
        }
    }

    private void compressItem(ItemStack input) {
        if (!selectedPlate.isEmpty() || isThauminite(input)) {
            ItemStack output = outputHandler.getStackInSlot(0);
            ItemStack plateToCreate = selectedPlate;

            if (isThauminite(input)) {
                plateToCreate = new ItemStack(Item.getByNameOrId("thaumicbases:thauminite_plate"));
            }

            if (!output.isEmpty() && output.isItemEqual(plateToCreate)) {
                output.grow(1);
                outputHandler.setStackInSlot(0, output);
            } else if (output.isEmpty()) {
                outputHandler.setStackInSlot(0, plateToCreate.copy());
            }

            inputHandler.extractItem(0, 1, false);
            markDirty();
            if (!world.isRemote) {
                world.playSound(
                        null,
                        pos,
                        SoundEvents.BLOCK_ANVIL_USE,
                        SoundCategory.BLOCKS,
                        0.5F,
                        1.0F
                );
            }
        }
    }

    private void fillWithEssentia() {
        if (mechanismEssentia >= MAX_MECHANISM_ESSENTIA) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport transport = (IEssentiaTransport) te;
                if (transport.canOutputTo(facing.getOpposite())) {
                    Aspect essentiaType = transport.getEssentiaType(facing.getOpposite());
                    if (essentiaType == Aspect.MECHANISM) {
                        int taken = transport.takeEssentia(essentiaType, 1, facing.getOpposite());
                        mechanismEssentia += taken;
                        if (mechanismEssentia >= MAX_MECHANISM_ESSENTIA) {
                            mechanismEssentia = MAX_MECHANISM_ESSENTIA;
                            break;
                        }
                    }
                }
            }
        }
        markDirty();
    }

    private static boolean isThauminite(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        String itemName = stack.getItem().getRegistryName().toString();
        return itemName.equals("thaumicbases:thauminite_ingot")
                || itemName.equals("thaumicbases:thauminite_plate");
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("container.compressor");
    }

    public static boolean isValidInput(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int id : oreIds) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.startsWith("ingot")) {
                return true;
            }
        }
        return false;
    }

    private List<ItemStack> getPlateOptionsFor(ItemStack input) {
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

    public List<ItemStack> getPlateOptions() {
        return getPlateOptionsFor(inputHandler.getStackInSlot(0));
    }

    private boolean isSelectedPlateCompatibleWithInput(ItemStack input) {
        if (selectedPlate.isEmpty() || input.isEmpty()) return false;

        for (ItemStack opt : getPlateOptionsFor(input)) {
            if (OreDictionary.itemMatches(opt, selectedPlate, false)) {
                return true;
            }
        }
        return false;
    }

    public void setSelectedPlate(ItemStack plate) {
        this.selectedPlate = plate.copy();
        markDirty();
    }

    public ItemStack getSelectedPlate() {
        return this.selectedPlate;
    }

    private boolean hasEnoughEssentia() {
        return mechanismEssentia >= ESSENTIA_COST_PER_PLATE;
    }

    private void consumeEssentia() {
        mechanismEssentia -= ESSENTIA_COST_PER_PLATE;
        if (mechanismEssentia < 0) {
            mechanismEssentia = 0;
        }
        markDirty();
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (aspect == Aspect.MECHANISM) {
            int toAdd = Math.min(amount, MAX_MECHANISM_ESSENTIA - mechanismEssentia);
            mechanismEssentia += toAdd;
            markDirty();
            return toAdd;
        }
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return mechanismEssentia;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return Aspect.MECHANISM;
    }

    @Override
    public int getMinimumSuction() {
        return 128;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return mechanismEssentia < MAX_MECHANISM_ESSENTIA ? 128 : 0;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return Aspect.MECHANISM;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("InputHandler", inputHandler.serializeNBT());
        compound.setTag("OutputHandler", outputHandler.serializeNBT());

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

        compound.setInteger("MechanismEssentia", mechanismEssentia);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("InputHandler")) {
            inputHandler.deserializeNBT(compound.getCompoundTag("InputHandler"));
        }

        if (compound.hasKey("OutputHandler")) {
            outputHandler.deserializeNBT(compound.getCompoundTag("OutputHandler"));
        }

        if (compound.hasKey("SelectedPlate")) {
            NBTTagCompound selectedPlateTag = compound.getCompoundTag("SelectedPlate");
            selectedPlate = new ItemStack(selectedPlateTag);
        } else {
            selectedPlate = ItemStack.EMPTY;
        }

        if (compound.hasKey("LastInput")) {
            NBTTagCompound lastInputTag = compound.getCompoundTag("LastInput");
            lastInput = new ItemStack(lastInputTag);
        } else {
            lastInput = ItemStack.EMPTY;
        }

        this.mechanismEssentia = compound.getInteger("MechanismEssentia");
        markDirty();
    }

    public ItemStackHandler getInventory() {
        return inputHandler;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        if (world.getTileEntity(pos) != this) {
            return false;
        } else {
            return player.getDistanceSq(
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + 0.5D,
                    (double) pos.getZ() + 0.5D
            ) <= 64.0D;
        }
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getSidedHandler(facing));
        }
        return super.getCapability(capability, facing);
    }

    private IItemHandler getSidedHandler(EnumFacing facing) {
        if (facing == EnumFacing.DOWN) {
            return outputHandler;
        } else {
            return inputHandler;
        }
    }

    public ItemStackHandler getInputHandler() {
        return inputHandler;
    }

    public ItemStackHandler getOutputHandler() {
        return outputHandler;
    }
}
