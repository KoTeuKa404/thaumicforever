package com.koteuka404.thaumicforever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ItemStructureSaver extends Item {
    private BlockPos firstCorner = null;
    private BlockPos secondCorner = null;

    public ItemStructureSaver() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("structure_saver");
        this.setRegistryName("structure_saver");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (player.isSneaking()) {
                secondCorner = player.getPosition();
                player.sendMessage(new TextComponentString("Second corner set at: " + secondCorner));
                if (firstCorner != null && secondCorner != null) {
                    saveStructure(world, player);
                }
            } else {
                firstCorner = player.getPosition();
                player.sendMessage(new TextComponentString("First corner set at: " + firstCorner));
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    private void saveStructure(World world, EntityPlayer player) {
        if (firstCorner == null || secondCorner == null) {
            player.sendMessage(new TextComponentString("Both corners must be set to save the structure!"));
            return;
        }

        int minX = Math.min(firstCorner.getX(), secondCorner.getX());
        int minY = Math.min(firstCorner.getY(), secondCorner.getY());
        int minZ = Math.min(firstCorner.getZ(), secondCorner.getZ());
        int maxX = Math.max(firstCorner.getX(), secondCorner.getX());
        int maxY = Math.max(firstCorner.getY(), secondCorner.getY());
        int maxZ = Math.max(firstCorner.getZ(), secondCorner.getZ());

        NBTTagCompound structureData = new NBTTagCompound();
        NBTTagList blockList = new NBTTagList();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(pos);
                    NBTTagCompound blockData = new NBTTagCompound();
                    blockData.setInteger("x", x - minX);
                    blockData.setInteger("y", y - minY);
                    blockData.setInteger("z", z - minZ);
                    blockData.setString("block", state.getBlock().getRegistryName().toString());
                    blockData.setInteger("meta", state.getBlock().getMetaFromState(state));
                    blockList.appendTag(blockData);
                }
            }
        }

        structureData.setTag("blocks", blockList);
        structureData.setInteger("sizeX", maxX - minX + 1);
        structureData.setInteger("sizeY", maxY - minY + 1);
        structureData.setInteger("sizeZ", maxZ - minZ + 1);

        File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "structures");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File structureFile = new File(saveDir, "saved_structure.nbt");

        try {
            CompressedStreamTools.writeCompressed(structureData, new FileOutputStream(structureFile));
            player.sendMessage(new TextComponentString("Structure saved successfully at: " + structureFile.getAbsolutePath()));
        } catch (IOException e) {
            player.sendMessage(new TextComponentString("Failed to save structure: " + e.getMessage()));
        }

        firstCorner = null;
        secondCorner = null;
    }
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; // Амулет буде мати візуальний ефект (як наче він зачарований)
    }
}
