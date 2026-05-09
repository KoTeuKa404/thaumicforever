package com.koteuka404.thaumicforever.research;

import com.koteuka404.thaumicforever.registry.ModOreBlocks;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import com.koteuka404.thaumicforever.wand.tile.TileArcaneWorkbenchNew;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;
import com.koteuka404.thaumicforever.block.EndOreBlock;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.entity.EntitySkeletonAngry;
import com.koteuka404.thaumicforever.entity.EntityVampireBat;
import com.koteuka404.thaumicforever.entity.ReviveSkeletonEntity;

public class ScanObjects implements IScanThing {

    private final String researchKeyBlock = "!aquareia_ore_scan";
    private final String researchKeyItem = "!aquareia_gem_scan";
    private final String researchKeyEntity = "!armor_stand_scan";
    private final String researchKeySkeletonAngry = "!skeleton_angry_scan";
    private final String researchKeySkeletonRevive = "!skeleton_revive_scan";
    private final String researchKeyVampireBat = "!vampire_bat_scan";
    private final String researchKeyTatteredScrolls = "!tattered_scrolls_scan";
    private final String researchKeyMysteryScrolls = "!mysterious_scrolls_scan";
    private final String researchKeyEndOre = "!end_ore_scan";
    private final String researchKeyTaintItem = "!orb_of_taint";
    private final String researchKeyAuraNode = "!aura_node_scan";
    private final String researchKeyWandBench = "!wand_workbench";
    private final String researchKeyEmptyFocus = "!empty_focus_scan";

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
            if (player.world.getBlockState((BlockPos) obj).getBlock() == ModBlocks.WAND_WORKBENCH) {
                return true;
            }
        }
        if (obj instanceof TileEntity) {
            if (obj instanceof TileArcaneWorkbenchNew) {
                return true;
            }
        }
        if (obj instanceof ItemStack) {
            ItemStack stack = (ItemStack) obj;
            if (stack.getItem() == ModItems.SCROLL_P
                    || stack.getItem() == ModItems.SCROLL_O
                    || stack.getItem() == ModItems.SCROLL_C) {
                return true;
            }
            if (stack.getItem() != null && stack.getItem().getRegistryName() != null) {
                String registryName = stack.getItem().getRegistryName().toString();
                int meta = stack.getMetadata();
                if ("thaumicaugmentation:research_notes".equals(registryName) && meta == 0) {
                    return true;
                }
            }
            if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                return true;
            }
            if (stack.getItem() == ModItems.orb_of_taint) {
                return true;
            }
            if (stack.getItem() == ModItems.EMPTY_FOCUS) {
                return true;
            }
        }
        if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            if (entity instanceof EntityItem) {
                ItemStack stack = ((EntityItem) entity).getItem();
                if (stack.getItem() == ModItems.SCROLL_P
                        || stack.getItem() == ModItems.SCROLL_O
                        || stack.getItem() == ModItems.SCROLL_C) {
                    return true;
                }
                if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                    return true;
                }
                if (stack.getItem() == ModItems.orb_of_taint) {
                    return true;
                }
                if (stack.getItem() == ModItems.EMPTY_FOCUS) {
                    return true;
                }
                if (stack.getItem() != null && stack.getItem().getRegistryName() != null) {
                    String registryName = stack.getItem().getRegistryName().toString();
                    int meta = stack.getMetadata();
                    if ("thaumicaugmentation:research_notes".equals(registryName) && meta == 0) {
                        return true;
                    }
                }
            }
            if (entity instanceof EntityArmorStand) {
                return true;
            }
            if (entity instanceof EntitySkeletonAngry) {
                return true;
            }
            if (entity instanceof ReviveSkeletonEntity) {
                return true;
            }
            if (entity instanceof EntityVampireBat) {
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
            if (player.world.getBlockState(pos).getBlock() == ModBlocks.WAND_WORKBENCH) {
                return researchKeyWandBench;
            }
        }
        if (object instanceof TileEntity) {
            if (object instanceof TileArcaneWorkbenchNew) {
                return researchKeyWandBench;
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
            if (stack.getItem() == ModItems.SCROLL_P
                    || stack.getItem() == ModItems.SCROLL_O
                    || stack.getItem() == ModItems.SCROLL_C) {
                return researchKeyMysteryScrolls;
            }
            if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                return researchKeyItem;
            }
            if (stack.getItem() == ModItems.orb_of_taint) {
                return researchKeyTaintItem;
            }
            if (stack.getItem() == ModItems.EMPTY_FOCUS) {
                return researchKeyEmptyFocus;
            }
        }
        if (object instanceof Entity) {
            if (object instanceof EntityItem) {
                ItemStack stack = ((EntityItem) object).getItem();
                if (stack.getItem() == ModItems.SCROLL_P
                        || stack.getItem() == ModItems.SCROLL_O
                        || stack.getItem() == ModItems.SCROLL_C) {
                    return researchKeyMysteryScrolls;
                }
                if (stack.getItem() == ModItems.AQUAREIA_GEM) {
                    return researchKeyItem;
                }
                if (stack.getItem() == ModItems.orb_of_taint) {
                    return researchKeyTaintItem;
                }
                if (stack.getItem() == ModItems.EMPTY_FOCUS) {
                    return researchKeyEmptyFocus;
                }
                if (stack.getItem() != null && stack.getItem().getRegistryName() != null) {
                    String registryName = stack.getItem().getRegistryName().toString();
                    int meta = stack.getMetadata();
                    if ("thaumicaugmentation:research_notes".equals(registryName) && meta == 0) {
                        return researchKeyTatteredScrolls;
                    }
                }
            }
            if (object instanceof EntityArmorStand) {
                return researchKeyEntity;
            }
            if (object instanceof EntitySkeletonAngry) {
                return researchKeySkeletonAngry;
            }
            if (object instanceof ReviveSkeletonEntity) {
                return researchKeySkeletonRevive;
            }
            if (object instanceof EntityVampireBat) {
                return researchKeyVampireBat;
            }
            if (object instanceof EntityAuraNode) {
                return researchKeyAuraNode;
            }
        }
        return null;
    }
}
