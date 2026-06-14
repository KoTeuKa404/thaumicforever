package com.koteuka404.thaumicforever.client.render;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.tile.TileVoidChest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileVoidChest extends TileEntitySpecialRenderer<TileVoidChest> {
    private static final ResourceLocation END_PORTAL = new ResourceLocation("minecraft", "textures/entity/end_portal.png");

    @Override
    public void render(TileVoidChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderManager renderManager = mc.getRenderManager();
        float parallaxX = (float) ((renderManager.viewerPosX - te.getPos().getX()) * 0.22D);
        float parallaxZ = (float) ((renderManager.viewerPosZ - te.getPos().getZ()) * 0.22D);
        float portalOffset = parallaxX + parallaxZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        mc.getTextureManager().bindTexture(END_PORTAL);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        renderPortalBox(0.06D, 0.06D, 0.06D, 0.94D, 0.94D, 0.94D, portalOffset, 0.62F);
        renderPortalBox(0.12D, 0.12D, 0.12D, 0.88D, 0.88D, 0.88D, -portalOffset * 1.7F, 0.38F);
        renderPortalWindows(portalOffset * 1.35F, 0.55F);

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderPortalBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float scroll, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float u0 = scroll;
        float v0 = scroll * 0.5F;
        float u1 = u0 + 1.0F;
        float v1 = v0 + 1.0F;
        float r = 0.42F;
        float g = 0.22F;
        float b = 0.75F;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        // Top and bottom give the lid window a moving void surface.
        vertex(buffer, minX, maxY, minZ, u0, v0, r, g, b, alpha);
        vertex(buffer, minX, maxY, maxZ, u0, v1, r, g, b, alpha);
        vertex(buffer, maxX, maxY, maxZ, u1, v1, r, g, b, alpha);
        vertex(buffer, maxX, maxY, minZ, u1, v0, r, g, b, alpha);

        vertex(buffer, minX, minY, maxZ, u0, v0, r, g, b, alpha);
        vertex(buffer, minX, minY, minZ, u0, v1, r, g, b, alpha);
        vertex(buffer, maxX, minY, minZ, u1, v1, r, g, b, alpha);
        vertex(buffer, maxX, minY, maxZ, u1, v0, r, g, b, alpha);

        // Side planes sit behind the transparent atlas cutouts.
        vertex(buffer, minX, minY, minZ, u0, v1, r, g, b, alpha);
        vertex(buffer, minX, maxY, minZ, u0, v0, r, g, b, alpha);
        vertex(buffer, maxX, maxY, minZ, u1, v0, r, g, b, alpha);
        vertex(buffer, maxX, minY, minZ, u1, v1, r, g, b, alpha);

        vertex(buffer, maxX, minY, maxZ, u0, v1, r, g, b, alpha);
        vertex(buffer, maxX, maxY, maxZ, u0, v0, r, g, b, alpha);
        vertex(buffer, minX, maxY, maxZ, u1, v0, r, g, b, alpha);
        vertex(buffer, minX, minY, maxZ, u1, v1, r, g, b, alpha);

        vertex(buffer, minX, minY, maxZ, u0, v1, r, g, b, alpha);
        vertex(buffer, minX, maxY, maxZ, u0, v0, r, g, b, alpha);
        vertex(buffer, minX, maxY, minZ, u1, v0, r, g, b, alpha);
        vertex(buffer, minX, minY, minZ, u1, v1, r, g, b, alpha);

        vertex(buffer, maxX, minY, minZ, u0, v1, r, g, b, alpha);
        vertex(buffer, maxX, maxY, minZ, u0, v0, r, g, b, alpha);
        vertex(buffer, maxX, maxY, maxZ, u1, v0, r, g, b, alpha);
        vertex(buffer, maxX, minY, maxZ, u1, v1, r, g, b, alpha);

        tessellator.draw();
    }

    private void renderPortalWindows(float scroll, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float u0 = scroll;
        float v0 = scroll * 0.5F;
        float u1 = u0 + 1.0F;
        float v1 = v0 + 1.0F;
        float r = 0.34F;
        float g = 0.18F;
        float b = 0.72F;
        double y0 = 0.14D;
        double y1 = 0.72D;
        double a = 0.08D;
        double c = 0.92D;
        double e = 0.003D;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        vertex(buffer, a, y0, -e, u0, v1, r, g, b, alpha);
        vertex(buffer, a, y1, -e, u0, v0, r, g, b, alpha);
        vertex(buffer, c, y1, -e, u1, v0, r, g, b, alpha);
        vertex(buffer, c, y0, -e, u1, v1, r, g, b, alpha);

        vertex(buffer, c, y0, 1.0D + e, u0, v1, r, g, b, alpha);
        vertex(buffer, c, y1, 1.0D + e, u0, v0, r, g, b, alpha);
        vertex(buffer, a, y1, 1.0D + e, u1, v0, r, g, b, alpha);
        vertex(buffer, a, y0, 1.0D + e, u1, v1, r, g, b, alpha);

        vertex(buffer, -e, y0, c, u0, v1, r, g, b, alpha);
        vertex(buffer, -e, y1, c, u0, v0, r, g, b, alpha);
        vertex(buffer, -e, y1, a, u1, v0, r, g, b, alpha);
        vertex(buffer, -e, y0, a, u1, v1, r, g, b, alpha);

        vertex(buffer, 1.0D + e, y0, a, u0, v1, r, g, b, alpha);
        vertex(buffer, 1.0D + e, y1, a, u0, v0, r, g, b, alpha);
        vertex(buffer, 1.0D + e, y1, c, u1, v0, r, g, b, alpha);
        vertex(buffer, 1.0D + e, y0, c, u1, v1, r, g, b, alpha);

        tessellator.draw();
    }

    private void vertex(BufferBuilder buffer, double x, double y, double z, float u, float v, float r, float g, float b, float a) {
        buffer.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }
}
