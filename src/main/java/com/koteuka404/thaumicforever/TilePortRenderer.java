package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TilePortRenderer extends TileEntitySpecialRenderer<TilePort> {

    private static final ResourceLocation BEAM_TEX = new ResourceLocation("thaumicforever", "textures/misc/beamh.png");
    @Override
    public void render(TilePort te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        BlockPos target = te.getTargetPort();
        if (target == null) return;
    
        double sx = x + 0.5, sy = y + 0.5, sz = z + 0.5;
        double ex = x + (target.getX() - te.getPos().getX()) + 0.5;
        double ey = y + (target.getY() - te.getPos().getY()) + 0.5;
        double ez = z + (target.getZ() - te.getPos().getZ()) + 0.5;
        double dx = ex - sx, dy = ey - sy, dz = ez - sz;
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
    
        int color = te.getBeamColor();
    
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = 1f;
        float beamWidth = 0.33f;
    
        // == BILLBOARD beam ==
        double cx = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double cy = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double cz = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
        double midx = (sx + ex) / 2.0, midy = (sy + ey) / 2.0, midz = (sz + ez) / 2.0;
        double vx = cx - midx, vy = cy - midy, vz = cz - midz;
        double bx = dx / dist, by = dy / dist, bz = dz / dist;
        double wx = vy * bz - vz * by;
        double wy = vz * bx - vx * bz;
        double wz = vx * by - vy * bx;
        double wlen = Math.sqrt(wx*wx + wy*wy + wz*wz);
        if (wlen > 0.0001) { wx /= wlen; wy /= wlen; wz /= wlen; }
        wx *= beamWidth; wy *= beamWidth; wz *= beamWidth;
   
        double hx = -dz / dist * beamWidth;
        double hy = 0;
        double hz = dx / dist * beamWidth;
    
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    
        Minecraft.getMinecraft().getTextureManager().bindTexture(BEAM_TEX);
    
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
    
        // == BILLBOARD beam ==
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(sx - wx, sy - wy, sz - wz).tex(0, 0).color(r, g, b, a).endVertex();
        buf.pos(ex - wx, ey - wy, ez - wz).tex(1, 0).color(r, g, b, a).endVertex();
        buf.pos(ex + wx, ey + wy, ez + wz).tex(1, 1).color(r, g, b, a).endVertex();
        buf.pos(sx + wx, sy + wy, sz + wz).tex(0, 1).color(r, g, b, a).endVertex();
        tess.draw();
    
        // == HORIZONTAL beam ==
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(sx - hx, sy - hy, sz - hz).tex(0, 0).color(r, g, b, a).endVertex();
        buf.pos(ex - hx, ey - hy, ez - hz).tex(1, 0).color(r, g, b, a).endVertex();
        buf.pos(ex + hx, ey + hy, ez + hz).tex(1, 1).color(r, g, b, a).endVertex();
        buf.pos(sx + hx, sy + hy, sz + hz).tex(0, 1).color(r, g, b, a).endVertex();
        tess.draw();
    
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }    
    
}
