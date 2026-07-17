package com.koteuka404.thaumicforever.client.render;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.client.fx.FXDispatcher;
import com.koteuka404.thaumicforever.client.model.ModelVoidTraiderTentacle;
import com.koteuka404.thaumicforever.client.model.VoidTraiderModel;
import com.koteuka404.thaumicforever.entity.EntityVoidTraider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderVoidTraider extends RenderLiving<EntityVoidTraider> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumicforever", "textures/entity/t_traider.png");
    private static final ResourceLocation TENTACLE_TEXTURE =
            new ResourceLocation("thaumicforever", "textures/entity/eldritch_taintacle.png");

    public RenderVoidTraider(RenderManager renderManager) {
        super(renderManager, new VoidTraiderModel(), 0.8F);
        this.addLayer(new LayerVoidTraiderFog());
        this.addLayer(new LayerVoidTraiderTentacles());
        this.addLayer(new LayerVoidTraiderShopItems());
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVoidTraider entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityVoidTraider entity, float partialTickTime) {
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
    }

    private static class LayerVoidTraiderTentacles implements LayerRenderer<EntityVoidTraider> {
        private final ModelVoidTraiderTentacle model = new ModelVoidTraiderTentacle();

        @Override
        public void doRenderLayer(EntityVoidTraider entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TENTACLE_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.disableCull();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            renderTentacle(entity, ageInTicks, -0.45F, 1.2F, -0.0F, 0.48F, -70.0F, 0.0F, -50.0F);
            renderTentacle(entity, ageInTicks, 0.75F, 0.9F, -0.0F, 0.48F, 60.0F, 0.0F, 100.0F);
            renderTentacle(entity, ageInTicks, 0.3F, 0.2F, -0.0F, 0.48F, 0.0F, 50.0F, 10.0F);

            GlStateManager.enableCull();
            GlStateManager.popMatrix();
        }

        private void renderTentacle(EntityVoidTraider entity, float ageInTicks, float x, float y, float z, float scale, float yaw, float pitch, float roll) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(roll, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(scale, scale, scale);
            this.model.render(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private static class LayerVoidTraiderShopItems implements LayerRenderer<EntityVoidTraider> {
        private static final float DISPLAY_ITEM_SCALE = 0.65F;
        // Five tabletop placeholders converted from the model's pixel coordinates to blocks.
        private static final float[][] DISPLAY_POSITIONS = {
            { 2.5F / 16.0F, 15.75F / 16.0F, -10.0F / 16.0F },
            { -5.5F / 16.0F, 15.75F / 16.0F, -10.0F / 16.0F },
            { 8.5F / 16.0F, 17.75F / 16.0F, -8.0F / 16.0F },
            { -12.5F / 16.0F, 17.75F / 16.0F, -8.0F / 16.0F },
            { 12.6869F / 16.0F, 20.001F / 16.0F, -2.1475F / 16.0F }
        };

        @Override
        public void doRenderLayer(EntityVoidTraider entity, float limbSwing, float limbSwingAmount,
                                  float partialTicks, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scale) {
            for (int displayIndex = 0; displayIndex < DISPLAY_POSITIONS.length; displayIndex++) {
                ItemStack stack = getDisplayStack(entity, displayIndex);
                if (stack.isEmpty()) {
                    continue;
                }

                float[] position = DISPLAY_POSITIONS[displayIndex];
                GlStateManager.pushMatrix();
                GlStateManager.translate(position[0], position[1], position[2]);
                // Keep the item flat and static on the tabletop, like an item frame.
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.scale(DISPLAY_ITEM_SCALE, DISPLAY_ITEM_SCALE, DISPLAY_ITEM_SCALE);
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableLighting();
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.disableRescaleNormal();
                GlStateManager.popMatrix();
            }
        }

        private ItemStack getDisplayStack(EntityVoidTraider entity, int displayIndex) {
            int slots = entity.getSaleItems().getSlots();
            if (slots == 0) {
                return ItemStack.EMPTY;
            }

            int start = Math.floorMod(entity.getEntityId() * 31, slots);
            int nonEmptyIndex = 0;
            for (int offset = 0; offset < slots; offset++) {
                ItemStack stack = entity.getSaleItems().getStackInSlot((start + offset) % slots);
                if (!stack.isEmpty()) {
                    if (nonEmptyIndex++ == displayIndex) {
                        return stack;
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private static class LayerVoidTraiderFog implements LayerRenderer<EntityVoidTraider> {
        @Override
        public void doRenderLayer(EntityVoidTraider entity, float limbSwing, float limbSwingAmount,
                                  float partialTicks, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scale) {
            if (FXDispatcher.INSTANCE == null) {
                return;
            }

            if (entity.ticksExisted % 4 == 0) {
                double x = entity.posX - 0.40D;
                double y = entity.posY + 0.80D;
                double z = entity.posZ - 0.2D;
                FXDispatcher.INSTANCE.drawVoidFogParticle(x, y, z, 0.0D, 0.004D, 0.0D);
            }

            double yaw = Math.toRadians(entity.renderYawOffset);
            double forwardX = -Math.sin(yaw);
            double forwardZ = Math.cos(yaw);
            double sideX = Math.cos(yaw);
            double sideZ = Math.sin(yaw);
            double eyeY = entity.posY + 1.45D;
            double eyeForward = 0.0D;
            double eyeSide = 0.23D;
            FXDispatcher.INSTANCE.drawVoidEyeGlowParticle(
                    entity.posX + forwardX * eyeForward - sideX * eyeSide,
                    eyeY,
                    entity.posZ + forwardZ * eyeForward - sideZ * eyeSide);
            FXDispatcher.INSTANCE.drawVoidEyeGlowParticle(
                    entity.posX + forwardX * eyeForward + sideX * eyeSide,
                    eyeY,
                    entity.posZ + forwardZ * eyeForward + sideZ * eyeSide);
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
