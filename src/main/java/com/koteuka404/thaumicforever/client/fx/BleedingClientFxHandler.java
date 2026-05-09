package com.koteuka404.thaumicforever.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;

@SideOnly(Side.CLIENT)
public class BleedingClientFxHandler {

    @SubscribeEvent
    public void onBleedingHitFx(LivingHurtEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityLivingBase)) {
            return;
        }
        if (!event.getEntity().world.isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack heldItem = player.getHeldItemMainhand();
        int bleedingLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.BLEEDING);
        if (bleedingLevel <= 0) {
            return;
        }

        EntityLivingBase target = event.getEntityLiving();
        int count = 3 + Math.min(4, bleedingLevel);

        for (int i = 0; i < count; i++) {
            double x = target.posX + (target.world.rand.nextDouble() - 0.5D) * target.width * 1.1D;
            double y = target.posY + target.height * (0.25D + target.world.rand.nextDouble() * 0.55D);
            double z = target.posZ + (target.world.rand.nextDouble() - 0.5D) * target.width * 1.1D;

            double mx = (target.world.rand.nextDouble() - 0.5D) * 0.03D;
            double my = 0.01D + target.world.rand.nextDouble() * 0.02D;
            double mz = (target.world.rand.nextDouble() - 0.5D) * 0.03D;

            Minecraft.getMinecraft().effectRenderer.addEffect(
                new FXBleedingHit(target.world, x, y, z, mx, my, mz)
            );
        }
    }
}
