package com.tutorialmod.turtywurty;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;

public class ItemZombieHeartAmulet extends Item implements IBauble {

    public ItemZombieHeartAmulet() {
        setUnlocalizedName("zombie_heart_amulet");
        setRegistryName("zombie_heart_amulet");
        setMaxStackSize(1);
    }

    // Вказуємо, що амулет буде працювати в слоті амулетів Baubles
    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    // Визивається кожен раз, коли гравець носить амулет у слоті Baubles
    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
            EntityPlayer player = (EntityPlayer) entity;

            // Якщо здоров'я гравця менше або дорівнює 4 HP (2 серця)
            if (player.getHealth() <= 4.0F) {
                summonGiantZombie(player, player.world);  // Призиваємо гігантського зомбі
                replaceWithBrokenAmulet(stack, player); // Заміна амулета на поламаний амулет
            }
        }
    }

    // Метод для ручного виклику зомбі за допомогою правої кнопки миші
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            summonGiantZombie(player, world);  // Призиваємо зомбі вручну
            replaceWithBrokenAmulet(stack, player);  // Заміна амулета на поламаний амулет
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    // Призов зомбі
    private void summonGiantZombie(EntityPlayer player, World world) {
        if (!world.isRemote) {
            EntityGiantBrainyZombie giantZombie = new EntityGiantBrainyZombie(world);
            giantZombie.setPosition(player.posX, player.posY, player.posZ);  // Призиваємо зомбі біля гравця
            world.spawnEntity(giantZombie);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);  // Звук
        }
    }

    // Заміна амулета на поламаний амулет після використання
    private void replaceWithBrokenAmulet(ItemStack stack, EntityPlayer player) {
        stack.shrink(1);  // Видаляємо амулет

        // Додаємо "поламаний амулет" у інвентар гравця
        ItemStack brokenAmulet = new ItemStack(ModItems.BROKEN_AMULET);  // Потрібно зареєструвати "поламаний амулет"
        if (!player.inventory.addItemStackToInventory(brokenAmulet)) {
            player.dropItem(brokenAmulet, false);  // Якщо інвентар повний, викидаємо на землю
        }
    }
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE; // Встановлення рідкості на EPIC
    }
}
