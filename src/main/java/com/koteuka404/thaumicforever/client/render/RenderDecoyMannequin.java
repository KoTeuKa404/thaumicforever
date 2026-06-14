package com.koteuka404.thaumicforever.client.render;

import com.koteuka404.thaumicforever.client.model.ModelMannequinDecoy;
import com.koteuka404.thaumicforever.entity.EntityDecoyMannequin;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderDecoyMannequin extends RenderLiving<EntityDecoyMannequin> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever:textures/entity/decoy_mannequin.png");

    public RenderDecoyMannequin(RenderManager renderManager) {
        super(renderManager, new ModelMannequinDecoy(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDecoyMannequin entity) {
        return TEXTURE;
    }
}
