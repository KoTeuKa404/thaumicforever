package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ZombieDropHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onEntityDrop(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();

        // GiantBrainyZombie - шанс  50%
        if (entity instanceof EntityGiantBrainyZombie) {
            if (RANDOM.nextFloat() <= 0.5) {
                event.getDrops().add(new net.minecraft.entity.item.EntityItem(
                    entity.world,
                    entity.posX,
                    entity.posY,
                    entity.posZ,
                    new ItemStack(ModItems.ItemZombieHeart)
                ));
            }
        }

        // BrainyZombie - шанс  10%
        else if (entity instanceof EntityBrainyZombie) {
            if (RANDOM.nextFloat() <= 0.1) {
                event.getDrops().add(new net.minecraft.entity.item.EntityItem(
                    entity.world,
                    entity.posX,
                    entity.posY,
                    entity.posZ,
                    new ItemStack(ModItems.ItemZombieHeart)
                ));
            }
        }
    }
}
