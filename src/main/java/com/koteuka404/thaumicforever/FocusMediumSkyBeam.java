package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusMediumSkyBeam extends FocusMedium {

    @Override
    public String getResearch() {
        return "FOCUSSKYBEAM"; 
    }

    @Override
    public String getKey() {
        return "thaumicforever.SKYBEAM"; 
    }

    @Override
    public Aspect getAspect() {
        return Aspect.AIR;
    }

    @Override
    public int getComplexity() {
        return 5 + getSettingValue("power") * 2; 
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        Vec3d startVec = trajectory.source;
        Vec3d endVec = trajectory.direction.scale(20.0).add(startVec); 

        AxisAlignedBB boundingBox = new AxisAlignedBB(
            Math.min(startVec.x, endVec.x), Math.min(startVec.y, endVec.y), Math.min(startVec.z, endVec.z),
            Math.max(startVec.x, endVec.x), Math.max(startVec.y, endVec.y), Math.max(startVec.z, endVec.z)
        );

        List<Entity> entitiesInRange = getPackage().getCaster().world.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox);

        for (Entity entity : entitiesInRange) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase targetEntity = (EntityLivingBase) entity;
                
                scheduleSkyBeam(targetEntity, 2, getSettingValue("power"));
                
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasIntermediary() {
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public float getPowerMultiplier() {
        return 1.0f;
    }

    private void scheduleSkyBeam(Entity target, int delaySeconds, int power) {
        World world = target.world;
        BlockPos targetPos = target.getPosition();
        
        if (world instanceof WorldServer) {
            ((WorldServer) world).addScheduledTask(() -> {
                castSkyBeam(world, targetPos, power);
            });
        }
    }

    private void castSkyBeam(World world, BlockPos pos, int power) {
        Vec3d skyPos = new Vec3d(pos.getX(), world.getActualHeight(), pos.getZ());
        Vec3d targetPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

        createSkyBeamVisual(world, skyPos, targetPos);

        world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos)).forEach(entity -> {
            entity.attackEntityFrom(DamageSource.MAGIC, 8.0f * power); 
        });
    }

    private void createSkyBeamVisual(World world, Vec3d start, Vec3d end) {
        for (int i = 0; i < 50; i++) {
            double progress = i / 50.0;
            double x = start.x + (end.x - start.x) * progress;
            double y = start.y + (end.y - start.y) * progress;
            double z = start.z + (end.z - start.z) * progress;
            
            world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, x, y, z, 0, 0, 0);
            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, x, y, z, 0, 0, 0);
        }
    }
}
