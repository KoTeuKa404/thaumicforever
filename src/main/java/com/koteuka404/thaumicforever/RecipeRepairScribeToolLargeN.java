package com.koteuka404.thaumicforever;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;


public class RecipeRepairScribeToolLargeN extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private final int dyesCount;      
    private final int repairAmount;

    public RecipeRepairScribeToolLargeN(int dyesCount) {
        this.dyesCount = dyesCount;
        this.repairAmount = dyesCount * 100;
        this.setRegistryName(new ResourceLocation(ThaumicForever.MODID, "repair_scribe_tool_large_" + dyesCount));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int countTool = 0;
        int countInk = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot.isEmpty()) continue;

            if (slot.getItem() instanceof ItemScribeToolLarge) {
                countTool++;
                if (countTool > 1) return false;
            } else if (slot.getItem() == Items.DYE && slot.getMetadata() == 0) {
                countInk += slot.getCount();
            } else {
                return false;
            }
        }
        return (countTool == 1 && countInk == dyesCount);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack foundTool = ItemStack.EMPTY;
        int countInk = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot.isEmpty()) continue;

            if (slot.getItem() instanceof ItemScribeToolLarge) {
                foundTool = slot;
            } else if (slot.getItem() == Items.DYE && slot.getMetadata() == 0) {
                countInk += slot.getCount();
            }
        }

        if (foundTool.isEmpty() || countInk != dyesCount) {
            return ItemStack.EMPTY;
        }

        ItemStack result = foundTool.copy();
        int oldDamage = result.getItemDamage();
        int newDamage = oldDamage - repairAmount;
        if (newDamage < 0) newDamage = 0;
        result.setItemDamage(newDamage);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public ItemStack getRecipeOutput() {
        ItemStack preview = new ItemStack(ModItems.ItemScribeToolLarge);
        int maxDmg = preview.getMaxDamage();
        int previewDamage = maxDmg - repairAmount;
        if (previewDamage < 0) previewDamage = 0;
        preview.setItemDamage(previewDamage);
        return preview;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= (dyesCount + 1);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.fromStacks(new ItemStack(ModItems.ItemScribeToolLarge)));
        ingredients.add(Ingredient.fromStacks(new ItemStack(Items.DYE, 1, 0)));
        return ingredients;
    }
}
