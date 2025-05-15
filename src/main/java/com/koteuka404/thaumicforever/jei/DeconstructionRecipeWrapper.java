package com.koteuka404.thaumicforever.jei;

import java.util.Collections;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class DeconstructionRecipeWrapper implements IRecipeWrapper {
    private final ItemStack input;
    private final List<ItemStack> outputs;
    private final float chance;

    public DeconstructionRecipeWrapper(ItemStack input, List<ItemStack> outputs, float chance) {
        this.input = input;
        this.outputs = outputs;
        this.chance = chance;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, Collections.singletonList(input));
        ingredients.setOutputs(ItemStack.class, outputs);
    }

    public float getChance() {
        return chance;
    }
}