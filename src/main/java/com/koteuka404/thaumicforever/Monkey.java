package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class Monkey extends ModelBase {
	private final ModelRenderer MainBody;
	private final ModelRenderer cube_r2_r1_r1;
	private final ModelRenderer cube_r1_r1;
	private final ModelRenderer right_arm;
	private final ModelRenderer right_Leg;
	private final ModelRenderer head;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r5_r1;
	private final ModelRenderer cube_r5_r2;
	private final ModelRenderer left_arm;
	private final ModelRenderer left_Leg;
	private final ModelRenderer getRightArm;
	private final ModelRenderer getLeftArm;

	public Monkey() {
		textureWidth = 128;
		textureHeight = 128;

		MainBody = new ModelRenderer(this);
		MainBody.setRotationPoint(0.0F, 13.0F, 5.0F);
		setRotationAngle(MainBody, 0.5236F, 0.0F, 0.0F);

		cube_r2_r1_r1 = new ModelRenderer(this);
		cube_r2_r1_r1.setRotationPoint(-5.0F, 14.0F, -0.5F);
		MainBody.addChild(cube_r2_r1_r1);
		setRotationAngle(cube_r2_r1_r1, 0.0F, 3.1416F, 0.0F);
		cube_r2_r1_r1.cubeList.add(new ModelBox(cube_r2_r1_r1, 0, 21, -10.0F, -16.3F, -7.6F, 10, 8, 10, 0.0F, false));

		cube_r1_r1 = new ModelRenderer(this);
		cube_r1_r1.setRotationPoint(-8.0F, -11.0F, 29.0F);
		MainBody.addChild(cube_r1_r1);
		setRotationAngle(cube_r1_r1, 1.5708F, 0.0F, -3.1416F);
		cube_r1_r1.cubeList.add(new ModelBox(cube_r1_r1, 0, 0, -15.0F, -32.0F, -1.0F, 14, 10, 10, 0.0F, false));

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(-6.4F, 2.75F, 3.0F);
		setRotationAngle(right_arm, 0.0F, 0.0F, -3.1416F);
		right_arm.cubeList.add(new ModelBox(right_arm, 50, 74, 21.6F, -1.75F, -3.0F, 2, 6, 5, 0.0F, false));
		right_arm.cubeList.add(new ModelBox(right_arm, 0, 40, 0.6527F, -2.0F, -3.6986F, 14, 6, 6, 0.0F, false));
		right_arm.cubeList.add(new ModelBox(right_arm, 41, 21, 11.6527F, -3.0F, -4.6986F, 10, 8, 8, 0.0F, false));

		right_Leg = new ModelRenderer(this);
		right_Leg.setRotationPoint(-5.0F, 16.0F, 9.0F);
		right_Leg.cubeList.add(new ModelBox(right_Leg, 0, 70, -5.5F, -2.75F, -4.25F, 6, 7, 7, 0.0F, false));
		right_Leg.cubeList.add(new ModelBox(right_Leg, 27, 74, -5.0F, 6.0F, -4.0F, 5, 2, 6, 0.0F, false));
		right_Leg.cubeList.add(new ModelBox(right_Leg, 78, 13, -5.0F, 1.0F, -3.0F, 5, 5, 5, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.5F, 4.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 78, 44, -4.0F, -5.0F, -10.0F, 8, 1, 1, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 68, 54, -5.0F, -8.0F, -9.0F, 10, 9, 5, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 50, 13, -2.5F, -3.0F, -11.0F, 5, 4, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 66, 13, 4.25F, -5.0F, -6.5F, 1, 2, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 78, 47, -5.25F, -5.0F, -6.5F, 1, 2, 2, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.5F, 0.0F, -4.0F);
		head.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.1745F, 0.0F, 0.0F);

		cube_r5_r1 = new ModelRenderer(this);
		cube_r5_r1.setRotationPoint(-1.5F, 19.9F, 5.0F);
		cube_r5.addChild(cube_r5_r1);
		setRotationAngle(cube_r5_r1, 0.0436F, 0.0F, 0.0F);
		cube_r5_r1.cubeList.add(new ModelBox(cube_r5_r1, 34, 54, -3.5F, -30.0F, -9.0F, 9, 11, 8, 0.0F, false));

		cube_r5_r2 = new ModelRenderer(this);
		cube_r5_r2.setRotationPoint(-2.0F, 20.5F, 4.0F);
		cube_r5.addChild(cube_r5_r2);
		setRotationAngle(cube_r5_r2, -0.0436F, 0.0F, 0.0F);
		cube_r5_r2.cubeList.add(new ModelBox(cube_r5_r2, 1, 53, -2.5F, -29.0F, -7.0F, 8, 8, 8, 0.0F, false));

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(7.6F, 2.75F, 3.0F);
		left_arm.cubeList.add(new ModelBox(left_arm, 41, 38, 11.3544F, -4.75F, -4.4791F, 10, 8, 8, 0.0F, false));
		left_arm.cubeList.add(new ModelBox(left_arm, 49, 0, -0.6456F, -3.75F, -3.4791F, 14, 6, 6, 0.0F, false));
		left_arm.cubeList.add(new ModelBox(left_arm, 27, 83, 21.4F, -4.25F, -3.0F, 2, 6, 5, 0.0F, false));

		left_Leg = new ModelRenderer(this);
		left_Leg.setRotationPoint(6.0F, 16.0F, 9.0F);
		left_Leg.cubeList.add(new ModelBox(left_Leg, 68, 70, -0.5F, -2.75F, -4.25F, 6, 7, 7, 0.0F, false));
		left_Leg.cubeList.add(new ModelBox(left_Leg, 78, 24, 0.0F, 6.0F, -4.0F, 5, 2, 6, 0.0F, false));
		left_Leg.cubeList.add(new ModelBox(left_Leg, 78, 33, 0.0F, 1.0F, -3.0F, 5, 5, 5, 0.0F, false));

		getRightArm = new ModelRenderer(this);
		getRightArm.setRotationPoint(0.0F, 0.0F, 0.0F);

		getLeftArm = new ModelRenderer(this);
		getLeftArm.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		MainBody.render(f5);
		right_arm.render(f5);
		right_Leg.render(f5);
		head.render(f5);
		left_arm.render(f5);
		left_Leg.render(f5);
		getRightArm.render(f5);
		getLeftArm.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
		this.swingProgress = entity.getSwingProgress(partialTickTime);
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
								float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		final float Z90  = (float)Math.toRadians(85);
		final float Z95  = (float)Math.toRadians(95);
		final float baseY = (float)Math.toRadians(25);
	
		right_arm.rotateAngleZ = Z95;
		left_arm.rotateAngleZ  = Z90;
		right_arm.rotateAngleX = 0.0F;
		left_arm.rotateAngleX  = 0.0F;
		right_arm.rotateAngleY = baseY;
		left_arm.rotateAngleY  = baseY;
	
		float spd  = 0.6662F;
		float ampXLeg = (float)Math.toRadians(55) * limbSwingAmount;
		right_Leg.rotateAngleX = (float)Math.cos(limbSwing * spd) * ampXLeg * 0.8F;
		left_Leg.rotateAngleX  = (float)Math.cos(limbSwing * spd + Math.PI) * ampXLeg * 0.8F;
	
		float breath = (float)Math.sin(ageInTicks * 0.1F) * (float)Math.toRadians(3);
		MainBody.rotateAngleX = (float)Math.toRadians(25) + breath;
		head.rotateAngleY = netHeadYaw * 0.017453292F;
		head.rotateAngleX = headPitch * 0.017453292F + breath * 0.5F;
	
		boolean isSlam = (entityIn instanceof EntityGorilla) && ((EntityGorilla)entityIn).isSpecialAttacking();
		boolean isVanillaSwing = (this.swingProgress > 0.0F);
	
		if (!isSlam && !isVanillaSwing) {
			float ampYWalk = (float)Math.toRadians(45) * limbSwingAmount;
			float phase    = (float)Math.cos(limbSwing * spd);
			right_arm.rotateAngleY = baseY +  phase * ampYWalk;
			left_arm.rotateAngleY  = baseY + -phase * ampYWalk;
		}
	
		if (isVanillaSwing) {
			float s    = Math.min(0.999F, this.swingProgress);
			float ease = (float)Math.sin(s * s * Math.PI);
			float yawRaise = (float)Math.toRadians(65.0F);
			right_arm.rotateAngleY = baseY + ease * yawRaise;
			left_arm.rotateAngleY  = baseY + ease * yawRaise;
		}
	
		if (isSlam) {
			EntityGorilla g = (EntityGorilla) entityIn;
			float p = g.getSlamProgress();                 // 0..1
			float raiseY = (float)Math.toRadians(110.0) * p;
			right_arm.rotateAngleY = baseY + raiseY;
			left_arm.rotateAngleY  = baseY + raiseY;
		}
	}
}