package com.koteuka404.thaumicforever;


import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class WatcherRenderer extends RenderLiving<WatcherEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/watcher.png");

    public WatcherRenderer(RenderManager renderManager) {
        super(renderManager, new ModelGuardian(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(WatcherEntity entity) {
        return TEXTURE;
    }
}
