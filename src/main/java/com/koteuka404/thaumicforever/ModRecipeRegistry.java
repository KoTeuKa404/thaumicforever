package com.koteuka404.thaumicforever;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModRecipeRegistry {
    @SubscribeEvent
    public static void onRegisterRecipes(final RegistryEvent.Register<IRecipe> event) {
        if (Loader.isModLoaded("thaumicwands")) {
            event.getRegistry().register(new CustomSalisMundusRecipe());
        }

        for (int n = 1; n <= 8; n++) {
            event.getRegistry().register(new RecipeRepairScribeToolLargeN(n));
        }    }
}
