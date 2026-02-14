package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.entities.projectile.EntityCausalityCollapser;

public class AuraNodeEntity extends Entity {

    private static final int    CHUNK_RADIUS_XZ        = 2;
    private static final int    TICKS_PER_SECOND       = 20;
    private static final int    PERIOD_SECONDS         = 1;
    private static final float  VIS_EPS                = 0.25f;

    private static final int    RAMP_TICKS             = 40;
    private static final float  RAMP_AMOUNT            = 20.0f;
    private static final float  RAMP_PER_TICK          = RAMP_AMOUNT / RAMP_TICKS;

    private static final int    FLUX_PERIOD_SECONDS    = 10;
    private static final float  FLUX_CLEAN_AMOUNT      = 0.15f;

    private static class PendingTopUp {
        float remaining;
        int   ticksLeft;
        PendingTopUp(float total, int ticks) { this.remaining = total; this.ticksLeft = ticks; }
    }

    private final Map<Long, PendingTopUp> pendingTopups = new HashMap<>();

    private int tickCounter    = 0;
    private int lastChunkRingX = -CHUNK_RADIUS_XZ;

    public AuraNodeEntity(World world) {
        super(world);
        this.setSize(0.6F, 0.6F);
        this.preventEntitySpawning = true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) return;

        if (ThaumcraftApi.internalMethods instanceof DummyInternalMethodHandler) return;

        if (!world.isRemote) {
            boolean hit = !world.getEntitiesWithinAABB(EntityAlumentum.class, getEntityBoundingBox().grow(0.3)).isEmpty()
                        || !world.getEntitiesWithinAABB(EntityCausalityCollapser.class, getEntityBoundingBox().grow(0.3)).isEmpty();
            if (hit) {
                setDead();
                return;
            }
        }

        processScheduledTopups();

        if (++tickCounter >= TICKS_PER_SECOND * PERIOD_SECONDS) {
            tickCounter = 0;
            pulseColumn();
        }

        long period = (long)FLUX_PERIOD_SECONDS * TICKS_PER_SECOND;
        if (period > 0 && (world.getTotalWorldTime() % period) == 0) {
            cleanseFlux();
        }
    }

    private void processScheduledTopups() {
        Iterator<Map.Entry<Long, PendingTopUp>> it = pendingTopups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, PendingTopUp> e = it.next();
            long key = e.getKey();
            PendingTopUp plan = e.getValue();

            int cx = (int)(key >> 32);
            int cz = (int)(key & 0xffffffffL);
            BlockPos center = chunkCenter(cx, cz);

            if (!isAuraAreaUsable(center)) {
                it.remove();
                continue;
            }

            int   base = Math.max(0, AuraHelper.getAuraBase(world, center));
            float vis  = AuraHelper.getVis(world, center);
            if (base <= 0) {
                it.remove();
                continue;
            }

            float room   = base - vis;
            float quota  = Math.min(RAMP_PER_TICK, plan.remaining);
            float toAdd  = Math.max(0f, Math.min(quota, room));

            if (toAdd > 0f) {
                AuraHelper.addVis(world, center, toAdd);
                plan.remaining -= toAdd;
            }

            plan.ticksLeft--;

            if (plan.ticksLeft <= 0 || plan.remaining <= 0f || room <= 0f) {
                it.remove();
            }
        }
    }

    private void pulseColumn() {
        final BlockPos here = this.getPosition();
        final int baseChunkX = here.getX() >> 4;
        final int baseChunkZ = here.getZ() >> 4;
        final int scanChunkX = baseChunkX + lastChunkRingX;

        for (int dz = -CHUNK_RADIUS_XZ; dz <= CHUNK_RADIUS_XZ; dz++) {
            int cz = baseChunkZ + dz;
            int cx = scanChunkX;

            BlockPos center = chunkCenter(cx, cz);
            if (!isAuraAreaUsable(center)) continue;

            int   base = Math.max(0, AuraHelper.getAuraBase(world, center));
            float vis  = AuraHelper.getVis(world, center);
            if (base <= 0) continue;

            float room = base - vis;
            if (room > VIS_EPS) {
                long key = pack(cx, cz);
                if (!pendingTopups.containsKey(key)) {
                    float planAmount = Math.min(RAMP_AMOUNT, room);
                    if (planAmount > 0f) {
                        pendingTopups.put(key, new PendingTopUp(planAmount, RAMP_TICKS));
                    }
                }
            }
        }

        if (++lastChunkRingX > CHUNK_RADIUS_XZ) lastChunkRingX = -CHUNK_RADIUS_XZ;
    }

    private void cleanseFlux() {
        BlockPos here = this.getPosition();
        int cx = here.getX() >> 4;
        int cz = here.getZ() >> 4;
        BlockPos center = chunkCenter(cx, cz);

        if (!isAuraAreaUsable(center)) return;

        float currentFlux;
        try {
            currentFlux = AuraHelper.getFlux(world, center);
        } catch (Throwable t) {
            currentFlux = FLUX_CLEAN_AMOUNT;
        }

        if (currentFlux <= 0f) return;

        float toDrain = Math.min(FLUX_CLEAN_AMOUNT, currentFlux);
        if (toDrain > 0f) {
            AuraHelper.drainFlux(world, center, toDrain, false);
        }
    }

    private static long pack(int cx, int cz) { return (((long)cx) << 32) | (cz & 0xffffffffL); }
    private static BlockPos chunkCenter(int cx, int cz) { return new BlockPos((cx << 4) + 8, 0, (cz << 4) + 8); }

    private boolean isAuraAreaUsable(BlockPos pos) {
        Chunk ch = world.getChunkFromBlockCoords(pos);
        if (ch == null) return false;
        return world.isAreaLoaded(pos, 0, true);
    }

    // ----------------- NBT/Entity API -----------------

    @Override protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.tickCounter    = nbt.getInteger("tickCounter");
        this.lastChunkRingX = nbt.getInteger("lastChunkRingX");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("tickCounter", this.tickCounter);
        nbt.setInteger("lastChunkRingX", this.lastChunkRingX);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (world.isRemote) return true;

        if (ThaumcraftApi.internalMethods instanceof DummyInternalMethodHandler) {
            return true;
        }

        ItemStack held = player.getHeldItem(hand);

        if (!held.isEmpty() && held.getItem() == ItemsTC.phial) {
            ItemStack auraPhial = new ItemStack(ModItems.AuraPhial);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setFloat("storedAura", 250.0f);
            auraPhial.setTagCompound(tag);

            held.shrink(1);
            if (!player.addItemStackToInventory(auraPhial)) {
                this.entityDropItem(auraPhial, 0.5F);
            }
            return true;
        }

        if (!held.isEmpty() && held.getItem() == ModItems.AuraPhial) {
            NBTTagCompound tag = held.getTagCompound();
            if (tag != null && tag.hasKey("storedAura")) {
                float stored = Math.max(0f, tag.getFloat("storedAura"));
                if (stored > 0f) {
                    int cx = (this.getPosition().getX() >> 4);
                    int cz = (this.getPosition().getZ() >> 4);
                    BlockPos center = chunkCenter(cx, cz);

                    int   base = Math.max(0, AuraHelper.getAuraBase(world, center));
                    float vis  = AuraHelper.getVis(world, center);
                    float room = base - vis;

                    if (base <= 0) {
                        return true;
                    }
                    if (room <= VIS_EPS) {
                        return true;
                    }

                    long key = pack(cx, cz);
                    if (!pendingTopups.containsKey(key)) {
                        float planAmount = Math.min(RAMP_AMOUNT, Math.min(room, stored));
                        if (planAmount > 0f) {
                            pendingTopups.put(key, new PendingTopUp(planAmount, RAMP_TICKS));
                            float leftover = stored - planAmount;
                            if (leftover > 0f) {
                                tag.setFloat("storedAura", leftover);
                                held.setTagCompound(tag);
                            } else {
                                held.shrink(1);
                            }
                        }
                    }
                }
            }
            return true;
        }

        return super.processInitialInteract(player, hand);
    }

    @Override
    public boolean canBeCollidedWith() { return true; }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (world.isRemote) return false;

        if (source.isExplosion()) {
            setDead();
            return true;
        }

        Entity imm = source.getImmediateSource();
        Entity tru = source.getTrueSource();

        if (imm instanceof EntityAlumentum || tru instanceof EntityAlumentum ||
            imm instanceof EntityCausalityCollapser || tru instanceof EntityCausalityCollapser) {
            setDead();
            return true;
        }

        return false;
    }
}
