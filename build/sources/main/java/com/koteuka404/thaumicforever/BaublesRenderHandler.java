package com.koteuka404.thaumicforever;

import baubles.api.BaublesApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BaublesRenderHandler {

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        
        ItemStack amuletStack = BaublesApi.getBaublesHandler(player).getStackInSlot(0);

        if (!amuletStack.isEmpty() && amuletStack.getItem() instanceof ItemZombieHeartAmulet) {
            renderAmuletOnPlayer(amuletStack, player, event);
        }
    }

    private void renderAmuletOnPlayer(ItemStack amuletStack, EntityPlayer player, RenderPlayerEvent.Post event) {
        GlStateManager.pushMatrix();

        event.getRenderer().getMainModel().bipedBody.postRender(0.0625F);

        GlStateManager.translate(0F, 0.7F, 0.3F);

        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(amuletStack, player.world, player);

        Minecraft.getMinecraft().getRenderItem().renderItem(amuletStack, model);

        GlStateManager.popMatrix();
    }
}
