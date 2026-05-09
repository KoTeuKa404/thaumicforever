package com.koteuka404.thaumicforever.wand.wand.updates;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class UpdateBloodPolymancy implements IWandUpdate {

    @Override
    public void onUpdate(ItemStack stack, EntityPlayer player) {
        if (player.ticksExisted % 40 != 0) return;
        if (player.world.isRemote) return;

        if (WandHelper.getPrimalChargePercentage(stack, player) >= 0.85F) return;
        if (player.getHealth() <= 6.0F) return;

        player.attackEntityFrom(DamageSource.MAGIC, 1.0F);
        WandHelper.addPrimalChargeDistributed(stack, 2, player);
    }
}
