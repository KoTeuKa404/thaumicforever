package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.registry.ModItems;

import java.util.UUID;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
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
    private static final String TAG_AMULET_OWNER = "TFZombieHeartOwner";

    public ItemZombieHeartAmulet() {
        setUnlocalizedName("zombie_heart_amulet");
        setRegistryName("zombie_heart_amulet");
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
            EntityPlayer player = (EntityPlayer) entity;

            if (player.getHealth() <= 4.0F) {
                summonGiantZombie(player, player.world); 
                replaceWithBrokenAmulet(stack, player); 
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            summonGiantZombie(player, world); 
            replaceWithBrokenAmulet(stack, player); 
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void summonGiantZombie(EntityPlayer player, World world) {
        if (!world.isRemote) {
            final EntityGiantBrainyZombie giantZombie = new EntityGiantBrainyZombie(world);
            giantZombie.setPosition(player.posX, player.posY, player.posZ);
            giantZombie.setAttackTarget(null);
            giantZombie.setRevengeTarget(null);

            giantZombie.getEntityData().setUniqueId(TAG_AMULET_OWNER, player.getUniqueID());

            // Hard guard: this summoned zombie must never target its owner.
            giantZombie.targetTasks.addTask(0, new EntityAIBase() {
                @Override
                public boolean shouldExecute() {
                    return true;
                }

                @Override
                public boolean shouldContinueExecuting() {
                    return true;
                }

                @Override
                public void updateTask() {
                    UUID ownerId = null;
                    try {
                        if (giantZombie.getEntityData().hasUniqueId(TAG_AMULET_OWNER)) {
                            ownerId = giantZombie.getEntityData().getUniqueId(TAG_AMULET_OWNER);
                        }
                    } catch (Throwable ignored) {
                    }

                    if (ownerId == null) return;

                    EntityLivingBase attackTarget = giantZombie.getAttackTarget();
                    if (attackTarget instanceof EntityPlayer && ownerId.equals(attackTarget.getUniqueID())) {
                        giantZombie.setAttackTarget(null);
                    }

                    EntityLivingBase revengeTarget = giantZombie.getRevengeTarget();
                    if (revengeTarget instanceof EntityPlayer && ownerId.equals(revengeTarget.getUniqueID())) {
                        giantZombie.setRevengeTarget(null);
                    }
                }
            });

            world.spawnEntity(giantZombie);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    
    


    private void replaceWithBrokenAmulet(ItemStack stack, EntityPlayer player) {
        stack.shrink(1);   

        ItemStack brokenAmulet = new ItemStack(ModItems.BROKEN_AMULET);  
        if (!player.inventory.addItemStackToInventory(brokenAmulet)) {
            player.dropItem(brokenAmulet, false);  
        }
    }
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE; 
    }
}
