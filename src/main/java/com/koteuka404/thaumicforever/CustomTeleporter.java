package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class CustomTeleporter extends Teleporter {

    private final WorldServer world;
    private final BlockPos pos;

    public CustomTeleporter(WorldServer world, BlockPos pos) {
        super(world);
        this.world = world;
        this.pos = pos;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        entity.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
    }
}
