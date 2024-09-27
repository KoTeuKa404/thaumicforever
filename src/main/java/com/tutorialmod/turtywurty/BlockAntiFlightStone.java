package com.tutorialmod.turtywurty;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BlockAntiFlightStone extends Block {

    // Радіус у чанках (3x3 чанки = радіус 48 блоків)
    private static final int RADIUS_BLOCKS = 48;

    public BlockAntiFlightStone() {
        super(Material.ROCK);
        setUnlocalizedName("anti_flight_stone");
        setRegistryName("anti_flight_stone");
        setHardness(3.0F); // Можна налаштувати як камінь
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Обробник подій для перевірки кожен тік
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;

        // Перебираємо всі гравців у світі
        List<EntityPlayer> players = world.playerEntities;

        for (EntityPlayer player : players) {
            BlockPos playerPos = player.getPosition();

            // Перевіряємо всі блоки цього типу у світі
            for (BlockPos pos : BlockPos.getAllInBox(playerPos.add(-RADIUS_BLOCKS, -RADIUS_BLOCKS, -RADIUS_BLOCKS),
                                                     playerPos.add(RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS))) {
                if (world.getBlockState(pos).getBlock() instanceof BlockAntiFlightStone) {
                    // Якщо гравець у радіусі, вимикаємо політ
                    if (player.capabilities.allowFlying) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                }
            }
        }
    }
}
