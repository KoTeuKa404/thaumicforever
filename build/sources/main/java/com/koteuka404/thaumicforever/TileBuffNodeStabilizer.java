package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileBuffNodeStabilizer extends TileThaumcraft implements ITickable {
    public int count = 0;
    int delay = 0;
    List<Entity> nodes = null;

    @Override
    public void update() {
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

                if (node.getRegenType() == 3) { // 3 = Fading
                    if (this.world.rand.nextInt(2000) == 0) {
                        node.getDataManager().set(EntityAuraNode.REGEN_TYPE, (byte)2); // 2 = Slow
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
                else if (node.getNodeType() == 5 && node.getRegenType() != 0) { // 5 = Unstable
                    if (this.world.rand.nextInt(1200) == 0) {
                        node.getDataManager().set(EntityAuraNode.REGEN_TYPE, (byte)0); // 0
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

    @Override
    public void invalidate() {
        super.invalidate();

        List<Entity> nodes = EntityUtils.getEntitiesInRange(
            this.world, 
            this.pos.getX() + 0.5, 
            this.pos.getY() + 1.5, 
            this.pos.getZ() + 0.5, 
            null, 
            EntityAuraNode.class, 
            0.6
        );

        if (nodes != null) {
            for (Entity e : nodes) {
                if (e instanceof EntityAuraNode) {
                    EntityAuraNode node = (EntityAuraNode) e;
                    node.stablized = false;

                    // System.out.println("[TileBuffNodeStabilizer.invalidate] Node aspects: " + node.getNodeAspects());

                    if (node.isAllAspectsBelow(2)) {
                        // System.out.println("[TileBuffNodeStabilizer.invalidate] KILL NODE: " + node + " aspects=" + node.getNodeAspects());
                        world.playSound(
                            null,
                            node.posX, node.posY, node.posZ,
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("thaumcraft", "wandfail")),
                            net.minecraft.util.SoundCategory.BLOCKS,
                            1.0F, 1.0F
                        );
                        node.setDead();
                    } else {
                        // System.out.println("[TileBuffNodeStabilizer.invalidate] NODE SURVIVES: " + node + " aspects=" + node.getNodeAspects());
                    }
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

    public EntityAuraNode getFirstNode() {
        if (nodes != null && !nodes.isEmpty() && nodes.get(0) instanceof EntityAuraNode) {
            return (EntityAuraNode) nodes.get(0);
        }
        return null;
    }

    public EntityAuraNode getNodeForAspect(String tag) {
        if (nodes != null) {
            for (Entity e : nodes) {
                if (e instanceof EntityAuraNode) {
                    EntityAuraNode node = (EntityAuraNode) e;
                    Aspect main = node.getMainAspect();
                    if (main != null && main.getTag().equals(tag))
                        return node;
                }
            }
        }
        return null;
    }
    
    @SideOnly(value=Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
            this.getPos().getX() - 0.3, this.getPos().getY() - 0.3, this.getPos().getZ() - 0.3, 
            this.getPos().getX() + 1.3, this.getPos().getY() + 1.3, this.getPos().getZ() + 1.3
        );
    }
}
