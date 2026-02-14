package com.koteuka404.thaumicforever.wand.wand.updates;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class UpdatePrimal implements IWandUpdate {

    @Override
    public void onUpdate(ItemStack stack, EntityPlayer player) {
        if (player.getEntityWorld().getTotalWorldTime() % 20 == 0)
            if (WandHelper.getPrimalChargePercentage(stack, player) < 0.5F)
                WandHelper.addPrimalChargeDistributed(stack, 1, player);
    }

}
