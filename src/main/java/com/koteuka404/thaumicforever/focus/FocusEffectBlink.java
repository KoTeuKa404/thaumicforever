package com.koteuka404.thaumicforever.focus;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectBlink extends FocusEffect {

    private static final Map<UUID, Long> CAST_TRIGGERED = new ConcurrentHashMap<>();
    private static final long CAST_TTL_MS = 15000L;

    @Override
    public String getResearch() {
        return "BLINK";
    }

    @Override
    public String getKey() {
        return "thaumicforever.BLINK";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MOTION;
    }

    @Override
    public int getComplexity() {
        return 8;
    }

    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target == null || getPackage() == null) return false;

        EntityLivingBase caster = getPackage().getCaster();
        if (caster == null) return false;

        World world = caster.world;
        if (world == null) return false;
        if (world.isRemote) return true;

        UUID castId = getPackage().getUniqueID();
        long now = System.currentTimeMillis();
        cleanupExpired(now);

        if (castId != null && CAST_TRIGGERED.containsKey(castId)) {
            return true;
        }

        Vec3d destination = resolveDestination(target);
        boolean teleported = teleportNear(caster, destination);

        if (teleported && castId != null) {
            CAST_TRIGGERED.put(castId, now);
        }

        return teleported;
    }

    private static Vec3d resolveDestination(RayTraceResult target) {
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            Entity e = target.entityHit;
            return new Vec3d(e.posX, e.posY + 0.15D, e.posZ);
        }

        Vec3d base = target.hitVec != null ? target.hitVec : Vec3d.ZERO;
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            EnumFacing side = target.sideHit != null ? target.sideHit : EnumFacing.UP;
            BlockPos hitPos = target.getBlockPos();
            if (hitPos != null) {
                BlockPos outPos = hitPos.offset(side);
                // Target the center of the adjacent block space, not the face itself.
                return new Vec3d(outPos.getX() + 0.5D, outPos.getY() + 0.1D, outPos.getZ() + 0.5D);
            }
            return base.addVector(side.getFrontOffsetX() * 1.15D, side.getFrontOffsetY() * 1.15D + 0.1D, side.getFrontOffsetZ() * 1.15D);
        }
        return base;
    }

    private static boolean teleportNear(EntityLivingBase caster, Vec3d base) {
        double[] yOffsets = new double[] {0.0D, 0.5D, 1.0D, -0.5D, 1.5D, 2.0D, -1.0D};
        double[] radii = new double[] {0.0D, 0.6D, 1.2D, 1.8D, 2.4D, 3.0D};
        int[] angles = new int[] {0, 45, 90, 135, 180, 225, 270, 315};

        double fromX = caster.posX;
        double fromY = caster.posY;
        double fromZ = caster.posZ;

        for (double yOff : yOffsets) {
            for (double r : radii) {
                if (r == 0.0D) {
                    if (tryTeleport(caster, base.x, base.y + yOff, base.z, fromX, fromY, fromZ)) return true;
                    continue;
                }
                for (int deg : angles) {
                    double rad = Math.toRadians(deg);
                    double tx = base.x + Math.cos(rad) * r;
                    double tz = base.z + Math.sin(rad) * r;
                    if (tryTeleport(caster, tx, base.y + yOff, tz, fromX, fromY, fromZ)) return true;
                }
            }
        }
        return false;
    }

    private static boolean tryTeleport(EntityLivingBase caster, double x, double y, double z,
                                       double fromX, double fromY, double fromZ) {
        if (!caster.attemptTeleport(x, y, z)) return false;

        if (caster instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) caster;
            player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        }

        caster.fallDistance = 0.0F;
        caster.world.playSound(null, fromX, fromY, fromZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        caster.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
        return true;
    }

    private static void cleanupExpired(long now) {
        CAST_TRIGGERED.entrySet().removeIf(e -> now - e.getValue() > CAST_TTL_MS);
    }
}
