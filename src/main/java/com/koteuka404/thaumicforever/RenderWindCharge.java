package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderWindCharge extends Render<EntityWindCharge> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumicforever", "textures/entity/wind_charge.png");
    private final WindCharge model = new WindCharge();

    public RenderWindCharge(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWindCharge entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(EntityWindCharge entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
    
        float yOffset = -1F;
        GlStateManager.translate(x, y + yOffset, z);
    
        float pivotY = entity.height * 0.5F;
        GlStateManager.translate(0.0F, pivotY, 0.0F);
    
        float phase = (entity.getEntityId() * 37) % 360;
        float speedDegPerTick = 28.0F;
        float spin = -(entity.ticksExisted + partialTicks) * speedDegPerTick + phase;
        GlStateManager.rotate(spin, 0.0F, 1.0F, 0.0F);
    
        GlStateManager.translate(0.0F, -pivotY, 0.0F);
    
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
    
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        model.render(entity, 0.0F, 0.0F, partialTicks, 0.0F, 0.0F, 0.0625F);
    
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
}
