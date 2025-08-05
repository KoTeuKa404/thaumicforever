package com.koteuka404.thaumicforever;

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

public class TilePort extends TileEntity implements ITickable, IAspectContainer, IEssentiaTransport {

    private final AspectList dummyBuffer = new AspectList();
    private int transferCooldown = 0;
    private static final int MAX_COLOR_DEPTH = 16;
    private Aspect bufferAspect = null;
    private int bufferAmount = 0;

    private BlockPos targetPort = null;
    private BlockPos sourcePort = null;
    private int cachedColor = 0x6600E5;

    @Override
    public void update() {
        if (world.isRemote) return;
    
        int color = computeBeamColorRecursive(0);
    
        if (this.cachedColor != color) {
            this.cachedColor = color;
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    
        if (transferCooldown > 0) {
            transferCooldown--;
            return;
        }
    
        if (targetPort != null) {
            TileEntity teTarget = world.getTileEntity(targetPort);
            if (teTarget instanceof TilePort) {
                TilePort tgt = (TilePort) teTarget;
                if (transferAspectToPort(tgt)) {
                    transferCooldown = 5;
                }
                return; 
            }
        }
    
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (teAbove instanceof TilePort) {
            TilePort prev = (TilePort) teAbove;
            if (prev.transferAspectToPort(this)) {
                transferCooldown = 5;
            }
            return;
        }
    
        if (!(teAbove instanceof TileBuffNodeStabilizer)) {
            return;
        }
        TileBuffNodeStabilizer stab = (TileBuffNodeStabilizer) teAbove;
        EntityAuraNode node = stab.getFirstNode();
        if (node == null) {
            return;
        }
    
        TileEntity teBelow = world.getTileEntity(pos.down());
        if (!(teBelow instanceof IAspectContainer)) {
            return;
        }
        IAspectContainer container = (IAspectContainer) teBelow;
    
        boolean success = transferAspectFromNodeToContainer(node);
    
        if (success) {
            transferCooldown = 5;
        }
    }
    
    private boolean transferAspectToPort(TilePort target) {
        TileEntity teAbove = world.getTileEntity(pos.up());
        if (!(teAbove instanceof TileBuffNodeStabilizer)) return false;
        TileBuffNodeStabilizer stab = (TileBuffNodeStabilizer) teAbove;
        EntityAuraNode node = stab.getFirstNode();
        if (node == null) return false;

        for (Aspect aspect : node.getNodeAspects().getAspectsSortedByAmount()) {
            if (node.getNodeAspects().getAmount(aspect) >= 2) {
                int moved = target.receiveEssentia(aspect, 1);
                // log("[transferPort] Try send " + aspect.getTag() + " -> " + moved);
                if (moved > 0) {
                    node.getNodeAspects().reduce(aspect, moved);
                    node.updateSyncAspects();
                    markDirty();
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    // log("[transferPort] SUCCESS: transferred " + moved + " " + aspect.getTag() + " from node to PORT");
                    return true;
                }
            }
        }
        return false;
    }

    public int receiveEssentia(Aspect aspect, int amount) {
        TileEntity teBelow = world.getTileEntity(pos.down());
        if (teBelow instanceof IEssentiaTransport) {
            IEssentiaTransport transport = (IEssentiaTransport) teBelow;
            EnumFacing portFacing = EnumFacing.UP;
            if (transport.canInputFrom(portFacing)) {
                int moved = transport.addEssentia(aspect, amount, portFacing);
                // log("[receiveEssentia] addEssentia(" + aspect.getTag() + ") -> " + moved);
                return moved;
            }
        }
        if (teBelow instanceof IAspectContainer) {
            IAspectContainer container = (IAspectContainer) teBelow;
            if (container.doesContainerAccept(aspect)) {
                int added = container.addToContainer(aspect, amount);
                // log("[receiveEssentia] addToContainer(" + aspect.getTag() + ") -> " + added);
                return added;
            }
        }
        return 0;
    }

    private boolean transferAspectFromNodeToContainer(EntityAuraNode node) {
        TileEntity teBelow = world.getTileEntity(pos.down());

        if (teBelow instanceof IEssentiaTransport) {
            IEssentiaTransport transport = (IEssentiaTransport) teBelow;
            EnumFacing portFacing = EnumFacing.UP;

            for (Aspect aspect : node.getNodeAspects().getAspectsSortedByAmount()) {
                // log("[transfer] Try addEssentia(" + aspect.getTag() + ", 1, UP)");
                if (node.getNodeAspects().getAmount(aspect) >= 2 && transport.canInputFrom(portFacing)) {
                    int moved = transport.addEssentia(aspect, 1, portFacing);
                    // log("[transfer] addEssentia(" + aspect.getTag() + ") -> " + moved);
                    if (moved > 0) {
                        node.getNodeAspects().reduce(aspect, moved);
                        node.updateSyncAspects();
                        markDirty();
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                        // log("[transfer] SUCCESS: transferred " + moved + " " + aspect.getTag() + " from node to container via addEssentia");
                        return true;
                    }
                }
            }
            return false;
        }

        if (teBelow instanceof IAspectContainer) {
            IAspectContainer container = (IAspectContainer) teBelow;
            for (Aspect aspect : node.getNodeAspects().getAspectsSortedByAmount()) {
                // log("[transfer] Try addToContainer(" + aspect.getTag() + ")");
                if (node.getNodeAspects().getAmount(aspect) >= 2 && container.doesContainerAccept(aspect)) {
                    int added = container.addToContainer(aspect, 1);
                    // log("[transfer] addToContainer(" + aspect.getTag() + ") -> " + added);
                    if (added > 0) {
                        node.getNodeAspects().reduce(aspect, added);
                        node.updateSyncAspects();
                        markDirty();
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                        // log("[transfer] SUCCESS: transferred " + added + " " + aspect.getTag() + " from node to container via addToContainer");
                        return true;
                    }
                }
            }
        }

        // log("[transfer] NO aspect transferred this tick");
        return false;
    }    

    // --- LOGGING METHOD ---
    private void log(String msg) {
        // System.out.println("[TilePort@" + pos + "] " + msg);
    }

    // --- IAspectContainer (пустишка) ---
    @Override public int addToContainer(Aspect a, int amount) { return 0; }
    @Override public boolean doesContainerAccept(Aspect a) { return false; }
    @Override public int containerContains(Aspect a) { return 0; }
    @Override public boolean takeFromContainer(AspectList al) { return false; }
    @Override public boolean takeFromContainer(Aspect a, int amt) { return false; }
    @Override public AspectList getAspects() { return dummyBuffer; }
    @Override public void setAspects(AspectList al) { }
    @Override public boolean doesContainerContainAmount(Aspect aspect, int amount) { return false; }
    @Override public boolean doesContainerContain(AspectList al) { return false; }

    // --- IEssentiaTransport ---
    @Override public boolean isConnectable(EnumFacing face) { return face == EnumFacing.DOWN; }
    @Override public boolean canInputFrom(EnumFacing face)  { return false; }
    @Override public boolean canOutputTo(EnumFacing face)   { return face == EnumFacing.DOWN; }
    @Override public void setSuction(Aspect aspect, int amount) { }
    @Override public int getSuctionAmount(EnumFacing face) { return 0; }
    @Override public Aspect getSuctionType(EnumFacing face) { return null; }
    @Override public int addEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }
    @Override public int takeEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }
    @Override public Aspect getEssentiaType(EnumFacing face) { return null; }
    @Override public int getEssentiaAmount(EnumFacing face) { return 0; }
    @Override public int getMinimumSuction() { return 0; }

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

    public int getCachedColor() { return cachedColor; }
    public void setCachedColor(int color) {
        this.cachedColor = color;
        markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        }
    }

    // --- NBT/Sync (позиції і колір) ---
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
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
        return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("TargetX")) {
            targetPort = new BlockPos(
                tag.getInteger("TargetX"),
                tag.getInteger("TargetY"),
                tag.getInteger("TargetZ")
            );
        }
        if (tag.hasKey("SourceX")) {
            sourcePort = new BlockPos(
                tag.getInteger("SourceX"),
                tag.getInteger("SourceY"),
                tag.getInteger("SourceZ")
            );
        }
        cachedColor = tag.getInteger("AspectColor");
    }
    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public void handleUpdateTag(NBTTagCompound tag) { readFromNBT(tag); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound()));
    }
    @Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
    private int computeBeamColorRecursive(int depth) {
        if (depth > MAX_COLOR_DEPTH)
            return 0x6600E5;
    
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
    
    public int getBeamColor() {
        return cachedColor;
    }
    
}
