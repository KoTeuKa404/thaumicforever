package com.koteuka404.thaumicforever;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class RavenCloakRender implements IRenderBauble {
    private final RavenCloakModel model;

    public RavenCloakRender() {
        this.model = new RavenCloakModel();
    }

    @Override
public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer player, RenderType renderType, float partialTicks) {
    if (renderType == RenderType.BODY) {
        System.out.println("Rendering cloak on player: " + player.getName());
        GlStateManager.pushMatrix();

        GlStateManager.translate(0.0F, 1.5F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        this.model.render(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
    }
}


}
