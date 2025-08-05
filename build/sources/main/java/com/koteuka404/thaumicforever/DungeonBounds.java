    package com.koteuka404.thaumicforever;

    import net.minecraft.util.math.BlockPos;

    public class DungeonBounds {
        public final BlockPos start;
        public final BlockPos end;

        public DungeonBounds(BlockPos start, BlockPos end) {
            this.start = start;
            this.end = end;
        }
    }
