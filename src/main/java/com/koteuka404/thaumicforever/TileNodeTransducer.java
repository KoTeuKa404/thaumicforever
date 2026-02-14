package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileNodeTransducer extends TileThaumcraft implements ITickable {

    public int count = 0;

    private enum Mode { IDLE, CONVERTING, ENERGIZED }

    private static final int SEARCH_COOLDOWN = 20;
    private static final int CONVERT_TIME    = 200;
    private static final int PUSH_RANGE      = 8;
    private static final float BOOM_STRENGTH = 2.0f;

    private static final int BURST_PERIOD_TICKS = 400;

    private static final double OUTPUT_SCALE  = 0.20; 
    private static final int    OUTPUT_PERIOD = 1;    

    private Mode mode = Mode.IDLE;

    private long lastProcessedTick = -1;
    private long convertStartedAt  = -1;
    private long lastFxAt          = -1;
    private long lastSearchAt      = -1;
    private long lastPushAt        = -1;

    private final Map<Aspect, Double> fracCarry = new HashMap<>();

    private AspectList cvPerTickAspects = new AspectList();

    private EntityAuraNode cachedNode = null;
    private int searchCooldown = 0;

    @Override
    public void update() {
        if (world == null) return;

        long now = world.getTotalWorldTime();

        if (world.isRemote) {
            boolean powered = world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0;
            int target = powered ? 37 : 0;
            if (count < target) count++;
            else if (count > target) count--;
            return;
        }

        if (lastProcessedTick == now) return;
        lastProcessedTick = now;

        boolean powered = world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0;

        if (searchCooldown <= 0 || (now - lastSearchAt) >= SEARCH_COOLDOWN) {
            searchCooldown = SEARCH_COOLDOWN;
            lastSearchAt = now;
            cachedNode = findNodeBelow();
        }

        if (powered && cachedNode != null && !cachedNode.isDead) {
            if (lastFxAt < 0 || (now - lastFxAt) >= 6) {
                lastFxAt = now;
                sendBoltsChargerNodeStabilizer();
            }
        }

        switch (mode) {
            case IDLE: {
                if (powered && cachedNode != null && !cachedNode.isDead) {
                    if (cachedNode.isTfCharged()) {
                        if (cvPerTickAspects == null || cvPerTickAspects.size() == 0) {
                            cvPerTickAspects = cachedNode.getOriginalAspects().copy();
                        }
                        cachedNode.stablized = true;
                        cachedNode.canEatNodesWhileStabilized = false;
                        mode = Mode.ENERGIZED;
                        markDirty();
                        break;
                    }

                    mode = Mode.CONVERTING;
                    convertStartedAt = now;
                    cachedNode.stablized = true;
                    cachedNode.canEatNodesWhileStabilized = false;
                    markDirty();
                }
                break;
            }

            case CONVERTING: {
                if (!powered || cachedNode == null || cachedNode.isDead) {
                    mode = Mode.IDLE;
                    convertStartedAt = -1;
                    markDirty();
                    break;
                }

                if (convertStartedAt < 0) convertStartedAt = now;
                long elapsed = now - convertStartedAt;
                if (elapsed >= CONVERT_TIME) {
                    AspectList before = cachedNode.getNodeAspects().copy();
                    AspectList primals = convertToPrimalsExact(before);
                    cvPerTickAspects = sqrtFloor(primals);

                    cachedNode.getNodeAspects().aspects.clear();
                    cachedNode.getOriginalAspects().aspects.clear();
                    for (Aspect p : cvPerTickAspects.getAspects()) {
                        int cap = cvPerTickAspects.getAmount(p);
                        if (cap > 0) {
                            cachedNode.getOriginalAspects().add(p, cap);
                            cachedNode.getNodeAspects().add(p, 1);
                            cachedNode.addAspectToOrderIfMissing(p);
                        }
                    }
                    java.util.List<Aspect> order =
                        new java.util.ArrayList<>(java.util.Arrays.asList(cvPerTickAspects.getAspectsSortedByAmount()));
                    cachedNode.setFixedAspectOrder(order.isEmpty() ? new java.util.ArrayList<>() : order);

                    cachedNode.updateSyncAspects();

                    cachedNode.stablized = true;
                    cachedNode.canEatNodesWhileStabilized = false;
                    cachedNode.setTfCharged(true);

                    mode = Mode.ENERGIZED;
                    convertStartedAt = -1;
                    markDirty();
                    tryPlayClick();
                }
                break;
            }

            case ENERGIZED: {
                if (!powered || cachedNode == null || cachedNode.isDead) {
                    mode = Mode.IDLE;
                    if (cachedNode != null) {
                        cachedNode.setTfCharged(false);
                        cachedNode.stablized = false;
                        cachedNode.canEatNodesWhileStabilized = false;
                        cachedNode.updateSyncAspects();
                    }
                    markDirty();
                    tryPlayClick();
                    break;
                }

                cachedNode.stablized = true;
                cachedNode.canEatNodesWhileStabilized = false;

                if (OUTPUT_PERIOD <= 1 || lastPushAt < 0 || (now - lastPushAt) >= (OUTPUT_PERIOD - 1)) {
                    lastPushAt = now;
                    pushCvPerTickScaled(cvPerTickAspects);
                }

                if (lastFxAt < 0 || (now - lastFxAt) >= 10) {
                    lastFxAt = now;
                    sendBoltsChargerNodeStabilizer();
                }
                break;
            }
        }
    }

    private void pushCvPerTickScaled(AspectList perTick) {
        if (perTick == null || perTick.size() == 0) return;

        AspectList scaled = new AspectList();
        for (Aspect a : perTick.getAspects()) {
            int base = perTick.getAmount(a);
            if (base <= 0) continue;

            double acc  = fracCarry.getOrDefault(a, 0.0);
            double want = base * OUTPUT_SCALE + acc;  
            int send    = (int)Math.floor(want);
            fracCarry.put(a, want - send);             

            if (send > 0) scaled.add(a, send);
        }

        if (scaled.size() == 0) return;
        pushCvPerTick(scaled); 
    }

    private static final int INIT_PORT_DY = 3;
    private static final int INIT_PORT_SCAN_COOLDOWN = 20;

    private BlockPos cachedInitPort = null;
    private long lastInitPortScan = -1;

    private void pushCvPerTick(AspectList perTick) {
        if (perTick == null || perTick.size() == 0) return;
    
        TilePort target = resolveInitialPort();
        if (target == null) return;
    
        for (Aspect a : perTick.getAspects()) {
            int cv = perTick.getAmount(a);
            if (cv > 0) {
                target.acceptCentivis(a, cv, this);
            }
        }
    }
    @javax.annotation.Nullable
    private TilePort resolveInitialPort() {
        if (world == null) return null;
        long now = world.getTotalWorldTime();
    
        if (cachedInitPort != null && (now - lastInitPortScan) < INIT_PORT_SCAN_COOLDOWN) {
            TileEntity te = world.getTileEntity(cachedInitPort);
            if (te instanceof TilePort && isPortEligible((TilePort) te)) {
                return (TilePort) te;
            }
        }
    
        lastInitPortScan = now;
    
        BlockPos p = pos.down(INIT_PORT_DY);
        TileEntity te = world.getTileEntity(p);
        if (te instanceof TilePort && isPortEligible((TilePort) te)) {
            cachedInitPort = p;
            return (TilePort) te;
        }
    
        cachedInitPort = null;
        return null;
    }
    
    private boolean isPortEligible(TilePort port) {
        TileEntity above = world.getTileEntity(port.getPos().up());
        return (above instanceof TileBuffNodeStabilizer);
    }
        

    private AspectList convertToPrimalsExact(AspectList src) {
        Map<Aspect, Integer> acc = new HashMap<>();
        for (Aspect a : src.getAspects()) {
            int amt = Math.max(0, src.getAmount(a));
            if (amt <= 0) continue;
            decomposeToPrimals(a, amt, acc);
        }
        AspectList out = new AspectList();
        for (Map.Entry<Aspect, Integer> e : acc.entrySet()) {
            if (e.getValue() > 0) out.add(e.getKey(), e.getValue());
        }
        return out;
    }

    private void decomposeToPrimals(Aspect a, int amt, Map<Aspect, Integer> acc) {
        if (amt <= 0) return;
        if (a.isPrimal()) {
            acc.put(a, acc.getOrDefault(a, 0) + amt);
            return;
        }
        Aspect[] comps = a.getComponents();
        if (comps == null || comps.length == 0) {
            acc.put(a, acc.getOrDefault(a, 0) + amt);
            return;
        }
        if (comps.length == 1) {
            decomposeToPrimals(comps[0], amt, acc);
            return;
        }
        int a1 = amt / 2;
        int a2 = amt - a1;
        decomposeToPrimals(comps[0], a1, acc);
        decomposeToPrimals(comps[1], a2, acc);
    }

    private AspectList sqrtFloor(AspectList primals) {
        AspectList out = new AspectList();
        for (Aspect p : primals.getAspects()) {
            int amt = primals.getAmount(p);
            if (amt > 0) {
                int root = (int)Math.floor(Math.sqrt(amt));
                if (root > 0) out.add(p, root);
            }
        }
        return out;
    }

    public interface ICentivisAcceptorAspect {
        int acceptCentivis(Aspect aspect, int amount, TileNodeTransducer source);
    }
    public interface ICentivisAcceptor {
        int acceptCentivis(int amount, TileNodeTransducer source);
    }

    private EntityAuraNode findNodeBelow() {
        BlockPos below = pos.down();
        double cx = below.getX() + 0.5;
        double cy = below.getY() + 0.5;
        double cz = below.getZ() + 0.5;
        List<Entity> list = EntityUtils.getEntitiesInRange(world, cx, cy, cz, null, EntityAuraNode.class, 0.6);
        if (list != null) for (Entity e : list) if (e instanceof EntityAuraNode && !e.isDead) return (EntityAuraNode)e;
        return null;
    }

    private void sendBoltsChargerNodeStabilizer() {
        if (world.isRemote || cachedNode == null || cachedNode.isDead) return;

        double tx = pos.getX() + 0.5;
        double ty = pos.getY() + 1.0;
        double tz = pos.getZ() + 0.5;

        double nx = cachedNode.posX;
        double ny = cachedNode.posY;
        double nz = cachedNode.posZ;

        double sx = nx;
        double sy = ny - 1.0;
        double sz = nz;

        java.util.function.BiConsumer<double[], double[]> sendBent = (start, end) -> {
            double x1 = start[0], y1 = start[1], z1 = start[2];
            double x2 = end[0],   y2 = end[1],   z2 = end[2];

            final boolean BEND = world.rand.nextFloat() < 0.30f;
            if (BEND) {
                double midX = (x1 + x2) * 0.5;
                double midY = (y1 + y2) * 0.5;
                double midZ = (z1 + z2) * 0.5;

                ThaumicForever.network.sendToAllAround(
                    new PacketLightningFX(x1, y1, z1, x2, y2, z2, true, midX, midY, midZ),
                    new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(
                        world.provider.getDimension(), x1, y1, z1, 32
                    )
                );
            } else {
                ThaumicForever.network.sendToAllAround(
                    new PacketLightningFX(x1, y1, z1, x2, y2, z2),
                    new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(
                        world.provider.getDimension(), x1, y1, z1, 32
                    )
                );
            }
        };

        sendBent.accept(new double[]{tx, ty, tz}, new double[]{nx, ny, nz});
        sendBent.accept(new double[]{nx, ny, nz}, new double[]{sx, sy, sz});
    }

    public void onTransducerBroken() {
        if (!world.isRemote && mode == Mode.ENERGIZED) {
            world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, BOOM_STRENGTH, false);
        int total = 0;
            for (Aspect a : cvPerTickAspects.getAspects()) total += cvPerTickAspects.getAmount(a);
            if (total <= 0) total = 25;
            AuraHelper.polluteAura(world, pos, Math.max(25, total * 2), true);
        }
    }

    public void onStabilizerBroken() {
        if (!world.isRemote && mode == Mode.ENERGIZED) {
            world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, BOOM_STRENGTH, false);
            int total = 0;
            for (Aspect a : cvPerTickAspects.getAspects()) total += cvPerTickAspects.getAmount(a);
            if (total <= 0) total = 25;
            AuraHelper.polluteAura(world, pos, Math.max(25, total * 2), true);
        }
    }

    private void tryPlayClick() {
        try {
            world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.4f, 1.4f);
        } catch (Throwable ignored) {}
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("mode", mode.ordinal());
        tag.setInteger("count", count);
        tag.setLong("lastProcessed", lastProcessedTick);
        tag.setLong("convertStartedAt", convertStartedAt);
        tag.setLong("lastFxAt", lastFxAt);
        tag.setLong("lastSearchAt", lastSearchAt);
        NBTTagCompound caps = new NBTTagCompound();
        cvPerTickAspects.writeToNBT(caps);
        tag.setTag("cvPerTickAspects", caps);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("mode", Constants.NBT.TAG_INT)) {
            int m = tag.getInteger("mode");
            if (m < 0 || m >= Mode.values().length) m = 0;
            mode = Mode.values()[m];
        } else mode = Mode.IDLE;

        count = tag.getInteger("count");
        lastProcessedTick = tag.getLong("lastProcessed");
        convertStartedAt   = tag.getLong("convertStartedAt");
        lastFxAt           = tag.getLong("lastFxAt");
        lastSearchAt       = tag.getLong("lastSearchAt");

        if (tag.hasKey("cvPerTickAspects", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound caps = tag.getCompoundTag("cvPerTickAspects");
            cvPerTickAspects.readFromNBT(caps);
        } else {
            cvPerTickAspects = new AspectList();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
            pos.getX() - 0.3, pos.getY() - 0.3, pos.getZ() - 0.3,
            pos.getX() + 1.3, pos.getY() + 1.3, pos.getZ() + 1.3
        );
    }

    public boolean isEnergized() { return mode == Mode.ENERGIZED; }
    public boolean isConverting(){ return mode == Mode.CONVERTING; }
}
