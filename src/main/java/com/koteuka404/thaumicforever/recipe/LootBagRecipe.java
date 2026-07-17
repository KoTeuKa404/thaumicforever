package com.koteuka404.thaumicforever.recipe;

import com.koteuka404.thaumicforever.item.LootBag;
import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class LootBagRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public LootBagRecipe() {
        setRegistryName(new ResourceLocation("thaumicforever", "lootbag_from_coins"));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int coins = 0;
        int bags = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == ModItems.coin) {
                coins += stack.getCount();
            } else if (stack.getItem() == ModItems.lootbag) {
                if (++bags > 1) return false;
                coins += LootBag.getCoinCount(stack);
            } else {
                return false;
            }
        }
        return bags == 0 ? hasMultipleCoinSlots(inv) : bags == 1 && hasCoins(inv);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int coins = 0;
        int bags = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == ModItems.coin) {
                coins += stack.getCount();
            } else if (stack.getItem() == ModItems.lootbag && bags++ == 0) {
                coins += LootBag.getCoinCount(stack);
            }
        }
        return coins > 0 ? LootBag.createStack(coins) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return LootBag.createStack(2);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    private static boolean hasCoins(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.coin) return true;
        }
        return false;
    }

    private static boolean hasMultipleCoinSlots(InventoryCrafting inv) {
        int slots = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.coin && ++slots >= 2) return true;
        }
        return false;
    }
}
