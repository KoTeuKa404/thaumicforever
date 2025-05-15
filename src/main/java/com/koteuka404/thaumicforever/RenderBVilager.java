package com.koteuka404.thaumicforever;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderBVilager extends RenderLiving<EntityBVilager> {
    private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("thaumicforever", "textures/entity/zombie_villager.png");

    public RenderBVilager(RenderManager renderManager) {
        super(renderManager, new BVilager(), 0.5F); 
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBVilager entity) {
        return ZOMBIE_TEXTURES; 
    }

    @Override
    protected void preRenderCallback(EntityBVilager entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
    }
}
