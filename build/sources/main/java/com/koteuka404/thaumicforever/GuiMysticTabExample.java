package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiMysticTabExample extends InventoryEffectRenderer {
    private static final ResourceLocation OVERLAY = new ResourceLocation("thaumicforever","textures/gui/baubles_container_1.png");
    private static final int SLOT_U = 177, SLOT_V = 0, SLOT_SIZE = 18;

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
        GlStateManager.color(1F,1F,1F,1F);

        int left = (width  - xSize) / 2;
        int top  = (height - ySize) / 2;
        mc.getTextureManager().bindTexture(OVERLAY);
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

        List<MysticBaubleSlots.SlotInfo> slots =
            MysticBaubleSlots.getAllSlots(mc.player);
        int vanillaCount = ContainerMysticTabExample.VANILLA_SLOT_COUNT;

        for (int i = 0; i < slots.size(); i++) {
            Slot slot = inventorySlots.getSlot(vanillaCount + i);
            if (!(slot instanceof SlotMystic)) continue;

            int x = left + slot.xPos;
            int y = top  + slot.yPos;
            drawTexturedModalRect(x-1, y-1, SLOT_U, SLOT_V, SLOT_SIZE, SLOT_SIZE);

            SlotMystic sm = (SlotMystic) slot;
            boolean unlocked = MysticBaubleSlots.isUnlocked(
                mc.player, sm.getCategoryKey()
            );

            if (unlocked && slot.getHasStack()) {
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem()
                  .renderItemAndEffectIntoGUI(slot.getStack(), x, y);
                RenderHelper.disableStandardItemLighting();
            }

            String iconName = unlocked
                ? sm.getType().toString().toLowerCase()
                : "unk";
            ResourceLocation icon = new ResourceLocation(
                "thaumicforever",
                "textures/gui/slots/" + iconName + ".png"
            );
            mc.getTextureManager().bindTexture(icon);
            GlStateManager.enableBlend();
            drawModalRectWithCustomSizedTexture(
                x-1, y-1,
                0, 0,
                SLOT_SIZE, SLOT_SIZE,
                SLOT_SIZE, SLOT_SIZE
            );
            GlStateManager.disableBlend();
        }

        int modelX = left + 51;
        int modelY = top  + 75;
        int scale  = 30;
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableColorMaterial();
        GuiInventory.drawEntityOnScreen(
            modelX, modelY, scale,
            (float)modelX - mouseXOffset,
            (float)modelY - 50F - mouseYOffset,
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
