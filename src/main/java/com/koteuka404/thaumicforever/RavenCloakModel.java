package com.koteuka404.thaumicforever;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class RavenCloakModel extends ModelBase {
    private final ModelRenderer cloac;
    private final ModelRenderer chest;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer head;

    public RavenCloakModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        // Main cloak part
        this.cloac = new ModelRenderer(this);
        this.cloac.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Chest part
        this.chest = new ModelRenderer(this, 62, 24);
        this.chest.addBox(-4, -20, -2, 8, 12, 4);
        this.cloac.addChild(this.chest);

        // Right wing part
        this.rightWing = new ModelRenderer(this, 49, 41);
        this.rightWing.addBox(-9, -22, -2, 13, 15, 0);
        this.cloac.addChild(this.rightWing);

        // Left wing part
        this.leftWing = new ModelRenderer(this, 48, 41);
        this.leftWing.addBox(-4, -22, -2, 13, 14, 0);
        this.cloac.addChild(this.leftWing);

        // Head part
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-6, -32, -6, 11, 11, 11);
        this.cloac.addChild(this.head);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.cloac.render(scale);
    }

    public void setRotation(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
