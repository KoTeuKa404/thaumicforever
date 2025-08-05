// // src/main/java/com/koteuka404/thaumicforever/EntityAuraNode.java
// package com.koteuka404.thaumicforever;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.Iterator;
// import java.util.List;

// import net.minecraft.entity.Entity;
// import net.minecraft.entity.MoverType;
// import net.minecraft.nbt.NBTTagCompound;
// import net.minecraft.network.datasync.DataParameter;
// import net.minecraft.network.datasync.DataSerializers;
// import net.minecraft.network.datasync.EntityDataManager;
// import net.minecraft.util.DamageSource;
// import net.minecraft.util.math.MathHelper;
// import net.minecraft.world.World;
// import thaumcraft.api.aspects.Aspect;
// import thaumcraft.api.aspects.AspectHelper;
// import thaumcraft.api.aspects.AspectList;
// import thaumcraft.common.lib.utils.EntityUtils;

// public class EntityAuraNode extends Entity {
//     public static final List<Aspect> ALL_ASPECTS;
//     static {
//         List<Aspect> tmp = new ArrayList<>(Aspect.aspects.values());
//         tmp.sort(Comparator.comparing(Aspect::getTag));
//         ALL_ASPECTS = Collections.unmodifiableList(tmp);
//     }

//     private static final DataParameter<Integer> NODE_SIZE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.VARINT);
//     private static final DataParameter<String> ASPECT_TYPE =EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
//     private static final DataParameter<String> ASPECT_TYPE_2 = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.STRING);
//     private static final DataParameter<Byte> NODE_TYPE = EntityDataManager.createKey(EntityAuraNode.class, DataSerializers.BYTE);

//     private int tickCounter = -1;
//     private int checkDelay   = -1;
//     private List<EntityAuraNode> neighbours;
//     public boolean stablized = false;
//     private transient boolean spawned = false;

//     private final AspectList eatenAspects = new AspectList();
//     private final AspectList nodeAspects  = new AspectList();

//     public EntityAuraNode(World worldIn) {
//         super(worldIn);
//         setSize(0.5f, 0.5f);
//         isImmuneToFire = true;
//         noClip = true;
//         this.spawned = true;

//     }

//     @Override
//     protected void entityInit() {
//         dataManager.register(NODE_SIZE,     0);
//         dataManager.register(ASPECT_TYPE,   "");
//         dataManager.register(ASPECT_TYPE_2, "");
//         dataManager.register(NODE_TYPE,     (byte)0);
//     }

//     @Override
// public void onUpdate() {
//     if (spawned && getNodeSize() == 0) {
//         spawned = false;
//         randomizeNode();
//         return;
//     }

//     if (tickCounter < 0) tickCounter = rand.nextInt(200);

//     prevPosX = posX;
//     prevPosY = posY;
//     prevPosZ = posZ;

//     NodeType.nodeTypes[getNodeType()].performTickEvent(this);
//     if (tickCounter++ > 200) {
//         tickCounter = 0;
//         NodeType.nodeTypes[getNodeType()].performPeriodicEvent(this);
//     }

//     checkAdjacentNodes();

//     if (motionX != 0.0 || motionY != 0.0 || motionZ != 0.0) {
//         motionX *= 0.8;
//         motionY *= 0.8;
//         motionZ *= 0.8;
//         super.move(MoverType.SELF, motionX, motionY, motionZ);
//     }
// }

    
// public void enforceAspectLimit() {
//     int t = getNodeType();
//     if (t == 1) { // Sinister
//         nodeAspects.aspects.clear();
//         Aspect current = getAspect();
//         if (current != null && (current.equals(Aspect.UNDEAD) || current.equals(Aspect.DARKNESS))) {
//             nodeAspects.add(current, getNodeSize());
//         } else {
//             Aspect chosen = rand.nextBoolean() ? Aspect.UNDEAD : Aspect.DARKNESS;
//             nodeAspects.add(chosen, getNodeSize());
//         }
//         updateSyncAspects();
//     } else if (t == 2) { // Hungry
//         nodeAspects.aspects.clear();
//         nodeAspects.add(Aspect.VOID, getNodeSize() * 2);
//         updateSyncAspects();
//     } else if (t == 3) { // Pure
//         nodeAspects.aspects.clear();
//         Aspect current = getAspect();
//         if (current != null && (current.equals(Aspect.LIFE) || current.equals(Aspect.AURA))) {
//             nodeAspects.add(current, getNodeSize());
//         } else {
//             Aspect chosen = rand.nextBoolean() ? Aspect.LIFE : Aspect.AURA;
//             nodeAspects.add(chosen, getNodeSize());
//         }
//         updateSyncAspects();
//     }
//     // For Normal and all others - нічого не міняй!
// }





 
// private void checkAdjacentNodes() {
//     if (neighbours == null || checkDelay < ticksExisted) {
//         neighbours = EntityUtils.getEntitiesInRange(
//             world, posX, posY, posZ, this, EntityAuraNode.class, 32.0
//         );
//         checkDelay = ticksExisted + 750;
//     }
//     if (stablized) return;

//     try {
//         Iterator<EntityAuraNode> it = neighbours.iterator();
//         while (it.hasNext()) {
//             EntityAuraNode other = it.next();
//             if (other == null || other.isDead) {
//                 it.remove();
//                 continue;
//             }
//             if (other.stablized) continue;

//             double xd = posX - other.posX;
//             double yd = posY - other.posY;
//             double zd = posZ - other.posZ;
//             double distSq = xd*xd + yd*yd + zd*zd;
//             double threshold = (getNodeSize() + other.getNodeSize()) * 1.5;

//             if (distSq < threshold && distSq > 0.1) {
//                 float f = (float)threshold;
//                 motionX += -xd / distSq / f * (other.getNodeSize() / 50.0);
//                 motionY += -yd / distSq / f * (other.getNodeSize() / 50.0);
//                 motionZ += -zd / distSq / f * (other.getNodeSize() / 50.0);
//                 continue;
//             }

//             if (distSq <= 0.1 && getNodeSize() >= other.getNodeSize() && !world.isRemote) {
//                 int bonus = (int)Math.sqrt(other.getNodeSize());
//                 setNodeSize(getNodeSize() + bonus);

//                 Aspect a1 = getDominantAspect();
//                 Aspect a2 = other.getDominantAspect();
//                 Aspect comb = AspectHelper.getCombinationResult(a1, a2);
//                 if (comb != null && rand.nextDouble() < Math.sqrt(other.getNodeSize())/100.0) {
//                     nodeAspects.add(comb, bonus);
//                 }

//                 int myType = getNodeType();
//                 int otherType = other.getNodeType();
//                 boolean canChangeType = (myType == 0); 

//                 if (canChangeType) {
//                     if ((myType == 0 && otherType != 0 && rand.nextInt(3)==0)
//                         || (myType != 0 && otherType != 0
//                             && rand.nextInt(100)<Math.sqrt(other.getNodeSize()/2.0))) {
//                         setNodeType(otherType);
//                     }
//                 }


//                 other.setDead();
//                 updateSyncAspects();
//             }
//         }
//     } catch (Exception ignored) {}

//     enforceAspectLimit();
// }


//     public String getAspectTag()       { return dataManager.get(ASPECT_TYPE); }
//     public void   setAspectTag(String tag)  { dataManager.set(ASPECT_TYPE,   tag == null ? "" : tag); }

//     public String getAspectTag2()      { return dataManager.get(ASPECT_TYPE_2); }
//     public void   setAspectTag2(String tag) { dataManager.set(ASPECT_TYPE_2, tag == null ? "" : tag); }

//     public Aspect getAspect()          { return Aspect.getAspect(getAspectTag()); }
//     public Aspect getSecondAspect()    { return Aspect.getAspect(getAspectTag2()); }
    
//     public List<Aspect> getSecondaryAspects() {
//         String tagStr = getAspectTag2();
//         List<Aspect> list = new ArrayList<>();
//         if (tagStr == null || tagStr.isEmpty()) return list;
    
//         for (String tag : tagStr.split(",")) {
//             Aspect asp = Aspect.getAspect(tag.trim());
//             if (asp != null && !asp.equals(getAspect())) {
//                 list.add(asp);
//             }
//         }
//         return list;
//     }
    
//     public AspectList getEatenAspects() { return eatenAspects; }
//     public AspectList getNodeAspects()  { return nodeAspects; }

//     public int getNodeSize()           { return dataManager.get(NODE_SIZE); }
//     public void setNodeSize(int sz)    { dataManager.set(NODE_SIZE, sz); }

//     public int getNodeType()           { return dataManager.get(NODE_TYPE); }

//     public void setNodeType(int t) {
//     int clamped = MathHelper.clamp(t, 0, NodeType.nodeTypes.length - 1);
//     dataManager.set(NODE_TYPE, (byte)clamped);

//     // Генеруй правильний набір аспектів для нового типу
//     if (clamped == 1 || clamped == 2 || clamped == 3) {
//         randomizeSpecialNode(clamped);
//     } else {
//         randomizeNormalNode();
//     }
//     enforceAspectLimit();
// }

    

//     public Aspect getDominantAspect() {
//         Aspect a = getAspect(); // з ASPECT_TYPE
//         return a != null ? a : Aspect.VOID;
//     }
    


//     public void updateSyncAspects() {
//         Aspect[] sorted = nodeAspects.getAspectsSortedByAmount();
    
//         // Основний аспект у ASPECT_TYPE
//         if (sorted.length > 0) setAspectTag(sorted[0].getTag());
//         else setAspectTag("");
    
//         // Другорядні аспекти у ASPECT_TYPE_2 разом із кількостями
//         if (sorted.length > 1) {
//             StringBuilder sb = new StringBuilder();
//             for (int i = 1; i < sorted.length; i++) {
//                 if (i > 1) sb.append(',');
//                 sb.append(sorted[i].getTag()).append(':').append(nodeAspects.getAmount(sorted[i]));
//             }
//             setAspectTag2(sb.toString());
//         } else {
//             setAspectTag2("");
//         }
//     }
    
    
    

//     @Override public boolean isPushedByWater()      { return false; }
//     @Override public boolean isImmuneToExplosions() { return true; }
//     @Override public boolean hitByEntity(Entity e)   { return false; }
//     @Override public boolean attackEntityFrom(DamageSource s, float a) { return false; }
//     @Override public void addVelocity(double x,double y,double z)    {}
//     @Override public void move(MoverType m,double x,double y,double z){ super.move(m,x,y,z); }

//     @Override
//     protected void writeEntityToNBT(NBTTagCompound tag) {
//         tag.setInteger("size", getNodeSize());
//         tag.setByte("type", (byte) getNodeType());
//         tag.setString("aspect", getAspectTag());
//         tag.setString("aspect2", getAspectTag2());
    
    
//         NBTTagCompound eaten = new NBTTagCompound();
//         eatenAspects.writeToNBT(eaten);
//         tag.setTag("eatenAspects", eaten);
    
//         NBTTagCompound active = new NBTTagCompound();
//         nodeAspects.writeToNBT(active);
//         tag.setTag("nodeAspects", active);
//     }
    

//     @Override
//     protected void readEntityFromNBT(NBTTagCompound tag) {    
//         setNodeSize(tag.getInteger("size"));
//         setNodeType(tag.getByte("type"));
//         setAspectTag(tag.getString("aspect"));
//         if (tag.hasKey("aspect2")) setAspectTag2(tag.getString("aspect2"));
    
//         if (tag.hasKey("eatenAspects", 10))
//             eatenAspects.readFromNBT(tag, "eatenAspects");
    
//         if (tag.hasKey("nodeAspects", 10))
//             nodeAspects.readFromNBT(tag, "nodeAspects");
    
//         updateSyncAspects(); // оновлює getAspect(), getAspect2()
//     }
    


//     @Override
//     public void setPositionAndRotation(double x,double y,double z,float yaw,float pitch) {
//         setPosition(x, y, z);
//         setRotation(yaw, pitch);
//     }

//     @Override
//     public boolean canRenderOnFire() { return false; }



//     public static int getDominantAspectAmount(int nodeSize) {
//         return nodeSize; // або можна додати розкид, якщо треба
//     }
    
//     public static int getSecondaryAspectAmount(int domAmount, java.util.Random rand) {
//         int min = Math.max(1, (int)(domAmount * 0.2));
//         int max = Math.max(min, (int)(domAmount * 0.7));
//         int secAmount;
//         do {
//             secAmount = min + rand.nextInt(max - min + 1);
//         } while (secAmount == domAmount); // не дозволяй однакове значення!
//         return secAmount;
//     }
    

//     public static void assignRandomNodeAspects(AspectList aspectList, int nodeSize, java.util.Random rand) {
//         aspectList.aspects.clear();
    
//         List<Aspect> pool = new ArrayList<>();
//         pool.addAll(Aspect.getPrimalAspects());
//         pool.addAll(Aspect.getCompoundAspects());
    
//         if (pool.isEmpty()) return;
    
//         Aspect dominant = pool.remove(rand.nextInt(pool.size()));
//         int domAmount = getDominantAspectAmount(nodeSize);
//         aspectList.aspects.put(dominant, domAmount);
    
//         // Додаємо другий аспект з жорстким значенням 1 (для тесту)
//         if (!pool.isEmpty() && rand.nextFloat() < 0.75f) {
//             Aspect secondary = null;
//             for (int i = 0; i < 10 && !pool.isEmpty(); i++) {
//                 Aspect candidate = pool.get(rand.nextInt(pool.size()));
//                 if (!candidate.equals(dominant)) {
//                     secondary = candidate;
//                     break;
//                 }
//             }
//             if (secondary != null) {
//                 int secAmount = 1; // <-- Ось тут жорстко 1
//                 aspectList.aspects.put(secondary, secAmount);
//             }
//         }
//     }
    

//     public void randomizeNode() {
//         int base = 100 / 3;
//         setNodeSize(2 + base + rand.nextInt(2 + base));
//         int type = rand.nextInt(100) < 60 && NodeType.nodeTypes.length > 1
//             ? 1 + rand.nextInt(NodeType.nodeTypes.length - 1)
//             : 0;
//         setNodeType(type); // тільки цей виклик!
//     }
    
    
//     private void randomizeNormalNode() {
//         assignRandomNodeAspects(nodeAspects, getNodeSize(), rand);
//         updateSyncAspects();
//     }
    
    
    
    
//     private void randomizeSpecialNode(int type) {
//         nodeAspects.aspects.clear();
    
//         if (type == 1) {
//             Aspect chosen = rand.nextBoolean() ? Aspect.UNDEAD : Aspect.DARKNESS;
//             nodeAspects.add(chosen, getNodeSize());
//         } else if (type == 2) {
//             nodeAspects.add(Aspect.VOID, getNodeSize() * 2);
//         } else if (type == 3) {
//             Aspect chosen = rand.nextBoolean() ? Aspect.LIFE : Aspect.AURA;
//             nodeAspects.add(chosen, getNodeSize());
//         }
//         updateSyncAspects();
//     }
    
// }



// // src/main/java/com/koteuka404/thaumicforever/RenderAuraNode.java
// package com.koteuka404.thaumicforever;

// import java.util.List;

// import org.lwjgl.opengl.GL11;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.GlStateManager;
// import net.minecraft.client.renderer.entity.Render;
// import net.minecraft.client.renderer.entity.RenderManager;
// import net.minecraft.client.resources.I18n;
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.util.math.MathHelper;
// import net.minecraftforge.fml.relauncher.Side;
// import net.minecraftforge.fml.relauncher.SideOnly;
// import thaumcraft.api.aspects.Aspect;
// import thaumcraft.client.lib.UtilsFX;
// import thaumcraft.common.items.tools.ItemThaumometer;
// import thaumcraft.common.lib.utils.EntityUtils;

// @SideOnly(Side.CLIENT)
// public class RenderAuraNode extends Render<EntityAuraNode> {
//     public static final ResourceLocation texture =
//         new ResourceLocation("thaumcraft", "textures/misc/auranodes.png");

//     public RenderAuraNode(RenderManager rm) {
//         super(rm);
//         this.shadowSize = 0.0f;
//     }

//     @Override
//     public void doRender(EntityAuraNode entity, double x, double y, double z, float fq, float pticks) {
//         if (entity.isDead) return;

//         double vr = 8000.0;
//         boolean canSee = EntityUtils.hasRevealer(Minecraft.getMinecraft().getRenderViewEntity());
//         if (!canSee) {
//             canSee = Minecraft.getMinecraft().player.getHeldItemMainhand() != null
//                 && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemThaumometer
//                 && EntityUtils.isVisibleTo(0.8f, Minecraft.getMinecraft().getRenderViewEntity(), entity, 16.0f);
//             vr = 300.0;
//         }
//         if (!canSee) return;

//         double d = entity.getDistance(Minecraft.getMinecraft().getRenderViewEntity());
//         if (d > vr) return;

//         float alpha = 1.0f - (float)Math.min(1.0, d / (vr * 0.9));
//         int color = 0x888888;
//         int blend = 1;
//         int type  = 1;
//         float size = 0.15f + entity.getNodeSize() / (100f * 1.5f);

//         Aspect dom = entity.getDominantAspect();
//         if (dom != null) {
//             color = dom.getColor();
//             blend = dom.getBlend();
//             type  = 1 + entity.getNodeType();
//         }


//         // базовые квадраты
//         GlStateManager.pushMatrix();
//         bindTexture(texture);
//         GlStateManager.disableDepth();
//         UtilsFX.renderFacingQuad(entity.posX, entity.posY, entity.posZ,
//             32, 32, entity.ticksExisted % 32, size, color, 0.75f * alpha, blend, pticks);
//         float s = 1f - MathHelper.sin((entity.ticksExisted + pticks) / 8f) / 5f;
//         UtilsFX.renderFacingQuad(entity.posX, entity.posY, entity.posZ,
//             32, 32, 800 + entity.ticksExisted % 16, s * size * 0.7f, color, 0.9f * alpha, blend, pticks);
//         UtilsFX.renderFacingQuad(entity.posX, entity.posY, entity.posZ,
//             32, 32, 32 * type + entity.ticksExisted % 32, size / 3f, 0xFFFFFF,
//             alpha, type == 2 ? 771 : 1, pticks);
//         GlStateManager.enableDepth();
//         GlStateManager.popMatrix();

//         // теги аспектов
//         if (d < 30.0) {
//             float sc = 1f - (float)Math.min(1.0, d / 25.0);
//             GlStateManager.pushMatrix();
//             GlStateManager.translate(x, y, z);
//             GlStateManager.scale(0.025 * sc, 0.025 * sc, 0.025 * sc);
//             UtilsFX.rotateToPlayer();
//             GL11.glRotatef(180f, 0f, 0f, 1f);
//             GL11.glColor4f(1f, 1f, 1f, 1f);
        
//             // головний аспект зліва (-12)
//             Aspect main = entity.getAspect();
//             if (main != null) {
//                 UtilsFX.drawTag(-12, -32, main, entity.getNodeSize(), 0, 0.0);
//             }
        
//             List<Aspect> secondaries = entity.getSecondaryAspects();
//             if (!secondaries.isEmpty()) {
//                 int offset = 4; // зсув правіше від основного
//                 for (Aspect asp : secondaries) {
//                     if (asp == null || asp.equals(main)) continue;
//                     UtilsFX.drawTag(offset, -32, asp, entity.getNodeSize(), 0, 0.0);
//                     offset += 16;
//                 }
//             }
        
//             // напис типу
//             GlStateManager.scale(0.5, 0.5, 0.5);
//             String text = I18n.format("nodetype." + entity.getNodeType());
//             int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
//             Minecraft.getMinecraft().fontRenderer.drawString(
//                 text, -sw / 2f, -72f, 0xFFFFFF, false);
//             GlStateManager.popMatrix();
//         }
        
//     }

//     @Override
//     protected ResourceLocation getEntityTexture(EntityAuraNode entity) {
//         return texture;
//     }
// }
