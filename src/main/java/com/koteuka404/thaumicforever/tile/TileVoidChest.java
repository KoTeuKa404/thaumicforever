package com.koteuka404.thaumicforever.tile;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileVoidChest extends TileEntity {
    private UUID networkId = UUID.randomUUID();
    private UUID originalNetworkId = this.networkId;

    public UUID getNetworkId() {
        if (this.networkId == null) {
            this.networkId = UUID.randomUUID();
            if (this.originalNetworkId == null) {
                this.originalNetworkId = this.networkId;
            }
            this.markDirty();
        }
        return this.networkId;
    }

    public UUID getOriginalNetworkId() {
        if (this.originalNetworkId == null) {
            this.originalNetworkId = this.getNetworkId();
            this.markDirty();
        }
        return this.originalNetworkId;
    }

    public void setNetworkId(UUID networkId) {
        this.networkId = networkId == null ? UUID.randomUUID() : networkId;
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    public void resetToOriginalNetwork() {
        this.setNetworkId(this.getOriginalNetworkId());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("NetworkId", 8)) {
            try {
                this.networkId = UUID.fromString(compound.getString("NetworkId"));
            } catch (IllegalArgumentException ignored) {
                this.networkId = UUID.randomUUID();
            }
        } else {
            this.networkId = UUID.randomUUID();
        }

        if (compound.hasKey("OriginalNetworkId", 8)) {
            try {
                this.originalNetworkId = UUID.fromString(compound.getString("OriginalNetworkId"));
            } catch (IllegalArgumentException ignored) {
                this.originalNetworkId = this.networkId;
            }
        } else {
            this.originalNetworkId = this.networkId;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("NetworkId", this.getNetworkId().toString());
        compound.setString("OriginalNetworkId", this.getOriginalNetworkId().toString());
        return compound;
    }

    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("container.thaumicforever.void_chest");
    }
}
