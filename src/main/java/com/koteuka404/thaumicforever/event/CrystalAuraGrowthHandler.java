package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.aura.PrimalAuraChunk;
import com.koteuka404.thaumicforever.aura.PrimalAuraHandler;
import com.koteuka404.thaumicforever.aura.PrimalAuraHandler.PrimalAuraWorldData;
import com.koteuka404.thaumicforever.item.Primal;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.world.ore.BlockCrystal;

public class CrystalAuraGrowthHandler {

    private static final int TICK_INTERVAL = 40;
    private static final int MAX_CRYSTALS_PER_PLAYER = 48;
    private static final int RANGE_XZ = 24;
    private static final int RANGE_Y = 10;
    private static final float GROW_COST = 10.0F;
    private static final float GROW_THRESHOLD_RATIO = 0.80F;
    private static final float STARVE_RATIO = 0.12F;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.world instanceof WorldServer)) return;

        WorldServer world = (WorldServer) event.world;
        if (world.getTotalWorldTime() % TICK_INTERVAL != 0) return;
        if (world.playerEntities == null || world.playerEntities.isEmpty()) return;

        for (EntityPlayer player : world.playerEntities) {
            if (player == null) continue;
            BlockPos center = player.getPosition();
            int processed = 0;

            for (int y = -RANGE_Y; y <= RANGE_Y && processed < MAX_CRYSTALS_PER_PLAYER; y++) {
                for (int x = -RANGE_XZ; x <= RANGE_XZ && processed < MAX_CRYSTALS_PER_PLAYER; x++) {
                    for (int z = -RANGE_XZ; z <= RANGE_XZ && processed < MAX_CRYSTALS_PER_PLAYER; z++) {
                        BlockPos pos = center.add(x, y, z);
                        Block block = world.getBlockState(pos).getBlock();
                        if (primalFor(block) == null && block != BlocksTC.crystalTaint) continue;
                        processed++;
                        processCrystal(world, pos);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onHarvestDrops(HarvestDropsEvent event) {
        if (event.getWorld() == null || event.getWorld().isRemote) return;

        IBlockState state = event.getState();
        Block block = state.getBlock();
        float returned = getCrystalAuraReturn(state, block);

        if (block == BlocksTC.crystalTaint) {
            AuraHelper.polluteAura(event.getWorld(), event.getPos(), returned, false);
            return;
        }

        Primal primal = primalFor(block);
        if (primal != null) {
            PrimalAuraHandler.add(event.getWorld(), event.getPos(), primal, returned);
        }
    }

    private void processCrystal(WorldServer world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == BlocksTC.crystalTaint) {
            processVitiumCrystal(world, pos, state);
            return;
        }

        Primal primal = primalFor(block);
        if (primal == null) return;
        if (!(block instanceof BlockCrystal)) return;

        PrimalAuraChunk chunk = PrimalAuraWorldData.get(world).getChunkAt(world, pos);
        float base = chunk.base[primal.id];
        float vis = chunk.vis[primal.id];
        BlockCrystal crystal = (BlockCrystal) block;
        int generation = crystal.getGeneration(state);

        if (canFeedCrystal(vis, base) && world.rand.nextInt(3 + generation) == 0) {
            tryGrowOrSpread(world, pos, state, block, primal);
            return;
        }

        if (base > 0.0F && vis < base * STARVE_RATIO && world.rand.nextInt(10) == 0) {
            int growth = crystal.getGrowth(state);
            if (growth > 0) {
                world.setBlockState(pos, state.withProperty(BlockCrystal.SIZE, growth - 1), 3);
                PrimalAuraHandler.add(world, pos, primal, Math.max(1.0F, base * 0.03F));
                return;
            }
            world.setBlockToAir(pos);
            PrimalAuraHandler.add(world, pos, primal, Math.max(1.0F, base * 0.03F));
        }
    }

    private void processVitiumCrystal(WorldServer world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (!(block instanceof BlockCrystal)) return;

        BlockCrystal crystal = (BlockCrystal) block;
        int generation = crystal.getGeneration(state);
        int base = AuraHelper.getAuraBase(world, pos);
        float flux = AuraHelper.getFlux(world, pos);

        if (canFeedCrystal(flux, base) && world.rand.nextInt(3 + generation) == 0) {
            tryGrowOrSpreadFlux(world, pos, state, block);
            return;
        }

        if (base > 0 && flux < base * STARVE_RATIO && world.rand.nextInt(10) == 0) {
            int growth = crystal.getGrowth(state);
            if (growth > 0) {
                world.setBlockState(pos, state.withProperty(BlockCrystal.SIZE, growth - 1), 3);
                AuraHelper.polluteAura(world, pos, Math.max(1.0F, base * 0.03F), false);
                return;
            }
            world.setBlockToAir(pos);
            AuraHelper.polluteAura(world, pos, Math.max(1.0F, base * 0.03F), false);
        }
    }

    private void tryGrowOrSpread(WorldServer world, BlockPos pos, IBlockState state, Block block, Primal primal) {
        if (!(block instanceof BlockCrystal)) return;

        BlockCrystal crystal = (BlockCrystal) block;
        int growth = crystal.getGrowth(state);
        int generation = crystal.getGeneration(state);
        if (growth < 3 && growth < 5 - generation + pos.toLong() % 3L) {
            float drained = drainCrystalFeed(world, pos, primal);
            if (drained < GROW_COST * 0.75F) return;

            world.setBlockState(pos, state.withProperty(BlockCrystal.SIZE, growth + 1), 3);
            return;
        }

        if (generation >= 4) return;

        BlockPos target = BlockCrystal.spreadCrystal(world, pos);
        if (target == null) return;

        float drained = drainCrystalFeed(world, pos, primal);
        if (drained < GROW_COST * 0.75F) return;

        if (world.rand.nextInt(6) == 0) {
            generation--;
        }
        world.setBlockState(target, block.getDefaultState().withProperty(BlockCrystal.GENERATION, generation + 1), 3);
    }

    private void tryGrowOrSpreadFlux(WorldServer world, BlockPos pos, IBlockState state, Block block) {
        if (!(block instanceof BlockCrystal)) return;

        BlockCrystal crystal = (BlockCrystal) block;
        int growth = crystal.getGrowth(state);
        int generation = crystal.getGeneration(state);
        if (growth < 3 && growth < 5 - generation + pos.toLong() % 3L) {
            float drained = drainFluxCrystalFeed(world, pos);
            if (drained < GROW_COST * 0.75F) return;

            world.setBlockState(pos, state.withProperty(BlockCrystal.SIZE, growth + 1), 3);
            return;
        }

        if (generation >= 4) return;

        BlockPos target = BlockCrystal.spreadCrystal(world, pos);
        if (target == null) return;

        float drained = drainFluxCrystalFeed(world, pos);
        if (drained < GROW_COST * 0.75F) return;

        if (world.rand.nextInt(6) == 0) {
            generation--;
        }
        world.setBlockState(target, block.getDefaultState().withProperty(BlockCrystal.GENERATION, generation + 1), 3);
    }

    private static boolean canFeedCrystal(float amount, float base) {
        return base > 0.0F && amount >= getGrowthThreshold(base) + GROW_COST * 0.75F;
    }

    private static float drainCrystalFeed(WorldServer world, BlockPos pos, Primal primal) {
        PrimalAuraChunk chunk = PrimalAuraWorldData.get(world).getChunkAt(world, pos);
        float threshold = getGrowthThreshold(chunk.base[primal.id]);
        float surplus = chunk.vis[primal.id] - threshold;
        if (surplus <= 0.0F) return 0.0F;
        return PrimalAuraHandler.drain(world, pos, primal, Math.min(GROW_COST, surplus), false);
    }

    private static float drainFluxCrystalFeed(WorldServer world, BlockPos pos) {
        int base = AuraHelper.getAuraBase(world, pos);
        float threshold = getGrowthThreshold(base);
        float surplus = AuraHelper.getFlux(world, pos) - threshold;
        if (surplus <= 0.0F) return 0.0F;
        return AuraHelper.drainFlux(world, pos, Math.min(GROW_COST, surplus), false);
    }

    private static float getGrowthThreshold(float base) {
        return Math.max(0.0F, base * GROW_THRESHOLD_RATIO);
    }

    private static float getCrystalAuraReturn(IBlockState state, Block block) {
        if (block instanceof BlockCrystal) {
            int growth = ((BlockCrystal) block).getGrowth(state);
            return Math.max(2.0F, (growth + 1) * 2.5F);
        }
        return 2.0F;
    }

    private static Primal primalFor(Block block) {
        if (block == null) return null;
        if (block == BlocksTC.crystalFire) return Primal.IGNIS;
        if (block == BlocksTC.crystalEarth) return Primal.TERRA;
        if (block == BlocksTC.crystalAir) return Primal.AER;
        if (block == BlocksTC.crystalWater) return Primal.AQUA;
        if (block == BlocksTC.crystalOrder) return Primal.ORDO;
        if (block == BlocksTC.crystalEntropy) return Primal.PERDITIO;
        return null;
    }
}
