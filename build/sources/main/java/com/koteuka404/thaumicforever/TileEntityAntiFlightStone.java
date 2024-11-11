package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityAntiFlightStone extends TileEntity implements ITickable {

    // Радіус у блоках (3x3 чанки = 48 блоків)
    private static final int RADIUS_BLOCKS = 48;

    // Створюємо AxisAlignedBB для визначення області перевірки
    private AxisAlignedBB detectionBox = null;

    @Override
    public void update() {
        if (!world.isRemote) {
            // Якщо detectionBox ще не створений, ініціалізуємо його
            if (detectionBox == null) {
                detectionBox = new AxisAlignedBB(
                    pos.add(-RADIUS_BLOCKS, -RADIUS_BLOCKS, -RADIUS_BLOCKS), 
                    pos.add(RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS)
                );
            }

            // Отримуємо всіх гравців у радіусі
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, detectionBox);

            // Перевіряємо кожного гравця
            for (EntityPlayer player : players) {
                // Вимикаємо політ, якщо він дозволений
                if (player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
            }
        }
    }
}
