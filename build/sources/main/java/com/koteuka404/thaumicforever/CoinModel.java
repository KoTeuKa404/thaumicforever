package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
public class CoinModel extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer nose;
	private final ModelRenderer headwear;
	private final ModelRenderer headwear2;
	private final ModelRenderer body;
	private final ModelRenderer bodywear;
	private final ModelRenderer arms;
	private final ModelRenderer mirrored;
	private final ModelRenderer right_leg;
	private final ModelRenderer left_leg;

	public CoinModel() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F, false));

		nose = new ModelRenderer(this);
		nose.setRotationPoint(0.0F, -2.0F, 0.0F);
		nose.cubeList.add(new ModelBox(nose, 24, 0, -1.0F, -1.0F, -6.0F, 2, 4, 2, 0.0F, false));

		headwear = new ModelRenderer(this);
		headwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		headwear.cubeList.add(new ModelBox(headwear, 32, 0, -4.0F, -10.0F, -4.0F, 8, 10, 8, 0.51F, false));

		headwear2 = new ModelRenderer(this);
		headwear2.setRotationPoint(0.0F, 0.0F, 0.0F);
		setRotationAngle(headwear2, -1.5708F, 0.0F, 0.0F);
		headwear2.cubeList.add(new ModelBox(headwear2, 30, 47, -8.0F, -8.0F, -6.0F, 16, 16, 1, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 16, 20, -4.0F, 0.0F, -3.0F, 8, 12, 6, 0.0F, false));

		bodywear = new ModelRenderer(this);
		bodywear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bodywear.cubeList.add(new ModelBox(bodywear, 0, 38, -4.0F, 0.0F, -3.0F, 8, 20, 6, 0.5F, false));

		arms = new ModelRenderer(this);
		arms.setRotationPoint(0.0F, 2.95F, -1.05F);
		setRotationAngle(arms, -0.7505F, 0.0F, 0.0F);
		arms.cubeList.add(new ModelBox(arms, 40, 38, -4.0F, 3.5F, -2.0F, 8, 4, 4, 0.0F, false));
		arms.cubeList.add(new ModelBox(arms, 44, 22, -8.0F, -1.5F, -2.0F, 4, 9, 4, 0.0F, false));

		mirrored = new ModelRenderer(this);
		mirrored.setRotationPoint(0.0F, 21.05F, 1.05F);
		arms.addChild(mirrored);
		mirrored.cubeList.add(new ModelBox(mirrored, 44, 22, 4.0F, -22.55F, -3.05F, 4, 9, 4, 0.0F, true));

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		right_leg.cubeList.add(new ModelBox(right_leg, 0, 22, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(2.0F, 12.0F, 0.0F);
		left_leg.cubeList.add(new ModelBox(left_leg, 0, 22, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		nose.render(f5);
		headwear.render(f5);
		headwear2.render(f5);
		body.render(f5);
		bodywear.render(f5);
		arms.render(f5);
		right_leg.render(f5);
		left_leg.render(f5);
	}
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		head.rotateAngleY = netHeadYaw * 0.017453292F;
		head.rotateAngleX = headPitch * 0.017453292F;
		headwear.rotateAngleY = netHeadYaw * 0.017453292F;
		headwear.rotateAngleX = headPitch * 0.017453292F;
		nose.rotateAngleY = head.rotateAngleY;
		nose.rotateAngleX = head.rotateAngleX;

		

		right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		right_leg.rotateAngleY = 0.0F;
		left_leg.rotateAngleY = 0.0F;
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}