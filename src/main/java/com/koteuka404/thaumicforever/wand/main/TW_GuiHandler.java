package com.koteuka404.thaumicforever.wand.main;

import com.koteuka404.thaumicforever.wand.client.gui.GuiArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.container.ContainerArcaneWorkbenchNew;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class TW_GuiHandler implements IGuiHandler {

    public static final int guiArcaneWorkbench = 0;

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case guiArcaneWorkbench:
                return new GuiArcaneWorkbenchNew(player.inventory, (TileArcaneWorkbench) world.getTileEntity(new BlockPos(x, y, z)));

        }

        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case guiArcaneWorkbench:
                return new ContainerArcaneWorkbenchNew(player.inventory, (TileArcaneWorkbench) world.getTileEntity(new BlockPos(x, y, z)));

        }

        return null;
    }

}
