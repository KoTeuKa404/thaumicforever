package com.koteuka404.thaumicforever.focus;

import java.util.ArrayList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMod;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusModTimeCooldown extends FocusMod {

    private static final Queue<PendingCast> PENDING = new ConcurrentLinkedQueue<>();
    private static volatile boolean LISTENER_REGISTERED = false;

    private boolean suppressImmediate = false;

    private static class PendingCast {
        int dimension;
        long executeAtTick;
        FocusPackage remaining;
        Trajectory[] trajectories;
        RayTraceResult[] targets;
        UUID casterUuid;
    }

    @Override
    public String getResearch() {
        return "TIMEBOMB";
    }

    @Override
    public String getKey() {
        return "thaumicforever.TIMEBOMB";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MOTION;
    }

    @Override
    public int getComplexity() {
        return 5 + getDelaySeconds() / 2;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting(
                "delay",
                "focus.thaumicforever.timecooldown.delay",
                new NodeSetting.NodeSettingIntList(
                    new int[] {2, 4, 6, 8},
                    new String[] {
                        "focus.thaumicforever.timecooldown.delay.2",
                        "focus.thaumicforever.timecooldown.delay.4",
                        "focus.thaumicforever.timecooldown.delay.6",
                        "focus.thaumicforever.timecooldown.delay.8"
                    }
                )
            )
        };
    }

    @Override
    public EnumSupplyType[] mustBeSupplied() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public RayTraceResult[] supplyTargets() {
        if (suppressImmediate) return null;
        return getParent() != null ? getParent().supplyTargets() : null;
    }

    @Override
    public Trajectory[] supplyTrajectories() {
        if (suppressImmediate) return null;
        return getParent() != null ? getParent().supplyTrajectories() : null;
    }

    @Override
    public boolean execute() {
        suppressImmediate = false;

        FocusPackage pack = getPackage();
        if (pack == null) return false;

        EntityLivingBase caster = pack.getCaster();
        if (caster == null || caster.world == null) return false;
        World world = caster.world;

        if (world.isRemote) {
            suppressImmediate = true;
            return true;
        }

        FocusPackage remaining = getRemainingPackage();
        if (remaining == null) {
            return true;
        }

        ensureListener();

        PendingCast pc = new PendingCast();
        pc.dimension = world.provider.getDimension();
        pc.executeAtTick = world.getTotalWorldTime() + getDelaySeconds() * 20L;
        pc.remaining = remaining;
        pc.trajectories = copyTrajectories(getParent() != null ? getParent().supplyTrajectories() : null);
        pc.targets = copyTargets(getParent() != null ? getParent().supplyTargets() : null);
        pc.casterUuid = caster.getUniqueID();

        PENDING.add(pc);

        // Stop current package progression; delayed part will run later.
        suppressImmediate = true;
        return true;
    }

    private int getDelaySeconds() {
        int v = getSettingValue("delay");
        if (v == 2 || v == 4 || v == 6 || v == 8) return v;
        return 2;
    }

    private static Trajectory[] copyTrajectories(Trajectory[] src) {
        if (src == null) return null;
        Trajectory[] out = new Trajectory[src.length];
        System.arraycopy(src, 0, out, 0, src.length);
        return out;
    }

    private static RayTraceResult[] copyTargets(RayTraceResult[] src) {
        if (src == null) return null;
        RayTraceResult[] out = new RayTraceResult[src.length];
        System.arraycopy(src, 0, out, 0, src.length);
        return out;
    }

    private static void ensureListener() {
        if (LISTENER_REGISTERED) return;
        synchronized (FocusModTimeCooldown.class) {
            if (LISTENER_REGISTERED) return;
            MinecraftForge.EVENT_BUS.register(new FocusModTimeCooldownTickHandler());
            LISTENER_REGISTERED = true;
        }
    }

    private static class FocusModTimeCooldownTickHandler {
        @SubscribeEvent
        public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.world == null || event.world.isRemote) return;

            World world = event.world;
            long now = world.getTotalWorldTime();
            int dim = world.provider.getDimension();

            ArrayList<PendingCast> toRemove = new ArrayList<>();
            for (PendingCast pc : PENDING) {
                if (pc == null) {
                    toRemove.add(pc);
                    continue;
                }
                if (pc.dimension != dim || pc.executeAtTick > now) continue;
                if (pc.remaining == null) {
                    toRemove.add(pc);
                    continue;
                }

                EntityLivingBase caster = pc.remaining.getCaster();
                if (caster == null || !caster.isEntityAlive() || caster.world == null || caster.world.provider.getDimension() != dim) {
                    toRemove.add(pc);
                    continue;
                }

                pc.remaining.setUniqueID(UUID.randomUUID());
                FocusEngine.runFocusPackage(pc.remaining, pc.trajectories, pc.targets);
                toRemove.add(pc);
            }

            for (PendingCast pc : toRemove) {
                PENDING.remove(pc);
            }
        }
    }
}
