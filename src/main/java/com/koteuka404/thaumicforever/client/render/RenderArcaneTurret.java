package com.koteuka404.thaumicforever.client.render;

import com.koteuka404.thaumicforever.client.model.ModelArcaneTurret;
import com.koteuka404.thaumicforever.entity.EntityArcaneTurret;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderArcaneTurret extends RenderLiving<EntityArcaneTurret> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/arcane_turret.png");

    public RenderArcaneTurret(RenderManager renderManager) {
        super(renderManager, new ModelArcaneTurret(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityArcaneTurret entity) {
        return TEXTURE;
    }
}
