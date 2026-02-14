package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

public class TilePort extends TileEntity implements ITickable,
        IAspectContainer, IEssentiaTransport,
        TileNodeTransducer.ICentivisAcceptorAspect {

    private final AspectList dummyBuffer = new AspectList();
    private int transferCooldown = 0;

    private static final int MAX_COLOR_DEPTH = 16;

    // CV: 100 CV = 1 
    private final Map<Aspect,Integer> cvBuffer = new HashMap<>();
    private static final int MAX_BUFFER_PER_ASPECT_CV = 2000; // 20 
    private static final int MAX_INTAKE_PER_TICK_CV   = 25;   
    private static final int CONVERT_CHUNK_CV         = 100;  // 100 CV -> 1 

    private static final int MAX_CHAIN_DEPTH   = 32; 
    private static final int FORWARD_PER_TICK_CV = 50; 
    private static final boolean CHECK_LOS_FOR_LINKS = true; 
    private static final int MIN_NODE_ASPECT = 2;

    private BlockPos targetPort = null;
    private BlockPos sourcePort = null;
    private int cachedColor = 0x6600E5;

    private long lastProcessedTick = -1;

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

        if (!cvBuffer.isEmpty() && !hasStabilizerAbove()) {
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
        int cur  = cvBuffer.getOrDefault(aspect, 0);
        int room = Math.max(0, MAX_BUFFER_PER_ASPECT_CV - cur);
        if (room <= 0) return 0;

        int accepted = Math.min(amount, Math.min(room, MAX_INTAKE_PER_TICK_CV));
        if (accepted <= 0) return 0;

        cvBuffer.put(aspect, cur + accepted);
        cachedColor = aspect.getColor();
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }

        if (!hasStabilizerAbove()) {
            flushCvDown();
        }
        return accepted;
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


    private boolean transferAspectToPort(TilePort target) {
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (!(teAbove instanceof TileBuffNodeStabilizer)) return false;
        TileBuffNodeStabilizer stab = (TileBuffNodeStabilizer) teAbove;
        EntityAuraNode node = stab.getFirstNode();
        if (node == null || node.isTfCharged()) return false;

        AspectList aspects = node.getNodeAspects();
        for (Aspect aspect : aspects.getAspectsSortedByAmount()) {
            int amt = aspects.getAmount(aspect);
            if (amt < MIN_NODE_ASPECT) continue;
            int moved = target.relayEssentia(aspect, 1, 0, this.getPos());
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

    public int getBeamColor() { return cachedColor; }
    public void setCachedColor(int color) {
        this.cachedColor = color;
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        }
    }

    private int computeBeamColorRecursive(int depth) {
        if (depth > MAX_COLOR_DEPTH) return 0x6600E5;

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
        tag.setInteger("AspectColor", cachedColor);
        tag.setLong("lastProcessedTick", lastProcessedTick);

        // CV буфер
        NBTTagCompound cv = new NBTTagCompound();
        int i = 0;
        for (Map.Entry<Aspect,Integer> e : cvBuffer.entrySet()) {
            NBTTagCompound one = new NBTTagCompound();
            one.setString("tag", e.getKey().getTag());
            one.setInteger("cv", e.getValue());
            cv.setTag("e" + (i++), one);
        }
        cv.setInteger("size", cvBuffer.size());
        tag.setTag("cvBuffer", cv);
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
        cachedColor = tag.getInteger("AspectColor");
        lastProcessedTick = tag.getLong("lastProcessedTick");

        cvBuffer.clear();
        if (tag.hasKey("cvBuffer")) {
            NBTTagCompound cv = tag.getCompoundTag("cvBuffer");
            int sz = cv.getInteger("size");
            for (int i = 0; i < sz; i++) {
                NBTTagCompound one = cv.getCompoundTag("e" + i);
                Aspect a = Aspect.getAspect(one.getString("tag"));
                int v = one.getInteger("cv");
                if (a != null && v > 0) cvBuffer.put(a, v);
            }
        }
    }

    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public void handleUpdateTag(NBTTagCompound tag) { readFromNBT(tag); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound())); }
    @Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) { readFromNBT(pkt.getNbtCompound()); }
}
