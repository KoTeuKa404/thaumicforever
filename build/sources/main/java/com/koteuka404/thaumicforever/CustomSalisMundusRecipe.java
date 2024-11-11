package com.koteuka404.thaumicforever;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

public class CustomSalisMundusRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public CustomSalisMundusRecipe() {
        setRegistryName(new ResourceLocation("thaumicforever", "salis_mundus_recipe"));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean bowl = false;
        boolean flint = false;
        boolean redstone = false;
        ArrayList<String> crystals = new ArrayList<>();

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack.isEmpty()) continue;

            if (stack.getItem() == Items.BOWL) {
                if (bowl) return false;
                bowl = true;
            } else if (stack.getItem() == Items.FLINT) {
                if (flint) return false;
                flint = true;
            } else if (stack.getItem() == Items.REDSTONE) {
                if (redstone) return false;
                redstone = true;
            } else if (stack.getItem() == ItemsTC.crystalEssence) {
                ItemCrystalEssence ice = (ItemCrystalEssence) stack.getItem();
                String aspect = ice.getAspects(stack).getAspects()[0].getTag();
                if (crystals.contains(aspect) || crystals.size() >= 3) {
                    return false;
                }
                crystals.add(aspect);
            } else {
                return false; // Неправильний інгредієнт
            }
        }

        return bowl && flint && redstone && crystals.size() == 3;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return new ItemStack(ItemsTC.salisMundus);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 6;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemsTC.salisMundus);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < ret.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty() && (itemstack.getItem() == Items.FLINT || itemstack.getItem() == Items.BOWL)) {
                ItemStack is = itemstack.copy();
                is.setCount(1);
                ret.set(i, is);
            } else {
                ret.set(i, ForgeHooks.getContainerItem(itemstack));
            }
        }

        return ret;
    }
}

