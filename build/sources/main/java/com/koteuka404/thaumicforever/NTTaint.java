package com.koteuka404.thaumicforever;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;


public class NTTaint extends NTNormal {
    private static final float FLUX_CHANCE    = 0.03f;
    private static final float SEED_CHANCE    = 0.015f;
    private static final float CORRUPT_CHANCE = 0.05f;

    private static final String ROTA = "returnoftheancients";
    private static final String BIOME_CLASS  = "com.artur.returnoftheancients.init.InitBiome";
    private static final String BIOME_FIELD  = "TAINT";

    public NTTaint(int id) {
        super(id);
    }

    @Override
    void performPeriodicEvent(EntityAuraNode node) {
        super.performPeriodicEvent(node);

        World world = node.world;
        BlockPos pos = node.getPosition();
        Random rand = world.rand;

        float flux = AuraHandler.getFlux(world, pos);
        float base = AuraHandler.getAuraBase(world, pos);
        float fluxFrac = flux / base;

        if (rand.nextFloat() < FLUX_CHANCE * (1f - fluxFrac)) {
            int amount = Math.max(1, (int)(super.calculateStrength(node) * 0.2f));
            AuraHandler.addFlux(world, pos, amount);
        }

        if (!world.isRemote && flux > 60 && rand.nextFloat() < SEED_CHANCE) {
            EntityTaintSeed seed = new EntityTaintSeed(world);
            seed.setPosition(node.posX + 0.5, node.posY + 1.0, node.posZ + 0.5);
            world.spawnEntity(seed);
        }

        if (Loader.isModLoaded(ROTA) && rand.nextFloat() < CORRUPT_CHANCE) {
            try {
                Class<?> clz = Class.forName(BIOME_CLASS);
                Field f = clz.getField(BIOME_FIELD);
                Biome taintBiome = (Biome)f.get(null);

                int deg = rand.nextInt(360);
                Vec3d orig = new Vec3d(node.posX, node.posY, node.posZ);
                int radius = (int)(3.0 + Math.sqrt(node.getNodeSize()));

                for (int d = 0; d < radius; d++) {
                    Vec3d off = new Vec3d(d, 0, 0)
                        .rotateYaw((float)Math.toRadians(deg));
                    BlockPos tgt = new BlockPos(orig.x + off.x, orig.y + off.y, orig.z + off.z);
                    if (world.getBiome(tgt) != taintBiome) {
                        Utils.setBiomeAt(world, tgt, taintBiome);
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
    }


    @Override
    int calculateStrength(EntityAuraNode node) {
        return Math.max(1, super.calculateStrength(node));
    }

    @Override
    void performTickEvent(EntityAuraNode node) {
    }
}
