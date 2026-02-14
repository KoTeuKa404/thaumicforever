package com.koteuka404.thaumicforever;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntitySpawns {

    public static void registerSpawns() {
        EntityRegistry.addSpawn(
            EntitySkeletonAngry.class,
            10,
            1,
            2,
            EnumCreatureType.MONSTER,
            Biomes.PLAINS, Biomes.FOREST, Biomes.DESERT,
            Biomes.SWAMPLAND, Biomes.ICE_PLAINS, Biomes.TAIGA,
            Biomes.HELL, Biomes.SAVANNA, Biomes.MESA
        );

        EntityRegistry.addSpawn(
            EntityGorilla.class,
            2,
            1,
            1,
            EnumCreatureType.CREATURE,
            Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE
        );
    }
}
