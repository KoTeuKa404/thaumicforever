package com.koteuka404.thaumicforever.client.render;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.network.PacketSkyBeamFX;

@SideOnly(Side.CLIENT)
public class SkyBeamClientRenderer {
    private static final ResourceLocation BEAM_TEX = new ResourceLocation("thaumicforever", "textures/misc/beamh.png");
    private static final ResourceLocation RING_TEX = new ResourceLocation("thaumicforever", "textures/misc/4particles.png");
    private static final CopyOnWriteArrayList<BeamInstance> BEAMS = new CopyOnWriteArrayList<>();

    private static class BeamInstance {
        final double x1, y1, z1, x2, y2, z2;
        final long endMillis;
        final int color;
        final float width;

        BeamInstance(PacketSkyBeamFX msg) {
            this.x1 = msg.x1;
            this.y1 = msg.y1;
            this.z1 = msg.z1;
            this.x2 = msg.x2;
            this.y2 = msg.y2;
            this.z2 = msg.z2;
            this.endMillis = System.currentTimeMillis() + Math.max(1, msg.lifeTicks) * 50L;
            this.color = msg.color;
            this.width = Math.max(0.02f, msg.width);
        }
    }

    public static void addBeam(PacketSkyBeamFX msg) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> BEAMS.add(new BeamInstance(msg)));
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null || BEAMS.isEmpty()) return;

        long now = System.currentTimeMillis();
        Iterator<BeamInstance> it = BEAMS.iterator();
        while (it.hasNext()) {
            BeamInstance b = it.next();
            if (b.endMillis <= now) {
                BEAMS.remove(b);
                continue;
            }
            float alpha = (float) Math.min(1.0, Math.max(0.0, (b.endMillis - now) / 220.0));
            renderBeam(mc, b, alpha);
        }
    }

    private static void renderBeam(Minecraft mc, BeamInstance b, float alpha) {
        double sx = b.x1, sy = b.y1, sz = b.z1;
        double ex = b.x2, ey = b.y2, ez = b.z2;
        double dx = ex - sx, dy = ey - sy, dz = ez - sz;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 1.0E-4D) return;

        float r = ((b.color >> 16) & 0xFF) / 255f;
        float g = ((b.color >> 8) & 0xFF) / 255f;
        float bl = (b.color & 0xFF) / 255f;
        float w = b.width;

        double cx = mc.getRenderManager().viewerPosX;
        double cy = mc.getRenderManager().viewerPosY;
        double cz = mc.getRenderManager().viewerPosZ;
        double midx = (sx + ex) * 0.5D, midy = (sy + ey) * 0.5D, midz = (sz + ez) * 0.5D;
        double vx = cx - midx, vy = cy - midy, vz = cz - midz;
        double bx = dx / dist, by = dy / dist, bz = dz / dist;
        double wx = vy * bz - vz * by;
        double wy = vz * bx - vx * bz;
        double wz = vx * by - vy * bx;
        double wlen = Math.sqrt(wx * wx + wy * wy + wz * wz);
        if (wlen > 1.0E-4D) {
            wx /= wlen;
            wy /= wlen;
            wz /= wlen;
        }
        wx *= w;
        wy *= w;
        wz *= w;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-cx, -cy, -cz);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);

        mc.getTextureManager().bindTexture(BEAM_TEX);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        renderBeamQuad(buf, tess, sx, sy, sz, ex, ey, ez, wx, wy, wz, r, g, bl, alpha);
        double[] v2 = rotateAroundAxis(wx, wy, wz, bx, by, bz, Math.toRadians(60.0));
        renderBeamQuad(buf, tess, sx, sy, sz, ex, ey, ez, v2[0], v2[1], v2[2], r, g, bl, alpha * 0.85f);
        double[] v3 = rotateAroundAxis(wx, wy, wz, bx, by, bz, Math.toRadians(-60.0));
        renderBeamQuad(buf, tess, sx, sy, sz, ex, ey, ez, v3[0], v3[1], v3[2], r, g, bl, alpha * 0.85f);

        mc.getTextureManager().bindTexture(RING_TEX);
        renderSourceRing(buf, tess, sx, sy, sz, cx, cy, cz, 0.22D + w * 1.6D, alpha);

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void renderBeamQuad(BufferBuilder buf, Tessellator tess,
                                       double sx, double sy, double sz,
                                       double ex, double ey, double ez,
                                       double wx, double wy, double wz,
                                       float r, float g, float b, float a) {
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(sx - wx, sy - wy, sz - wz).tex(0, 0).color(r, g, b, a).endVertex();
        buf.pos(ex - wx, ey - wy, ez - wz).tex(1, 0).color(r, g, b, a).endVertex();
        buf.pos(ex + wx, ey + wy, ez + wz).tex(1, 1).color(r, g, b, a).endVertex();
        buf.pos(sx + wx, sy + wy, sz + wz).tex(0, 1).color(r, g, b, a).endVertex();
        tess.draw();
    }

    private static void renderSourceRing(BufferBuilder buf, Tessellator tess,
                                         double x, double y, double z,
                                         double camX, double camY, double camZ,
                                         double size, float alpha) {
        double nx = camX - x;
        double ny = camY - y;
        double nz = camZ - z;
        double nlen = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (nlen < 1.0E-4D) return;
        nx /= nlen;
        ny /= nlen;
        nz /= nlen;

        double ux = 0.0D, uy = 1.0D, uz = 0.0D;
        if (Math.abs(ny) > 0.95D) {
            ux = 1.0D; uy = 0.0D; uz = 0.0D;
        }

        double rx = uy * nz - uz * ny;
        double ry = uz * nx - ux * nz;
        double rz = ux * ny - uy * nx;
        double rlen = Math.sqrt(rx * rx + ry * ry + rz * rz);
        if (rlen < 1.0E-4D) return;
        rx = (rx / rlen) * size;
        ry = (ry / rlen) * size;
        rz = (rz / rlen) * size;

        double tx = ny * rz - nz * ry;
        double ty = nz * rx - nx * rz;
        double tz = nx * ry - ny * rx;

        double u0 = 960.0D / 1024.0D;
        double u1 = 1024.0D / 1024.0D;
        double v0 = 256.0D / 1024.0D;
        double v1 = 320.0D / 1024.0D;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(x - rx - tx, y - ry - ty, z - rz - tz).tex(u0, v0).color(1f, 1f, 1f, alpha).endVertex();
        buf.pos(x + rx - tx, y + ry - ty, z + rz - tz).tex(u1, v0).color(1f, 1f, 1f, alpha).endVertex();
        buf.pos(x + rx + tx, y + ry + ty, z + rz + tz).tex(u1, v1).color(1f, 1f, 1f, alpha).endVertex();
        buf.pos(x - rx + tx, y - ry + ty, z - rz + tz).tex(u0, v1).color(1f, 1f, 1f, alpha).endVertex();
        tess.draw();
    }

    private static double[] rotateAroundAxis(double vx, double vy, double vz,
                                             double ax, double ay, double az,
                                             double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double dot = vx * ax + vy * ay + vz * az;
        double rx = vx * cos + (ay * vz - az * vy) * sin + ax * dot * (1.0D - cos);
        double ry = vy * cos + (az * vx - ax * vz) * sin + ay * dot * (1.0D - cos);
        double rz = vz * cos + (ax * vy - ay * vx) * sin + az * dot * (1.0D - cos);
        return new double[] {rx, ry, rz};
    }
}
