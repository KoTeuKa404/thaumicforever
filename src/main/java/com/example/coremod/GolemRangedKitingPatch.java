package com.example.coremod;

import java.lang.reflect.Field;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class GolemRangedKitingPatch {
    private static final String GOLEM_CLASS = "thaumcraft.common.golems.EntityThaumcraftGolem";

    private static Field fEntityHost;
    private static Field fRangedAttackEntityHost;
    private static Field fRangedAttackTime;
    private static Field fEntityMoveSpeed;
    private static Field fSeeTime;
    private static Field fAttackIntervalMin;
    private static Field fMaxRangedAttackTime;
    private static Field fAttackRadius;
    private static Field fMaxAttackDistance;
    private static boolean reflectReady;
    private static boolean reflectInitTried;

    private GolemRangedKitingPatch() {}

    public static void updateTask(Object aiObj) {
        if (!ensureReflection(aiObj)) {
            return;
        }

        try {
            EntityLiving host = (EntityLiving) fEntityHost.get(aiObj);
            if (host == null) {
                return;
            }
            EntityLivingBase target = host.getAttackTarget();
            if (target == null) {
                return;
            }

            IRangedAttackMob rangedHost = (IRangedAttackMob) fRangedAttackEntityHost.get(aiObj);
            int rangedAttackTime = fRangedAttackTime.getInt(aiObj);
            double moveSpeed = fEntityMoveSpeed.getDouble(aiObj);
            int seeTime = fSeeTime.getInt(aiObj);
            int attackIntervalMin = fAttackIntervalMin.getInt(aiObj);
            int maxRangedAttackTime = fMaxRangedAttackTime.getInt(aiObj);
            float attackRadius = fAttackRadius.getFloat(aiObj);
            float maxAttackDistance = fMaxAttackDistance.getFloat(aiObj);

            double distanceSq = host.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
            boolean canSee = host.getEntitySenses().canSee(target);

            if (canSee) {
                seeTime++;
            } else {
                seeTime = 0;
            }
            fSeeTime.setInt(aiObj, seeTime);

            PathNavigate nav = host.getNavigator();
            boolean isGolemArcher = GOLEM_CLASS.equals(host.getClass().getName())
                    || host.getClass().getName().endsWith(".EntityThaumcraftGolem");
            double kiteThresholdSq = maxAttackDistance * 0.60D;

            if (distanceSq <= maxAttackDistance && seeTime >= 20) {
                if (isGolemArcher && distanceSq < kiteThresholdSq) {
                    Vec3d away = new Vec3d(host.posX - target.posX, 0.0D, host.posZ - target.posZ);
                    if (away.lengthSquared() < 0.0001D) {
                        away = new Vec3d((host.getRNG().nextDouble() - 0.5D), 0.0D, (host.getRNG().nextDouble() - 0.5D));
                    }
                    away = away.normalize();
                    double step = 4.0D;
                    double destX = host.posX + away.x * step;
                    double destY = host.posY;
                    double destZ = host.posZ + away.z * step;
                    nav.tryMoveToXYZ(destX, destY, destZ, moveSpeed * 1.1D);
                } else {
                    nav.clearPath();
                }
            } else {
                nav.tryMoveToEntityLiving(target, moveSpeed);
            }

            host.getLookHelper().setLookPositionWithEntity(target, 10.0F, 30.0F);

            rangedAttackTime--;
            if (rangedAttackTime == 0) {
                if (distanceSq > maxAttackDistance || !canSee) {
                    fRangedAttackTime.setInt(aiObj, rangedAttackTime);
                    return;
                }

                float distanceNorm = MathHelper.sqrt(distanceSq) / attackRadius;
                float strength = MathHelper.clamp(distanceNorm, 0.1F, 1.0F);
                rangedHost.attackEntityWithRangedAttack(target, strength);
                rangedAttackTime = MathHelper.floor(distanceNorm * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
            } else if (rangedAttackTime < 0) {
                float distanceNorm = MathHelper.sqrt(distanceSq) / attackRadius;
                rangedAttackTime = MathHelper.floor(distanceNorm * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
            }

            fRangedAttackTime.setInt(aiObj, rangedAttackTime);
        } catch (Throwable ignored) {
        }
    }

    private static boolean ensureReflection(Object aiObj) {
        if (reflectReady) {
            return true;
        }
        if (reflectInitTried) {
            return false;
        }
        reflectInitTried = true;

        try {
            Class<?> c = aiObj.getClass();
            fEntityHost = findFieldByNames(c, "entityHost");
            fRangedAttackEntityHost = findFieldByNames(c, "rangedAttackEntityHost");
            fRangedAttackTime = findFieldByNames(c, "rangedAttackTime");
            fEntityMoveSpeed = findFieldByNames(c, "entityMoveSpeed");
            fSeeTime = findFieldByNames(c, "seeTime");
            fAttackIntervalMin = findFieldByNames(c, "attackIntervalMin");
            fMaxRangedAttackTime = findFieldByNames(c, "maxRangedAttackTime");
            fAttackRadius = findFieldByNames(c, "attackRadius");
            fMaxAttackDistance = findFieldByNames(c, "maxAttackDistance");

            if (fEntityHost == null || fRangedAttackEntityHost == null ||
                fRangedAttackTime == null || fEntityMoveSpeed == null ||
                fSeeTime == null || fAttackIntervalMin == null ||
                fMaxRangedAttackTime == null || fAttackRadius == null ||
                fMaxAttackDistance == null) {
                // Fallback for obfuscated/renamed fields (server).
                assignFieldsByTypeOrder(c);
            }

            fEntityHost.setAccessible(true);
            fRangedAttackEntityHost.setAccessible(true);
            fRangedAttackTime.setAccessible(true);
            fEntityMoveSpeed.setAccessible(true);
            fSeeTime.setAccessible(true);
            fAttackIntervalMin.setAccessible(true);
            fMaxRangedAttackTime.setAccessible(true);
            fAttackRadius.setAccessible(true);
            fMaxAttackDistance.setAccessible(true);

            reflectReady = true;
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Field findFieldByNames(Class<?> c, String... names) {
        if (names == null) return null;
        for (String n : names) {
            try {
                Field f = c.getDeclaredField(n);
                return f;
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static void assignFieldsByTypeOrder(Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        java.util.List<Field> ints = new java.util.ArrayList<>();
        java.util.List<Field> floats = new java.util.ArrayList<>();

        for (Field f : fields) {
            Class<?> t = f.getType();
            if (fEntityHost == null && EntityLiving.class.isAssignableFrom(t)) {
                fEntityHost = f;
                continue;
            }
            if (fRangedAttackEntityHost == null && IRangedAttackMob.class.isAssignableFrom(t)) {
                fRangedAttackEntityHost = f;
                continue;
            }
            if (fEntityMoveSpeed == null && (t == Double.TYPE || t == Double.class)) {
                fEntityMoveSpeed = f;
                continue;
            }
            if (t == Integer.TYPE || t == Integer.class) {
                ints.add(f);
                continue;
            }
            if (t == Float.TYPE || t == Float.class) {
                floats.add(f);
            }
        }

        if (fRangedAttackTime == null && ints.size() > 0) fRangedAttackTime = ints.get(0);
        if (fSeeTime == null && ints.size() > 1) fSeeTime = ints.get(1);
        if (fAttackIntervalMin == null && ints.size() > 2) fAttackIntervalMin = ints.get(2);
        if (fMaxRangedAttackTime == null && ints.size() > 3) fMaxRangedAttackTime = ints.get(3);
        if (fAttackRadius == null && floats.size() > 0) fAttackRadius = floats.get(0);
        if (fMaxAttackDistance == null && floats.size() > 1) fMaxAttackDistance = floats.get(1);
    }
}
