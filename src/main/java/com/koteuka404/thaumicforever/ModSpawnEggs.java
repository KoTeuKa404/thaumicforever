package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModSpawnEggs {

    private static final Set<String> registered = new HashSet<>();

    public static void registerEggs() {
        registerEgg("skeleton_angry", 0xFFFFFF, 0xFF0000);
        registerEgg("revive_skeleton", 0x846232, 0x000000); 
        registerEgg("bvilager", 0x228B22, 0xFF0000); 
        registerEgg("coinvilager", 0x563C33, 0xFFD700); 
        registerEgg("wizard", 0x6A0DAD, 0xFFD700);
        registerEgg("gorilla", 0x4B4B4B, 0xFFFFFF); 
        registerEgg("watcher_guard", 0xbcc6c1, 0x64B496);
    }

    private static void registerEgg(String entityName, int primaryColor, int secondaryColor) {
        if (registered.contains(entityName)) {
            System.out.println("entity " + entityName + " pass");
            return;
        }

        ResourceLocation entityID = new ResourceLocation(ThaumicForever.MODID, entityName);
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(entityID);

        if (entry == null) {
            System.err.println("entity not found: " + entityName);
            return;
        }

        EntityRegistry.registerEgg(entityID, primaryColor, secondaryColor);
        registered.add(entityName);
    }
}
