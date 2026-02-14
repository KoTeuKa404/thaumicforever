package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class FXLightningBolt extends Particle {
    private final double x2, y2, z2;

    private static final float BASE_WIDTH  = 0.02125f;
    private static final float GLOW_SCALE  = 2.4f;
    private static final int   SEGMENTS    = 8;
    private static final float JITTER_AMPL = 0.07f;

    private final long seed;

    private float outerR = 1.0f, outerG = 0.58f, outerB = 1.0f; 
    private float innerR = 1.0f, innerG = 0.82f, innerB = 1.0f; 

    public FXLightningBolt(World world, double x1, double y1, double z1,
                           double x2, double y2, double z2) {
        super(world, x1, y1, z1, 0, 0, 0);
        this.x2 = x2; this.y2 = y2; this.z2 = z2;

        this.motionX = this.motionY = this.motionZ = 0;
        this.prevPosX = this.posX = x1;
        this.prevPosY = this.posY = y1;
        this.prevPosZ = this.posZ = z1;

        this.particleMaxAge = 8;
        this.canCollide = false;

        this.seed = world.rand.nextLong();
    }

    public FXLightningBolt(World world, double x1, double y1, double z1,
                           double x2, double y2, double z2,
                           boolean white) {
        this(world, x1, y1, z1, x2, y2, z2);
        if (white) {
            this.outerR = 0.95f; this.outerG = 0.95f; this.outerB = 1.00f;
            this.innerR = 1.00f; this.innerG = 1.00f; this.innerB = 1.00f;
        }
    }

    @Override
    public void onUpdate() {
        if (++this.particleAge >= this.particleMaxAge) setExpired();
    }

    @Override
    public void renderParticle(
        net.minecraft.client.renderer.BufferBuilder buffer, Entity entityIn, float partialTicks,
        float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ
    ) {
        double sx = this.posX - interpPosX;
        double sy = this.posY - interpPosY;
        double sz = this.posZ - interpPosZ;
        double ex = this.x2   - interpPosX;
        double ey = this.y2   - interpPosY;
        double ez = this.z2   - interpPosZ;

        double dx = ex - sx, dy = ey - sy, dz = ez - sz;
        double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len < 1e-4) return;
        dx /= len; dy /= len; dz /= len;

        double cx = Minecraft.getMinecraft().getRenderManager().viewerPosX - (sx + ex) * 0.5;
        double cy = Minecraft.getMinecraft().getRenderManager().viewerPosY - (sy + ey) * 0.5;
        double cz = Minecraft.getMinecraft().getRenderManager().viewerPosZ - (sz + ez) * 0.5;

        double sideX = dy * cz - dz * cy;
        double sideY = dz * cx - dx * cz;
        double sideZ = dx * cy - dy * cx;
        double sLen  = Math.sqrt(sideX*sideX + sideY*sideY + sideZ*sideZ);
        if (sLen < 1e-6) return;
        sideX /= sLen; sideY /= sLen; sideZ /= sLen;

        float tNorm = (particleAge + partialTicks) / particleMaxAge;
        float fade  = 1.0f - tNorm;
        float width = BASE_WIDTH * (0.95f + 0.1f * (float)Math.sin(seed * 0.001 + tNorm * 12.0));

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);

        drawRibbon(sx, sy, sz, dx, dy, dz, sideX, sideY, sideZ, len, width * GLOW_SCALE,
                   outerR, outerG, outerB, 0.22f * fade, tNorm, JITTER_AMPL);

        drawRibbon(sx, sy, sz, dx, dy, dz, sideX, sideY, sideZ, len, width,
                   innerR, innerG, innerB, 0.85f * fade, tNorm, JITTER_AMPL * 0.6f);

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawRibbon(double sx, double sy, double sz,
                            double dirX, double dirY, double dirZ,
                            double sideX, double sideY, double sideZ,
                            double length, float halfWidth,
                            float r, float g, float b, float a,
                            float tNorm, float jitterAmpl) {

        java.util.Random rnd = new java.util.Random(seed);
        double jitterPhase = tNorm * 10.0;

        double[] noise = new double[SEGMENTS + 1];
        for (int i = 0; i <= SEGMENTS; i++) {
            noise[i] = (rnd.nextDouble() - 0.5) * 2.0;
        }

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(r, g, b, a);

        for (int i = 0; i < SEGMENTS; i++) {
            double k0 = (double)i / SEGMENTS;
            double k1 = (double)(i + 1) / SEGMENTS;

            double x0 = sx + dirX * (length * k0);
            double y0 = sy + dirY * (length * k0);
            double z0 = sz + dirZ * (length * k0);

            double x1 = sx + dirX * (length * k1);
            double y1 = sy + dirY * (length * k1);
            double z1 = sz + dirZ * (length * k1);

            double j0 = Math.sin(jitterPhase + k0 * 12.0) * jitterAmpl * noise[i];
            double j1 = Math.sin(jitterPhase + k1 * 12.0) * jitterAmpl * noise[i + 1];

            x0 += sideX * j0; y0 += sideY * j0; z0 += sideZ * j0;
            x1 += sideX * j1; y1 += sideY * j1; z1 += sideZ * j1;

            double lx0 = x0 - sideX * halfWidth, ly0 = y0 - sideY * halfWidth, lz0 = z0 - sideZ * halfWidth;
            double rx0 = x0 + sideX * halfWidth, ry0 = y0 + sideY * halfWidth, rz0 = z0 + sideZ * halfWidth;

            double lx1 = x1 - sideX * halfWidth, ly1 = y1 - sideY * halfWidth, lz1 = z1 - sideZ * halfWidth;
            double rx1 = x1 + sideX * halfWidth, ry1 = y1 + sideY * halfWidth, rz1 = z1 + sideZ * halfWidth;

            GL11.glVertex3d(lx0, ly0, lz0);
            GL11.glVertex3d(lx1, ly1, lz1);
            GL11.glVertex3d(rx1, ry1, rz1);
            GL11.glVertex3d(rx0, ry0, rz0);
        }

        GL11.glEnd();
    }
}
