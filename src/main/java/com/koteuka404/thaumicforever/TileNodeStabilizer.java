package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileNodeStabilizer extends TileThaumcraft implements ITickable {
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
