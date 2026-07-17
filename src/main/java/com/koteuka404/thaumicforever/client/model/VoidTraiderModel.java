package com.koteuka404.thaumicforever.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class VoidTraiderModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer Back;
    private final ModelRenderer Down_ground_r1;
    private final ModelRenderer Eye;
    private final ModelRenderer arch;
    private final ModelRenderer cube_r13_r1;
    private final ModelRenderer cube_r5_r1;
    private final ModelRenderer cube_r6_r1;
    private final ModelRenderer cube_r7_r1;
    private final ModelRenderer cube_r2_r1;
    private final ModelRenderer cube_r9_r1;
    private final ModelRenderer cube_r12_r1;
    private final ModelRenderer cube_r10_r1;
    private final ModelRenderer cube_r11_r1;
    private final ModelRenderer cube_r13_r2;
    private final ModelRenderer cube_r14_r1;
    private final ModelRenderer cube_r14_r2;
    private final ModelRenderer cube_r1_r1;
    private final ModelRenderer TableStone;
    private final ModelRenderer placeholder;
    private final ModelRenderer lamp;

    public VoidTraiderModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.root = new ModelRenderer(this);
        this.root.setRotationPoint(0F, 24F, 0F);

        this.Back = new ModelRenderer(this);
        this.Back.setRotationPoint(0F, 0F, 0F);
        this.Back.cubeList.add(new ModelBox(this.Back, 0, 0, -20F, -32F, 8F, 40, 32, 0, 0F, false));

        this.Down_ground_r1 = new ModelRenderer(this);
        this.Down_ground_r1.setRotationPoint(0F, -4F, 2F);
        setRotationAngle(this.Down_ground_r1, -1.5708F, 0F, 0F);
        this.Down_ground_r1.cubeList.add(new ModelBox(this.Down_ground_r1, 30, 57, -17F, -5F, 0F, 34, 10, 0, 0F, false));

        this.Eye = new ModelRenderer(this);
        this.Eye.setRotationPoint(0F, 0F, 0F);
        this.Eye.cubeList.add(new ModelBox(this.Eye, 50, 47, -6F, -15F, 6F, 4, 4, 0, 0F, false));
        this.Eye.cubeList.add(new ModelBox(this.Eye, 50, 51, 2F, -15F, 6F, 4, 4, 0, 0F, false));

        this.arch = new ModelRenderer(this);
        this.arch.setRotationPoint(0F, 0F, 0F);
        this.arch.cubeList.add(new ModelBox(this.arch, 100, 0, -20F, -4F, -2F, 4, 4, 10, 0F, false));
        this.arch.cubeList.add(new ModelBox(this.arch, 100, 0, 15.9696F, -8.3473F, -2F, 4, 4, 10, 0F, false));
        this.arch.cubeList.add(new ModelBox(this.arch, 100, 0, 16F, -4F, -2F, 4, 4, 10, 0F, false));

        this.cube_r13_r1 = new ModelRenderer(this);
        this.cube_r13_r1.setRotationPoint(16.5008F, -10.5027F, 3F);
        setRotationAngle(this.cube_r13_r1, 0F, 0F, -0.3491F);
        this.cube_r13_r1.mirror = true;
        this.cube_r13_r1.cubeList.add(new ModelBox(this.cube_r13_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, true));
        this.cube_r13_r1.mirror = false;

        this.cube_r5_r1 = new ModelRenderer(this);
        this.cube_r5_r1.setRotationPoint(14.6694F, -14.43F, 3F);
        setRotationAngle(this.cube_r5_r1, 0F, 0F, -0.5236F);
        this.cube_r5_r1.cubeList.add(new ModelBox(this.cube_r5_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r6_r1 = new ModelRenderer(this);
        this.cube_r6_r1.setRotationPoint(12.1838F, -17.9798F, 3F);
        setRotationAngle(this.cube_r6_r1, 0F, 0F, -0.6981F);
        this.cube_r6_r1.cubeList.add(new ModelBox(this.cube_r6_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r7_r1 = new ModelRenderer(this);
        this.cube_r7_r1.setRotationPoint(9.1197F, -21.0439F, 3F);
        setRotationAngle(this.cube_r7_r1, 0F, 0F, -0.8727F);
        this.cube_r7_r1.cubeList.add(new ModelBox(this.cube_r7_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r2_r1 = new ModelRenderer(this);
        this.cube_r2_r1.setRotationPoint(-5.57F, -23.5295F, 3F);
        setRotationAngle(this.cube_r2_r1, 0F, 0F, 1.0472F);
        this.cube_r2_r1.cubeList.add(new ModelBox(this.cube_r2_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r9_r1 = new ModelRenderer(this);
        this.cube_r9_r1.setRotationPoint(5.57F, -23.5295F, 3F);
        setRotationAngle(this.cube_r9_r1, 0F, 0F, -1.0472F);
        this.cube_r9_r1.cubeList.add(new ModelBox(this.cube_r9_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r12_r1 = new ModelRenderer(this);
        this.cube_r12_r1.setRotationPoint(-14.6694F, -14.43F, 3F);
        setRotationAngle(this.cube_r12_r1, 0F, 0F, 0.5236F);
        this.cube_r12_r1.cubeList.add(new ModelBox(this.cube_r12_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r10_r1 = new ModelRenderer(this);
        this.cube_r10_r1.setRotationPoint(-9.1197F, -21.0439F, 3F);
        setRotationAngle(this.cube_r10_r1, 0F, 0F, 0.8727F);
        this.cube_r10_r1.cubeList.add(new ModelBox(this.cube_r10_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r11_r1 = new ModelRenderer(this);
        this.cube_r11_r1.setRotationPoint(-12.1838F, -17.9798F, 3F);
        setRotationAngle(this.cube_r11_r1, 0F, 0F, 0.6981F);
        this.cube_r11_r1.cubeList.add(new ModelBox(this.cube_r11_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r13_r2 = new ModelRenderer(this);
        this.cube_r13_r2.setRotationPoint(-16.5008F, -10.5027F, 3F);
        setRotationAngle(this.cube_r13_r2, 0F, 0F, 0.3491F);
        this.cube_r13_r2.cubeList.add(new ModelBox(this.cube_r13_r2, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r14_r1 = new ModelRenderer(this);
        this.cube_r14_r1.setRotationPoint(17.6223F, -6.3169F, 3F);
        setRotationAngle(this.cube_r14_r1, 0F, 0F, -0.1745F);
        this.cube_r14_r1.mirror = true;
        this.cube_r14_r1.cubeList.add(new ModelBox(this.cube_r14_r1, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, true));
        this.cube_r14_r1.mirror = false;

        this.cube_r14_r2 = new ModelRenderer(this);
        this.cube_r14_r2.setRotationPoint(-17.6223F, -6.3169F, 3F);
        setRotationAngle(this.cube_r14_r2, 0F, 0F, 0.1745F);
        this.cube_r14_r2.cubeList.add(new ModelBox(this.cube_r14_r2, 100, 0, -2F, -2F, -5F, 4, 4, 10, 0F, false));

        this.cube_r1_r1 = new ModelRenderer(this);
        this.cube_r1_r1.setRotationPoint(5F, -22F, 0F);
        setRotationAngle(this.cube_r1_r1, 0F, 0F, -1.5708F);
        this.cube_r1_r1.cubeList.add(new ModelBox(this.cube_r1_r1, 100, 14, 0.8379F, -7.7974F, -2F, 4, 6, 10, 0F, false));

        this.TableStone = new ModelRenderer(this);
        this.TableStone.setRotationPoint(0F, 0F, 0F);
        this.TableStone.cubeList.add(new ModelBox(this.TableStone, 0, 32, -6.5F, -8F, -12F, 14, 8, 8, 0F, false));
        this.TableStone.cubeList.add(new ModelBox(this.TableStone, 44, 32, 4F, -6F, -10F, 14, 6, 8, 0F, false));
        this.TableStone.cubeList.add(new ModelBox(this.TableStone, 44, 32, -18F, -6F, -10F, 14, 6, 8, 0F, false));

        this.placeholder = new ModelRenderer(this);
        this.placeholder.setRotationPoint(0F, 0F, 0F);
        this.placeholder.cubeList.add(new ModelBox(this.placeholder, -4, 94, 2.5F, -8.25F, -10F, 4, 0, 4, 0F, false));
        this.placeholder.cubeList.add(new ModelBox(this.placeholder, -4, 90, -5.5F, -8.25F, -10F, 4, 0, 4, 0F, false));
        this.placeholder.cubeList.add(new ModelBox(this.placeholder, -4, 102, 12.6869F, -3.999F, -2.1475F, 4, 0, 4, 0F, false));
        this.placeholder.cubeList.add(new ModelBox(this.placeholder, -4, 98, 8.5F, -6.25F, -8F, 4, 0, 4, 0F, false));
        this.placeholder.cubeList.add(new ModelBox(this.placeholder, -4, 82, -16.6869F, -3.999F, -2.1475F, 4, 0, 4, 0F, false));
        this.placeholder.cubeList.add(new ModelBox(this.placeholder, -4, 86, -12.5F, -6.25F, -8F, 4, 0, 4, 0F, false));

        this.lamp = new ModelRenderer(this);
        this.lamp.setRotationPoint(0F, 0F, 0F);
        this.lamp.cubeList.add(new ModelBox(this.lamp, 0, 48, -13F, -9F, 1F, 6, 1, 6, 0F, false));
        this.lamp.cubeList.add(new ModelBox(this.lamp, 0, 48, -13F, -13F, 1F, 6, 1, 6, 0F, false));
        this.lamp.cubeList.add(new ModelBox(this.lamp, 24, 48, -13F, -17F, 4F, 6, 3, 0, 0F, false));
        this.lamp.cubeList.add(new ModelBox(this.lamp, 4, 55, -12F, -14F, 2F, 4, 5, 4, 0F, false));
        this.lamp.cubeList.add(new ModelBox(this.lamp, 8, 64, -11F, -12F, 3F, 2, 3, 2, 0F, false));
        this.lamp.cubeList.add(new ModelBox(this.lamp, 88, 34, -15.5F, -8F, -1F, 10, 8, 6, 0F, false));

        // Background planes, stone arch frame and lamp/table prop are kept in the file
        // for later editing, but are not rendered.
        // The old flat background is replaced by the renderer's translucent fog FX.
        // this.Back.addChild(this.Down_ground_r1);
        // Eyes are rendered separately as an emissive layer.
        // this.Back.addChild(this.arch);
        this.arch.addChild(this.cube_r13_r1);
        this.arch.addChild(this.cube_r5_r1);
        this.arch.addChild(this.cube_r6_r1);
        this.arch.addChild(this.cube_r7_r1);
        this.arch.addChild(this.cube_r2_r1);
        this.arch.addChild(this.cube_r9_r1);
        this.arch.addChild(this.cube_r12_r1);
        this.arch.addChild(this.cube_r10_r1);
        this.arch.addChild(this.cube_r11_r1);
        this.arch.addChild(this.cube_r13_r2);
        this.arch.addChild(this.cube_r14_r1);
        this.arch.addChild(this.cube_r14_r2);
        this.arch.addChild(this.cube_r1_r1);
        this.root.addChild(this.TableStone);
        this.root.addChild(this.placeholder);
        // this.root.addChild(this.lamp);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
    }

    private static void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
