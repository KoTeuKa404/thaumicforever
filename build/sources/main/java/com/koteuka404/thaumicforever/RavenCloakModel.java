package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class RavenCloakModel extends ModelBase {
	private final ModelRenderer cloac;
	private final ModelRenderer chest;
	private final ModelRenderer right_wing;
	private final ModelRenderer left_wing;
	private final ModelRenderer head;
	private final ModelRenderer right_arm;
	private final ModelRenderer left_arm;
    public float rotateAngleY = 0.0F;
    public float rotateAngleX = 0.0F;

	public RavenCloakModel() {
		textureWidth = 128;
		textureHeight = 64;

		cloac = new ModelRenderer(this);
		cloac.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		right_wing = new ModelRenderer(this);
		right_wing.setRotationPoint(0.0F, -20.0F, 3.0F);
		cloac.addChild(right_wing);
		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -9.0F, -0.25F, 0.3F, 13, 15, 0, 0.0F, false));
		right_wing.cubeList.add(new ModelBox(right_wing, 49, 41, -6.0F, 3.5F, 0.15F, 13, 12, 0, 0.0F, false));
		right_wing.cubeList.add(new ModelBox(right_wing, 48, 41, -4.75F, 8.0F, -0.05F, 13, 15, 0, 0.0F, false));

		left_wing = new ModelRenderer(this);
		left_wing.setRotationPoint(0.0F, -19.0F, 3.0F);
		cloac.addChild(left_wing);
		left_wing.cubeList.add(new ModelBox(left_wing, 48, 41, -4.0F, -1.25F, 0.1F, 13, 14, 0, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -19.0F, 0.0F);
		cloac.addChild(head);
		head.cubeList.add(new ModelBox(head, 3, 2, -4.5F, -9.5F, -4.2F, 9, 9, 9, 0.0F, false));

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(-5.0F, -20.0F, 0.0F);
		cloac.addChild(right_arm);
		right_arm.cubeList.add(new ModelBox(right_arm, 59, 23, -3.0F, 0.5F, -2.0F, 3, 11, 4, 1.0F, true));

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(5.0F, -20.0F, 0.0F);
		cloac.addChild(left_arm);
		left_arm.cubeList.add(new ModelBox(left_arm, 59, 23, 0.0F, 0.5F, -2.0F, 3, 11, 4, 1.0F, false));

		chest = new ModelRenderer(this);
		chest.setRotationPoint(0.0F, 0.0F, 0.0F);
		chest.cubeList.add(new ModelBox(chest, 78, 7, -4.0F, 5.0F, -0.25F, 8, 12, 3, 0.9F, false));
	}
    // Рендер голови
    public void renderHead(Entity entity, float scale) {
        head.rotateAngleY = this.rotateAngleY;
        head.rotateAngleX = this.rotateAngleX; 
        head.render(scale);
    }

    // Рендер тіла
    public void renderBody(Entity entity, float scale) {
        cloac.render(scale);
    }
    public void renderChest(Entity entity, float scale) {
        chest.render(scale);

    }
    // Рендер правої руки
    public void renderRightArm(Entity entity, float scale) {
        right_arm.render(scale);
    }

    // Рендер лівої руки
    public void renderLeftArm(Entity entity, float scale) {
        left_arm.render(scale);
    }

    public void setToPlayerAngles(ModelPlayer modelPlayer, EntityPlayer player) {
    // Прив’язка голови
    head.rotateAngleY = modelPlayer.bipedHead.rotateAngleY; 
    head.rotateAngleX = modelPlayer.bipedHead.rotateAngleX; 

    // Права рука
    right_arm.rotateAngleX = modelPlayer.bipedRightArm.rotateAngleX; 
    right_arm.rotateAngleY = modelPlayer.bipedRightArm.rotateAngleY;
    right_arm.rotateAngleZ = modelPlayer.bipedRightArm.rotateAngleZ;
    // Ліва рука
    left_arm.rotateAngleX = modelPlayer.bipedLeftArm.rotateAngleX; 
    left_arm.rotateAngleY = modelPlayer.bipedLeftArm.rotateAngleY; 
    left_arm.rotateAngleZ = modelPlayer.bipedLeftArm.rotateAngleZ; 

    // Прив’язка тіла
    chest.rotateAngleX = modelPlayer.bipedBody.rotateAngleX;
    chest.rotateAngleY = modelPlayer.bipedBody.rotateAngleY; 
    chest.rotateAngleZ = modelPlayer.bipedBody.rotateAngleZ;

    // Прив’язка крил до руху тіла
    right_wing.rotateAngleX = chest.rotateAngleX;
    right_wing.rotateAngleY = chest.rotateAngleY;
    right_wing.rotateAngleZ = chest.rotateAngleZ;

    left_wing.rotateAngleX = chest.rotateAngleX;
    left_wing.rotateAngleY = chest.rotateAngleY;
    left_wing.rotateAngleZ = chest.rotateAngleZ;

    // // Динамічне зміщення залежно від стану гравця (присів чи ні)
    // float yOffset = player.isSneaking() ? 0.0F : -0.25F;
    // float zOffset = player.isSneaking() ? 0.0F : 0.0F;
    // chest.offsetY = yOffset; // Зміщення тіла вниз
    // chest.offsetZ = zOffset; // Зміщення тіла вперед
    // right_wing.offsetY = yOffset;
    // right_wing.offsetZ = zOffset;
    // left_wing.offsetY = yOffset;
    // left_wing.offsetZ = zOffset;
    // head.offsetY = yOffset;
    // head.offsetZ = zOffset;
    // right_arm.offsetY = yOffset;
    // right_arm.offsetZ = zOffset;
    // left_arm.offsetY = yOffset;
    // left_arm.offsetZ = zOffset;

}


}
