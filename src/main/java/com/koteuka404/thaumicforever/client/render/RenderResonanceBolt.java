package com.koteuka404.thaumicforever.client.render;

import com.koteuka404.thaumicforever.entity.EntityResonanceBolt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class RenderResonanceBolt extends Render<EntityResonanceBolt> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/particleMote.png");

    public RenderResonanceBolt(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityResonanceBolt entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(770, 1);
        GlStateManager.color(0.55F, 0.25F, 1.0F, 0.75F);
        bindTexture(TEXTURE);

        float s = 0.18F;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-s, -s, 0.0D).tex(0.0D, 1.0D).endVertex();
        buffer.pos(s, -s, 0.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(s, s, 0.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(-s, s, 0.0D).tex(0.0D, 0.0D).endVertex();
        tess.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityResonanceBolt entity) {
        return TEXTURE;
    }
}
