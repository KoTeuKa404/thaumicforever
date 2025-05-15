package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GorillaHandTileEntityItemStackRenderer extends TileEntityItemStackRenderer {
    
    private final ModelGorillaHand model = new ModelGorillaHand();
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever", "textures/items/hand.png");
    
    @Override
    public void renderByItem(ItemStack itemStack) {
        GlStateManager.pushMatrix();
    
        GlStateManager.translate(0.5F, 1.15F, 0.5F);
        
        GlStateManager.rotate(180F, 1.0F, 0.0F, 0.0F);
        
        GlStateManager.rotate(15F, 0.0F, 1.0F, 0.0F);
        
        GlStateManager.scale(0.65F, 0.65F, 0.65F);
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        
        model.render(null, 0, 0, 0, 0, 0, 0.0625F);
        
        GlStateManager.popMatrix();
    }
    


}
