package com.koteuka404.thaumicforever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
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
            BlockPos spawnPos = player.getPosition().up();
            File structureFile = new File(DimensionManager.getCurrentSaveRootDirectory(), "structures/saved_structure.nbt");

            if (!structureFile.exists()) {
                player.sendMessage(new TextComponentString("Structure file not found!"));
                return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
            }

            try {
                NBTTagCompound structureData = CompressedStreamTools.readCompressed(new FileInputStream(structureFile));

                spawnBlocks(world, spawnPos, structureData);
                spawnTileEntities(world, spawnPos, structureData);
                spawnEntities(world, spawnPos, structureData);

                player.sendMessage(new TextComponentString("Structure spawned successfully!"));
            } catch (IOException e) {
                player.sendMessage(new TextComponentString("Failed to read structure file: " + e.getMessage()));
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    private void spawnBlocks(World world, BlockPos spawnPos, NBTTagCompound structureData) {
        NBTTagList blockList = structureData.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < blockList.tagCount(); i++) {
            NBTTagCompound blockData = blockList.getCompoundTagAt(i);
            int x = blockData.getInteger("x");
            int y = blockData.getInteger("y");
            int z = blockData.getInteger("z");
            String blockName = blockData.getString("block");

            IBlockState state = net.minecraft.block.Block.getBlockFromName(blockName).getDefaultState();
            BlockPos blockPos = spawnPos.add(x, y, z);
            world.setBlockState(blockPos, state, 3);
        }
    }

    private void spawnTileEntities(World world, BlockPos spawnPos, NBTTagCompound structureData) {
        NBTTagList tileEntityList = structureData.getTagList("tile_entities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tileEntityList.tagCount(); i++) {
            NBTTagCompound tileEntityData = tileEntityList.getCompoundTagAt(i);
            int x = tileEntityData.getInteger("x");
            int y = tileEntityData.getInteger("y");
            int z = tileEntityData.getInteger("z");

            BlockPos tilePos = spawnPos.add(x, y, z);
            TileEntity tileEntity = world.getTileEntity(tilePos);
            if (tileEntity != null) {
                tileEntity.readFromNBT(tileEntityData);
                tileEntity.markDirty();
            }
        }
    }

    private void spawnEntities(World world, BlockPos spawnPos, NBTTagCompound structureData) {
        NBTTagList entityList = structureData.getTagList("entities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entityList.tagCount(); i++) {
            NBTTagCompound entityData = entityList.getCompoundTagAt(i);
            double x = entityData.getDouble("x") + spawnPos.getX();
            double y = entityData.getDouble("y") + spawnPos.getY();
            double z = entityData.getDouble("z") + spawnPos.getZ();

            Entity entity = EntityList.createEntityFromNBT(entityData, world);
            if (entity != null) {
                entity.setPosition(x, y, z);
                world.spawnEntity(entity);
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
