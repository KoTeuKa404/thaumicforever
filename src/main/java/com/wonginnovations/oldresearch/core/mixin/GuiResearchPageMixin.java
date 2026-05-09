package com.wonginnovations.oldresearch.core.mixin;

import com.wonginnovations.oldresearch.OldResearch;
import com.wonginnovations.oldresearch.common.lib.network.PacketHandler;
import com.wonginnovations.oldresearch.common.lib.network.PacketGivePlayerNoteToServer;
import com.wonginnovations.oldresearch.common.lib.research.OldResearchManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.*;
import thaumcraft.client.gui.GuiResearchPage;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Locale;

@Mixin(value = GuiResearchPage.class, remap = false)
public abstract class GuiResearchPageMixin extends GuiScreen {

    @Shadow(remap = false)
    protected int paneHeight;
    @Shadow(remap = false)
    int hrx;
    @Shadow(remap = false)
    int hry;
    @Shadow(remap = false)
    ResourceLocation tex1;
    @Shadow(remap = false)
    ResourceLocation dummyResearch;
    @Shadow(remap = false)
    ResourceLocation dummyMap;
    @Shadow(remap = false)
    ResourceLocation dummyFlask;
    @Shadow(remap = false)
    ResourceLocation dummyChest;
    @Shadow(remap = false)
    boolean[] hasResearch;
    @Shadow(remap = false)
    boolean[] hasItem;
    @Shadow(remap = false)
    boolean[] hasCraft;
    @Shadow(remap = false)
    boolean hasAllRequisites;
    @Shadow(remap = false)
    boolean hold;
    @Shadow(remap = false)
    AspectList knownPlayerAspects;
    @Shadow(remap = false)
    private int maxAspectPages;
    @Shadow(remap = false)
    private static int aspectsPage;
    @Shadow(remap = false)
    ResourceLocation tex3;
    @Shadow(remap = false)
    static ResourceLocation shownRecipe;
    @Shadow(remap = false)
    boolean allowWithPagePopup;

    @Shadow(remap = false)
    abstract boolean mouseInside(int x, int y, int w, int h, int mx, int my);
    @Shadow(remap = false)
    abstract void drawPopupAt(int x, int y, int mx, int my, String text);
    @Shadow(remap = false)
    abstract void drawStackAt(ItemStack itemstack, int x, int y, int mx, int my, boolean clickthrough);
    @Shadow(remap = false)
    public abstract void drawTexturedModalRectScaled(int par1, int par2, int par3, int par4, int par5, int par6, float scale);

    @Shadow(remap = false)
    boolean isComplete;

    @Shadow(remap = false)
    IPlayerKnowledge playerKnowledge;
    @Shadow(remap = false)
    List tipText;
    @Unique
    private final Map<Point, ItemStack> oldresearch$renderedNotes = new HashMap<>();

    @Unique
    private void oldresearch$drawPopupAt(int x, int y, int mx, int my, String... text) {
        if ((shownRecipe == null || this.allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16) {
            ArrayList<String> s = new ArrayList<>();
            for (String t : text)
                s.add(I18n.format(t));
            this.tipText = s;
        }
    }

    @Unique
    private String oldresearch$localizeResearchTitle(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }

        String stripped = OldResearchManager.getStrippedKey(key);
        if (stripped == null || stripped.isEmpty()) {
            return key;
        }

        // 1) direct key used by most JSON entries
        String[] candidates = new String[] {
                "research." + stripped + ".title",
                stripped.startsWith("PA_") ? "research." + stripped.substring(3) + ".title" : "",
                stripped.startsWith("TF_") ? "research." + stripped.substring(3) + ".title" : "",
                stripped
        };

        for (String candidate : candidates) {
            if (candidate == null || candidate.isEmpty()) {
                continue;
            }
            String translated = I18n.format(candidate);
            if (!translated.equals(candidate)) {
                return translated;
            }
        }

        // 2) try research registry localized name
        ResearchEntry re = ResearchCategories.getResearch(stripped);
        if (re != null) {
            String localized = re.getLocalizedName();
            if (localized != null
                    && !localized.isEmpty()
                    && !localized.startsWith("research.")
                    && !localized.endsWith(".title")) {
                return localized;
            }
        }

        // 3) human-readable fallback instead of raw localization key
        String human = stripped
                .replace("PA_", "")
                .replace("TF_", "")
                .replace('_', ' ')
                .toLowerCase(Locale.ROOT);
        if (human.isEmpty()) {
            return stripped;
        }
        return Character.toUpperCase(human.charAt(0)) + human.substring(1);
    }

    @Inject(method = "drawRequirements", at = @At("RETURN"), remap = false)
    public void drawRequirementsPost(int x, int mx, int my, ResearchStage stage, CallbackInfo ci) {
        oldresearch$renderedNotes.clear();
        if (stage.getResearch() == null || stage.getResearch().length == 0) {
            return;
        }

        int y = (this.height - this.paneHeight) / 2 - 16 + 210 - 18;
        int ss = 18;
        if (stage.getResearch().length > 6) {
            ss = 110 / stage.getResearch().length;
        }

        int shift = 24;
        for (int a = 0; a < stage.getResearch().length; ++a) {
            String key = stage.getResearch()[a];
            if (!key.startsWith("rn_")) {
                shift += ss;
                continue;
            }

            ItemStack note = OldResearchManager.getNote(key);
            if (note != null) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableColorMaterial();
                GlStateManager.enableLighting();
                oldresearch$renderedNotes.put(new Point(x - 15 + shift, y), note);
                this.itemRender.renderItemAndEffectIntoGUI(InventoryUtils.cycleItemStack(note), x - 15 + shift, y);
                GlStateManager.disableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();

                if (this.hasResearch != null && a < this.hasResearch.length && this.hasResearch[a]) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.renderEngine.bindTexture(this.tex1);
                    GlStateManager.disableDepth();
                    this.drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                    GlStateManager.enableDepth();
                }

                String title = oldresearch$localizeResearchTitle(key);
                String noteText = I18n.format("tc.researchtheory", title);
                this.oldresearch$drawPopupAt(x - 15 + shift, y, mx, my, noteText, "researchnote.click");
            }

            shift += ss;
        }
    }

    @Redirect(
        method = "parsePages",
        at = @At(value = "INVOKE", target = "Lthaumcraft/api/research/ResearchStage;getKnow()[Lthaumcraft/api/research/ResearchStage$Knowledge;"),
        remap = false
    )
    public ResearchStage.Knowledge[] parsePagesGetKnow(ResearchStage instance) {
        boolean hasCraft = instance.getCraft() != null && instance.getCraft().length > 0;
        boolean hasObtain = instance.getObtain() != null && instance.getObtain().length > 0;
        boolean hasResearch = instance.getResearch() != null && instance.getResearch().length > 0;
        ResearchStage.Knowledge[] know = instance.getKnow();
        boolean hasKnowledge = know != null && know.length > 0;

        if (!hasCraft && !hasObtain && !hasResearch && !hasKnowledge) {
            return new ResearchStage.Knowledge[0];
        }
        return know;
    }

    @Unique
    private boolean oldresearch$handleNoteClick(int mx, int my) {
        for (Point p : oldresearch$renderedNotes.keySet()) {
            if ((mx >= p.x && mx <= p.x + 16) && (my >= p.y && my <= p.y + 16)) {
                ItemStack note = oldresearch$renderedNotes.get(p);
                if (!note.isEmpty() && OldResearchManager.getData(note) != null) {
                    String key = OldResearchManager.getData(note).key;
                    OldResearch.LOGGER.info("Research note click: key={} mx={} my={}", key, mx, my);
                    PacketHandler.INSTANCE.sendToServer(new PacketGivePlayerNoteToServer(key));
                } else {
                    OldResearch.LOGGER.warn("Research note click with missing data: mx={} my={}", mx, my);
                }
                return true;
            }
        }
        return false;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    public void mouseClickedInjection(int mx, int my, int button, CallbackInfo ci) {
        if (oldresearch$handleNoteClick(mx, my)) {
            ci.cancel();
        }
    }

    @Inject(method = "func_73864_a", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    public void mouseClickedInjectionObf(int mx, int my, int button, CallbackInfo ci) {
        if (oldresearch$handleNoteClick(mx, my)) {
            ci.cancel();
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void ctorInjection(ResearchEntry research, ResourceLocation recipe, double x, double y, CallbackInfo ci) {
        this.knownPlayerAspects = new AspectList();

        for (Aspect a : OldResearch.proxy.getPlayerKnowledge().getAspectsDiscovered(this.mc.player.getGameProfile().getName()).getAspects()) {
            this.knownPlayerAspects.add(a, OldResearchManager.getAspectComplexity(a));
        }

        this.maxAspectPages = this.knownPlayerAspects != null ? MathHelper.ceil((float)this.knownPlayerAspects.size() / 5.0F) : 0;
    }

    @Inject(method = "drawAspectPage", at = @At("HEAD"), cancellable = true, remap = false)
    public void drawAspectPageInjection(int x, int y, int mx, int my, CallbackInfo ci) {
        if (this.knownPlayerAspects != null && this.knownPlayerAspects.size() > 0) {
            GlStateManager.pushMatrix();
            int count = -1;
            int start = aspectsPage * 5;
            Aspect[] var9 = this.knownPlayerAspects.getAspectsSortedByAmount();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                Aspect aspect = var9[var11];
                ++count;
                if (count >= start) {
                    if (count > start + 4) {
                        break;
                    }

                    if (aspect.getImage() != null) {
                        int ty = y + count % 5 * 40;
                        if (mx >= x && my >= ty && mx < x + 40 && my < ty + 40) {
                            this.mc.renderEngine.bindTexture(this.tex3);
                            GlStateManager.pushMatrix();
                            GlStateManager.enableBlend();
                            GlStateManager.blendFunc(770, 771);
                            GlStateManager.translate(x - 2, y + count % 5 * 40 - 2, 0.0);
                            GlStateManager.scale(2.0, 2.0, 0.0);
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                            UtilsFX.drawTexturedQuadFull(0.0F, 0.0F, this.zLevel);
                            GlStateManager.popMatrix();
                        }

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x + 2, y + 2 + count % 5 * 40, 0.0);
                        GlStateManager.scale(1.5F, 1.5F, 1.5F);
                        UtilsFX.drawTag(0, 0, aspect, 0.0F, 0, this.zLevel);
                        GlStateManager.popMatrix();
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x + 16, y + 29 + count % 5 * 40, 0.0);
                        GlStateManager.scale(0.5F, 0.5F, 0.5F);
                        String text = aspect.getName();
                        int offset = this.mc.fontRenderer.getStringWidth(text) / 2;
                        this.mc.fontRenderer.drawString(text, -offset, 0, 5263440);
                        GlStateManager.popMatrix();
                        if (aspect.getComponents() != null) {
                            GlStateManager.pushMatrix();
                            GlStateManager.translate(x + 60, y + 4 + count % 5 * 40, 0.0);
                            GlStateManager.scale(1.25F, 1.25F, 1.25F);
                            if (OldResearch.proxy.getPlayerKnowledge().hasDiscoveredAspect(this.mc.player.getGameProfile().getName(), aspect.getComponents()[0])) {
                                UtilsFX.drawTag(0, 0, aspect.getComponents()[0], 0.0F, 0, this.zLevel);
                            } else {
                                this.mc.renderEngine.bindTexture(this.dummyResearch);
                                GlStateManager.color(0.8F, 0.8F, 0.8F, 1.0F);
                                UtilsFX.drawTexturedQuadFull(0.0F, 0.0F, this.zLevel);
                            }

                            GlStateManager.popMatrix();
                            GlStateManager.pushMatrix();
                            GlStateManager.translate(x + 102, y + 4 + count % 5 * 40, 0.0);
                            GlStateManager.scale(1.25F, 1.25F, 1.25F);
                            if (OldResearch.proxy.getPlayerKnowledge().hasDiscoveredAspect(this.mc.player.getGameProfile().getName(), aspect.getComponents()[1])) {
                                UtilsFX.drawTag(0, 0, aspect.getComponents()[1], 0.0F, 0, this.zLevel);
                            } else {
                                this.mc.renderEngine.bindTexture(this.dummyResearch);
                                GlStateManager.color(0.8F, 0.8F, 0.8F, 1.0F);
                                UtilsFX.drawTexturedQuadFull(0.0F, 0.0F, this.zLevel);
                            }

                            GlStateManager.popMatrix();
                            if (OldResearch.proxy.getPlayerKnowledge().hasDiscoveredAspect(this.mc.player.getGameProfile().getName(), aspect.getComponents()[0])) {
                                text = aspect.getComponents()[0].getName();
                                offset = this.mc.fontRenderer.getStringWidth(text) / 2;
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(x + 22 + 50, y + 29 + count % 5 * 40, 0.0);
                                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                                this.mc.fontRenderer.drawString(text, -offset, 0, 5263440);
                                GlStateManager.popMatrix();
                            }

                            if (OldResearch.proxy.getPlayerKnowledge().hasDiscoveredAspect(this.mc.player.getGameProfile().getName(), aspect.getComponents()[1])) {
                                text = aspect.getComponents()[1].getName();
                                offset = this.mc.fontRenderer.getStringWidth(text) / 2;
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(x + 22 + 92, y + 29 + count % 5 * 40, 0.0);
                                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                                this.mc.fontRenderer.drawString(text, -offset, 0, 5263440);
                                GlStateManager.popMatrix();
                            }

                            this.mc.fontRenderer.drawString("=", x + 9 + 32, y + 12 + count % 5 * 40, 10066329);
                            this.mc.fontRenderer.drawString("+", x + 10 + 79, y + 12 + count % 5 * 40, 10066329);
                        } else {
                            this.mc.fontRenderer.drawString(I18n.format("tc.aspect.primal"), x + 54, y + 12 + count % 5 * 40, 7829367);
                        }
                    }
                }
            }

            this.mc.renderEngine.bindTexture(this.tex1);
            float bob = MathHelper.sin((float)this.mc.player.ticksExisted / 3.0F) * 0.2F + 0.1F;
            if (aspectsPage > 0) {
                this.drawTexturedModalRectScaled(x - 20, y + 208, 0, 184, 12, 8, bob);
            }

            if (aspectsPage < this.maxAspectPages - 1) {
                this.drawTexturedModalRectScaled(x + 144, y + 208, 12, 184, 12, 8, bob);
            }

            GlStateManager.popMatrix();
        }
        ci.cancel();
    }

    @Redirect(method = "drawPage", at = @At(value = "INVOKE", target = "Lthaumcraft/api/capabilities/IPlayerKnowledge;isResearchComplete(Ljava/lang/String;)Z", remap = false), remap = false)
    public boolean drawPageInjection(IPlayerKnowledge instance, String s) {
        return instance.isResearchComplete(s);
    }
}
