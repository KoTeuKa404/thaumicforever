package com.koteuka404.thaumicforever;

import java.io.File;
import java.io.FileOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

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
            if (player.isSneaking()) {
                secondCorner = player.getPosition();
                player.sendMessage(new TextComponentString("Second corner set at: " + secondCorner));
                if (firstCorner != null) {
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

        BlockPos startPos = new BlockPos(minX, minY, minZ);
        BlockPos size = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);

        MinecraftServer server = world.getMinecraftServer();
        if (server == null) {
            player.sendMessage(new TextComponentString("Failed to access Minecraft server."));
            return;
        }

        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = new Template();

        try {
            // Capture blocks from the world into the template
            template.takeBlocksFromWorld(world, startPos, size, true, null);

            // Define the name and location for the template
            ResourceLocation templateName = new ResourceLocation("thaumicforever", "saved_structure");

            // Save the Template
            File saveDir = new File(server.getWorld(0).getSaveHandler().getWorldDirectory(), "structures");
            if (!saveDir.exists() && !saveDir.mkdirs()) {
                player.sendMessage(new TextComponentString("Failed to create structures directory!"));
                return;
            }

            File structureFile = new File(saveDir, templateName.getResourcePath() + ".nbt");

            // Write the structure to a file
            NBTTagCompound nbt = template.writeToNBT(new NBTTagCompound());
            try (FileOutputStream fos = new FileOutputStream(structureFile)) {
                CompressedStreamTools.writeCompressed(nbt, fos);
            }

            player.sendMessage(new TextComponentString("Structure saved successfully as: " + structureFile.getAbsolutePath()));
        } catch (Exception e) {
            player.sendMessage(new TextComponentString("Unexpected error occurred: " + e.getMessage()));
            e.printStackTrace();
        }

        firstCorner = null;
        secondCorner = null;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
