package com.tutorialmod.turtywurty;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CustomModel extends ModelBase {
    public final ModelRenderer bb_main;
    public final ModelRenderer leftArm;
    public final ModelRenderer rightArm;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;
    public final ModelRenderer head;
    public final ModelRenderer body;

    public CustomModel() {
        textureWidth = 64;
        textureHeight = 64;

        // Основна частина тіла
        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);

        // Голова
        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -30.0F, 0.0F);
        head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false)); // Голова
        bb_main.addChild(head);

        // Тіло
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, -24.0F, 0.0F);
        body.cubeList.add(new ModelBox(body, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false)); // Тіло
        bb_main.addChild(body);

        // Ліва рука
        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(5.0F, -24.0F, 0.0F);
        leftArm.cubeList.add(new ModelBox(leftArm, 32, 48, -1.0F, -2.0F, -1.0F, 4, 12, 4, 0.0F, true)); // Ліва рука
        bb_main.addChild(leftArm);

        // Права рука
        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-5.0F, -24.0F, 0.0F);
        rightArm.cubeList.add(new ModelBox(rightArm, 40, 16, -3.0F, -2.0F, -1.0F, 4, 12, 4, 0.0F, false)); // Права рука
        bb_main.addChild(rightArm);

        // Ліва нога
        leftLeg = new ModelRenderer(this);
        leftLeg.setRotationPoint(1.9F, -12.0F, 0.0F);
        leftLeg.cubeList.add(new ModelBox(leftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true)); // Ліва нога
        bb_main.addChild(leftLeg);

        // Права нога
        rightLeg = new ModelRenderer(this);
        rightLeg.setRotationPoint(-1.9F, -12.0F, 0.0F);
        rightLeg.cubeList.add(new ModelBox(rightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false)); // Права нога
        bb_main.addChild(rightLeg);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        bb_main.render(f5);
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        // Анімація рук і ніг
        this.leftArm.rotateAngleX = (float) Math.cos(f * 0.6662F) * 1.4F * f1;
        this.rightArm.rotateAngleX = (float) Math.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
        this.leftLeg.rotateAngleX = (float) Math.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
        this.rightLeg.rotateAngleX = (float) Math.cos(f * 0.6662F) * 1.4F * f1;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public ModelRenderer getMainRenderer() {
        return this.bb_main;
    }
}
