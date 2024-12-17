package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;

public class DoubleTableGui extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/gui/guiresearchtable.png");
    private final IInventory tileEntity;
    private long lastClickedTime = 0;
    private boolean shouldLogAspects = false; 

    public DoubleTableGui(InventoryPlayer playerInventory, IInventory tileEntity) {
        super(new DoubleTableContainer(playerInventory, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = 240;
        this.ySize = 240;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        int lupaX = this.guiLeft + 85;
        int lupaY = this.guiTop + 40;
        if (System.currentTimeMillis() - lastClickedTime < 1000) {
            drawTexturedModalRect(lupaX, lupaY, 241, 16, 16, 16); 
        } else {
            drawTexturedModalRect(lupaX, lupaY, 241, 0, 16, 16);
        }

        if (tileEntity instanceof DoubleTableTileEntity) {
            DoubleTableTileEntity doubleTable = (DoubleTableTileEntity) tileEntity;

            AspectList storedAspects = doubleTable.getStoredAspects();
            if (storedAspects != null) {
                renderSidebarAspects(storedAspects);
            }

            ItemStack scroll = doubleTable.getStackInSlot(5);
            if (!scroll.isEmpty() && scroll.hasTagCompound()) {
                NBTTagCompound tag = scroll.getTagCompound();
                if (tag != null && tag.hasKey("aspects")) {
                    NBTTagCompound aspectTag = tag.getCompoundTag("aspects");
                    AspectList scrollAspects = new AspectList();
                    for (String aspectKey : aspectTag.getKeySet()) {
                        Aspect aspect = Aspect.getAspect(aspectKey);
                        if (aspect != null) {
                            scrollAspects.add(aspect, aspectTag.getInteger(aspectKey));
                        }
                    }
                    renderCenterAspects(scrollAspects);
                }
            }
        }
    }

    private void renderSidebarAspects(AspectList storedAspects) {
        Aspect[] aspects = storedAspects.getAspects();
    
        int x = this.guiLeft + 45; 
        int y = this.guiTop + 32; 
        for (int i = 0; i < aspects.length; i++) {
            if (aspects[i] != null) {
                try {
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glPushMatrix();
    
                    UtilsFX.drawTag(x, y, aspects[i], storedAspects.getAmount(aspects[i]), 0, zLevel, 771, 1.0F);
    
                    GL11.glPopMatrix();
                    GL11.glPopAttrib();
    
                    y += 16;
    
                    if ((i + 1) % 5 == 0) {
                        x += 15; 
                        y = this.guiTop + 32; 
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    

    private void renderCenterAspects(AspectList scrollAspects) {
        Aspect[] aspects = scrollAspects.getAspects();

        int centerX = this.guiLeft + 170;
        int centerY = this.guiTop + 75;
        int radius = 30; 

        for (int i = 0; i < aspects.length; i++) {
            Aspect aspect = aspects[i];
            int amount = scrollAspects.getAmount(aspect);

            double angle = 2 * Math.PI * i / aspects.length;
            int x = centerX + (int) (radius * Math.cos(angle)) - 8;
            int y = centerY + (int) (radius * Math.sin(angle)) - 8;

            if (amount == 7) {
             
                continue;
            } else if (amount == 6 || amount == 5) {
              
                ResourceLocation texture = new ResourceLocation("thaumicforever", "textures/gui/unk.png");
                mc.getTextureManager().bindTexture(texture);

                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                UtilsFX.drawTexturedQuadF(x, y, 0, 0, 16, 16, zLevel);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            } else if (amount < 5) {
                UtilsFX.drawTag(x, y, aspect, amount, 0, zLevel, 771, 1.0F);
            }

            radius += 5;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception e) {
        }

        int lupaX = this.guiLeft + 85;
        int lupaY = this.guiTop + 40;
        int lupaWidth = 16;
        int lupaHeight = 16;

        if (mouseX >= lupaX && mouseX <= lupaX + lupaWidth && mouseY >= lupaY && mouseY <= lupaY + lupaHeight) {
            if (mouseButton == 0) { 

                lastClickedTime = System.currentTimeMillis();

                if (this.tileEntity instanceof DoubleTableTileEntity) {
                    BlockPos pos = ((DoubleTableTileEntity) this.tileEntity).getPos();
                    ThaumicForever.network.sendToServer(new PacketClickLupa(pos));
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
