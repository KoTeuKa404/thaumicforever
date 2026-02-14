package com.koteuka404.thaumicforever.wand.inventory;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.InventoryArcaneWorkbench;

public class InventoryArcaneWorkbenchNew extends InventoryArcaneWorkbench {

    private final NonNullList<ItemStack> stackList;
    private int inventoryHeight = 3;
    private int inventoryWidth = 5;
    public Container eventHandler;

    public InventoryArcaneWorkbenchNew(TileEntity tileEntity, Container container) {
        super(tileEntity, container);
        this.stackList = NonNullList.withSize(16, ItemStack.EMPTY);
        this.eventHandler = container;
    }

    public int getSizeInventory() {
        return this.stackList.size();
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.stackList)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }

    public ItemStack getStackInSlot(int index) {
        if (index >= this.getSizeInventory()) return ItemStack.EMPTY;
        if (index >= 9 && index <= 14) return getVirtualCrystal(index);
        return this.stackList.get(index);
    }

    public ItemStack getStackInRowAndColumn(int row, int column) {
        return row >= 0 && row < this.inventoryWidth && column >= 0 && column <= this.inventoryHeight ? this.getStackInSlot(row + column * this.inventoryWidth) : ItemStack.EMPTY;
    }

    public ItemStack removeStackFromSlot(int index) {
        if (index >= 9 && index <= 14) return ItemStack.EMPTY;
        ItemStack stack = ItemStackHelper.getAndRemove(this.stackList, index);
        this.eventHandler.onCraftMatrixChanged(this);
        return stack;
    }

    public ItemStack decrStackSize(int index, int count) {
        if (index >= 9 && index <= 14) return ItemStack.EMPTY;
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, index, count);

        if (!itemstack.isEmpty())
            this.eventHandler.onCraftMatrixChanged(this);

        return itemstack;
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= 9 && index <= 14) return;
        this.stackList.set(index, stack);
        this.eventHandler.onCraftMatrixChanged(this);
    }

    public void clear() {
        this.stackList.clear();
    }

    public int getHeight() {
        return this.inventoryHeight;
    }

    public int getWidth() {
        return this.inventoryWidth;
    }

    public void fillStackedContents(RecipeItemHelper helper) {
        for (ItemStack itemstack : this.stackList)
            helper.accountStack(itemstack);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= 9 && index <= 14) return false;
        return true;
    }

    public ItemStack getRealStackInSlot(int index) {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(index);
    }

    public void clearSlot(int index) {
        if (index >= 0 && index < this.getSizeInventory()) {
            this.stackList.set(index, ItemStack.EMPTY);
        }
    }

    private ItemStack getVirtualCrystal(int index) {
        ItemStack wand = getRealStackInSlot(15);
        if (wand.isEmpty()) return ItemStack.EMPTY;

        int meta = index - 9;
        Aspect aspect = null;
        for (ShardType st : ShardType.values()) {
            if (st.getMetadata() == meta) {
                aspect = st.getAspect();
                break;
            }
        }
        if (aspect == null) return ItemStack.EMPTY;

        AspectList charge = WandHelper.getPrimalCharge(wand);
        int count = charge.getAmount(aspect);
        if (wand.getItem() instanceof IWand) {
            AspectList discount = ((IWand) wand.getItem()).getCap(wand).getAspectDiscount();
            if (discount != null) count += discount.getAmount(aspect);
        }
        if (count <= 0) return ItemStack.EMPTY;
        if (count > 64) count = 64;

        return ThaumcraftApiHelper.makeCrystal(aspect, count);
    }

}
