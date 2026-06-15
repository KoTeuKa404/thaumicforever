package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.world.biomes.BiomeHandler;
import com.koteuka404.thaumicforever.entity.EntityGorilla;
import com.koteuka404.thaumicforever.entity.EntitySkeletonAngry;
import com.koteuka404.thaumicforever.entity.EntityVampireBat;

public class ModEntitySpawns {

    public static void registerSpawns() {
        EntityRegistry.addSpawn(
            EntitySkeletonAngry.class,
            14,
            1,
            2,
            EnumCreatureType.MONSTER,
            Biomes.PLAINS, Biomes.FOREST, Biomes.DESERT,
            Biomes.SWAMPLAND, Biomes.ICE_PLAINS, Biomes.TAIGA,
            Biomes.SAVANNA, Biomes.MESA
        );

        EntityRegistry.addSpawn(
            EntityGorilla.class,
            2,
            1,
            1,
            EnumCreatureType.CREATURE,
            Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE
        );

        if (BiomeHandler.EERIE != null) {
            EntityRegistry.addSpawn(
                EntitySkeleton.class,
                80,
                1,
                4,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );

            EntityRegistry.addSpawn(
                EntitySkeletonAngry.class,
                40,
                1,
                2,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );

            EntityRegistry.addSpawn(
                EntityZombie.class,
                95,
                1,
                4,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );

            EntityRegistry.addSpawn(
                EntityZombieVillager.class,
                20,
                1,
                1,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );

            EntityRegistry.addSpawn(
                EntityBrainyZombie.class,
                24,
                1,
                2,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );

            EntityRegistry.addSpawn(
                EntityGiantBrainyZombie.class,
                5,
                1,
                1,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );

            EntityRegistry.addSpawn(
                EntityWitch.class,
                8,
                1,
                1,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );

            EntityRegistry.addSpawn(
                EntityVampireBat.class,
                30,
                1,
                3,
                EnumCreatureType.MONSTER,
                BiomeHandler.EERIE
            );
        }
    }
}
