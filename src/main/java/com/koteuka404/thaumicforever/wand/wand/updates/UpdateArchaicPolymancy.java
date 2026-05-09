package com.koteuka404.thaumicforever.wand.wand.updates;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class UpdateArchaicPolymancy implements IWandUpdate {

    @Override
    public void onUpdate(ItemStack stack, EntityPlayer player) {
        if (player.ticksExisted % 20 != 0) return;

        Biome biome = player.world.getBiome(player.getPosition());
        boolean hotOrNether = BiomeDictionary.getTypes(biome).contains(Type.HOT)
                || BiomeDictionary.getTypes(biome).contains(Type.NETHER);

        float target = hotOrNether ? 0.80F : 0.55F;
        int recharge = hotOrNether ? 2 : 1;

        if (WandHelper.getPrimalChargePercentage(stack, player) < target) {
            WandHelper.addPrimalChargeDistributed(stack, recharge, player);
        }
    }
}
