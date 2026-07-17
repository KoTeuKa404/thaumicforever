package com.koteuka404.thaumicforever.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/** Emissive eye geometry rendered as a separate layer. */
public class VoidTraiderEyesModel extends ModelBase {
    private final ModelRenderer eyes;

    public VoidTraiderEyesModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.eyes = new ModelRenderer(this);
        // Match VoidTraiderModel.root so the emissive eyes stay on the original face.
        this.eyes.setRotationPoint(0F, 24F, 0F);
        this.eyes.cubeList.add(new ModelBox(this.eyes, 50, 47, -6F, -15F, 6F, 4, 4, 0, 0F, false));
        this.eyes.cubeList.add(new ModelBox(this.eyes, 50, 51, 2F, -15F, 6F, 4, 4, 0, 0F, false));
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.eyes.render(scale);
    }
}
