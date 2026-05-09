package com.koteuka404.thaumicforever.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PotionFlight extends Potion {

    public static final PotionFlight INSTANCE = new PotionFlight();
    private static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "textures/misc/wind.png");
    static final String TAG_FLIGHT_OWNED = "thaumicforever_flight_owned";
    static final String TAG_FLIGHT_GRACE = "thaumicforever_flight_grace";
    static final int DEFAULT_GRACE_TICKS = 30;

    private PotionFlight() {
        super(false, 0x00FFFF); 
        setPotionName("effect.flight");
        setRegistryName(new ResourceLocation("thaumicforever", "flight"));
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            NBTTagCompound data = player.getEntityData();
            data.setBoolean(TAG_FLIGHT_OWNED, true);
            data.setInteger(TAG_FLIGHT_GRACE, DEFAULT_GRACE_TICKS);
            player.capabilities.allowFlying = true;
            int amp = Math.max(0, amplifier);
            float baseSpeed = 0.02f + 0.03f * Math.min(amp, 4);
            PotionEffect strength = player.getActivePotionEffect(MobEffects.STRENGTH);
            if (strength != null && strength.getAmplifier() >= 1) {
                baseSpeed *= 1.5f;
            }
            setFlySpeed(player.capabilities, baseSpeed);
            player.sendPlayerAbilities();
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributes, int amplifier) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.capabilities.isCreativeMode && !player.isSpectator()) {
                NBTTagCompound data = player.getEntityData();
                if (data.getBoolean(TAG_FLIGHT_OWNED)) {
                    data.setInteger(TAG_FLIGHT_GRACE, DEFAULT_GRACE_TICKS);
                }
                if (player.onGround) {
                    revokeFlight(player);
                } else {
                    player.fallDistance = 0.0F;
                }
            } else {
                clearFlightTags(player);
            }
        }
        super.removeAttributesModifiersFromEntity(entity, attributes, amplifier);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true; 
    }

    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        GlStateManager.disableBlend();
    }

    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
        GlStateManager.disableBlend();
    }

    private static void setFlySpeed(PlayerCapabilities capabilities, float speed) {
        try {
            ReflectionHelper.setPrivateValue(PlayerCapabilities.class, capabilities, speed, "flySpeed", "field_75096_f");
            return;
        } catch (Throwable ignored) {
        }
        try {
            java.lang.reflect.Method method = PlayerCapabilities.class.getDeclaredMethod("setFlySpeed", float.class);
            method.setAccessible(true);
            method.invoke(capabilities, speed);
        } catch (Throwable ignored) {
        }
    }

    public static void resetFlySpeed(EntityPlayer player) {
        if (player == null) return;
        setFlySpeed(player.capabilities, 0.05f);
        player.sendPlayerAbilities();
    }

    public static void revokeFlight(EntityPlayer player) {
        if (player.capabilities.isCreativeMode || player.isSpectator()) {
            resetFlySpeed(player);
            clearFlightTags(player);
            return;
        }
        player.capabilities.allowFlying = false;
        player.capabilities.isFlying = false;
        resetFlySpeed(player);
        clearFlightTags(player);
    }

    public static void clearFlightTags(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        data.removeTag(TAG_FLIGHT_OWNED);
        data.removeTag(TAG_FLIGHT_GRACE);
    }
}
