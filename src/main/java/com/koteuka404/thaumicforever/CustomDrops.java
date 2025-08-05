package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;

public class CustomDrops {

    private static final Random RANDOM = new Random();


    @SubscribeEvent
    public void onBlockHarvest(HarvestDropsEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayer player = event.getHarvester();

        if (world.isRemote || player == null) {
            return; 
        }

        if (event.getState().getBlock() == BlocksTC.stonePorous) {
            if (RANDOM.nextFloat() < 0.015f) {
                event.getDrops().add(new ItemStack(ModItems.stone));
            }
        }
        if (event.getState().getBlock() == Blocks.LEAVES) {

            if (event.getState().getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.JUNGLE) {
                if (RANDOM.nextFloat() < 0.15f) { 
                    event.getDrops().add(new ItemStack(ModItems.banana, 1));
                }
            }
        }
        if (event.getState().getBlock().getRegistryName() != null&& "thaumicbases:enderleaves".equals(event.getState().getBlock().getRegistryName().toString())) {
            float dropChance = 0.1f; 
            if (RANDOM.nextFloat() < dropChance) {
                event.getDrops().add(new ItemStack(Items.ENDER_PEARL, 1));
            }
        }
    }

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        World world = event.getEntity().getEntityWorld();
        Entity entity = event.getEntity();
        EntityPlayer attacker = null;

        if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            attacker = (EntityPlayer) event.getSource().getTrueSource();
        }

        if (world.isRemote) {
            return; 
        }

        if (entity instanceof EntityThaumicSlime) {
            int size = ((EntityThaumicSlime) entity).getSlimeSize();
            for (int i = 0; i < size; i++) {
                EntityItem drop = new EntityItem(world, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.taint_slime));
                event.getDrops().add(drop);
            }
        }

        if (entity instanceof EntityCultist || entity instanceof EntityCultistCleric || entity instanceof EntityCultistKnight) {
            float luck = (attacker != null) ? attacker.getLuck() : 0;
            float dropChance = 0.1f + (luck * 0.04f); 
            
            if (RANDOM.nextFloat() < dropChance) {
                EntityItem drop = new EntityItem(world, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.lootbag));
                event.getDrops().add(drop);
            }
        }

        boolean isGiantTaintacle = entity instanceof EntityTaintacleGiant;
        boolean isOvergrownTaintacle = false;

        try {
            Class<?> overgrownClass = Class.forName("mod.icarus.crimsonrevelations.entity.boss.EntityOvergrownTaintacle");
            isOvergrownTaintacle = overgrownClass.isInstance(entity);
        } catch (ClassNotFoundException ignored) {
        }

        if (isGiantTaintacle || isOvergrownTaintacle) {
            if (RANDOM.nextFloat() < 1.0f) {
                EntityItem drop = new EntityItem(world, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.orb_of_taint));
                event.getDrops().add(drop);
            }
        }

    
        if (entity instanceof EntityGorilla && attacker != null) {
            int looting = event.getLootingLevel();
            if (looting >= 3) {
                EntityItem drop = new EntityItem(world, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.hand));
                event.getDrops().add(drop);
            }
        }

    }

}
