package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class VoidTeleportItem extends Item {

    public VoidTeleportItem() {
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;

            if (isInVoidDimension(playerMP)) {
                returnToSavedPosition(playerMP, stack);
            } else {
                savePlayerPosition(playerMP, stack);
                teleportToVoid(playerMP, stack);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private boolean isInVoidDimension(EntityPlayerMP player) {
        return player.dimension == ModDimensions.VOID_DIMENSION_ID;
    }

    private void savePlayerPosition(EntityPlayerMP player, ItemStack stack) {
        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();

        if (nbt != null) {
            nbt.setDouble("prevX", player.posX);
            nbt.setDouble("prevY", player.posY);
            nbt.setDouble("prevZ", player.posZ);
            nbt.setInteger("prevDim", player.dimension);
            stack.setTagCompound(nbt);
        }

        player.sendMessage(new TextComponentString("Saved your current position!"));
    }

    private void returnToSavedPosition(EntityPlayerMP player, ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
    
        if (nbt != null && nbt.hasKey("prevX") && nbt.hasKey("prevY") && nbt.hasKey("prevZ") && nbt.hasKey("prevDim")) {
            double prevX = nbt.getDouble("prevX");
            double prevY = nbt.getDouble("prevY");
            double prevZ = nbt.getDouble("prevZ");
            int prevDim = nbt.getInteger("prevDim");
    
            WorldServer targetWorld = player.getServer().getWorld(prevDim);
    
            if (targetWorld != null) {
                BlockPos targetPos = new BlockPos(prevX, prevY, prevZ);
                player.getServer().addScheduledTask(() -> {
                    player.changeDimension(prevDim, new TFCustomTeleporter(targetWorld, targetPos));
                    player.sendMessage(new TextComponentString("Returned to your saved position!"));
                });
            } else {
                player.sendMessage(new TextComponentString("Failed to find the target dimension!"));
            }
        } else {
            // If no saved position, teleport to spawn point
            BlockPos spawnPos = player.getBedLocation(player.dimension);
            boolean isBedSpawn = spawnPos != null;
    
            if (!isBedSpawn) {
                spawnPos = player.getServerWorld().getSpawnPoint();
            }
    
            BlockPos finalSpawnPos = spawnPos;
            int spawnDim = isBedSpawn ? player.dimension : 0;
    
            player.getServer().addScheduledTask(() -> {
                player.changeDimension(spawnDim, new TFCustomTeleporter(player.getServer().getWorld(spawnDim), finalSpawnPos));
                player.sendMessage(new TextComponentString(isBedSpawn
                    ? "No saved position found! Teleported to your bed spawn."
                    : "No saved position found! Teleported to the world spawn."));
            });
        }
    }
    

    private void teleportToVoid(EntityPlayerMP player, ItemStack stack) {
        WorldServer voidWorld = player.getServer().getWorld(ModDimensions.VOID_DIMENSION_ID);

        BlockPos structureCenter = PlayerStructureManager.getOrCreateStructureForPlayer(player.getUniqueID(), voidWorld);

        player.getServer().addScheduledTask(() -> {
            voidWorld.getChunkProvider().provideChunk(structureCenter.getX() >> 4, structureCenter.getZ() >> 4);
            player.changeDimension(ModDimensions.VOID_DIMENSION_ID, new TFCustomTeleporter(voidWorld, structureCenter));
            player.sendMessage(new TextComponentString("Teleported to the center of your Void structure!"));
        });
    }
}
