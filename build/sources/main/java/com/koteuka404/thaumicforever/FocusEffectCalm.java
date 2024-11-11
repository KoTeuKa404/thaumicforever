package com.koteuka404.thaumicforever;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

public class FocusEffectCalm extends FocusEffect {

    private int durationInTicks = 0;
    private EntityLiving mob = null;

    @Override
    public String getResearch() {
        return "FOCUSCALM";  // Назва дослідження
    }

    @Override
    public String getKey() {
        return "thaumicforever.CALM";  // Унікальний ключ
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MIND;  // Використовує аспект розуму
    }

    @Override
    public int getComplexity() {
        return getSettingValue("duration") / 2 + 3;
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.entityHit instanceof EntityLiving) {
            mob = (EntityLiving) target.entityHit;
            EntityLivingBase caster = getPackage().getCaster();

            if (caster instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) caster;

                // Тривалість ефекту
                durationInTicks = getSettingValue("duration") * 20;  // Конвертуємо в тік (20 тіків = 1 секунда)

                // Регіструємо періодичне оновлення через TickEvent
                MinecraftForge.EVENT_BUS.register(this);

                // Додаємо початкові ефекти (наприклад, сліпоту та сповільнення)


                return true;
            }
        }
        return false;
    }

    // Подія оновлення кожен тік
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (durationInTicks > 0 && mob != null && !mob.isDead) {
            // Скидаємо агресію кожен тік
            mob.setAttackTarget(null);
            mob.setRevengeTarget(null);
            mob.getNavigator().clearPath();

            // Зменшуємо тривалість
            durationInTicks--;

            if (durationInTicks <= 0) {
                // Коли ефект завершився, скидаємо реєстрацію
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] duration = { 3, 5, 7, 9, 11, 13, 15 };  // Тривалість дії (секунди)
        String[] durationDesc = { "3s", "5s", "7s", "9s", "11s", "13s", "15s" };
        
        return new NodeSetting[] {
            new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntList(duration, durationDesc))
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        // Можеш залишити цей метод порожнім або додати візуальні ефекти
    }
}
