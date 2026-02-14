package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerMazeEvents {

    private static final String LABYRINTH_VISITED_TAG = "thaumicforeverMazeTitleSeen";

    private static final int Y_RANGE_ABOVE = 80;
    private static final int HORIZ_MARGIN = 2;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!player.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (isPlayerAboveAnyMaze(player) && !hasSeenMazeTitle(player)) {
                sendMazeActionBar(player, "thaumicforever.title.labyrinth_found");
                setSeenMazeTitle(player);
            }
        }
    }

    private boolean isPlayerAboveAnyMaze(EntityPlayer player) {
        World world = player.world;
        BlockPos p = player.getPosition();
        int px = p.getX();
        int py = p.getY();
        int pz = p.getZ();

        for (BoundingBox box : getAllBoxesMerged(world)) {
            if (px >= box.getMinX() - HORIZ_MARGIN && px <= box.getMaxX() + HORIZ_MARGIN
            && pz >= box.getMinZ() - HORIZ_MARGIN && pz <= box.getMaxZ() + HORIZ_MARGIN) {
                int topY = box.getMaxY();
                if (py >= topY && py <= topY + Y_RANGE_ABOVE) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<BoundingBox> getAllBoxesMerged(World world) {
        Set<BoundingBox> merged = new HashSet<>(WorldTickHandler.getInstance().getAllDungeonBounds());
        DungeonBoundsData data = DungeonBoundsData.get(world);
        if (data != null) merged.addAll(data.getBoxes());
        return merged;
    }

    public static boolean hasSeenMazeTitle(EntityPlayer player) {
        return player.getEntityData().getBoolean(LABYRINTH_VISITED_TAG);
    }

    public static void setSeenMazeTitle(EntityPlayer player) {
        player.getEntityData().setBoolean(LABYRINTH_VISITED_TAG, true);
    }

    public static void sendMazeActionBar(EntityPlayer player, String translationKey) {
        player.sendStatusMessage(
            new TextComponentTranslation(translationKey).setStyle(
                new Style().setColor(TextFormatting.LIGHT_PURPLE)
            ),
            true
        );
    }
}
