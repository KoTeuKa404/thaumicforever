package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class CustomModel extends ModelBase {
    public final ModelRenderer bb_main;
    public final ModelRenderer head;
    public final ModelRenderer upperBody;
    public final ModelRenderer leftArm;
    public final ModelRenderer rightArm;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;
    public final ModelRenderer leftBodyPiece;
    public final ModelRenderer rightBodyPiece;
    public final ModelRenderer lowerBody;

    public CustomModel() {
        textureWidth = 64;
        textureHeight = 64;

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -30.0F, 0.0F);
        head.cubeList.add(new ModelBox(head, 0, 0, -1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F, false));
        bb_main.addChild(head);

        upperBody = new ModelRenderer(this);
        upperBody.setRotationPoint(0.0F, -24.0F, 0.0F);
        upperBody.cubeList.add(new ModelBox(upperBody, 0, 26, -6.0F, 0.0F, -1.5F, 12, 3, 3, 0.0F, false));
        bb_main.addChild(upperBody);

        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(5.0F, -24.0F, -1.0F);
        leftArm.cubeList.add(new ModelBox(leftArm, 32, 16, 0.0F, 0.0F, 0.0F, 2, 12, 2, 0.0F, true));
        bb_main.addChild(leftArm);

        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-7.0F, -24.0F, -1.0F);
        rightArm.cubeList.add(new ModelBox(rightArm, 24, 0, 0.0F, 0.0F, 0.0F, 2, 12, 2, 0.0F, false));
        bb_main.addChild(rightArm);

        leftLeg = new ModelRenderer(this);
        leftLeg.setRotationPoint(0.9F, -12.0F, -1.0F);
        leftLeg.cubeList.add(new ModelBox(leftLeg, 40, 16, 0.0F, 0.0F, 0.0F, 2, 11, 2, 0.0F, true));
        bb_main.addChild(leftLeg);

        rightLeg = new ModelRenderer(this);
        rightLeg.setRotationPoint(-3.05F, -12.0F, -1.0F);
        rightLeg.cubeList.add(new ModelBox(rightLeg, 8, 0, 0.0F, 0.0F, 0.0F, 2, 11, 2, 0.0F, false));
        bb_main.addChild(rightLeg);

        leftBodyPiece = new ModelRenderer(this);
        leftBodyPiece.setRotationPoint(1.0F, -21.0F, -1.0F);
        leftBodyPiece.cubeList.add(new ModelBox(leftBodyPiece, 48, 16, 0.0F, 0.0F, 0.0F, 2, 7, 2, 0.0F, false));
        bb_main.addChild(leftBodyPiece);

        rightBodyPiece = new ModelRenderer(this);
        rightBodyPiece.setRotationPoint(-3.0F, -21.0F, -1.0F);
        rightBodyPiece.cubeList.add(new ModelBox(rightBodyPiece, 16, 0, 0.0F, 0.0F, 0.0F, 2, 7, 2, 0.0F, false));
        bb_main.addChild(rightBodyPiece);

        lowerBody = new ModelRenderer(this);
        lowerBody.setRotationPoint(0.0F, -14.0F, -1.0F);
        lowerBody.cubeList.add(new ModelBox(lowerBody, 0, 48, -4.0F, 0.0F, 0.0F, 8, 2, 2, 0.0F, false));
        bb_main.addChild(lowerBody);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        bb_main.render(f5);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.leftArm.rotateAngleX = (float) Math.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;
        this.rightArm.rotateAngleX = (float) Math.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.1F * limbSwingAmount;
        this.leftLeg.rotateAngleX = (float) Math.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.1F * limbSwingAmount;
        this.rightLeg.rotateAngleX = (float) Math.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;

        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getSwingProgress(scaleFactor) > 0.0F) {
            float swingProgress = ((EntityLivingBase) entity).getSwingProgress(scaleFactor);
            this.rightArm.rotateAngleX = -2.0F + 1.5F * swingProgress; 
            this.rightArm.rotateAngleY = 0.0F;
            this.rightArm.rotateAngleZ = (float) Math.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        }
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
