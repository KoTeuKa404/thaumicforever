package com.koteuka404.thaumicforever.wand.container;

import com.koteuka404.thaumicforever.wand.container.slot.SlotArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.container.slot.SlotCrystalLocked;
import com.koteuka404.thaumicforever.wand.container.slot.SlotWand;
import com.koteuka404.thaumicforever.wand.api.recipe.IPlayerDependentArcaneRecipe;
import com.koteuka404.thaumicforever.wand.crafting.ThaumicWandsCraftingManager;
import com.koteuka404.thaumicforever.wand.inventory.InventoryArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import java.util.ArrayList;
import java.util.List;

public class ContainerArcaneWorkbenchNew extends Container {

    private final TileArcaneWorkbench tileEntity;
    private final InventoryPlayer ip;
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    public static int[] xx = new int[]{64, 17, 112, 17, 112, 64};
    public static int[] yy = new int[]{13, 35, 35, 93, 93, 115};
    private int selectedRecipeIndex = 0;
    private int matchingRecipeCount = 0;
    private int lastSentSelectedRecipeIndex = -1;
    private int lastSentMatchingRecipeCount = -1;
    private List<IRecipe> cachedMatches = new ArrayList<>();

    public ContainerArcaneWorkbenchNew(InventoryPlayer inv, TileArcaneWorkbench e) {
        this.tileEntity = e;
        this.ip = inv;
        setWorkbenchEventHandler(this);

        // Crafting Grid 0-8
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                addSlotToContainer(new Slot(this.tileEntity.inventoryCraft, y + x * 3, 40 + y * 24, 40 + x * 24));

        // Shard Slots 9-14
        for (ShardType st : ShardType.values())
            if (st.getMetadata() < 6)
                addSlotToContainer(new SlotCrystalLocked(st.getAspect(), this.tileEntity.inventoryCraft, st.getMetadata() + 9, xx[st.getMetadata()], yy[st.getMetadata()]));

        // Wand Slot 15
        addSlotToContainer(new SlotWand(this.tileEntity.inventoryCraft, 15, 160, 24));

        // Output Slot 16
        addSlotToContainer(new SlotArcaneWorkbenchNew(this, this.tileEntity, inv.player, this.tileEntity.inventoryCraft, this.craftResult, 16, 160, 64));

        //Player Inventory 17-43
        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 3; y++)
                addSlotToContainer(new Slot(inv, x + y * 9 + 9, 16 + x * 18, 151 + y * 18));

        //Player Hotbar 44-53
        for (int x = 0; x < 9; x++)
            addSlotToContainer(new Slot(inv, x, 16 + x * 18, 209));


        onCraftMatrixChanged(this.tileEntity.inventoryCraft);
    }

    private void setWorkbenchEventHandler(Container handler) {
        this.tileEntity.inventoryCraft.eventHandler = handler;
        if (this.tileEntity.inventoryCraft instanceof InventoryArcaneWorkbenchNew) {
            ((InventoryArcaneWorkbenchNew) this.tileEntity.inventoryCraft).eventHandler = handler;
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!this.tileEntity.getWorld().isRemote) {
            if (this.lastSentSelectedRecipeIndex != this.selectedRecipeIndex || this.lastSentMatchingRecipeCount != this.matchingRecipeCount) {
                for (IContainerListener listener : this.listeners) {
                    listener.sendWindowProperty(this, 0, this.selectedRecipeIndex);
                    listener.sendWindowProperty(this, 1, this.matchingRecipeCount);
                }
                this.lastSentSelectedRecipeIndex = this.selectedRecipeIndex;
                this.lastSentMatchingRecipeCount = this.matchingRecipeCount;
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        IRecipe selectedRecipe = getSelectedFromMatches(this.cachedMatches);
        if (selectedRecipe == null || !recipeMatches(selectedRecipe, this.ip.player)) {
            this.cachedMatches = getMatchingRecipes(this.ip.player);
            this.matchingRecipeCount = this.cachedMatches.size();
            if (this.matchingRecipeCount <= 0) {
                this.selectedRecipeIndex = 0;
            } else if (this.selectedRecipeIndex >= this.matchingRecipeCount) {
                this.selectedRecipeIndex = 0;
            } else if (this.selectedRecipeIndex < 0) {
                this.selectedRecipeIndex = this.matchingRecipeCount - 1;
            }
            selectedRecipe = getSelectedFromMatches(this.cachedMatches);
        } else {
            this.matchingRecipeCount = this.cachedMatches.size();
        }

        if (selectedRecipe != null && !canCraftRecipe(selectedRecipe)) {
            for (int i = 0; i < this.cachedMatches.size(); i++) {
                IRecipe candidate = this.cachedMatches.get(i);
                if (canCraftRecipe(candidate)) {
                    this.selectedRecipeIndex = i;
                    selectedRecipe = candidate;
                    break;
                }
            }
        }

        boolean canCraft = selectedRecipe != null && canCraftRecipe(selectedRecipe);

        if (canCraft)
            slotChangedCraftingGrid(this.tileEntity.getWorld(), this.ip.player, this.tileEntity.inventoryCraft, this.craftResult);
        else {
            this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
            if (!this.tileEntity.getWorld().isRemote)
                ((EntityPlayerMP) this.ip.player).connection.sendPacket(new SPacketSetSlot(this.windowId, 16, ItemStack.EMPTY));
        }

        super.detectAndSendChanges();
    }

    @Override
    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMat, InventoryCraftResult craftRes) {
        if (!world.isRemote) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
            ItemStack itemstack = ItemStack.EMPTY;
            IRecipe selected = getSelectedRecipe(player);
            if (selected instanceof IArcaneRecipe) {
                IArcaneRecipe arecipe = (IArcaneRecipe) selected;
                if (ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(arecipe.getResearch())) {
                    craftRes.setRecipeUsed(arecipe);
                    itemstack = arecipe.getCraftingResult(craftMat);
                }
            } else if (selected != null) {
                InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);
                for (int a = 0; a < 9; a++)
                    craftInv.setInventorySlotContents(a, craftMat.getStackInSlot(a));
                craftRes.setRecipeUsed(selected);
                itemstack = selected.getCraftingResult(craftInv);
            } else {
                InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);
                for (int a = 0; a < 9; a++)
                    craftInv.setInventorySlotContents(a, craftMat.getStackInSlot(a));
                IRecipe irecipe = CraftingManager.findMatchingRecipe(craftInv, world);
                if (irecipe != null) {
                    craftRes.setRecipeUsed(irecipe);
                    itemstack = irecipe.getCraftingResult(craftMat);
                }
            }
            craftRes.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 16, itemstack));
        }

    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return this.tileEntity.getWorld().getTileEntity(this.tileEntity.getPos()) == this.tileEntity && ((par1EntityPlayer.getDistanceSqToCenter(this.tileEntity.getPos()) <= 64.0D));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!this.tileEntity.getWorld().isRemote) {
            setWorkbenchEventHandler(new ContainerDummy());
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotIndex) {
        ItemStack var2 = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack var4 = slot.getStack();
            var2 = var4.copy();

            // Output
            if (slotIndex == 16) {
                if (!mergeItemStack(var4, 17, 53, true))
                    return ItemStack.EMPTY;
                slot.onSlotChange(var4, var2);
            }

            else if (slotIndex >= 17 && slotIndex < 53) {
                if (this.inventorySlots.get(15).isItemValid(var4)) {
                    if (!mergeItemStack(var4, 15, 16, false))
                        return ItemStack.EMPTY;
                } else if (slotIndex >= 17 && slotIndex < 44) {
                    if (!mergeItemStack(var4, 44, 53, false))
                        return ItemStack.EMPTY;
                } else if (slotIndex >= 44 && slotIndex < 53) {
                    if (!mergeItemStack(var4, 17, 44, false))
                        return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(var4, 17, 53, false)) {
                return ItemStack.EMPTY;
            }
            if (var4.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (var4.getCount() == var2.getCount())
                return ItemStack.EMPTY;
            slot.onTake(this.ip.player, var4);
        }
        return var2;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return (slot.inventory != this.craftResult && super.canMergeSlot(stack, slot));
    }

    public IArcaneRecipe getSelectedArcaneRecipe(EntityPlayer player) {
        IRecipe selected = getSelectedRecipe(player);
        return selected instanceof IArcaneRecipe ? (IArcaneRecipe) selected : null;
    }

    public int getMatchingRecipeCount() {
        return this.matchingRecipeCount;
    }

    public IRecipe getSelectedRecipe(EntityPlayer player) {
        List<IRecipe> matches = this.cachedMatches;
        if (matches == null || matches.isEmpty()) {
            matches = getMatchingRecipes(player);
            this.cachedMatches = matches;
            this.matchingRecipeCount = matches.size();
        }
        if (matches.isEmpty()) return null;
        int idx = this.selectedRecipeIndex;
        if (idx < 0 || idx >= matches.size()) idx = 0;
        return matches.get(idx);
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (id == 0 || id == 1) {
            if (this.matchingRecipeCount <= 1) {
                return true;
            }
            if (id == 0) {
                this.selectedRecipeIndex = (this.selectedRecipeIndex - 1 + this.matchingRecipeCount) % this.matchingRecipeCount;
            } else {
                this.selectedRecipeIndex = (this.selectedRecipeIndex + 1) % this.matchingRecipeCount;
            }
            onCraftMatrixChanged(this.tileEntity.inventoryCraft);
            detectAndSendChanges();
            return true;
        }
        return super.enchantItem(playerIn, id);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == 0) {
            this.selectedRecipeIndex = data;
        } else if (id == 1) {
            this.matchingRecipeCount = data;
            if (this.matchingRecipeCount <= 0) {
                this.selectedRecipeIndex = 0;
            } else if (this.selectedRecipeIndex >= this.matchingRecipeCount) {
                this.selectedRecipeIndex = 0;
            }
        }
    }

    private IRecipe getSelectedFromMatches(List<IRecipe> matches) {
        if (matches == null || matches.isEmpty()) return null;
        int idx = this.selectedRecipeIndex;
        if (idx < 0 || idx >= matches.size()) idx = 0;
        return matches.get(idx);
    }

    private List<IRecipe> getMatchingRecipes(EntityPlayer player) {
        List<IRecipe> out = new ArrayList<>();
        List<IArcaneRecipe> arcane = ThaumicWandsCraftingManager.findMatchingArcaneRecipes(this.tileEntity.inventoryCraft, player);
        out.addAll(arcane);

        InventoryCrafting craftView = new InventoryCrafting(new ContainerDummy(), 3, 3);
        for (int i = 0; i < 9; i++) {
            craftView.setInventorySlotContents(i, this.tileEntity.inventoryCraft.getStackInSlot(i));
        }

        for (ResourceLocation key : CraftingManager.REGISTRY.getKeys()) {
            IRecipe recipe = CraftingManager.REGISTRY.getObject(key);
            if (recipe == null || recipe instanceof IArcaneRecipe) continue;
            boolean matches;
            if (recipe instanceof IPlayerDependentArcaneRecipe) {
                matches = ((IPlayerDependentArcaneRecipe) recipe).matches(craftView, player.world, player);
            } else {
                matches = recipe.matches(craftView, player.world);
            }
            if (matches) {
                out.add(recipe);
            }
        }
        return out;
    }

    private boolean recipeMatches(IRecipe recipe, EntityPlayer player) {
        if (recipe == null) return false;
        if (recipe instanceof IPlayerDependentArcaneRecipe) {
            return ((IPlayerDependentArcaneRecipe) recipe).matches(this.tileEntity.inventoryCraft, player.world, player);
        }
        if (recipe instanceof IArcaneRecipe) {
            return recipe.matches(this.tileEntity.inventoryCraft, player.world);
        }

        InventoryCrafting craftView = new InventoryCrafting(new ContainerDummy(), 3, 3);
        for (int i = 0; i < 9; i++) {
            craftView.setInventorySlotContents(i, this.tileEntity.inventoryCraft.getStackInSlot(i));
        }
        return recipe.matches(craftView, player.world);
    }

    private boolean canCraftRecipe(IRecipe selectedRecipe) {
        if (selectedRecipe == null) return false;
        if (!(selectedRecipe instanceof IArcaneRecipe)) return true;

        IArcaneRecipe recipe = (IArcaneRecipe) selectedRecipe;
        ItemStack wand = this.tileEntity.inventoryCraft.getStackInSlot(15);
        if (wand.isEmpty()) return false;

        int vis = WandHelper.getActualVisCost(recipe.getVis(), wand, this.ip.player);
        if (!this.tileEntity.getWorld().isRemote && Math.max(0, RechargeHelper.getCharge(wand)) < vis) {
            return false;
        }

        AspectList crystals = WandHelper.getActualCrystals(recipe.getCrystals(), wand);
        return crystals == null || crystals.size() <= 0 || this.tileEntity.getWorld().isRemote
                || WandHelper.consumePrimalCharge(wand, crystals, this.ip.player, true);
    }

}
