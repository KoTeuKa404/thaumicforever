package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TFCustomTeleporter extends Teleporter {
    private final BlockPos pos;

    public TFCustomTeleporter(WorldServer world, BlockPos pos) {
        super(world);
        this.pos = pos;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        entity.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5);
    }
}
