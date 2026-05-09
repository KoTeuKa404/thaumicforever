package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.config.ModConfig;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.world.biomes.BiomeHandler;
import com.koteuka404.thaumicforever.entity.EntitySkeletonAngry;
import com.koteuka404.thaumicforever.entity.EntityVampireBat;

public class EerieBiomeMobHandler {

    // Day spawn chance in Eerie relative to the normal night behavior.
    private static final float DAY_SPAWN_CHANCE = 0.5f;

    @SubscribeEvent
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        World world = event.getWorld();
        if (world == null || world.isRemote) return;
        if (event.getResult() == Event.Result.DENY) return;
        if (!(event.getEntityLiving() instanceof IMob)) return;
        if (event.isSpawner()) return;

        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (!world.isBlockLoaded(pos)) return;
        if (!isEerie(world.getBiome(pos))) return;

        if (!isAllowedEerieMob(event.getEntityLiving())) {
            event.setResult(Event.Result.DENY);
            return;
        }

        if (!world.isDaytime()) return;
        if (!ModConfig.enableEerieDayMobSpawns) return;

        if (world.rand.nextFloat() < DAY_SPAWN_CHANCE) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof IMob)) return;

        World world = event.getEntityLiving().world;
        if (world == null || world.isRemote || !world.isDaytime()) return;
        if (!ModConfig.enableEerieDayMobSpawns) return;

        BlockPos pos = event.getEntityLiving().getPosition();
        if (!world.isBlockLoaded(pos)) return;
        if (!isEerie(world.getBiome(pos))) return;
        if (!isAllowedEerieMob(event.getEntityLiving())) return;

        if (event.getEntityLiving().isBurning()) {
            event.getEntityLiving().extinguish();
        }
    }

    private static boolean isEerie(Biome biome) {
        if (biome == null) return false;
        if (biome == BiomeHandler.EERIE) return true;

        int eerieId = Biome.getIdForBiome(BiomeHandler.EERIE);
        int biomeId = Biome.getIdForBiome(biome);
        if (eerieId >= 0 && biomeId == eerieId) return true;

        ResourceLocation key = biome.getRegistryName();
        return key != null && "thaumcraft".equals(key.getResourceDomain()) && "eerie".equals(key.getResourcePath());
    }

    private static boolean isAllowedEerieMob(EntityLivingBase e) {
        return e instanceof EntitySkeleton
            || e instanceof EntitySkeletonAngry
            || e instanceof EntityZombie
            || e instanceof EntityZombieVillager
            || e instanceof EntityBrainyZombie
            || e instanceof EntityGiantBrainyZombie
            || e instanceof EntityWitch
            || e instanceof EntityVampireBat;
    }
}
