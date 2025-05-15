package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class WindCharge extends ModelBase {
	private final ModelRenderer charge;
	private final ModelRenderer wind;

	public WindCharge() {
		textureWidth = 64;
		textureHeight = 32;

		charge = new ModelRenderer(this);
		charge.setRotationPoint(0.0F, 22.0F, 0.0F);
		setRotationAngle(charge, 3.1416F, 0.0F, 0.0F);
		charge.cubeList.add(new ModelBox(charge, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		wind = new ModelRenderer(this);
		wind.setRotationPoint(0.0F, 22.0F, 0.0F);
		wind.cubeList.add(new ModelBox(wind, 0, 9, -3.0F, -2.0F, -3.0F, 6, 4, 6, 0.0F, false));
		wind.cubeList.add(new ModelBox(wind, 15, 20, -4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		charge.render(f5);
		wind.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}