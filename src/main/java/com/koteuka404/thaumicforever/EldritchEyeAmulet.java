package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

public class EldritchEyeAmulet extends Item implements IBauble {

    public EldritchEyeAmulet() {
        super();
        this.setMaxStackSize(1);
        this.setUnlocalizedName("eldritch_eye_amulet");
        this.setRegistryName("eldritch_eye_amulet");
        MinecraftForge.EVENT_BUS.register(this); 
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer && player.world.isRemote) {
            World world = player.world;
            float x = (float)(player.posX + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2f);
            float z = (float)(player.posZ + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, (float)(player.posY + 0.22 * player.height), z, player);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void fogSetColor(EntityViewRenderEvent.FogColors event) {
        EntityPlayer owner = Minecraft.getMinecraft().player;

        if (owner != null && isWearingAmulet(owner)) {
            for (EntityPlayer nearbyPlayer : owner.world.playerEntities) {
                if (nearbyPlayer != owner && isWithinRadius(owner, nearbyPlayer, 10)) {
                    event.setRed(0.0F);
                    event.setGreen(0.0F);
                    event.setBlue(0.0F);
                    break;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void fogDensity(EntityViewRenderEvent.RenderFogEvent event) {
        EntityPlayer owner = Minecraft.getMinecraft().player;

        if (owner != null && isWearingAmulet(owner)) {
            for (EntityPlayer nearbyPlayer : owner.world.playerEntities) {
                if (nearbyPlayer != owner && isWithinRadius(owner, nearbyPlayer, 20)) {
                    GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
                    GlStateManager.setFogStart(0.0F);
                    GlStateManager.setFogEnd(8.0F); 
                    GlStateManager.setFogDensity(0.4F); 
                    break;
                }
            }
        }
    }

    private boolean isWearingAmulet(EntityPlayer player) {
        ItemStack baubleSlot = baubles.api.BaublesApi.getBaublesHandler(player).getStackInSlot(0);
        return baubleSlot != null && baubleSlot.getItem() instanceof EldritchEyeAmulet;
    }

    private boolean isWithinRadius(EntityPlayer owner, EntityPlayer target, double radius) {
        return owner.getDistance(target) <= radius;
    }
}
