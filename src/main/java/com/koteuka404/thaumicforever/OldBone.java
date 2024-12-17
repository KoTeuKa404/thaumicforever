package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class OldBone extends Item {

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        World world = entityItem.getEntityWorld();

        if (!world.isRemote && entityItem != null && !entityItem.isDead) {
            AxisAlignedBB searchBox = new AxisAlignedBB(
                    entityItem.posX - 3, entityItem.posY - 3, entityItem.posZ - 3,
                    entityItem.posX + 3, entityItem.posY + 3, entityItem.posZ + 3
            );

            List<EntityItem> nearbyBones = world.getEntitiesWithinAABB(EntityItem.class, searchBox, nearbyItem ->
                    nearbyItem.getItem().getItem() instanceof OldBone && !nearbyItem.isDead);

            int totalBones = nearbyBones.stream()
                    .mapToInt(bone -> bone.getItem().getCount())
                    .sum();

            int skeletonsToSpawn = totalBones / 4;

            if (skeletonsToSpawn > 0) {
                int bonesToRemove = skeletonsToSpawn * 4;

                for (EntityItem nearbyBone : nearbyBones) {
                    ItemStack stack = nearbyBone.getItem();
                    int count = stack.getCount();

                    if (count > bonesToRemove) {
                        stack.shrink(bonesToRemove);
                        bonesToRemove = 0;
                    } else {
                        bonesToRemove -= count;
                        nearbyBone.setDead();
                    }

                    if (bonesToRemove <= 0) {
                        break;
                    }
                }

                for (int i = 0; i < skeletonsToSpawn; i++) {
                    ReviveSkeletonEntity reviveSkeleton = new ReviveSkeletonEntity(world);
                    reviveSkeleton.setPosition(entityItem.posX, entityItem.posY, entityItem.posZ);
                    world.spawnEntity(reviveSkeleton);
                }

                entityItem.setDead();
            }
        }

        return super.onEntityItemUpdate(entityItem);
    }
}
