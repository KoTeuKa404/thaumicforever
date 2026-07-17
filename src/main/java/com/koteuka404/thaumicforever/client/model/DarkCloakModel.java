package com.koteuka404.thaumicforever.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


public class DarkCloakModel extends ModelBase {
	private static final float WING_BLOCK_CLIP_BIAS_Z = 0.0F;
	private static final float MAX_CLOAK_PITCH = (float) (Math.PI / 2.0D);
	private static final float RUN_SPEED_START = 0.10F;
	private static final float RUN_SPEED_FULL = 0.28F;

	private final ModelRenderer cloak;
	private final ModelRenderer head;
	private final ModelRenderer chest;
	private final ModelRenderer arms;
	private final ModelRenderer right_arm;
	private final ModelRenderer left_arm;
	private final ModelRenderer wing;
	private final ModelRenderer right_wing;
	private final ModelRenderer left_wing;


	public DarkCloakModel() {
		textureWidth = 128;
		textureHeight = 64;

		cloak = new ModelRenderer(this);
		cloak.setRotationPoint(0.0F, 26.0F, 0.0F);
		

		head = new ModelRenderer(this);
		head.setRotationPoint(-0.5F, -25.75F, -0.5F);
		cloak.addChild(head);
		head.cubeList.add(new ModelBox(head, 3, 2, -4.0F, -9.0F, -4.0F, 9, 9, 9, 0.0F, false));

		chest = new ModelRenderer(this);
		chest.setRotationPoint(0.0F, -11.0F, -2.0F);
		cloak.addChild(chest);
		chest.cubeList.add(new ModelBox(chest, 77, 6, -4.0F, -14.0F, 0.15F, 8, 12, 4, 0.9F, false));

		arms = new ModelRenderer(this);
		arms.setRotationPoint(-5.0F, -25.0F, 0.0F);
		cloak.addChild(arms);
		

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(0.0F, 0.0F, 0.0F);
		arms.addChild(right_arm);
		right_arm.cubeList.add(new ModelBox(right_arm, 59, 23, -2.5F, -0.5F, -2.0F, 3, 11, 4, 1.0F, true));

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(10.0F, 0.0F, 0.0F);
		arms.addChild(left_arm);
		left_arm.cubeList.add(new ModelBox(left_arm, 59, 23, -0.75F, -0.5F, -2.0F, 3, 11, 4, 1.0F, false));

		wing = new ModelRenderer(this);
		wing.setRotationPoint(0.0F, -25.0F, 2.0F);
		cloak.addChild(wing);
		

		right_wing = new ModelRenderer(this);
		right_wing.setRotationPoint(0.0F, 0.0F, 1.0F);
		wing.addChild(right_wing);
		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -8.5F, -1.25F, 0.3F, 13, 15, 0, 0.0F, false));
		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -6.0F, 2.5F, 0.15F, 13, 12, 0, 0.0F, false));
		right_wing.cubeList.add(new ModelBox(right_wing, 48, 41, -4.75F, 7.0F, -0.05F, 13, 15, 0, 0.0F, false));

		left_wing = new ModelRenderer(this);
		left_wing.setRotationPoint(0.0F, 1.0F, 1.0F);
		wing.addChild(left_wing);
		left_wing.cubeList.add(new ModelBox(left_wing, 48, 41, -4.5F, -2.25F, 0.1F, 13, 14, 0, 0.0F, false));
	}



	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		cloak.render(f5);
	}

	public void renderAttached(EntityPlayer player, ModelPlayer modelPlayer, float scale, float partialTicks) {
		if (modelPlayer == null) return;
		float cloakPitch = getCloakPitch(player, partialTicks);
		float cloakBackOffset = getCloakBackOffset(player, partialTicks);

		GlStateManager.pushMatrix();
		modelPlayer.bipedHead.postRender(scale);
		renderPartAt(head,
				cloak.rotationPointX + head.rotationPointX - modelPlayer.bipedHead.rotationPointX,
				cloak.rotationPointY + head.rotationPointY - modelPlayer.bipedHead.rotationPointY,
				cloak.rotationPointZ + head.rotationPointZ - modelPlayer.bipedHead.rotationPointZ,
				scale);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		modelPlayer.bipedBody.postRender(scale);
		renderPartAt(chest,
				cloak.rotationPointX + chest.rotationPointX - modelPlayer.bipedBody.rotationPointX,
				cloak.rotationPointY + chest.rotationPointY - modelPlayer.bipedBody.rotationPointY,
				cloak.rotationPointZ + chest.rotationPointZ - modelPlayer.bipedBody.rotationPointZ,
				scale);
		GlStateManager.disableCull();
		renderPartAt(wing,
				cloak.rotationPointX + wing.rotationPointX - modelPlayer.bipedBody.rotationPointX,
				cloak.rotationPointY + wing.rotationPointY - modelPlayer.bipedBody.rotationPointY,
				cloak.rotationPointZ + wing.rotationPointZ - modelPlayer.bipedBody.rotationPointZ + WING_BLOCK_CLIP_BIAS_Z + cloakBackOffset,
				cloakPitch,
				0.0F,
				0.0F,
				scale);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		modelPlayer.bipedRightArm.postRender(scale);
		renderPartAt(right_arm,
				cloak.rotationPointX + arms.rotationPointX + right_arm.rotationPointX - modelPlayer.bipedRightArm.rotationPointX,
				cloak.rotationPointY + arms.rotationPointY + right_arm.rotationPointY - modelPlayer.bipedRightArm.rotationPointY,
				cloak.rotationPointZ + arms.rotationPointZ + right_arm.rotationPointZ - modelPlayer.bipedRightArm.rotationPointZ,
				scale);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		modelPlayer.bipedLeftArm.postRender(scale);
		renderPartAt(left_arm,
				cloak.rotationPointX + arms.rotationPointX + left_arm.rotationPointX - modelPlayer.bipedLeftArm.rotationPointX,
				cloak.rotationPointY + arms.rotationPointY + left_arm.rotationPointY - modelPlayer.bipedLeftArm.rotationPointY,
				cloak.rotationPointZ + arms.rotationPointZ + left_arm.rotationPointZ - modelPlayer.bipedLeftArm.rotationPointZ,
				scale);
		GlStateManager.popMatrix();
	}

	public void renderAttached(EntityPlayer player, ModelPlayer modelPlayer, float scale) {
		renderAttached(player, modelPlayer, scale, 0.0F);
	}

	private void renderPartAt(ModelRenderer part, float x, float y, float z, float scale) {
		renderPartAt(part, x, y, z, 0.0F, 0.0F, 0.0F, scale);
	}

	private void renderPartAt(ModelRenderer part, float x, float y, float z, float angleX, float angleY, float angleZ, float scale) {
		float oldPointX = part.rotationPointX;
		float oldPointY = part.rotationPointY;
		float oldPointZ = part.rotationPointZ;
		float oldAngleX = part.rotateAngleX;
		float oldAngleY = part.rotateAngleY;
		float oldAngleZ = part.rotateAngleZ;

		part.rotationPointX = x;
		part.rotationPointY = y;
		part.rotationPointZ = z;
		part.rotateAngleX = angleX;
		part.rotateAngleY = angleY;
		part.rotateAngleZ = angleZ;
		part.render(scale);

		part.rotationPointX = oldPointX;
		part.rotationPointY = oldPointY;
		part.rotationPointZ = oldPointZ;
		part.rotateAngleX = oldAngleX;
		part.rotateAngleY = oldAngleY;
		part.rotateAngleZ = oldAngleZ;
	}

	private float getCloakPitch(EntityPlayer player, float partialTicks) {
		float limbSwingAmount = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTicks;
		limbSwingAmount = MathHelper.clamp(limbSwingAmount, 0.0F, 1.0F);

		float swing = player.limbSwing - player.limbSwingAmount * (1.0F - partialTicks);
		float walkingSway = MathHelper.cos(swing * 0.6662F) * 0.18F * limbSwingAmount;
		float movementLift = limbSwingAmount * 0.55F;
		float runLift = getRunSpeedFactor(player) * 0.72F;
		return MathHelper.clamp(movementLift + walkingSway + runLift, 0.0F, MAX_CLOAK_PITCH);
	}

	private float getCloakBackOffset(EntityPlayer player, float partialTicks) {
		float limbSwingAmount = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTicks;
		limbSwingAmount = MathHelper.clamp(limbSwingAmount, 0.0F, 1.0F);

		return limbSwingAmount * 0.65F + getRunSpeedFactor(player) * 0.45F;
	}

	private float getRunSpeedFactor(EntityPlayer player) {
		double speedSq = player.motionX * player.motionX + player.motionZ * player.motionZ;
		float speed = (float) Math.sqrt(speedSq);
		return MathHelper.clamp((speed - RUN_SPEED_START) / (RUN_SPEED_FULL - RUN_SPEED_START), 0.0F, 1.0F);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
