package com.koteuka404.thaumicforever.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import com.koteuka404.thaumicforever.client.model.DarkCloakModel;

public class DarkCloakRender extends RenderLiving<EntityLiving> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever:textures/entity/dark_cloak3.png");

    public DarkCloakRender(RenderManager renderManager) {
        super(renderManager, new DarkCloakModel(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityLiving entity, float partialTickTime) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.06F, 0.06F, 0.06F);
        GlStateManager.translate(0.0F, 1.5F, 0.0F); 

        super.preRenderCallback(entity, partialTickTime);
        GlStateManager.popMatrix();
    }
}
