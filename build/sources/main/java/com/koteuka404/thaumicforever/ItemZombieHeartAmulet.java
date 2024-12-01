package com.koteuka404.thaumicforever;

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
        EntityGiantBrainyZombie giantZombie = new EntityGiantBrainyZombie(world);
        giantZombie.setPosition(player.posX, player.posY, player.posZ); 
        giantZombie.targetTasks.addTask(1, new EntityAIBase() {
            @Override
            public boolean shouldExecute() {
                return giantZombie.getAttackTarget() != null;
            }

            @Override
            public void updateTask() {
                if (giantZombie.getAttackTarget() instanceof EntityPlayer) {
                    EntityPlayer target = (EntityPlayer) giantZombie.getAttackTarget();
                    if (isPlayerWearingAmulet(target)) {
                        giantZombie.setAttackTarget(null);
                    }
                }
            }

            @Override
            public boolean shouldContinueExecuting() {
                return false;  
            }
        });

        world.spawnEntity(giantZombie);
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);  
    }
}

private boolean isPlayerWearingAmulet(EntityPlayer player) {
    for (ItemStack stack : player.inventory.mainInventory) {
        if (stack.getItem() instanceof ItemZombieHeartAmulet) {
            return true;
        }
    }
    return false;
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
