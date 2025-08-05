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
import thaumcraft.common.lib.utils.EntityUtils;

public class EntityAuraNode extends Entity {
    public static final List<Aspect> ALL_ASPECTS;
    static {
        List<Aspect> tmp = new ArrayList<>(Aspect.aspects.values());
        tmp.sort(Comparator.comparing(Aspect::getTag));
        ALL_ASPECTS = Collections.unmodifiableList(tmp);
    }
    public static final double CHANCE_SINISTER = 0.0167+0.0167;   
    public static final double CHANCE_HUNGRY   = 0.0056+0.0056;   
    public static final double CHANCE_UNSTABLE = 0.0167+0.0167;  
    public static final double CHANCE_PURE     = 0.0167+0.0167;  
    public static final double CHANCE_TAINT    = 0.0028+0.0028;  
    public static final double CHANCE_NORMAL   = 1.0 - (CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE + CHANCE_PURE);
    
    private static final DataParameter<Integer> NODE_SIZE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.VARINT);
    private static final DataParameter<String> ASPECT_TYPE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
    private static final DataParameter<String> ASPECT_TYPE_2 = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
    private static final DataParameter<Byte> NODE_TYPE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.BYTE);
    public static final DataParameter<String> FIXED_ASPECT_ORDER = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
    public static final DataParameter<Byte> REGEN_TYPE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.BYTE);


    private int tickCounter = -1;
    private int checkDelay   = -1;
    private List<EntityAuraNode> neighbours;
    public boolean stablized = false;
    private transient boolean spawned = false;

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
        dataManager.register(NODE_SIZE,     0);
        dataManager.register(ASPECT_TYPE,   "");
        dataManager.register(ASPECT_TYPE_2, "");
        dataManager.register(NODE_TYPE,     (byte)0);
        dataManager.register(FIXED_ASPECT_ORDER,""); 
        dataManager.register(REGEN_TYPE, (byte)0); 

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

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        NodeType.nodeTypes[getNodeType()].performTickEvent(this);
        if (tickCounter++ > 200) {
            tickCounter = 0;
            NodeType.nodeTypes[getNodeType()].performPeriodicEvent(this);
        }

        if (ticksExisted % 80 == 0) {
            regenerateAspects();
        }

        checkAdjacentNodes();

        if (motionX != 0.0 || motionY != 0.0 || motionZ != 0.0) {
            motionX *= 0.8;
            motionY *= 0.8;
            motionZ *= 0.8;
            super.move(MoverType.SELF, motionX, motionY, motionZ);
        }
    }

    private void regenerateAspects() {
        byte regenType = dataManager.get(REGEN_TYPE);
        float regenMult = 0.1f; // Normal
        if (regenType == 1) regenMult = 1.0f;   // Fast
        if (regenType == 2) regenMult = 0.05f;   // Slow
        if (regenType == 3) return; 
        if (this.stablized) { 
            regenMult *= 0.5f;
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
        if (changed) updateSyncAspects();
    }

    public AspectList getOriginalAspects() {
        return originalAspects;
    }
    
    
    public void enforceAspectLimit() {
        int t = getNodeType();
        if (t == 1) { // Sinister
            if (nodeAspects.size() == 1) {
                Aspect current = getMainAspect();
                if (current != null && (current.equals(Aspect.UNDEAD) || current.equals(Aspect.DARKNESS))) {
                    if (nodeAspects.getAmount(current) != getNodeSize()) {
                        int diff = getNodeSize() - nodeAspects.getAmount(current);
                        if (diff > 0) nodeAspects.add(current, diff);
                        else if (diff < 0) nodeAspects.reduce(current, -diff);
                        updateSyncAspects();
                    }
                }
            }
        } else if (t == 2) { // Hungry
            if (nodeAspects.size() == 1 && nodeAspects.getAmount(Aspect.VOID) == getNodeSize() * 2) {
                int requiredVoid = getNodeSize() * 2;
                int curVoid = nodeAspects.getAmount(Aspect.VOID);
                if (curVoid < requiredVoid) {
                    nodeAspects.add(Aspect.VOID, requiredVoid - curVoid);
                    updateSyncAspects();
                }
            }
        } else if (t == 3) { // Pure
            if (nodeAspects.size() == 1 &&
                (nodeAspects.getAmount(Aspect.LIFE) == getNodeSize() ||
                 nodeAspects.getAmount(Aspect.AURA) == getNodeSize())) {
                Aspect current = getMainAspect();
                if (current != null && (current.equals(Aspect.LIFE) || current.equals(Aspect.AURA))) {
                    if (nodeAspects.getAmount(current) != getNodeSize()) {
                        int diff = getNodeSize() - nodeAspects.getAmount(current);
                        if (diff > 0) nodeAspects.add(current, diff);
                        else if (diff < 0) nodeAspects.reduce(current, -diff);
                        updateSyncAspects();
                    }
                }
            }
        } else if (t == 4) { // Taint
            if (nodeAspects.size() == 1 && nodeAspects.getAmount(Aspect.FLUX) == getNodeSize()) {
                int diff = getNodeSize() - nodeAspects.getAmount(Aspect.FLUX);
                if (diff > 0) nodeAspects.add(Aspect.FLUX, diff);
                else if (diff < 0) nodeAspects.reduce(Aspect.FLUX, -diff);
                updateSyncAspects();
            }
        }
    }


    public void addAspectToOrderIfMissing(Aspect asp) {
        List<Aspect> order = getFixedAspectOrder();
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
        if (csv == null || csv.isEmpty()) return Collections.emptyList();
        List<Aspect> list = new ArrayList<>();
        for (String tag : csv.split(",")) {
            Aspect a = Aspect.getAspect(tag);
            if (a != null) list.add(a);
        }
        return list;
    }

    private void checkAdjacentNodes() {
        if (neighbours == null || checkDelay < ticksExisted) {
            neighbours = EntityUtils.getEntitiesInRange(
                world, posX, posY, posZ, this, EntityAuraNode.class, 32.0
            );
            checkDelay = ticksExisted + 750;
        }
        if (stablized) return;
    
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
                        eater.updateSyncAspects();
                        eaten.updateSyncAspects();
                    }

                    if (eaten.nodeAspects.visSize() <= 0) {
                        int bonus = (int) Math.sqrt(eaten.getNodeSize());
                        eater.setNodeSize(eater.getNodeSize() + bonus);

                        Aspect a1 = eater.getMainAspect();
                        Aspect a2 = eaten.getMainAspect();
                        Aspect comb = AspectHelper.getCombinationResult(a1, a2);
                        if (comb != null && rand.nextDouble() < Math.sqrt(eaten.getNodeSize()) / 100.0) {
                            eater.nodeAspects.add(comb, bonus);
                            eater.addAspectToOrderIfMissing(comb);
                            eater.originalAspects.add(comb, bonus);
                            eater.updateSyncAspects();
                        }

                        int myType = eater.getNodeType();
                        int otherType = eaten.getNodeType();
                        boolean canChangeType = (myType == 0);

                        if (canChangeType) {
                            if ((myType == 0 && otherType != 0 && rand.nextInt(3) == 0)
                                    || (myType != 0 && otherType != 0 && rand.nextInt(100) < Math.sqrt(eaten.getNodeSize() / 2.0))) {
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
    
        enforceAspectLimit();
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
    public void setNodeSize(int sz)    { dataManager.set(NODE_SIZE, sz); }

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
        updateSyncAspects();
    }


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
        int min = Math.max(1, (int)(domAmount * 0.2));
        int max = Math.max(min, (int)(domAmount * 0.7));
        if (max == min) max++; 
        int secAmount;
        do {
            secAmount = min + rand.nextInt(max - min + 1);
        } while (secAmount == domAmount);
        return secAmount;
    }

    public static void assignRandomNodeAspects(AspectList aspectList, int nodeSize, java.util.Random rand) {
        aspectList.aspects.clear();
    
        List<Aspect> primals = new ArrayList<>(Aspect.getPrimalAspects());
        List<Aspect> compounds = new ArrayList<>(Aspect.getCompoundAspects());
    
        float f = rand.nextFloat();
        int count = (f < 0.75f) ? 1 : (f < 0.95f ? 2 : 3);
    
        Aspect dominant = primals.remove(rand.nextInt(primals.size()));
        int domAmount = getMainAspectAmount(nodeSize, rand);
        aspectList.aspects.put(dominant, domAmount);
    
        Aspect[] already = new Aspect[] { dominant, null, null };
    
        for (int i = 1; i < count; i++) {
            Aspect asp = null;
            if (rand.nextFloat() < 0.55f && !compounds.isEmpty()) {
                asp = getRandomCompoundAspectTC4(compounds, rand);
            }
            if (asp == null && !primals.isEmpty()) {
                asp = primals.remove(rand.nextInt(primals.size()));
            }
            if (asp != null && !aspectList.aspects.containsKey(asp)) {
                int amt = getSecondaryAspectAmount(domAmount, rand);
                aspectList.aspects.put(asp, amt);
                already[i] = asp;
            }
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
        if (f < 0.5f && !gen1.isEmpty()) return gen1.get(rand.nextInt(gen1.size()));         // 50% gen1
        else if (f < 0.75f && !gen2.isEmpty()) return gen2.get(rand.nextInt(gen2.size()));   // 25% gen2
        else if (f < 0.875f && !gen3.isEmpty()) return gen3.get(rand.nextInt(gen3.size()));  // 12.5% gen3
        else if (!gen4.isEmpty()) return gen4.get(rand.nextInt(gen4.size()));                // 12.5% gen4+
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
        int base = 100 / 3;
        setNodeSize(2 + base + rand.nextInt(2 + base));
        double r = rand.nextDouble();
        int type;
        if      (r < CHANCE_SINISTER)  type = 1; // NTDark (Sinister)
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY)   type = 2; // NTHungry
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE) type = 5; // NTUnstable
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE + CHANCE_PURE) type = 3; // NTPure
        else if (r < CHANCE_SINISTER + CHANCE_HUNGRY + CHANCE_UNSTABLE + CHANCE_PURE + CHANCE_TAINT) type = 4; // NTTaint
        else type = 0; // NTNormal
    
        dataManager.set(NODE_TYPE, (byte)type);
        if (type == 1 || type == 2 || type == 3 || type == 4 || type == 5|| type == 6) {
            randomizeSpecialNode(type);
        } else {
            randomizeNormalNode();
        }
        originalAspects = nodeAspects.copy();

        float regenRnd = rand.nextFloat();
        byte regenType;
        if (regenRnd < 0.07f)        regenType = 1; // Fast (7%)
        else if (regenRnd < 0.14f)   regenType = 2; // Slow (7%)
        else if (regenRnd < 0.20f)   regenType = 3; // Fading (6%)
        else                         regenType = 0; // Normal
        dataManager.set(REGEN_TYPE, regenType);


    
        enforceAspectLimit();
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
        assignRandomNodeAspects(nodeAspects, getNodeSize(), rand);
        List<Aspect> initialOrder = Arrays.stream(nodeAspects.getAspectsSortedByAmount()).collect(Collectors.toList());
        setFixedAspectOrder(initialOrder);
        updateSyncAspects();
    }
    
    private void randomizeSpecialNode(int type) {
        nodeAspects.aspects.clear();
        switch (type) {
            case 1: // Sinister/Dark
                nodeAspects.aspects.clear();
                Aspect dark = rand.nextBoolean() ? Aspect.UNDEAD : Aspect.DARKNESS;
                nodeAspects.add(dark, getNodeSize());
                break;
            case 2: // Hungry
                nodeAspects.aspects.clear();
                nodeAspects.add(Aspect.VOID, getNodeSize() * 2);
                break;
            case 3: // Pure
                nodeAspects.aspects.clear();
                Aspect pure = rand.nextBoolean() ? Aspect.LIFE : Aspect.AURA;
                nodeAspects.add(pure, getNodeSize());
                break;
            case 4: // Taint
                nodeAspects.aspects.clear();
                nodeAspects.add(Aspect.FLUX, getNodeSize());
                break;
            case 5: // Unstable
                nodeAspects.aspects.clear();
                assignRandomNodeAspects(nodeAspects, getNodeSize(), rand);
                break;
        }
        List<Aspect> initialOrder = Arrays.stream(nodeAspects.getAspectsSortedByAmount()).collect(Collectors.toList());
        setFixedAspectOrder(initialOrder);
        updateSyncAspects();
    }
    
}