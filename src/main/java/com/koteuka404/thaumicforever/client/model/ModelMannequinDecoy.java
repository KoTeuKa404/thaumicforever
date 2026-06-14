package com.koteuka404.thaumicforever.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMannequinDecoy extends ModelBase {
    public final ModelRenderer head;
    public final ModelRenderer body;
    public final ModelRenderer leftArm;
    public final ModelRenderer rightArm;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;
    public final ModelRenderer chestPlate;
    public final ModelRenderer waistRope;
    public final ModelRenderer shoulderStick;

    public ModelMannequinDecoy() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        setRotateAngle(this.head, 0.1309F, 0.0F, 0.1309F);

        this.body = new ModelRenderer(this, 16, 16);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);

        this.leftArm = new ModelRenderer(this, 40, 16);
        this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        setRotateAngle(this.leftArm, -0.1361F, -0.3255F, -0.2523F);

        this.rightArm = new ModelRenderer(this, 40, 32);
        this.rightArm.mirror = true;
        this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.rightArm.mirror = false;
        setRotateAngle(this.rightArm, 0.0F, 0.0F, 0.12F);

        this.leftLeg = new ModelRenderer(this, 0, 16);
        this.leftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);

        this.rightLeg = new ModelRenderer(this, 0, 32);
        this.rightLeg.mirror = true;
        this.rightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.rightLeg.mirror = false;
        setRotateAngle(this.rightLeg, 0.0F, 0.0F, 0.2182F);

        this.chestPlate = new ModelRenderer(this, 20, 32);
        this.chestPlate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.chestPlate.addBox(-4.5F, 1.0F, -2.5F, 9, 9, 1, 0.0F);

        this.waistRope = new ModelRenderer(this, 16, 42);
        this.waistRope.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.waistRope.addBox(-4.5F, 10.0F, -2.5F, 9, 2, 5, 0.0F);

        this.shoulderStick = new ModelRenderer(this, 16, 50);
        this.shoulderStick.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.shoulderStick.addBox(-6.0F, -1.0F, -1.0F, 12, 2, 2, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.head.render(scale);
        this.body.render(scale);
        this.leftArm.render(scale);
        this.rightArm.render(scale);
        this.leftLeg.render(scale);
        this.rightLeg.render(scale);
        this.chestPlate.render(scale);
        this.waistRope.render(scale);
        this.shoulderStick.render(scale);
    }

    private static void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
