package com.koteuka404.thaumicforever.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelVoidTraiderTentacle extends ModelBase {
    private static final int SEGMENT_COUNT = 9;
    private static final int[] SEGMENT_SIZES = {6, 6, 5, 5, 4, 4, 4, 3, 3};

    private final ModelRenderer root;
    private final ModelRenderer[] segments = new ModelRenderer[SEGMENT_COUNT];

    public ModelVoidTraiderTentacle() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.root = new ModelRenderer(this, 0, 0);
        this.root.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
        this.root.setRotationPoint(0.0F, 0.0F, 0.0F);

        ModelRenderer parent = this.root;
        int previousSize = 6;
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            int size = SEGMENT_SIZES[i];
            float halfSize = size / 2.0F;
            ModelRenderer segment = new ModelRenderer(this, 0, 16);
            segment.addBox(-halfSize, -halfSize, -halfSize, size, size, size);
            segment.setRotationPoint(0.0F, -((previousSize + size) / 2.0F), 0.0F);

            parent.addChild(segment);
            this.segments[i] = segment;
            parent = segment;
            previousSize = size;
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.root.rotateAngleX = 0.0F;
        this.root.rotateAngleY = 0.0F;
        this.root.rotateAngleZ = 0.0F;

        for (int i = 0; i < SEGMENT_COUNT; i++) {
            float progress = i / (float) SEGMENT_COUNT;
            float wave = MathHelper.sin(ageInTicks * 0.08F + i * 0.45F) * 0.08F;
            ModelRenderer segment = this.segments[i];

            segment.rotateAngleX = 0.05F + progress * 0.08F + wave;
            segment.rotateAngleY = MathHelper.sin(ageInTicks * 0.04F + i * 0.30F) * 0.03F;
            segment.rotateAngleZ = -0.05F + progress * 0.10F + wave * 0.6F;
        }
    }
}
