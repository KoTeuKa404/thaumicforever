package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelNodeMagnet
extends ModelBase {
    ModelRenderer crystal;
    ModelRenderer leg2;
    ModelRenderer tripod;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer leg1;
    ModelRenderer magl;
    ModelRenderer magbase;
    ModelRenderer magcross;
    ModelRenderer magr;
    ModelRenderer base;
    ModelRenderer domebase;
    ModelRenderer dome;

    public ModelNodeMagnet() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.leg2 = new ModelRenderer(this, 20, 10);
        this.leg2.addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg2.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg2.setTextureSize(64, 32);
        this.setRotation(this.leg2, 0.5235988f, 1.570796f, 0.0f);
        this.tripod = new ModelRenderer(this, 13, 0);
        this.tripod.addBox(-1.5f, 0.0f, -1.5f, 3, 2, 3);
        this.tripod.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.tripod.setTextureSize(64, 32);
        this.setRotation(this.tripod, 0.0f, 0.0f, 0.0f);
        this.leg3 = new ModelRenderer(this, 20, 10);
        this.leg3.addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg3.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg3.setTextureSize(64, 32);
        this.setRotation(this.leg3, 0.5235988f, 3.141593f, 0.0f);
        this.leg4 = new ModelRenderer(this, 20, 10);
        this.leg4.addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg4.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg4.setTextureSize(64, 32);
        this.setRotation(this.leg4, 0.5235988f, 4.712389f, 0.0f);
        this.leg1 = new ModelRenderer(this, 20, 10);
        this.leg1.addBox(-1.0f, 1.0f, -1.0f, 2, 13, 2);
        this.leg1.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.leg1.setTextureSize(64, 32);
        this.setRotation(this.leg1, 0.5235988f, 0.0f, 0.0f);
        this.base = new ModelRenderer(this, 32, 0);
        this.base.addBox(-3.0f, -6.0f, -3.0f, 6, 6, 6);
        this.base.setRotationPoint(0.0f, 13.0f, 0.0f);
        this.base.setTextureSize(64, 32);
        this.setRotation(this.base, 0.0f, 0.0f, 0.0f);
        this.crystal = new ModelRenderer(this, 32, 25);
        this.crystal.addBox(-1.0f, -4.0f, 5.0f, 2, 2, 2);
        this.crystal.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.crystal.setTextureSize(64, 32);
        this.setRotation(this.crystal, 0.0f, 0.0f, 0.0f);
        this.domebase = new ModelRenderer(this, 32, 19);
        this.domebase.addBox(-2.0f, -5.0f, 3.0f, 4, 4, 1);
        this.domebase.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.domebase.setTextureSize(64, 32);
        this.setRotation(this.domebase, 0.0f, 0.0f, 0.0f);
        this.dome = new ModelRenderer(this, 44, 16);
        this.dome.addBox(-2.0f, -5.0f, 4.0f, 4, 4, 4);
        this.dome.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dome.setTextureSize(64, 32);
        this.setRotation(this.dome, 0.0f, 0.0f, 0.0f);
        this.magbase = new ModelRenderer(this, 0, 18);
        this.magbase.addBox(-1.0f, -1.0f, -4.0f, 2, 2, 1);
        this.magbase.setRotationPoint(0.0f, -3.0f, 0.0f);
        this.magbase.setTextureSize(64, 32);
        this.setRotation(this.magbase, 0.0f, 0.0f, 0.0f);
        this.magcross = new ModelRenderer(this, 0, 14);
        this.magcross.addBox(-3.0f, -1.0f, -6.0f, 6, 2, 2);
        this.magcross.setRotationPoint(0.0f, -3.0f, 0.0f);
        this.magcross.setTextureSize(64, 32);
        this.setRotation(this.magcross, 0.0f, 0.0f, 0.0f);
        this.magr = new ModelRenderer(this, 0, 8);
        this.magr.addBox(-4.0f, -1.0f, -9.0f, 2, 2, 4);
        this.magr.setRotationPoint(0.0f, -3.0f, 0.0f);
        this.magr.setTextureSize(64, 32);
        this.magr.mirror = true;
        this.setRotation(this.magr, 0.0f, 0.0f, 0.0f);
        this.magl = new ModelRenderer(this, 0, 8);
        this.magl.addBox(2.0f, -1.0f, -9.0f, 2, 2, 4);
        this.magl.setRotationPoint(0.0f, -3.0f, 0.0f);
        this.magl.setTextureSize(64, 32);
        this.magr.mirror = true;
        this.setRotation(this.magl, 0.0f, 0.0f, 0.0f);
        this.base.addChild(this.crystal);
        this.base.addChild(this.dome);
        this.base.addChild(this.domebase);
        this.base.addChild(this.magbase);
        this.base.addChild(this.magcross);
        this.base.addChild(this.magl);
        this.base.addChild(this.magr);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.leg2.render(f5);
        this.tripod.render(f5);
        this.leg3.render(f5);
        this.leg4.render(f5);
        this.leg1.render(f5);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.base.render(f5);
        GL11.glDisable(3042);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float headpitch, float headyaw, float p_78087_6_, Entity entity) {
        this.base.rotateAngleY = headpitch / 57.295776f;
        this.base.rotateAngleX = headyaw / 57.295776f;
        this.magl.rotateAngleZ = this.magr.rotateAngleZ = ((EntityNodeMagnet)entity).rot / 57.295776f;
        this.magcross.rotateAngleZ = this.magr.rotateAngleZ;
        this.magbase.rotateAngleZ = this.magr.rotateAngleZ;
    }
}
