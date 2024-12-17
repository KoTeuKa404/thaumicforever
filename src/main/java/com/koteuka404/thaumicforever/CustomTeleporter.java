package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class CustomTeleporter extends Teleporter {
    private final WorldServer world;
    private final BlockPos pos;

    public CustomTeleporter(BlockPos pos) {
        super(null); 
        this.world = null; 
        this.pos = pos;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        entity.motionX = 0.0;
        entity.motionY = 0.0;
        entity.motionZ = 0.0;
    }
}
