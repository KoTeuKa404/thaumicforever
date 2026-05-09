package com.koteuka404.thaumicforever.client.render;

import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import com.koteuka404.thaumicforever.ThaumicForever;

public class RenderVampireBat extends RenderBat {

    private static final ResourceLocation TEX = new ResourceLocation(
        ThaumicForever.MODID, "textures/entity/vampirebat.png"
    );

    public RenderVampireBat(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(net.minecraft.entity.passive.EntityBat entity) {
        return TEX;
    }
}

