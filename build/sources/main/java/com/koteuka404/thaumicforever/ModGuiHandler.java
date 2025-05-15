package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {
    public static final int DECONSTRUCTION_TABLE_GUI   = 0;
    public static final int CHEST_GUI_ID               = 1;
    public static final int DOUBLE_TABLE_GUI           = 2;
    public static final int CRIMSON_BOOK_GUI           = 3;
    public static final int GUI_ID_MATTERY_DUPLICATOR  = 4;
    public static final int GUI_ID_COMPRESSOR          = 5;
    public static final int GUI_ID_REPURPOSER          = 6;
    public static final int GUI_BAUBLES                = 7;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        InventoryPlayer inv = player.inventory;
        TileEntity te       = world.getTileEntity(new BlockPos(x, y, z));

        if (id == DECONSTRUCTION_TABLE_GUI && te instanceof DeconstructionTableTileEntity) {
            return new DeconstructionTableContainer(inv, (DeconstructionTableTileEntity) te);
        } 
        else if (id == CHEST_GUI_ID && te instanceof TileEntityAbandonedChest) {
            return new ContainerAbandonedChest(inv, (TileEntityAbandonedChest) te);
        }
        else if (id == DOUBLE_TABLE_GUI && te instanceof DoubleTableTileEntity) {
            return new DoubleTableContainer(inv, (DoubleTableTileEntity) te);
        }
        else if (id == GUI_ID_MATTERY_DUPLICATOR && te instanceof TileEntityMatteryDuplicator) {
            return new ContainerMatteryDuplicator(inv, (TileEntityMatteryDuplicator) te);
        }
        else if (id == GUI_ID_COMPRESSOR && te instanceof TileEntityCompressor) {
            return new ContainerCompressor(inv, (TileEntityCompressor) te);
        }
        else if (id == GUI_ID_REPURPOSER && te instanceof TileEntityRepurposer) {
            return new ContainerRepurposer(inv, (TileEntityRepurposer) te);
        }
        else if (id == GUI_BAUBLES) {
            return new ContainerMysticTabExample(player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        if (id == DECONSTRUCTION_TABLE_GUI && te instanceof DeconstructionTableTileEntity) {
            return new DeconstructionTableGui(player.inventory, (DeconstructionTableTileEntity) te);
        } 
        else if (id == CHEST_GUI_ID && te instanceof TileEntityAbandonedChest) {
            return new ChestGui(player.inventory, (TileEntityAbandonedChest) te);
        }
        else if (id == DOUBLE_TABLE_GUI && te instanceof DoubleTableTileEntity) {
            return new DoubleTableGui(player.inventory, (DoubleTableTileEntity) te);
        }
        else if (id == GUI_ID_MATTERY_DUPLICATOR && te instanceof TileEntityMatteryDuplicator) {
            return new GuiMatteryDuplicator(player.inventory, (TileEntityMatteryDuplicator) te);
        }
        else if (id == GUI_ID_COMPRESSOR && te instanceof TileEntityCompressor) {
            return new GuiCompressor(player.inventory, (TileEntityCompressor) te);
        }
        else if (id == GUI_ID_REPURPOSER && te instanceof TileEntityRepurposer) {
            return new GuiRepurposer(player.inventory, (TileEntityRepurposer) te);
        }
        else if (id == GUI_BAUBLES) {
            return new GuiMysticTabExample(player.inventory);
        }
        return null;
    }
}
