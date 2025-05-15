package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Monkey extends ModelBase {
    public final ModelRenderer MainBody;
    public final ModelRenderer cube_r1;
    public final ModelRenderer UnderBody;
    public final ModelRenderer cube_r2;
    public final ModelRenderer cube_r2_r1;
    public final ModelRenderer right_arm;
    public final ModelRenderer right_arm_r1;
    public final ModelRenderer right_arm_r2;
    public final ModelRenderer right_Leg;
    public final ModelRenderer head;
    public final ModelRenderer cube_r5;
    public final ModelRenderer left_arm;
    public final ModelRenderer right_arm_r3;
    public final ModelRenderer left_arm_r1;
    public final ModelRenderer left_Leg;
    public float animationProgress = 0.0F; //don`t work this :(

    public Monkey() {
        textureWidth = 128;
        textureHeight = 128;

        MainBody = new ModelRenderer(this);
        MainBody.setRotationPoint(0.0F, 10.0F, 6.0F);
        setRotationAngle(MainBody, 0.0F, 3.1416F, 0.0F);

        cube_r1 = new ModelRenderer(this);
        cube_r1.setRotationPoint(-0.5F, -7.0F, -2.25F);
        MainBody.addChild(cube_r1);
        setRotationAngle(cube_r1, -2.1817F, 0.0F, 0.0F);
        cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -7.0F, -11.0F, -4.75F, 14, 10, 10, 0.0F, false));

        UnderBody = new ModelRenderer(this);
        UnderBody.setRotationPoint(0.0F, 10.0F, 6.0F);
        setRotationAngle(UnderBody, 0.0F, 3.1416F, 0.0F);

        cube_r2 = new ModelRenderer(this);
        cube_r2.setRotationPoint(0.0F, 12.6F, -8.6F);
        UnderBody.addChild(cube_r2);
        setRotationAngle(cube_r2, -0.6545F, 0.0F, 0.0F);

        cube_r2_r1 = new ModelRenderer(this);
        cube_r2_r1.setRotationPoint(0.0F, -0.6F, 9.0F);
        cube_r2.addChild(cube_r2_r1);
        setRotationAngle(cube_r2_r1, 0.0524F, 0.0F, 0.0F);
        cube_r2_r1.cubeList.add(new ModelBox(cube_r2_r1, 0, 20, -5.5F, -14.3F, -14.0F, 10, 8, 10, 0.0F, false));

        right_arm = new ModelRenderer(this);
        right_arm.setRotationPoint(-6.4F, 7.0F, 4.0F);
        setRotationAngle(right_arm, 0.0F, 0.8727F, 1.5708F);

        right_arm_r1 = new ModelRenderer(this);
        right_arm_r1.setRotationPoint(17.9F, 4.0F, -4.0F);
        right_arm.addChild(right_arm_r1);
        setRotationAngle(right_arm_r1, 0.0F, 0.1745F, -3.1416F);
        right_arm_r1.cubeList.add(new ModelBox(right_arm_r1, 29, 73, -2.7F, -2.0F, 5.0F, 2, 6, 5, 0.0F, false));

        right_arm_r2 = new ModelRenderer(this);
        right_arm_r2.setRotationPoint(-6.6F, 22.0F, -4.0F);
        right_arm.addChild(right_arm_r2);
        setRotationAngle(right_arm_r2, 0.0F, -0.1745F, 0.0F);
        right_arm_r2.cubeList.add(new ModelBox(right_arm_r2, 48, 0, 4.0F, -22.0F, 0.0F, 14, 6, 6, 0.0F, true));
        right_arm_r2.cubeList.add(new ModelBox(right_arm_r2, 36, 38, 15.0F, -23.0F, -1.0F, 10, 8, 8, 0.0F, true));

        right_Leg = new ModelRenderer(this);
        right_Leg.setRotationPoint(-5.0F, 16.0F, 9.0F);
        right_Leg.cubeList.add(new ModelBox(right_Leg, 66, 54, -5.5F, -2.75F, -4.25F, 6, 7, 7, 0.0F, false));
        right_Leg.cubeList.add(new ModelBox(right_Leg, 48, 12, -5.0F, 6.0F, -4.0F, 5, 2, 6, 0.0F, false));
        right_Leg.cubeList.add(new ModelBox(right_Leg, 44, 73, -5.0F, 1.0F, -3.0F, 5, 5, 5, 0.0F, false));

        head = new ModelRenderer(this);
        head.setRotationPoint(0.5F, 4.0F, 0.0F);
        head.cubeList.add(new ModelBox(head, 48, 12, -4.0F, -5.0F, -10.0F, 8, 1, 1, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 0, 70, -4.5F, -8.0F, -9.0F, 9, 9, 5, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 40, 32, -3.0F, -3.0F, -11.0F, 6, 4, 2, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 1, 0, 3.75F, -5.0F, -6.0F, 1, 2, 2, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 1, 0, -4.75F, -5.0F, -6.0F, 1, 2, 2, 0.0F, true));

        cube_r5 = new ModelRenderer(this);
        cube_r5.setRotationPoint(0.5F, 0.0F, -4.0F);
        head.addChild(cube_r5);
        setRotationAngle(cube_r5, -0.1745F, 0.0F, 0.0F);
        cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 54, -5.0F, -9.0F, -3.0F, 9, 8, 8, 0.0F, false));
        cube_r5.cubeList.add(new ModelBox(cube_r5, 34, 54, -5.0F, -10.0F, -4.0F, 9, 11, 7, 0.0F, false));

        left_arm = new ModelRenderer(this);
        left_arm.setRotationPoint(7.6F, 7.0F, 4.0F);
        setRotationAngle(left_arm, 0.0F, 0.8727F, 1.5708F);

        right_arm_r3 = new ModelRenderer(this);
        right_arm_r3.setRotationPoint(-6.6F, 16.0F, -4.0F);
        left_arm.addChild(right_arm_r3);
        setRotationAngle(right_arm_r3, 0.0F, -0.1745F, 0.0F);
        right_arm_r3.cubeList.add(new ModelBox(right_arm_r3, 36, 38, 15.0F, -23.0F, -1.0F, 10, 8, 8, 0.0F, true));
        right_arm_r3.cubeList.add(new ModelBox(right_arm_r3, 48, 0, 4.0F, -22.0F, 0.0F, 14, 6, 6, 0.0F, true));

        left_arm_r1 = new ModelRenderer(this);
        left_arm_r1.setRotationPoint(17.9F, -2.0F, -4.0F);
        left_arm.addChild(left_arm_r1);
        setRotationAngle(left_arm_r1, 0.0F, 0.1745F, -3.1416F);
        left_arm_r1.cubeList.add(new ModelBox(left_arm_r1, 29, 85, -2.7F, -2.0F, 5.0F, 2, 6, 5, 0.0F, false));

        left_Leg = new ModelRenderer(this);
        left_Leg.setRotationPoint(6.0F, 16.0F, 9.0F);
        left_Leg.cubeList.add(new ModelBox(left_Leg, 66, 54, -0.5F, -2.75F, -4.25F, 6, 7, 7, 0.0F, false));
        left_Leg.cubeList.add(new ModelBox(left_Leg, 48, 12, 0.0F, 6.0F, -4.0F, 5, 2, 6, 0.0F, false));
        left_Leg.cubeList.add(new ModelBox(left_Leg, 44, 73, 0.0F, 1.0F, -3.0F, 5, 5, 5, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        MainBody.render(f5);
        UnderBody.render(f5);
        right_arm.render(f5);
        right_Leg.render(f5);
        head.render(f5);
        left_arm.render(f5);
        left_Leg.render(f5);
    }


    public float interpolateAnimation(float progress, float start, float end) {
        return (1.0F - progress) * start + progress * end;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
	public ModelRenderer getRightArm() {
        return this.right_arm;
    }

    public ModelRenderer getLeftArm() {
        return this.left_arm;
    }

    // @Override
    // public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    //     super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    
    //     this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
    //     this.head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
    
    //     this.right_arm.rotateAngleY = (float) Math.toRadians(45);
    //     this.left_arm.rotateAngleY = (float) Math.toRadians(45);
    
    //     this.right_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
    //     this.left_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
    
    //     this.right_arm.rotateAngleY += MathHelper.cos(limbSwing * 0.6662F) * 0.2F * limbSwingAmount;
    //     this.left_arm.rotateAngleY += MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.2F * limbSwingAmount;
    
    //     this.right_Leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.6F * limbSwingAmount;
    //     this.left_Leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.6F * limbSwingAmount;
    // }
    

}
