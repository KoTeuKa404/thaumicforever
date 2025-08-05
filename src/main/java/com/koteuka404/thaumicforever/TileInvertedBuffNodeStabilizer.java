package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileInvertedBuffNodeStabilizer extends TileThaumcraft implements ITickable {
    public int count = 0;
    private int delay = 0;
    private List<Entity> nodes = null;

    @Override
    public void update() {
        BlockPos below = pos.down();
        double cx = below.getX() + 0.5;
        double cy = below.getY() + 0.5;
        double cz = below.getZ() + 0.5;

        if (nodes == null || delay % 100 == 0) {
            nodes = EntityUtils.getEntitiesInRange(
                world, cx, cy, cz,
                null, EntityAuraNode.class, 0.5
            );
        }

        if (world.isRemote) {
            if (!nodes.isEmpty() && !gettingPower()) {
                if (delay == 0) count = 37;
                if (count < 37) count++;
            } else if (count > 0) {
                count--;
            }
        }

        if (delay == 0) {
            delay = world.rand.nextInt(100);
        }
        delay++;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
            pos.getX() - 0.3, pos.getY() - 0.3, pos.getZ() - 0.3,
            pos.getX() + 1.3, pos.getY() + 1.3, pos.getZ() + 1.3
        );
    }
}
