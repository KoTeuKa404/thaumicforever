package com.koteuka404.thaumicforever;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectCleanse extends FocusEffect {

    private int durationInTicks = 0;
    private EntityLivingBase targetEntity = null;

    @Override
    public String getResearch() {
        return "FOCUSCLEANSE";  // Назва дослідження для фокуса очищення
    }

    @Override
    public String getKey() {
        return "thaumicforever.CLEANSE";  // Унікальний ключ для фокуса очищення
    }

    @Override
    public Aspect getAspect() {
        return Aspect.ORDER;  // Використовує аспект порядку (Order)
    }

    @Override
    public int getComplexity() {
        // Складність фокуса визначається тривалістю
        return getSettingValue("duration") / 2 + 5;
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.entityHit instanceof EntityLivingBase) {
            targetEntity = (EntityLivingBase) target.entityHit;
            EntityLivingBase caster = getPackage().getCaster();

            if (caster instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) caster;

                // Тривалість очищення (в тиках)
                durationInTicks = getSettingValue("duration") * 20;

                // Очищення всіх негативних ефектів на початку
                cleanseNegativeEffects(targetEntity);

                // Реєструємо подію для кожного тіку (щоб очищення тривало протягом дії фокуса)
                MinecraftForge.EVENT_BUS.register(this);

                return true;
            }
        }
        return false;
    }

    // Метод для очищення всіх негативних ефектів
    private void cleanseNegativeEffects(EntityLivingBase entity) {
        for (PotionEffect effect : entity.getActivePotionEffects()) {
            if (effect.getPotion().isBadEffect()) {
                entity.removePotionEffect(effect.getPotion());  // Видаляємо негативний ефект
            }
        }
    }

    // Подія оновлення кожен тік
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (durationInTicks > 0 && targetEntity != null && !targetEntity.isDead) {
            cleanseNegativeEffects(targetEntity);  // Очищуємо негативні ефекти кожен тік
            durationInTicks--;

            if (durationInTicks <= 0) {
                // Скидаємо реєстрацію, коли тривалість завершилась
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] duration = { 3, 5, 7};  // Тривалість дії (секунди)
        String[] durationDesc = { "3s", "5s", "7s"};

        return new NodeSetting[] {
            new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntList(duration, durationDesc))
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        // Візуальні ефекти (якщо потрібно додати частинки для очищення)
    }
}
