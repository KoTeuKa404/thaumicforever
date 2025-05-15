package com.koteuka404.thaumicforever;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGorillaHand extends Render<EntityGorillaHand> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/items/hand.png");
    private final ModelGorillaHand model = new ModelGorillaHand();

    public RenderGorillaHand(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityGorillaHand entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(-90F, 0, 1, 0);
        model.render(null, 0, 0, 0, 0, 0, 0.0625F);

        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGorillaHand entity) {
        return TEXTURE;
    }
}
