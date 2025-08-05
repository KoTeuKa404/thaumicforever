package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerMazeEvents {

    private static final String LABYRINTH_VISITED_TAG = "thaumicforeverMazeTitleSeen";

    private boolean isPlayerAboveMaze(EntityPlayer player) {
        return MazeDungeonWrapper.isInMaze(player.world, player.getPosition());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!player.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (isPlayerAboveMaze(player) && !hasSeenMazeTitle(player)) {
                sendMazeActionBar(player, "thaumicforever.title.labyrinth_found");
                setSeenMazeTitle(player);
            }
        }
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
