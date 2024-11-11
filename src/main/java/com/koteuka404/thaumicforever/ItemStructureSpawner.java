package com.koteuka404.thaumicforever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
import net.minecraftforge.common.util.Constants;

public class ItemStructureSpawner extends Item {
    public ItemStructureSpawner() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("structure_spawner");
        this.setRegistryName("structure_spawner");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem(hand);
            BlockPos spawnPos = player.getPosition().up();

            File structureFile = new File(DimensionManager.getCurrentSaveRootDirectory(), "structures/saved_structure.nbt");
            if (!structureFile.exists()) {
                player.sendMessage(new TextComponentString("Structure file not found!"));
                return new ActionResult<>(EnumActionResult.FAIL, heldItem);
            }

            try {
                NBTTagCompound structureData = CompressedStreamTools.readCompressed(new FileInputStream(structureFile));
                NBTTagList blockList = structureData.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
                int sizeX = structureData.getInteger("sizeX");
                int sizeY = structureData.getInteger("sizeY");
                int sizeZ = structureData.getInteger("sizeZ");

                for (int i = 0; i < blockList.tagCount(); i++) {
                    NBTTagCompound blockData = blockList.getCompoundTagAt(i);
                    int x = blockData.getInteger("x");
                    int y = blockData.getInteger("y");
                    int z = blockData.getInteger("z");
                    String blockName = blockData.getString("block");
                    int meta = blockData.getInteger("meta");

                    BlockPos blockPos = spawnPos.add(x, y, z);
                    world.setBlockState(blockPos, net.minecraft.block.Block.getBlockFromName(blockName).getStateFromMeta(meta), 3);
                }

                player.sendMessage(new TextComponentString("Structure spawned successfully!"));
            } catch (IOException e) {
                player.sendMessage(new TextComponentString("Failed to read structure file: " + e.getMessage()));
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; 
    }
}
