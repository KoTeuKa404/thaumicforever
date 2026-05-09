package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.koteuka404.thaumicforever.recipe.CustomSalisMundusRecipe;
import com.koteuka404.thaumicforever.recipe.RecipeRepairScribeToolLargeN;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModRecipeRegistry {
    @SubscribeEvent
    public static void onRegisterRecipes(final RegistryEvent.Register<IRecipe> event) {
        if (Loader.isModLoaded(ThaumicForever.MODID)) {
            event.getRegistry().register(new CustomSalisMundusRecipe());
        }

        for (int n = 1; n <= 8; n++) {
            event.getRegistry().register(new RecipeRepairScribeToolLargeN(n));
        }    }
}
