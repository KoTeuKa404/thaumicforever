package com.koteuka404.thaumicforever;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

@Mod.EventBusSubscriber
public class RemoveRecipes {

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
        
        ResourceLocation voidIngotRecipe = new ResourceLocation("thaumcraft", "void_metal_ingot");
        
        if (modRegistry.containsKey(voidIngotRecipe)) {
            modRegistry.remove(voidIngotRecipe);
        }
    }
}
