package com.koteuka404.thaumicforever.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileBigJar extends TileEntity implements IAspectContainer, IAspectSource, IEssentiaTransport {
    public static final int MAX_BRAINS = 6;
    private static final int MIND_CAPACITY_PER_BRAIN = 100;
    private static final int SUCTION_AMOUNT = 128;
    private static final int MINIMUM_SUCTION = 32;

    private BlockPos origin;
    private int brainCount = 1;
    private int mindEssentia;
    private final float[] brainOffsetX = new float[MAX_BRAINS];
    private final float[] brainOffsetY = new float[MAX_BRAINS];
    private final float[] brainOffsetZ = new float[MAX_BRAINS];

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public int getBrainCount() {
        return brainCount;
    }

    public int getMindEssentia() {
        return mindEssentia;
    }

    public int getMindEssentiaCapacity() {
        return Math.max(1, brainCount) * MIND_CAPACITY_PER_BRAIN;
    }

    public boolean addBrain() {
        if (brainCount >= MAX_BRAINS) {
            return false;
        }
        brainCount++;
        markDirty();
        sync();
        return true;
    }

    public float getBrainOffsetX(int index) {
        return index >= 0 && index < brainOffsetX.length ? brainOffsetX[index] : 0.0F;
    }

    public float getBrainOffsetY(int index) {
        return index >= 0 && index < brainOffsetY.length ? brainOffsetY[index] : 0.0F;
    }

    public float getBrainOffsetZ(int index) {
        return index >= 0 && index < brainOffsetZ.length ? brainOffsetZ[index] : 0.0F;
    }

    public void setBrainOffsets(int index, float x, float y, float z) {
        if (index < 0 || index >= brainOffsetX.length) return;
        this.brainOffsetX[index] = x;
        this.brainOffsetY[index] = y;
        this.brainOffsetZ[index] = z;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (origin != null) {
            compound.setInteger("OriginX", origin.getX());
            compound.setInteger("OriginY", origin.getY());
            compound.setInteger("OriginZ", origin.getZ());
        }
        compound.setInteger("BrainCount", brainCount);
        compound.setInteger("MindEssentia", mindEssentia);
        for (int i = 0; i < MAX_BRAINS; i++) {
            compound.setFloat("BrainOffsetX" + i, brainOffsetX[i]);
            compound.setFloat("BrainOffsetY" + i, brainOffsetY[i]);
            compound.setFloat("BrainOffsetZ" + i, brainOffsetZ[i]);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("OriginX")) {
            origin = new BlockPos(
                compound.getInteger("OriginX"),
                compound.getInteger("OriginY"),
                compound.getInteger("OriginZ")
            );
        }
        brainCount = compound.hasKey("BrainCount") ? compound.getInteger("BrainCount") : 1;
        if (brainCount < 1) {
            brainCount = 1;
        } else if (brainCount > MAX_BRAINS) {
            brainCount = MAX_BRAINS;
        }
        mindEssentia = compound.hasKey("MindEssentia") ? compound.getInteger("MindEssentia") : 0;
        mindEssentia = Math.max(0, Math.min(mindEssentia, getMindEssentiaCapacity()));
        for (int i = 0; i < MAX_BRAINS; i++) {
            brainOffsetX[i] = compound.getFloat("BrainOffsetX" + i);
            brainOffsetY[i] = compound.getFloat("BrainOffsetY" + i);
            brainOffsetZ[i] = compound.getFloat("BrainOffsetZ" + i);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public void sync() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public AspectList getAspects() {
        return mindEssentia > 0 ? new AspectList().add(Aspect.MIND, mindEssentia) : new AspectList();
    }

    @Override
    public void setAspects(AspectList aspects) {
        mindEssentia = aspects == null ? 0 : Math.max(0, Math.min(aspects.getAmount(Aspect.MIND), getMindEssentiaCapacity()));
        markDirty();
        sync();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return Aspect.MIND.equals(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia >= getMindEssentiaCapacity()) return 0;
        int added = Math.min(amount, getMindEssentiaCapacity() - mindEssentia);
        mindEssentia += added;
        markDirty();
        sync();
        return amount - added;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia < amount) return false;
        mindEssentia -= amount;
        markDirty();
        sync();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        if (!doesContainerContain(aspects)) return false;
        int amount = aspects == null ? 0 : aspects.getAmount(Aspect.MIND);
        return amount <= 0 || takeFromContainer(Aspect.MIND, amount);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return Aspect.MIND.equals(aspect) && mindEssentia >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        if (aspects == null) return true;
        for (Aspect aspect : aspects.getAspects()) {
            int amount = aspects.getAmount(aspect);
            if (amount <= 0) continue;
            if (!Aspect.MIND.equals(aspect) || amount > mindEssentia) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return Aspect.MIND.equals(aspect) ? mindEssentia : 0;
    }

    @Override
    public boolean isBlocked() {
        return false;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return true;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return Aspect.MIND;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return mindEssentia < getMindEssentiaCapacity() ? SUCTION_AMOUNT : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia <= 0) return 0;
        int taken = Math.min(amount, mindEssentia);
        mindEssentia -= taken;
        markDirty();
        sync();
        return taken;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia >= getMindEssentiaCapacity()) return 0;
        int added = Math.min(amount, getMindEssentiaCapacity() - mindEssentia);
        mindEssentia += added;
        markDirty();
        sync();
        return added;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return Aspect.MIND;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return mindEssentia;
    }

    @Override
    public int getMinimumSuction() {
        return MINIMUM_SUCTION;
    }
}
