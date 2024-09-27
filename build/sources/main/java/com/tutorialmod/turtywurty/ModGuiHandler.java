package com.tutorialmod.turtywurty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModGuiHandler implements IGuiHandler {
    public static final int DECONSTRUCTION_TABLE_GUI = 0;
    public static final int CHEST_GUI_ID = 1;

    public ModGuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ThaumicForever.instance, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == DECONSTRUCTION_TABLE_GUI && tileEntity instanceof DeconstructionTableTileEntity) {
            return new DeconstructionTableContainer(player.inventory, (IInventory) tileEntity);
        } else if (ID == CHEST_GUI_ID && tileEntity instanceof TileEntityAbandonedChest) {
            return new ContainerAbandonedChest(player.inventory, (TileEntityAbandonedChest) tileEntity);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == DECONSTRUCTION_TABLE_GUI && tileEntity instanceof DeconstructionTableTileEntity) {
            return new DeconstructionTableGui(player.inventory, (IInventory) tileEntity);
        } else if (ID == CHEST_GUI_ID && tileEntity instanceof TileEntityAbandonedChest) {
            return new ChestGui(player.inventory, (TileEntityAbandonedChest) tileEntity);
        }
        return null;
    }
}
