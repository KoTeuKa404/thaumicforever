package com.koteuka404.thaumicforever.item;

import org.lwjgl.opengl.GL11;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.koteuka404.thaumicforever.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ItemMask extends ItemArmor implements IBauble, IRenderBauble {

    private static final ArmorMaterial MASK_MATERIAL = EnumHelper.addArmorMaterial(
            "tf_mask",
            "thaumicforever:mask",
            8,
            new int[] {1, 2, 3, 1},
            15,
            net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            0.0F
    );

    private static final ResourceLocation MASK_TEXTURE =
            new ResourceLocation("thaumicforever", "textures/entity/mask.png");

    private static final float MAGIC_DAMAGE_MULTIPLIER = 0.15F;

    private static final float TEX_W = 128.0F;
    private static final float TEX_H = 64.0F;

    private static final float V0 = 16.0F / TEX_H;
    private static final float V1 = 32.0F / TEX_H;

    private static final float LEFT_U0 = 0.0F / TEX_W;
    private static final float LEFT_U1 = 16.0F / TEX_W;

    private static final float FRONT_U0 = 16.0F / TEX_W;
    private static final float FRONT_U1 = 32.0F / TEX_W;

    private static final float RIGHT_U0 = 32.0F / TEX_W;
    private static final float RIGHT_U1 = 48.0F / TEX_W;

    private static final float BACK_U0 = 48.0F / TEX_W;
    private static final float BACK_U1 = 64.0F / TEX_W;

    /*
     * Розмір шлема для armor-render.
     * Ці значення вже нормально працюють як шлем.
     */
    private static final float HELMET_R = 0.370F;
    private static final float HELMET_TOP = -0.615F;
    private static final float HELMET_BOTTOM = 0.095F;

    /*
     * Окрема підгонка тільки для Baubles.
     *
     * BAUBLE_Y_OFFSET:
     * більше значення - нижче
     * менше значення - вище
     *
     * BAUBLE_ROTATE_Y:
     * виправляє поворот на 90 градусів
     */
    private static final float BAUBLE_X_OFFSET = 0.000F;
    private static final float BAUBLE_Y_OFFSET = 1.650F;
    private static final float BAUBLE_Z_OFFSET = 0.000F;
    private static final float BAUBLE_ROTATE_Y = -90.0F;

    @SideOnly(Side.CLIENT)
    private MaskModel model;

    public ItemMask() {
        super(MASK_MATERIAL, 0, EntityEquipmentSlot.HEAD);

        setRegistryName("mask");
        setUnlocalizedName("mask");
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.HEAD;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        DamageSource source = event.getSource();
        if (source == null || !source.isMagicDamage() || event.getAmount() <= 0.0F) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (isMaskEquipped(player)) {
            event.setAmount(event.getAmount() * MAGIC_DAMAGE_MULTIPLIER);
        }
    }

    private static boolean isMaskEquipped(EntityPlayer player) {
        if (isMask(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD))) {
            return true;
        }

        return BaublesApi.isBaubleEquipped(player, ModItems.MASK) >= 0;
    }

    private static boolean isMask(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() == ModItems.MASK;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumicforever:textures/entity/mask.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(
            EntityLivingBase entityLiving,
            ItemStack itemStack,
            EntityEquipmentSlot armorSlot,
            ModelBiped defaultModel
    ) {
        if (armorSlot != EntityEquipmentSlot.HEAD) {
            return defaultModel;
        }

        MaskModel mask = getMaskModel();
        mask.setFrom(defaultModel);
        return mask;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
        if (type != RenderType.HEAD) {
            return;
        }

        GlStateManager.pushMatrix();

        /*
         * Для Baubles не використовуємо:
         * bipedHead.postRender()
         * translateToFace()
         * defaultTransforms()
         * ручний headYaw/headPitch
         *
         * Вони якраз і давали злітання вверх або кривий поворот.
         */
        IRenderBauble.Helper.translateToHeadLevel(player);

        if (player.isSneaking()) {
            IRenderBauble.Helper.rotateIfSneaking(player);
        }

        /*
         * Спочатку ставимо модель нижче,
         * потім повертаємо її назад на 90 градусів.
         */
        GlStateManager.translate(BAUBLE_X_OFFSET, BAUBLE_Y_OFFSET, BAUBLE_Z_OFFSET);
        GlStateManager.rotate(BAUBLE_ROTATE_Y, 0.0F, 1.0F, 0.0F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(MASK_TEXTURE);
        renderHelmetMask();

        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    private MaskModel getMaskModel() {
        if (model == null) {
            model = new MaskModel();
        }

        return model;
    }

    @SideOnly(Side.CLIENT)
    private static void renderHelmetMask() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(MASK_TEXTURE);

        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
        );

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        renderFrontFace();
        renderLeftFace();
        renderRightFace();
        renderBackFace();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    private static void renderFrontFace() {
        float x0 = -HELMET_R;
        float x1 = HELMET_R;
        float y0 = HELMET_TOP;
        float y1 = HELMET_BOTTOM;
        float z = -HELMET_R;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buf.pos(x0, y1, z).tex(FRONT_U0, V1).endVertex();
        buf.pos(x1, y1, z).tex(FRONT_U1, V1).endVertex();
        buf.pos(x1, y0, z).tex(FRONT_U1, V0).endVertex();
        buf.pos(x0, y0, z).tex(FRONT_U0, V0).endVertex();

        tess.draw();
    }

    @SideOnly(Side.CLIENT)
    private static void renderLeftFace() {
        float x = -HELMET_R;
        float y0 = HELMET_TOP;
        float y1 = HELMET_BOTTOM;
        float zFront = -HELMET_R;
        float zBack = HELMET_R;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buf.pos(x, y1, zBack).tex(LEFT_U0, V1).endVertex();
        buf.pos(x, y1, zFront).tex(LEFT_U1, V1).endVertex();
        buf.pos(x, y0, zFront).tex(LEFT_U1, V0).endVertex();
        buf.pos(x, y0, zBack).tex(LEFT_U0, V0).endVertex();

        tess.draw();
    }

    @SideOnly(Side.CLIENT)
    private static void renderRightFace() {
        float x = HELMET_R;
        float y0 = HELMET_TOP;
        float y1 = HELMET_BOTTOM;
        float zFront = -HELMET_R;
        float zBack = HELMET_R;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buf.pos(x, y1, zFront).tex(RIGHT_U0, V1).endVertex();
        buf.pos(x, y1, zBack).tex(RIGHT_U1, V1).endVertex();
        buf.pos(x, y0, zBack).tex(RIGHT_U1, V0).endVertex();
        buf.pos(x, y0, zFront).tex(RIGHT_U0, V0).endVertex();

        tess.draw();
    }

    @SideOnly(Side.CLIENT)
    private static void renderBackFace() {
        float x0 = -HELMET_R;
        float x1 = HELMET_R;
        float y0 = HELMET_TOP;
        float y1 = HELMET_BOTTOM;
        float z = HELMET_R;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buf.pos(x1, y1, z).tex(BACK_U0, V1).endVertex();
        buf.pos(x0, y1, z).tex(BACK_U1, V1).endVertex();
        buf.pos(x0, y0, z).tex(BACK_U1, V0).endVertex();
        buf.pos(x1, y0, z).tex(BACK_U0, V0).endVertex();

        tess.draw();
    }

    @SideOnly(Side.CLIENT)
    private static class MaskModel extends ModelBiped {

        MaskModel() {
            super(0.0F, 0.0F, 128, 64);
            this.setVisible(false);
        }

        void setFrom(ModelBiped source) {
            this.setVisible(false);

            this.bipedHead.showModel = true;
            this.bipedHeadwear.showModel = false;

            if (source == null) {
                this.bipedHead.rotateAngleX = 0.0F;
                this.bipedHead.rotateAngleY = 0.0F;
                this.bipedHead.rotateAngleZ = 0.0F;

                this.bipedHead.rotationPointX = 0.0F;
                this.bipedHead.rotationPointY = 0.0F;
                this.bipedHead.rotationPointZ = 0.0F;

                return;
            }

            this.bipedHead.rotateAngleX = source.bipedHead.rotateAngleX;
            this.bipedHead.rotateAngleY = source.bipedHead.rotateAngleY;
            this.bipedHead.rotateAngleZ = source.bipedHead.rotateAngleZ;

            this.bipedHead.rotationPointX = source.bipedHead.rotationPointX;
            this.bipedHead.rotationPointY = source.bipedHead.rotationPointY;
            this.bipedHead.rotationPointZ = source.bipedHead.rotationPointZ;
        }

        @Override
        public void render(
                Entity entity,
                float limbSwing,
                float limbSwingAmount,
                float ageInTicks,
                float netHeadYaw,
                float headPitch,
                float scale
        ) {
            GlStateManager.pushMatrix();

            Minecraft.getMinecraft().getTextureManager().bindTexture(MASK_TEXTURE);

            /*
             * Armor-render працює правильно через ModelBiped.
             * Тут нічого не міняємо.
             */
            this.bipedHead.postRender(scale);
            renderHelmetMask();

            GlStateManager.popMatrix();
        }
    }
}
