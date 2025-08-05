package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityWindCharge extends Entity {

    private EntityLivingBase thrower;

    public EntityWindCharge(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityWindCharge(World world, EntityLivingBase shooter) {
        this(world);
        this.thrower = shooter;
        this.setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.3, shooter.posZ);

        Vec3d look = shooter.getLookVec();

        double speed = 1.5; 
        this.motionX = look.x * speed;
        this.motionY = look.y * speed * 1.4;
        this.motionZ = look.z * speed;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        Vec3d start = this.getPositionVector();
        Vec3d end = start.addVector(this.motionX, this.motionY, this.motionZ);

        RayTraceResult blockResult = this.world.rayTraceBlocks(start, end);

        Entity hitEntity = null;
        RayTraceResult entityResult = null;
        double closest = 0.0D;

        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, 
            this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(0.3D));
        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                if (entity == this.thrower && this.ticksExisted <= 5) continue;

                float f = 0.3F;
                net.minecraft.util.math.AxisAlignedBB aabb = entity.getEntityBoundingBox().grow(f);
                RayTraceResult intercept = aabb.calculateIntercept(start, end);
                if (intercept != null) {
                    double distanceTo = start.distanceTo(intercept.hitVec);
                    if (distanceTo < closest || closest == 0.0D) {
                        hitEntity = entity;
                        entityResult = new RayTraceResult(entity);
                        closest = distanceTo;
                    }
                }
            }
        }

        if (entityResult != null && (blockResult == null || closest < start.distanceTo(blockResult.hitVec))) {
            this.onImpact(entityResult);
        } else if (blockResult != null) {
            this.onImpact(blockResult);
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.rotationYaw += 10.0F;

        if (this.ticksExisted > 40) {
            this.setDead();
        }
    }

    private void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            System.out.println("[DEBUG] Wind Charge Impact at " + this.posX + ", " + this.posY + ", " + this.posZ);
    
            float explosionPower = 0.0F;
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, explosionPower, false);
    
            double knockbackRadius = 3.0;
            List<Entity> affectedEntities = this.world.getEntitiesWithinAABBExcludingEntity(this,
                    this.getEntityBoundingBox().grow(knockbackRadius));
    
        
            if (this.thrower != null && !affectedEntities.contains(this.thrower)) {
                double distance = this.thrower.getDistance(this);
                if (distance < knockbackRadius + 0.5) { 
                    affectedEntities.add(this.thrower);
                }
            }
    
            for (Entity entity : affectedEntities) {
                if (entity instanceof EntityLivingBase) {
                    Vec3d knockbackDirection = entity.getPositionVector().subtract(this.getPositionVector()).normalize();
                    double knockbackStrength = 1.0;
    
                    if (entity.getDistance(this) < 1.2) {
                        entity.motionY += 1.0;
                    }
    
                    entity.motionX += knockbackDirection.x * knockbackStrength;
                    entity.motionY += knockbackDirection.y * (knockbackStrength * 0.9);
                    entity.motionZ += knockbackDirection.z * knockbackStrength;
    
                    if (entity.isBurning()) {
                        entity.extinguish();
                        System.out.println("[DEBUG] Extinguished " + entity.getName());
                    }
                }
            }
    
            this.setDead();
        }
    }    

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) return false;
        this.setDead();
        return true;
    }
}
