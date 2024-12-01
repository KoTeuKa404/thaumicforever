// // Made with Blockbench 4.11.2
// // Exported for Minecraft version 1.7 - 1.12
// // Paste this class into your mod and generate all required imports


// public class RavenCloakModel extends ModelBase {
// 	private final ModelRenderer cloac;
// 	private final ModelRenderer right_wing;
// 	private final ModelRenderer left_wing;
// 	private final ModelRenderer head;
// 	private final ModelRenderer right_arm;
// 	private final ModelRenderer left_arm;
// 	private final ModelRenderer chest;

// 	public RavenCloakModel() {
// 		textureWidth = 128;
// 		textureHeight = 64;

// 		cloac = new ModelRenderer(this);
// 		cloac.setRotationPoint(0.0F, 24.0F, 0.0F);
		

// 		right_wing = new ModelRenderer(this);
// 		right_wing.setRotationPoint(0.0F, -20.0F, 3.0F);
// 		cloac.addChild(right_wing);
// 		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -9.0F, -0.25F, 0.3F, 13, 15, 0, 0.0F, false));
// 		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -6.0F, 3.5F, 0.15F, 13, 12, 0, 0.0F, false));
// 		right_wing.cubeList.add(new ModelBox(right_wing, 48, 41, -4.75F, 8.0F, -0.05F, 13, 15, 0, 0.0F, false));

// 		left_wing = new ModelRenderer(this);
// 		left_wing.setRotationPoint(0.0F, -19.0F, 3.0F);
// 		cloac.addChild(left_wing);
// 		left_wing.cubeList.add(new ModelBox(left_wing, 48, 41, -4.0F, -1.25F, 0.1F, 13, 14, 0, 0.0F, false));

// 		head = new ModelRenderer(this);
// 		head.setRotationPoint(0.0F, -19.0F, 0.0F);
// 		cloac.addChild(head);
// 		head.cubeList.add(new ModelBox(head, 3, 2, -4.5F, -9.5F, -4.2F, 9, 9, 9, 0.0F, false));

// 		right_arm = new ModelRenderer(this);
// 		right_arm.setRotationPoint(-5.0F, -20.0F, 0.0F);
// 		cloac.addChild(right_arm);
// 		right_arm.cubeList.add(new ModelBox(right_arm, 59, 23, -3.0F, 0.5F, -2.0F, 3, 11, 4, 1.0F, true));

// 		left_arm = new ModelRenderer(this);
// 		left_arm.setRotationPoint(5.0F, -20.0F, 0.0F);
// 		cloac.addChild(left_arm);
// 		left_arm.cubeList.add(new ModelBox(left_arm, 59, 23, 0.0F, 0.5F, -2.0F, 3, 11, 4, 1.0F, false));

// 		chest = new ModelRenderer(this);
// 		chest.setRotationPoint(0.0F, 0.0F, 0.0F);
// 		chest.cubeList.add(new ModelBox(chest, 78, 7, -4.0F, 5.0F, -0.25F, 8, 12, 3, 0.9F, false));
// 	}

// 	@Override
// 	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
// 		cloac.render(f5);
// 		chest.render(f5);
// 	}

// 	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
// 		modelRenderer.rotateAngleX = x;
// 		modelRenderer.rotateAngleY = y;
// 		modelRenderer.rotateAngleZ = z;
// 	}
// }