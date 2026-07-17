package com.koteuka404.thaumicforever.event;

import java.util.List;

import com.koteuka404.thaumicforever.entity.EntityDecoyMannequin;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DecoyMannequinHandler {
    private static final double MOB_BAIT_RANGE = 24.0D;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!(event.world instanceof WorldServer) || event.phase != TickEvent.Phase.END) {
            return;
        }
        baitHostileMobs((WorldServer) event.world);
    }

    private void baitHostileMobs(WorldServer world) {
        List<EntityDecoyMannequin> decoys = world.getEntities(EntityDecoyMannequin.class, decoy -> decoy != null && !decoy.isDead);
        for (EntityDecoyMannequin decoy : decoys) {
            AxisAlignedBB area = decoy.getEntityBoundingBox().grow(MOB_BAIT_RANGE);
            List<EntityCreature> mobs = world.getEntitiesWithinAABB(EntityCreature.class, area, entity -> entity instanceof IMob && entity.isEntityAlive());
            for (EntityCreature mob : mobs) {
                EntityLivingBase currentTarget = mob.getAttackTarget();
                if (currentTarget == null || currentTarget.isDead || mob.getDistanceSq(decoy) < mob.getDistanceSq(currentTarget)) {
                    mob.setAttackTarget(decoy);
                    mob.getNavigator().tryMoveToEntityLiving(decoy, 1.0D);
                }
            }
        }
    }
}
