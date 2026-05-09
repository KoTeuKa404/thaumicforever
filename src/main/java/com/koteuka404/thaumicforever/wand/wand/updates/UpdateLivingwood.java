package com.koteuka404.thaumicforever.wand.wand.updates;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UpdateLivingwood implements IWandUpdate {

    @Override
    public void onUpdate(ItemStack stack, EntityPlayer player) {
        World world = player.getEntityWorld();
        if (world.getTotalWorldTime() % 20 != 0) return;

        float targetCharge = world.isDaytime() ? 0.70F : 0.55F;
        if (WandHelper.getPrimalChargePercentage(stack, player) >= targetCharge) return;

        int recharge = isNaturalGround(player) ? 2 : 1;
        WandHelper.addPrimalChargeDistributed(stack, recharge, player);
    }

    private boolean isNaturalGround(EntityPlayer player) {
        BlockPos down = player.getPosition().down();
        String id = String.valueOf(player.world.getBlockState(down).getBlock().getRegistryName());
        return id.contains("grass") || id.contains("dirt") || id.contains("leaves");
    }
}
