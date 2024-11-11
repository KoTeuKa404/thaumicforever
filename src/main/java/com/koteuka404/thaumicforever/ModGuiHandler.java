package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.IGuiHandler;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModGuiHandler implements IGuiHandler {

    public static final int DECONSTRUCTION_TABLE_GUI = 0;
    public static final int CHEST_GUI_ID = 1;
    public static final int DOUBLE_TABLE_GUI = 2;
    public static final int CRIMSON_BOOK_GUI = 3;
    public static final int GUI_ID_MATTERY_DUPLICATOR = 4;
    public static final int GUI_ID_COMPRESSOR = 5;
    public static final int GUI_ID_REPURPOSER = 6;

    public ModGuiHandler() {
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (ID == DECONSTRUCTION_TABLE_GUI && tileEntity instanceof DeconstructionTableTileEntity) {
            return new DeconstructionTableContainer(player.inventory, (IInventory) tileEntity);
        } else if (ID == CHEST_GUI_ID && tileEntity instanceof TileEntityAbandonedChest) {
            return new ContainerAbandonedChest(player.inventory, (TileEntityAbandonedChest) tileEntity);
        } else if (ID == DOUBLE_TABLE_GUI) {
            return new DoubleTableContainer(player.inventory);
        } else if (ID == CRIMSON_BOOK_GUI) {
            return null;
        } else if (ID == GUI_ID_MATTERY_DUPLICATOR && tileEntity instanceof TileEntityMatteryDuplicator) {
            return new ContainerMatteryDuplicator(player.inventory, (TileEntityMatteryDuplicator) tileEntity);
        } else if (ID == GUI_ID_COMPRESSOR && tileEntity instanceof TileEntityCompressor) {
            return new ContainerCompressor(player.inventory, (TileEntityCompressor) tileEntity);
        } else if (ID == GUI_ID_REPURPOSER && tileEntity instanceof TileEntityRepurposer) {
            return new ContainerRepurposer(player.inventory, (TileEntityRepurposer) tileEntity);
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
        } else if (ID == DOUBLE_TABLE_GUI) {
            return new DoubleTableGui(new DoubleTableContainer(player.inventory));
        } else if (ID == CRIMSON_BOOK_GUI) {
            return new GuiCrimsonBook();
        } else if (ID == GUI_ID_MATTERY_DUPLICATOR && tileEntity instanceof TileEntityMatteryDuplicator) {
            return new GuiMatteryDuplicator(player.inventory, (TileEntityMatteryDuplicator) tileEntity);
        } else if (ID == GUI_ID_COMPRESSOR && tileEntity instanceof TileEntityCompressor) {
            return new GuiCompressor(player.inventory, (TileEntityCompressor) tileEntity);
        } else if (ID == GUI_ID_REPURPOSER && tileEntity instanceof TileEntityRepurposer) {
            return new GuiRepurposer(player.inventory, (TileEntityRepurposer) tileEntity);
        }

        return null;
    }
}
