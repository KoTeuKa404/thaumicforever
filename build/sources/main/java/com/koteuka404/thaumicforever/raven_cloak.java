// // Made with Blockbench 4.11.2
// // Exported for Minecraft version 1.7 - 1.12
// // Paste this class into your mod and generate all required imports


// public class raven_cloak extends ModelBase {
// 	private final ModelRenderer cloac;
// 	private final ModelRenderer chest;
// 	private final ModelRenderer right_wing;
// 	private final ModelRenderer left_wing;
// 	private final ModelRenderer head;

// 	public raven_cloak() {
// 		textureWidth = 128;
// 		textureHeight = 64;

// 		cloac = new ModelRenderer(this);
// 		cloac.setRotationPoint(0.0F, 24.0F, 0.0F);
		

// 		chest = new ModelRenderer(this);
// 		chest.setRotationPoint(0.0F, 0.0F, 0.0F);
// 		cloac.addChild(chest);
// 		chest.cubeList.add(new ModelBox(chest, 62, 24, 4.0F, -20.0F, -2.0F, 4, 12, 4, 1.0F, false));
// 		chest.cubeList.add(new ModelBox(chest, 62, 6, -4.0F, -20.0F, -2.0F, 8, 12, 4, 1.01F, false));
// 		chest.cubeList.add(new ModelBox(chest, 62, 24, -8.0F, -20.0F, -2.0F, 4, 12, 4, 1.0F, true));
// 		chest.cubeList.add(new ModelBox(chest, 0, 47, -9.5F, -21.25F, -3.5F, 19, 1, 7, 0.0F, false));

// 		right_wing = new ModelRenderer(this);
// 		right_wing.setRotationPoint(0.0F, 2.0F, 5.0F);
// 		cloac.addChild(right_wing);
// 		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -9.0F, -22.0F, -1.6F, 13, 15, 0, 0.0F, false));
// 		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -9.0F, -14.5F, -1.85F, 13, 12, 0, 0.0F, false));
// 		right_wing.cubeList.add(new ModelBox(right_wing, 48, 41, -3.75F, -18.0F, -1.5F, 13, 15, 0, 0.0F, false));

// 		left_wing = new ModelRenderer(this);
// 		left_wing.setRotationPoint(0.0F, 2.0F, 5.0F);
// 		cloac.addChild(left_wing);
// 		left_wing.cubeList.add(new ModelBox(left_wing, 48, 41, -4.0F, -22.0F, -1.75F, 13, 14, 0, 0.0F, false));

// 		head = new ModelRenderer(this);
// 		head.setRotationPoint(0.0F, 0.0F, 0.0F);
// 		cloac.addChild(head);
// 		head.cubeList.add(new ModelBox(head, 0, 0, -5.5F, -32.25F, -5.5F, 11, 11, 11, 0.0F, false));
// 	}

// 	@Override
// 	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
// 		cloac.render(f5);
// 	}

// 	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
// 		modelRenderer.rotateAngleX = x;
// 		modelRenderer.rotateAngleY = y;
// 		modelRenderer.rotateAngleZ = z;
// 	}
// }