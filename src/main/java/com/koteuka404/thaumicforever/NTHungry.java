package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;

public class NTHungry extends NTNormal {
    public NTHungry(int id) { super(id); }

    private int getHungryRange(EntityAuraNode node) {
        int base = 3;
        int modifier = Math.max(0, node.getNodeSize() / 10);
        return Math.min(MAX_PULL_RADIUS, base + modifier);
    }

    private static final int  DAMAGE_TICK_DELAY = 7;          
    private static final float DAMAGE_SCALE     = 1.0f/1.5f;   

    private static final double DESIRED_SPEED_BASE  = 0.22;    
    private static final double DESIRED_SPEED_BONUS = 0.24;   
    private static final double STEER_GAIN          = 0.35;    
    private static final double MIN_VERTICAL_LIFT   = 0.10;    
    private static final double MAX_SPEED           = 0.90;   
    private static final int MAX_PULL_RADIUS = 8;

    private final Map<UUID, Long> lastHitTickByEntity = new HashMap<>();

    private static Vec3d nodeCenter(EntityAuraNode node) {
        return new Vec3d(node.posX + 0.5, node.posY + 0.6, node.posZ + 0.5);
    }

    @Override
    void performTickEvent(EntityAuraNode node) {
        World world = node.world;
        if (world.isRemote) return;

        Vec3d c = nodeCenter(node);
        double cx = c.x, cy = c.y, cz = c.z;

        int pullRadius = getHungryRange(node);
        AxisAlignedBB box = new AxisAlignedBB(
            cx - pullRadius, cy - pullRadius, cz - pullRadius,
            cx + pullRadius, cy + pullRadius, cz + pullRadius
        );

        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);
        for (EntityItem item : items) {
            Vec3d pos = new Vec3d(item.posX, item.posY, item.posZ);
            double dist = pos.distanceTo(c);
            if (dist > 0.12) {
                Vec3d dir = c.subtract(pos).normalize();
                double factor = Math.max(0.0, 1.0 - Math.min(dist, pullRadius) / pullRadius);
                double strength = Math.max(0.030, 0.13 * factor); 
                item.motionX += dir.x * strength;
                item.motionY += dir.y * strength * 1.45;        
                item.motionZ += dir.z * strength;
                item.velocityChanged = true;
            }
        }

        List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, box);

        float nodeCore = 0.8f + node.getNodeSize() * 0.08f;
        float oldRadius = Math.min(2.0f, nodeCore * 2.0f);
        float damageRadius = Math.max(0.25f, oldRadius - 1.0f);
        AxisAlignedBB damageBox = new AxisAlignedBB(
            cx - damageRadius, cy - damageRadius, cz - damageRadius,
            cx + damageRadius, cy + damageRadius, cz + damageRadius
        );

        for (EntityLivingBase ent : living) {
            if (ent instanceof net.minecraft.entity.player.EntityPlayer &&
                ((net.minecraft.entity.player.EntityPlayer)ent).isCreative()) continue;

            Vec3d pos = new Vec3d(ent.posX, ent.posY, ent.posZ);
            double dist = pos.distanceTo(c);

            if (dist > 0.1) {
                Vec3d dir = c.subtract(pos).normalize();
                double factor = Math.max(0.0, 1.0 - Math.min(dist, pullRadius) / pullRadius);
                double desiredSpeed = DESIRED_SPEED_BASE + DESIRED_SPEED_BONUS * factor;
                Vec3d desiredVel = new Vec3d(dir.x * desiredSpeed, dir.y * desiredSpeed, dir.z * desiredSpeed);

                ent.motionX += (desiredVel.x - ent.motionX) * STEER_GAIN;
                ent.motionY += (desiredVel.y - ent.motionY) * STEER_GAIN;
                ent.motionZ += (desiredVel.z - ent.motionZ) * STEER_GAIN;

                if (ent.onGround || ent.collidedHorizontally) {
                    ent.motionY = Math.max(ent.motionY, MIN_VERTICAL_LIFT + 0.10 * factor);
                }

                ent.isAirBorne = true;
                ent.onGround = false;
                ent.fallDistance = 0f;
                ent.velocityChanged = true;

                double v2 = ent.motionX*ent.motionX + ent.motionY*ent.motionY + ent.motionZ*ent.motionZ;
                double maxV2 = MAX_SPEED*MAX_SPEED;
                if (v2 > maxV2) {
                    double k = Math.sqrt(maxV2 / v2);
                    ent.motionX *= k; ent.motionY *= k; ent.motionZ *= k;
                }
            }

            boolean inDamage = damageBox.intersects(ent.getEntityBoundingBox())
                             || dist <= (double)damageRadius * 0.85;
            if (inDamage) {
                long now  = world.getTotalWorldTime();
                long last = lastHitTickByEntity.getOrDefault(ent.getUniqueID(), -1000L);
                if (now - last >= DAMAGE_TICK_DELAY) {
                    float base = (1.0F + node.getNodeSize() * 0.08f) / 2.0f;
                    float dmg  = Math.min(3.0F, Math.max(0.5F, base * DAMAGE_SCALE));
                    ent.attackEntityFrom(DamageSource.MAGIC, dmg);
                    lastHitTickByEntity.put(ent.getUniqueID(), now);
                }
            }
        }

        lastHitTickByEntity.keySet().removeIf(uuid ->
            living.stream().noneMatch(e -> e.getUniqueID().equals(uuid))
        );
    }

    @Override
    void performPeriodicEvent(EntityAuraNode node) {
        World world = node.world;
        if (world.isRemote) return;
        Random rand = world.rand;

        Vec3d c = nodeCenter(node);
        double cx = c.x, cy = c.y, cz = c.z;

        double absorbRadius = 1.1;
        AxisAlignedBB absorbBox = new AxisAlignedBB(
            cx - absorbRadius, cy - absorbRadius, cz - absorbRadius,
            cx + absorbRadius, cy + absorbRadius, cz + absorbRadius
        );
        List<EntityItem> absorbItems = world.getEntitiesWithinAABB(EntityItem.class, absorbBox);
        for (EntityItem ei : absorbItems) {
            ItemStack stack = ei.getItem();
            absorbStackAspects(node, stack, "[HungryNode] Поглинуто", stack.toString());
            if (world instanceof net.minecraft.world.WorldServer) {
                double nx = cx, ny = cy, nz = cz;
                double ex = ei.posX, ey = ei.posY, ez = ei.posZ;
                for (int i = 0; i < 6; i++) {
                    double px = ex + (nx - ex) * world.rand.nextDouble();
                    double py = ey + (ny - ey) * world.rand.nextDouble();
                    double pz = ez + (nz - ez) * world.rand.nextDouble();
                    ((net.minecraft.world.WorldServer) world).spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL, px, py, pz, 1, 0, 0, 0, 0.08
                    );
                }
            }
            ei.setDead();
        }

        decomposeAllToPrimals(node.getNodeAspects(), node);

        BlockPos below = node.getPosition().down();
        if (world.isBlockLoaded(below)) {
            IBlockState bs = world.getBlockState(below);
            float hardness = bs.getBlockHardness(world, below);
            if (!bs.getMaterial().isLiquid() && hardness < 5.0f && hardness >= 0f) {
                ItemStack blockStack = new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs));
                absorbStackAspects(node, blockStack, "[HungryNode] Поглинуто", "блоку " + blockStack);
                world.setBlockToAir(below);
            }
        }

        int blockRadius = getHungryRange(node);
        BlockPos center = node.getPosition();
        BlockPos closestBlock = null;
        double minDist = Double.MAX_VALUE;
        for (int dx = -blockRadius; dx <= blockRadius; dx++) {
            for (int dy = -blockRadius; dy <= blockRadius; dy++) {
                for (int dz = -blockRadius; dz <= blockRadius; dz++) {
                    BlockPos bp = center.add(dx, dy, dz);
                    if (bp.equals(center) || world.isAirBlock(bp)) continue;
                    IBlockState state = world.getBlockState(bp);
                    if (state.getMaterial().isLiquid()) continue;
                    if (!state.getBlock().isFullBlock(state)
                        || state.getBlockHardness(world, bp) < 0f
                        || state.getBlockHardness(world, bp) > 5f) continue;
                    double d2 = bp.distanceSq(center);
                    if (d2 < minDist) { minDist = d2; closestBlock = bp; }
                }
            }
        }
        if (closestBlock != null) {
            IBlockState state = world.getBlockState(closestBlock);
            ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            absorbStackAspects(node, stack, "[HungryNode] Поглинуто", "блок " + stack);
            world.setBlockToAir(closestBlock);

            if (world instanceof net.minecraft.world.WorldServer) {
                double ex = closestBlock.getX() + 0.5;
                double ey = closestBlock.getY() + 0.5;
                double ez = closestBlock.getZ() + 0.5;
                for (int i = 0; i < 7; i++) {
                    double px = ex + (cx - ex) * world.rand.nextDouble();
                    double py = ey + (cy - ey) * world.rand.nextDouble();
                    double pz = ez + (cz - ez) * world.rand.nextDouble();
                    ((net.minecraft.world.WorldServer) world).spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL, px, py, pz, 1, 0, 0, 0, 0.11
                    );
                }
            }
        }

        int drain = calculateStrength(node);
        float frac = (float) AuraHelper.getVis(world, node.getPosition()) /
                     Math.max(1f, AuraHelper.getAuraBase(world, node.getPosition()));
        if (rand.nextFloat() < frac && rand.nextInt(1 + node.getNodeSize() * 2) == 0) {
            AuraHandler.drainVis(world, node.getPosition(), drain, false);
            node.setNodeSize(node.getNodeSize() + 1);
        }
    }

    private void decomposeAllToPrimals(AspectList al, EntityAuraNode node) {
        boolean changed;
        do {
            changed = false;
            Aspect[] aspects = al.getAspects();
            for (Aspect asp : aspects) {
                if (asp == Aspect.VOID) continue;
                if (!asp.isPrimal()) {
                    int amt = al.getAmount(asp);
                    int decomposeAmt = amt / 2;
                    if (decomposeAmt > 0) {
                        al.remove(asp, decomposeAmt * 2);
                        Aspect[] comps = asp.getComponents();
                        if (comps != null && comps.length == 2) {
                            al.add(comps[0], decomposeAmt);
                            al.add(comps[1], decomposeAmt);
                            node.addAspectToOrderIfMissing(comps[0]);
                            node.addAspectToOrderIfMissing(comps[1]);
                        }
                        changed = true;
                    }
                }
            }
        } while (changed);
        node.updateSyncAspects();
    }

    private void absorbStackAspects(EntityAuraNode node, ItemStack stack, String prefix, String suffix) {
        AspectList al = AspectHelper.getObjectAspects(stack);
        if ((al == null || al.size() == 0) && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("Aspects")) {
                al = new AspectList();
                NBTTagCompound aspectsTag = tag.getCompoundTag("Aspects");
                for (String key : aspectsTag.getKeySet()) {
                    Aspect asp = Aspect.getAspect(key);
                    int amt = aspectsTag.getInteger(key);
                    if (asp != null && amt > 0) al.add(asp, amt);
                }
            }
        }
        if (al != null && al.size() > 0) {
            for (Aspect asp : al.getAspects()) {
                int amt = al.getAmount(asp);
                processHungryNodeAbsorption(node, asp, amt);
            }
        }
    }

    public void processHungryNodeAbsorption(EntityAuraNode node, Aspect aspect, int amt) {
        node.getNodeAspects().add(aspect, amt);
        node.addAspectToOrderIfMissing(aspect);
        decomposeAllToPrimals(node.getNodeAspects(), node);
        node.updateSyncAspects();
    }

    @Override
    int calculateStrength(EntityAuraNode node) {
        return Math.max(1, (int)(super.calculateStrength(node) * 0.1f));
    }
}
