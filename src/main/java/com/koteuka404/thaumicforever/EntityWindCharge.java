package com.koteuka404.thaumicforever;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityWindCharge extends Entity {

    private EntityLivingBase thrower;

    private static final double SPEED       = 1.25;
    private static final double DRAG        = 0.99;
    private static final double GRAVITY     = 0.0;

    private static final double RADIUS      = 4.0;
    private static final double STRENGTH    = 1.5;
    private static final double MAX_Y_BOOST = 1.0;

    private static final int    MAX_TICKS   = 360;

    private static final String NBT_THROWER = "Thrower";
    private static final String NBT_AGE     = "Age";
    private static final String NBT_MX      = "mx";
    private static final String NBT_MY      = "my";
    private static final String NBT_MZ      = "mz";

    private int deflectCooldown = 0;

    public EntityWindCharge(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.noClip = false;
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;
    }

    public EntityWindCharge(World world, EntityLivingBase shooter) {
        this(world);
        this.thrower = shooter;

        Vec3d look = shooter.getLookVec().normalize();
        double off = 0.4;
        this.setPosition(
            shooter.posX + look.x * off,
            shooter.posY + shooter.getEyeHeight() - 0.1 + look.y * off,
            shooter.posZ + look.z * off
        );

        this.motionX = look.x * SPEED;
        this.motionY = look.y * SPEED;
        this.motionZ = look.z * SPEED;

        faceMotion();
    }

    public static EntityWindCharge shoot(World world, EntityLivingBase shooter, float power) {
        EntityWindCharge p = new EntityWindCharge(world, shooter);

        double inacc = 0.0;
        Vec3d look = shooter.getLookVec().normalize();
        p.motionX = look.x * SPEED * power + (world.rand.nextGaussian() * inacc);
        p.motionY = look.y * SPEED * power + (world.rand.nextGaussian() * inacc);
        p.motionZ = look.z * SPEED * power + (world.rand.nextGaussian() * inacc);

        world.spawnEntity(p);

        world.playSound(
            shooter instanceof EntityPlayer ? (EntityPlayer)shooter : null,
            shooter.posX, shooter.posY, shooter.posZ,
            SoundEvents.ENTITY_ARROW_SHOOT,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F / (world.rand.nextFloat() * 0.5F + 1.0F) + power / 2.0F
        );
        return p;
    }

    @Override protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(NBT_AGE)) this.ticksExisted = nbt.getInteger(NBT_AGE);
        if (nbt.hasKey(NBT_MX)) this.motionX = nbt.getDouble(NBT_MX);
        if (nbt.hasKey(NBT_MY)) this.motionY = nbt.getDouble(NBT_MY);
        if (nbt.hasKey(NBT_MZ)) this.motionZ = nbt.getDouble(NBT_MZ);
        if (nbt.hasUniqueId(NBT_THROWER) && this.world instanceof WorldServer) {
            UUID id = nbt.getUniqueId(NBT_THROWER);
            Entity e = ((WorldServer)this.world).getEntityFromUuid(id);
            this.thrower = (e instanceof EntityLivingBase) ? (EntityLivingBase)e : null;
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_AGE, this.ticksExisted);
        nbt.setDouble(NBT_MX, this.motionX);
        nbt.setDouble(NBT_MY, this.motionY);
        nbt.setDouble(NBT_MZ, this.motionZ);
        if (this.thrower != null) nbt.setUniqueId(NBT_THROWER, this.thrower.getUniqueID());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.prevRotationYaw   = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        if (deflectCooldown > 0) deflectCooldown--;

        faceMotion();

        Vec3d start = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d end   = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        RayTraceResult blockHit  = this.world.rayTraceBlocks(start, end);
        RayTraceResult entityHit = rayTraceEntities(start, end);

        boolean hitEntityFirst = entityHit != null &&
                (blockHit == null || start.distanceTo(entityHit.hitVec) < start.distanceTo(blockHit.hitVec));

        if (!world.isRemote) {
            if (hitEntityFirst && entityHit.entityHit != null) {
                onEntityHit(entityHit.entityHit);
            } else if (blockHit != null) {
                onBlockHit(blockHit.getBlockPos());
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;

        this.motionX *= DRAG;
        this.motionY = this.motionY * DRAG - GRAVITY;
        this.motionZ *= DRAG;

        if (this.ticksExisted++ > MAX_TICKS) setDead();
    }

    private void faceMotion() {
        double vx = this.motionX, vy = this.motionY, vz = this.motionZ;
        double h = Math.sqrt(vx*vx + vz*vz);
        if (h < 1.0E-6 && Math.abs(vy) < 1.0E-6) return;
        this.rotationYaw   = (float)(Math.toDegrees(Math.atan2(vz, vx)) - 90.0);
        this.rotationPitch = (float)(-Math.toDegrees(Math.atan2(vy, h)));
    }

    private RayTraceResult rayTraceEntities(Vec3d start, Vec3d end) {
        RayTraceResult hit = null;
        double closest = Double.MAX_VALUE;

        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(
            this,
            this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(0.5)
        );

        for (Entity e : list) {
            if (!e.canBeCollidedWith()) continue;
            if (e == this.thrower && this.ticksExisted <= 5) continue;
            AxisAlignedBB aabb = e.getEntityBoundingBox().grow(0.3);
            RayTraceResult intercept = aabb.calculateIntercept(start, end);
            if (intercept != null) {
                double d = start.distanceTo(intercept.hitVec);
                if (d < closest) { closest = d; hit = new RayTraceResult(e); }
            }
        }
        return hit;
    }

    private void onEntityHit(Entity target) {
        applyWindBurst(new Vec3d(this.posX, this.posY, this.posZ));
        setDead();
    }

    private void onBlockHit(BlockPos pos) {
        applyWindBurst(new Vec3d(this.posX, this.posY, this.posZ));
        setDead();
    }

    private void applyWindBurst(Vec3d epicenter) {
        if (world.isRemote) return;

        AxisAlignedBB area = new AxisAlignedBB(
            posX - RADIUS, posY - RADIUS, posZ - RADIUS,
            posX + RADIUS, posY + RADIUS, posZ + RADIUS
        );

        List<Entity> affected = world.getEntitiesWithinAABBExcludingEntity(this, area);

        if (this.thrower != null && !affected.contains(this.thrower) &&
            this.thrower.getDistance(this) <= RADIUS + 0.5) {
            affected.add(this.thrower);
        }

        for (Entity e : affected) {
            Vec3d delta = e.getPositionVector().subtract(epicenter);
            double dist = Math.max(0.1, delta.lengthVector());
            if (dist > RADIUS) continue;

            double falloff = 1.0 - (dist / RADIUS);
            double push = falloff * STRENGTH;

            Vec3d n = delta.normalize();
            Vec3d h = new Vec3d(n.x, 0.0, n.z);
            double hl = h.lengthVector();
            if (hl > 1.0E-6) h = h.scale(1.0 / hl);
            else h = Vec3d.ZERO;

            double horizScale = 0.60 + 0.25 * (1.0 - falloff);
            double vx = h.x * push * horizScale;
            double vz = h.z * push * horizScale;
            double vy = computeUpwardBoost(e, n, falloff);
            vy += Math.max(0.0, n.y) * 0.10;
            if (vy > MAX_Y_BOOST) vy = MAX_Y_BOOST;

            e.motionX += vx;
            e.motionY += vy;
            e.motionZ += vz;

            if (e instanceof EntityPlayer) ((EntityPlayer)e).fallDistance = 0.0F;
            if (e instanceof EntityLivingBase) ((EntityLivingBase)e).velocityChanged = true;

            if (e.isBurning()) e.extinguish();
            if (e instanceof EntityItem) {
                e.motionX += h.x * 0.1;
                e.motionZ += h.z * 0.1;
            }
        }

        extinguishFireBlocks(epicenter, 2);

        world.setEntityState(this, (byte)3);
    }

    private double computeUpwardBoost(Entity e, Vec3d normDir, double falloff) {
        double baseUp = 0.32 + 0.25 * falloff;
        double dirUp  = Math.max(0.0, normDir.y) * 0.25;
        double groundBoost = (e.onGround ? 0.12 : 0.0);
        double vy = baseUp + dirUp + groundBoost;
        vy = Math.min(vy, 1.0);
        vy *= ceilingFactor(e);
        return Math.max(vy, 0.22);
    }

    private double ceilingFactor(Entity e) {
        double top = e.getEntityBoundingBox().maxY + 0.05;
        Vec3d start = new Vec3d(e.posX, top, e.posZ);
        Vec3d end   = start.addVector(0, 2.1, 0);
        RayTraceResult r = this.world.rayTraceBlocks(start, end, false, true, false);
        return (r != null) ? 0.35 : 1.0;
    }

    private void extinguishFireBlocks(Vec3d epicenter, int radius) {
        int minX = (int)Math.floor(epicenter.x) - radius;
        int minY = (int)Math.floor(epicenter.y) - radius;
        int minZ = (int)Math.floor(epicenter.z) - radius;
        int maxX = (int)Math.floor(epicenter.x) + radius;
        int maxY = (int)Math.floor(epicenter.y) + radius;
        int maxZ = (int)Math.floor(epicenter.z) + radius;

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (world.getBlockState(p).getBlock() == Blocks.FIRE)
                        world.setBlockToAir(p);
                }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3 && world.isRemote) {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();

            for (int i = 0; i < 30; ++i) {
                double ox = (rnd.nextDouble() - 0.5) * 1.4;
                double oy = (rnd.nextDouble() - 0.5) * 0.8;
                double oz = (rnd.nextDouble() - 0.5) * 1.4;

                double mx = ox * 0.15;
                double my = oy * 0.20;
                double mz = oz * 0.15;

                world.spawnParticle(
                        EnumParticleTypes.CLOUD,
                        posX + ox,
                        posY + 0.2 + oy,
                        posZ + oz,
                        mx,
                        my,
                        mz
                );
            }

            int ringCount = 24;
            double ringRadius = 2.2;
            for (int i = 0; i < ringCount; ++i) {
                double angle = (2.0 * Math.PI * i) / ringCount;
                double rx = Math.cos(angle) * ringRadius;
                double rz = Math.sin(angle) * ringRadius;

                double baseX = posX + rx;
                double baseY = posY + 0.1;
                double baseZ = posZ + rz;

                double mx = rx * 0.08 + (rnd.nextDouble() - 0.5) * 0.02;
                double my = 0.08 + (rnd.nextDouble() - 0.5) * 0.04;
                double mz = rz * 0.08 + (rnd.nextDouble() - 0.5) * 0.02;

                world.spawnParticle(
                        EnumParticleTypes.EXPLOSION_NORMAL,
                        baseX,
                        baseY,
                        baseZ,
                        mx,
                        my,
                        mz
                );
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) return false;
        setDead();
        return true;
    }

    public void setDeflectCooldown(int ticks) { this.deflectCooldown = ticks; }
    public boolean canBeDeflected() { return this.deflectCooldown <= 0; }
}
