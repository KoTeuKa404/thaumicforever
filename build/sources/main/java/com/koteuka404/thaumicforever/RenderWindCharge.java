package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderWindCharge extends Render<EntityWindCharge> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/wind_charge.png");
    private final WindCharge model = new WindCharge();

    public RenderWindCharge(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWindCharge entity) {
        return TEXTURE;
    }
    @Override
    public void doRender(EntityWindCharge entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
    
        GlStateManager.translate(x, y - 1.0F, z);
    
        GlStateManager.rotate(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
    
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
    
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
    
        GlStateManager.popMatrix();
    }
    
    
}
