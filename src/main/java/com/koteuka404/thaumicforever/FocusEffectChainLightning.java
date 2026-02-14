package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectChainLightning extends FocusEffect {

    private static final float BASE_DAMAGE = 2.0F;
    private static final int   BASE_JUMPS  = 2;
    private static final double RADIUS     = 3.0D;

    @Override
    public String getResearch() {
        return "CHAINLIGHTNING";
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
        int power = getSettingValue("power"); // 1..5
        return 4 + power * 2;
    }


    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("power", "focus.common.power",
                    new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }


    @Override
    public float getDamageForDisplay(float finalPower) {
        return getDamagePerHit(finalPower);
    }

    private float getDamagePerHit(float finalPower) {
        int powerSetting = getSettingValue("power"); // 1..5
        return (BASE_DAMAGE + powerSetting) * finalPower;
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

            if (world.isRemote) {
                return true;
            }

            Set<EntityLivingBase> hit = new HashSet<>();
            hit.add(initial);

            int powerSetting = getSettingValue("power"); // 1..5
            int maxJumps = BASE_JUMPS + powerSetting;

            chainJump(initial, world, finalPower, 0, maxJumps, hit);
            return true;
        }

        return false;
    }

    private void chainJump(EntityLivingBase current, World world,
                            float finalPower, int jumps, int maxJumps,
                            Set<EntityLivingBase> hit) {
        if (!current.isEntityAlive()) return;

        current.attackEntityFrom(DamageSource.MAGIC, getDamagePerHit(finalPower));

        if (jumps >= maxJumps) return;

        List<EntityLivingBase> possibleTargets = world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            new AxisAlignedBB(
                current.posX - RADIUS, current.posY - RADIUS, current.posZ - RADIUS,
                current.posX + RADIUS, current.posY + RADIUS, current.posZ + RADIUS
            ),
            e -> !hit.contains(e) && e.isEntityAlive()
        );

        if (!possibleTargets.isEmpty()) {
            EntityLivingBase next = possibleTargets.stream()
                .min((a, b) -> Double.compare(a.getDistanceSq(current), b.getDistanceSq(current)))
                .orElse(possibleTargets.get(0));

            hit.add(next);

            sendLightningPacket(world, current, next);

            chainJump(next, world, finalPower, jumps + 1, maxJumps, hit);
        }
    }

    private void sendLightningPacket(World world, EntityLivingBase from, EntityLivingBase to) {
        if (world.isRemote) return;

        double x1 = from.posX;
        double y1 = from.posY + from.height * 0.5;
        double z1 = from.posZ;
        double x2 = to.posX;
        double y2 = to.posY + to.height * 0.5;
        double z2 = to.posZ;

        double cx = (x1 + x2) * 0.5;
        double cy = (y1 + y2) * 0.5;
        double cz = (z1 + z2) * 0.5;

        if (world.rand.nextFloat() < 0.30f) {
            double midX = (x1 + x2) / 2.0;
            double midY = (y1 + y2) / 2.0;
            double midZ = (z1 + z2) / 2.0;
            double off  = 0.5 + world.rand.nextDouble() * 0.5;
            midX += (world.rand.nextDouble() - 0.5) * off;
            midY += (world.rand.nextDouble() - 0.5) * off;
            midZ += (world.rand.nextDouble() - 0.5) * off;

            ThaumicForever.network.sendToAllAround(
                new PacketLightningFX(x1, y1, z1, x2, y2, z2, true, midX, midY, midZ),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), cx, cy, cz, 48)
            );
        } else {
            ThaumicForever.network.sendToAllAround(
                new PacketLightningFX(x1, y1, z1, x2, y2, z2),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), cx, cy, cz, 48)
            );
        }
    }

    @Override
    public void onCast(Entity caster) {
    }
}
