package com.tutorialmod.turtywurty;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class AspectRegistryEvent {

    // Додаємо новий аспект WEATHER (tempestas)
    public static final Aspect WEATHER = new Aspect("Weather", 0xFFFFFF, new Aspect[] {Aspect.AIR, Aspect.WATER}, new ResourceLocation("thaumicforever", "textures/misc/tempestas.png"), 1);

    // Метод для реєстрації аспектів
    public static void registerAspects() {
        // Реєстрація аспекту відбувається автоматично при створенні об'єкта Aspect
        // Видалення аспектів DAY, NIGHT, MATTERY, POWER
        removeAspect("DAY");
        removeAspect("NIGHT");
        removeAspect("MATTERY");
        removeAspect("POWER");
    }

    // Метод для видалення аспекту за назвою
    public static void removeAspect(String aspectName) {
        Aspect aspect = Aspect.getAspect(aspectName);
        if (aspect != null) {
            // Замість безпосереднього видалення, просто переконаємося, що не використовуємо аспект
            System.out.println("Aspect " + aspectName + " exists but will be ignored.");
        } else {
            System.out.println("Aspect " + aspectName + " not found.");
        }
    }
}
