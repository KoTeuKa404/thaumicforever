// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


public class breeze_wind - Converted extends ModelBase {
	private final ModelRenderer wind_top;
	private final ModelRenderer wind_middle;
	private final ModelRenderer wind_bottom;

	public breeze_wind - Converted() {
		textureWidth = 128;
		textureHeight = 128;

		wind_top = new ModelRenderer(this);
		wind_top.setRotationPoint(0.0F, 11.0F, 0.0F);
		wind_top.cubeList.add(new ModelBox(wind_top, 105, 57, -2.5F, -8.0F, -2.5F, 5, 8, 5, 0.0F, false));
		wind_top.cubeList.add(new ModelBox(wind_top, 0, 0, -9.0F, -8.0F, -9.0F, 18, 8, 18, 0.0F, false));
		wind_top.cubeList.add(new ModelBox(wind_top, 6, 6, -6.0F, -8.0F, -6.0F, 12, 8, 12, 0.0F, false));

		wind_middle = new ModelRenderer(this);
		wind_middle.setRotationPoint(0.0F, 17.0F, 0.0F);
		wind_middle.cubeList.add(new ModelBox(wind_middle, 74, 28, -6.0F, -6.0F, -6.0F, 12, 6, 12, 0.0F, false));
		wind_middle.cubeList.add(new ModelBox(wind_middle, 78, 32, -4.0F, -6.0F, -4.0F, 8, 6, 8, 0.0F, false));
		wind_middle.cubeList.add(new ModelBox(wind_middle, 49, 71, -2.5F, -6.0F, -2.5F, 5, 6, 5, 0.0F, false));

		wind_bottom = new ModelRenderer(this);
		wind_bottom.setRotationPoint(0.0F, 24.0F, 0.0F);
		wind_bottom.cubeList.add(new ModelBox(wind_bottom, 1, 83, -2.5F, -7.0F, -2.5F, 5, 7, 5, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		wind_top.render(f5);
		wind_middle.render(f5);
		wind_bottom.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}