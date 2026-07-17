// // Made with Blockbench 4.12.6
// // Exported for Minecraft version 1.7 - 1.12
// // Paste this class into your mod and generate all required imports


// public class DarkCloakModel extends ModelBase {
// 	private final ModelRenderer cloak;
// 	private final ModelRenderer head;
// 	private final ModelRenderer chest;
// 	private final ModelRenderer arms;
// 	private final ModelRenderer right_arm;
// 	private final ModelRenderer left_arm;
// 	private final ModelRenderer wing;
// 	private final ModelRenderer right_wing;
// 	private final ModelRenderer left_wing;

// 	public DarkCloakModel() {
// 		textureWidth = 128;
// 		textureHeight = 64;

// 		cloak = new ModelRenderer(this);
// 		cloak.setRotationPoint(0.0F, 26.0F, 0.0F);
		

// 		head = new ModelRenderer(this);
// 		head.setRotationPoint(-0.5F, -25.75F, -0.5F);
// 		cloak.addChild(head);
// 		head.cubeList.add(new ModelBox(head, 3, 2, -4.0F, -9.0F, -4.0F, 9, 9, 9, 0.0F, false));

// 		chest = new ModelRenderer(this);
// 		chest.setRotationPoint(0.0F, -11.0F, -2.0F);
// 		cloak.addChild(chest);
// 		chest.cubeList.add(new ModelBox(chest, 77, 6, -4.0F, -14.0F, 0.15F, 8, 12, 4, 0.9F, false));

// 		arms = new ModelRenderer(this);
// 		arms.setRotationPoint(-5.0F, -25.0F, 0.0F);
// 		cloak.addChild(arms);
		

// 		right_arm = new ModelRenderer(this);
// 		right_arm.setRotationPoint(0.0F, 0.0F, 0.0F);
// 		arms.addChild(right_arm);
// 		right_arm.cubeList.add(new ModelBox(right_arm, 59, 23, -2.5F, -0.5F, -2.0F, 3, 11, 4, 1.0F, true));

// 		left_arm = new ModelRenderer(this);
// 		left_arm.setRotationPoint(10.0F, 0.0F, 0.0F);
// 		arms.addChild(left_arm);
// 		left_arm.cubeList.add(new ModelBox(left_arm, 59, 23, -0.75F, -0.5F, -2.0F, 3, 11, 4, 1.0F, false));

// 		wing = new ModelRenderer(this);
// 		wing.setRotationPoint(0.0F, -25.0F, 2.0F);
// 		cloak.addChild(wing);
		

// 		right_wing = new ModelRenderer(this);
// 		right_wing.setRotationPoint(0.0F, 0.0F, 1.0F);
// 		wing.addChild(right_wing);
// 		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -8.5F, -1.25F, 0.3F, 13, 15, 0, 0.0F, false));
// 		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -6.0F, 2.5F, 0.15F, 13, 12, 0, 0.0F, false));
// 		right_wing.cubeList.add(new ModelBox(right_wing, 48, 41, -4.75F, 7.0F, -0.05F, 13, 15, 0, 0.0F, false));

// 		left_wing = new ModelRenderer(this);
// 		left_wing.setRotationPoint(0.0F, 1.0F, 1.0F);
// 		wing.addChild(left_wing);
// 		left_wing.cubeList.add(new ModelBox(left_wing, 48, 41, -4.5F, -2.25F, 0.1F, 13, 14, 0, 0.0F, false));
// 	}

// 	@Override
// 	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
// 		cloak.render(f5);
// 	}

// 	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
// 		modelRenderer.rotateAngleX = x;
// 		modelRenderer.rotateAngleY = y;
// 		modelRenderer.rotateAngleZ = z;
// 	}
// }