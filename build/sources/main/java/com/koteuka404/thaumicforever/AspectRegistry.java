package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class AspectRegistry {

    // Додаємо новий аспект WEATHER (tempestas)
    public static final Aspect WEATHER = new Aspect("Weather", 0xFFFFFF, new Aspect[] {Aspect.AIR, Aspect.WATER}, new ResourceLocation("thaumicforever", "textures/misc/tempestas.png"), 1);
    public static final Aspect MATTERYA = new Aspect("Mattery",0x00FF00, new Aspect[] {Aspect.DESIRE, Aspect.CRAFT}, new ResourceLocation("thaumicforever", "textures/misc/mattery.png"), 1);

    // Метод для реєстрації аспектів
    public static void registerAspects() {
        removeAspect("DAY");
        removeAspect("NIGHT");
        removeAspect("MATTERY");
        removeAspect("POWER");
    }

    public static void removeAspect(String aspectName) {
        Aspect aspect = Aspect.getAspect(aspectName);
        if (aspect != null) {
            System.out.println("Aspect " + aspectName + " exists but will be ignored.");
        } else {
            System.out.println("Aspect " + aspectName + " not found.");
        }
    }
}
