package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class SkeletonReviveModel extends ModelBase {
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    public SkeletonReviveModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.body = new ModelRenderer(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.leftArm = new ModelRenderer(this, 40, 16);
        this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2);
        this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);

        this.rightArm = new ModelRenderer(this, 40, 16);
        this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2);
        this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

        this.leftLeg = new ModelRenderer(this, 0, 16);
        this.leftLeg.addBox(-1.0F, 0.0F, -1.1F, 2, 12, 2);
        this.leftLeg.setRotationPoint(2.0F, 12.0F, 0.1F);

        this.rightLeg = new ModelRenderer(this, 0, 16);
        this.rightLeg.addBox(-1.0F, 0.0F, -1.1F, 2, 12, 2);
        this.rightLeg.setRotationPoint(-2.0F, 12.0F, 0.1F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.head.render(scale);
        this.body.render(scale);
        this.leftArm.render(scale);
        this.rightArm.render(scale);
        this.leftLeg.render(scale);
        this.rightLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        // Анімація для стрибка: перевірка, чи моб у стрибку
        
            // Отримуємо гравця, який знаходиться поруч
            EntityPlayer player = entityIn.world.getClosestPlayerToEntity(entityIn, 10.0D);
            double distanceToPlayer = player != null ? entityIn.getDistance(player) : 10.0D;

            // Поступове обмеження амплітуди анімації при наближенні гравця
            float animationScale = 1.0F;
            if (distanceToPlayer < 2.0) {
                animationScale = (float) distanceToPlayer / 2.0F; // Зменшує амплітуду при близькій відстані
            }

            // Звичайна анімація ходьби з урахуванням `animationScale`
            this.rightArm.rotateAngleX = (float) (Math.cos(limbSwing * 0.6662F + Math.PI) * 2.0F * limbSwingAmount * 0.5F * animationScale);
            this.leftArm.rotateAngleX = (float) (Math.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F * animationScale);
            this.rightLeg.rotateAngleX = (float) (Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * animationScale);
            this.leftLeg.rotateAngleX = (float) (Math.cos(limbSwing * 0.6662F + Math.PI) * 1.4F * limbSwingAmount * animationScale);
        }

}
