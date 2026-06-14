package com.koteuka404.thaumicforever.tile;

import com.koteuka404.thaumicforever.aura.PrimalAuraChunk;
import com.koteuka404.thaumicforever.aura.PrimalAuraHandler;
import com.koteuka404.thaumicforever.aura.PrimalAuraHandler.PrimalAuraWorldData;
import com.koteuka404.thaumicforever.block.BlockCrystallizer;
import com.koteuka404.thaumicforever.item.Primal;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TileCrystallizer extends TileEntity implements ITickable, TileNodeTransducer.ICentivisAcceptorAspect {

    private static final int PROCESS_TIME = 40;
    private static final float AURA_COST = 4.0F;
    private static final float MIN_SURPLUS = 2.0F;
    private static final int CV_PER_CRYSTAL = 200;
    private static final int MAX_CV_BUFFER_PER_ASPECT = CV_PER_CRYSTAL * 10;

    private final Map<Aspect, Integer> cvBuffer = new HashMap<>();
    private int progress;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        Aspect cvAspect = findBufferedCvAspect();
        Primal primal = cvAspect == null ? findDominantSurplus() : null;
        if (cvAspect == null && primal == null) {
            progress = 0;
            return;
        }

        progress++;
        if (progress < PROCESS_TIME) return;

        progress = 0;
        if (cvAspect != null) {
            if (!drainCentivis(cvAspect, CV_PER_CRYSTAL)) {
                markDirty();
                return;
            }
            outputCrystal(cvAspect);
        } else {
            float drained = PrimalAuraHandler.drain(world, pos, primal, AURA_COST, false);
            if (drained < AURA_COST * 0.75F) {
                markDirty();
                return;
            }
            outputCrystal(aspectFor(primal));
        }

        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.55F, 0.75F + world.rand.nextFloat() * 0.35F);
        markDirty();
    }

    public Primal findDominantSurplus() {
        PrimalAuraChunk chunk = PrimalAuraWorldData.get(world).getChunkAt(world, pos);
        Primal best = null;
        float bestSurplus = 0.0F;

        for (Primal primal : Primal.values()) {
            float base = chunk.base[primal.id];
            float surplus = chunk.vis[primal.id] - base;
            float threshold = Math.max(MIN_SURPLUS, base * 0.02F);
            if (surplus >= threshold && surplus >= AURA_COST && surplus > bestSurplus) {
                bestSurplus = surplus;
                best = primal;
            }
        }

        return best;
    }

    private Aspect findBufferedCvAspect() {
        Aspect best = null;
        int bestAmount = 0;
        Iterator<Map.Entry<Aspect, Integer>> it = cvBuffer.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Aspect, Integer> entry = it.next();
            Aspect aspect = entry.getKey();
            int amount = entry.getValue();
            if (aspect == null || amount <= 0) {
                it.remove();
                continue;
            }
            if (amount >= CV_PER_CRYSTAL && amount > bestAmount) {
                best = aspect;
                bestAmount = amount;
            }
        }
        return best;
    }

    private boolean drainCentivis(Aspect aspect, int amount) {
        int current = getBufferedCentivis(aspect);
        if (current < amount) return false;
        int remaining = current - amount;
        if (remaining <= 0) {
            cvBuffer.remove(aspect);
        } else {
            cvBuffer.put(aspect, remaining);
        }
        return true;
    }

    private void outputCrystal(Aspect aspect) {
        if (aspect == null) return;

        ItemStack crystal = ThaumcraftApiHelper.makeCrystal(aspect, 1);
        EnumFacing facing = getFacing();

        TileEntity targetTile = world.getTileEntity(pos.offset(facing));
        if (targetTile != null && targetTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
            IItemHandler handler = targetTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (handler != null) {
                ItemStack remaining = crystal.copy();
                for (int slot = 0; slot < handler.getSlots() && !remaining.isEmpty(); slot++) {
                    remaining = handler.insertItem(slot, remaining, false);
                }
                if (remaining.isEmpty()) {
                    return;
                }
                crystal = remaining;
            }
        }

        double x = pos.getX() + 0.5D + facing.getFrontOffsetX() * 0.65D;
        double y = pos.getY() + 0.45D;
        double z = pos.getZ() + 0.5D + facing.getFrontOffsetZ() * 0.65D;

        EntityItem entity = new EntityItem(world, x, y, z, crystal);
        entity.motionX = facing.getFrontOffsetX() * 0.08D;
        entity.motionY = 0.08D;
        entity.motionZ = facing.getFrontOffsetZ() * 0.08D;
        world.spawnEntity(entity);
    }

    private EnumFacing getFacing() {
        if (world == null) return EnumFacing.NORTH;
        try {
            return world.getBlockState(pos).getValue(BlockCrystallizer.FACING);
        } catch (Throwable ignored) {
            return EnumFacing.NORTH;
        }
    }

    @Override
    public int acceptCentivis(Aspect aspect, int amount, TileNodeTransducer source) {
        if (aspect == null || amount <= 0) return 0;
        int current = getBufferedCentivis(aspect);
        int accepted = Math.min(amount, Math.max(0, MAX_CV_BUFFER_PER_ASPECT - current));
        if (accepted <= 0) return 0;
        cvBuffer.put(aspect, current + accepted);
        markDirty();
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return accepted;
    }

    public int getProgress() {
        return progress;
    }

    public float getProgressPercent() {
        return PROCESS_TIME <= 0 ? 0.0F : (progress * 100.0F) / PROCESS_TIME;
    }

    public float getSurplus(Primal primal) {
        if (world == null || primal == null) return 0.0F;
        PrimalAuraChunk chunk = PrimalAuraWorldData.get(world).getChunkAt(world, pos);
        return chunk.vis[primal.id] - chunk.base[primal.id];
    }

    public int getBufferedCentivis(Aspect aspect) {
        if (aspect == null) return 0;
        Integer value = cvBuffer.get(aspect);
        return value == null ? 0 : value;
    }

    private static Aspect aspectFor(Primal primal) {
        switch (primal) {
            case IGNIS: return Aspect.FIRE;
            case TERRA: return Aspect.EARTH;
            case AER: return Aspect.AIR;
            case AQUA: return Aspect.WATER;
            case ORDO: return Aspect.ORDER;
            case PERDITIO: return Aspect.ENTROPY;
            default: return null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Progress", progress);
        NBTTagCompound cv = new NBTTagCompound();
        for (Map.Entry<Aspect, Integer> entry : cvBuffer.entrySet()) {
            Aspect aspect = entry.getKey();
            int amount = entry.getValue();
            if (aspect != null && amount > 0) {
                cv.setInteger(aspect.getTag(), amount);
            }
        }
        compound.setTag("CvBuffer", cv);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        progress = compound.getInteger("Progress");
        cvBuffer.clear();
        if (compound.hasKey("CvBuffer", 10)) {
            NBTTagCompound cv = compound.getCompoundTag("CvBuffer");
            for (String key : cv.getKeySet()) {
                Aspect aspect = Aspect.getAspect(key);
                int amount = cv.getInteger(key);
                if (aspect != null && amount > 0) {
                    cvBuffer.put(aspect, Math.min(amount, MAX_CV_BUFFER_PER_ASPECT));
                }
            }
        }
    }
}
