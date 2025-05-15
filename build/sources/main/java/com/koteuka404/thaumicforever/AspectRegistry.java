package com.koteuka404.thaumicforever;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class AspectRegistry {

    public static final Aspect WEATHER = new Aspect("weather", 0xFFFFFF, new Aspect[]{Aspect.AIR, Aspect.WATER}, new ResourceLocation("thaumicforever", "textures/misc/tempestas.png"), 1);
    public static final Aspect MATTERYA = new Aspect("matterya", 0x00FF00, new Aspect[]{Aspect.DESIRE, Aspect.CRAFT}, new ResourceLocation("thaumicforever", "textures/misc/mattery.png"), 1);

    public static void registerAspects() {
        removeAspect("day");
        removeAspect("night");
        removeAspect("mattery");
        removeAspect("energy");
    }

    public static void removeAspect(String aspectName) {
        try {
            Field aspectsField = Aspect.class.getDeclaredField("aspects");
            aspectsField.setAccessible(true);
            Map<String, Aspect> aspects = (Map<String, Aspect>) aspectsField.get(null);
            
            if (aspects.containsKey(aspectName.toLowerCase())) {
                aspects.remove(aspectName.toLowerCase());
                System.out.println("Removed aspect: " + aspectName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}