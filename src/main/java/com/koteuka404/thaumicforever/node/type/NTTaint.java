package com.koteuka404.thaumicforever.node.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.koteuka404.thaumicforever.entity.EntityAuraNode;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;

public class NTTaint extends NTNormal {
    private static final float FLUX_CHANCE    = 0.03f;
    private static final float SEED_CHANCE    = 0.015f;
    private static final float CORRUPT_CHANCE = 0.05f;

    private static final String ROTA_MODID = "thaumrota";

    private static final boolean ONLY_ROTA_TAINT_BIOMES = false;

    private static List<Biome> cachedTaintBiomes;

    public NTTaint(int id) {
        super(id);
    }

    @Override
    public void performPeriodicEvent(EntityAuraNode node) {
        super.performPeriodicEvent(node);

        World world = node.world;

        if (world.isRemote) {
            return;
        }

        BlockPos pos = node.getPosition();
        Random rand = world.rand;

        float flux = AuraHandler.getFlux(world, pos);
        float base = Math.max(1.0f, AuraHandler.getAuraBase(world, pos));
        float fluxFrac = clamp(flux / base, 0.0f, 1.0f);

        if (rand.nextFloat() < FLUX_CHANCE * (1.0f - fluxFrac)) {
            int amount = Math.max(1, (int) (super.calculateStrength(node) * 0.2f));
            AuraHandler.addFlux(world, pos, amount);
        }

        if (flux > 60.0f && rand.nextFloat() < SEED_CHANCE) {
            EntityTaintSeed seed = new EntityTaintSeed(world);
            seed.setPosition(node.posX + 0.5, node.posY + 1.0, node.posZ + 0.5);
            world.spawnEntity(seed);
        }

        if (rand.nextFloat() < CORRUPT_CHANCE) {
            corruptNearbyBiome(world, node, rand);
        }
    }

    private static void corruptNearbyBiome(World world, EntityAuraNode node, Random rand) {
        List<Biome> taintBiomes = getTaintBiomes();

        if (taintBiomes.isEmpty()) {
            return;
        }

        BlockPos origin = node.getPosition();

        int radius = 3 + (int) Math.sqrt(Math.max(1.0, node.getNodeSize()));
        int attempts = Math.max(6, radius * 2);

        for (int i = 0; i < attempts; i++) {
            double angle = rand.nextDouble() * Math.PI * 2.0;
            int distance = 1 + rand.nextInt(Math.max(1, radius));

            int dx = (int) Math.round(Math.cos(angle) * distance);
            int dz = (int) Math.round(Math.sin(angle) * distance);

            BlockPos target = origin.add(dx, 0, dz);

            if (!world.isBlockLoaded(target)) {
                continue;
            }

            Biome current = world.getBiome(target);

            if (isTaintBiome(current)) {
                continue;
            }

            Biome taintBiome = pickBestTaintBiomeFor(world, target, taintBiomes, rand);

            if (taintBiome != null && current != taintBiome) {
                Utils.setBiomeAt(world, target, taintBiome);
                return;
            }
        }
    }

    private static Biome pickBestTaintBiomeFor(World world, BlockPos pos, List<Biome> taintBiomes, Random rand) {
        if (taintBiomes.isEmpty()) {
            return null;
        }

        boolean waterLike = world.getHeight(pos).getY() <= world.getSeaLevel();

        List<Biome> preferred = new ArrayList<>();

        for (Biome biome : taintBiomes) {
            ResourceLocation id = biome.getRegistryName();

            String path = id == null
                    ? ""
                    : id.getResourcePath().toLowerCase(Locale.ROOT);

            if (waterLike) {
                if (path.contains("sea") || path.contains("ocean")) {
                    preferred.add(biome);
                }
            } else {
                if (!path.contains("sea")
                        && !path.contains("ocean")
                        && !path.contains("beach")
                        && !path.contains("edge")) {
                    preferred.add(biome);
                }
            }
        }

        List<Biome> pool = preferred.isEmpty() ? taintBiomes : preferred;
        return pool.get(rand.nextInt(pool.size()));
    }

    private static List<Biome> getTaintBiomes() {
        if (cachedTaintBiomes != null) {
            return cachedTaintBiomes;
        }

        List<Biome> result = new ArrayList<>();

        for (Biome biome : ForgeRegistries.BIOMES) {
            if (isTaintBiome(biome)) {
                result.add(biome);
            }
        }

        cachedTaintBiomes = Collections.unmodifiableList(result);
        return cachedTaintBiomes;
    }

    private static boolean isTaintBiome(Biome biome) {
        if (biome == null) {
            return false;
        }

        ResourceLocation id = biome.getRegistryName();

        String domain = id == null
                ? ""
                : id.getResourceDomain().toLowerCase(Locale.ROOT);

        String path = id == null
                ? ""
                : id.getResourcePath().toLowerCase(Locale.ROOT);

        if (ONLY_ROTA_TAINT_BIOMES && !ROTA_MODID.equals(domain)) {
            return false;
        }

        if (hasBiomeType(biome, "TAINT")
                || hasBiomeType(biome, "TAINT_R")
                || hasBiomeType(biome, "TAINT_R_L")
                || hasBiomeType(biome, "TAINT_R_L_SEA")
                || hasBiomeType(biome, "TAINT_R_EDGE")) {
            return true;
        }

        return path.contains("taint")
                || path.contains("tainted");
    }

    private static boolean hasBiomeType(Biome biome, String typeName) {
        try {
            return BiomeDictionary.hasType(biome, BiomeDictionary.Type.getType(typeName));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public int calculateStrength(EntityAuraNode node) {
        return Math.max(1, super.calculateStrength(node));
    }

    @Override
    public void performTickEvent(EntityAuraNode node) {
    }
}
