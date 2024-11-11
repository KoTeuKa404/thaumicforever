package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;

public class RenderInvisibleSnowball extends RenderSnowball<EntityTimeFreezeProjectile> {

    public RenderInvisibleSnowball(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn) {
        super(renderManagerIn, itemIn, itemRendererIn);
    }

    @Override
    public void doRender(EntityTimeFreezeProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Ось приклад логіки для рендеру частинок
        // Можеш додати сюди логіку рендера сфери, але не рендерити текстуру сніжка.
        if (entity != null) {
            // Приклад: Рендеримо частинки чи ефекти навколо сфери
            Minecraft.getMinecraft().effectRenderer.addEffect(
                new Particle(Minecraft.getMinecraft().world, x, y, z, 0.1F, 0.1F, 0.1F));
        }
    }
}
