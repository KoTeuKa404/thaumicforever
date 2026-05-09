package com.koteuka404.thaumicforever.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.koteuka404.thaumicforever.tile.TileGreatResearchTable;
import com.koteuka404.thaumicforever.container.GreatResearchTableContainer;
import com.koteuka404.thaumicforever.ThaumicForever;

public class GreatResearchTableGui extends DoubleTableGui {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/gui/gui_great.png");
    private static final ResourceLocation BLOCK_OVERLAY = new ResourceLocation(ThaumicForever.MODID, "textures/gui/block.png");
    private static final int WRITING_TOOL_U = 240;
    private static final int WRITING_TOOL_V = 26;
    private static final int WRITING_TOOL_SIZE = 16;
    private final IInventory greatTableInventory;

    public GreatResearchTableGui(InventoryPlayer playerInventory, IInventory tileEntity) {
        super(playerInventory, tileEntity, new GreatResearchTableContainer(playerInventory, tileEntity));
        this.greatTableInventory = tileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if (this.greatTableInventory.getStackInSlot(1).isEmpty()) {
            this.mc.getTextureManager().bindTexture(BLOCK_OVERLAY);
            drawModalRectWithCustomSizedTexture(this.guiLeft + 69, this.guiTop + 26, 0, 0, 115, 88, 115, 88);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int progress = Math.max(0, this.greatTableInventory.getField(0));
        int progressPercent = Math.min(100, progress * 100 / TileGreatResearchTable.BASE_SOLVE_TICKS);
        int efficiencyPercent = Math.max(0, this.greatTableInventory.getField(1));
        String text = progressPercent + "% / " + efficiencyPercent + "%";
        this.fontRenderer.drawString(text, 120 - this.fontRenderer.getStringWidth(text) / 2, 124, 0xD8C49A);
        drawWritingEffects(progress, efficiencyPercent);
    }

    private void drawWritingEffects(int progress, int efficiencyPercent) {
        ItemStack ink = this.greatTableInventory.getStackInSlot(0);
        ItemStack note = this.greatTableInventory.getStackInSlot(1);
        if (ink.isEmpty() || note.isEmpty() || progress <= 0 || efficiencyPercent <= 0) return;

        long time = MinecraftTime.get();
        float t = (time % 240L) / 240.0F;
        int paperX = 72;
        int paperY = 34;
        int paperW = 112;
        int paperH = 72;

        int x = paperX + 10 + (int)(t * (paperW - 26));
        int y = paperY + 18 + (int)(Math.sin(t * Math.PI * 4.0D) * 7.0D);
        if (((time / 240L) & 1L) == 1L) {
            x = paperX + paperW - 18 - (int)(t * (paperW - 26));
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(x + 8.0F, y + 8.0F, 80.0F);
        GL11.glRotatef((float)Math.sin(t * Math.PI * 2.0D) * 18.0F - 25.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-8.0F, -8.0F, 0.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        drawModalRectWithCustomSizedTexture(
            0,
            0,
            WRITING_TOOL_U,
            WRITING_TOOL_V,
            WRITING_TOOL_SIZE,
            WRITING_TOOL_SIZE,
            256,
            256
        );
        GL11.glPopMatrix();

        int seed = progress / 7;
        for (int i = 0; i < 5; i++) {
            int dotX = paperX + 18 + Math.abs(seed * 17 + i * 29) % (paperW - 36);
            int dotY = paperY + 22 + Math.abs(seed * 11 + i * 19) % (paperH - 34);
            int alpha = 35 + (int)(Math.sin((time + i * 13L) / 8.0D) * 18.0D);
            drawRect(dotX, dotY, dotX + 1, dotY + 1, (alpha << 24) | 0x2B1B10);
        }
    }

    private static class MinecraftTime {
        private static long get() {
            return net.minecraft.client.Minecraft.getSystemTime() / 50L;
        }
    }
}
