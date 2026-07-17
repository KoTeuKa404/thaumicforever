package com.koteuka404.thaumicforever.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.container.ContainerArcaneTurret;
import com.koteuka404.thaumicforever.entity.EntityArcaneTurret;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiArcaneTurret extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thaumicforever", "textures/gui/gui_arcaneturret.png");
    private final EntityArcaneTurret turret;

    public GuiArcaneTurret(EntityArcaneTurret turret) {
        super(new ContainerArcaneTurret(turret));
        this.turret = turret;
        this.xSize = 176;
        this.ySize = 166;
    }

    public GuiArcaneTurret(EntityPlayer player, EntityArcaneTurret turret) {
        super(new ContainerArcaneTurret(player, turret));
        this.turret = turret;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new ArcaneToggleButton(1, this.guiLeft + 90, this.guiTop + 13, "gui.thaumicforever.arcane_turret.animals", () -> this.turret.getTargetAnimal()));
        this.buttonList.add(new ArcaneToggleButton(2, this.guiLeft + 90, this.guiTop + 27, "gui.thaumicforever.arcane_turret.mobs", () -> this.turret.getTargetMob()));
        this.buttonList.add(new ArcaneToggleButton(3, this.guiLeft + 90, this.guiTop + 41, "gui.thaumicforever.arcane_turret.players", () -> this.turret.getTargetPlayer()));
        this.buttonList.add(new ArcaneToggleButton(4, this.guiLeft + 90, this.guiTop + 55, "gui.thaumicforever.arcane_turret.friendly", () -> this.turret.getTargetFriendly()));
        this.buttonList.add(new ArcaneToggleButton(5, this.guiLeft + 90, this.guiTop + 69, "gui.thaumicforever.arcane_turret.forward_fire", () -> this.turret.getForwardFire()));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        int health = (int)(39.0F * (this.turret.getHealth() / this.turret.getMaxHealth()));
        this.drawTexturedModalRect(x + 30, y + 59, 192, 48, health, 6);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 1 && button.id <= 5) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
            toggleClientFlag(button.id);
            return;
        }
        super.actionPerformed(button);
    }

    private void toggleClientFlag(int id) {
        if (id == 1) this.turret.setTargetAnimal(!this.turret.getTargetAnimal());
        if (id == 2) this.turret.setTargetMob(!this.turret.getTargetMob());
        if (id == 3) this.turret.setTargetPlayer(!this.turret.getTargetPlayer());
        if (id == 4) this.turret.setTargetFriendly(!this.turret.getTargetFriendly());
        if (id == 5) this.turret.setForwardFire(!this.turret.getForwardFire());
    }

    private static final class ArcaneToggleButton extends GuiButton {
        private final ToggleState state;

        private ArcaneToggleButton(int id, int x, int y, String labelKey, ToggleState state) {
            super(id, x, y, 8, 8, labelKey);
            this.state = state;
        }

        @Override
        public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }

            mc.getTextureManager().bindTexture(GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.x, this.y, 192, 16, 8, 8);
            if (this.state.isEnabled()) {
                this.drawTexturedModalRect(this.x, this.y, 192, 24, 8, 8);
            }
            this.drawString(mc.fontRenderer, I18n.format(this.displayString), this.x + 12, this.y, 0xFFFFFF);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    private interface ToggleState {
        boolean isEnabled();
    }
}
