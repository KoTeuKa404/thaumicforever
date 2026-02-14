package com.koteuka404.thaumicforever.wand.crafting.recipe;

import com.koteuka404.thaumicforever.wand.api.ThaumicWandsAPI;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandCap;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandRod;
import com.koteuka404.thaumicforever.wand.api.recipe.IPlayerDependentArcaneRecipe;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import com.koteuka404.thaumicforever.wand.util.WandWrapper;
import com.koteuka404.thaumicforever.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneWorkbench;

public class RecipeScepter extends Impl<IRecipe> implements IPlayerDependentArcaneRecipe {

    private WandWrapper wrapper = new WandWrapper();

    public RecipeScepter(ResourceLocation name) {
        this.setRegistryName(name);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world, EntityPlayer player) {

        if (!(inv instanceof IArcaneWorkbench) )
            return false;
        int[] empty = {0, 3, 7, 8};
        for (int i : empty)
            if (!inv.getStackInSlot(i).isEmpty())
                return false;
        if (!getParts(inv).isValidWand())
            return false;
        if (!wrapper.canCraftScepter(player))
            return false;
        if (!checkCrystals(inv, player))
            return false;
        return true;

    }

    private WandWrapper getParts(InventoryCrafting inv) {

        wrapper = new WandWrapper();

        if (inv.getStackInSlot(1).isEmpty() || inv.getStackInSlot(2).isEmpty() || inv.getStackInSlot(5).isEmpty() || inv.getStackInSlot(6).isEmpty() || inv.getStackInSlot(4).isEmpty())
            return wrapper;

        if (!ItemStack.areItemsEqual(inv.getStackInSlot(1), inv.getStackInSlot(6)) || !ItemStack.areItemsEqual(inv.getStackInSlot(1), inv.getStackInSlot(5)))
            return wrapper;

        if (inv.getStackInSlot(2).getItem() != ModItems.AQUAREIA_GEM)
            return wrapper;

        for (IWandCap wc : ThaumicWandsAPI.wandCaps.values())
            if (ItemStack.areItemsEqual(inv.getStackInSlot(1), wc.getItemStack())) {
                wrapper.setCap(wc);
                break;
            }

        for (IWandRod wr : ThaumicWandsAPI.wandRods.values())
            if (ItemStack.areItemsEqual(inv.getStackInSlot(4), wr.getItemStack())) {
                wrapper.setRod(wr);
                break;
            }


        return wrapper;

    }

    private boolean checkCrystals(InventoryCrafting inv, EntityPlayer player) {
        AspectList required = getCrystals();
        if (required == null || required.size() == 0) return true;

        if (inv.getSizeInventory() >= 16) {
            ItemStack wand = inv.getStackInSlot(15);
            if (!wand.isEmpty()) {
                AspectList crystals = WandHelper.getActualCrystals(required, wand);
                return WandHelper.consumePrimalCharge(wand, crystals, player, true);
            }
        }

        AspectList available = new AspectList();
        for (int i = 9; i <= 14 && i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            AspectList stackAspects = new AspectList(stack);
            for (Aspect aspect : stackAspects.getAspects()) {
                int amount = stackAspects.getAmount(aspect) * stack.getCount();
                if (amount > 0) available.add(aspect, amount);
            }
        }

        for (Aspect aspect : required.getAspects()) {
            if (available.getAmount(aspect) < required.getAmount(aspect)) return false;
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return getParts(inv).isValidWand() ? getParts(inv).makeScepter() : ItemStack.EMPTY;
    }

    @Override
    public AspectList getCrystals() {
        return wrapper.getCrystals();
    }

    @Override
    public int getVis() {
        return wrapper.getVisCost();
    }

    @Override
    public String getResearch() {
        return "";
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return false;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height == 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 9; i != inv.getSizeInventory(); i++)
            items.set(i, inv.getStackInSlot(i));

        return items;
    }

}
