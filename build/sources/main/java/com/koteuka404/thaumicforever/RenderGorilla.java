package com.koteuka404.thaumicforever;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderGorilla extends RenderLiving<EntityGorilla> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/texture.png");

    public RenderGorilla(RenderManager renderManager) {
        super(renderManager, new Monkey(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGorilla entity) {
        return TEXTURE;
    }

    @Override
    public void doRender(EntityGorilla entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Monkey model = (Monkey) this.getMainModel();

        float limbSwing = entity.limbSwing;
        float limbSwingAmount = entity.limbSwingAmount;
        float netHeadYaw = entity.rotationYawHead - entity.prevRotationYawHead;
        float headPitch = entity.rotationPitch;

        entity.updateModelAngles(model, limbSwing, limbSwingAmount, netHeadYaw, headPitch);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
