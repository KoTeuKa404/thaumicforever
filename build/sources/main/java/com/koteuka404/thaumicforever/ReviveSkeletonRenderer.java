package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class ReviveSkeletonRenderer extends RenderLiving<ReviveSkeletonEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/revive_skeleton.png");

    public ReviveSkeletonRenderer(RenderManager renderManager) {
        super(renderManager, new SkeletonReviveModel(), 0.5F);
        this.addLayer(new LayerHeldItemCustom(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(ReviveSkeletonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(ReviveSkeletonEntity entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
    }

    private static class LayerHeldItemCustom implements LayerRenderer<ReviveSkeletonEntity> {
        private final ReviveSkeletonRenderer render;

        public LayerHeldItemCustom(ReviveSkeletonRenderer render) {
            this.render = render;
        }

        @Override
        public void doRenderLayer(ReviveSkeletonEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            ItemStack mainHandItem = entity.getHeldItemMainhand();
            ItemStack offHandItem = entity.getHeldItemOffhand();

            if (!mainHandItem.isEmpty() || !offHandItem.isEmpty()) {
                GlStateManager.pushMatrix();
                if (this.render.getMainModel().isChild) {
                    float scaleChild = 0.5F;
                    GlStateManager.translate(0.0F, 0.75F, 0.0F);
                    GlStateManager.scale(scaleChild, scaleChild, scaleChild);
                }

                renderHeldItem(entity, mainHandItem, EnumHandSide.RIGHT);
                renderHeldItem(entity, offHandItem, EnumHandSide.LEFT);

                GlStateManager.popMatrix();
            }
        }

        private void renderHeldItem(EntityLivingBase entity, ItemStack stack, EnumHandSide handSide) {
            if (!stack.isEmpty()) {
                GlStateManager.pushMatrix();
                // Використовуємо стандартний рендеринг предметів Minecraft
                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, handSide == EnumHandSide.LEFT ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, handSide == EnumHandSide.LEFT);
                GlStateManager.popMatrix();
            }
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
