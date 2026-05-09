package com.koteuka404.thaumicforever.wand.wand.updates;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;

public class UpdateInfernalPolymancy implements IWandUpdate {

    @Override
    public void onUpdate(ItemStack stack, EntityPlayer player) {
        if (player.ticksExisted % 20 != 0) return;

        boolean inNether = player.world.provider.getDimension() == -1;
        float target = inNether ? 0.90F : 0.45F;
        int recharge = inNether ? 2 : 1;

        if (WandHelper.getPrimalChargePercentage(stack, player) < target) {
            WandHelper.addPrimalChargeDistributed(stack, recharge, player);
        }

        if (player.isBurning()) player.extinguish();
        if (player.isPotionActive(MobEffects.WITHER)) player.removePotionEffect(MobEffects.WITHER);
    }
}
