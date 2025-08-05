package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelJar extends ModelBase {
	private final ModelRenderer Core;
	private final ModelRenderer Lid;

	public ModelJar() {
		textureWidth = 64;
		textureHeight = 32;

		Core = new ModelRenderer(this);
		Core.setRotationPoint(0.0F, 0.0F, 0.0F);
		Core.cubeList.add(new ModelBox(Core, 0, 0, -5.0F, 4.0F, -5.0F, 10, 12, 10, 0.0F, false));

		Lid = new ModelRenderer(this);
		Lid.setRotationPoint(0.0F, 0.0F, 0.0F);
		Lid.cubeList.add(new ModelBox(Lid, 0, 24, -3.0F, 2.0F, -3.0F, 6, 2, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Core.render(f5);
		Lid.render(f5);
	}
	public void renderCore(float scale) {
		Core.render(scale);
	}
	
	public void renderLid(float scale) {
		Lid.render(scale);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}