package com.koteuka404.thaumicforever.client.render;

import com.koteuka404.thaumicforever.tile.TileVoidBeacon;

import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.api.aspects.Aspect;

public class RenderTileVoidBeacon extends TileEntitySpecialRenderer<TileVoidBeacon> {
    private static final float[] COLOR = colorComponents(Aspect.ELDRITCH.getColor());

    @Override
    public void render(TileVoidBeacon tile, double x, double y, double z, float partialTicks,
            int destroyStage, float alpha) {
        if (tile.getWorld() == null || !tile.isBeamActive()) return;
        int height = tile.getWorld().getHeight() - tile.getPos().getY();
        GlStateManager.alphaFunc(516, 0.1F);
        bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
        TileEntityBeaconRenderer.renderBeamSegment(x, y + 0.19D, z, partialTicks, 1.0D,
                tile.getWorld().getTotalWorldTime(), 0, height, COLOR, 0.2D, 0.25D);
    }

    @Override
    public boolean isGlobalRenderer(TileVoidBeacon tile) {
        return true;
    }

    private static float[] colorComponents(int color) {
        return new float[] {
                ((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F
        };
    }
}
