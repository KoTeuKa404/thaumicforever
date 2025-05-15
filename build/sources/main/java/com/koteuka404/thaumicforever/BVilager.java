package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class BVilager extends ModelBase {
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer left_arm;
    private final ModelRenderer left_arm_r1;
    private final ModelRenderer right_arm;
    private final ModelRenderer right_arm_r1;
    private final ModelRenderer left_leg;
    private final ModelRenderer right_leg;

    public BVilager() {
        textureWidth = 64;
        textureHeight = 64;

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 24, 0, -1.0F, -3.0F, -6.0F, 2, 4, 2, 0.0F, false));

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 6.0F, 0.0F);
        body.cubeList.add(new ModelBox(body, 16, 20, -4.0F, -6.0F, -3.0F, 8, 12, 6, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 0, 38, -4.0F, -6.0F, -3.0F, 8, 18, 6, 0.5F, false));

        left_arm = new ModelRenderer(this);
        left_arm.setRotationPoint(5.0F, 2.0F, 0.0F);

        left_arm_r1 = new ModelRenderer(this);
        left_arm_r1.setRotationPoint(1.5F, -0.25F, 0.0F);
        left_arm.addChild(left_arm_r1);
        setRotationAngle(left_arm_r1, -1.6144F, 0.0F, 0.0F);
        left_arm_r1.cubeList.add(new ModelBox(left_arm_r1, 44, 22, -2.0F, -1.75F, -2.0F, 4, 12, 4, 0.0F, true));

        right_arm = new ModelRenderer(this);
        right_arm.setRotationPoint(-5.0F, 2.0F, 0.0F);

        right_arm_r1 = new ModelRenderer(this);
        right_arm_r1.setRotationPoint(-1.5F, -0.75F, 0.0F);
        right_arm.addChild(right_arm_r1);
        setRotationAngle(right_arm_r1, -1.5708F, 0.0F, 0.0F);
        right_arm_r1.cubeList.add(new ModelBox(right_arm_r1, 44, 22, -2.0F, -1.25F, -2.0F, 4, 12, 4, 0.0F, false));

        left_leg = new ModelRenderer(this);
        left_leg.setRotationPoint(2.0F, 12.0F, 0.0F);
        left_leg.cubeList.add(new ModelBox(left_leg, 0, 22, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

        right_leg = new ModelRenderer(this);
        right_leg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        right_leg.cubeList.add(new ModelBox(right_leg, 0, 22, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        head.render(f5);
        body.render(f5);
        left_arm.render(f5);
        right_arm.render(f5);
        left_leg.render(f5);
        right_leg.render(f5);
    }

    @Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
		this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
		this.head.rotateAngleX = headPitch * ((float) Math.PI / 180F);

		this.right_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.0F * limbSwingAmount;
		this.left_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.0F * limbSwingAmount;

		this.right_arm.rotateAngleZ = 0.1F;
		this.left_arm.rotateAngleZ = -0.1F;

		this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

		if (entity instanceof EntityLivingBase) {
			EntityLivingBase livingEntity = (EntityLivingBase) entity;
			if (livingEntity.swingProgress > 0.0F) {
				float swingProgress = MathHelper.sin(livingEntity.swingProgress * (float) Math.PI);
				this.right_arm.rotateAngleX = -swingProgress * 2.0F + 0.5F;
				this.left_arm.rotateAngleX = swingProgress * 2.0F + 0.5F;
				this.right_arm.rotateAngleZ = 0.0F;
				this.left_arm.rotateAngleZ = 0.0F;
			}
		}
	}


    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
