package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WorldTickHandler {

    private static final WorldTickHandler INSTANCE = new WorldTickHandler();
    private final Set<BoundingBox> dungeonBounds = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Random random = new Random();

    private static final int REVIVE_WAVE_COOLDOWN_MIN = 20 * 1;
    private static final int REVIVE_WAVE_COOLDOWN_MAX = 20 * 2;

    private static final int MAX_TOTAL_PER_BOX = 160;
    private static final int MAX_ANGRY_PER_BOX = 40;
    private static final int MAX_REVIVE_PER_BOX = 120;
    private static final int MAX_SPAWNS_PER_TICK_PER_BOX = 24;
    private static final int RESCAN_BLOCKS_EVERY_TICKS = 20 * 12; 
    private static final int RECOUNT_MOBS_EVERY_TICKS  = 10;      
    private static final int CANDIDATE_LIMIT           = 180;

    private final Map<BoundingBox, BoxState> boxStateMap = new HashMap<>();

    private WorldTickHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static WorldTickHandler getInstance() {
        return INSTANCE;
    }

    public void addDungeonBounds(BlockPos startPos, BlockPos endPos) {
        BoundingBox box = new BoundingBox(startPos, endPos);
        dungeonBounds.add(box);
    }

    public Set<BoundingBox> getAllDungeonBounds() {
        return new HashSet<>(dungeonBounds);
    }

    private Set<BoundingBox> getAllDungeonBoundsMerged(World world) {
        Set<BoundingBox> merged = new HashSet<>(dungeonBounds);
        DungeonBoundsData data = DungeonBoundsData.get(world);
        if (data != null) merged.addAll(data.getBoxes());
        return merged;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        World world = event.world;
        if (world.isRemote) return;

        Set<BoundingBox> boxes = getAllDungeonBoundsMerged(world);
        if (boxes.isEmpty()) return;

        long now = world.getTotalWorldTime();

        for (BoundingBox box : boxes) {
            BoxState st = boxStateMap.computeIfAbsent(box, b -> new BoxState(world, b, random.nextInt(20)));
            if (!st.isPlayerInside(world)) continue;
            if ((now + st.tickOffset) % 2 != 0) continue;

            st.ensureRecount(world, now);
            st.ensureRescan(world, now);

            int totalNow = st.totalNow;
            if (totalNow >= MAX_TOTAL_PER_BOX) continue;

            int spawnBudget = Math.min(MAX_SPAWNS_PER_TICK_PER_BOX, MAX_TOTAL_PER_BOX - totalNow);
            if (spawnBudget <= 0) continue;

            st.tickWaveCooldown();

            if (spawnBudget > 0 && st.angryNow < MAX_ANGRY_PER_BOX) {
                BlockPos pos = st.pickSpawnPos(world, box);
                if (pos != null && spawnAngrySkeletonAt(world, pos)) {
                    st.angryNow++; st.totalNow++; spawnBudget--;
                }
            }

            if (spawnBudget > 0 && st.isWaveReady()) {
                int planned   = 6 + random.nextInt(7); // 6..12
                int waveSlots = Math.min(planned, Math.min(spawnBudget, MAX_REVIVE_PER_BOX - st.reviveNow));
                if (waveSlots > 0) {
                    int spawned = spawnReviveSkeletonsAt(world, st, waveSlots);
                    st.reviveNow += spawned; st.totalNow += spawned; spawnBudget -= spawned;
                }
                st.resetWaveCooldown(randomCooldown());
            }

            if (spawnBudget > 0 && st.reviveNow < MAX_REVIVE_PER_BOX && random.nextInt(3) == 0) {
                int want = Math.min(2, spawnBudget);
                int spawned = spawnReviveSkeletonsAt(world, st, want);
                st.reviveNow += spawned; st.totalNow += spawned; spawnBudget -= spawned;
            }

            st.lastCountAt = now;
        }
    }


    private boolean spawnAngrySkeletonAt(World world, BlockPos spawnPos) {
        EntitySkeletonAngry s = new EntitySkeletonAngry(world);
        s.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        kickstartEntity(s, world, spawnPos);
        return world.spawnEntity(s);
    }

    private int spawnReviveSkeletonsAt(World world, BoxState st, int count) {
        int spawned = 0;
        BlockPos base = st.pickSpawnPos(world, st.box);
        if (base == null) return 0;

        for (int i = 0; i < count; i++) {
            double ox = (random.nextDouble() - 0.5) * 1.5;
            double oz = (random.nextDouble() - 0.5) * 1.5;

            ReviveSkeletonEntity e = new ReviveSkeletonEntity(world);
            e.setPosition(base.getX() + 0.5 + ox, base.getY() + 0.5, base.getZ() + 0.5 + oz);

            e.motionX = (random.nextDouble() - 0.5) * 0.4;
            e.motionZ = (random.nextDouble() - 0.5) * 0.4;

            if (random.nextInt(100) < 5) {
                int meta = random.nextInt(4);
                ItemStack scroll = new ItemStack(ModItems.SCROLL_P, 1, meta);
                e.setHeldItem(EnumHand.MAIN_HAND, scroll);
            }

            kickstartEntity(e, world, base);
            if (world.spawnEntity(e)) spawned++;
        }
        return spawned;
    }

    private int randomCooldown() {
        return REVIVE_WAVE_COOLDOWN_MIN + random.nextInt(REVIVE_WAVE_COOLDOWN_MAX - REVIVE_WAVE_COOLDOWN_MIN + 1);
    }

 
    private void kickstartEntity(EntityMob mob, World world, BlockPos origin) {
        mob.motionX += (random.nextDouble() - 0.5D) * 0.25D;
        mob.motionZ += (random.nextDouble() - 0.5D) * 0.25D;
        mob.velocityChanged = true;

        mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60, 0));
        mob.setHomePosAndDistance(origin, 32);
    }

    private static boolean isChiseledWithAir(World world, BlockPos airPos) {
        if (!world.isAirBlock(airPos)) return false;
        IBlockState s = world.getBlockState(airPos.down());
        if (s.getBlock() != Blocks.STONEBRICK) return false;
        try {
            if (s.getValue(BlockStoneBrick.VARIANT) == BlockStoneBrick.EnumType.CHISELED) return true;
        } catch (Exception ignored) {}
        return s.getBlock().getMetaFromState(s) == 3;
    }

    private static boolean canPlayerSeePos(World world, EntityPlayer player, BlockPos target) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d vecTarget = new Vec3d(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        RayTraceResult result = world.rayTraceBlocks(eyes, vecTarget, false, true, false);
        return result == null || result.typeOfHit == RayTraceResult.Type.MISS;
    }


    private static final class BoxState {
        final BoundingBox box;
        final AxisAlignedBB aabb;
        final int tickOffset; 

        int totalNow = 0, angryNow = 0, reviveNow = 0;
        long lastCountAt = -9999;

        int waveCooldown = 0;

        final List<BlockPos> candidates = new ArrayList<>(CANDIDATE_LIMIT);
        final List<BlockPos> pedestals  = new ArrayList<>();
        long lastScanAt = -9999;

        private final BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        private final Random rnd = new Random();

        BoxState(World world, BoundingBox box, int tickOffset) {
            this.box = box;
            this.tickOffset = tickOffset;
            this.aabb = new AxisAlignedBB(
                    box.getMinX(), box.getMinY(), box.getMinZ(),
                    box.getMaxX() + 1, box.getMaxY() + 1, box.getMaxZ() + 1
            );
            this.waveCooldown = 20 + rnd.nextInt(20);
            ensureRescan(world, world.getTotalWorldTime());
        }

        boolean isPlayerInside(World world) {
            List<EntityPlayer> inside = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
            return !inside.isEmpty();
        }

        void ensureRecount(World world, long now) {
            if (now - lastCountAt < RECOUNT_MOBS_EVERY_TICKS) return;

            List<EntityMob> mobs = world.getEntitiesWithinAABB(EntityMob.class, aabb);
            int angry = 0, revive = 0;
            for (EntityMob m : mobs) {
                if (m instanceof EntitySkeletonAngry) angry++;
                else if (m instanceof ReviveSkeletonEntity) revive++;
            }
            this.angryNow = angry;
            this.reviveNow = revive;
            this.totalNow = angry + revive;
            this.lastCountAt = now;
        }

        void ensureRescan(World world, long now) {
            if (now - lastScanAt < RESCAN_BLOCKS_EVERY_TICKS && !candidates.isEmpty()) return;

            candidates.clear();
            pedestals.clear();

            for (int x = box.getMinX(); x <= box.getMaxX(); x++) {
                for (int y = box.getMinY(); y <= box.getMaxY(); y++) {
                    for (int z = box.getMinZ(); z <= box.getMaxZ(); z++) {
                        mp.setPos(x, y, z);
                        if (world.getBlockState(mp).getBlock() == thaumcraft.api.blocks.BlocksTC.pedestalArcane) {
                            pedestals.add(mp.toImmutable());
                        }
                    }
                }
            }

            final int stepX = 2, stepZ = 2;
            outer:
            for (int x = box.getMinX(); x <= box.getMaxX(); x += stepX) {
                for (int y = box.getMinY(); y <= box.getMaxY(); y++) {
                    for (int z = box.getMinZ(); z <= box.getMaxZ(); z += stepZ) {
                        mp.setPos(x, y, z);
                        if (!isChiseledWithAir(world, mp)) continue;

                        final int radius = 20;
                        final int r2 = radius * radius;
                        for (BlockPos ped : pedestals) {
                            int dx = ped.getX() - mp.getX();
                            int dy = ped.getY() - mp.getY();
                            int dz = ped.getZ() - mp.getZ();
                            if (dx*dx + dy*dy + dz*dz <= r2) {
                                continue outer; 
                            }
                        }

                        candidates.add(mp.toImmutable());
                        if (candidates.size() >= CANDIDATE_LIMIT) {
                            lastScanAt = now;
                            shuffleCandidates();
                            return;
                        }
                    }
                }
            }

            lastScanAt = now;
            shuffleCandidates();
        }

        private void shuffleCandidates() {
            Collections.shuffle(candidates, rnd);
        }

        void tickWaveCooldown() {
            if (waveCooldown > 0) waveCooldown--;
        }

        boolean isWaveReady() { return waveCooldown <= 0; }

        void resetWaveCooldown(int ticks) { waveCooldown = ticks; }

        BlockPos pickSpawnPos(World world, BoundingBox box) {
            if (candidates.isEmpty()) return null;

            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
            if (players.isEmpty()) return candidates.get(rnd.nextInt(candidates.size()));

            for (int i = 0; i < 8; i++) {
                BlockPos p = candidates.get(rnd.nextInt(candidates.size()));
                if (!passesVisibilityAndDistance(world, players, box, p)) continue;
                return p;
            }
            return null;
        }

        private boolean passesVisibilityAndDistance(World world, List<EntityPlayer> players, BoundingBox box, BlockPos pos) {
            boolean anyInside = false;
            for (EntityPlayer player : players) {
                if (!box.isVecInside(player.getPosition())) continue;
                anyInside = true;
                double d2 = player.getDistanceSq(pos);
                if (d2 < 36.0D) return false;          
                if (canPlayerSeePos(world, player, pos)) return false; 
            }
            return true || !anyInside;
        }
    }
}

// ---------------- BoundingBox ----------------

class BoundingBox {
    private final BlockPos start;
    private final BlockPos end;

    public BoundingBox(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
    }

    public int getMinX() { return Math.min(start.getX(), end.getX()); }
    public int getMaxX() { return Math.max(start.getX(), end.getX()); }
    public int getMinY() { return Math.min(start.getY(), end.getY()); }
    public int getMaxY() { return Math.max(start.getY(), end.getY()); }
    public int getMinZ() { return Math.min(start.getZ(), end.getZ()); }
    public int getMaxZ() { return Math.max(start.getZ(), end.getZ()); }

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
                    Block block = world.getBlockState(pos).getBlock();
                    if (block == Blocks.IRON_BARS) {
                        world.destroyBlock(pos, true);
                    }
                }
            }
        }
    }

    public boolean isVecInside(BlockPos pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return x >= getMinX() && x <= getMaxX()
            && y >= getMinY() && y <= getMaxY()
            && z >= getMinZ() && z <= getMaxZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoundingBox)) return false;
        BoundingBox b = (BoundingBox) o;
        return getMinX() == b.getMinX() && getMinY() == b.getMinY() && getMinZ() == b.getMinZ()
            && getMaxX() == b.getMaxX() && getMaxY() == b.getMaxY() && getMaxZ() == b.getMaxZ();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getMinX();
        result = 31 * result + getMinY();
        result = 31 * result + getMinZ();
        result = 31 * result + getMaxX();
        result = 31 * result + getMaxY();
        result = 31 * result + getMaxZ();
        return result;
    }
}
