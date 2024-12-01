package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.crafting.ContainerDummy;

public class TileEntityMatteryDuplicator extends TileEntity implements IInventory, ITickable, IAspectContainer, IEssentiaTransport {

    private ItemStack[] inventory = new ItemStack[10];
    private String customName;
    private InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerDummy(), 3, 3);

    private AspectList essentia = new AspectList();
    private static final Aspect REQUIRED_ESSENTIA = AspectRegistry.MATTERYA;
    private static final int ESSENTIA_COST = 100;
    private static final int MAX_ESSENTIA_AMOUNT = 500;
    private int essentiaAmount = 0;
    private boolean inventoryChanged = false;
    public TileEntityMatteryDuplicator() {
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = ItemStack.EMPTY;
        }
    }

    private ItemStack previousResult = ItemStack.EMPTY;

    @Override
    public void update() {
        if (!world.isRemote) {
            fillWithEssentia();
    
            ItemStack result = previousResult;
            if (inventoryChanged) {
                result = createCraftingResult();
                previousResult = result; 
                inventoryChanged = false;
            }
    
            ItemStack currentResult = getStackInSlot(9);
    
            if (!result.isEmpty() && hasEnoughEssentia()) {
                if (currentResult.isEmpty()) {
                    setInventorySlotContents(9, result.copy()); 
                  
                }
            } else {
                if (!currentResult.isEmpty()) {
                    setInventorySlotContents(9, ItemStack.EMPTY);
                }
            }
    
            if (inventoryChanged || (!result.isEmpty() && currentResult.isEmpty())) {
                markForUpdate();
            }
        }
    }
    

    public void consumeEssentia() {
        if (essentiaAmount >= ESSENTIA_COST) {
            essentiaAmount -= ESSENTIA_COST;
            if (essentiaAmount < 0) {
                essentiaAmount = 0;
            }
            markForUpdate();
        }
    }

    private void fillWithEssentia() {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport transport = (IEssentiaTransport) te;
                if (transport.canOutputTo(facing.getOpposite()) && essentiaAmount < MAX_ESSENTIA_AMOUNT) {
                    Aspect essentiaType = transport.getEssentiaType(facing.getOpposite());
                    if (essentiaType != null && essentiaType == REQUIRED_ESSENTIA) {
                        int taken = transport.takeEssentia(essentiaType, 1, facing.getOpposite());
                        if (taken > 0) {
                            essentiaAmount += taken;
                            if (essentiaAmount > MAX_ESSENTIA_AMOUNT) {
                                essentiaAmount = MAX_ESSENTIA_AMOUNT;
                            }
                            markForUpdate();
                        }
                    }
                }
            }
        }
    }

    public boolean hasEnoughEssentia() {
        return essentiaAmount >= ESSENTIA_COST;
    }


    private ItemStack createCraftingResult() {
        for (int i = 0; i < 9; i++) {
            if (inventory[i] == null) {
                inventory[i] = ItemStack.EMPTY;
            }
            craftMatrix.setInventorySlotContents(i, inventory[i]);
        }
    
        IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix, world);
        if (recipe != null && recipe.matches(craftMatrix, world)) {
            return recipe.getCraftingResult(craftMatrix);
        }
    
        return ItemStack.EMPTY;
    }
    
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack != null ? stack : ItemStack.EMPTY;
        inventoryChanged = true; 
        markDirty();
    }
    
    private void updateCraftingResult() {
        ItemStack result = createCraftingResult();
        inventory[9] = result.isEmpty() ? ItemStack.EMPTY : result.copy();
        previousResult = result.copy();
        markDirty();
    }

    private void markForUpdate() {
        markDirty(); 
        if (world != null) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setInteger("EssentiaAmount", essentiaAmount);
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.essentiaAmount = tag.getInteger("EssentiaAmount");
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(this.pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("EssentiaAmount", essentiaAmount);

        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (!this.inventory[i].isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }
        compound.setTag("Items", nbtTagList);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.essentiaAmount = compound.getInteger("EssentiaAmount");

        NBTTagList nbtTagList = compound.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound itemTag = nbtTagList.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.inventory.length) {
                this.inventory[slot] = new ItemStack(itemTag);
            }
        }
    }

    @Override
    public AspectList getAspects() {
        AspectList aspects = new AspectList();
        aspects.add(REQUIRED_ESSENTIA, essentiaAmount);
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect == REQUIRED_ESSENTIA;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == REQUIRED_ESSENTIA && essentiaAmount + amount <= MAX_ESSENTIA_AMOUNT) {
            int essentiaToAdd = Math.min(amount, MAX_ESSENTIA_AMOUNT - essentiaAmount);
            essentiaAmount += essentiaToAdd;
            markForUpdate();
            return essentiaToAdd;
        }
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (aspect == REQUIRED_ESSENTIA && essentiaAmount >= amount) {
            essentiaAmount -= amount;
            markForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return aspect == REQUIRED_ESSENTIA && essentiaAmount >= amount;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return essentiaAmount;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return ot.getAmount(REQUIRED_ESSENTIA) <= essentiaAmount;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        if (doesContainerContain(ot)) {
            takeFromContainer(REQUIRED_ESSENTIA, ot.getAmount(REQUIRED_ESSENTIA));
            return true;
        }
        return false;
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
    public int getMinimumSuction() {
        return 128;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return essentiaAmount < MAX_ESSENTIA_AMOUNT ? 128 : 0;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return REQUIRED_ESSENTIA;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return addToContainer(aspect, amount);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return takeFromContainer(aspect, amount) ? amount : 0;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return essentiaAmount;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return REQUIRED_ESSENTIA;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.mattery_duplicator";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this &&
                player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= 0 && index < this.inventory.length) {
            return this.inventory[index] != null ? this.inventory[index] : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (!inventory[index].isEmpty()) {
            ItemStack itemstack;

            if (inventory[index].getCount() <= count) {
                itemstack = inventory[index];
                inventory[index] = ItemStack.EMPTY;
                markDirty();
                updateCraftingResult();
                return itemstack;
            } else {
                itemstack = inventory[index].splitStack(count);
                if (inventory[index].getCount() == 0) {
                    inventory[index] = ItemStack.EMPTY;
                }
                markDirty();
                updateCraftingResult();
                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!inventory[index].isEmpty()) {
            ItemStack itemstack = inventory[index];
            inventory[index] = ItemStack.EMPTY;
            updateCraftingResult();
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

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

    @Override
    public void clear() {
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
