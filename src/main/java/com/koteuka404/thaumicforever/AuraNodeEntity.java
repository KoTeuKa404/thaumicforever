package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemsTC;

public class AuraNodeEntity extends Entity {
    private static final float MAX_AURA                 = 250.0f;
    private static final float INCREMENT_AMOUNT         = 5.0f;
    private static final int   CHUNK_RADIUS             = 3;
    private static final int   TICKS_PER_SECOND         = 20;
    private static final int   DRAIN_INTERVAL_SECONDS   = 25;    
    private int tickCounter     = 0;
    private int secondsCounter  = 0;  

    public AuraNodeEntity(World world) {
        super(world);
        this.setSize(0.6F, 0.6F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        tickCounter++;
        if (tickCounter >= TICKS_PER_SECOND) {
            tickCounter = 0;
            increaseAura();

            secondsCounter++;
            if (secondsCounter >= DRAIN_INTERVAL_SECONDS) {
                secondsCounter = 0;
                drainFluxNearby();
            }
        }
    }

    private void increaseAura() {
        BlockPos nodePos = this.getPosition();
        for (int xOffset = -CHUNK_RADIUS * 16; xOffset <= CHUNK_RADIUS * 16; xOffset += 16) {
            for (int zOffset = -CHUNK_RADIUS * 16; zOffset <= CHUNK_RADIUS * 16; zOffset += 16) {
                BlockPos pos = nodePos.add(xOffset, 0, zOffset);
                float currentVis = AuraHelper.getVis(world, pos);
                if (currentVis < MAX_AURA) {
                    float increaseAmount = Math.min(INCREMENT_AMOUNT, MAX_AURA - currentVis);
                    AuraHelper.addVis(world, pos, increaseAmount);
                }
            }
        }
    }

    private void drainFluxNearby() {
        BlockPos nodePos = this.getPosition();
        for (int xOffset = -CHUNK_RADIUS * 16; xOffset <= CHUNK_RADIUS * 16; xOffset += 16) {
            for (int zOffset = -CHUNK_RADIUS * 16; zOffset <= CHUNK_RADIUS * 16; zOffset += 16) {
                BlockPos pos = nodePos.add(xOffset, 0, zOffset);
                AuraHelper.drainFlux(world, pos, 0.1f, false);
            }
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (!heldItem.isEmpty() && heldItem.getItem() == ItemsTC.phial) {
                ItemStack auraPhial = new ItemStack(ModItems.AuraPhial);
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setFloat("storedAura", MAX_AURA);
                auraPhial.setTagCompound(nbt);
                heldItem.shrink(1);
                if (!player.addItemStackToInventory(auraPhial)) {
                    this.entityDropItem(auraPhial, 0.5F);
                }
                return true;
            }
            else if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.AuraPhial) {
                NBTTagCompound nbt = heldItem.getTagCompound();
                if (nbt != null && nbt.hasKey("storedAura")) {
                    float storedAura = nbt.getFloat("storedAura");
                    AuraHelper.addVis(world, this.getPosition(), storedAura);
                    heldItem.shrink(1);
                }
                return true;
            }
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
}
