package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusMediumOrbitSphere extends FocusMedium {

    @Override
    public String getResearch() {
        return "FOCUSORBSPHERE"; // Назва дослідження для цього фокуса
    }

    @Override
    public String getKey() {
        return "thaumicforever.ORBSPHERE"; // Унікальний ключ для цього фокуса
    }

    @Override
    public Aspect getAspect() {
        return Aspect.AIR; // Магічний аспект для цього фокуса
    }

    @Override
    public int getComplexity() {
        return 5 + getSettingValue("radius") * 2; // Складність залежить від радіусу
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TRAJECTORY  }; // Заміна POSITION на TARGET
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        // Отримуємо гравця або кастера як ціль
        Entity caster = getPackage().getCaster();
        if (caster instanceof EntityLivingBase) {
            EntityLivingBase player = (EntityLivingBase) caster;
            
            // Запускаємо створення сфери навколо гравця
            createOrbitSphere(player, getSettingValue("radius"));
            
            return true;
        }
        return false;
    }

    @Override
    public boolean hasIntermediary() {
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("radius", "focus.common.radius", new NodeSetting.NodeSettingIntRange(3, 5)) // Радіус обертання
        };
    }

    @Override
    public float getPowerMultiplier() {
        return 1.0f;
    }

    // Метод для створення сфери навколо гравця і виконання наступних фокусів
    private void createOrbitSphere(EntityLivingBase player, int radius) {
        World world = player.world;
        Vec3d playerPos = player.getPositionVector();
        double angleStep = Math.PI / 10; // Кутовий крок для обертання сфери

        // Цикл по кутах для створення сферичної візуалізації
        for (double theta = 0; theta < 2 * Math.PI; theta += angleStep) {
            for (double phi = 0; phi < Math.PI; phi += angleStep) {
                // Обчислюємо координати частинки у сфері навколо гравця
                double x = playerPos.x + radius * Math.sin(phi) * Math.cos(theta);
                double y = playerPos.y + radius * Math.cos(phi);
                double z = playerPos.z + radius * Math.sin(phi) * Math.sin(theta);
                
                // Додаємо частинки у світ для створення сфери
                world.spawnParticle(EnumParticleTypes.SPELL_WITCH, x, y, z, 0, 0, 0);
                
                // Викликаємо наступний фокус в цій точці
                executeNextFocus(new Vec3d(x, y, z), player);
            }
        }
    }

   // Виконує наступний фокус у ланцюжку
    private void executeNextFocus(Vec3d position, EntityLivingBase player) {
        // Створюємо траєкторію для наступного фокуса на основі поточної позиції
        Trajectory trajectory = new Trajectory(position, new Vec3d(0, 0, 0));

        // Перевіряємо, чи є наступний фокус у ланцюжку
        if (getRemainingPackage() != null) {
            // Додаємо порожній масив RayTraceResult[] як третій параметр
            FocusEngine.runFocusPackage(getRemainingPackage(), new Trajectory[] { trajectory }, new RayTraceResult[0]);
        }
    }

}
