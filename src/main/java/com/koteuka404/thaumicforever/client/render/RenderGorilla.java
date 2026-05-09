package com.koteuka404.thaumicforever.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import com.koteuka404.thaumicforever.entity.EntityGorilla;
import com.koteuka404.thaumicforever.entity.Monkey;

public class RenderGorilla extends RenderLiving<EntityGorilla> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation("thaumicforever", "textures/entity/gorilla.png");

    public RenderGorilla(RenderManager renderManager) {
        super(renderManager, new Monkey(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGorilla entity) {
        return TEXTURE;
    }
}
