package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectChainLightning extends FocusEffect {

    private static final int MAX_JUMPS = 5;
    private static final double RADIUS = 3.0D;

    @Override
    public String getResearch() {
        return "CHAINFLIGHT";
    }

    @Override
    public String getKey() {
        return "thaumicforever.CHAINLIGHTNING";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.ENERGY;
    }

    @Override
    public int getComplexity() {
        return 8;
    }

    @Override
    public void renderParticleFX(World world, double x, double y, double z, double dx, double dy, double dz) {
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target == null) return false;

        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit instanceof EntityLivingBase) {
            EntityLivingBase initial = (EntityLivingBase) target.entityHit;
            World world = initial.world;
            Set<EntityLivingBase> hit = new HashSet<>();
            hit.add(initial);
            chainJump(initial, world, finalPower, 0, hit);
            return true;
        }

        return false;
    }

    private void chainJump(EntityLivingBase current, World world, float power, int jumps, Set<EntityLivingBase> hit) {
        current.attackEntityFrom(DamageSource.MAGIC, 5 * power);

        if (jumps >= MAX_JUMPS) return;

        List<EntityLivingBase> possibleTargets = world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                new AxisAlignedBB(
                        current.posX - RADIUS, current.posY - RADIUS, current.posZ - RADIUS,
                        current.posX + RADIUS, current.posY + RADIUS, current.posZ + RADIUS
                ),
                e -> !hit.contains(e) && e.isEntityAlive()
        );

        if (!possibleTargets.isEmpty()) {
            EntityLivingBase next = possibleTargets.get(0);
            hit.add(next);

            if (!world.isRemote) { 
                spawnLightningFX(world, current, next);
            }

            chainJump(next, world, power, jumps + 1, hit);
        }
    }

    private void spawnLightningFX(World world, EntityLivingBase from, EntityLivingBase to) {
    if (world.isRemote) {
        double x1 = from.posX;
        double y1 = from.posY + from.height * 0.5;
        double z1 = from.posZ;
        double x2 = to.posX;
        double y2 = to.posY + to.height * 0.5;
        double z2 = to.posZ;

        int steps = 12;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double px = x1 + (x2 - x1) * t;
            double py = y1 + (y2 - y1) * t;
            double pz = z1 + (z2 - z1) * t;
            world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 0, 0, 0);
        }
    }
}

}
