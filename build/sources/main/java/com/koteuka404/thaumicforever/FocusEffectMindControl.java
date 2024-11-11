package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectMindControl extends FocusEffect {

    @Override
    public String getResearch() {
        return "MINDCONTROL";
    }

    @Override
    public String getKey() {
        return "thaumicforever.MINDCONTROL";
    }

    @Override
    public int getComplexity() {
        return 11; // Складність фокусу
    }

    @Override
    public Aspect getAspect() {
        return Aspect.AVERSION; // Вибір аспекту, який підходить для фокусу "Контроль розуму"
    }

    @Override
    public boolean execute(RayTraceResult rayTraceResult, Trajectory trajectory, float power, int duration) {
        Entity target = rayTraceResult.entityHit;

        if (target instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) target;
            if (entity instanceof EntityMob) {
                applyMindControl((EntityMob) entity);
                return true; // Повертаємо true, якщо магія була виконана успішно
            }
        }
        return false; // Повертаємо false, якщо магія не спрацювала
    }

    private void applyMindControl(EntityMob entity) {
        // Логіка контролю над розумом, щоб змусити моба атакувати інших
        List<Entity> nearbyEntities = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getEntityBoundingBox().grow(10));

        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof EntityLiving && nearbyEntity != entity) {
                entity.setAttackTarget((EntityLiving) nearbyEntity); // Встановлюємо нову ціль для атаки
            }
        }
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[0]; // Параметри для налаштування фокусу
    }

    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        // Візуальний ефект при використанні фокусу (можна налаштувати за бажанням)
    }
}
