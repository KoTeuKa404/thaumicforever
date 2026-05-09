package com.koteuka404.thaumicforever.focus;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import com.koteuka404.thaumicforever.network.PacketSkyBeamFX;
import com.koteuka404.thaumicforever.registry.AspectRegistry;
import com.koteuka404.thaumicforever.ThaumicForever;

public class FocusMediumSkyBeam extends FocusMedium {
    private static final int DEFAULT_BEAM_HOLD_TICKS = 80; // 4 seconds
    private static final int EFFECT_INTERVAL_TICKS = 8;
    private static final int MIN_CAST_COOLDOWN_TICKS = 10; // 0.5 second safety floor
    private static final Queue<HeldBeam> HELD_BEAMS = new ConcurrentLinkedQueue<>();
    private static volatile boolean TICKER_REGISTERED = false;

    private static class HeldBeam {
        int dim;
        double x1, y1, z1;
        double dx, dy, dz;
        double range;
        double lastX2, lastY2, lastZ2;
        long expireAt;
        long nextEmit;
        UUID casterUuid;
        FocusPackage remainingTemplate;
    }

    private static class HitData {
        final RayTraceResult target;
        final Vec3d hitVec;

        HitData(RayTraceResult target, Vec3d hitVec) {
            this.target = target;
            this.hitVec = hitVec;
        }
    }

    private final List<Trajectory> outTrajectories = new ArrayList<>();
    private final List<RayTraceResult> outTargets = new ArrayList<>();

    @Override
    public String getResearch() {
        return "FOCUSBEAM";
    }

    @Override
    public String getKey() {
        return "thaumicforever.SKYBEAM";
    }

    @Override
    public Aspect getAspect() {
        return AspectRegistry.WEATHER;
    }

    @Override
    public int getComplexity() {
        int duration = getSettingValue("duration");
        int durationTier = Math.max(0, (duration - 4) / 2); // 4/6/8/10s -> 0/1/2/3
        return 6 + getSettingValue("range") / 3 + durationTier;
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY };
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        if (trajectory == null || trajectory.source == null || trajectory.direction == null) return false;
        if (getPackage() == null || getPackage().world == null) return false;
        outTrajectories.clear();
        outTargets.clear();

        World world = getPackage().world;
        Vec3d start = trajectory.source;
        Vec3d dir = trajectory.direction.normalize();
        if (dir.lengthVector() < 1.0E-4D) return false;

        double range = getSettingValue("range");
        int holdSeconds = getSettingValue("duration");
        if (holdSeconds <= 0) holdSeconds = DEFAULT_BEAM_HOLD_TICKS / 20;
        int holdTicks = holdSeconds * 20;
        int castCooldownTicks = Math.max(MIN_CAST_COOLDOWN_TICKS, holdTicks - 20);
        EntityLivingBase caster = getPackage().getCaster();
        if (!world.isRemote && caster != null) {
            long now = world.getTotalWorldTime();
            long nextCast = caster.getEntityData().getLong("thaumicforever.skybeam.next_cast");
            if (now < nextCast) return false;
            caster.getEntityData().setLong("thaumicforever.skybeam.next_cast", now + castCooldownTicks);
        }
        HitData hit = computeHitData(world, caster, start, dir, range);
        if (hit == null) return false;
        RayTraceResult closestHit = hit.target;
        Vec3d hitVec = hit.hitVec;

        Vec3d outDir = hitVec.subtract(start);
        if (outDir.lengthVector() < 1.0E-4D) outDir = dir;
        else outDir = outDir.normalize();
        outTrajectories.add(new Trajectory(start, outDir));
        outTargets.add(closestHit);

        sendBeamFX(world, start, hitVec, 6);
        holdBeam(
            world,
            start,
            hitVec,
            dir,
            range,
            holdTicks,
            getPackage().getCasterUUID(),
            getRemainingPackage()
        );
        return true;
    }

    @Override
    public boolean hasIntermediary() {
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] duration = { 4, 6, 8, 10 };
        String[] durationDesc = { "4s", "6s", "8s", "10s" };
        return new NodeSetting[] {
            new NodeSetting("range", "focus.common.range", new NodeSetting.NodeSettingIntRange(8, 24)),
            new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntList(duration, durationDesc))
        };
    }

    @Override
    public float getPowerMultiplier() {
        return 1.0f;
    }

    @Override
    public RayTraceResult[] supplyTargets() {
        if (outTargets.isEmpty()) return null;
        return outTargets.toArray(new RayTraceResult[0]);
    }

    @Override
    public Trajectory[] supplyTrajectories() {
        if (outTrajectories.isEmpty()) return null;
        return outTrajectories.toArray(new Trajectory[0]);
    }

    private void sendBeamFX(World world, Vec3d start, Vec3d end, int lifeTicks) {
        if (world.isRemote) return;
        double cx = (start.x + end.x) * 0.5D;
        double cy = (start.y + end.y) * 0.5D;
        double cz = (start.z + end.z) * 0.5D;
        ThaumicForever.network.sendToAllAround(
            new PacketSkyBeamFX(start.x, start.y, start.z, end.x, end.y, end.z, lifeTicks, 0xAEE6FF, 0.12f),
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), cx, cy, cz, 64.0D)
        );
    }

    private static HitData computeHitData(World world, EntityLivingBase caster, Vec3d start, Vec3d dir, double range) {
        if (world == null || start == null || dir == null) return null;
        Vec3d ndir = dir.normalize();
        if (ndir.lengthVector() < 1.0E-4D) return null;
        Vec3d end = start.addVector(ndir.x * range, ndir.y * range, ndir.z * range);

        RayTraceResult blockHit = world.rayTraceBlocks(start, end, false, true, false);
        RayTraceResult closestHit = blockHit;
        Vec3d hitVec = blockHit != null && blockHit.hitVec != null ? blockHit.hitVec : end;
        double bestDistSq = start.squareDistanceTo(hitVec);

        AxisAlignedBB scanBox = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z).grow(1.0D);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, scanBox);
        for (EntityLivingBase entity : entities) {
            if (entity == null || !entity.isEntityAlive() || entity == caster) continue;

            AxisAlignedBB ebox = entity.getEntityBoundingBox().grow(0.3D);
            RayTraceResult intercept = ebox.calculateIntercept(start, end);
            if (intercept == null || intercept.hitVec == null) continue;

            double distSq = start.squareDistanceTo(intercept.hitVec);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                hitVec = intercept.hitVec;
                closestHit = new RayTraceResult(entity, intercept.hitVec);
            }
        }

        if (closestHit == null || hitVec == null) {
            BlockPos endPos = new BlockPos(end);
            closestHit = new RayTraceResult(end, EnumFacing.UP, endPos);
            hitVec = end;
        }
        return new HitData(closestHit, hitVec);
    }

    private static void ensureTicker() {
        if (TICKER_REGISTERED) return;
        synchronized (FocusMediumSkyBeam.class) {
            if (TICKER_REGISTERED) return;
            MinecraftForge.EVENT_BUS.register(new BeamTicker());
            TICKER_REGISTERED = true;
        }
    }

    private void holdBeam(
        World world,
        Vec3d start,
        Vec3d end,
        Vec3d dir,
        double range,
        int durationTicks,
        UUID casterUuid,
        FocusPackage remainingTemplate
    ) {
        if (world.isRemote) return;
        ensureTicker();
        HeldBeam hb = new HeldBeam();
        hb.dim = world.provider.getDimension();
        hb.x1 = start.x;
        hb.y1 = start.y;
        hb.z1 = start.z;
        hb.dx = dir.x;
        hb.dy = dir.y;
        hb.dz = dir.z;
        hb.range = range;
        hb.lastX2 = end.x;
        hb.lastY2 = end.y;
        hb.lastZ2 = end.z;
        hb.nextEmit = world.getTotalWorldTime();
        hb.expireAt = hb.nextEmit + Math.max(1, durationTicks);
        hb.casterUuid = casterUuid;
        hb.remainingTemplate = remainingTemplate;
        HELD_BEAMS.add(hb);
    }

    private static class BeamTicker {
        @SubscribeEvent
        public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.world == null || event.world.isRemote) return;
            long now = event.world.getTotalWorldTime();
            int dim = event.world.provider.getDimension();

            List<HeldBeam> remove = new ArrayList<>();
            for (HeldBeam hb : HELD_BEAMS) {
                if (hb == null || hb.dim != dim) continue;
                if (now > hb.expireAt) {
                    remove.add(hb);
                    continue;
                }
                if (now < hb.nextEmit) continue;

                Vec3d start = new Vec3d(hb.x1, hb.y1, hb.z1);
                Vec3d dir = new Vec3d(hb.dx, hb.dy, hb.dz).normalize();
                Vec3d end = new Vec3d(hb.lastX2, hb.lastY2, hb.lastZ2);

                // Execute the next focus stage every tick while the beam is active.
                if (hb.remainingTemplate != null && hb.casterUuid != null) {
                    EntityLivingBase caster = event.world.getPlayerEntityByUUID(hb.casterUuid);
                    if (caster == null) {
                        List<EntityLivingBase> living = event.world.getEntities(EntityLivingBase.class, e -> e != null && e.isEntityAlive());
                        for (EntityLivingBase e : living) {
                            if (hb.casterUuid.equals(e.getUniqueID())) {
                                caster = e;
                                break;
                            }
                        }
                    }
                    if (caster == null || !caster.isEntityAlive()) {
                        remove.add(hb);
                        continue;
                    }

                    HitData hit = computeHitData(event.world, caster, start, dir, hb.range);
                    if (hit != null && hit.hitVec != null) {
                        end = hit.hitVec;
                        hb.lastX2 = end.x;
                        hb.lastY2 = end.y;
                        hb.lastZ2 = end.z;

                        Vec3d outDir = end.subtract(start);
                        if (outDir.lengthVector() < 1.0E-4D) outDir = dir;
                        else outDir = outDir.normalize();
                        Trajectory[] trajectories = new Trajectory[] { new Trajectory(start, outDir) };
                        RayTraceResult[] targets = new RayTraceResult[] { hit.target };

                        FocusPackage tickPackage = hb.remainingTemplate.copy(caster);
                        tickPackage.initialize(caster);
                        tickPackage.setUniqueID(UUID.randomUUID());
                        if (tickPackage.getFocusEffects() != null) {
                            for (thaumcraft.api.casters.FocusEffect fx : tickPackage.getFocusEffects()) {
                                if (fx != null) fx.onCast(caster);
                            }
                        }
                        FocusEngine.runFocusPackage(tickPackage, trajectories, targets);
                    }
                }

                double cx = (start.x + end.x) * 0.5D;
                double cy = (start.y + end.y) * 0.5D;
                double cz = (start.z + end.z) * 0.5D;

                ThaumicForever.network.sendToAllAround(
                    new PacketSkyBeamFX(start.x, start.y, start.z, end.x, end.y, end.z, 10, 0xAEE6FF, 0.12f),
                    new NetworkRegistry.TargetPoint(dim, cx, cy, cz, 64.0D)
                );

                hb.nextEmit = now + EFFECT_INTERVAL_TICKS;
            }

            for (HeldBeam hb : remove) {
                HELD_BEAMS.remove(hb);
            }
        }
    }
}
