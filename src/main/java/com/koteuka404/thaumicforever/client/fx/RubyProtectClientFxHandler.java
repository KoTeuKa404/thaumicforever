package com.koteuka404.thaumicforever.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;

@SideOnly(Side.CLIENT)
public class RubyProtectClientFxHandler {

    @SubscribeEvent
    public void onPlayerDamagedClient(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityPlayer) || !entity.world.isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        Entity attacker = event.getSource().getTrueSource();
        if (!(attacker instanceof EntityLivingBase)) {
            return;
        }

        for (ItemStack armorPiece : player.getArmorInventoryList()) {
            int level = EnumInfusionEnchantment.getInfusionEnchantmentLevel(armorPiece, EnumInfusionEnchantment.RUBYPROTECT);
            if (level > 0) {
                Minecraft.getMinecraft().effectRenderer.addEffect(
                    new FXRubyRunes(
                        player.world,
                        player.posX,
                        player.posY + player.height / 2.0,
                        player.posZ,
                        player,
                        30,
                        player.rotationYaw,
                        player.rotationPitch
                    )
                );
                break;
            }
        }
    }
}
