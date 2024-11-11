package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

public class ScanObjects implements IScanThing {

    private final String researchKeyBlock = "!aquareia_ore_scan"; // Унікальний ключ для блоку
    private final String researchKeyItem = "!aquareia_gem_scan";  // Унікальний ключ для предмета
    private final String researchKeyEntity = "!armor_stand_scan"; // Унікальний ключ для стійки для броні

    public ScanObjects() {
        // Реєстрація сканування для блоку, предмета та ентиті
        ScanningManager.addScannableThing(this);
    }

    @Override
    public boolean checkThing(EntityPlayer player, Object obj) {
        // Перевірка блоку
        if (obj instanceof BlockPos) {
            if (player.world.getBlockState((BlockPos) obj).getBlock() == ModOreBlocks.AQUAREIA_ORE) {
                return true;
            }
        }
        // Перевірка предмета
        if (obj instanceof ItemStack) {
            ItemStack stack = (ItemStack) obj;
            if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                return true;
            }
        }
        // Перевірка ентиті (стійка для броні)
        if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            if (entity instanceof EntityArmorStand) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getResearchKey(EntityPlayer player, Object object) {
        if (object instanceof BlockPos) {
            return researchKeyBlock;
        }
        if (object instanceof ItemStack) {
            return researchKeyItem;
        }
        if (object instanceof Entity && object instanceof EntityArmorStand) {
            return researchKeyEntity;
        }
        return null;
    }
}
