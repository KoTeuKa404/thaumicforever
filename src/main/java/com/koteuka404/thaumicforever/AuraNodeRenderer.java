package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class AuraNodeRenderer extends Render<AuraNodeEntity> {
    private static final ResourceLocation[] NODE_TEXTURES = {
        new ResourceLocation("thaumicforever", "textures/misc/aura_1.png"),
        new ResourceLocation("thaumicforever", "textures/misc/aura_2.png"),
        new ResourceLocation("thaumicforever", "textures/misc/aura_3.png")
    };
    private static final float CYCLE = 30.0f;
    private static final float SCALE = 2.5f;

    public AuraNodeRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(AuraNodeEntity entity, double x, double y, double z,
                        float entityYaw, float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (!ItemAquareiaGoggles.shouldRenderHud(player)) {
            return;
        }

        float time  = entity.ticksExisted + partialTicks;
        float blend = (time % CYCLE) / CYCLE;
        int idx     = (int)(time / CYCLE) % NODE_TEXTURES.length;
        int nextIdx = (idx + 1) % NODE_TEXTURES.length;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.5, z);
        GlStateManager.rotate(-renderManager.playerViewY, 0, 1, 0);
        GlStateManager.rotate( renderManager.playerViewX, 1, 0, 0);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.disableLighting();

        for (int layer = 0; layer < 3; layer++) {
            float pulse = (float)(Math.sin(time/10.0 + layer) * 0.1);
            float baseSize = 0.5f + layer * 0.25f + pulse;
            float size = baseSize * SCALE;
            float alpha = 1.0f - Math.min(1.0f, entity.getDistance(player) / 30.0f);
            alpha *= (1.0f - layer * 0.3f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(NODE_TEXTURES[idx]);
            drawQuad(size, alpha * (1 - blend));

            Minecraft.getMinecraft().getTextureManager().bindTexture(NODE_TEXTURES[nextIdx]);
            drawQuad(size, alpha * blend);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawQuad(float size, float alpha) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        buf.pos(-size, -size, 0).tex(0, 1).color(1f, 1f, 1f, alpha).endVertex();
        buf.pos(-size,  size, 0).tex(0, 0).color(1f, 1f, 1f, alpha).endVertex();
        buf.pos( size,  size, 0).tex(1, 0).color(1f, 1f, 1f, alpha).endVertex();
        buf.pos( size, -size, 0).tex(1, 1).color(1f, 1f, 1f, alpha).endVertex();

        tess.draw();
    }

    @Override
    protected ResourceLocation getEntityTexture(AuraNodeEntity entity) {
        return NODE_TEXTURES[0];
    }
}
