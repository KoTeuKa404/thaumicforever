package com.koteuka404.thaumicforever.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelArcaneTurret extends ModelBase {
	private final ModelRenderer leg2;
	private final ModelRenderer tripod;
	private final ModelRenderer leg3;
	private final ModelRenderer leg4;
	private final ModelRenderer leg1;
	private final ModelRenderer base;
	private final ModelRenderer crystal;
	private final ModelRenderer dome;
	private final ModelRenderer domebase;
	private final ModelRenderer rod;

	public ModelArcaneTurret() {
		textureWidth = 64;
		textureHeight = 32;

		leg2 = new ModelRenderer(this);
		leg2.setRotationPoint(0.0F, 12.0F, 0.0F);
		leg2.cubeList.add(new ModelBox(leg2, 20, 10, -1.0F, 1.0F, -1.0F, 2, 13, 2, 0.0F, false));
		setRotationAngle(leg2, 0.5235988F, 1.570796F, 0.0F);

		tripod = new ModelRenderer(this);
		tripod.setRotationPoint(0.0F, 12.0F, 0.0F);
		tripod.cubeList.add(new ModelBox(tripod, 13, 0, -1.5F, 0.0F, -1.5F, 3, 2, 3, 0.0F, false));

		leg3 = new ModelRenderer(this);
		leg3.setRotationPoint(0.0F, 12.0F, 0.0F);
		leg3.cubeList.add(new ModelBox(leg3, 20, 10, -1.0F, 1.0F, -1.0F, 2, 13, 2, 0.0F, false));
		setRotationAngle(leg3, 0.5235988F, 3.141593F, 0.0F);

		leg4 = new ModelRenderer(this);
		leg4.setRotationPoint(0.0F, 12.0F, 0.0F);
		leg4.cubeList.add(new ModelBox(leg4, 20, 10, -1.0F, 1.0F, -1.0F, 2, 13, 2, 0.0F, false));
		setRotationAngle(leg4, 0.5235988F, 4.712389F, 0.0F);

		leg1 = new ModelRenderer(this);
		leg1.setRotationPoint(0.0F, 12.0F, 0.0F);
		leg1.cubeList.add(new ModelBox(leg1, 20, 10, -1.0F, 1.0F, -1.0F, 2, 13, 2, 0.0F, false));
		setRotationAngle(leg1, 0.5235988F, 0.0F, 0.0F);

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 13.0F, 0.0F);
		base.cubeList.add(new ModelBox(base, 32, 0, -3.0F, -6.0F, -3.0F, 6, 6, 6, 0.0F, false));

		crystal = new ModelRenderer(this);
		crystal.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(crystal);
		crystal.cubeList.add(new ModelBox(crystal, 32, 25, -1.0F, -4.0F, 5.0F, 2, 2, 2, 0.0F, false));

		dome = new ModelRenderer(this);
		dome.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(dome);
		dome.cubeList.add(new ModelBox(dome, 44, 16, -2.0F, -5.0F, 4.0F, 4, 4, 4, 0.0F, false));

		domebase = new ModelRenderer(this);
		domebase.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(domebase);
		domebase.cubeList.add(new ModelBox(domebase, 32, 19, -2.0F, -5.0F, 3.0F, 4, 4, 1, 0.0F, false));

		rod = new ModelRenderer(this);
		rod.setRotationPoint(8.0F, -11.0F, 6.0F);
		setRotationAngle(rod, -1.5708F, 0.0F, 0.0F);
		rod.cubeList.add(new ModelBox(rod, 0, 0, -9.0F, 10.0F, 7.0F, 2, 11, 2, 0.0F, false));
		rod.cubeList.add(new ModelBox(rod, 8, 5, -10.0F, 17.0F, 6.0F, 4, 1, 4, 0.0F, false));
		rod.cubeList.add(new ModelBox(rod, 1, 25, -11.0F, 13.0F, 5.0F, 6, 1, 6, 0.0F, false));
		rod.cubeList.add(new ModelBox(rod, 3, 20, -10.0F, 9.0F, 6.0F, 4, 1, 4, 0.0F, false));
		rod.cubeList.add(new ModelBox(rod, 4, 10, -10.0F, 21.0F, 6.0F, 4, 4, 4, 0.0F, false));
		base.addChild(rod);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		leg2.render(f5);
		tripod.render(f5);
		leg3.render(f5);
		leg4.render(f5);
		leg1.render(f5);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		base.render(f5);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
		this.base.rotateAngleY = netHeadYaw / 57.295776F;
		this.base.rotateAngleX = headPitch / 57.295776F;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
