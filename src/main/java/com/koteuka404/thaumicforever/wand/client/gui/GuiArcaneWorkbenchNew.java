package com.koteuka404.thaumicforever.wand.client.gui;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.wand.container.ContainerArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.crafting.ThaumicWandsCraftingManager;
import com.koteuka404.thaumicforever.wand.main.ThaumicWands;
import com.koteuka404.thaumicforever.wand.util.LocalizationHelper;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


public class GuiArcaneWorkbenchNew extends GuiContainer {

    private TileArcaneWorkbench tileEntity;
    private InventoryPlayer ip;
    ResourceLocation tex;
    private static final float PRIMAL_ICON_SCALE = 1.0f;

    public GuiArcaneWorkbenchNew(InventoryPlayer inventory, TileArcaneWorkbench e) {
        super(new ContainerArcaneWorkbenchNew(inventory, e));
        this.tex = new ResourceLocation(ThaumicWands.modID, "textures/gui/arcaneworkbench.png");
        this.tileEntity = e;
        this.ip = inventory;
        this.ySize = 234;
        this.xSize = 190;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.renderEngine.bindTexture(this.tex);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int cost = 0;
        int discount = 0;
        IArcaneRecipe result = ThaumicWandsCraftingManager.findMatchingArcaneRecipe(this.tileEntity.inventoryCraft, this.ip.player);
        if (result != null) {
            cost = result.getVis();
            cost = WandHelper.getActualVisCost(cost, getWand(), this.ip.player);
            discount = (int) (WandHelper.getTotalDiscount(getWand(), this.ip.player) * 100F);
        }
        drawPrimalAspects(var5, var6);
        GlStateManager.disableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate((var5 + 168), (var6 + 46), 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.0F);
        int charge = RechargeHelper.getCharge(getWand());
        String text = (charge < 0 ? "0" : charge) + " " + LocalizationHelper.localize("workbench.available");
        int ll = this.fontRenderer.getStringWidth(text) / 2;
        this.fontRenderer.drawString(text, -ll, 5, (Math.max(charge, 0) < cost) ? 15625838 : 7237358);
        GlStateManager.scale(1F, 1F, 1F);
        GlStateManager.popMatrix();

        if (cost > 0) {
            if (charge < cost) {
                GlStateManager.pushMatrix();
                GlStateManager.color(0.33F, 0.33F, 0.33F, 0.66F);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);
                this.itemRender.renderItemAndEffectIntoGUI(result.getCraftingResult((InventoryCrafting) this.tileEntity.inventoryCraft), var5 + 160, var6 + 64);
                this.itemRender.renderItemOverlayIntoGUI(this.mc.fontRenderer, result.getCraftingResult((InventoryCrafting) this.tileEntity.inventoryCraft), var5 + 160, var6 + 64, "");
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GL11.glPopMatrix();
                RenderHelper.disableStandardItemLighting();
            }
            GL11.glPushMatrix();
            GlStateManager.translate((var5 + 168), (var6 + 38), 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.0F);
            text = cost + " " + LocalizationHelper.localize("workbench.cost");
            if (discount > 0)
                text = text + " (" + discount + "% " + LocalizationHelper.localize("workbench.discount") + ")";
            ll = this.fontRenderer.getStringWidth(text) / 2;
            this.fontRenderer.drawString(text, -ll, 10, 12648447);
            GlStateManager.scale(1F, 1F, 1F);
            GlStateManager.popMatrix();
        }
    }

    private ItemStack getWand() {
        return this.tileEntity.inventoryCraft.getStackInSlot(15);
    }

    private void drawPrimalAspects(int guiLeft, int guiTop) {
        ItemStack wand = getWand();
        if (wand.isEmpty()) return;

        AspectList charge = WandHelper.getPrimalCharge(wand);
        for (ShardType st : ShardType.values()) {
            if (st.getMetadata() >= 6) continue;
            Aspect aspect = st.getAspect();
            int amount = charge.getAmount(aspect);
            int x = guiLeft + ContainerArcaneWorkbenchNew.xx[st.getMetadata()];
            int y = guiTop + ContainerArcaneWorkbenchNew.yy[st.getMetadata()];
            drawAspectIconWithCountScaled(x, y, aspect, amount, PRIMAL_ICON_SCALE);
        }
    }

    private void drawAspectIconWithCountScaled(int x, int y, Aspect aspect, int amount, float scale) {
        if (aspect == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1f);

        drawAspectIconTinted(0, 0, aspect);

        if (amount > 0) {
            String s = String.valueOf(amount);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 200);
            GlStateManager.scale(0.5f, 0.5f, 1f);

            int sx = (int) ((16 - 1) / 0.5f);
            int sy = (int) ((16 - 1) / 0.5f);
            fontRenderer.drawStringWithShadow(
                    s,
                    sx - fontRenderer.getStringWidth(s),
                    sy - fontRenderer.FONT_HEIGHT,
                    0xFFFFFF
            );
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    private void drawAspectIconTinted(int x, int y, Aspect aspect) {
        mc.getTextureManager().bindTexture(aspect.getImage());

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();

        int col = aspect.getColor();
        GlStateManager.color(
                ((col >> 16) & 255) / 255f,
                ((col >> 8) & 255) / 255f,
                (col & 255) / 255f,
                1f
        );

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(x, y + 16, zLevel).tex(0, 1).endVertex();
        buf.pos(x + 16, y + 16, zLevel).tex(1, 1).endVertex();
        buf.pos(x + 16, y, zLevel).tex(1, 0).endVertex();
        buf.pos(x, y, zLevel).tex(0, 0).endVertex();
        tes.draw();

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

}
