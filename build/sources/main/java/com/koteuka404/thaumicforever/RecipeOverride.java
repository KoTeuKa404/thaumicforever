package com.koteuka404.thaumicforever;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

@Mod.EventBusSubscriber
public class RecipeOverride {

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        if (ModConfig.general.enableMechanismComplexRecipe) {
            ShapedArcaneRecipe mechanismComplexRecipe = new ShapedArcaneRecipe(
                new ResourceLocation("thaumcraft", "mechanism_complex"), // Ім'я рецепту
                "BASEARTIFICE",
                50, 
                new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), // Потрібні аспекти
                new ItemStack(ItemsTC.mechanismComplex), 
                " B ",
                "AIA",
                " B ",
                'B', new ItemStack(ItemsTC.mechanismSimple), 
                'A', new ItemStack(ItemsTC.plate, 1, 2), 
                'I', ModItems.ItemBrassGear 
            );

            ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft", "mechanism_complex"), mechanismComplexRecipe);
        }
    }
}
