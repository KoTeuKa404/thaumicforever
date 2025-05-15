package com.koteuka404.thaumicforever;


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGorillaHand extends ModelBase {
	private final ModelRenderer hand;
	private final ModelRenderer cube_r1;
	private final ModelRenderer bone;
	private final ModelRenderer finger;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;

	public ModelGorillaHand() {
		textureWidth = 64;
		textureHeight = 64;

		hand = new ModelRenderer(this);
		hand.setRotationPoint(0.0F, 24.0F, 0.0F);
		hand.cubeList.add(new ModelBox(hand, 29, 15, -1.75F, -8.25F, -2.5F, 3, 3, 3, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(3.0F, -11.0F, 0.0F);
		hand.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, -1.5708F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 20, 15, -4.0F, -2.0F, -0.25F, 3, 6, 6, 0.0F, false));
		cube_r1.cubeList.add(new ModelBox(cube_r1, 20, 3, -1.0F, -3.0F, 0.75F, 3, 7, 6, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 4, 2, -1.25F, -5.25F, -2.0F, 2, 4, 2, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 9, 7, -0.75F, -1.25F, -1.5F, 1, 1, 1, 0.0F, false));

		finger = new ModelRenderer(this);
		finger.setRotationPoint(0.0F, 24.0F, 0.0F);
		finger.cubeList.add(new ModelBox(finger, 16, 53, -4.75F, -17.0F, 0.0F, 2, 2, 2, 0.0F, false));
		finger.cubeList.add(new ModelBox(finger, 32, 56, -1.75F, -17.0F, 1.0F, 2, 2, 2, 0.0F, false));
		finger.cubeList.add(new ModelBox(finger, 40, 56, 1.25F, -17.0F, 0.0F, 2, 2, 2, 0.0F, false));
		finger.cubeList.add(new ModelBox(finger, 24, 57, 2.25F, -16.0F, -4.0F, 2, 2, 2, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(3.0F, -13.0F, 0.0F);
		finger.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -1.5708F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 23, 39, -1.0F, -2.0F, 0.75F, 3, 1, 6, 0.0F, false));
		cube_r2.cubeList.add(new ModelBox(cube_r2, 18, 35, -4.0F, -1.0F, -0.25F, 3, 1, 6, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(2.7708F, -18.6742F, -3.0F);
		finger.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, -0.2182F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 3, 18, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(3.0612F, -17.1585F, -3.0F);
		finger.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, -0.1745F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 32, 45, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(2.25F, -18.1585F, 0.8112F);
		finger.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.1745F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 57, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-0.75F, -19.6742F, 1.5208F);
		finger.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.2182F, 0.0F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 3, 18, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(2.25F, -19.6742F, 0.5208F);
		finger.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.2182F, 0.0F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 2, 17, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-0.75F, -18.1585F, 1.8112F);
		finger.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.1745F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 42, 50, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-3.75F, -18.1585F, 0.8112F);
		finger.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.1745F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 24, 53, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-3.75F, -19.6742F, 0.5208F);
		finger.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.2182F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 5, 18, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		hand.render(f5);
		bone.render(f5);
		finger.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}