package com.koteuka404.thaumicforever.research;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.IScanThing;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;

public class SafeScanSky implements IScanThing {

    @Override
    public boolean checkThing(EntityPlayer player, Object obj) {
        if (!canScanSky(player, obj)) {
            return false;
        }

        SkyTarget target = getSkyTarget(player);
        return target.valid;
    }

    @Override
    public void onSuccess(EntityPlayer player, Object obj) {
        if (!canScanSky(player, obj)) {
            return;
        }

        SkyTarget target = getSkyTarget(player);
        if (!target.valid) {
            return;
        }

        int day = (int) (player.world.getTotalWorldTime() / 24000L);
        String dayPrefix = "CEL_" + day + "_";
        String key = dayPrefix + target.researchSuffix;

        if (ThaumcraftCapabilities.knowsResearch(player, key)) {
            player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.1", ""), true);
            return;
        }

        if (hasScribingTools(player) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
            ItemStack note = new ItemStack(ItemsTC.celestialNotes, 1, target.noteMeta);
            if (!player.inventory.addItemStackToInventory(note)) {
                player.dropItem(note, false);
            }
            ThaumcraftApi.internalMethods.progressResearch(player, key);
        } else {
            player.sendStatusMessage(new TextComponentTranslation("tc.celestial.fail.2", ""), true);
        }

        cleanOldCelestialResearch(player, dayPrefix);
    }

    @Override
    public String getResearchKey(EntityPlayer player, Object object) {
        return "";
    }

    private static boolean canScanSky(EntityPlayer player, Object obj) {
        return obj == null
            && player.rotationPitch <= 0.0F
            && player.world.canSeeSky(player.getPosition().up())
            && player.world.provider.getDimensionType() == DimensionType.OVERWORLD
            && ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING");
    }

    private static boolean hasScribingTools(EntityPlayer player) {
        if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1, 32767), true)) {
            return true;
        }

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IScribeTools) {
                return true;
            }
        }
        return false;
    }

    private static SkyTarget getSkyTarget(EntityPlayer player) {
        int yaw = ((int) (player.rotationYaw + 90.0F)) % 360;
        if (yaw < 0) {
            yaw += 360;
        }
        int pitch = (int) Math.abs(player.rotationPitch);
        int celestialAngle = (int) ((player.world.getCelestialAngle(0.0F) + 0.25D) * 360.0D) % 360;
        boolean moon = celestialAngle > 180;
        if (moon) {
            celestialAngle -= 180;
        }

        boolean alignedYaw;
        boolean alignedPitch;
        if (celestialAngle > 90) {
            alignedYaw = Math.abs(Math.abs(yaw) - 180) < 10;
            alignedPitch = Math.abs(180 - celestialAngle - pitch) < 7;
        } else {
            alignedYaw = Math.abs(yaw) < 10;
            alignedPitch = Math.abs(celestialAngle - pitch) < 7;
        }

        if (alignedYaw && alignedPitch) {
            if (moon) {
                int phase = player.world.provider.getMoonPhase(player.world.getWorldTime());
                return new SkyTarget(true, "Moon" + phase, 5 + phase);
            }
            return new SkyTarget(true, "Sun", 0);
        }

        if (moon) {
            EnumFacing facing = player.getAdjustedHorizontalFacing();
            int star = facing.getIndex() - 2;
            return new SkyTarget(true, "Star" + star, 1 + star);
        }

        return SkyTarget.INVALID;
    }

    private static void cleanOldCelestialResearch(EntityPlayer player, String keepPrefix) {
        ArrayList<String> remove = new ArrayList<String>();
        for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (key.startsWith("CEL_") && !key.startsWith(keepPrefix)) {
                remove.add(key);
            }
        }
        for (String key : remove) {
            ThaumcraftCapabilities.getKnowledge(player).removeResearch(key);
        }
        ResearchManager.syncList.put(player.getName(), Boolean.TRUE);
    }

    private static class SkyTarget {
        static final SkyTarget INVALID = new SkyTarget(false, "", 0);

        final boolean valid;
        final String researchSuffix;
        final int noteMeta;

        SkyTarget(boolean valid, String researchSuffix, int noteMeta) {
            this.valid = valid;
            this.researchSuffix = researchSuffix;
            this.noteMeta = noteMeta;
        }
    }
}
