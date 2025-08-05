package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DungeonUtil {

    public static void breakAllIronBarsInAllDungeons(World world) {
        for (BoundingBox box : WorldTickHandler.getInstance().getAllDungeonBounds()) {
            breakAllIronBarsInBox(world, box);
        }
    }

    public static void breakAllIronBarsInBox(World world, BoundingBox box) {
        for (int x = box.getMinX(); x <= box.getMaxX(); x++) {
            for (int y = box.getMinY(); y <= box.getMaxY(); y++) {
                for (int z = box.getMinZ(); z <= box.getMaxZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getBlock() == Blocks.IRON_BARS) {
                        world.destroyBlock(pos, false); 
                    }
                }
            }
        }
    }

}
