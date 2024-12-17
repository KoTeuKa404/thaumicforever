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
    private final String researchKeySkeletonAngry = "!skeleton_angry_scan"; 
    private final String researchKeySkeletonRevive = "!skeleton_revive_scan"; 
    private final String researchKeyTatteredScrolls = "!tattered_scrolls_scan"; 

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
            // Логіка для Aquareia Gem
            if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                return true;
            }
            // Логіка для Tattered Scrolls
            if (stack.getItem() != null && stack.getItem().getRegistryName() != null) {
                String registryName = stack.getItem().getRegistryName().toString();
                int meta = stack.getMetadata();
                System.out.println("[DEBUG] RegistryName: " + registryName + ", Meta: " + meta); // Дебаг
                if ("thaumicaugmentation:research_notes".equals(registryName) && meta == 0) {
                    return true;
                }
            }
        }
        if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            if (entity instanceof EntityArmorStand) {
                return true;
            }
            if (entity instanceof EntitySkeletonAngry) {
                return true;
            }
            if (entity instanceof ReviveSkeletonEntity) {
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
            ItemStack stack = (ItemStack) object;
            // Ключ для Tattered Scrolls
            if (stack.getItem() != null && stack.getItem().getRegistryName() != null) {
                String registryName = stack.getItem().getRegistryName().toString();
                int meta = stack.getMetadata();
                if ("thaumicaugmentation:research_notes".equals(registryName) && meta == 0) {
                    return researchKeyTatteredScrolls;
                }
            }
            if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                return researchKeyItem;
            }
        }
        if (object instanceof Entity) {
            if (object instanceof EntityArmorStand) {
                return researchKeyEntity;
            }
            if (object instanceof EntitySkeletonAngry) {
                return researchKeySkeletonAngry;
            }
            if (object instanceof ReviveSkeletonEntity) {
                return researchKeySkeletonRevive;
            }
        }
        return null;
    }
}
