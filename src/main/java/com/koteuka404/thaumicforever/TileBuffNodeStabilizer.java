package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileBuffNodeStabilizer extends TileThaumcraft implements ITickable {
    public int count = 0;
    int delay = 0;
    List<Entity> nodes = null;

    private long lastProcessedTick = -1;
    private long lastLightningAt   = -1;

    @Override
    public void update() {
        long now = world.getTotalWorldTime();
        if (lastProcessedTick == now) return;
        lastProcessedTick = now;

        if (this.nodes == null || this.delay % 100 == 0) {
            this.nodes = EntityUtils.getEntitiesInRange(
                this.world,
                this.pos.getX() + 0.5,
                this.pos.getY() + 1.5,
                this.pos.getZ() + 0.5,
                null,
                EntityAuraNode.class,
                0.5
            );
        }

        if (this.nodes != null) {
            for (Entity e : this.nodes) {
                if (e instanceof EntityAuraNode) {
                    ((EntityAuraNode) e).stablized = false;
                }
            }
        }
        
        if (!this.gettingPower()) {
            boolean notFirst = false;
            for (Entity e : this.nodes) {
                Vec3d v2;
                if (e == null || e.isDead || !(e instanceof EntityAuraNode)) continue;
                EntityAuraNode an = (EntityAuraNode)e;
                an.stablized = !notFirst; 
                Vec3d v1 = new Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 1.5, this.pos.getZ() + 0.5);
                double d = v1.squareDistanceTo(v2 = new Vec3d(an.posX, an.posY, an.posZ));

                if (d > 0.001) {
                    v1 = v1.subtract(v2).normalize();
                    if (notFirst) {
                        an.motionX -= v1.x / 750.0;
                        an.motionY -= v1.y / 750.0;
                        an.motionZ -= v1.z / 750.0;
                    } else {
                        an.motionX += v1.x / 1000.0;
                        an.motionY += v1.y / 1000.0;
                        an.motionZ += v1.z / 1000.0;
                    }
                } else if (notFirst) {
                    an.motionY += 0.005;
                }
                notFirst = true;
            }
        }

        if (this.gettingPower()) {
            if (this.world.isRemote && this.count > 0) --this.count;

            if (this.delay == 0) this.delay = this.world.rand.nextInt(100);
            ++this.delay;
            return;
        }

        if (this.nodes != null && !this.nodes.isEmpty()) {
            Entity e = this.nodes.get(0);
            if (e instanceof EntityAuraNode) {
                EntityAuraNode node = (EntityAuraNode) e;

                node.stablized = true;

                if (!world.isRemote) {
                    if (lastLightningAt < 0 || (now - lastLightningAt) >= 60) {
                        lastLightningAt = now;
                        doNodeTransferLightning(node);
                    }
                }
            }
        }

        if (this.nodes != null && !this.nodes.isEmpty()) {
            Entity e = this.nodes.get(0);
            if (e instanceof EntityAuraNode) {
                EntityAuraNode node = (EntityAuraNode) e;

                double cx = this.pos.getX() + 0.5;
                double cy = this.pos.getY() + 1.5;
                double cz = this.pos.getZ() + 0.5;
                double dx = cx - node.posX;
                double dy = cy - node.posY;
                double dz = cz - node.posZ;
                double distSq = dx * dx + dy * dy + dz * dz;

                if (distSq > 0.0005) {
                    double force = Math.min(0.1, 0.02 + distSq * 0.18);
                    node.motionX += dx * force;
                    node.motionY += dy * force;
                    node.motionZ += dz * force;
                } else {
                    node.motionX *= 0.5;
                    node.motionY *= 0.5;
                    node.motionZ *= 0.5;
                }

                node.stablized = true;

                if (node.getRegenType() == 3) { // Fading -> Slow
                    if (this.world.rand.nextInt(2000) == 0) {
                        node.getDataManager().set(EntityAuraNode.REGEN_TYPE, (byte)2);
                        world.playSound(
                            null, node.posX, node.posY, node.posZ,
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(
                                new net.minecraft.util.ResourceLocation("thaumcraft", "wandfizz")
                            ),
                            net.minecraft.util.SoundCategory.BLOCKS,
                            0.7F, 1.25F
                        );
                    }
                }
                else if (node.getNodeType() == 5 && node.getRegenType() != 0) { // Unstable -> Normal
                    if (this.world.rand.nextInt(1200) == 0) {
                        node.getDataManager().set(EntityAuraNode.REGEN_TYPE, (byte)0);
                        world.playSound(
                            null, node.posX, node.posY, node.posZ,
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(
                                new net.minecraft.util.ResourceLocation("thaumcraft", "wandfizz")
                            ),
                            net.minecraft.util.SoundCategory.BLOCKS,
                            0.7F, 1.05F
                        );
                    }
                }
            }
        }

        if (this.world.isRemote && this.nodes != null && this.nodes.size() > 0) {
            if (!this.gettingPower()) {
                if (this.delay == 0) {
                    this.count = 37;
                }
                if (this.count < 37) {
                    ++this.count;
                }
            } else if (this.count > 0) {
                --this.count;
            }
        }
        if (this.delay == 0) {
            this.delay = this.world.rand.nextInt(100);
        }
        ++this.delay;
    }

    public EntityAuraNode getFirstNode() {
        if (nodes != null && !nodes.isEmpty() && nodes.get(0) instanceof EntityAuraNode) {
            return (EntityAuraNode) nodes.get(0);
        }
        return null;
    }

    private static final int DRAIN_PER_PULSE = 1; 


    private Aspect pickNextAspectSequential(EntityAuraNode victim) {
        if (victim == null) return null;

        java.util.List<Aspect> order = victim.getFixedAspectOrder();
        for (Aspect a : order) {
            if (victim.getNodeAspects().getAmount(a) > 0) return a;
        }

        Aspect[] sorted = victim.getNodeAspects().getAspectsSortedByAmount();
        if (sorted != null) {
            for (Aspect a : sorted) {
                if (victim.getNodeAspects().getAmount(a) > 0) return a;
            }
        }

        for (Aspect a : victim.getNodeAspects().getAspects()) {
            if (victim.getNodeAspects().getAmount(a) > 0) return a;
        }
        return null;
    }

    private void removeAspectCompletely(EntityAuraNode node, Aspect asp) {
        if (node == null || asp == null) return;

        int curActive = node.getNodeAspects().getAmount(asp);
        if (curActive > 0) node.getNodeAspects().reduce(asp, curActive);

        int curOrig = node.getOriginalAspects().getAmount(asp);
        if (curOrig > 0) node.getOriginalAspects().reduce(asp, curOrig);

        java.util.List<Aspect> order = node.getFixedAspectOrder();
        if (order.remove(asp)) node.setFixedAspectOrder(order);

        node.updateSyncAspects();
    }

    public void doNodeTransferLightning(EntityAuraNode stabilized) {
        if (stabilized == null || stabilized.isDead || stabilized.isTfCharged()) return;

        java.util.List<Entity> candidates = thaumcraft.common.lib.utils.EntityUtils.getEntitiesInRange(
            world, stabilized.posX, stabilized.posY, stabilized.posZ, stabilized,
            EntityAuraNode.class, 4.5
        );
    
        EntityAuraNode victim = null;
        double minDist = 99;
        for (Entity e : candidates) {
            if (e instanceof EntityAuraNode && e != stabilized) {
                EntityAuraNode cand = (EntityAuraNode) e;
                if (cand.isDead) continue;
                if (cand.stablized) continue;          
                if (cand.isTfCharged()) continue;     
                double d = stabilized.getDistance(e);
                if (d < minDist) { minDist = d; victim = cand; }
            }
        }
        if (victim == null) return;

        Aspect asp = pickNextAspectSequential(victim);
        if (asp == null) return;

        int cur = victim.getNodeAspects().getAmount(asp);
        int take = Math.min(DRAIN_PER_PULSE, cur);     
        if (take <= 0) return;

        victim.getNodeAspects().reduce(asp, take);
        victim.getOriginalAspects().reduce(asp, take);
        victim.updateSyncAspects();

        stabilized.getNodeAspects().add(asp, take);
        stabilized.getOriginalAspects().add(asp, take);
        stabilized.addAspectToOrderIfMissing(asp);
        stabilized.updateSyncAspects();

        if (victim.getNodeAspects().getAmount(asp) <= 0) {
            removeAspectCompletely(victim, asp);
        }

        boolean depleted =
            victim.getNodeAspects().visSize() <= 0 ||
            victim.getNodeAspects().getAspects() == null ||
            victim.getNodeAspects().getAspects().length == 0;

        if (depleted) {
            victim.setDead();
            world.playSound(null, victim.posX, victim.posY, victim.posZ,
                net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("thaumcraft","wandfail")),
                net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 0.85F
            );
        } else {
            world.playSound(null, stabilized.posX, stabilized.posY, stabilized.posZ,
                net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("thaumcraft","wandfizz")),
                net.minecraft.util.SoundCategory.BLOCKS, 0.6F, 1.18F
            );
        }

        if (!world.isRemote) {
            double x1 = stabilized.posX, y1 = stabilized.posY, z1 = stabilized.posZ;
            double x2 = victim.posX,     y2 = victim.posY,     z2 = victim.posZ;

            if (world.rand.nextFloat() < 0.3f) {
                double midX = (x1 + x2) * 0.5, midY = (y1 + y2) * 0.5, midZ = (z1 + z2) * 0.5;
                double off  = 0.5 + world.rand.nextDouble() * 0.5;
                midX += (world.rand.nextDouble() - 0.5) * off;
                midY += (world.rand.nextDouble() - 0.5) * off;
                midZ += (world.rand.nextDouble() - 0.5) * off;

                ThaumicForever.network.sendToAllAround(
                    new PacketLightningFX(x1, y1, z1, x2, y2, z2, true, midX, midY, midZ),
                    new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(
                        world.provider.getDimension(), x1, y1, z1, 32
                    )
                );
            } else {
                ThaumicForever.network.sendToAllAround(
                    new PacketLightningFX(x1, y1, z1, x2, y2, z2),
                    new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(
                        world.provider.getDimension(), x1, y1, z1, 32
                    )
                );
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.nodes != null) {
            for (Entity e : this.nodes) {
                if (e instanceof EntityAuraNode) {
                    ((EntityAuraNode) e).stablized = false;
                }
            }
        }
    }
    
    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (this.nodes != null) {
            for (Entity e : this.nodes) {
                if (e instanceof EntityAuraNode) {
                    ((EntityAuraNode) e).stablized = false;
                }
            }
        }
    }
    
    @SideOnly(value=Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
            this.getPos().getX() - 0.3, this.getPos().getY() - 0.3, this.getPos().getZ() - 0.3, 
            this.getPos().getX() + 1.3, this.getPos().getY() + 1.3, this.getPos().getZ() + 1.3
        );
    }
}
