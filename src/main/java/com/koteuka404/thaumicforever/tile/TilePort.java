package com.koteuka404.thaumicforever.tile;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.koteuka404.thaumicforever.wand.tile.TileArcaneWorkbenchNew;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import com.koteuka404.thaumicforever.block.PortBlock;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

public class TilePort extends TileEntity implements ITickable,
        IAspectContainer, IEssentiaTransport,
        TileNodeTransducer.ICentivisAcceptorAspect {

    private final AspectList dummyBuffer = new AspectList();
    private int transferCooldown = 0;

    private static final int MAX_COLOR_DEPTH = 16;
    private static final int CHARGER_LINGER_TICKS = 60;

    // CV: 100 CV = 1 
    private final Map<Aspect,Integer> cvBuffer = new HashMap<>();
    private final Map<Aspect,Integer> essentiaCvBuffer = new HashMap<>();
    private static final int MAX_BUFFER_PER_ASPECT_CV = 2000; // 20 
    private static final int MAX_ESSENTIA_BUFFER_PER_ASPECT_CV = 2000;
    private static final int CONVERT_CHUNK_CV         = 100;  // 100 CV -> 1 
    private static final int CV_STRENGTH_DIVISOR      = 1;

    private static final int MAX_CHAIN_DEPTH   = 32; 
    private static final int FORWARD_PER_TICK_CV = 50; 
    private static final boolean CHECK_LOS_FOR_LINKS = true; 
    private static final int MIN_NODE_ASPECT = 2;

    private BlockPos targetPort = null;
    private BlockPos sourcePort = null;
    private BlockPos targetCharger = null;
    private int cachedColor = 0x6600E5;
    private Aspect lastCvAspect = null;

    private long lastProcessedTick = -1;
    private long lastCvAt = -1;

    private static final int CHARGER_SCAN_COOLDOWN = 20;
    private static final int CHARGER_SCAN_RANGE = 4;
    private BlockPos cachedChargerPos = null;
    private long lastChargerScan = -1;

    @Override
    public void update() {
        if (world.isRemote) return;

        long now = world.getTotalWorldTime();
        if (lastProcessedTick == now) return;
        lastProcessedTick = now;

        int color = computeBeamColorRecursive(0);
        if (this.cachedColor != color) {
            this.cachedColor = color;
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }

        refreshChargerTarget(now);

        if (!cvBuffer.isEmpty() && hasDirectCvReceiverAbove()) {
            flushCvUp();
        }

        if (!cvBuffer.isEmpty() && getChargerTarget() == null && !hasStabilizerAbove() && !hasDirectCvReceiverAbove()) {
            flushCvDown();
        }

        if (targetPort != null && !targetPort.equals(getPos()) && forwardCvExistingToTarget()) {
            transferCooldown = Math.max(transferCooldown, 1);
        }

        if (transferCooldown > 0) {
            transferCooldown--;
            return;
        }

        if (isUp()) {
            TileEntity teAbove = world.getTileEntity(pos.up());
        
            if (teAbove instanceof TilePort) {
                TilePort above = (TilePort) teAbove;
        
                BlockPos tp = above.getTargetPort();
                if (tp != null && tp.equals(above.getPos())) {
                    above.setTargetPort(null);
                    tp = null;
                }
        
                if (tp != null && tp.equals(this.getPos())) {
                    if (above.transferAspectToPort(this)) {
                        transferCooldown = 5;
                        return; 
                    }
                }
            }
            else if (teAbove instanceof TileBuffNodeStabilizer) {
                TileBuffNodeStabilizer stab = (TileBuffNodeStabilizer) teAbove;
                EntityAuraNode node = stab.getFirstNode();
                if (node != null && !node.isTfCharged()) {
                    if (targetPort != null && !targetPort.equals(getPos())) {
                        TileEntity teTarget = world.getTileEntity(targetPort);
                        if (teTarget instanceof TilePort) {
                            if (transferAspectToPort((TilePort) teTarget)) {
                                transferCooldown = 5;
                                return;
                            }
                        }
                    }
                }
            }
        }
        
    }

    private boolean isUp() {
        if (world == null) return false;
        IBlockState st = world.getBlockState(pos);
        return (st.getBlock() instanceof PortBlock)
                && st.getValue(PortBlock.FACING) == EnumFacing.DOWN;
    }

    // -------------------- CV relay API --------------------

    @Override
    public int acceptCentivis(Aspect aspect, int amount, TileNodeTransducer source) {
        if (aspect == null || amount <= 0) return 0;

        TileEntity up = world.getTileEntity(pos.up());
        if (up instanceof TileNodeTransducer.ICentivisAcceptorAspect && !(up instanceof TileBuffNodeStabilizer)) {
            int accepted = ((TileNodeTransducer.ICentivisAcceptorAspect) up).acceptCentivis(aspect, scaleCentivis(amount), source);
            return toAcceptedInput(amount, accepted);
        }
        if (!(up instanceof TileBuffNodeStabilizer)) {
            return 0;
        }
        return relayCentivis(aspect, amount, 0);
    }


    public int relayCentivis(Aspect aspect, int amount, int depth) {
        if (amount <= 0) return 0;

        if (depth >= MAX_CHAIN_DEPTH || targetPort == null || targetPort.equals(getPos())) {
            return acceptCentivisLocal(aspect, amount);
        }

        TileEntity te = world.getTileEntity(targetPort);
        if (te instanceof TilePort && !te.isInvalid()) {
            int acceptedDownstream = ((TilePort) te).relayCentivis(aspect, amount, depth + 1);
            if (acceptedDownstream > 0) {
                return acceptedDownstream;
            }
        }

        return acceptCentivisLocal(aspect, amount);
    }

    private int acceptCentivisLocal(Aspect aspect, int amount) {
        int accepted = tryPassCentivisNow(aspect, amount);
        if (accepted <= 0) return 0;

        cachedColor = aspect.getColor();
        lastCvAspect = aspect;
        lastCvAt = world.getTotalWorldTime();
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return accepted;
    }

    private int tryPassCentivisNow(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0 || world == null) return 0;

        TileEntity teAbove = world.getTileEntity(pos.up());
        if (teAbove instanceof TileNodeTransducer.ICentivisAcceptorAspect && !(teAbove instanceof TileBuffNodeStabilizer)) {
            int accepted = ((TileNodeTransducer.ICentivisAcceptorAspect) teAbove).acceptCentivis(aspect, scaleCentivis(amount), null);
            return toAcceptedInput(amount, accepted);
        }

        int effectiveAmount = scaleCentivis(amount);

        int charged = tryPassCentivisToCharger(aspect, effectiveAmount);
        if (charged > 0) return toAcceptedInput(amount, charged);

        if (!hasStabilizerAbove()) {
            int outputBelow = tryOutputCvToBelow(aspect, effectiveAmount);
            if (outputBelow > 0) return toAcceptedInput(amount, outputBelow);
        }

        int pedestalCharged = tryPassCentivisToLocalPedestal(aspect, effectiveAmount);
        if (pedestalCharged > 0) return toAcceptedInput(amount, pedestalCharged);

        return 0;
    }

    private int scaleCentivis(int amount) {
        if (amount <= 0) return 0;
        return Math.max(1, (amount + CV_STRENGTH_DIVISOR - 1) / CV_STRENGTH_DIVISOR);
    }

    private int toAcceptedInput(int originalAmount, int acceptedEffectiveAmount) {
        if (originalAmount <= 0 || acceptedEffectiveAmount <= 0) return 0;
        return Math.min(originalAmount, acceptedEffectiveAmount * CV_STRENGTH_DIVISOR);
    }

    private int tryPassCentivisToCharger(Aspect aspect, int amount) {
        BlockPos chargerPos = getChargerTarget();
        if (chargerPos == null || !isChargerBlock(chargerPos)) return 0;

        TileEntity te = world.getTileEntity(chargerPos.down());
        if (!(te instanceof TileArcaneWorkbenchNew)) return 0;
        return ((TileArcaneWorkbenchNew) te).acceptPrimalCentivis(aspect, amount);
    }

    private int tryPassCentivisToLocalPedestal(Aspect aspect, int amount) {
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (teAbove instanceof TileRechargePedestal && !teAbove.isInvalid()) {
            return ((TileRechargePedestal) teAbove).acceptPrimalCentivis(aspect, amount);
        }

        TileEntity teBelow = world.getTileEntity(pos.down());
        if (teBelow instanceof TileRechargePedestal && !teBelow.isInvalid()) {
            return ((TileRechargePedestal) teBelow).acceptPrimalCentivis(aspect, amount);
        }

        return 0;
    }

    private void flushCvUp() {
        if (cvBuffer.isEmpty()) return;
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (!(teAbove instanceof TileNodeTransducer.ICentivisAcceptorAspect) || teAbove instanceof TileBuffNodeStabilizer) return;

        TileNodeTransducer.ICentivisAcceptorAspect receiver = (TileNodeTransducer.ICentivisAcceptorAspect) teAbove;
        Iterator<Map.Entry<Aspect,Integer>> it = cvBuffer.entrySet().iterator();
        boolean any = false;
        while (it.hasNext()) {
            Map.Entry<Aspect,Integer> e = it.next();
            Aspect aspect = e.getKey();
            int have = e.getValue();
            if (aspect == null || have <= 0) {
                it.remove();
                continue;
            }

            int accepted = receiver.acceptCentivis(aspect, Math.min(have, FORWARD_PER_TICK_CV), null);
            if (accepted <= 0) continue;

            have -= accepted;
            any = true;
            if (have <= 0) {
                it.remove();
            } else {
                e.setValue(have);
            }
        }

        if (any) {
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    private boolean forwardCvExistingToTarget() {
        TileEntity teTarget = world.getTileEntity(targetPort);
        if (!(teTarget instanceof TilePort) || teTarget.isInvalid()) return false;
        TilePort tgt = (TilePort) teTarget;

        if (CHECK_LOS_FOR_LINKS) {
            double sx = this.pos.getX() + 0.5, sy = this.pos.getY() + 0.5, sz = this.pos.getZ() + 0.5;
            double tx = tgt.pos.getX() + 0.5, ty = tgt.pos.getY() + 0.5, tz = tgt.pos.getZ() + 0.5;
            if (!hasLineOfSight(sx, sy, sz, tx, ty, tz)) return false;
        }

        if (cvBuffer.isEmpty()) return false;

        boolean any = false;
        Iterator<Map.Entry<Aspect,Integer>> it = cvBuffer.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Aspect,Integer> e = it.next();
            Aspect a = e.getKey();
            int have = e.getValue();
            if (have <= 0) { it.remove(); continue; }
            if (!canSendFromNode(a)) continue;

            int toSend = Math.min(have, FORWARD_PER_TICK_CV);
            if (toSend <= 0) continue;

            int accepted = tgt.acceptCentivis(a, toSend, null);
            if (accepted > 0) {
                have -= accepted;
                any = true;
                if (have <= 0) it.remove(); else e.setValue(have);
            }
        }

        if (any) {
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return any;
    }

    private void flushCvDown() {
        if (cvBuffer.isEmpty()) return;

        TileEntity teBelow = world.getTileEntity(pos.down());
        IEssentiaTransport transport = (teBelow instanceof IEssentiaTransport) ? (IEssentiaTransport) teBelow : null;
        IAspectContainer  container  = (teBelow instanceof IAspectContainer)  ? (IAspectContainer)  teBelow : null;

        Iterator<Map.Entry<Aspect,Integer>> it = cvBuffer.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Aspect,Integer> e = it.next();
            Aspect a = e.getKey();
            int cv  = e.getValue();
            boolean changed = false;

            if (!canSendFromNode(a)) continue;
            while (cv >= CONVERT_CHUNK_CV) {
                boolean moved = false;
                if (transport != null && transport.canInputFrom(EnumFacing.UP)) {
                    int m = transport.addEssentia(a, 1, EnumFacing.UP);
                    if (m > 0) { cv -= CONVERT_CHUNK_CV; moved = true; changed = true; }
                } else if (container != null && container.doesContainerAccept(a)) {
                    int m = container.addToContainer(a, 1);
                    if (m > 0) { cv -= CONVERT_CHUNK_CV; moved = true; changed = true; }
                }
                if (!moved) break; 
            }

            if (cv <= 0) it.remove();
            else if (changed) e.setValue(cv);
        }
    }

    private int tryOutputCvToBelow(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0 || !canSendFromNode(aspect)) return 0;

        TileEntity teBelow = world.getTileEntity(pos.down());
        IEssentiaTransport transport = (teBelow instanceof IEssentiaTransport) ? (IEssentiaTransport) teBelow : null;
        IAspectContainer container = (teBelow instanceof IAspectContainer) ? (IAspectContainer) teBelow : null;
        if (transport == null && container == null) return 0;

        int current = essentiaCvBuffer.getOrDefault(aspect, 0);
        int room = Math.max(0, MAX_ESSENTIA_BUFFER_PER_ASPECT_CV - current);
        int acceptedInput = Math.min(amount, room);
        if (acceptedInput <= 0) return 0;

        int buffered = current + acceptedInput;
        int acceptedCv = 0;
        while (buffered >= CONVERT_CHUNK_CV) {
            boolean moved = false;
            if (transport != null && transport.canInputFrom(EnumFacing.UP)) {
                int m = transport.addEssentia(aspect, 1, EnumFacing.UP);
                moved = m > 0;
            } else if (container != null && container.doesContainerAccept(aspect)) {
                int m = container.addToContainer(aspect, 1);
                moved = m > 0;
            }

            if (!moved) break;
            buffered -= CONVERT_CHUNK_CV;
            acceptedCv += CONVERT_CHUNK_CV;
        }

        if (buffered > 0) {
            essentiaCvBuffer.put(aspect, buffered);
        } else {
            essentiaCvBuffer.remove(aspect);
        }

        if (acceptedInput > 0 || acceptedCv > 0) {
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return acceptedInput;
    }

    private void refreshChargerTarget(long now) {
        if (world == null) return;

        if (targetCharger == null && isLocalPedestalEndpoint()) {
            if (cachedChargerPos != null) {
                cachedChargerPos = null;
                markDirty();
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
            return;
        }

        if (cachedChargerPos != null && !isChargerBlock(cachedChargerPos)) {
            cachedChargerPos = null;
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }

        if (cachedChargerPos != null && (now - lastChargerScan) < CHARGER_SCAN_COOLDOWN) {
            if (isChargerBlock(cachedChargerPos) && isNearestAutoPortForCharger(cachedChargerPos)) return;
            cachedChargerPos = null;
        }

        if ((now - lastChargerScan) < CHARGER_SCAN_COOLDOWN) return;
        lastChargerScan = now;

        BlockPos found = null;
        double best = Double.MAX_VALUE;

        int r = CHARGER_SCAN_RANGE;
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (!isChargerBlock(scan)) continue;
                    if (!isNearestAutoPortForCharger(scan)) continue;
                    double d = pos.distanceSq(scan);
                    if (d < best) {
                        best = d;
                        found = scan.toImmutable();
                    }
                }
            }
        }

        if (found == null && cachedChargerPos == null) return;
        if (found != null && found.equals(cachedChargerPos)) return;
        if (found == null && cachedChargerPos == null) return;

        cachedChargerPos = found;
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    private boolean isChargerBlock(BlockPos p) {
        if (p == null || world == null) return false;
        IBlockState state = world.getBlockState(p);
        if (state == null || state.getBlock() != ModBlocks.ARCANE_WORKBENCH_WAND_CHARGER) return false;
        TileEntity below = world.getTileEntity(p.down());
        return below instanceof TileArcaneWorkbenchNew;
    }

    private boolean isNearestAutoPortForCharger(BlockPos chargerPos) {
        if (world == null || chargerPos == null || targetCharger != null) return true;
        if (isLocalPedestalEndpoint()) return false;

        double myDist = pos.distanceSq(chargerPos);
        int r = CHARGER_SCAN_RANGE;
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(chargerPos.getX() + dx, chargerPos.getY() + dy, chargerPos.getZ() + dz);
                    TileEntity te = world.getTileEntity(scan);
                    if (!(te instanceof TilePort) || te == this || te.isInvalid()) continue;

                    TilePort other = (TilePort) te;
                    if (other.targetCharger != null || other.isLocalPedestalEndpoint()) continue;

                    double otherDist = other.getPos().distanceSq(chargerPos);
                    if (otherDist < myDist || (otherDist == myDist && isTieBreakerBefore(other.getPos(), this.pos))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isLocalPedestalEndpoint() {
        if (world == null) return false;

        TileEntity teAbove = world.getTileEntity(pos.up());
        if (teAbove instanceof TileRechargePedestal && !teAbove.isInvalid()) return true;

        TileEntity teBelow = world.getTileEntity(pos.down());
        return teBelow instanceof TileRechargePedestal && !teBelow.isInvalid();
    }


    private boolean transferAspectToPort(TilePort target) {
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (!(teAbove instanceof TileBuffNodeStabilizer)) return false;
        TileBuffNodeStabilizer stab = (TileBuffNodeStabilizer) teAbove;
        EntityAuraNode node = stab.getFirstNode();
        if (node == null || node.isTfCharged()) return false;

        List<TilePort> route = buildRouteTo(target);
        AspectList aspects = node.getNodeAspects();
        for (Aspect aspect : aspects.getAspectsSortedByAmount()) {
            int amt = aspects.getAmount(aspect);
            if (amt < MIN_NODE_ASPECT) continue;
            int moved = tryOutputAlongRouteBackwards(route, aspect, 1);
            if (moved > 0) {
                aspects.reduce(aspect, moved);
                node.updateSyncAspects();
                markDirty();
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                return true;
            }
        }
        return false;
    }

    private List<TilePort> buildRouteTo(TilePort target) {
        List<TilePort> forwardRoute = buildForwardRouteTo(target);
        List<TilePort> reverseRoute = buildRouteFromTargetSources(target);
        return reverseRoute.size() > forwardRoute.size() ? reverseRoute : forwardRoute;
    }

    private List<TilePort> buildForwardRouteTo(TilePort target) {
        List<TilePort> route = new ArrayList<>();
        if (target == null || world == null) return route;
        route.add(this);
        HashSet<BlockPos> visited = new HashSet<>();
        visited.add(this.getPos());

        TilePort current = this;
        for (int depth = 0; depth < MAX_CHAIN_DEPTH; depth++) {
            BlockPos next = current.getTargetPort();
            if (next == null || next.equals(current.getPos()) || !visited.add(next)) break;

            TileEntity te = world.getTileEntity(next);
            if (!(te instanceof TilePort) || te.isInvalid()) break;

            TilePort nextPort = (TilePort) te;
            route.add(nextPort);
            current = nextPort;
        }

        if (!routeContains(route, target.getPos())) {
            route.add(target);
        }
        return route;
    }

    private List<TilePort> buildRouteFromTargetSources(TilePort target) {
        List<TilePort> reversed = new ArrayList<>();
        if (target == null || world == null) return reversed;

        HashSet<BlockPos> visited = new HashSet<>();
        TilePort current = target;
        for (int depth = 0; depth < MAX_CHAIN_DEPTH && current != null; depth++) {
            if (!visited.add(current.getPos())) break;
            reversed.add(current);
            if (current.getPos().equals(this.getPos())) break;

            BlockPos prev = current.getSourcePort();
            if (prev == null || prev.equals(current.getPos())) break;
            TileEntity te = world.getTileEntity(prev);
            if (!(te instanceof TilePort) || te.isInvalid()) break;
            current = (TilePort) te;
        }

        if (reversed.isEmpty() || !reversed.get(reversed.size() - 1).getPos().equals(this.getPos())) {
            return new ArrayList<>();
        }

        List<TilePort> route = new ArrayList<>();
        for (int i = reversed.size() - 1; i >= 0; i--) {
            route.add(reversed.get(i));
        }
        return route;
    }

    private boolean routeContains(List<TilePort> route, BlockPos portPos) {
        if (route == null || portPos == null) return false;
        for (TilePort port : route) {
            if (port != null && portPos.equals(port.getPos())) {
                return true;
            }
        }
        return false;
    }

    private int tryOutputAlongRouteBackwards(List<TilePort> route, Aspect aspect, int amount) {
        if (route == null || route.isEmpty()) return 0;

        for (int i = route.size() - 1; i >= 0; i--) {
            TilePort port = route.get(i);
            if (port == null || port.isInvalid()) continue;

            int moved = port.tryOutputToBelow(aspect, amount);
            if (moved > 0) {
                return moved;
            }
        }
        return 0;
    }

    private boolean canSendFromNode(Aspect aspect) {
        if (aspect == null || world == null) return false;
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (!(teAbove instanceof TileBuffNodeStabilizer)) return true;
        EntityAuraNode node = ((TileBuffNodeStabilizer) teAbove).getFirstNode();
        if (node == null) return false;
        return node.getNodeAspects().getAmount(aspect) >= MIN_NODE_ASPECT;
    }

    private boolean transferAspectFromNodeToContainer(EntityAuraNode node, IAspectContainer container) {
        AspectList aspects = node.getNodeAspects();
        for (Aspect aspect : aspects.getAspectsSortedByAmount()) {
            int amt = aspects.getAmount(aspect);
            if (amt < MIN_NODE_ASPECT) continue;
            if (!container.doesContainerAccept(aspect)) continue;
            int added = container.addToContainer(aspect, 1);
            if (added > 0) {
                aspects.reduce(aspect, added);
                node.updateSyncAspects();
                markDirty();
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                return true;
            }
        }
        return false;
    }

    private boolean hasStabilizerAbove() {
        if (world == null) return false;
        return world.getTileEntity(pos.up()) instanceof TileBuffNodeStabilizer;
    }

    private boolean hasDirectCvReceiverAbove() {
        if (world == null) return false;
        TileEntity up = world.getTileEntity(pos.up());
        return up instanceof TileNodeTransducer.ICentivisAcceptorAspect && !(up instanceof TileBuffNodeStabilizer);
    }

    private boolean hasLineOfSight(double sx, double sy, double sz, double ex, double ey, double ez) {
        net.minecraft.util.math.Vec3d start = new net.minecraft.util.math.Vec3d(sx, sy, sz);
        net.minecraft.util.math.Vec3d end   = new net.minecraft.util.math.Vec3d(ex, ey, ez);
        // stopOnLiquid=false, ignoreBlockWithoutBoundingBox=false, returnLastUncollidableBlock=false
        return world.rayTraceBlocks(start, end, false, false, false) == null;
    }

    // -------------------- IAspectContainer stubs --------------------
    @Override public int addToContainer(Aspect a, int amount) { return 0; }
    @Override public boolean doesContainerAccept(Aspect a) { return false; }
    @Override public int containerContains(Aspect a) { return 0; }
    @Override public boolean takeFromContainer(AspectList al) { return false; }
    @Override public boolean takeFromContainer(Aspect a, int amt) { return false; }
    @Override public AspectList getAspects() { return dummyBuffer; }
    @Override public void setAspects(AspectList al) { }
    @Override public boolean doesContainerContainAmount(Aspect aspect, int amount) { return false; }
    @Override public boolean doesContainerContain(AspectList al) { return false; }

    // -------------------- IEssentiaTransport --------------------
    @Override
    public boolean isConnectable(EnumFacing face) {
        return true;
    }
    @Override public boolean canInputFrom(EnumFacing face)  { return false; } 
    @Override public boolean canOutputTo(EnumFacing face)   { return isUp() && face == EnumFacing.DOWN; }

    @Override public void setSuction(Aspect aspect, int amount) { }
    @Override public int getSuctionAmount(EnumFacing face) { return 0; }
    @Override public Aspect getSuctionType(EnumFacing face) { return null; }
    @Override public int addEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }
    @Override public int takeEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }
    @Override public Aspect getEssentiaType(EnumFacing face) { return null; }
    @Override public int getEssentiaAmount(EnumFacing face) { return 0; }
    @Override public int getMinimumSuction() { return 0; }

    public int receiveEssentia(Aspect aspect, int amount) {
        return relayEssentia(aspect, amount, 0, null);
    }

    private int relayEssentia(Aspect aspect, int amount, int depth, BlockPos from) {
        if (aspect == null || amount <= 0 || world == null) return 0;

        if (!hasStabilizerAbove()) {
            int moved = tryOutputToBelow(aspect, amount);
            if (moved > 0) return moved;
        }

        if (depth >= MAX_CHAIN_DEPTH) {
            return 0;
        }

        BlockPos next = null;
        if (targetPort != null && !targetPort.equals(getPos()) && !targetPort.equals(from)) {
            next = targetPort;
        } else if (sourcePort != null && !sourcePort.equals(getPos()) && !sourcePort.equals(from)) {
            next = sourcePort;
        }
        if (next == null) return 0;

        TileEntity te = world.getTileEntity(next);
        if (!(te instanceof TilePort) || te.isInvalid()) return 0;
        TilePort tgt = (TilePort) te;

        if (CHECK_LOS_FOR_LINKS) {
            double sx = this.pos.getX() + 0.5, sy = this.pos.getY() + 0.5, sz = this.pos.getZ() + 0.5;
            double tx = tgt.pos.getX() + 0.5, ty = tgt.pos.getY() + 0.5, tz = tgt.pos.getZ() + 0.5;
            if (!hasLineOfSight(sx, sy, sz, tx, ty, tz)) return 0;
        }

        return tgt.relayEssentia(aspect, amount, depth + 1, this.getPos());
    }

    private int tryOutputToBelow(Aspect aspect, int amount) {
        TileEntity teBelow = world.getTileEntity(pos.down());
        if (teBelow instanceof IEssentiaTransport) {
            IEssentiaTransport transport = (IEssentiaTransport) teBelow;
            if (transport.canInputFrom(EnumFacing.UP)) {
                return transport.addEssentia(aspect, amount, EnumFacing.UP);
            }
        }
        if (teBelow instanceof IAspectContainer) {
            IAspectContainer container = (IAspectContainer) teBelow;
            if (container.doesContainerAccept(aspect)) {
                return container.addToContainer(aspect, amount);
            }
        }
        return 0;
    }

    public void setTargetPort(BlockPos pos) {
        this.targetPort = pos;
        this.targetCharger = null;
        this.cachedChargerPos = null;
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        }
    }
    public BlockPos getTargetPort() { return targetPort; }

    public void setSourcePort(BlockPos pos) {
        this.sourcePort = pos;
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        }
    }
    public BlockPos getSourcePort() { return sourcePort; }

    public void setTargetCharger(BlockPos pos) {
        this.targetCharger = pos;
        this.cachedChargerPos = pos;
        this.targetPort = null;
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        }
    }

    public void clearTargetCharger() {
        this.targetCharger = null;
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        }
    }

    public int getBeamColor() { return cachedColor; }
    public void setCachedColor(int color) {
        this.cachedColor = color;
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        }
    }

    public BlockPos getChargerTarget() { return targetCharger != null ? targetCharger : cachedChargerPos; }

    public boolean hasCvCharge() {
        return false;
    }

    public AspectList getBoostAspects(float percent, int minAmount) {
        AspectList out = new AspectList();
        if (percent <= 0f) return out;

        boolean has = false;
        for (Map.Entry<Aspect, Integer> e : cvBuffer.entrySet()) {
            Aspect a = e.getKey();
            int cv = e.getValue();
            if (a == null || cv <= 0) continue;

            float scaled = (cv / (float) CONVERT_CHUNK_CV) * percent;
            int bonus = (int) Math.floor(scaled);
            if (bonus <= 0 && minAmount > 0) {
                bonus = minAmount;
            }
            if (bonus > 0) {
                out.add(a, bonus);
                has = true;
            }
        }

        if (!has && lastCvAspect != null && lastCvAt >= 0 && world != null) {
            long now = world.getTotalWorldTime();
            if ((now - lastCvAt) <= CHARGER_LINGER_TICKS) {
                out.add(lastCvAspect, Math.max(1, minAmount));
            }
        }

        return out;
    }

    public AspectList getPrimalCvWeights() {
        AspectList out = new AspectList();
        for (Map.Entry<Aspect, Integer> e : cvBuffer.entrySet()) {
            Aspect aspect = e.getKey();
            int cv = e.getValue();
            if (aspect == null || cv <= 0) continue;

            AspectList one = new AspectList().add(aspect, cv);
            AspectList primals = WandHelper.decomposeToPrimals(one);
            if (primals == null || primals.size() <= 0) continue;
            for (Aspect primal : primals.getAspects()) {
                int amount = primals.getAmount(primal);
                if (amount > 0) out.add(primal, amount);
            }
        }
        return out;
    }

    public int getAvailablePrimalCvPoints(int cvPerPoint) {
        if (cvPerPoint <= 0) return 0;
        AspectList weights = getPrimalCvWeights();
        int total = 0;
        for (Aspect primal : Aspect.getPrimalAspects()) {
            total += Math.max(0, weights.getAmount(primal)) / cvPerPoint;
        }
        return total;
    }

    public boolean drainPrimalCvForRecharge(AspectList delta, int cvPerPoint) {
        if (delta == null || delta.size() <= 0 || cvPerPoint <= 0) return false;

        AspectList available = getPrimalCvWeights();
        Map<Aspect, Integer> needCv = new HashMap<>();
        for (Aspect primal : Aspect.getPrimalAspects()) {
            int needPoints = delta.getAmount(primal);
            if (needPoints <= 0) continue;
            int requiredCv = needPoints * cvPerPoint;
            if (available.getAmount(primal) < requiredCv) return false;
            needCv.put(primal, requiredCv);
        }

        boolean changed = false;
        for (Aspect primal : Aspect.getPrimalAspects()) {
            int need = needCv.getOrDefault(primal, 0);
            if (need <= 0) continue;

            int direct = cvBuffer.getOrDefault(primal, 0);
            if (direct <= 0) continue;

            int used = Math.min(direct, need);
            direct -= used;
            need -= used;
            changed = true;
            if (direct <= 0) cvBuffer.remove(primal);
            else cvBuffer.put(primal, direct);
            needCv.put(primal, need);
        }

        Iterator<Map.Entry<Aspect, Integer>> it = cvBuffer.entrySet().iterator();
        while (it.hasNext() && hasRemainingNeed(needCv)) {
            Map.Entry<Aspect, Integer> e = it.next();
            Aspect aspect = e.getKey();
            int have = e.getValue();
            if (aspect == null || have <= 0 || isPrimalAspect(aspect)) continue;

            AspectList perCv = WandHelper.decomposeToPrimals(new AspectList().add(aspect, 1));
            if (!coversAnyNeed(perCv, needCv)) continue;

            int used = 0;
            while (used < have && coversAnyNeed(perCv, needCv)) {
                used++;
                for (Aspect primal : Aspect.getPrimalAspects()) {
                    int covered = perCv.getAmount(primal);
                    if (covered <= 0) continue;
                    int need = needCv.getOrDefault(primal, 0);
                    if (need > 0) needCv.put(primal, Math.max(0, need - covered));
                }
            }

            if (used > 0) {
                have -= used;
                changed = true;
                if (have <= 0) it.remove();
                else e.setValue(have);
            }
        }

        if (hasRemainingNeed(needCv)) return false;

        if (changed) {
            markDirty();
            if (world != null && !world.isRemote) {
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        }
        return changed;
    }

    private boolean hasRemainingNeed(Map<Aspect, Integer> needCv) {
        for (Integer need : needCv.values()) {
            if (need != null && need > 0) return true;
        }
        return false;
    }

    private boolean coversAnyNeed(AspectList perCv, Map<Aspect, Integer> needCv) {
        if (perCv == null || perCv.size() <= 0) return false;
        for (Aspect primal : Aspect.getPrimalAspects()) {
            if (needCv.getOrDefault(primal, 0) > 0 && perCv.getAmount(primal) > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isPrimalAspect(Aspect aspect) {
        if (aspect == null) return false;
        for (Aspect primal : Aspect.getPrimalAspects()) {
            if (aspect == primal) return true;
        }
        return false;
    }

    private boolean isChargerActive(long now) {
        if (hasCvCharge()) return true;
        return lastCvAt >= 0 && (now - lastCvAt) <= CHARGER_LINGER_TICKS;
    }

    private int getCvPrimaryColor() {
        if (lastCvAspect != null) return lastCvAspect.getColor();
        int best = 0;
        int bestAmount = 0;
        for (Map.Entry<Aspect, Integer> e : cvBuffer.entrySet()) {
            int amt = e.getValue();
            if (amt > bestAmount && e.getKey() != null) {
                bestAmount = amt;
                best = e.getKey().getColor();
            }
        }
        return best;
    }

    public boolean isChargerBeamActive() {
        if (world == null) return false;
        return isPrimaryChargerLink();
    }

    public boolean isPrimaryChargerLink() {
        BlockPos chargerTarget = getChargerTarget();
        if (world == null || chargerTarget == null) return false;
        long now = world.getTotalWorldTime();
        if (!isChargerActive(now) || !isChargerBlock(chargerTarget)) return false;

        double myDist = pos.distanceSq(chargerTarget);
        int r = CHARGER_SCAN_RANGE;
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(chargerTarget.getX() + dx, chargerTarget.getY() + dy, chargerTarget.getZ() + dz);
                    TileEntity te = world.getTileEntity(scan);
                    if (!(te instanceof TilePort) || te == this || te.isInvalid()) continue;

                    TilePort other = (TilePort) te;
                    if (!chargerTarget.equals(other.getChargerTarget())) continue;
                    if (!other.isChargerActive(now)) continue;

                    double otherDist = other.getPos().distanceSq(chargerTarget);
                    if (otherDist < myDist || (otherDist == myDist && isTieBreakerBefore(other.getPos(), this.pos))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean isTieBreakerBefore(BlockPos a, BlockPos b) {
        if (a.getX() != b.getX()) return a.getX() < b.getX();
        if (a.getY() != b.getY()) return a.getY() < b.getY();
        return a.getZ() < b.getZ();
    }

    public int getChargerBeamColor() {
        int cvColor = getCvPrimaryColor();
        return cvColor != 0 ? cvColor : cachedColor;
    }

    public BlockPos resolveChargerTargetRecursive(int depth) {
        if (world == null) return null;
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        HashSet<BlockPos> visited = new HashSet<>();

        queue.add(getPos());
        visited.add(getPos());

        while (!queue.isEmpty()) {
            BlockPos p = queue.removeFirst();
            TileEntity te = world.getTileEntity(p);
            if (!(te instanceof TilePort)) continue;
            TilePort port = (TilePort) te;

            if (port.isChargerBeamActive() && port.getChargerTarget() != null) {
                return port.getChargerTarget();
            }

            BlockPos next = port.getTargetPort();
            if (next != null && visited.add(next)) queue.addLast(next);
            BlockPos prev = port.getSourcePort();
            if (prev != null && visited.add(prev)) queue.addLast(prev);
        }

        return null;
    }

    public int resolveChargerColorRecursive(int depth) {
        return resolveNodeBeamColorRecursive(depth);
    }

    public int resolveNodeBeamColorRecursive(int depth) {
        if (world == null) return cachedColor;
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        HashSet<BlockPos> visited = new HashSet<>();

        queue.add(getPos());
        visited.add(getPos());

        while (!queue.isEmpty()) {
            BlockPos p = queue.removeFirst();
            TileEntity te = world.getTileEntity(p);
            if (!(te instanceof TilePort)) continue;
            TilePort port = (TilePort) te;

            int nodeColor = port.getLocalNodeMainAspectColor();
            if (nodeColor != 0) return nodeColor;

            BlockPos next = port.getTargetPort();
            if (next != null && visited.add(next)) queue.addLast(next);
            BlockPos prev = port.getSourcePort();
            if (prev != null && visited.add(prev)) queue.addLast(prev);
        }

        return cachedColor;
    }

    int getLocalNodeMainAspectColor() {
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (teAbove instanceof TileBuffNodeStabilizer) {
            EntityAuraNode node = ((TileBuffNodeStabilizer) teAbove).getFirstNode();
            if (node != null && node.getMainAspect() != null) {
                return node.getMainAspect().getColor();
            }
        }
        return 0;
    }

    private int computeBeamColorRecursive(int depth) {
        if (depth > MAX_COLOR_DEPTH) return 0x6600E5;
        long now = world != null ? world.getTotalWorldTime() : 0;
        if (hasCvCharge() || isChargerActive(now)) {
            int cvColor = getCvPrimaryColor();
            if (cvColor != 0) return cvColor;
        }

        if (targetPort != null && !targetPort.equals(getPos())) {
            TileEntity te = world.getTileEntity(targetPort);
            if (te instanceof TilePort) {
                int col = ((TilePort) te).computeBeamColorRecursive(depth + 1);
                if (col != 0x6600E5) return col;
            }
        }
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (teAbove instanceof TileBuffNodeStabilizer) {
            TileBuffNodeStabilizer stab = (TileBuffNodeStabilizer) teAbove;
            EntityAuraNode node = stab.getFirstNode();
            if (node != null) {
                Aspect main = node.getMainAspect();
                if (main != null) return main.getColor();
            }
        }
        return 0x6600E5;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (targetPort != null) {
            tag.setInteger("TargetX", targetPort.getX());
            tag.setInteger("TargetY", targetPort.getY());
            tag.setInteger("TargetZ", targetPort.getZ());
        }
        if (sourcePort != null) {
            tag.setInteger("SourceX", sourcePort.getX());
            tag.setInteger("SourceY", sourcePort.getY());
            tag.setInteger("SourceZ", sourcePort.getZ());
        }
        if (targetCharger != null) {
            tag.setInteger("TargetChargerX", targetCharger.getX());
            tag.setInteger("TargetChargerY", targetCharger.getY());
            tag.setInteger("TargetChargerZ", targetCharger.getZ());
        }
        if (lastCvAspect != null) {
            tag.setString("LastCvAspect", lastCvAspect.getTag());
        }
        tag.setLong("LastCvAt", lastCvAt);
        if (cachedChargerPos != null) {
            tag.setInteger("ChargerX", cachedChargerPos.getX());
            tag.setInteger("ChargerY", cachedChargerPos.getY());
            tag.setInteger("ChargerZ", cachedChargerPos.getZ());
        }
        tag.setInteger("AspectColor", cachedColor);
        tag.setLong("lastProcessedTick", lastProcessedTick);

        NBTTagCompound essentiaCv = new NBTTagCompound();
        int i = 0;
        for (Map.Entry<Aspect, Integer> e : essentiaCvBuffer.entrySet()) {
            Aspect aspect = e.getKey();
            int cv = e.getValue();
            if (aspect == null || cv <= 0) continue;

            NBTTagCompound one = new NBTTagCompound();
            one.setString("tag", aspect.getTag());
            one.setInteger("cv", cv);
            essentiaCv.setTag("e" + (i++), one);
        }
        essentiaCv.setInteger("size", i);
        tag.setTag("essentiaCvBuffer", essentiaCv);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("TargetX")) {
            targetPort = new BlockPos(tag.getInteger("TargetX"), tag.getInteger("TargetY"), tag.getInteger("TargetZ"));
        }
        if (tag.hasKey("SourceX")) {
            sourcePort = new BlockPos(tag.getInteger("SourceX"), tag.getInteger("SourceY"), tag.getInteger("SourceZ"));
        }
        if (tag.hasKey("TargetChargerX")) {
            targetCharger = new BlockPos(tag.getInteger("TargetChargerX"), tag.getInteger("TargetChargerY"), tag.getInteger("TargetChargerZ"));
        } else {
            targetCharger = null;
        }
        if (tag.hasKey("LastCvAspect")) {
            lastCvAspect = Aspect.getAspect(tag.getString("LastCvAspect"));
        } else {
            lastCvAspect = null;
        }
        lastCvAt = tag.getLong("LastCvAt");
        if (tag.hasKey("ChargerX")) {
            cachedChargerPos = new BlockPos(tag.getInteger("ChargerX"), tag.getInteger("ChargerY"), tag.getInteger("ChargerZ"));
        } else {
            cachedChargerPos = null;
        }
        cachedColor = tag.getInteger("AspectColor");
        lastProcessedTick = tag.getLong("lastProcessedTick");

        cvBuffer.clear();
        essentiaCvBuffer.clear();
        if (tag.hasKey("essentiaCvBuffer")) {
            NBTTagCompound essentiaCv = tag.getCompoundTag("essentiaCvBuffer");
            int size = essentiaCv.getInteger("size");
            for (int i = 0; i < size; i++) {
                NBTTagCompound one = essentiaCv.getCompoundTag("e" + i);
                Aspect aspect = Aspect.getAspect(one.getString("tag"));
                int cv = one.getInteger("cv");
                if (aspect != null && cv > 0) {
                    essentiaCvBuffer.put(aspect, cv);
                }
            }
        }
    }

    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public void handleUpdateTag(NBTTagCompound tag) { readFromNBT(tag); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound())); }
    @Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) { readFromNBT(pkt.getNbtCompound()); }
}
