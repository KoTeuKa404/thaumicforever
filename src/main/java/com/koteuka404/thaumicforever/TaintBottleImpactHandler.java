package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.entities.projectile.EntityBottleTaint;

public class TaintBottleImpactHandler {

    private static final double NODE_TAINT_RADIUS = 6.0;

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent.Throwable event) {
        if (!(event.getEntity() instanceof EntityBottleTaint)) return;

        BlockPos center = event.getEntity().getPosition();

        AxisAlignedBB aabb = new AxisAlignedBB(center).grow(NODE_TAINT_RADIUS);
        List<EntityAuraNode> nodes = event.getEntity().world.getEntitiesWithinAABB(EntityAuraNode.class, aabb);

        if (!nodes.isEmpty()) {
            for (EntityAuraNode n : nodes) {
                if (n != null && !n.isDead && n.getNodeType() != 4) {
                    n.setNodeType(4);
                }
            }
        }
    }
}
