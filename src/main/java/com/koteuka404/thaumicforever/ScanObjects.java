package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

public class ScanObjects implements IScanThing {

    private final String researchKeyBlock = "!aquareia_ore_scan"; 
    private final String researchKeyItem = "!aquareia_gem_scan";  
    private final String researchKeyEntity = "!armor_stand_scan";

    public ScanObjects() {
        ScanningManager.addScannableThing(this);
    }

    @Override
    public boolean checkThing(EntityPlayer player, Object obj) {
        if (obj instanceof BlockPos) {
            if (player.world.getBlockState((BlockPos) obj).getBlock() == ModOreBlocks.AQUAREIA_ORE) {
                return true;
            }
        }
        if (obj instanceof ItemStack) {
            ItemStack stack = (ItemStack) obj;
            if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                return true;
            }
        }
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
