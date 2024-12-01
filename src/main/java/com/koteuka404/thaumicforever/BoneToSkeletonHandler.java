package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class BoneToSkeletonHandler {

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        // Перевіряємо, чи світ не є віддаленим (серверний)
        if (!event.world.isRemote) {
            // Збираємо всі предмети в світі
            List<EntityItem> items = event.world.getEntities(EntityItem.class, item -> item != null && !item.isDead);

            for (EntityItem item : items) {
                // Перевіряємо, чи це кістка (OldBone)
                if (item.getItem().getItem() == ModItems.OldBone) {
                    // Пошук поруч інших кісток
                    AxisAlignedBB searchBox = new AxisAlignedBB(
                            item.posX - 3, item.posY - 3, item.posZ - 3,
                            item.posX + 3, item.posY + 3, item.posZ + 3
                    );

                    List<EntityItem> nearbyItems = event.world.getEntitiesWithinAABB(EntityItem.class, searchBox);
                    int boneCount = 0;

                    // Рахуємо всі кістки поблизу
                    for (EntityItem nearbyItem : nearbyItems) {
                        if (nearbyItem.getItem().getItem() == ModItems.OldBone) {
                            boneCount += nearbyItem.getItem().getCount();
                        }
                    }

                    // Якщо знайдено 4 або більше OldBone
                    if (boneCount >= 4) {
                        // Видаляємо рівно 4 OldBone
                        int bonesToRemove = 4;
                        for (EntityItem nearbyItem : nearbyItems) {
                            if (nearbyItem.getItem().getItem() == ModItems.OldBone) {
                                ItemStack stack = nearbyItem.getItem();
                                int count = stack.getCount();

                                if (count > bonesToRemove) {
                                    stack.shrink(bonesToRemove);
                                    bonesToRemove = 0;
                                } else {
                                    bonesToRemove -= count;
                                    nearbyItem.setDead();
                                }

                                if (bonesToRemove <= 0) {
                                    break;
                                }
                            }
                        }

                        // Спавнимо вашого кастомного скелета
                        ReviveSkeletonEntity reviveSkeleton = new ReviveSkeletonEntity(event.world);
                        reviveSkeleton.setPosition(item.posX, item.posY, item.posZ);
                        event.world.spawnEntity(reviveSkeleton);


                        // Завершуємо обробку, щоб уникнути дублювань
                        return;
                    }
                }
            }
        }
    }
}
