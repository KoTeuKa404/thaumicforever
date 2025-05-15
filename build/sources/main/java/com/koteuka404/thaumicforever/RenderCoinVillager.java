package com.koteuka404.thaumicforever;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderCoinVillager extends RenderLiving<CoinVillager> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/entity/moneychanger.png");

    public RenderCoinVillager(RenderManager renderManager) {
        super(renderManager, new CoinModel(), 0.5F); 
    }

    @Override
    protected ResourceLocation getEntityTexture(CoinVillager entity) {
        return TEXTURE;
    }

    public static final IRenderFactory<CoinVillager> FACTORY = new IRenderFactory<CoinVillager>() {
        @Override
        public RenderCoinVillager createRenderFor(RenderManager manager) {
            return new RenderCoinVillager(manager);
        }
    };
}
