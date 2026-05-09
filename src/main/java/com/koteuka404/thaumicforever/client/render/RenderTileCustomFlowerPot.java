package com.koteuka404.thaumicforever.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.tile.TileCustomFlowerPot;

@SideOnly(Side.CLIENT)
public class RenderTileCustomFlowerPot extends TileEntitySpecialRenderer<TileCustomFlowerPot> {
    @Override
    public void render(TileCustomFlowerPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = te.getPlant();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlock)) {
            return;
        }

        Block plantBlock = ((ItemBlock) stack.getItem()).getBlock();
        IBlockState state = plantBlock.getStateFromMeta(stack.getMetadata());

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.33D, z + 0.5D);
        GlStateManager.scale(0.62F, 0.62F, 0.62F);
        GlStateManager.translate(-0.5D, 0.0D, -0.5D);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, 1.0F);

        GlStateManager.popMatrix();
    }
}
