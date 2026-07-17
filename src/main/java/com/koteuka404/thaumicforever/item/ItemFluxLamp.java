package com.koteuka404.thaumicforever.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.registry.ModItems;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.lib.potions.PotionWarpWard;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ItemFluxLamp extends Item implements IBauble, IRenderBauble {

    public ItemFluxLamp() {
        setMaxStackSize(1);
        setRegistryName("flux_lamp");
        setUnlocalizedName("flux_lamp");
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.BELT;
    }

    @Override
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
        if (type != RenderType.BODY || player.isInvisible()) return;

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        // Baubles' BODY transform is centered on the torso; place the lamp at the belt.
        GlStateManager.translate(0.35F, 0.95F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.65F, 0.65F, 0.65F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            removeFluxEffects(player);
            if (PotionWarpWard.instance != null && (player.ticksExisted % 40 == 0)) {
                player.addPotionEffect(new PotionEffect(PotionWarpWard.instance, 60, 0, true, false));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        PotionEffect effect = event.getPotionEffect();
        if (effect != null && hasFluxLamp(entity) && isFluxEffect(effect.getPotion())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (hasFluxLamp(event.getEntityLiving()) && isFluxDamage(event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (hasFluxLamp(event.getEntityLiving()) && isFluxDamage(event.getSource())) {
            event.setCanceled(true);
        }
    }

    private static boolean hasFluxLamp(EntityLivingBase entity) {
        return entity instanceof EntityPlayer
                && BaublesApi.isBaubleEquipped((EntityPlayer) entity, ModItems.FLUX_LAMP) >= 0;
    }

    private static void removeFluxEffects(EntityPlayer player) {
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            if (isFluxEffect(effect.getPotion())) {
                player.removePotionEffect(effect.getPotion());
            }
        }
    }

    private static boolean isFluxEffect(Potion potion) {
        if (potion == null) return false;
        if (potion == PotionFluxTaint.instance) return true;

        ResourceLocation id = Potion.REGISTRY.getNameForObject(potion);
        String key = id == null ? "" : id.toString().toLowerCase(Locale.ROOT);
        String name = potion.getName() == null ? "" : potion.getName().toLowerCase(Locale.ROOT);
        return key.contains("flux") || key.contains("taint")
                || name.contains("flux") || name.contains("taint");
    }

    private static boolean isFluxDamage(net.minecraft.util.DamageSource source) {
        if (source == null) return false;
        if (source == DamageSourceThaumcraft.taint) return true;
        String type = source.damageType == null ? "" : source.damageType.toLowerCase(Locale.ROOT);
        return type.contains("flux") || type.contains("taint");
    }

    @Override
    @SuppressWarnings("deprecation")
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + "Neutralizes " + TextFormatting.DARK_PURPLE
                + "Flux" + TextFormatting.GRAY + " effects and damage.");
        tooltip.add(TextFormatting.GRAY + "Dampens the effects of " + TextFormatting.DARK_PURPLE + "Warp" + TextFormatting.GRAY + ".");
    }
}
