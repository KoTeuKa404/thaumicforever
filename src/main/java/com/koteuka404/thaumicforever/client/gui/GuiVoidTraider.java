package com.koteuka404.thaumicforever.client.gui;

import com.koteuka404.thaumicforever.container.ContainerVoidTraider;
import com.koteuka404.thaumicforever.entity.EntityVoidTraider;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiVoidTraider extends GuiContainer {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation("thaumicforever", "textures/gui/gui_voidtraider.png");
    private static final ResourceLocation TRADE_TEXTURE =
        new ResourceLocation("thaumicforever", "textures/gui/gui_voidtraider1.png");
    // gui_voidtraider1 only contains the selected-trade layout. Reuse the
    // pedestal frame from the main trader texture for the selected item/price.
    private static final int STONE_SLOT_U = 32;
    private static final int STONE_SLOT_V = 40;
    private static final int SELECTED_ITEM_X = 80;
    private static final int SELECTED_ITEM_Y = 32;
    private static final int REQUIREMENT_Y = 76;
    private static final int STONE_SLOT_WIDTH = 18;
    private static final int STONE_SLOT_HEIGHT = 22;
    private int selectedSaleSlot = -1;

    public GuiVoidTraider(InventoryPlayer playerInventory, EntityVoidTraider voidTraider) {
        super(new ContainerVoidTraider(playerInventory, voidTraider));
        this.xSize = 176;
        this.ySize = 226;
        ((ContainerVoidTraider) this.inventorySlots).setRequirementSlotsVisible(false);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (this.selectedSaleSlot < 0) {
            return;
        }

        List<ItemStack> requirements = ((ContainerVoidTraider) this.inventorySlots)
            .getRequirements(this.selectedSaleSlot);
        int[] xPositions = { 56, 80, 104 };

        GlStateManager.enableBlend();
        for (int i = 0; i < requirements.size() && i < xPositions.length; i++) {
            Slot inputSlot = this.inventorySlots.getSlot(9 + i);
            if (!inputSlot.getHasStack()) {
                ItemStack preview = requirements.get(i);
                int itemY = REQUIREMENT_Y - 2;
                renderDarkPreview(preview, xPositions[i], itemY);
                this.itemRender.renderItemOverlayIntoGUI(
                    this.fontRenderer, preview, xPositions[i], itemY, null);
            }
        }
        GlStateManager.disableBlend();
        resetRenderState();
    }

    private void renderDarkPreview(ItemStack stack, int x, int y) {
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);

        // Keep the preview dark without using a stencil buffer. Stencil state
        // is shared with the whole Minecraft framebuffer and can black-screen
        // the next GUI frame when another renderer touches it.
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableDepth();
        drawRect(x, y, x + 16, y + 16, 0x66909090);
        resetRenderState();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int relativeX = mouseX - this.guiLeft;
        int relativeY = mouseY - this.guiTop;

        if (this.selectedSaleSlot >= 0) {
            if (isInside(relativeX, relativeY, SELECTED_ITEM_X, SELECTED_ITEM_Y)
                || isRequirementSlot(relativeX, relativeY)) {
                super.mouseClicked(mouseX, mouseY, mouseButton);
                return;
            }
            if (relativeY < 140) {
                clearSelectedTrade();
                return;
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        for (int slot = 0; slot < 9; slot++) {
            int slotX = 25 + (slot % 3) * 54;
            int slotY = 28 + (slot / 3) * 40;
            if (isInside(relativeX, relativeY, slotX, slotY)) {
                this.selectedSaleSlot = slot;
                showSelectedTrade();
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        resetRenderState();
        this.mc.getTextureManager().bindTexture(this.selectedSaleSlot < 0 ? TEXTURE : TRADE_TEXTURE);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(x, y, 8, 8, this.xSize, this.ySize);

        if (this.selectedSaleSlot >= 0) {
            this.mc.getTextureManager().bindTexture(TEXTURE);
            drawStoneSlot(x + SELECTED_ITEM_X - 1, y + SELECTED_ITEM_Y - 3);
            for (int requirement = 0; requirement < 3; requirement++) {
                drawStoneSlot(x + 56 + requirement * 24 - 1, y + REQUIREMENT_Y - 1);
            }
        }
    }

    private void drawStoneSlot(int x, int y) {
        this.drawTexturedModalRect(x, y, STONE_SLOT_U, STONE_SLOT_V,
            STONE_SLOT_WIDTH, STONE_SLOT_HEIGHT);
    }

    private void showSelectedTrade() {
        ((ContainerVoidTraider) this.inventorySlots).setRequirementSlotsVisible(true);
        for (int slot = 0; slot < 9; slot++) {
            Slot saleSlot = this.inventorySlots.getSlot(slot);
            if (slot == this.selectedSaleSlot) {
                saleSlot.xPos = SELECTED_ITEM_X;
                saleSlot.yPos = SELECTED_ITEM_Y;
            } else {
                saleSlot.xPos = -1000;
                saleSlot.yPos = -1000;
            }
        }
    }

    private void clearSelectedTrade() {
        this.selectedSaleSlot = -1;
        ((ContainerVoidTraider) this.inventorySlots).setRequirementSlotsVisible(false);
        for (int slot = 0; slot < 9; slot++) {
            Slot saleSlot = this.inventorySlots.getSlot(slot);
            saleSlot.xPos = 25 + (slot % 3) * 54;
            saleSlot.yPos = 28 + (slot / 3) * 40;
        }
    }

    private static boolean isInside(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private static boolean isRequirementSlot(int mouseX, int mouseY) {
        for (int i = 0; i < 3; i++) {
            if (isInside(mouseX, mouseY, 56 + i * 24, REQUIREMENT_Y)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        resetRenderState();
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        renderRequirementTooltip(mouseX, mouseY);
        resetRenderState();
    }

    private void renderRequirementTooltip(int mouseX, int mouseY) {
        if (this.selectedSaleSlot < 0) {
            return;
        }

        int relativeX = mouseX - this.guiLeft;
        int relativeY = mouseY - this.guiTop;
        List<ItemStack> requirements = ((ContainerVoidTraider) this.inventorySlots)
            .getRequirements(this.selectedSaleSlot);

        for (int i = 0; i < requirements.size() && i < 3; i++) {
            if (isInside(relativeX, relativeY, 56 + i * 24, REQUIREMENT_Y)
                && !this.inventorySlots.getSlot(9 + i).getHasStack()) {
                this.renderToolTip(requirements.get(i), mouseX, mouseY);
                return;
            }
        }
    }

    private static void resetRenderState() {
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
