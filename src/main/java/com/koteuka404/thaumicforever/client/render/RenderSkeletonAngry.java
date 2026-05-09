package com.koteuka404.thaumicforever.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import com.koteuka404.thaumicforever.client.model.SkeletonReviveModel;
import com.koteuka404.thaumicforever.entity.EntitySkeletonAngry;

public class RenderSkeletonAngry extends RenderLiving<EntitySkeletonAngry> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/skeleton.png");

    public RenderSkeletonAngry(RenderManager renderManager) {
        super(renderManager, new SkeletonReviveModel(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySkeletonAngry entity) {
        return TEXTURE;
    }
}
