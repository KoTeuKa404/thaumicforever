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

    public EntityWindCharge(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityWindCharge(World world, EntityLivingBase shooter) {
        this(world);
        this.setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.1, shooter.posZ);

        Vec3d look = shooter.getLookVec();

        double speed = 1.5; 
        this.motionX = look.x * speed;
        this.motionY = look.y * speed * 1.4;
        this.motionZ = look.z * speed;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.setPosition(this.posX, this.posY - 0.2, this.posZ);

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;

        this.rotationYaw += 10.0F;

        RayTraceResult result = this.world.rayTraceBlocks(this.getPositionVector(), 
                        this.getPositionVector().add(new Vec3d(this.motionX, this.motionY, this.motionZ)));

        if (result != null) {
            this.onImpact(result);
        }

        if (this.ticksExisted > 40) {
            this.setDead();
        }
    }

    private void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            System.out.println("[DEBUG] Wind Charge Impact at " + this.posX + ", " + this.posY + ", " + this.posZ);

            float explosionPower = 1.0F; 
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, explosionPower, false);

            double knockbackRadius = 3.0; 
            List<Entity> affectedEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, 
                    this.getEntityBoundingBox().grow(knockbackRadius));

            for (Entity entity : affectedEntities) {
                if (entity instanceof EntityLivingBase) {
                    Vec3d knockbackDirection = entity.getPositionVector().subtract(this.getPositionVector()).normalize();
                    
                    double knockbackStrength = 3.0; 
                    entity.motionX += knockbackDirection.x * knockbackStrength;
                    entity.motionY += knockbackDirection.y * (knockbackStrength * 0.9);
                    entity.motionZ += knockbackDirection.z * knockbackStrength;

                    System.out.println("[DEBUG] Knockback applied to " + entity.getName());
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
