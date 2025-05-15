package com.koteuka404.thaumicforever;

import java.util.List;

import baubles.api.BaubleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiMysticTabExample extends InventoryEffectRenderer {
    
    private static final ResourceLocation OVERLAY = new ResourceLocation("thaumicforever", "textures/gui/baubles_container_1.png");
    private static final int SLOT_U    = 177, SLOT_V = 0, SLOT_SIZE = 18;
    
    private float mouseXOffset;
    private float mouseYOffset;

    public GuiMysticTabExample(InventoryPlayer inv) {
        super(new ContainerMysticTabExample(inv));
        this.allowUserInput = false;
        this.xSize = 176; this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(
        float partialTicks, int mouseX, int mouseY
    ) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.disableLighting();
        GlStateManager.color(1,1,1,1);

        int left = (width - xSize) / 2, top = (height - ySize) / 2;
        mc.getTextureManager().bindTexture(OVERLAY);
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

        List<BaubleType> types = MysticBaubleSlots.getForPlayer(mc.player);
        int vanillaCount = ContainerMysticTabExample.VANILLA_SLOT_COUNT;
        for (int i = 0; i < types.size(); i++) {
            Slot s = inventorySlots.getSlot(vanillaCount + i);
            if (!(s instanceof SlotMystic)) continue;

            int x = left + s.xPos, y = top + s.yPos;
            drawTexturedModalRect(x - 1, y - 1, SLOT_U, SLOT_V, SLOT_SIZE, SLOT_SIZE);
            if (s.getHasStack()) {
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(s.getStack(), x, y);
                RenderHelper.disableStandardItemLighting();
            }
            SlotMystic sm = (SlotMystic) s;
            String typeName = sm.getType().toString().toLowerCase();
            ResourceLocation icon = new ResourceLocation(
                "thaumicforever", "textures/gui/slots/" + typeName + ".png"
            );
            mc.getTextureManager().bindTexture(icon);
            GlStateManager.enableBlend();
            drawModalRectWithCustomSizedTexture(
                x - 1, y - 1,
                0, 0,
                SLOT_SIZE, SLOT_SIZE,
                SLOT_SIZE, SLOT_SIZE
            );
            GlStateManager.disableBlend();
        }

        int modelX = left + 51;       
        int modelY = top + 75;
        int scale = 30;
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableColorMaterial();
        GuiInventory.drawEntityOnScreen(
            modelX, modelY, scale,
            (float)(modelX) - mouseXOffset,
            (float)(modelY) - 50f - mouseYOffset,
            mc.player
        );
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseXOffset = mouseX;
        this.mouseYOffset = mouseY;

        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
}
