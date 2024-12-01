package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

public class AuraNodeEntity extends Entity {
    private static final float MAX_AURA = 300.0f; 
    private static final float INCREMENT_AMOUNT = 5.0f; 
    private static final int CHUNK_RADIUS = 3;
    private static final int TICKS_PER_SECOND = 20; 
    private int tickCounter = 0; 

    public AuraNodeEntity(World world) {
        super(world);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        
        tickCounter++;
        
        if (tickCounter >= TICKS_PER_SECOND) {
            tickCounter = 0;
            increaseAura();
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

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }
}
