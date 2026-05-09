package com.koteuka404.thaumicforever.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import com.koteuka404.thaumicforever.item.ItemKnowledgeFragment;

public class DeconstructionTableTileEntity extends TileEntity implements ITickable {

    private final ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }
    
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
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
    };

    public int burnTime = 0;

    public DeconstructionTableTileEntity() {
    }


    public java.util.Map<Item, ItemStack[]> getRecipes() {
        return java.util.Collections.emptyMap();
    }
    
    public java.util.Map<Item, Float> getChances() {
        return java.util.Collections.emptyMap();
    }
    
    public boolean isOldResearchLoaded() {
        return true;
    }

    
    @Override
    public void update() {
        if (!world.isRemote && !inputHandler.getStackInSlot(0).isEmpty()) {
            burnTime++;

            if (burnTime >= 80) {
                ItemStack inputStack = inputHandler.getStackInSlot(0);
                ItemStack result = getResult(inputStack);
                if (!result.isEmpty()) {
                    ItemStack outputStack = outputHandler.getStackInSlot(0);

                    if (outputStack.isEmpty()) {
                        outputHandler.setStackInSlot(0, result);
                    } else if (canStackResult(outputStack, result)) {
                        outputStack.grow(1);
                    } else {
                        burnTime = 0;
                        return;
                    }

                    inputStack.shrink(1);
                    burnTime = 0;
                    markDirty();
                } else {
                    burnTime = 0;
                }
            }
        }
    }

    public boolean isProcessing() {
        return burnTime > 0;
    }

    private ItemStack getResult(ItemStack input) {
        AspectList aspects = getInputAspects(input);
        if (aspects == null || aspects.size() <= 0) {
            return ItemStack.EMPTY;
        }
        return ItemKnowledgeFragment.create(aspects);
    }

    private static AspectList getInputAspects(ItemStack input) {
        if (input == null || input.isEmpty()) return null;
        return AspectHelper.getObjectAspects(input.copy());
    }

    private static boolean canStackResult(ItemStack existing, ItemStack result) {
        return !existing.isEmpty()
                && existing.getItem() == result.getItem()
                && existing.getItemDamage() == result.getItemDamage()
                && ItemStack.areItemStackTagsEqual(existing, result)
                && existing.getCount() < existing.getMaxStackSize();
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

        this.burnTime = compound.getInteger("BurnTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setTag("InputHandler", inputHandler.serializeNBT());
        compound.setTag("OutputHandler", outputHandler.serializeNBT());
        compound.setInteger("BurnTime", this.burnTime);

        return compound;
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
            if (facing == EnumFacing.DOWN) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(outputHandler);
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inputHandler);
            }
        }
        return super.getCapability(capability, facing);
    }

    public ItemStackHandler getInputHandler() {
        return inputHandler;
    }

    public ItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this &&
            player.getDistanceSq((double) this.pos.getX() + 0.5D,
                                 (double) this.pos.getY() + 0.5D,
                                 (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public static boolean isValidInput(ItemStack stack) {
        AspectList aspects = getInputAspects(stack);
        return aspects != null && aspects.size() > 0;
    }
}
