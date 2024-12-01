package com.koteuka404.thaumicforever;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RavenCloakRender extends RenderLiving<EntityLiving> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever:textures/entity/raven_cloak3.png");

    public RavenCloakRender(RenderManager renderManager) {
        super(renderManager, new RavenCloakModel(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityLiving entity, float partialTickTime) {
        // Тестування масштабування і зміщення
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.06F, 0.06F, 0.06F); // зменшення моделі для тестування
        GlStateManager.translate(0.0F, 1.5F, 0.0F); // зміщення для тестування

        super.preRenderCallback(entity, partialTickTime);
        GlStateManager.popMatrix();
    }
}
