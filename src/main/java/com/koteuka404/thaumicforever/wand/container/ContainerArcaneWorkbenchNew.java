package com.koteuka404.thaumicforever.wand.container;

import com.koteuka404.thaumicforever.wand.container.slot.SlotArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.container.slot.SlotCrystalLocked;
import com.koteuka404.thaumicforever.wand.container.slot.SlotWand;
import com.koteuka404.thaumicforever.wand.crafting.ThaumicWandsCraftingManager;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class ContainerArcaneWorkbenchNew extends Container {

    private final TileArcaneWorkbench tileEntity;
    private final InventoryPlayer ip;
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    public static int[] xx = new int[]{64, 17, 112, 17, 112, 64};
    public static int[] yy = new int[]{13, 35, 35, 93, 93, 115};

    public ContainerArcaneWorkbenchNew(InventoryPlayer inv, TileArcaneWorkbench e) {
        this.tileEntity = e;
        this.ip = inv;

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
        addSlotToContainer(new SlotArcaneWorkbenchNew(this.tileEntity, inv.player, this.tileEntity.inventoryCraft, this.craftResult, 16, 160, 64));

        //Player Inventory 17-43
        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 3; y++)
                addSlotToContainer(new Slot(inv, x + y * 9 + 9, 16 + x * 18, 151 + y * 18));

        //Player Hotbar 44-53
        for (int x = 0; x < 9; x++)
            addSlotToContainer(new Slot(inv, x, 16 + x * 18, 209));


        onCraftMatrixChanged(this.tileEntity.inventoryCraft);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        onCraftMatrixChanged(this.tileEntity.inventoryCraft);
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        IArcaneRecipe recipe = ThaumicWandsCraftingManager.findMatchingArcaneRecipe(this.tileEntity.inventoryCraft, this.ip.player);
        boolean hasVis = true;
        boolean hasCrystals = true;

        if (recipe != null) {
            int vis = 0;
            AspectList crystals = null;
            vis = recipe.getVis();
            ItemStack wand = this.tileEntity.inventoryCraft.getStackInSlot(15);
            if (wand.isEmpty()) {
                hasVis = false;
                hasCrystals = false;
            } else {
                vis = WandHelper.getActualVisCost(vis, wand, ip.player);
                crystals = WandHelper.getActualCrystals(recipe.getCrystals(), wand);
                if (this.tileEntity.getWorld().isRemote) {
                    hasVis = true;
                } else {
                    hasVis = Math.max(0, RechargeHelper.getCharge(wand)) >= vis;
                }
                if (crystals != null && crystals.size() > 0) {
                    if (this.tileEntity.getWorld().isRemote) hasCrystals = true;
                    else hasCrystals = WandHelper.consumePrimalCharge(wand, crystals, ip.player, true);
                }
            }
        }

        if (hasVis && hasCrystals)
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
            IArcaneRecipe arecipe = ThaumicWandsCraftingManager.findMatchingArcaneRecipe(craftMat, player);
            if (arecipe != null
                    && (arecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting")
                    || entityplayermp.getRecipeBook().isUnlocked(arecipe))
                    && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(arecipe.getResearch())) {
                craftRes.setRecipeUsed(arecipe);
                itemstack = arecipe.getCraftingResult(craftMat);
            } else {
                InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);
                for (int a = 0; a < 9; a++)
                    craftInv.setInventorySlotContents(a, craftMat.getStackInSlot(a));
                IRecipe irecipe = CraftingManager.findMatchingRecipe(craftInv, world);
                if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting")
                        || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
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
                if (slotIndex >= 16 && slotIndex < 43) {
                    if (!mergeItemStack(var4, 43, 52, false))
                        return ItemStack.EMPTY;
                } else if (slotIndex >= 43 && slotIndex < 52) {
                    if (!mergeItemStack(var4, 16, 43, false))
                        return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(var4, 16, 52, false)) {
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

}
