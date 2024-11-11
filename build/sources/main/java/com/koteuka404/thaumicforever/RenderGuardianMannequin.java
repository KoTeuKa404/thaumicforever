package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderGuardianMannequin extends RenderLiving<EntityGuardianMannequin> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever:textures/entity/guardian_mannequin.png");

    public RenderGuardianMannequin(RenderManager renderManager) {
        super(renderManager, new CustomModel(), 0.5F);  // Використовуємо вашу модель
        this.addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.modelLeggings = new ModelBiped(0.5F);
                this.modelArmor = new ModelBiped(1.0F);
            }
        });

        // Додаємо кастомний шар для рендеру предметів у руках
        this.addLayer(new LayerHeldItemCustom(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGuardianMannequin entity) {
        return TEXTURE;  // Повертаємо текстуру моба
    }

    @Override
    protected void preRenderCallback(EntityGuardianMannequin entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(1.0F, 1.0F, 1.0F);  // Можливо, потрібна зміна масштабування
    }

    // Кастомний клас для рендеру предметів у руках
    private static class LayerHeldItemCustom implements LayerRenderer<EntityGuardianMannequin> {
        private final RenderGuardianMannequin render;

        public LayerHeldItemCustom(RenderGuardianMannequin render) {
            this.render = render;
        }

        @Override
        public void doRenderLayer(EntityGuardianMannequin entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            ItemStack itemstack = entity.getHeldItemMainhand();

            if (!itemstack.isEmpty()) {
                GlStateManager.pushMatrix();

                // Прив'язуємо предмет до лівої руки моделі
                if (this.render.getMainModel() instanceof CustomModel) {
                    CustomModel model = (CustomModel) this.render.getMainModel();
                    model.leftArm.postRender(-0.00F);  // Використовуємо пост-обробку ротації та позиції руки
                }

                // Налаштування позиції та обертання предмета
                GlStateManager.translate(0.35F, 0.73F, 0.0F);  
                GlStateManager.rotate(90F, 1F, 0F, 0F);   // Поворот на 90 градусів по осі X
                GlStateManager.rotate(180F, 0F, 0F, 1F);  // Поворот на 180 градусів по осі Y

                // Рендер предмета у лівій руці
                renderItem(entity, itemstack, EnumHandSide.LEFT);

                GlStateManager.popMatrix();
            }
        }

        
        


        private void renderItem(EntityLivingBase entity, ItemStack stack, EnumHandSide handSide) {
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, handSide == EnumHandSide.LEFT ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, handSide == EnumHandSide.LEFT);
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
