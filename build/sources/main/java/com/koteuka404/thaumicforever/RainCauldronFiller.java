package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class RainCauldronFiller {

    private static final int CHECK_INTERVAL = 100; // Частота перевірок (раз на 100 тіків)
    private int tickCounter = 0;

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) {
            return;
        }
        tickCounter = 0; // Скидання лічильника після перевірки

        World world = event.world;

        // Перевірка, чи йде дощ та чи світ не є пустелею (де дощ неможливий)
        if (world.isRaining()) {
            // Ітерація по кожному гравцеві для визначення розташування котлів у їхній околиці
            world.playerEntities.forEach(player -> {
                BlockPos playerPos = player.getPosition();
                Random random = world.rand;

                // Перевіряємо кілька блоків навколо гравця (напр., у радіусі 10 блоків, перевіряємо випадковий блок)
                for (int i = 0; i < 5; i++) { // Обмежуємо кількість перевірених блоків до 5 за раз
                    int xOffset = random.nextInt(21) - 10;
                    int zOffset = random.nextInt(21) - 10;
                    BlockPos cauldronPos = playerPos.add(xOffset, 0, zOffset);
                    IBlockState state = world.getBlockState(cauldronPos);

                    // Перевірка, чи блок є котлом
                    if (state.getBlock() instanceof BlockCauldron) {
                        int level = state.getValue(BlockCauldron.LEVEL);

                        // Наповнюємо котел на один шар, якщо його рівень води менший за максимум (3)
                        if (level < 3) {
                            // Додаємо шанс наповнення, щоб це відбувалось поступово
                            if (random.nextInt(100) < 60) { // 10% ймовірності раз на 100 тіків
                                world.setBlockState(cauldronPos, state.withProperty(BlockCauldron.LEVEL, level + 1), 2);
                            }
                        }
                    }
                }
            });
        }
    }
}
