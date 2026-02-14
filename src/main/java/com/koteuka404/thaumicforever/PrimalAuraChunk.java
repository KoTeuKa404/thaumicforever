package com.koteuka404.thaumicforever;

import net.minecraft.nbt.NBTTagCompound;

public final class PrimalAuraChunk {

    public short[] base = new short[Primal.COUNT]; // 0..32766
    public float[] vis  = new float[Primal.COUNT]; // 0..32766

    public void clamp() {
        for (int i = 0; i < Primal.COUNT; i++) {
            if (vis[i] < 0) vis[i] = 0;
            if (vis[i] > 32766f) vis[i] = 32766f;

            if (base[i] < 0) base[i] = 0;
            if (base[i] > 32766) base[i] = 32766;
        }
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        for (int i = 0; i < Primal.COUNT; i++) {
            tag.setShort("b" + i, base[i]);
            tag.setFloat("v" + i, vis[i]);
        }
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        for (int i = 0; i < Primal.COUNT; i++) {
            base[i] = tag.getShort("b" + i);
            vis[i]  = tag.getFloat("v" + i);
        }
        clamp();
    }
}
