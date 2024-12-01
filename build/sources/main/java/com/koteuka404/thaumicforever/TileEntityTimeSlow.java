package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityTimeSlow extends TileEntity implements ITickable {

    private static final int RADIUS = 3; // Радіус охоплення
    private static final int SLOWDOWN_INTERVAL = 3; // Кількість пропущених тиків

    // Глобальна мапа для контролю уповільнених TileEntity
    private static final Map<BlockPos, Integer> slowRegistry = new HashMap<>();

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            addToRegistry();
        }
    }

    @Override
    public void onChunkUnload() {
        if (!world.isRemote) {
            removeFromRegistry();
        }
    }

    @Override
    public void invalidate() {
        if (!world.isRemote) {
            removeFromRegistry();
        }
        super.invalidate();
    }

    private void addToRegistry() {
        AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS));
        for (BlockPos targetPos : BlockPos.getAllInBox(new BlockPos(area.minX, area.minY, area.minZ),
                                                       new BlockPos(area.maxX, area.maxY, area.maxZ))) {
            if (!slowRegistry.containsKey(targetPos)) {
                slowRegistry.put(targetPos, 0); // Додаємо блок у реєстр
            }
        }
    }

    private void removeFromRegistry() {
        AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS));
        for (BlockPos targetPos : BlockPos.getAllInBox(new BlockPos(area.minX, area.minY, area.minZ),
                                                       new BlockPos(area.maxX, area.maxY, area.maxZ))) {
            slowRegistry.remove(targetPos); // Видаляємо блок із реєстру
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            // Просто контролюємо реєстр, нічого іншого тут не робимо
        }
    }

    public static boolean shouldUpdate(BlockPos pos) {
        if (slowRegistry.containsKey(pos)) {
            int ticksSkipped = slowRegistry.get(pos);
            ticksSkipped++;
            if (ticksSkipped >= SLOWDOWN_INTERVAL) {
                slowRegistry.put(pos, 0); // Скидаємо лічильник
                return true; // Дозволяємо оновлення
            } else {
                slowRegistry.put(pos, ticksSkipped); // Оновлюємо лічильник
                return false; // Блокуємо оновлення
            }
        }
        return true; // Якщо блок не під впливом, дозволяємо оновлення
    }
}
