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
    private final String researchKeyEndOre = "!end_ore_scan"; 
    private final String researchKeyTaintItem = "!orb_of_taint";  
    private final String researchKeyAuraNode = "!aura_node_scan";

    public ScanObjects() {
        ScanningManager.addScannableThing(this);
    }

    @Override
    public boolean checkThing(EntityPlayer player, Object obj) {
        if (obj instanceof BlockPos) {
            if (player.world.getBlockState((BlockPos) obj).getBlock() == ModOreBlocks.AQUAREIA_ORE) {
                return true;
            }
            if (player.world.getBlockState((BlockPos) obj).getBlock() == ModBlocks.EndOreBlock) { 
                return true;
            }
        }
        if (obj instanceof ItemStack) {
            ItemStack stack = (ItemStack) obj;
            if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                return true;
            }
            if (stack.getItem() == ModItems.orb_of_taint) {
                return true;
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
            if (entity instanceof EntityAuraNode) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getResearchKey(EntityPlayer player, Object object) {
        if (object instanceof BlockPos) {
            BlockPos pos = (BlockPos) object;
            if (player.world.getBlockState(pos).getBlock() == ModOreBlocks.AQUAREIA_ORE) {
                return researchKeyBlock;
            }
            if (player.world.getBlockState(pos).getBlock() == ModBlocks.EndOreBlock) {
                return researchKeyEndOre;
            }
        }
        if (object instanceof ItemStack) {
            ItemStack stack = (ItemStack) object;
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
            if (stack.getItem() == ModItems.orb_of_taint) {
                return researchKeyTaintItem;
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
            if (object instanceof EntityAuraNode) {
                return researchKeyAuraNode;
            }
        }
        return null;
    }
}
