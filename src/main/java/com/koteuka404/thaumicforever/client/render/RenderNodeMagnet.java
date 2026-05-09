package com.koteuka404.thaumicforever.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import com.koteuka404.thaumicforever.client.model.ModelNodeMagnet;
import com.koteuka404.thaumicforever.entity.EntityNodeMagnet;
public class RenderNodeMagnet extends RenderLiving<EntityNodeMagnet> {
    private static final ResourceLocation rl = new ResourceLocation("thaumicforever", "textures/entity/node_magnet.png");

    public RenderNodeMagnet(RenderManager rm) {
        super(rm, new ModelNodeMagnet(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityNodeMagnet entity) {
        return rl;
    }
}
