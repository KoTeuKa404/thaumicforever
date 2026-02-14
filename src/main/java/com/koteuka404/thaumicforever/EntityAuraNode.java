package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

public class EntityAuraNode extends Entity {
    public static final List<Aspect> ALL_ASPECTS;
    static {
        List<Aspect> tmp = new ArrayList<>(Aspect.aspects.values());
        tmp.sort(Comparator.comparing(Aspect::getTag));
        ALL_ASPECTS = Collections.unmodifiableList(tmp);
    }

    public static final double CHANCE_SINISTER = 0.0167 + 0.0227;
    public static final double CHANCE_HUNGRY   = 0.0056 + 0.0056;
    public static final double CHANCE_UNSTABLE = 0.0167 + 0.0167;
    public static final double CHANCE_PURE     = 0.0167 + 0.0267;
    public static final double CHANCE_TAINT    = 0.0028 + 0.0128;
    public static final double CHANCE_NORMAL   = 1.0 - (CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE + CHANCE_PURE + CHANCE_TAINT);

    private int regenCooldown = 0;

    private static final DataParameter<Integer> NODE_SIZE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.VARINT);
    private static final DataParameter<String> ASPECT_TYPE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
    private static final DataParameter<String> ASPECT_TYPE_2 = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
    private static final DataParameter<Byte> NODE_TYPE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.BYTE);
    public static final DataParameter<String> FIXED_ASPECT_ORDER = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
    public static final DataParameter<Byte> REGEN_TYPE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.BYTE);
    public static final DataParameter<Boolean> TF_CHARGED = EntityDataManager.createKey (EntityAuraNode.class, DataSerializers.BOOLEAN);

    private int tickCounter = -1;
    private int checkDelay   = -1;
    private List<EntityAuraNode> neighbours;
    public boolean stablized = false;
    private transient boolean spawned = false;
    public boolean canEatNodesWhileStabilized = false;

    private final AspectList eatenAspects = new AspectList();
    private final AspectList nodeAspects  = new AspectList();
    private AspectList originalAspects = new AspectList();

    public EntityAuraNode(World worldIn) {
        super(worldIn);
        setSize(0.5f, 0.5f);
        isImmuneToFire = true;
        noClip = true;
        this.spawned = true;
    }

    @Override
    protected void entityInit() {
        dataManager.register(NODE_SIZE, 0);
        dataManager.register(ASPECT_TYPE, "");
        dataManager.register(ASPECT_TYPE_2, "");
        dataManager.register(NODE_TYPE, (byte)0);
        dataManager.register(FIXED_ASPECT_ORDER, "");
        dataManager.register(REGEN_TYPE, (byte)0);
        dataManager.register(TF_CHARGED, false);
    }

    public int getRegenType() {
        return this.getDataManager().get(REGEN_TYPE);
    }

    @Override
    public void onUpdate() {
        if (spawned && getNodeSize() == 0) {
            spawned = false;
            randomizeNode();
            return;
        }

        if (tickCounter < 0) tickCounter = rand.nextInt(200);

        if (stablized) {
            if (motionX != 0.0 || motionY != 0.0 || motionZ != 0.0) {
                motionX *= 0.8;
                motionY *= 0.8;
                motionZ *= 0.8;
                super.move(MoverType.SELF, motionX, motionY, motionZ);
            }
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (!stablized) {
            NodeType.nodeTypes[getNodeType()].performTickEvent(this);
            if (tickCounter++ > 200) {
                tickCounter = 0;
                NodeType.nodeTypes[getNodeType()].performPeriodicEvent(this);
            }
        } else {
            if (tickCounter > 200) tickCounter = 0;
        }

        if (regenCooldown > 0) {
            regenCooldown--;
        } else {
            if (shouldRegenerate()) {
                regenerateAspects();
                regenCooldown = getRegenInterval();
            }
        }

        checkAdjacentNodes();

        if (motionX != 0.0 || motionY != 0.0 || motionZ != 0.0) {
            motionX *= 0.8;
            motionY *= 0.8;
            motionZ *= 0.8;
            super.move(MoverType.SELF, motionX, motionY, motionZ);
        }
    }


    public int getRegenInterval() {
        int base;
        byte regenType = dataManager.get(REGEN_TYPE);

        switch (regenType) {
            case 1: base = 200; break;         // Fast 10s
            case 2: base = 800; break;         // Slow 40s
            case 3: return Integer.MAX_VALUE;  // Fading never
            case 0:
            default: base = 300; break;        // Normal 15s
        }
        if (this.stablized) base *= 2.5;
        return base;
    }

    public boolean shouldRegenerate() {
        if (isDead) return false;
        // if (isTfCharged()) return false;
        if (getRegenType() == 3) return false;
        return true;
    }
    

    private void regenerateAspects() {
        byte regenType = dataManager.get(REGEN_TYPE);
        float regenMult = 0.1f; // Normal
        if (regenType == 1) regenMult = 1.0f;   // Fast
        if (regenType == 2) regenMult = 0.05f;  // Slow
        if (regenType == 3) return;
        if (this.stablized) {
            regenMult *= 0.1f;
        }
        boolean changed = false;
        for (Aspect a : originalAspects.getAspects()) {
            int orig = originalAspects.getAmount(a);
            int cur = nodeAspects.getAmount(a);
            if (cur < orig) {
                int regen = Math.max(1, Math.round(regenMult));
                int newAmt = Math.min(orig, cur + regen);
                nodeAspects.aspects.put(a, newAmt);
                changed = true;
            }
        }
        if (changed) updateSyncAspects(); // resync size
    }

    public AspectList getOriginalAspects() {
        return originalAspects;
    }

    public void enforceAspectLimit() {
        int t = getNodeType();
        if (t == 0 || t == 4 || t == 5) return; // Normal / Taint / Unstable -
    
        int desired = Math.max(1, getNodeSize());
        int currentSum = Math.max(0, nodeAspects.visSize());
        if (getNodeSize() <= 0 && currentSum > 0) desired = currentSum;
        if (getNodeSize() <= 0 && currentSum <= 0) desired = 25;
    
        Aspect forced = null;
    
        if (t == 1) { // Sinister
            Aspect cur = getMainAspect();
            if (cur == Aspect.UNDEAD || cur == Aspect.DARKNESS) forced = cur;
            else forced = rand.nextBoolean() ? Aspect.UNDEAD : Aspect.DARKNESS;
    
        } else if (t == 2) { // Hungry
            forced = Aspect.VOID;
    
        } else if (t == 3) { // Pure
            Aspect cur = getMainAspect();
            if (cur == Aspect.LIFE || cur == Aspect.AURA) forced = cur;
            else forced = rand.nextBoolean() ? Aspect.LIFE : Aspect.AURA;
        }
    
        if (forced == null) return;
    
        nodeAspects.aspects.clear();
        nodeAspects.add(forced, desired);
    
        originalAspects = new AspectList();
        originalAspects.add(forced, desired);
    
        setFixedAspectOrder(java.util.Collections.singletonList(forced));
    
        updateSyncAspects();
    }
    

    public void addAspectToOrderIfMissing(Aspect asp) {
        List<Aspect> order = getFixedAspectOrder();
        if (!(order instanceof ArrayList)) order = new ArrayList<>(order);
        if (!order.contains(asp)) {
            order.add(asp);
            setFixedAspectOrder(order);
        }
    }

    public void setFixedAspectOrder(List<Aspect> order) {
        String csv = order.stream().map(Aspect::getTag).collect(Collectors.joining(","));
        dataManager.set(FIXED_ASPECT_ORDER, csv);
    }

    public List<Aspect> getFixedAspectOrder() {
        String csv = dataManager.get(FIXED_ASPECT_ORDER);
        List<Aspect> list = new ArrayList<>();
        if (csv == null || csv.isEmpty()) return list;
        for (String tag : csv.split(",")) {
            Aspect a = Aspect.getAspect(tag);
            if (a != null) list.add(a);
        }
        return list;
    }
    public boolean isTfCharged() {
        return dataManager.get(TF_CHARGED);
    }
    public void setTfCharged(boolean v) {
        dataManager.set(TF_CHARGED, v);
    }
    private void checkAdjacentNodes() {
        if (neighbours == null || checkDelay < ticksExisted) {
            neighbours = EntityUtils.getEntitiesInRange(
                world, posX, posY, posZ, this, EntityAuraNode.class, 32.0
            );
            checkDelay = ticksExisted + 750;
        }
        if (stablized && !canEatNodesWhileStabilized) return;

        try {
            Iterator<EntityAuraNode> it = neighbours.iterator();
            while (it.hasNext()) {
                EntityAuraNode other = it.next();
                if (other == null || other.isDead) {
                    it.remove();
                    continue;
                }
                if (other.stablized) continue;

                double xd = posX - other.posX;
                double yd = posY - other.posY;
                double zd = posZ - other.posZ;
                double distSq = xd * xd + yd * yd + zd * zd;

                double threshold = (getNodeSize() + other.getNodeSize()) * 1.5;

                if (distSq < threshold && distSq > 0.1) {
                    float f = (float)threshold;
                    motionX += -xd / distSq / f * (other.getNodeSize() / 50.0);
                    motionY += -yd / distSq / f * (other.getNodeSize() / 50.0);
                    motionZ += -zd / distSq / f * (other.getNodeSize() / 50.0);
                    continue;
                }

                if (distSq <= 0.1 && !world.isRemote) {
                    EntityAuraNode nodeA = this;
                    EntityAuraNode nodeB = other;

                    boolean aHungry = nodeA.getNodeType() == 2;
                    boolean bHungry = nodeB.getNodeType() == 2;
                    EntityAuraNode eater, eaten;

                    if (aHungry && !bHungry) {
                        eater = nodeA;
                        eaten = nodeB;
                    } else if (!aHungry && bHungry) {
                        eater = nodeB;
                        eaten = nodeA;
                    } else {
                        int sumA = nodeA.nodeAspects.visSize();
                        int sumB = nodeB.nodeAspects.visSize();
                        eater = (sumA >= sumB) ? nodeA : nodeB;
                        eaten = (sumA >= sumB) ? nodeB : nodeA;
                    }

                    boolean anyStolen = false;
                    for (Aspect asp : eaten.nodeAspects.getAspects()) {
                        int eatAmount = eaten.nodeAspects.getAmount(asp);
                        if (eatAmount > 0) {
                            eater.nodeAspects.add(asp, 1);
                            eaten.nodeAspects.reduce(asp, 1);

                            eater.originalAspects.add(asp, 1);
                            eater.addAspectToOrderIfMissing(asp);
                            anyStolen = true;
                        }
                    }
                    if (anyStolen) {
                        eater.updateSyncAspects(); // size
                        eaten.updateSyncAspects();
                    }

                    if (eaten.nodeAspects.visSize() <= 0) {
                        int bonus = (int) Math.sqrt(Math.max(1, eaten.getNodeSize()));
                        // size = sum 
                        Aspect dom = eater.getMainAspect();
                        if (dom == null) {
                            Aspect[] all = eater.nodeAspects.getAspects();
                            if (all != null && all.length > 0) dom = all[0];
                        }
                        if (dom != null && bonus > 0) {
                            eater.nodeAspects.add(dom, bonus);
                        }

                        Aspect a1 = eater.getMainAspect();
                        Aspect a2 = eaten.getMainAspect();
                        Aspect comb = AspectHelper.getCombinationResult(a1, a2);
                        if (comb != null && rand.nextDouble() < Math.sqrt(Math.max(1, eaten.getNodeSize())) / 100.0) {
                            eater.nodeAspects.add(comb, bonus);
                            eater.addAspectToOrderIfMissing(comb);
                            eater.originalAspects.add(comb, bonus);
                        }

                        int myType = eater.getNodeType();
                        int otherType = eaten.getNodeType();
                        boolean canChangeType = (myType == 0);

                        if (canChangeType) {
                            if ((myType == 0 && otherType != 0 && rand.nextInt(3) == 0)
                                    || (myType != 0 && otherType != 0 && rand.nextInt(100) < Math.sqrt(Math.max(1, eaten.getNodeSize()) / 2.0))) {
                                eater.setNodeType(otherType);
                            }
                        }

                        eaten.setDead();
                        eater.updateSyncAspects();
                    }
                    break;
                }

            }
        } catch (Exception ignored) {}
    }

    public String getAspectTag()       { return dataManager.get(ASPECT_TYPE); }
    public void   setAspectTag(String tag)  { dataManager.set(ASPECT_TYPE,   tag == null ? "" : tag); }

    public String getAspectTag2()      { return dataManager.get(ASPECT_TYPE_2); }
    public void   setAspectTag2(String tag) { dataManager.set(ASPECT_TYPE_2, tag == null ? "" : tag); }

    public Aspect getAspect()          { return Aspect.getAspect(getAspectTag()); }
    public Aspect getSecondAspect()    { return Aspect.getAspect(getAspectTag2()); }

    public List<Aspect> getSecondaryAspects() {
        String tagStr = getAspectTag2();
        List<Aspect> list = new ArrayList<>();
        if (tagStr == null || tagStr.isEmpty()) return list;

        for (String part : tagStr.split(",")) {
            String tag = part.contains(":") ? part.substring(0, part.indexOf(':')) : part;
            Aspect asp = Aspect.getAspect(tag.trim());
            if (asp != null && !asp.equals(getAspect())) {
                list.add(asp);
            }
        }
        return list;
    }

    public int getSecondaryAmount(Aspect asp) {
        String tagStr = getAspectTag2();
        if (tagStr == null || tagStr.isEmpty() || asp == null) return 0;

        for (String part : tagStr.split(",")) {
            String[] split = part.split(":");
            if (split.length == 2 && split[0].equalsIgnoreCase(asp.getTag())) {
                try { return Integer.parseInt(split[1]); } catch (Exception e) { return 0; }
            }
        }
        return 0;
    }

    public AspectList getEatenAspects() { return eatenAspects; }
    public AspectList getNodeAspects()  { return nodeAspects; }

    public int getNodeSize()           { return dataManager.get(NODE_SIZE); }
    public void setNodeSize(int sz)    { dataManager.set(NODE_SIZE, Math.max(0, sz)); }

    public int getNodeType()           { return dataManager.get(NODE_TYPE); }

    public void setNodeType(int t) {
        int clamped = MathHelper.clamp(t, 0, NodeType.nodeTypes.length - 1);
        dataManager.set(NODE_TYPE, (byte)clamped);
    }

    public void updateSyncAspects() {
        List<Aspect> order = getFixedAspectOrder();
        Aspect main = order.isEmpty() ? null : order.get(0);

        if (main != null) {
            int amt = nodeAspects.getAmount(main);
            setAspectTag(main.getTag() + ":" + amt);
        } else {
            setAspectTag("");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < order.size(); i++) {
            Aspect a = order.get(i);
            int amt = nodeAspects.getAmount(a);
            if (amt > 0) {
                if (sb.length() > 0) sb.append(',');
                sb.append(a.getTag()).append(':').append(amt);
            }
        }
        setAspectTag2(sb.toString());

        resyncSizeFromAspects();
    }

    private void resyncSizeFromAspects() {
        int sum = Math.max(0, nodeAspects.visSize());
        if (sum != getNodeSize()) {
            setNodeSize(sum);
        }
    }

    public Aspect getMainAspect() {
        String tagStr = getAspectTag();
        if (tagStr == null || tagStr.isEmpty()) return null;
        String tag = tagStr.contains(":") ? tagStr.substring(0, tagStr.indexOf(':')) : tagStr;
        return Aspect.getAspect(tag.trim());
    }

    public int getMainAspectAmount() {
        String tagStr = getAspectTag();
        if (tagStr == null || tagStr.isEmpty()) return 0;
        if (!tagStr.contains(":")) return 0;
        String[] parts = tagStr.split(":");
        if (parts.length == 2) {
            try {
                return Integer.parseInt(parts[1]);
            } catch (Exception ignored) {}
        }
        return 0;
    }

    @Override public boolean isPushedByWater()      { return false; }
    @Override public boolean isImmuneToExplosions() { return true; }
    @Override public boolean hitByEntity(Entity e)   { return false; }
    @Override public boolean attackEntityFrom(DamageSource s, float a) { return false; }
    @Override public void addVelocity(double x,double y,double z)    {}
    @Override public void move(MoverType m,double x,double y,double z){ super.move(m,x,y,z); }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("size", getNodeSize());
        tag.setByte("type", (byte) getNodeType());

        NBTTagCompound eaten = new NBTTagCompound();
        eatenAspects.writeToNBT(eaten);
        tag.setTag("eatenAspects", eaten);

        NBTTagCompound active = new NBTTagCompound();
        nodeAspects.writeToNBT(active);
        tag.setTag("nodeAspects", active);

        NBTTagCompound orig = new NBTTagCompound();
        originalAspects.writeToNBT(orig);
        tag.setTag("originalAspects", orig);

        tag.setString("fixedAspectOrder", dataManager.get(FIXED_ASPECT_ORDER));
        tag.setByte("regenType", dataManager.get(REGEN_TYPE));

        tag.setBoolean("tfCharged", isTfCharged());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        setNodeSize(tag.getInteger("size"));
        setNodeType(tag.getByte("type"));

        if (tag.hasKey("eatenAspects", 10))
            eatenAspects.readFromNBT(tag.getCompoundTag("eatenAspects"));
        if (tag.hasKey("nodeAspects", 10))
            nodeAspects.readFromNBT(tag.getCompoundTag("nodeAspects"));
        if (tag.hasKey("originalAspects", 10))
            originalAspects.readFromNBT(tag.getCompoundTag("originalAspects"));

        if (tag.hasKey("fixedAspectOrder", 8)) {
            dataManager.set(FIXED_ASPECT_ORDER, tag.getString("fixedAspectOrder"));
        }
        if (tag.hasKey("regenType", 1)) {
            dataManager.set(REGEN_TYPE, tag.getByte("regenType"));
        }
        if (tag.hasKey("tfCharged")) dataManager.set(TF_CHARGED, tag.getBoolean("tfCharged")); 
        updateSyncAspects();    }

    @Override
    public void setPositionAndRotation(double x,double y,double z,float yaw,float pitch) {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }

    @Override
    public boolean canRenderOnFire() { return false; }

    public static int getMainAspectAmount(int nodeSize, java.util.Random rand) {
        return 15 + rand.nextInt(26); // [15;40]
    }

    public static int getSecondaryAspectAmount(int domAmount, java.util.Random rand) {
        final int LOW  = 7;
        final int HIGH = 20;

        int cap = Math.min(HIGH, domAmount - 1);     
        if (cap < LOW) return Math.max(1, domAmount - 1);

        return LOW + rand.nextInt(cap - LOW + 1);
    }

    public static void assignRandomNodeAspects(AspectList aspectList, int nodeSize, java.util.Random rand) {
        aspectList.aspects.clear();
    
        java.util.List<Aspect> primals   = new java.util.ArrayList<>(Aspect.getPrimalAspects());
        java.util.List<Aspect> compounds = new java.util.ArrayList<>(Aspect.getCompoundAspects());
    
        float f = rand.nextFloat();
        int count = (f < 0.75f) ? 1 : (f < 0.95f ? 2 : 3);
    
        Aspect dominant = null;
        if (!compounds.isEmpty() && rand.nextFloat() < 0.30f) {
            dominant = getRandomCompoundAspectTC4(compounds, rand);
        }
        if (dominant == null) {
            dominant = primals.remove(rand.nextInt(primals.size()));
        } else {
            compounds.remove(dominant);
            primals.remove(dominant);
        }
    
        int domAmount = getMainAspectAmount(nodeSize, rand); // 15–40
        aspectList.aspects.put(dominant, domAmount);
    
        for (int i = 1; i < count; i++) {
            Aspect asp = null;
            if (rand.nextFloat() < 0.35f && !compounds.isEmpty()) {
                asp = getRandomCompoundAspectTC4(compounds, rand);
            }
            if (asp == null && !primals.isEmpty()) {
                asp = primals.remove(rand.nextInt(primals.size()));
            }
            if (asp != null && !aspectList.aspects.containsKey(asp)) {
                final int amt;
                if (i == 1) {
                    amt = getSecondaryAspectAmount(domAmount, rand);
                } else {
                    int min = Math.max(1, (int)(domAmount * 0.2));
                    int max = Math.max(min, (int)(domAmount * 0.7));
                    if (max >= domAmount) max = domAmount - 1;
                    if (max < min) max = min;
                    amt = min + rand.nextInt(max - min + 1);
                }
                aspectList.aspects.put(asp, amt);
            }
        }
    
        java.util.List<Aspect> allPrimals = new java.util.ArrayList<>(Aspect.getPrimalAspects());
        java.util.List<Aspect> missing = allPrimals.stream()
                .filter(p -> aspectList.getAmount(p) <= 0)
                .collect(java.util.stream.Collectors.toList());
    
        Aspect target = missing.isEmpty()
                ? allPrimals.get(rand.nextInt(allPrimals.size()))
                : missing.get(rand.nextInt(missing.size()));
    
        int add = 3 + rand.nextInt(4); // 3–6
        aspectList.add(target, add);
    
        if (rand.nextFloat() < 0.25f) {
            Aspect extra = allPrimals.get(rand.nextInt(allPrimals.size()));
            if (extra != target) aspectList.add(extra, 1);
        }
    }
    
    
    private static Aspect getRandomCompoundAspectTC4(List<Aspect> compounds, java.util.Random rand) {
        List<Aspect> gen1 = new ArrayList<>();
        List<Aspect> gen2 = new ArrayList<>();
        List<Aspect> gen3 = new ArrayList<>();
        List<Aspect> gen4 = new ArrayList<>();

        for (Aspect a : compounds) {
            int depth = getAspectDepthTC4(a);
            if (depth == 1) gen1.add(a);
            else if (depth == 2) gen2.add(a);
            else if (depth == 3) gen3.add(a);
            else if (depth >= 4) gen4.add(a);
        }

        float f = rand.nextFloat();
        if (f < 0.5f && !gen1.isEmpty()) return gen1.get(rand.nextInt(gen1.size()));
        else if (f < 0.75f && !gen2.isEmpty()) return gen2.get(rand.nextInt(gen2.size()));
        else if (f < 0.875f && !gen3.isEmpty()) return gen3.get(rand.nextInt(gen3.size()));
        else if (!gen4.isEmpty()) return gen4.get(rand.nextInt(gen4.size()));
        List<Aspect> all = new ArrayList<>();
        all.addAll(gen1); all.addAll(gen2); all.addAll(gen3); all.addAll(gen4);
        if (!all.isEmpty()) return all.get(rand.nextInt(all.size()));
        return null;
    }

    private static int getAspectDepthTC4(Aspect a) {
        if (a.isPrimal()) return 0;
        if (a.getComponents() == null || a.getComponents().length == 0) return 1;
        int max = 0;
        for (Aspect c : a.getComponents()) {
            int d = getAspectDepthTC4(c);
            if (d > max) max = d;
        }
        return max + 1;
    }

    public void randomizeNode() {
        double r = rand.nextDouble();
        int type;
        if      (r < CHANCE_SINISTER)  type = 1; // Dark
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY)                       type = 2; // Hungry
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE)     type = 5; // Unstable
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE + CHANCE_PURE) type = 3; // Pure
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE + CHANCE_PURE + CHANCE_TAINT) type = 4; // Taint
        else type = 0; // Normal
    
        dataManager.set(NODE_TYPE, (byte)type);
        if (type >= 1 && type <= 5) {
            randomizeSpecialNode(type);
        } else {
            randomizeNormalNode();
        }
    
        updateSyncAspects();
    
        originalAspects = nodeAspects.copy();
    
        float regenRnd = rand.nextFloat();
        byte regenType;
        if (regenRnd < 0.07f)        regenType = 1; // Fast 7%
        else if (regenRnd < 0.14f)   regenType = 2; // Slow 7%
        else if (regenRnd < 0.20f)   regenType = 3; // Fading 6%
        else                         regenType = 0; // Normal
        dataManager.set(REGEN_TYPE, regenType);
    
        enforceAspectLimit();
        updateSyncAspects();
    }
    

    public boolean isAllAspectsBelow(int minAmount) {
        Aspect[] aspects = nodeAspects.getAspects();
        if (aspects == null || aspects.length == 0) return false;
        for (Aspect a : aspects) {
            if (nodeAspects.getAmount(a) >= minAmount) return false;
        }
        return true;
    }

    private void randomizeNormalNode() {
        assignRandomNodeAspects(nodeAspects, /*nodeSize*/ 0, rand);
        List<Aspect> initialOrder = Arrays.stream(nodeAspects.getAspectsSortedByAmount()).collect(Collectors.toList());
        setFixedAspectOrder(initialOrder);
    }

    private void randomizeSpecialNode(int type) {
        nodeAspects.aspects.clear();
        int amt = 15 + rand.nextInt(26); // 15..40
        switch (type) {
            case 1: { // Sinister/Dark
                Aspect dark = rand.nextBoolean() ? Aspect.UNDEAD : Aspect.DARKNESS;
                nodeAspects.add(dark, amt);
                break;
            }
            case 2: { // Hungry
                nodeAspects.add(Aspect.VOID, amt);
                break;
            }
            case 3: { // Pure
                Aspect pure = rand.nextBoolean() ? Aspect.LIFE : Aspect.AURA;
                nodeAspects.add(pure, amt);
                break;
            }
            case 4: { // Taint
                Aspect any = ALL_ASPECTS.get(rand.nextInt(ALL_ASPECTS.size()));
                nodeAspects.add(any, amt);
                break;
            }
            case 5: { // Unstable
                assignRandomNodeAspects(nodeAspects, /*nodeSize*/ 0, rand);
                break;
            }
        }
        List<Aspect> initialOrder = Arrays.stream(nodeAspects.getAspectsSortedByAmount()).collect(Collectors.toList());
        setFixedAspectOrder(initialOrder);
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    @Override
    public boolean processInitialInteract(net.minecraft.entity.player.EntityPlayer player, net.minecraft.util.EnumHand hand) {
        net.minecraft.item.ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) return false;

        if (held.getItem() instanceof IWand && !WandHelper.isWandFullyCharged(held, player)) {
            player.setActiveHand(hand);
            return true;
        }

        boolean isPearl = held.getItem() == ItemsTC.primordialPearl;
        if (!isPearl) return false;
    
        if (!world.isRemote) {
            boolean researched = hasPrimordialResearch(player);
            applyPrimordialManipulation(player, researched);
            if (!player.capabilities.isCreativeMode) held.shrink(1);
        }
        return true;
    }
    
    private void addOrRaiseAspectStrict(Aspect a, int add, boolean makePrimary) {
        if (a == null || add <= 0) return;
    
        int before = nodeAspects.getAmount(a);
        int newCur = (before <= 0) ? add : (before + add);
        nodeAspects.aspects.put(a, newCur);
    
        int orig = originalAspects.getAmount(a);
        if (orig < newCur) originalAspects.aspects.put(a, newCur);
    
        addAspectToOrderIfMissing(a);
    
        if (makePrimary) {
            List<Aspect> order = getFixedAspectOrder();
            if (!(order instanceof ArrayList)) order = new ArrayList<>(order);
            order.remove(a);
            order.add(0, a);
            setFixedAspectOrder(order);
        }
    }
    
    private void addOrRaiseAspectStrict(Aspect a, int add) {
        addOrRaiseAspectStrict(a, add, false);
    }
    
    // @Override
    // public boolean canBeCollidedWith() {
    //     return true;
    // }
    
    private boolean hasPrimordialResearch(net.minecraft.entity.player.EntityPlayer player) {
        if (player == null) return false;
        IPlayerKnowledge kn = ThaumcraftCapabilities.getKnowledge(player);
        if (kn == null) return false;
        final String key = "PRIMALNODE";
        return kn.isResearchKnown(key) || kn.isResearchKnown(key.toLowerCase(java.util.Locale.ROOT));
    }
    
    public void applyPrimordialManipulation(@javax.annotation.Nullable net.minecraft.entity.player.EntityPlayer source, boolean researched) {
        java.util.List<Aspect> primals = new java.util.ArrayList<>(Aspect.getPrimalAspects());
        java.util.List<Aspect> missing = primals.stream()
                .filter(p -> nodeAspects.getAmount(p) <= 0)
                .collect(java.util.stream.Collectors.toList());
    
        Aspect target = missing.isEmpty()
                ? primals.get(rand.nextInt(primals.size()))
                : missing.get(rand.nextInt(missing.size()));
    
        int add = researched ? (1 + rand.nextInt(3)) : (1 + rand.nextInt(2)); 
        boolean makePrimary = !missing.isEmpty();
        addOrRaiseAspectStrict(target, add, makePrimary);
    
        if (rand.nextFloat() < (researched ? 0.35f : 0.20f)) {
            Aspect extra = primals.get(rand.nextInt(primals.size()));
            if (extra != target) addOrRaiseAspectStrict(extra, 1, false);
        }
    
        // Fading(3) -> Slow(2) -> Normal(0) -> Fast(1)
        byte rt = (byte) getRegenType();
        byte next = rt;
        float upChance = 0f;

        if (rt == 3) { // Fading -> Pale
            next = 2;
            upChance = 0.50f; // 50%
        } else if (rt == 2) { // Pale -> Normal
            next = 0;
            upChance = 0.50f; // 50%
        } else if (rt == 0) { // Normal -> Bright
            next = 1;
            upChance = 0.20f; // 20%
        }

        if (next != rt && rand.nextFloat() < upChance) {
            dataManager.set(REGEN_TYPE, next);
        }
    
        float power = researched ? 2.5f : 4.0f;
        world.createExplosion(null, posX, posY, posZ, power, false);
        spawnFluxAroundNode(33, 5, source);
    
        enforceAspectLimit();
        updateSyncAspects();
    }
    
    private void spawnFluxAroundNode(int count, int radius, @javax.annotation.Nullable net.minecraft.entity.player.EntityPlayer src) {
        if (world.isRemote || count <= 0 || radius <= 0) return;
    
        for (int i = 0; i < count; i++) {
            int dx = rand.nextInt(radius * 2 + 1) - radius;
            int dz = rand.nextInt(radius * 2 + 1) - radius;
            if ((dx * dx + dz * dz) > (radius * radius)) { i--; continue; }
    
            net.minecraft.util.math.BlockPos p = getPosition().add(dx, 0, dz);
            if (!world.isBlockLoaded(p)) continue;
    
            if (AuraHelper.shouldPreserveAura(world, src, p)) continue;
    
            float base = AuraHelper.getAuraBase(world, p);
            float perPulse = Math.max(0.5f, base * 0.01f * (0.85f + rand.nextFloat() * 0.3f));
    
            AuraHelper.polluteAura(world, p, perPulse, true);
        }
    }
    

}
