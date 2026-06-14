package com.koteuka404.thaumicforever.tile;

import com.koteuka404.thaumicforever.aura.PrimalAuraHandler;
import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.item.Primal;
import com.koteuka404.thaumicforever.network.PacketPrimalAuraConverterFX;

import java.util.EnumMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thaumcraft.api.aspects.Aspect;

public class TilePrimalAuraConverter extends TileEntity implements ITickable, TileNodeTransducer.ICentivisAcceptorAspect {

    private static final int MAX_BUFFER_PER_PRIMAL_CV = 4000;
    private static final int CV_PER_AURA = 100;
    private static final int MAX_AURA_PER_TICK = 2;

    private final EnumMap<Primal, Integer> cvBuffer = new EnumMap<>(Primal.class);
    private Primal lastPrimal = null;

    @Override
    public void update() {
        if (world == null || world.isRemote || cvBuffer.isEmpty()) return;

        boolean changed = false;
        for (Primal primal : Primal.values()) {
            int cv = cvBuffer.containsKey(primal) ? cvBuffer.get(primal) : 0;
            if (cv < CV_PER_AURA) continue;

            int aura = Math.min(MAX_AURA_PER_TICK, cv / CV_PER_AURA);
            PrimalAuraHandler.add(world, pos, primal, aura);
            spawnConversionParticles(primal, aura);
            cv -= aura * CV_PER_AURA;
            changed = true;

            if (cv <= 0) {
                cvBuffer.remove(primal);
            } else {
                cvBuffer.put(primal, cv);
            }
        }

        if (changed) {
            markDirty();
        }
    }

    @Override
    public int acceptCentivis(Aspect aspect, int amount, TileNodeTransducer source) {
        if (world == null || aspect == null || amount <= 0) return 0;
        EnumMap<Primal, Integer> split = new EnumMap<>(Primal.class);
        decomposeAspect(aspect, amount, split);
        if (split.isEmpty()) return 0;

        int canAccept = amount;
        for (Primal primal : Primal.values()) {
            int needed = split.containsKey(primal) ? split.get(primal) : 0;
            if (needed <= 0) continue;

            int room = Math.max(0, MAX_BUFFER_PER_PRIMAL_CV - getBufferedCentivis(primal));
            canAccept = Math.min(canAccept, (int) Math.floor((room * (double) amount) / needed));
        }

        if (canAccept <= 0) return 0;
        if (canAccept < amount) {
            split.clear();
            decomposeAspect(aspect, canAccept, split);
        }

        for (Primal primal : Primal.values()) {
            int cv = split.containsKey(primal) ? split.get(primal) : 0;
            if (cv > 0) {
                acceptPrimalCentivis(primal, cv);
            }
        }
        return canAccept;
    }

    public int getBufferedCentivis(Primal primal) {
        if (primal == null) return 0;
        return cvBuffer.containsKey(primal) ? cvBuffer.get(primal) : 0;
    }

    public Primal getLastPrimal() {
        return lastPrimal;
    }

    private void decomposeAspect(Aspect aspect, int amount, EnumMap<Primal, Integer> out) {
        if (aspect == null || amount <= 0 || out == null) return;

        Primal primal = primalFor(aspect);
        if (primal != null) {
            out.put(primal, out.containsKey(primal) ? out.get(primal) + amount : amount);
            return;
        }

        Aspect[] components = aspect.getComponents();
        if (components == null || components.length == 0) {
            return;
        }

        int remaining = amount;
        for (int i = 0; i < components.length && remaining > 0; i++) {
            int share = remaining / (components.length - i);
            if (share <= 0) share = remaining;
            decomposeAspect(components[i], share, out);
            remaining -= share;
        }
    }

    private int acceptPrimalCentivis(Primal primal, int amount) {
        int current = getBufferedCentivis(primal);
        int room = Math.max(0, MAX_BUFFER_PER_PRIMAL_CV - current);
        int accepted = Math.min(room, amount);
        if (accepted <= 0) return 0;

        cvBuffer.put(primal, current + accepted);
        lastPrimal = primal;
        markDirty();
        return accepted;
    }

    private static Primal primalFor(Aspect aspect) {
        if (aspect == Aspect.FIRE) return Primal.IGNIS;
        if (aspect == Aspect.EARTH) return Primal.TERRA;
        if (aspect == Aspect.AIR) return Primal.AER;
        if (aspect == Aspect.WATER) return Primal.AQUA;
        if (aspect == Aspect.ORDER) return Primal.ORDO;
        if (aspect == Aspect.ENTROPY) return Primal.PERDITIO;
        return null;
    }

    private void spawnConversionParticles(Primal primal, int aura) {
        if (world == null || world.isRemote || primal == null || aura <= 0 || ThaumicForever.network == null) return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.9D;
        double z = pos.getZ() + 0.5D;
        ThaumicForever.network.sendToAllAround(
                new PacketPrimalAuraConverterFX(x, y, z, colorFor(primal)),
                new TargetPoint(world.provider.getDimension(), x, y, z, 32.0D));
    }

    private static int colorFor(Primal primal) {
        switch (primal) {
            case IGNIS: return 0xFF4010;
            case TERRA: return 0x40D935;
            case AER: return 0xF0F05A;
            case AQUA: return 0x4090FF;
            case ORDO: return 0xCCCCF2;
            case PERDITIO: return 0x60388C;
            default: return 0xB0B0B0;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        for (Primal primal : Primal.values()) {
            compound.setInteger("Cv" + primal.name(), getBufferedCentivis(primal));
        }
        if (lastPrimal != null) {
            compound.setString("LastPrimal", lastPrimal.name());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cvBuffer.clear();
        for (Primal primal : Primal.values()) {
            int cv = compound.getInteger("Cv" + primal.name());
            if (cv > 0) {
                cvBuffer.put(primal, cv);
            }
        }
        if (compound.hasKey("LastPrimal")) {
            try {
                lastPrimal = Primal.valueOf(compound.getString("LastPrimal"));
            } catch (IllegalArgumentException ignored) {
                lastPrimal = null;
            }
        }
    }
}
