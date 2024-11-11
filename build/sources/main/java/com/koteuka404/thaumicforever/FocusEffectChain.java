package com.koteuka404.thaumicforever;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectChain extends FocusEffect {

    private static final int FLIGHT_DISABLE_DURATION_TICKS = 200; // 10 секунд = 200 тактів

    @Override
    public String getResearch() {
        return "CHAINFLIGHT"; // Дослідження для доступу до цього заклинання
    }

    @Override
    public String getKey() {
        return "thaumicforever.CHAINFLIGHT"; // Унікальний ключ для цього заклинання
    }

    @Override
    public int getComplexity() {
        return 15; // Складність у 15 одиниць
    }

    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        // Реалізація для відображення частинок. Якщо частинки не потрібні, цей метод можна залишити порожнім.
    }

    @Override
    public boolean execute(RayTraceResult rayTraceResult, Trajectory trajectory, float power, int potency) {
        if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) rayTraceResult.entityHit;

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;

                // Перевіряємо, чи гравець у польоті, і вимикаємо його
                if (player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();

                    // Додаємо відстеження тактів для відновлення здатності до польоту
                    MinecraftForge.EVENT_BUS.register(new Object() {
                        int ticks = 0;

                        // Обробник подій на кожен тік
                        @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
                        public void onTick(TickEvent.PlayerTickEvent event) {
                            if (event.player == player) {
                                ticks++;
                                if (ticks >= FLIGHT_DISABLE_DURATION_TICKS) {
                                    // Відновлюємо можливість польоту через 200 тактів
                                    player.capabilities.allowFlying = true;
                                    player.sendPlayerAbilities();
                                    // Відключаємо обробник подій після завершення
                                    MinecraftForge.EVENT_BUS.unregister(this);
                                }
                            }
                        }
                    });
                }
            }
        }
        return true;
    }

    @Override
    public Aspect getAspect() {
        return Aspect.TRAP; // Використовуємо Aspect.TRAP для цього ефекту
    }
}
