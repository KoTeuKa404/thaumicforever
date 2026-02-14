package com.koteuka404.thaumicforever.wand.wand.updates;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class UpdateWater implements IWandUpdate {

    @Override
    public void onUpdate(ItemStack stack, EntityPlayer player) {
        if (player.getEntityWorld().getTotalWorldTime() % 20 == 0) {
            Biome b = player.getEntityWorld().getBiome(player.getPosition());
            if (BiomeDictionary.getTypes(b).contains(Type.SNOWY) && WandHelper.getPrimalChargePercentage(stack, player) < 0.5F) {
                WandHelper.addPrimalChargeDistributed(stack, 1, player);

            }
        }
    }

}
