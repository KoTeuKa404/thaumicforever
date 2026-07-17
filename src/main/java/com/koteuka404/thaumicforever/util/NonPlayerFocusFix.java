package com.koteuka404.thaumicforever.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.items.casters.foci.FocusMediumBolt;
import thaumcraft.common.items.casters.foci.FocusMediumTouch;
import thaumcraft.common.lib.utils.EntityUtils;

/**
 * Makes Thaumcraft's instant ray-based media work when the caster is not a player.
 */
public final class NonPlayerFocusFix {
    private static final double BOLT_RANGE = 16.0D;
    private static final double TOUCH_RANGE = 4.0D;

    private NonPlayerFocusFix() {}

    public static void apply(FocusPackage focusPackage, EntityLivingBase caster) {
        if (focusPackage == null || caster == null) {
            return;
        }

        List<IFocusElement> nodes = focusPackage.nodes;
        for (int i = 0; i < nodes.size(); i++) {
            IFocusElement node = nodes.get(i);
            if (node != null && node.getClass() == FocusMediumBolt.class) {
                BoltCompat replacement = new BoltCompat();
                copySettings((FocusNode) node, replacement);
                nodes.set(i, replacement);
            } else if (node != null && node.getClass() == FocusMediumTouch.class) {
                TouchCompat replacement = new TouchCompat();
                copySettings((FocusNode) node, replacement);
                nodes.set(i, replacement);
            } else if (node instanceof FocusModSplit) {
                for (FocusPackage splitPackage : ((FocusModSplit) node).getSplitPackages()) {
                    splitPackage.setCasterUUID(caster.getUniqueID());
                    splitPackage.world = caster.world;
                    apply(splitPackage, caster);
                }
            } else if (node instanceof FocusPackage) {
                FocusPackage nestedPackage = (FocusPackage) node;
                nestedPackage.setCasterUUID(caster.getUniqueID());
                nestedPackage.world = caster.world;
                apply(nestedPackage, caster);
            }
        }
    }

    private static void copySettings(FocusNode source, FocusNode target) {
        for (String key : source.getSettingList()) {
            if (target.getSetting(key) != null) {
                target.getSetting(key).setValue(source.getSettingValue(key));
            }
        }
    }

    private static RayTraceResult[] findTargets(FocusNode node, double range) {
        if (node.getPackage() == null || node.getParent() == null) {
            return new RayTraceResult[0];
        }

        Trajectory[] parentTrajectories = node.getParent().supplyTrajectories();
        if (parentTrajectories == null || parentTrajectories.length == 0) {
            return new RayTraceResult[0];
        }

        List<RayTraceResult> targets = new ArrayList<>();
        for (Trajectory trajectory : parentTrajectories) {
            RayTraceResult ray = trace(node, trajectory, range);
            if (ray != null) {
                targets.add(ray);
            }
        }
        return targets.toArray(new RayTraceResult[targets.size()]);
    }

    private static Trajectory[] findTrajectories(FocusNode node, double range) {
        if (node.getPackage() == null || node.getParent() == null) {
            return new Trajectory[0];
        }

        Trajectory[] supplied = node.getParent().supplyTrajectories();
        if (supplied == null || supplied.length == 0) {
            return new Trajectory[0];
        }

        Trajectory[] result = new Trajectory[supplied.length];
        for (int i = 0; i < supplied.length; i++) {
            Trajectory trajectory = supplied[i];
            Vec3d direction = trajectory.direction.normalize();
            RayTraceResult ray = trace(node, trajectory, range);
            Vec3d end = trajectory.source.add(direction.scale(range));

            if (ray != null && ray.hitVec != null) {
                end = ray.hitVec;
            } else if (ray != null && ray.entityHit != null) {
                end = ray.entityHit.getPositionVector().addVector(0.0D, ray.entityHit.height * 0.5D, 0.0D);
            }

            result[i] = new Trajectory(end, direction);
        }
        return result;
    }

    private static RayTraceResult trace(FocusNode node, Trajectory trajectory, double range) {
        Vec3d direction = trajectory.direction.normalize();
        EntityLivingBase caster = node.getPackage().getCaster();
        RayTraceResult entityRay = EntityUtils.getPointedEntityRay(
                node.getPackage().world,
                caster,
                trajectory.source,
                direction,
                0.25D,
                range,
                0.25F,
                false);

        if (entityRay != null) {
            return entityRay;
        }

        Vec3d end = trajectory.source.add(direction.scale(range));
        return node.getPackage().world.rayTraceBlocks(trajectory.source, end);
    }

    private static final class BoltCompat extends FocusMediumBolt {
        @Override
        public RayTraceResult[] supplyTargets() {
            return findTargets(this, BOLT_RANGE);
        }

        @Override
        public Trajectory[] supplyTrajectories() {
            return findTrajectories(this, BOLT_RANGE);
        }
    }

    private static final class TouchCompat extends FocusMediumTouch {
        @Override
        public RayTraceResult[] supplyTargets() {
            return findTargets(this, TOUCH_RANGE);
        }

        @Override
        public Trajectory[] supplyTrajectories() {
            return findTrajectories(this, TOUCH_RANGE);
        }
    }
}
