package com.koteuka404.thaumicforever.entity;

import java.util.HashSet;
import java.util.Set;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;

public class EntityDecoyMannequin extends EntityCreature {
    private static final int CHUNK_RADIUS = 1;
    private final Set<ChunkPos> forcedChunks = new HashSet<>();
    private ForgeChunkManager.Ticket chunkTicket;

    public EntityDecoyMannequin(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.8F);
        this.experienceValue = 0;
        this.setNoAI(true);
    }

    @Override
    protected void initEntityAI() {
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public void onLivingUpdate() {
        this.motionX = 0.0D;
        this.motionY = Math.min(this.motionY, 0.0D);
        this.motionZ = 0.0D;
        this.getNavigator().clearPath();
        if (!this.world.isRemote) {
            updateChunkTicket();
        }
        super.onLivingUpdate();
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public boolean isAIDisabled() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        this.entityDropItem(new ItemStack(ModItems.DECOY_MANNEQUIN), 0.0F);
    }

    @Override
    public void setDead() {
        releaseChunkTicket();
        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    public void restoreChunkTicket(ForgeChunkManager.Ticket ticket) {
        this.chunkTicket = ticket;
        forceNearbyChunks();
    }

    private void updateChunkTicket() {
        if (!(this.world instanceof WorldServer)) {
            return;
        }
        if (this.chunkTicket == null) {
            this.chunkTicket = ForgeChunkManager.requestTicket(ThaumicForever.instance, this.world, ForgeChunkManager.Type.ENTITY);
            if (this.chunkTicket != null) {
                this.chunkTicket.bindEntity(this);
            }
        }
        forceNearbyChunks();
    }

    private void forceNearbyChunks() {
        if (this.chunkTicket == null) {
            return;
        }
        int chunkX = (int) Math.floor(this.posX) >> 4;
        int chunkZ = (int) Math.floor(this.posZ) >> 4;
        Set<ChunkPos> wanted = new HashSet<>();
        for (int dx = -CHUNK_RADIUS; dx <= CHUNK_RADIUS; dx++) {
            for (int dz = -CHUNK_RADIUS; dz <= CHUNK_RADIUS; dz++) {
                wanted.add(new ChunkPos(chunkX + dx, chunkZ + dz));
            }
        }

        for (ChunkPos old : new HashSet<>(forcedChunks)) {
            if (!wanted.contains(old)) {
                ForgeChunkManager.unforceChunk(this.chunkTicket, old);
                forcedChunks.remove(old);
            }
        }
        for (ChunkPos pos : wanted) {
            if (forcedChunks.add(pos)) {
                ForgeChunkManager.forceChunk(this.chunkTicket, pos);
            }
        }
    }

    private void releaseChunkTicket() {
        if (this.chunkTicket == null) {
            return;
        }
        for (ChunkPos pos : new HashSet<>(forcedChunks)) {
            ForgeChunkManager.unforceChunk(this.chunkTicket, pos);
        }
        forcedChunks.clear();
        ForgeChunkManager.releaseTicket(this.chunkTicket);
        this.chunkTicket = null;
    }
}
