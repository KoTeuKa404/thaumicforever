package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectMindControl extends FocusEffect {

    @Override
    public String getResearch() {
        return "MINDCONTROL";
    }

    @Override
    public String getKey() {
        return "thaumicforever.MINDCONTROL";
    }

    @Override
    public int getComplexity() {
        return 11; 
    }

    @Override
    public Aspect getAspect() {
        return Aspect.AVERSION; 
    }

    @Override
    public boolean execute(RayTraceResult rayTraceResult, Trajectory trajectory, float power, int duration) {
        Entity target = rayTraceResult.entityHit;

        if (target instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) target;
            if (entity instanceof EntityMob) {
                applyMindControl((EntityMob) entity);
                return true;
            }
        }
        return false;
    }

    private void applyMindControl(EntityMob entity) {
        List<Entity> nearbyEntities = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getEntityBoundingBox().grow(10));

        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof EntityLiving && nearbyEntity != entity) {
                entity.setAttackTarget((EntityLiving) nearbyEntity);
            }
        }
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[0]; 
    }

    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
    }
}
