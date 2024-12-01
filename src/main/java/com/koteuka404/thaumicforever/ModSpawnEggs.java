package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModSpawnEggs {

    public static void registerEggs() {
        registerEgg("skeleton_angry", 0xFFFFFF, 0xFF0000); // Білий і червоний
        registerEgg("revive_skeleton", 0x846232, 0x000000); // Коричневий і чорний
    }

    private static void registerEgg(String entityName, int primaryColor, int secondaryColor) {
        ResourceLocation entityID = new ResourceLocation(ThaumicForever.MODID, entityName);
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(entityID);

        if (entry == null) {
            System.err.println("Сутність не знайдена: " + entityName);
            return;
        }

        EntityRegistry.registerEgg(entityID, primaryColor, secondaryColor);
        System.out.println("Яйце спавну для " + entityName + " успішно зареєстроване!");
    }
}
