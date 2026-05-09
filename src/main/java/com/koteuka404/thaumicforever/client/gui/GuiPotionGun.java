package com.koteuka404.thaumicforever.client.gui;

import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.util.text.TextFormatting;
import com.koteuka404.thaumicforever.container.ContainerPotionGun;
import com.koteuka404.thaumicforever.inventory.InventoryPotionGun;
import com.koteuka404.thaumicforever.item.ItemPotionGun;
import com.koteuka404.thaumicforever.ThaumicForever;

public class GuiPotionGun extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/gui/gui_potion_gun.png");
    private static final int GUI_OFFSET_X = 0;
    private static final int GUI_OFFSET_Y = -10;
    private static final int TANK_X = 82;
    private static final int TANK_Y = 30;
    private static final int TANK_W = 22;
    private static final int TANK_H = 84;
    private static final int TANK_U = 206;
    private static final int TANK_V = 30;
    private static final int FILL_X = TANK_X + 2;
    private static final int FILL_Y = TANK_Y + 6;
    private static final int FILL_W = 8;
    private static final int FILL_H = 72;
    private static final int FILL_U = 232;
    private final IInventory gunInventory;

    public GuiPotionGun(InventoryPlayer playerInventory, IInventory gunInventory) {
        super(new ContainerPotionGun(playerInventory, gunInventory, playerInventory.player));
        this.gunInventory = gunInventory;
        this.xSize = 198;
        this.ySize = 230;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft += GUI_OFFSET_X;
        this.guiTop += GUI_OFFSET_Y;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // no labels; texture-only UI
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if (this.gunInventory instanceof InventoryPotionGun) {
            ItemStack gun = ((InventoryPotionGun) this.gunInventory).getGunStack();
            int fluid = ItemPotionGun.getFluidAmount(gun);
            int cap = ItemPotionGun.getFluidCapacity(gun);
            if (cap > 0) {
                int barX = this.guiLeft + TANK_X;
                int barY = this.guiTop + TANK_Y;

                // 10-step gauge: every 1000 mB adds exactly 1/10 of the bar.
                int steps = fluid <= 0 ? 0 : Math.max(1, Math.min(10, (fluid + 999) / 1000));
                int fill = (int) Math.floor((steps / 10.0D) * FILL_H);
                if (fill > 0) {
                    int rgb = getTankColor(gun);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    int fy = this.guiTop + FILL_Y + (FILL_H - fill);
                    int ticks = this.mc.player == null ? 0 : this.mc.player.ticksExisted;
                    int scroll = ticks % 256;

                    GL11.glColor4f(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
                    this.drawTexturedModalRect(this.guiLeft + FILL_X, fy, FILL_U, scroll, FILL_W, fill);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                // Use the textured flask/strip from GUI sheet on top.
                this.drawTexturedModalRect(barX, barY, TANK_U, TANK_V, TANK_W, TANK_H);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.gunInventory instanceof InventoryPotionGun) {
            ItemStack gun = ((InventoryPotionGun) this.gunInventory).getGunStack();
            int fluid = ItemPotionGun.getFluidAmount(gun);
            int cap = ItemPotionGun.getFluidCapacity(gun);
            int barX = this.guiLeft + FILL_X;
            int barY = this.guiTop + FILL_Y;
            int barW = FILL_W;
            int barH = FILL_H;
            if (mouseX >= barX && mouseX < barX + barW && mouseY >= barY && mouseY < barY + barH) {
                ItemStack template = ItemPotionGun.getPotionTemplate(gun);
                int steps = fluid <= 0 ? 0 : Math.max(1, Math.min(10, (fluid + 999) / 1000));
                String line = TextFormatting.AQUA.toString() + fluid + " / " + cap + " mB "
                        + TextFormatting.GRAY + "[" + steps + "/10]";
                if (!template.isEmpty()) {
                    line = line + TextFormatting.GRAY + " (" + template.getDisplayName() + ")";
                }
                this.drawHoveringText(Collections.singletonList(line), mouseX, mouseY);
            }
        }
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    private static int getTankColor(ItemStack gun) {
        ItemStack template = ItemPotionGun.getPotionTemplate(gun);
        if (template.isEmpty()) return 0xAA44FF;

        ResourceLocation id = template.getItem().getRegistryName();
        if (id != null) {
            String key = id.toString();
            if ("thaumcraft:bottle_taint".equals(key)) return 0xAA22CC;
            if ("thaumicforever:purifying_bottle".equals(key)) return 0x99FFF0;
            if ("thaumicforever:vis_bottle".equals(key)) return 0x88CCFF;
        }
        FluidStack fs = FluidUtil.getFluidContained(template);
        if (fs != null && fs.getFluid() != null && "liquid_death".equals(fs.getFluid().getName())) {
            return 0x4B1167;
        }

        List<PotionEffect> effects = PotionUtils.getEffectsFromStack(template);
        if (effects == null || effects.isEmpty()) return 0xAA44FF;

        int r = 0;
        int g = 0;
        int b = 0;
        int c = 0;
        for (PotionEffect effect : effects) {
            if (effect == null || effect.getPotion() == null) continue;
            int col = effect.getPotion().getLiquidColor();
            r += (col >> 16) & 0xFF;
            g += (col >> 8) & 0xFF;
            b += col & 0xFF;
            c++;
        }
        if (c <= 0) return 0xAA44FF;
        return ((r / c) << 16) | ((g / c) << 8) | (b / c);
    }
}
