package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;

public class GorillaHand extends Item {

    public GorillaHand() {
        super();
        this.setMaxStackSize(1);
        this.setRegistryName("monkey_paw");
        this.setUnlocalizedName("monkey_paw");
        this.setCreativeTab(ThaumicForever.CREATIVE_TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            int[] possibleActions = {1, 2, 4, 6};
            int action = possibleActions[new Random().nextInt(possibleActions.length)];
            nbt.setInteger("action", action);

            switch (action) {
                case 1:
                    handleGiveResearch(player);
                    break;
                case 2:
                    spawnMobs(world, player);
                    player.sendMessage(new TextComponentString("The trial has begun!"));
                    break;
                case 4:
                    handleResetWarp(player);
                    break;
                case 6:
                    handleSpectatorMode(player);
                    break;
            }

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }


    private void handleGiveResearch(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            player.sendMessage(new TextComponentString("Only a server-side player can receive research."));
            return;
        }
        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);

        knowledge.sync(serverPlayer);

        List<ResearchEntry> candidateEntries = getAvailableResearch(knowledge);

        if (candidateEntries.isEmpty()) {
            forceResearchProgress(serverPlayer, knowledge);
            knowledge.sync(serverPlayer);
            candidateEntries = getAvailableResearch(knowledge);
            if (candidateEntries.isEmpty()) {
                player.sendMessage(new TextComponentString("No available research to unlock (even after forcing)."));
                return;
            }
        }

        int count = new Random().nextInt(3) + 2; 
        count = Math.min(count, candidateEntries.size());
        Collections.shuffle(candidateEntries, new Random());
        List<String> gainedResearchNames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ResearchEntry entry = candidateEntries.get(i);
            gainedResearchNames.add(entry.getLocalizedName());
            ThaumcraftApi.internalMethods.completeResearch(serverPlayer, entry.getKey());
        }
        player.sendMessage(new TextComponentString("You have gained research: " + gainedResearchNames));
    }


    private List<ResearchEntry> getAvailableResearch(IPlayerKnowledge knowledge) {
        List<ResearchEntry> result = new ArrayList<>();
        if (!ResearchCategories.researchCategories.isEmpty()) {
            for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
                for (ResearchEntry entry : cat.research.values()) {
                    if (knowledge.isResearchKnown(entry.getKey()) && !knowledge.isResearchComplete(entry.getKey())) {
                        result.add(entry);
                    }
                }
            }
        }
        return result;
    }

    private void forceResearchProgress(EntityPlayerMP serverPlayer, IPlayerKnowledge knowledge) {
        for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry entry : cat.research.values()) {
                if (!knowledge.isResearchComplete(entry.getKey())) {
                    ThaumcraftApi.internalMethods.progressResearch(serverPlayer, entry.getKey());
                }
            }
        }
    }


    private void spawnMobs(World world, EntityPlayer player) {
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = 8 + rand.nextDouble() * 4; 
            double spawnX = player.posX + Math.cos(angle) * distance;
            double spawnZ = player.posZ + Math.sin(angle) * distance;
            double spawnY = player.posY;

            EntityZombie zombie = new EntityZombie(world);
            zombie.setPosition(spawnX, spawnY, spawnZ);
            zombie.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            zombie.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            zombie.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
            zombie.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            world.spawnEntity(zombie);

            angle = rand.nextDouble() * 2 * Math.PI;
            distance = 8 + rand.nextDouble() * 4;
            spawnX = player.posX + Math.cos(angle) * distance;
            spawnZ = player.posZ + Math.sin(angle) * distance;

            EntitySkeleton skeleton = new EntitySkeleton(world);
            skeleton.setPosition(spawnX, spawnY, spawnZ);
            skeleton.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            skeleton.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            skeleton.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
            skeleton.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            world.spawnEntity(skeleton);
        }
    }

  
    private void handleResetWarp(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            player.sendMessage(new TextComponentString("Only a server-side player can reset warp."));
            return;
        }
        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        if (warp != null) {
            warp.set(IPlayerWarp.EnumWarpType.TEMPORARY, 0);
            warp.set(IPlayerWarp.EnumWarpType.NORMAL, 0);
            warp.set(IPlayerWarp.EnumWarpType.PERMANENT, 0);
            warp.sync(serverPlayer);
            player.sendMessage(new TextComponentString("All warp levels have been reset to 0."));
        } else {
            player.sendMessage(new TextComponentString("Warp capability not found."));
        }
    }


    private void handleSpectatorMode(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            player.sendMessage(new TextComponentString("Only a server-side player can be set to spectator mode."));
            return;
        }
        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        GameType originalGameType = serverPlayer.interactionManager.getGameType();
        double originalX = serverPlayer.posX;
        double originalY = serverPlayer.posY;
        double originalZ = serverPlayer.posZ;

        serverPlayer.setGameType(GameType.SPECTATOR);
        serverPlayer.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 600, 0, false, false));
        serverPlayer.sendMessage(new TextComponentString("You are now in Spectator mode for 30 seconds."));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                serverPlayer.setGameType(originalGameType);
                serverPlayer.setPositionAndUpdate(originalX, originalY, originalZ);
                serverPlayer.sendMessage(new TextComponentString("You have returned from Spectator mode."));
                timer.cancel();
            }
        }, 30000);
    }
}
