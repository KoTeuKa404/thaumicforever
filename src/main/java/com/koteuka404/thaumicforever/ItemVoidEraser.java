package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemVoidEraser extends Item {
    private final Map<String, BlockPos> pos1 = new HashMap<>();
    private final Map<String, BlockPos> pos2 = new HashMap<>();

    public ItemVoidEraser() {
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.SUCCESS;

        String playerName = player.getName();

        if (!pos1.containsKey(playerName)) {
            pos1.put(playerName, pos);
            player.sendMessage(new TextComponentString("§a[VoidEraser] Позиція 1 встановлена: " + pos));
        } else if (!pos2.containsKey(playerName)) {
            pos2.put(playerName, pos);
            player.sendMessage(new TextComponentString("§b[VoidEraser] Позиція 2 встановлена: " + pos));
            replaceVoidWithAir(world, playerName);
        } else {
            pos1.remove(playerName);
            pos2.remove(playerName);
            player.sendMessage(new TextComponentString("§c[VoidEraser] Виділення скинуто."));
        }

        return EnumActionResult.SUCCESS;
    }

    private void replaceVoidWithAir(World world, String playerName) {
        BlockPos p1 = pos1.get(playerName);
        BlockPos p2 = pos2.get(playerName);

        int minX = Math.min(p1.getX(), p2.getX());
        int minY = Math.min(p1.getY(), p2.getY());
        int minZ = Math.min(p1.getZ(), p2.getZ());
        int maxX = Math.max(p1.getX(), p2.getX());
        int maxY = Math.max(p1.getY(), p2.getY());
        int maxZ = Math.max(p1.getZ(), p2.getZ());

        int replaced = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() == Blocks.STRUCTURE_VOID) {
                        world.setBlockToAir(pos);
                        replaced++;
                    }
                }
            }
        }

        pos1.remove(playerName);
        pos2.remove(playerName);
        world.getMinecraftServer().getPlayerList().getPlayerByUsername(playerName)
                .sendMessage(new TextComponentString("§e[VoidEraser] Заміна завершена. Повітря вставлено: " + replaced));
    }
}
