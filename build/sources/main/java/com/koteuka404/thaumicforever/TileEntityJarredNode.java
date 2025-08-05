package com.koteuka404.thaumicforever;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityJarredNode extends TileEntity {
    private NBTTagCompound nodeNBT = null;

    public void setNodeNBT(NBTTagCompound tag) {
        this.nodeNBT = tag == null ? null : tag.copy();

        if (nodeNBT != null && (!nodeNBT.hasKey("nodeAspects", 10) || nodeNBT.getCompoundTag("nodeAspects").hasNoTags())) {
            if (nodeNBT.hasKey("aspects", 10)) {
                nodeNBT.setTag("nodeAspects", nodeNBT.getTag("aspects"));
            } else {
                NBTTagCompound testAspects = new NBTTagCompound();
                testAspects.setInteger("aer", 42);
                nodeNBT.setTag("nodeAspects", testAspects);
            }
        }
        markDirty();
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    public NBTTagCompound getTrueNodeNBT() {
        return nodeNBT;
    }

    public void writeNodeNBT(NBTTagCompound outTag) {
        if (nodeNBT != null) {
            for (String key : nodeNBT.getKeySet()) {
                outTag.setTag(key, nodeNBT.getTag(key));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (nodeNBT != null) {
            for (String key : nodeNBT.getKeySet()) {
                compound.setTag(key, nodeNBT.getTag(key));
            }
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        nodeNBT = new NBTTagCompound();
        for (String key : compound.getKeySet()) {
            if (!key.equals("x") && !key.equals("y") && !key.equals("z") && !key.equals("id")) {
                nodeNBT.setTag(key, compound.getTag(key));
            }
        }
        if (nodeNBT != null && !nodeNBT.hasKey("nodeAspects", 10) && nodeNBT.hasKey("aspects", 10)) {
            nodeNBT.setTag("nodeAspects", nodeNBT.getTag("aspects"));
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        writeNodeNBT(tag);
        return tag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
