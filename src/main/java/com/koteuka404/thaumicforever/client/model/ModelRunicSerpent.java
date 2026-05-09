package com.koteuka404.thaumicforever.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelRunicSerpent extends ModelBase {

    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer[] body = new ModelRenderer[10];
    private final ModelRenderer tail;

    public ModelRunicSerpent() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4.0F, -3.0F, -6.0F, 8, 6, 8);
        head.setRotationPoint(0.0F, 21.0F, -8.0F);

        jaw = new ModelRenderer(this, 0, 14);
        jaw.addBox(-3.0F, 0.0F, -6.5F, 6, 2, 6);
        jaw.setRotationPoint(0.0F, 2.0F, 0.0F);
        head.addChild(jaw);

        for (int i = 0; i < body.length; i++) {
            body[i] = new ModelRenderer(this, 0, 24);
            body[i].addBox(-3.0F, -2.5F, -3.0F, 6, 5, 6);
            body[i].setRotationPoint(0.0F, 21.5F, -2.0F + i * 5.0F);
        }

        tail = new ModelRenderer(this, 24, 24);
        tail.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 6);
        tail.setRotationPoint(0.0F, 21.5F, 48.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {

        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        head.render(scale);

        for (ModelRenderer segment : body) {
            segment.render(scale);
        }

        tail.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {

        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F;

        jaw.rotateAngleX = 0.12F + MathHelper.sin(ageInTicks * 0.25F) * 0.08F;

        float waveSpeed = 0.35F;
        float wavePower = 0.35F;

        for (int i = 0; i < body.length; i++) {
            float wave = MathHelper.sin(ageInTicks * waveSpeed + i * 0.75F) * wavePower;

            body[i].rotateAngleY = wave;
            body[i].rotationPointX = MathHelper.sin(ageInTicks * waveSpeed + i * 0.75F) * 2.0F;
        }

        tail.rotateAngleY = MathHelper.sin(ageInTicks * waveSpeed + body.length * 0.75F) * 0.45F;
        tail.rotationPointX = MathHelper.sin(ageInTicks * waveSpeed + body.length * 0.75F) * 2.0F;
    }
}
