package com.koteuka404.thaumicforever.wand.wand.updates;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class UpdateDreamwood implements IWandUpdate {

    @Override
    public void onUpdate(ItemStack stack, EntityPlayer player) {
        World world = player.getEntityWorld();
        if (world.getTotalWorldTime() % 20 != 0) return;

        boolean boosted = !world.isDaytime() || world.provider.getDimension() == 1;
        float targetCharge = boosted ? 0.90F : 0.75F;
        int recharge = boosted ? 2 : 1;

        if (WandHelper.getPrimalChargePercentage(stack, player) < targetCharge) {
            WandHelper.addPrimalChargeDistributed(stack, recharge, player);
        }
    }
}
