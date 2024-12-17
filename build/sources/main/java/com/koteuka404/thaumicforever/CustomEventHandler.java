package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CustomEventHandler {

    // Таймери для гравців
    private static final Map<EntityPlayer, Integer> waveTimers = new HashMap<>();

    public CustomEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void startParticleWave(World world, EntityPlayer player) {
        waveTimers.put(player, 0); // Початок хвилі (таймер = 0)
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.world;

        // Перевіряємо, чи гравець активував хвилю
        if (waveTimers.containsKey(player)) {
            int timer = waveTimers.get(player);
            double startRadius = 2.0; // Початковий радіус
            double maxRadius = 5.0;  // Максимальний радіус
            int steps = 50;          // Загальна кількість кроків

            if (timer <= steps) {
                // Обчислюємо поточний радіус хвилі
                double currentRadius = startRadius + (maxRadius - startRadius) * (timer / (double) steps);

                // Генерація партиклів по колу
                if (world.isRemote) {
                    for (int angle = 0; angle < 360; angle += 10) { // Крок 10 градусів
                        double radians = Math.toRadians(angle);
                        double x = player.posX + currentRadius * Math.cos(radians);
                        double z = player.posZ + currentRadius * Math.sin(radians);
                        double y = player.posY + 1.0; // Трохи вище рівня гравця

                        world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0.01, 0);
                    }
                }

                // Наносимо урон та підпалюємо ворогів у радіусі
                if (!world.isRemote) {
                    AxisAlignedBB area = new AxisAlignedBB(
                            player.posX - currentRadius, player.posY - 1, player.posZ - currentRadius,
                            player.posX + currentRadius, player.posY + 2, player.posZ + currentRadius
                    );

                    List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area, entity -> entity != player);

                    for (EntityLivingBase entity : entities) {
                        if (entity.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(player), 10.0F)) {
                            entity.setFire(5); // Підпалюємо на 5 секунд
                        }
                    }
                }

                // Збільшуємо таймер для наступного тіку
                waveTimers.put(player, timer + 1);
            } else {
                // Завершуємо хвилю
                waveTimers.remove(player);
            }
        }
    }
}
