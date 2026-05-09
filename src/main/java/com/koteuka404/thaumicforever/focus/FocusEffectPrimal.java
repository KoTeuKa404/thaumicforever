package com.koteuka404.thaumicforever.focus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;

public class FocusEffectPrimal extends FocusEffect {
    private static final long CACHE_TTL_MS = 30000L;
    private static final Map<UUID, CastState> PREPARED_BY_CASTER = new ConcurrentHashMap<>();
    private static final Map<UUID, CastRuntime> RUNTIME_BY_CAST = new ConcurrentHashMap<>();
    private static final Map<UUID, CastRuntime> LAST_RUNTIME_BY_CASTER = new ConcurrentHashMap<>();
    private static final Map<UUID, RollBag> MEDIUM_BAGS_BY_CASTER = new ConcurrentHashMap<>();
    private static final Map<UUID, RollBag> EFFECT_BAGS_BY_CASTER = new ConcurrentHashMap<>();

    private static class CastState {
        final List<String> mediumKeys;
        final String effectKey;
        final long settingsSeed;

        CastState(List<String> mediumKeys, String effectKey, long settingsSeed) {
            this.mediumKeys = mediumKeys;
            this.effectKey = effectKey;
            this.settingsSeed = settingsSeed;
        }
    }

    private static class CastRuntime {
        final CastState state;
        final boolean skip;
        final long timestamp;

        CastRuntime(CastState state, boolean skip, long timestamp) {
            this.state = state;
            this.skip = skip;
            this.timestamp = timestamp;
        }
    }

    private static class RollBag {
        List<String> order = new ArrayList<>();
        int index = 0;
        int signature = 0;
    }

    @Override
    public String getResearch() {
        return "PRIMAL_FOCI";
    }

    @Override
    public String getKey() {
        return "thaumicforever.PRIMAL";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MAGIC;
    }

    @Override
    public int getComplexity() {
        return 10;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return 0.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        if (world == null) return;
        FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
        pp.grav = 0.01F;
        pp.age = 16 + world.rand.nextInt(8);
        pp.alpha = new float[] {0.75F, 0.0F};
        pp.grid = 64;
        pp.partStart = 264;
        pp.partInc = 1;
        pp.partNum = 4;
        pp.slowDown = 0.85D;
        pp.rot = (float) (world.rand.nextGaussian() * 0.2D);
        pp.scale = new float[] {0.9F, 1.2F};
        pp.redStart = 0.7F;
        pp.greenStart = 0.35F;
        pp.blueStart = 0.95F;
        pp.redEnd = 0.7F;
        pp.greenEnd = 0.35F;
        pp.blueEnd = 0.95F;
        FXDispatcher.INSTANCE.drawGenericParticles(x, y, z, vx, vy, vz, pp);
    }

    @Override
    public void onCast(Entity casterEntity) {
        if (!(casterEntity instanceof EntityLivingBase)) return;
        EntityLivingBase caster = (EntityLivingBase) casterEntity;
        World world = caster.world;
        if (world == null || world.isRemote) return;

        cleanup();

        UUID casterId = caster.getUniqueID();
        UUID castId = getPackage() != null ? getPackage().getUniqueID() : UUID.randomUUID();

        CastState prepared = PREPARED_BY_CASTER.get(casterId);
        CastState nextPrepared = rollRandomState(world, casterId);

        if (prepared == null) {
            PREPARED_BY_CASTER.put(casterId, nextPrepared);
            CastRuntime cr = new CastRuntime(null, true, System.currentTimeMillis());
            RUNTIME_BY_CAST.put(castId, cr);
            LAST_RUNTIME_BY_CASTER.put(casterId, cr);
            sendStatus(caster, nextPrepared == null
                ? "Chaos primed: no valid medium/effect"
                : "Chaos primed: " + formatState(nextPrepared));
            return;
        }

        CastRuntime cr = new CastRuntime(prepared, false, System.currentTimeMillis());
        RUNTIME_BY_CAST.put(castId, cr);
        LAST_RUNTIME_BY_CASTER.put(casterId, cr);
        PREPARED_BY_CASTER.put(casterId, nextPrepared);

        String nextText = nextPrepared == null ? "none" : formatState(nextPrepared);
        sendStatus(caster, "Chaos cast: " + formatState(prepared) + " | next: " + nextText);
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (getPackage() == null) return false;

        EntityLivingBase caster = getPackage().getCaster();
        if (caster == null) return false;

        World world = caster.world;
        if (world == null) return false;

        cleanup();

        UUID castId = getPackage().getUniqueID();
        UUID casterId = caster.getUniqueID();
        CastRuntime runtime = RUNTIME_BY_CAST.get(castId);
        if (runtime == null) {
            runtime = LAST_RUNTIME_BY_CASTER.get(casterId);
        }

        if (runtime != null && runtime.skip) {
            return true;
        }

        if (runtime == null || runtime.state == null) {
            // sendStatus(caster, "Chaos: runtime mismatch, cast canceled");
            return false;
        }
        CastState state = runtime.state;

        if (world.isRemote) return true;
        return executeRolledPackage(caster, state, target, trajectory, finalPower);
    }

    private boolean executeRolledPackage(EntityLivingBase caster, CastState state, RayTraceResult target, Trajectory trajectory, float finalPower) {
        IFocusElement effectEl = FocusEngine.getElement(state.effectKey);

        if (!(effectEl instanceof FocusEffect)) return false;
        if (state.mediumKeys == null || state.mediumKeys.isEmpty()) return false;

        FocusNode effectNode = (FocusNode) effectEl;
        effectNode.initialize();
        randomizeNodeSettings(effectNode, state.settingsSeed ^ 0x8D2A5E7712C44B31L);

        FocusPackage fp = new FocusPackage(caster);
        int midx = 0;
        for (String mediumKey : state.mediumKeys) {
            IFocusElement mediumEl = FocusEngine.getElement(mediumKey);
            if (!(mediumEl instanceof FocusMedium) || mediumEl instanceof FocusMediumRoot) {
                continue;
            }
            FocusNode mediumNode = (FocusNode) mediumEl;
            mediumNode.initialize();
            randomizeNodeSettings(mediumNode, state.settingsSeed ^ (0x4F1BBCD12A9E551DL + (long) midx * 0x9E3779B97F4A7C15L));
            fp.addNode(mediumNode);
            midx++;
        }
        if (midx <= 0) return false;
        fp.addNode(effectNode);
        fp.multiplyPower(finalPower);
        fp.setUniqueID(UUID.randomUUID());

        for (FocusEffect effect : fp.getFocusEffects()) {
            effect.onCast(caster);
        }

        Trajectory[] trajectories = trajectory != null ? new Trajectory[] {trajectory} : null;
        RayTraceResult[] targets = target != null ? new RayTraceResult[] {target} : null;
        FocusEngine.runFocusPackage(fp, trajectories, targets);
        return true;
    }

    private CastState rollRandomState(World world, UUID casterId) {
        List<String> mediumKeys = new ArrayList<>();
        List<String> effectKeys = new ArrayList<>();

        for (String key : FocusEngine.elements.keySet()) {
            IFocusElement element = FocusEngine.getElement(key);
            if (element == null) continue;
            if (element instanceof FocusNode) {
                ((FocusNode) element).initialize();
            }

            if (element instanceof FocusMedium && !(element instanceof FocusMediumRoot)) {
                mediumKeys.add(key);
                continue;
            }

            if (element instanceof FocusEffect && !key.equals(getKey())) {
                effectKeys.add(key);
            }
        }

        if (mediumKeys.isEmpty() || effectKeys.isEmpty()) {
            return null;
        }

        // Do not exclude intermediary mediums (e.g. cloud-like chains),
        // otherwise part of focus medium pool never appears in chaos rolls.
        List<String> usableMediums = new ArrayList<>(mediumKeys);

        int maxMediumCount = Math.min(2, usableMediums.size());
        int mediumCount = maxMediumCount <= 1 ? 1 : (1 + world.rand.nextInt(maxMediumCount));

        RollBag mediumBag = MEDIUM_BAGS_BY_CASTER.computeIfAbsent(casterId, k -> new RollBag());
        RollBag effectBag = EFFECT_BAGS_BY_CASTER.computeIfAbsent(casterId, k -> new RollBag());

        // Keep rolling until we get a valid medium/effect chain by real focus graph rules.
        for (int attempt = 0; attempt < 64; attempt++) {
            List<String> pickedMediums = drawFromBag(mediumBag, usableMediums, mediumCount, world.rand);
            if (pickedMediums.isEmpty()) {
                continue;
            }

            List<String> pickedEffect = drawFromBag(effectBag, effectKeys, 1, world.rand);
            if (pickedEffect.isEmpty()) {
                continue;
            }
            String effect = pickedEffect.get(0);
            if (!isValidChain(pickedMediums, effect)) {
                continue;
            }
            return new CastState(pickedMediums, effect, world.rand.nextLong());
        }
        return null;
    }

    private static boolean isValidChain(List<String> mediumKeys, String effectKey) {
        if (mediumKeys == null || mediumKeys.isEmpty() || effectKey == null) return false;

        Set<FocusNode.EnumSupplyType> available = EnumSet.of(
            FocusNode.EnumSupplyType.TARGET,
            FocusNode.EnumSupplyType.TRAJECTORY
        );

        boolean prevWasIntermediary = false;
        for (String mediumKey : mediumKeys) {
            IFocusElement mediumEl = FocusEngine.getElement(mediumKey);
            if (!(mediumEl instanceof FocusMedium) || mediumEl instanceof FocusMediumRoot) {
                return false;
            }

            if (prevWasIntermediary) {
                // Intermediary mediums (like cloud-style) must terminate the medium chain.
                return false;
            }

            FocusNode mediumNode = (FocusNode) mediumEl;
            mediumNode.initialize();
            if (!isSupplied(available, mediumNode.mustBeSupplied())) return false;

            available = toSet(mediumNode.willSupply());
            prevWasIntermediary = ((FocusMedium) mediumEl).hasIntermediary();
        }

        IFocusElement effectEl = FocusEngine.getElement(effectKey);
        if (!(effectEl instanceof FocusEffect)) return false;
        FocusNode effectNode = (FocusNode) effectEl;
        effectNode.initialize();
        return isSupplied(available, effectNode.mustBeSupplied());
    }

    private static Set<FocusNode.EnumSupplyType> toSet(FocusNode.EnumSupplyType[] arr) {
        EnumSet<FocusNode.EnumSupplyType> set = EnumSet.noneOf(FocusNode.EnumSupplyType.class);
        if (arr != null) {
            for (FocusNode.EnumSupplyType t : arr) {
                if (t != null) set.add(t);
            }
        }
        return set;
    }

    private static boolean isSupplied(Set<FocusNode.EnumSupplyType> available, FocusNode.EnumSupplyType[] required) {
        if (required == null || required.length == 0) return true;
        if (available == null || available.isEmpty()) return false;
        for (FocusNode.EnumSupplyType req : required) {
            if (req != null && !available.contains(req)) return false;
        }
        return true;
    }

    private static List<String> drawFromBag(RollBag bag, List<String> pool, int count, Random rnd) {
        List<String> out = new ArrayList<>();
        if (pool == null || pool.isEmpty() || count <= 0) return out;

        List<String> sortedPool = new ArrayList<>(pool);
        sortedPool.sort(String::compareTo);
        int signature = sortedPool.hashCode();

        if (bag.order.isEmpty() || bag.index >= bag.order.size() || bag.signature != signature) {
            bag.order = new ArrayList<>(sortedPool);
            Collections.shuffle(bag.order, rnd);
            bag.index = 0;
            bag.signature = signature;
        }

        while (out.size() < count) {
            if (bag.index >= bag.order.size()) {
                bag.order = new ArrayList<>(sortedPool);
                Collections.shuffle(bag.order, rnd);
                bag.index = 0;
            }
            String pick = bag.order.get(bag.index++);
            if (pick != null && !out.contains(pick)) {
                out.add(pick);
            }
            if (out.size() >= sortedPool.size()) {
                break;
            }
        }
        return out;
    }

    private void randomizeNodeSettings(FocusNode node, long seed) {
        if (node == null || node.getSettingList() == null || node.getSettingList().isEmpty()) return;

        Random rnd = new Random(seed);
        List<String> keys = new ArrayList<>(node.getSettingList());
        keys.sort(String::compareTo);

        for (String key : keys) {
            NodeSetting setting = node.getSetting(key);
            if (setting == null) continue;

            List<Integer> options = collectPossibleValues(setting);
            if (options.isEmpty()) continue;
            int pick = options.get(rnd.nextInt(options.size()));
            setting.setValue(pick);
        }
    }

    private List<Integer> collectPossibleValues(NodeSetting setting) {
        List<Integer> out = new ArrayList<>();
        int guard = 0;
        while (guard++ < 64) {
            int v = setting.getValue();
            if (out.isEmpty() || out.get(out.size() - 1) != v) {
                out.add(v);
            }
            setting.increment();
            if (setting.getValue() == v) {
                break;
            }
        }
        return out;
    }

    private static String formatState(CastState state) {
        if (state == null) return "none";
        StringBuilder sb = new StringBuilder();
        if (state.mediumKeys != null && !state.mediumKeys.isEmpty()) {
            for (int i = 0; i < state.mediumKeys.size(); i++) {
                if (i > 0) sb.append(" > ");
                sb.append(shortKey(state.mediumKeys.get(i)));
            }
        } else {
            sb.append("no_medium");
        }
        sb.append(" / ").append(shortKey(state.effectKey));
        return sb.toString();
    }

    private static String shortKey(String key) {
        if (key == null) return "none";
        int dot = key.lastIndexOf('.');
        if (dot >= 0 && dot + 1 < key.length()) return key.substring(dot + 1);
        int colon = key.lastIndexOf(':');
        if (colon >= 0 && colon + 1 < key.length()) return key.substring(colon + 1);
        return key;
    }

    private static void cleanup() {
        long now = System.currentTimeMillis();
        RUNTIME_BY_CAST.entrySet().removeIf(e -> now - e.getValue().timestamp > CACHE_TTL_MS);
        LAST_RUNTIME_BY_CASTER.entrySet().removeIf(e -> now - e.getValue().timestamp > CACHE_TTL_MS);
    }

    private static void sendStatus(EntityLivingBase caster, String text) {
        if (caster instanceof EntityPlayerMP) {
            ((EntityPlayerMP) caster).sendStatusMessage(new TextComponentString(text), true);
        }
    }
}
