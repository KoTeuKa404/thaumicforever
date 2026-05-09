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

public class TileBigJarPart extends TileEntity implements IAspectContainer, IAspectSource, IEssentiaTransport {
    private BlockPos master;

    public void setMaster(BlockPos master) {
        this.master = master;
    }

    public BlockPos getMaster() {
        return master;
    }

    private TileBigJar getMasterJar() {
        if (world == null || master == null) return null;
        TileEntity te = world.getTileEntity(master);
        return te instanceof TileBigJar ? (TileBigJar) te : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (master != null) {
            compound.setInteger("MasterX", master.getX());
            compound.setInteger("MasterY", master.getY());
            compound.setInteger("MasterZ", master.getZ());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("MasterX")) {
            master = new BlockPos(
                compound.getInteger("MasterX"),
                compound.getInteger("MasterY"),
                compound.getInteger("MasterZ")
            );
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

    @Override
    public AspectList getAspects() {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.getAspects() : new AspectList();
    }

    @Override
    public void setAspects(AspectList aspects) {
        TileBigJar jar = getMasterJar();
        if (jar != null) jar.setAspects(aspects);
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.doesContainerAccept(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.addToContainer(aspect, amount) : amount;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.takeFromContainer(aspect, amount);
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.takeFromContainer(aspects);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.doesContainerContainAmount(aspect, amount);
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.doesContainerContain(aspects);
    }

    @Override
    public int containerContains(Aspect aspect) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.containerContains(aspect) : 0;
    }

    @Override
    public boolean isBlocked() {
        TileBigJar jar = getMasterJar();
        return jar == null || jar.isBlocked();
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.isConnectable(face);
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null && jar.canOutputTo(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        TileBigJar jar = getMasterJar();
        if (jar != null) jar.setSuction(aspect, amount);
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.getSuctionType(face) : null;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.getSuctionAmount(face) : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.takeEssentia(aspect, amount, face) : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.addEssentia(aspect, amount, face) : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.getEssentiaType(face) : null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.getEssentiaAmount(face) : 0;
    }

    @Override
    public int getMinimumSuction() {
        TileBigJar jar = getMasterJar();
        return jar != null ? jar.getMinimumSuction() : 0;
    }
}
