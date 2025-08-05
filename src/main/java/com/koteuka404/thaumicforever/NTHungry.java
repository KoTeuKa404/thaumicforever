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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;

public class NTHungry extends NTNormal {
    public NTHungry(int id) {
        super(id);
    }
    private final Map<UUID, Integer> entityTicksInRange = new HashMap<>();

    private int getHungryRange(EntityAuraNode node) {
        int base = 3;
        int modifier = Math.max(0, node.getNodeSize() / 10);
        return Math.min(11, base + modifier);
    }

    @Override
    void performTickEvent(EntityAuraNode node) {
        World world = node.world;
        double cx = node.posX + 0.5;
        double cy = node.posY + 0.5;
        double cz = node.posZ + 0.5;
        int pullRadius = getHungryRange(node);

        AxisAlignedBB box = new AxisAlignedBB(
            cx - pullRadius, cy - pullRadius, cz - pullRadius,
            cx + pullRadius, cy + pullRadius, cz + pullRadius
        );

        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);
        for (EntityItem item : items) {
            Vec3d pos = new Vec3d(item.posX, item.posY, item.posZ);
            Vec3d center = new Vec3d(cx, cy, cz);
            double dist = pos.distanceTo(center);
            if (dist > 0.12) {
                Vec3d diff = center.subtract(pos).normalize();
                double factor = (1.0 - dist / pullRadius);
                double baseStrength = 0.17;
                double minStrength = 0.038;
                double yBoost = 1.6;

                double strength = Math.max(minStrength, baseStrength * factor);

                item.motionX += diff.x * strength;
                item.motionY += diff.y * strength * yBoost;
                item.motionZ += diff.z * strength;
                item.velocityChanged = true;
            }
        }

        final int DAMAGE_TICK_DELAY = 10;
        List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, box);

        float nodeCore = 0.8f + node.getNodeSize() * 0.08f;
        float damageRadius = nodeCore * 2.0f;
        damageRadius = Math.min(damageRadius, 2.0f);

        AxisAlignedBB damageBox = new AxisAlignedBB(
            cx - damageRadius, cy - damageRadius, cz - damageRadius,
            cx + damageRadius, cy + damageRadius, cz + damageRadius
        );

        for (EntityLivingBase ent : living) {
            if (ent instanceof net.minecraft.entity.player.EntityPlayer && ((net.minecraft.entity.player.EntityPlayer)ent).isCreative()) continue;
            Vec3d pos = new Vec3d(ent.posX, ent.posY, ent.posZ);
            Vec3d center = new Vec3d(cx, cy, cz);
            double dist = pos.distanceTo(center);

            if (dist > nodeCore * 0.5) {
                Vec3d diff = center.subtract(pos).normalize();
                double factor = (1.0 - dist / pullRadius);
                double baseStrength = 0.14;
                double minStrength = 0.048;
                double yBoost = 1.7;

                double strength = Math.max(minStrength, baseStrength * factor);

                ent.motionX += diff.x * strength;
                ent.motionY += diff.y * strength * yBoost;
                ent.motionZ += diff.z * strength;
                ent.fallDistance = 0f;
                ent.onGround = false;
                ent.velocityChanged = true;
            }

            if (damageBox.intersects(ent.getEntityBoundingBox()) && ent.ticksExisted % DAMAGE_TICK_DELAY == 0) {
                ent.hurtResistantTime = 0;
                float damage = (1.0F + node.getNodeSize() * 0.08f) / 2.0f;
                ent.attackEntityFrom(DamageSource.MAGIC, damage);
            }
        }

        entityTicksInRange.keySet().removeIf(uuid ->
            living.stream().noneMatch(ent -> ent.getUniqueID().equals(uuid))
        );

        if (world.isRemote) {
            spawnHungryNodeParticles(node, pullRadius);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void spawnHungryNodeParticles(EntityAuraNode node, int pullRadius) {
        Random rand = net.minecraft.client.Minecraft.getMinecraft().world.rand;
        for (int i = 0; i < 2 + node.getNodeSize() / 10; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = 0.15 + Math.random() * pullRadius * 0.8;
            double px = node.posX + 0.5 + Math.cos(angle) * radius;
            double py = node.posY + 0.2 + Math.random() * 0.5;
            double pz = node.posZ + 0.5 + Math.sin(angle) * radius;
            net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(
                new net.minecraft.client.particle.Particle(
                    net.minecraft.client.Minecraft.getMinecraft().world,
                    px, py, pz,
                    0, 0, 0
                ) {
                    {
                        this.particleRed = 0.0f;
                        this.particleGreen = 0.0f;
                        this.particleBlue = 0.0f;
                        this.particleAlpha = 0.20f + rand.nextFloat() * 0.17f;
                        this.particleScale = 0.14f + rand.nextFloat() * 0.15f;
                        this.particleMaxAge = 16 + rand.nextInt(10);
                    }

                    @Override
                    public int getFXLayer() {
                        return 1;
                    }
                }
            );
        }
    }

    @Override
    void performPeriodicEvent(EntityAuraNode node) {
        World world = node.world;
        if (world.isRemote) return;
        Random rand = world.rand;

        double cx = node.posX + 0.5;
        double cy = node.posY + 0.5;
        double cz = node.posZ + 0.5;

        double absorbRadius = 1.1;
        AxisAlignedBB absorbBox = new AxisAlignedBB(
            cx - absorbRadius, cy - absorbRadius, cz - absorbRadius,
            cx + absorbRadius, cy + absorbRadius, cz + absorbRadius
        );
        List<EntityItem> absorbItems = world.getEntitiesWithinAABB(EntityItem.class, absorbBox);
        for (EntityItem ei : absorbItems) {
            ItemStack stack = ei.getItem();
            absorbStackAspects(node, stack, "[HungryNode] Поглинуто", stack.toString());
            if (!world.isRemote && world instanceof net.minecraft.world.WorldServer) {
                double nx = cx, ny = cy, nz = cz;
                double ex = ei.posX, ey = ei.posY, ez = ei.posZ;
                for (int i = 0; i < 6; i++) {
                    double px = ex + (nx - ex) * world.rand.nextDouble();
                    double py = ey + (ny - ey) * world.rand.nextDouble();
                    double pz = ez + (nz - ez) * world.rand.nextDouble();
                    ((net.minecraft.world.WorldServer) world).spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        px, py, pz, 1, 0, 0, 0, 0.08
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
                    if (!state.getBlock().isFullBlock(state) || state.getBlockHardness(world, bp) < 0f || state.getBlockHardness(world, bp) > 5f)
                        continue;
                    double dist = bp.distanceSq(center);
                    if (dist < minDist) {
                        minDist = dist;
                        closestBlock = bp;
                    }
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
                        EnumParticleTypes.SMOKE_NORMAL,
                        px, py, pz, 1, 0, 0, 0, 0.11
                    );
                }
            }
        }

        int drain = calculateStrength(node);
        float frac = (float)AuraHelper.getVis(world, node.getPosition()) /
                     AuraHelper.getAuraBase(world, node.getPosition());
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
