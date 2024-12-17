package com.koteuka404.thaumicforever;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

public class EntityBottleVis extends EntityThrowable {

    public EntityBottleVis(World worldIn) {
        super(worldIn);
    }

    public EntityBottleVis(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            BlockPos impactPos = result.getBlockPos();
            if (impactPos == null) {
                impactPos = new BlockPos(this.posX, this.posY, this.posZ);
            }

            AuraHelper.addVis(world, impactPos, 50.0f);

            this.setDead();
        }
    }
}
