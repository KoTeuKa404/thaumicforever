package com.koteuka404.thaumicforever;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderWizardVillager extends RenderLiving<WizardVillager> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/wizard.png");

    public RenderWizardVillager(RenderManager renderManager) {
        super(renderManager, new WizardModel(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(WizardVillager entity) {
        return TEXTURE;
    }
}
