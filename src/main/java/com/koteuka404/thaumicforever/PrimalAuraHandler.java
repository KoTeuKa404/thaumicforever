package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public final class PrimalAuraHandler {

    private static final float REGEN_RATE = 0.01f;
    private static final float DIFF_RATE  = 0.001f;
    private static final float NODE_REGEN_BOOST = 2.0f;

    private static final int CHUNKS_PER_TICK = 64;

    private static final int SAVE_EVERY_TICKS = 40;
    private static final float EPS = 0.0005f;

    private static final float BASELEVEL_MIN = 60f;
    private static final float BASELEVEL_MAX = 140f;

    private static final float ASPECT_MIN = 40f;
    private static final float ASPECT_MAX = 180f;

    private static final float JITTER_EACH = 6f;

    private static final float DOM_BOOST = 0.65f;
    private static final float LOW_DROP  = 0.55f;
    private static final float MIN_W     = 0.20f;
    private static final float MAX_W     = 3.00f;


    private static final float SET_TO_TC_VIS = 1.0f;

    private static final float MAX_TC_VIS_PER_CHUNK_STEP = 2.5f;

    private static final float TC_ROOM_EPS = 0.0001f;

    private PrimalAuraHandler() {}

    public static float get(World world, BlockPos pos, Primal p) {
        if (world == null || world.isRemote || pos == null || p == null) return 0f;
        PrimalAuraChunk c = PrimalAuraWorldData.get(world).getChunkAt(world, pos);
        return c.vis[p.id];
    }

    public static float drain(World world, BlockPos pos, Primal p, float amount, boolean simulate) {
        if (world == null || world.isRemote || pos == null || p == null) return 0f;
        if (amount <= 0f) return 0f;

        PrimalAuraWorldData data = PrimalAuraWorldData.get(world);
        PrimalAuraChunk c = data.getChunkAt(world, pos);

        float have = c.vis[p.id];
        float take = Math.min(have, amount);

        if (!simulate && take > 0f) {
            c.vis[p.id] = have - take;
            c.clamp();
            data.setDirty();
        }

        return take;
    }

    public static void add(World world, BlockPos pos, Primal p, float amount) {
        if (world == null || world.isRemote || pos == null || p == null) return;
        if (amount <= 0f) return;

        PrimalAuraWorldData data = PrimalAuraWorldData.get(world);
        PrimalAuraChunk c = data.getChunkAt(world, pos);

        c.vis[p.id] += amount;
        c.clamp();
        data.setDirty();
    }

    public static boolean consumeSet(World world, BlockPos pos, float amount, boolean simulate) {
        if (world == null || world.isRemote || pos == null) return false;
        if (amount <= 0f) return true;

        for (Primal p : Primal.values()) {
            if (get(world, pos, p) < amount) return false;
        }
        if (simulate) return true;

        for (Primal p : Primal.values()) {
            drain(world, pos, p, amount, false);
        }
        return true;
    }

    public static boolean consumeAspects(World world, BlockPos pos, AspectList aspects, boolean simulate) {
        if (world == null || world.isRemote || pos == null) return false;
        if (aspects == null || aspects.size() <= 0) return false;

        float[] weights = new float[Primal.COUNT];
        for (Aspect a : aspects.getAspects()) {
            int amt = aspects.getAmount(a);
            if (amt > 0) addAspectToPrimalsCost(a, (float) amt, weights, 0);
        }

        for (int i = 0; i < Primal.COUNT; i++) {
            float need = weights[i];
            if (need > 0f && get(world, pos, Primal.values()[i]) < need) return false;
        }
        if (simulate) return true;

        for (int i = 0; i < Primal.COUNT; i++) {
            float need = weights[i];
            if (need > 0f) drain(world, pos, Primal.values()[i], need, false);
        }
        return true;
    }

    private static void addAspectToPrimalsCost(Aspect a, float amount, float[] out, int depth) {
        if (a == null || amount <= 0f || out == null || depth > 10) return;
        Primal p = primalFromAspect(a);
        if (p != null && a.isPrimal()) {
            out[p.id] += amount;
            return;
        }

        Aspect[] comps = a.getComponents();
        if (comps == null || comps.length == 0) return;
        for (Aspect c : comps) {
            addAspectToPrimalsCost(c, amount, out, depth + 1);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
        if (!(evt.world instanceof WorldServer)) return;

        WorldServer ws = (WorldServer) evt.world;
        if (ws.playerEntities == null || ws.playerEntities.isEmpty()) return;

        PrimalAuraWorldData data = PrimalAuraWorldData.get(ws);
        Map<Long, float[]> nodeBias = buildNodeBias(ws);

        int players = ws.playerEntities.size();
        int perPlayer = Math.max(1, CHUNKS_PER_TICK / players);

        Random r = ws.rand;
        boolean changed = false;

        for (EntityPlayer pl : ws.playerEntities) {
            if (pl == null) continue;

            BlockPos center = pl.getPosition();
            int ccx = center.getX() >> 4;
            int ccz = center.getZ() >> 4;

            for (int n = 0; n < perPlayer; n++) {
                int dx = r.nextInt(33) - 16;
                int dz = r.nextInt(33) - 16;
                int cx = ccx + dx;
                int cz = ccz + dz;

                if (!isChunkReady(ws, cx, cz)) continue;

                PrimalAuraChunk c = data.getChunk(ws, cx, cz);

                float[] bias = nodeBias.get(chunkKey(cx, cz));
                float totalBias = sumBias(bias);

                for (int i = 0; i < Primal.COUNT; i++) {
                    float before = c.vis[i];
                    float target = c.base[i];
                    float rate = REGEN_RATE;
                    if (totalBias > 0f) {
                        float w = bias[i] / totalBias;
                        rate *= (1.0f + (w * NODE_REGEN_BOOST));
                    }
                    float after = before + (target - before) * rate;
                    if (Math.abs(after - before) > EPS) changed = true;
                    c.vis[i] = after;
                }

                changed |= diffusePair(ws, data, cx, cz, cx + 1, cz);
                changed |= diffusePair(ws, data, cx, cz, cx, cz + 1);

                if (tryConvertPrimalIntoTcVis(ws, cx, cz, c)) {
                    changed = true;
                }

                c.clamp();
            }
        }

        if (changed) data.setDirty();
        data.flushMaybe(ws.getTotalWorldTime(), SAVE_EVERY_TICKS);
    }

    private static Map<Long, float[]> buildNodeBias(WorldServer ws) {
        Map<Long, float[]> map = new HashMap<>();
        for (EntityAuraNode node : ws.getEntities(EntityAuraNode.class, e -> e != null && !e.isDead)) {
            BlockPos pos = node.getPosition();
            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            long key = chunkKey(cx, cz);

            float[] acc = map.get(key);
            if (acc == null) {
                acc = new float[Primal.COUNT];
                map.put(key, acc);
            }

            AspectList aspects = node.getNodeAspects();
            if (aspects == null) continue;
            for (Aspect a : aspects.getAspects()) {
                int amt = aspects.getAmount(a);
                if (amt > 0) addAspectToPrimals(a, (float) amt, acc, 0);
            }
        }
        return map;
    }

    private static long chunkKey(int cx, int cz) {
        return (((long) cx) << 32) ^ (cz & 0xffffffffL);
    }

    private static float sumBias(float[] bias) {
        if (bias == null) return 0f;
        float sum = 0f;
        for (int i = 0; i < bias.length; i++) sum += Math.max(0f, bias[i]);
        return sum;
    }

    private static void addAspectToPrimals(Aspect a, float amount, float[] out, int depth) {
        if (a == null || amount <= 0f || out == null || depth > 10) return;
        Primal p = primalFromAspect(a);
        if (p != null && a.isPrimal()) {
            out[p.id] += amount;
            return;
        }

        Aspect[] comps = a.getComponents();
        if (comps == null || comps.length == 0) return;
        float share = amount / comps.length;
        for (Aspect c : comps) {
            addAspectToPrimals(c, share, out, depth + 1);
        }
    }

    private static Primal primalFromAspect(Aspect a) {
        if (a == null) return null;
        String tag = a.getTag();
        if (tag == null) return null;
        switch (tag.toLowerCase(Locale.ROOT)) {
            case "ignis": return Primal.IGNIS;
            case "terra": return Primal.TERRA;
            case "aer": return Primal.AER;
            case "aqua": return Primal.AQUA;
            case "ordo": return Primal.ORDO;
            case "perditio": return Primal.PERDITIO;
            default: return null;
        }
    }

    private static boolean isChunkReady(WorldServer ws, int cx, int cz) {
        try {
            return ws.getChunkProvider().isChunkGeneratedAt(cx, cz);
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean diffusePair(WorldServer ws, PrimalAuraWorldData data, int ax, int az, int bx, int bz) {
        if (!isChunkReady(ws, bx, bz)) return false;

        PrimalAuraChunk a = data.getChunk(ws, ax, az);
        PrimalAuraChunk b = data.getChunk(ws, bx, bz);

        boolean changed = false;

        for (int i = 0; i < Primal.COUNT; i++) {
            float diff = a.vis[i] - b.vis[i];
            float move = diff * DIFF_RATE;

            if (Math.abs(move) > EPS) changed = true;

            a.vis[i] -= move;
            b.vis[i] += move;
        }

        a.clamp();
        b.clamp();
        return changed;
    }


    private static boolean tryConvertPrimalIntoTcVis(WorldServer ws, int cx, int cz, PrimalAuraChunk primalChunk) {
        if (ws == null || primalChunk == null) return false;

        BlockPos pos = new BlockPos((cx << 4) + 8, 64, (cz << 4) + 8);

        float tcBase;
        float tcVis;
        try {
            tcBase = (float) AuraHelper.getAuraBase(ws, pos);
            tcVis  = AuraHelper.getVis(ws, pos);
        } catch (Throwable t) {
            return false;
        }

        float room = tcBase - tcVis;
        if (room <= TC_ROOM_EPS) return false; // already at/above base, do nothing

        float minPrimal = Float.MAX_VALUE;
        for (int i = 0; i < Primal.COUNT; i++) {
            float v = primalChunk.vis[i];
            if (v < minPrimal) minPrimal = v;
        }
        if (minPrimal <= 0.0001f) return false;

        float wantTc = Math.min(room, MAX_TC_VIS_PER_CHUNK_STEP);

        float setsNeeded = (SET_TO_TC_VIS <= 0f) ? 0f : (wantTc / SET_TO_TC_VIS);
        if (setsNeeded <= 0.0001f) return false;

        float setsTake = Math.min(setsNeeded, minPrimal);
        if (setsTake <= 0.0001f) return false;

        float tcGive = setsTake * SET_TO_TC_VIS;

        if (tcGive > room) {
            tcGive = room;
            setsTake = (SET_TO_TC_VIS <= 0f) ? 0f : (tcGive / SET_TO_TC_VIS);
        }
        if (tcGive <= 0.0001f || setsTake <= 0.0001f) return false;

        try {
            AuraHelper.addVis(ws, pos, tcGive);
        } catch (Throwable t) {
            return false;
        }

        for (int i = 0; i < Primal.COUNT; i++) {
            primalChunk.vis[i] = primalChunk.vis[i] - setsTake;
            if (primalChunk.vis[i] < 0f) primalChunk.vis[i] = 0f;
        }

        return true;
    }


    public static final class PrimalAuraWorldData extends WorldSavedData {

        private static final String DATA_NAME = "thaumicforever_primal_aura";
        private final Map<Long, PrimalAuraChunk> chunks = new HashMap<>();

        private boolean dirty = false;
        private long lastSavedTick = -999999;

        public PrimalAuraWorldData() { super(DATA_NAME); }
        public PrimalAuraWorldData(String name) { super(name); }

        public static PrimalAuraWorldData get(World world) {
            PrimalAuraWorldData data = (PrimalAuraWorldData) world.getPerWorldStorage()
                .getOrLoadData(PrimalAuraWorldData.class, DATA_NAME);

            if (data == null) {
                data = new PrimalAuraWorldData(DATA_NAME);
                world.getPerWorldStorage().setData(DATA_NAME, data);
                data.markDirty();
            }
            return data;
        }

        public void setDirty() { dirty = true; }

        public void flushMaybe(long nowTick, int everyTicks) {
            if (!dirty) return;
            if ((nowTick - lastSavedTick) < everyTicks) return;

            lastSavedTick = nowTick;
            dirty = false;
            markDirty();
        }

        private static long key(int cx, int cz) {
            return (((long) cx) << 32) ^ (cz & 0xffffffffL);
        }

        public PrimalAuraChunk getChunk(World world, int cx, int cz) {
            long k = key(cx, cz);
            PrimalAuraChunk c = chunks.get(k);
            if (c != null) return c;

            c = new PrimalAuraChunk();
            generateBase(world, cx, cz, c);
            for (int i = 0; i < Primal.COUNT; i++) c.vis[i] = c.base[i];
            c.clamp();

            chunks.put(k, c);
            setDirty();
            return c;
        }

        public PrimalAuraChunk getChunkAt(World world, BlockPos pos) {
            ChunkPos cp = new ChunkPos(pos);
            return getChunk(world, cp.x, cp.z);
        }

        private void generateBase(World world, int cx, int cz, PrimalAuraChunk out) {
            final int bx = (cx << 4) + 8;
            final int bz = (cz << 4) + 8;
            final BlockPos samplePos = new BlockPos(bx, 64, bz);

            Biome biome = world.getBiome(samplePos);

            long seed = world.getSeed() ^ (cx * 341873128712L) ^ (cz * 132897987541L);
            Random r = new Random(seed);

            int tcBaseRaw = safeGetTcAuraBase(world, samplePos);
            float baseLevel = clamp(tcBaseRaw, BASELEVEL_MIN, BASELEVEL_MAX);

            float[] w = biomeWeights(biome, samplePos);

            float avg = 0f;
            for (int i = 0; i < Primal.COUNT; i++) avg += w[i];
            avg /= Primal.COUNT;
            if (avg < 0.001f) avg = 1f;
            for (int i = 0; i < Primal.COUNT; i++) w[i] /= avg;

            for (int i = 0; i < Primal.COUNT; i++) {
                float v = baseLevel * w[i] + (r.nextFloat() * 2f - 1f) * JITTER_EACH;
                v = clamp(v, ASPECT_MIN, ASPECT_MAX);
                out.base[i] = (short) Math.round(v);
            }

            int boost = pickWeighted(r, w);
            out.base[boost] = (short) clampInt(out.base[boost] + (8 + r.nextInt(17)), (int)ASPECT_MIN, (int)ASPECT_MAX);
        }

        private static int safeGetTcAuraBase(World world, BlockPos pos) {
            try {
                return AuraHelper.getAuraBase(world, pos);
            } catch (Throwable t) {
                return 0;
            }
        }

        private static float[] biomeWeights(Biome biome, BlockPos pos) {
            float ignis = 1f, terra = 1f, aer = 1f, aqua = 1f, ordo = 1f, perditio = 1f;

            float t = biome.getTemperature(pos);
            float rf = biome.getRainfall();

            ignis += (t - 0.8f) * 0.20f;
            aer   += (t - 0.8f) * 0.10f;
            aqua  += (rf - 0.4f) * 0.20f;

            boolean isOcean = BiomeDictionary.hasType(biome, Type.OCEAN) || BiomeDictionary.hasType(biome, Type.RIVER);
            boolean isSwamp = BiomeDictionary.hasType(biome, Type.SWAMP);

            boolean isDesert = BiomeDictionary.hasType(biome, Type.SANDY)
                    && (BiomeDictionary.hasType(biome, Type.HOT) || BiomeDictionary.hasType(biome, Type.DRY));
            if (!isDesert) {
                String rn = biome.getRegistryName() != null ? biome.getRegistryName().toString() : "";
                if (rn.contains("desert")) isDesert = true;
            }

            boolean isJungle = BiomeDictionary.hasType(biome, Type.JUNGLE);
            boolean isForest = BiomeDictionary.hasType(biome, Type.FOREST);
            boolean isMountain = BiomeDictionary.hasType(biome, Type.MOUNTAIN) || BiomeDictionary.hasType(biome, Type.HILLS);
            boolean isSnow = BiomeDictionary.hasType(biome, Type.SNOWY) || BiomeDictionary.hasType(biome, Type.COLD);
            boolean isNether = BiomeDictionary.hasType(biome, Type.NETHER);
            boolean isEnd = BiomeDictionary.hasType(biome, Type.END);

            boolean isMesa = BiomeDictionary.hasType(biome, Type.MESA);
            if (!isMesa) {
                String rn = biome.getRegistryName() != null ? biome.getRegistryName().toString() : "";
                if (rn.contains("mesa") || rn.contains("badlands")) isMesa = true;
            }

            boolean isDead = BiomeDictionary.hasType(biome, Type.DEAD)
                    || BiomeDictionary.hasType(biome, Type.SPOOKY)
                    || BiomeDictionary.hasType(biome, Type.WASTELAND);

            int[] dom = null;
            int[] low = null;

            if (isSwamp) {
                dom = new int[] { 3, 1 };
                low = new int[] { 0, 4 };
            } else if (isOcean) {
                dom = new int[] { 3, 5 };
                low = new int[] { 0, 1 };
            } else if (isSnow) {
                dom = new int[] { 4, 3 };
                low = new int[] { 0, 5 };
            } else if (isMesa) {
                dom = new int[] { 5, 2 };
                low = new int[] { 3, 1 };
            } else if (isDesert) {
                dom = new int[] { 0, 2 };
                low = new int[] { 3, 1 };
            } else if (isNether) {
                dom = new int[] { 0, 5 };
                low = new int[] { 3, 4 };
            } else if (isEnd) {
                dom = new int[] { 4, 5 };
                low = new int[] { 1, 3 };
            } else if (isMountain) {
                dom = new int[] { 2, 1 };
                low = new int[] { 3, 5 };
            } else if (isJungle) {
                dom = new int[] { 1, 3 };
                low = new int[] { 0, 5 };
            } else if (isForest) {
                dom = new int[] { 1, 3 };
                low = new int[] { 5, 0 };
            } else if (isDead) {
                dom = new int[] { 5, 4 };
                low = new int[] { 3, 0 };
            }

            if (dom != null && low != null) {
                float[] arr = new float[] { ignis, terra, aer, aqua, ordo, perditio };
                for (int idx : dom) arr[idx] += DOM_BOOST;
                for (int idx : low) arr[idx] -= LOW_DROP;

                ignis = arr[0]; terra = arr[1]; aer = arr[2]; aqua = arr[3]; ordo = arr[4]; perditio = arr[5];
            } else {
                terra += 0.10f;
                ordo += 0.05f;
            }

            ignis = clamp(ignis, MIN_W, MAX_W);
            terra = clamp(terra, MIN_W, MAX_W);
            aer   = clamp(aer,   MIN_W, MAX_W);
            aqua  = clamp(aqua,  MIN_W, MAX_W);
            ordo  = clamp(ordo,  MIN_W, MAX_W);
            perditio = clamp(perditio, MIN_W, MAX_W);

            return new float[] { ignis, terra, aer, aqua, ordo, perditio };
        }

        private static int pickWeighted(Random r, float[] w) {
            float sum = 0f;
            for (float x : w) sum += Math.max(0f, x);
            if (sum <= 0.0001f) return r.nextInt(Primal.COUNT);

            float roll = r.nextFloat() * sum;
            float acc = 0f;
            for (int i = 0; i < w.length; i++) {
                acc += Math.max(0f, w[i]);
                if (roll <= acc) return i;
            }
            return w.length - 1;
        }

        private static float clamp(float v, float lo, float hi) {
            if (v < lo) return lo;
            if (v > hi) return hi;
            return v;
        }

        private static int clampInt(int v, int lo, int hi) {
            if (v < lo) return lo;
            if (v > hi) return hi;
            return v;
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<Long, PrimalAuraChunk> e : chunks.entrySet()) {
                long k = e.getKey();
                int cx = (int) (k >> 32);
                int cz = (int) (k & 0xffffffffL);

                NBTTagCompound t = new NBTTagCompound();
                t.setInteger("x", cx);
                t.setInteger("z", cz);
                t.setTag("d", e.getValue().writeNBT());
                list.appendTag(t);
            }
            compound.setTag("chunks", list);
            return compound;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            chunks.clear();
            NBTTagList list = compound.getTagList("chunks", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound t = list.getCompoundTagAt(i);
                int cx = t.getInteger("x");
                int cz = t.getInteger("z");
                NBTTagCompound d = t.getCompoundTag("d");

                PrimalAuraChunk c = new PrimalAuraChunk();
                c.readNBT(d);
                chunks.put(key(cx, cz), c);
            }

            dirty = false;
            lastSavedTick = -999999;
        }
    }
}
