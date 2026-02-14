package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class HungryFX {
    private HungryFX() {}

    public static void spawnAbsorbTrail(World world, Vec3d from, Vec3d to) {
        if (world == null) return;
        final int segments = 14;
        final double speed = 0.10;
        final double lifeMin = 10, lifeMax = 18;

        Vec3d dir = to.subtract(from);
        double len = dir.lengthVector();
        if (len < 1e-4) return;
        Vec3d n = dir.scale(1.0 / len);

        for (int i = 0; i <= segments; i++) {
            double t = i / (double)segments;
            double arc = Math.sin(Math.PI * t) * 0.15 * Math.min(1.0, len / 6.0);
            Vec3d side = new Vec3d(-n.z, 0, n.x).scale(arc);

            Vec3d p = from.add(n.scale(len * t)).add(side);
            double mx = n.x * speed;
            double my = n.y * speed;
            double mz = n.z * speed;

            Particle par = new Particle(world, p.x, p.y, p.z, mx, my, mz) {
                {
                    this.particleRed = 0.0f;
                    this.particleGreen = 0.0f;
                    this.particleBlue = 0.0f;
                    this.particleAlpha = 0.22f + world.rand.nextFloat() * 0.18f;
                    this.particleScale = 0.12f + world.rand.nextFloat() * 0.18f;
                    this.particleMaxAge = (int)(lifeMin + world.rand.nextDouble() * (lifeMax - lifeMin));
                }
                @Override public void onUpdate() {
                    super.onUpdate();
                    this.motionX *= 1.04;
                    this.motionY *= 1.04;
                    this.motionZ *= 1.04;
                }
                @Override public int getFXLayer() { return 1; }
            };

            Minecraft.getMinecraft().effectRenderer.addEffect(par);
        }
    }
}
