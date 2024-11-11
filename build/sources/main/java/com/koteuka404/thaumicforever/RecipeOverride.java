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
        // Перевірка конфігурації
        if (ModConfig.general.enableMechanismComplexRecipe) {
            // Створюємо новий рецепт з тим самим ResourceLocation
            ShapedArcaneRecipe mechanismComplexRecipe = new ShapedArcaneRecipe(
                new ResourceLocation("thaumcraft", "mechanism_complex"), // Ім'я рецепту
                "BASEARTIFICE", // Назва дослідження
                50, // Вартість Vis
                new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), // Потрібні аспекти
                new ItemStack(ItemsTC.mechanismComplex), // Результат (mechanismComplex)
                " B ", // Верхній ряд: [ ] [Void Gear] [ ]
                "AIA", // Середній ряд: [Plate] [Iron Gear] [Plate]
                " B ", // Нижній ряд: [ ] [Void Gear] [ ]
                'B', new ItemStack(ItemsTC.mechanismSimple), // 'B' — це простий механізм
                'A', new ItemStack(ItemsTC.plate, 1, 2), // 'A' — це металеві пластини
                'I', ModItems.ItemBrassGear // 'I' — це латунна шестерня
            );

            // Реєстрація рецепту
            ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft", "mechanism_complex"), mechanismComplexRecipe);
        }
    }
}
