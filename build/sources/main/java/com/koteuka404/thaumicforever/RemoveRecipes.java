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
        // Отримуємо реєстр рецептів
        IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
        
        // Ідентифікатор рецепта для Void Metal Ingot
        ResourceLocation voidIngotRecipe = new ResourceLocation("thaumcraft", "void_metal_ingot");
        
        // Видаляємо рецепт
        if (modRegistry.containsKey(voidIngotRecipe)) {
            modRegistry.remove(voidIngotRecipe);
            System.out.println("Рецепт Void Metal Ingot видалено!");
        }
    }
}
