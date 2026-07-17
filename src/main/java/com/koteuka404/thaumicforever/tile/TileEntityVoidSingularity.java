package com.koteuka404.thaumicforever.tile;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityVoidSingularity extends TileEntity implements ITickable {

    private static final int FUSE_TICKS = 40;
    private static final double RADIUS = 30.0D;
    private static final float DAMAGE = 115.0F;
    private int fuse;

    @Override
    public void update() {
        if (world == null) {
            return;
        }

        if (world.isRemote) {
            spawnFuseParticles();
            return;
        }

        fuse++;
        if (fuse >= FUSE_TICKS) {
            detonate();
        }
    }

    private void detonate() {
        detonateAt(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        world.setBlockToAir(pos);
    }

    public static void detonateAt(net.minecraft.world.World world, double x, double y, double z) {
        if (world == null || world.isRemote) {
            return;
        }

        AxisAlignedBB area = new AxisAlignedBB(x - RADIUS, y - RADIUS, z - RADIUS,
                x + RADIUS, y + RADIUS, z + RADIUS);

        DamageSource source = new DamageSource("void_singularity")
                .setDamageBypassesArmor()
                .setDamageIsAbsolute()
                .setMagicDamage();
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase target : targets) {
            if (!target.isDead && target.getDistance(x, y, z) <= RADIUS) {
                float healthBefore = target.getHealth();
                float requiredHealth = Math.max(0.0F, healthBefore - DAMAGE);
                target.hurtResistantTime = 0;
                target.attackEntityFrom(source, DAMAGE);

                // Some armor and shield handlers can cancel the damage event entirely.
                // Enforce the same absolute health loss as Primal Rupture.
                if (target.getHealth() > requiredHealth) {
                    target.setAbsorptionAmount(0.0F);
                    target.setHealth(requiredHealth);
                    if (requiredHealth <= 0.0F && !target.isDead) {
                        target.onDeath(source);
                    }
                }
            }
        }

        world.playSound(null, x, y, z, SoundEvents.ENTITY_ENDERDRAGON_GROWL,
                SoundCategory.BLOCKS, 1.0F, 0.6F);
        world.spawnParticle(EnumParticleTypes.PORTAL, x, y, z, 0.0D, 0.0D, 0.0D, 120);
    }

    private void spawnFuseParticles() {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z,
                0.0D, 0.02D, 0.0D);
        world.spawnParticle(EnumParticleTypes.PORTAL, x, y, z,
                0.0D, 0.0D, 0.0D);
    }

    @Override
    public net.minecraft.nbt.NBTTagCompound writeToNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Fuse", fuse);
        return compound;
    }

    @Override
    public void readFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        super.readFromNBT(compound);
        fuse = compound.getInteger("Fuse");
    }
}
