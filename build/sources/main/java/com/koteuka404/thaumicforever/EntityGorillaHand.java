package com.koteuka404.thaumicforever;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityGorillaHand extends EntityThrowable {

    public EntityGorillaHand(World worldIn) {
        super(worldIn);
    }

    public EntityGorillaHand(World worldIn, EntityLivingBase thrower) {
        super(worldIn, thrower);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 6.0F);
        }
        if (!this.world.isRemote) {
            this.setDead(); 
        }
    }
}
