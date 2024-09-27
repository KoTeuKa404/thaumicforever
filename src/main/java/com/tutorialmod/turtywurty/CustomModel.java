package com.tutorialmod.turtywurty;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CustomModel extends ModelBase {
    public final ModelRenderer bb_main;
    public final ModelRenderer leftArm;  // Зроблено публічним
    public final ModelRenderer rightArm; // Зроблено публічним
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;

    public CustomModel() {
        textureWidth = 64;
        textureHeight = 64;

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -1.0F, -30.0F, -1.0F, 2, 7, 2, 0.0F, false)); // Це голова
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 26, -6.0F, -24.0F, -1.5F, 12, 3, 3, 0.0F, false)); // Це верхня частина тіла
        
        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(5.0F, -24.0F, -1.0F);
        leftArm.cubeList.add(new ModelBox(leftArm, 32, 16, 0.0F, 0.0F, 0.0F, 2, 12, 2, 0.0F, true)); // Ліва рука
        bb_main.addChild(leftArm);
        
        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-7.0F, -24.0F, -1.0F);
        rightArm.cubeList.add(new ModelBox(rightArm, 24, 0, 0.0F, 0.0F, 0.0F, 2, 12, 2, 0.0F, false)); // Права рука
        bb_main.addChild(rightArm);
        
        leftLeg = new ModelRenderer(this);
        leftLeg.setRotationPoint(0.9F, -12.0F, -1.0F);
        leftLeg.cubeList.add(new ModelBox(leftLeg, 40, 16, 0.0F, 0.0F, 0.0F, 2, 11, 2, 0.0F, true)); // Ліва нога
        bb_main.addChild(leftLeg);
        
        rightLeg = new ModelRenderer(this);
        rightLeg.setRotationPoint(-3.05F, -12.0F, -1.0F);
        rightLeg.cubeList.add(new ModelBox(rightLeg, 8, 0, 0.0F, 0.0F, 0.0F, 2, 11, 2, 0.0F, false)); // Права нога
        bb_main.addChild(rightLeg);

        bb_main.cubeList.add(new ModelBox(bb_main, 48, 16, 1.0F, -21.0F, -1.0F, 2, 7, 2, 0.0F, false)); // Лівий кусок тіла
        bb_main.cubeList.add(new ModelBox(bb_main, 16, 0, -3.0F, -21.0F, -1.0F, 2, 7, 2, 0.0F, false)); // Правий кусок тіла
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 48, -4.0F, -14.0F, -1.0F, 8, 2, 2, 0.0F, false)); // Нижній кусок тіла
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

    // Додаємо публічний �