package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import com.koteuka404.thaumicforever.wand.client.gui.GuiArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.container.ContainerArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.tile.TileArcaneWorkbenchNew;
import net.minecraft.client.gui.inventory.GuiChest;
import com.koteuka404.thaumicforever.client.gui.ChestGui;
import com.koteuka404.thaumicforever.client.gui.DeconstructionTableGui;
import com.koteuka404.thaumicforever.client.gui.DoubleTableGui;
import com.koteuka404.thaumicforever.client.gui.GreatResearchTableGui;
import com.koteuka404.thaumicforever.client.gui.GuiCompressor;
import com.koteuka404.thaumicforever.client.gui.GuiMatteryDuplicator;
import com.koteuka404.thaumicforever.client.gui.GuiMysticTabExample;
import com.koteuka404.thaumicforever.client.gui.GuiPotionGun;
import com.koteuka404.thaumicforever.client.gui.GuiRepurposer;
import com.koteuka404.thaumicforever.container.ContainerAbandonedChest;
import com.koteuka404.thaumicforever.container.ContainerCompressor;
import com.koteuka404.thaumicforever.container.ContainerMatteryDuplicator;
import com.koteuka404.thaumicforever.container.ContainerMysticTabExample;
import com.koteuka404.thaumicforever.container.ContainerPotionGun;
import com.koteuka404.thaumicforever.container.ContainerPouch;
import com.koteuka404.thaumicforever.container.ContainerRepurposer;
import com.koteuka404.thaumicforever.inventory.InventoryPotionGun;
import com.koteuka404.thaumicforever.inventory.InventoryPouch;
import com.koteuka404.thaumicforever.item.ItemPotionGun;
import com.koteuka404.thaumicforever.item.ItemPouch;
import com.koteuka404.thaumicforever.tile.DeconstructionTableTileEntity;
import com.koteuka404.thaumicforever.tile.DoubleTableTileEntity;
import com.koteuka404.thaumicforever.tile.TileEntityAbandonedChest;
import com.koteuka404.thaumicforever.tile.TileEntityCompressor;
import com.koteuka404.thaumicforever.tile.TileEntityMatteryDuplicator;
import com.koteuka404.thaumicforever.tile.TileEntityRepurposer;
import com.koteuka404.thaumicforever.container.DeconstructionTableContainer;
import com.koteuka404.thaumicforever.container.DoubleTableContainer;
import com.koteuka404.thaumicforever.container.GreatResearchTableContainer;

public class ModGuiHandler implements IGuiHandler {
    private static final int BAUBLES_ARG_BASE = 1000;

    public static final int DECONSTRUCTION_TABLE_GUI   = 0;
    public static final int CHEST_GUI_ID               = 1;
    public static final int DOUBLE_TABLE_GUI           = 2;
    public static final int CRIMSON_BOOK_GUI           = 3;
    public static final int GUI_ID_MATTERY_DUPLICATOR  = 4;
    public static final int GUI_ID_COMPRESSOR          = 5;
    public static final int GUI_ID_REPURPOSER          = 6;
    public static final int GUI_BAUBLES                = 7;
    public static final int GUI_WAND_WORKBENCH         = 8;
    public static final int GUI_POUCH                  = 9;
    public static final int GUI_POTION_GUN             = 10;
    public static final int GREAT_RESEARCH_TABLE_GUI   = 11;

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
        else if (id == GREAT_RESEARCH_TABLE_GUI && te instanceof net.minecraft.inventory.IInventory) {
            return new GreatResearchTableContainer(inv, (net.minecraft.inventory.IInventory) te);
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
        else if (id == GUI_WAND_WORKBENCH && te instanceof TileArcaneWorkbenchNew) {
            return new ContainerArcaneWorkbenchNew(inv, (TileArcaneWorkbenchNew) te);
        }
        else if (id == GUI_POUCH) {
            ItemStack stack = getPlayerStackByGuiArg(player, x);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemPouch)) return null;
            return new ContainerPouch(inv, new InventoryPouch(stack), player);
        }
        else if (id == GUI_POTION_GUN) {
            ItemStack stack = getPlayerStackByGuiArg(player, x);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemPotionGun)) return null;
            return new ContainerPotionGun(inv, new InventoryPotionGun(stack, player, x), player);
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
        else if (id == GREAT_RESEARCH_TABLE_GUI && te instanceof net.minecraft.inventory.IInventory) {
            return new GreatResearchTableGui(player.inventory, (net.minecraft.inventory.IInventory) te);
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
        else if (id == GUI_WAND_WORKBENCH && te instanceof TileArcaneWorkbenchNew) {
            return new GuiArcaneWorkbenchNew(player.inventory, (TileArcaneWorkbenchNew) te);
        }
        else if (id == GUI_POUCH) {
            ItemStack stack = getPlayerStackByGuiArg(player, x);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemPouch)) return null;
            return new GuiChest(player.inventory, new InventoryPouch(stack));
        }
        else if (id == GUI_POTION_GUN) {
            ItemStack stack = getPlayerStackByGuiArg(player, x);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemPotionGun)) return null;
            return new GuiPotionGun(player.inventory, new InventoryPotionGun(stack, player, x));
        }
        return null;
    }

    private static ItemStack getPlayerStackByGuiArg(EntityPlayer player, int arg) {
        if (arg >= BAUBLES_ARG_BASE) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            int slot = arg - BAUBLES_ARG_BASE;
            if (baubles != null && slot >= 0 && slot < baubles.getSlots()) {
                return baubles.getStackInSlot(slot);
            }
            return ItemStack.EMPTY;
        }

        if (arg == 1) return player.getHeldItemOffhand();
        if (arg == 0) return player.getHeldItemMainhand();

        int slot = arg - 2;
        if (slot >= 0 && slot < player.inventory.getSizeInventory()) {
            return player.inventory.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }
}
