package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

public class AuraNodeEntity extends Entity {
    private static final float MAX_AURA = 300.0f; // Максимальний рівень аури
    private static final float INCREMENT_AMOUNT = 5.0f; // Кількість підвищення аури за секунду
    private static final int CHUNK_RADIUS = 3; // Радіус в чанках (3x3 чанк)
    private static final int TICKS_PER_SECOND = 20; // Кількість оновлень на секунду
    private int tickCounter = 0; // Лічильник для обмеження підвищення раз на секунду

    public AuraNodeEntity(World world) {
        super(world);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        
        // Збільшуємо лічильник на кожен виклик onUpdate (кожен тік)
        tickCounter++;
        
        // Перевіряємо, чи пройшла 1 секунда
        if (tickCounter >= TICKS_PER_SECOND) {
            tickCounter = 0; // Скидаємо лічильник
            increaseAura(); // Підвищуємо ауру раз на секунду
        }
    }

    /**
     * Підвищення рівня аури в радіусі 3x3 чанків без перевищення максимальної межі в кожному чанку.
     */
    private void increaseAura() {
        // Поточна позиція ноди
        BlockPos nodePos = this.getPosition();

        // Обхід блоків в радіусі 3x3 чанків
        for (int xOffset = -CHUNK_RADIUS * 16; xOffset <= CHUNK_RADIUS * 16; xOffset += 16) {
            for (int zOffset = -CHUNK_RADIUS * 16; zOffset <= CHUNK_RADIUS * 16; zOffset += 16) {
                BlockPos pos = nodePos.add(xOffset, 0, zOffset); // Позиція в кожному чанку

                // Отримати поточний рівень аури в цій позиції
                float currentVis = AuraHelper.getVis(world, pos);

                // Якщо аура менша за максимальну, піднімаємо її поступово
                if (currentVis < MAX_AURA) {
                    float increaseAmount = Math.min(INCREMENT_AMOUNT, MAX_AURA - currentVis);
                    AuraHelper.addVis(world, pos, increaseAmount); // Додаємо 5 одиниць аури кожну секунду
                }
            }
        }
    }

    @Override
    protected void entityInit() {
        // Ніякої додаткової ініціалізації не потрібно
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        // Читання з NBT (якщо потрібно)
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        // Запис в NBT (якщо потрібно)
    }
}
