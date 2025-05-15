package com.koteuka404.thaumicforever.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class DeconstructionRecipeCategory implements IRecipeCategory<DeconstructionRecipeWrapper> {
    public static final String UID = "thaumicforever.deconstruction";
    private final IDrawable background;
    private final String title;

    public DeconstructionRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation("thaumicforever", "textures/gui/deconstruction_table.png"), 0, 0, 176, 76);
        this.title = I18n.format("jei.thaumicforever.deconstruction");
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getModName() {
        return "ThaumicForever";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DeconstructionRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        stacks.init(0, true, 64, 15); // input
        stacks.init(1, false, 64, 47); // output

        stacks.set(ingredients);
        stacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 1) { // output slot
                tooltip.add("Chance: " + (int)(recipeWrapper.getChance() * 100) + "%");
            }
        });
        
    }


}   
