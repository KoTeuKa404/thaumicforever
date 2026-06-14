package com.koteuka404.thaumicforever.tile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.golems.IGolemAPI;

public class TileBigJar extends TileEntity implements ITickable, IAspectContainer, IAspectSource, IEssentiaTransport {
    public static final int MAX_BRAINS = 6;
    private static final int MIND_CAPACITY_PER_BRAIN = 100;
    private static final int XP_CAPACITY_PER_BRAIN = 2000;
    private static final int SUCTION_AMOUNT = 128;
    private static final int MINIMUM_SUCTION = 32;
    private static final int GOLEM_CHECK_INTERVAL = 20;
    private static final int GOLEM_INCITE_TIME = 60;
    private static final int GOLEM_MUTINY_TIME = 400;
    private static final double GOLEM_INCITE_RANGE = 6.0D;
    private static final double GOLEM_INCITE_RANGE_SQ = GOLEM_INCITE_RANGE * GOLEM_INCITE_RANGE;
    private static final double GOLEM_MUTINY_RANGE_SQ = 16.0D * 16.0D;

    private BlockPos origin;
    private int brainCount = 1;
    private int mindEssentia;
    private int xp;
    private int eatDelay;
    private int golemCheckDelay;
    private int golemInciteTicks;
    private int incitedGolemId = -1;
    private int golemMutinyTicks;
    private int mutinousGolemId = -1;
    private double inciteTargetX;
    private double inciteTargetY;
    private double inciteTargetZ;
    private final float[] brainOffsetX = new float[MAX_BRAINS];
    private final float[] brainOffsetY = new float[MAX_BRAINS];
    private final float[] brainOffsetZ = new float[MAX_BRAINS];

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public int getBrainCount() {
        return brainCount;
    }

    public int getMindEssentia() {
        return mindEssentia;
    }

    public int getMindEssentiaCapacity() {
        return Math.max(1, brainCount) * MIND_CAPACITY_PER_BRAIN;
    }

    public int getXp() {
        return xp;
    }

    public int getXpCapacity() {
        return Math.max(1, brainCount) * XP_CAPACITY_PER_BRAIN;
    }

    public int addExperience(int amount) {
        if (amount <= 0 || xp >= getXpCapacity()) {
            return 0;
        }
        int added = Math.min(amount, getXpCapacity() - xp);
        xp += added;
        markDirty();
        sync();
        return added;
    }

    public boolean consumeExperience(int amount) {
        if (amount <= 0) return true;
        if (xp < amount) return false;
        xp -= amount;
        markDirty();
        sync();
        return true;
    }

    public boolean addBrain() {
        if (brainCount >= MAX_BRAINS) {
            return false;
        }
        brainCount++;
        xp = Math.min(xp, getXpCapacity());
        markDirty();
        sync();
        return true;
    }

    @Override
    public void update() {
        if (world == null) {
            return;
        }

        if (xp > getXpCapacity()) {
            xp = getXpCapacity();
        }

        Entity closest = null;
        if (xp < getXpCapacity()) {
            closest = getClosestXPOrb();
            if (closest != null && eatDelay == 0) {
                double dx = (pos.getX() + 1.0 - closest.posX) / 25.0;
                double dy = (pos.getY() + 1.0 - closest.posY) / 25.0;
                double dz = (pos.getZ() + 1.0 - closest.posZ) / 25.0;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (distance > 0.0001D) {
                    double pull = 1.0D - distance;
                    if (pull > 0.0D) {
                        pull *= pull;
                        closest.motionX += dx / distance * pull * 0.3D;
                        closest.motionY += dy / distance * pull * 0.5D;
                        closest.motionZ += dz / distance * pull * 0.3D;
                    }
                }
            }
        }

        if (world.isRemote) {
            return;
        }

        updateGolemIncitement();

        if (eatDelay > 0) {
            eatDelay--;
            return;
        }

        if (xp >= getXpCapacity()) {
            return;
        }

        List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(
                EntityXPOrb.class,
                new AxisAlignedBB(pos.getX() - 0.1D, pos.getY() - 0.1D, pos.getZ() - 0.1D,
                        pos.getX() + 2.1D, pos.getY() + 2.1D, pos.getZ() + 2.1D)
        );
        if (orbs.isEmpty()) {
            return;
        }

        boolean changed = false;
        for (EntityXPOrb orb : orbs) {
            if (orb == null || orb.isDead || xp >= getXpCapacity()) {
                continue;
            }
            int added = addExperience(orb.getXpValue());
            if (added > 0) {
                orb.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.1F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
                orb.setDead();
                changed = true;
            }
        }

        if (changed) {
            markDirty();
            sync();
        }
    }

    public Entity getClosestXPOrb() {
        double closestDistance = Double.MAX_VALUE;
        Entity closest = null;
        List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(
                EntityXPOrb.class,
                new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2)
                        .grow(8.0D, 8.0D, 8.0D)
        );
        for (EntityXPOrb orb : orbs) {
            double distance = getDistanceSq(orb.posX, orb.posY, orb.posZ);
            if (distance < closestDistance) {
                closest = orb;
                closestDistance = distance;
            }
        }
        return closest;
    }

    public boolean hasInciteTarget() {
        return golemInciteTicks > 0;
    }

    public double getInciteTargetX() {
        return inciteTargetX;
    }

    public double getInciteTargetY() {
        return inciteTargetY;
    }

    public double getInciteTargetZ() {
        return inciteTargetZ;
    }

    public float getBrainOffsetX(int index) {
        return index >= 0 && index < brainOffsetX.length ? brainOffsetX[index] : 0.0F;
    }

    public float getBrainOffsetY(int index) {
        return index >= 0 && index < brainOffsetY.length ? brainOffsetY[index] : 0.0F;
    }

    public float getBrainOffsetZ(int index) {
        return index >= 0 && index < brainOffsetZ.length ? brainOffsetZ[index] : 0.0F;
    }

    public void setBrainOffsets(int index, float x, float y, float z) {
        if (index < 0 || index >= brainOffsetX.length) return;
        this.brainOffsetX[index] = x;
        this.brainOffsetY[index] = y;
        this.brainOffsetZ[index] = z;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (origin != null) {
            compound.setInteger("OriginX", origin.getX());
            compound.setInteger("OriginY", origin.getY());
            compound.setInteger("OriginZ", origin.getZ());
        }
        compound.setInteger("BrainCount", brainCount);
        compound.setInteger("MindEssentia", mindEssentia);
        compound.setInteger("XP", xp);
        compound.setInteger("xp", xp);
        compound.setInteger("GolemInciteTicks", golemInciteTicks);
        compound.setInteger("IncitedGolemId", incitedGolemId);
        compound.setInteger("GolemMutinyTicks", golemMutinyTicks);
        compound.setInteger("MutinousGolemId", mutinousGolemId);
        compound.setDouble("InciteTargetX", inciteTargetX);
        compound.setDouble("InciteTargetY", inciteTargetY);
        compound.setDouble("InciteTargetZ", inciteTargetZ);
        for (int i = 0; i < MAX_BRAINS; i++) {
            compound.setFloat("BrainOffsetX" + i, brainOffsetX[i]);
            compound.setFloat("BrainOffsetY" + i, brainOffsetY[i]);
            compound.setFloat("BrainOffsetZ" + i, brainOffsetZ[i]);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("OriginX")) {
            origin = new BlockPos(
                compound.getInteger("OriginX"),
                compound.getInteger("OriginY"),
                compound.getInteger("OriginZ")
            );
        }
        brainCount = compound.hasKey("BrainCount") ? compound.getInteger("BrainCount") : 1;
        if (brainCount < 1) {
            brainCount = 1;
        } else if (brainCount > MAX_BRAINS) {
            brainCount = MAX_BRAINS;
        }
        mindEssentia = compound.hasKey("MindEssentia") ? compound.getInteger("MindEssentia") : 0;
        mindEssentia = Math.max(0, Math.min(mindEssentia, getMindEssentiaCapacity()));
        if (compound.hasKey("XP")) {
            xp = compound.getInteger("XP");
        } else if (compound.hasKey("xp")) {
            xp = compound.getInteger("xp");
        } else {
            xp = 0;
        }
        xp = Math.max(0, Math.min(xp, getXpCapacity()));
        golemInciteTicks = Math.max(0, compound.getInteger("GolemInciteTicks"));
        incitedGolemId = compound.hasKey("IncitedGolemId") ? compound.getInteger("IncitedGolemId") : -1;
        golemMutinyTicks = Math.max(0, compound.getInteger("GolemMutinyTicks"));
        mutinousGolemId = compound.hasKey("MutinousGolemId") ? compound.getInteger("MutinousGolemId") : -1;
        inciteTargetX = compound.getDouble("InciteTargetX");
        inciteTargetY = compound.getDouble("InciteTargetY");
        inciteTargetZ = compound.getDouble("InciteTargetZ");
        for (int i = 0; i < MAX_BRAINS; i++) {
            brainOffsetX[i] = compound.getFloat("BrainOffsetX" + i);
            brainOffsetY[i] = compound.getFloat("BrainOffsetY" + i);
            brainOffsetZ[i] = compound.getFloat("BrainOffsetZ" + i);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public void sync() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public AspectList getAspects() {
        return mindEssentia > 0 ? new AspectList().add(Aspect.MIND, mindEssentia) : new AspectList();
    }

    @Override
    public void setAspects(AspectList aspects) {
        mindEssentia = aspects == null ? 0 : Math.max(0, Math.min(aspects.getAmount(Aspect.MIND), getMindEssentiaCapacity()));
        markDirty();
        sync();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return Aspect.MIND.equals(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia >= getMindEssentiaCapacity()) return 0;
        int added = Math.min(amount, getMindEssentiaCapacity() - mindEssentia);
        mindEssentia += added;
        markDirty();
        sync();
        return amount - added;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia < amount) return false;
        mindEssentia -= amount;
        markDirty();
        sync();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        if (!doesContainerContain(aspects)) return false;
        int amount = aspects == null ? 0 : aspects.getAmount(Aspect.MIND);
        return amount <= 0 || takeFromContainer(Aspect.MIND, amount);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return Aspect.MIND.equals(aspect) && mindEssentia >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        if (aspects == null) return true;
        for (Aspect aspect : aspects.getAspects()) {
            int amount = aspects.getAmount(aspect);
            if (amount <= 0) continue;
            if (!Aspect.MIND.equals(aspect) || amount > mindEssentia) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return Aspect.MIND.equals(aspect) ? mindEssentia : 0;
    }

    @Override
    public boolean isBlocked() {
        return false;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return true;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return Aspect.MIND;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return mindEssentia < getMindEssentiaCapacity() ? SUCTION_AMOUNT : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia <= 0) return 0;
        int taken = Math.min(amount, mindEssentia);
        mindEssentia -= taken;
        markDirty();
        sync();
        return taken;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!Aspect.MIND.equals(aspect) || amount <= 0 || mindEssentia >= getMindEssentiaCapacity()) return 0;
        int added = Math.min(amount, getMindEssentiaCapacity() - mindEssentia);
        mindEssentia += added;
        markDirty();
        sync();
        return added;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return Aspect.MIND;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return mindEssentia;
    }

    @Override
    public int getMinimumSuction() {
        return MINIMUM_SUCTION;
    }

    private void updateGolemIncitement() {
        updateGolemMutiny();

        if (golemInciteTicks > 0) {
            Entity golem = world.getEntityByID(incitedGolemId);
            EntityPlayer owner = getGolemOwnerPlayer(golem);
            if (!isValidGolemTarget(golem) || owner == null || owner.isDead || golem.getDistanceSq(pos) > GOLEM_INCITE_RANGE_SQ) {
                clearGolemIncitement();
                return;
            }

            inciteTargetX = golem.posX;
            inciteTargetY = golem.posY + golem.height * 0.5D;
            inciteTargetZ = golem.posZ;
            golemInciteTicks--;
            if (golemInciteTicks <= 0) {
                startGolemMutiny(golem);
                clearGolemIncitement();
            } else if (golemInciteTicks % 10 == 0) {
                sync();
            }
            return;
        }

        if (brainCount <= 0) {
            return;
        }

        if (golemCheckDelay > 0) {
            golemCheckDelay--;
            return;
        }
        golemCheckDelay = GOLEM_CHECK_INTERVAL;

        if (world.rand.nextInt(100) != 0) {
            return;
        }

        Entity golem = findNearestOwnedGolem();
        if (golem == null) {
            return;
        }

        incitedGolemId = golem.getEntityId();
        golemInciteTicks = GOLEM_INCITE_TIME;
        inciteTargetX = golem.posX;
        inciteTargetY = golem.posY + golem.height * 0.5D;
        inciteTargetZ = golem.posZ;
        markDirty();
        sync();
    }

    private Entity findNearestOwnedGolem() {
        AxisAlignedBB box = new AxisAlignedBB(pos).grow(GOLEM_INCITE_RANGE);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
        Entity nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity entity : entities) {
            if (!isValidGolemTarget(entity) || getGolemOwnerPlayer(entity) == null) {
                continue;
            }
            double distance = entity.getDistanceSq(pos);
            if (distance < best) {
                best = distance;
                nearest = entity;
            }
        }
        return nearest;
    }

    private boolean isValidGolemTarget(Entity entity) {
        if (entity == null || entity.isDead) {
            return false;
        }
        if (entity instanceof IGolemAPI) {
            return true;
        }
        String name = entity.getClass().getName().toLowerCase();
        return name.contains("thaumcraft") && name.contains("golem");
    }

    private EntityPlayer getGolemOwnerPlayer(Entity golem) {
        UUID owner = extractOwnerUuid(golem);
        return owner == null ? null : world.getPlayerEntityByUUID(owner);
    }

    private void turnGolemAgainstOwner(Entity golem, EntityPlayer owner) {
        if (golem instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) golem;
            living.setAttackTarget(owner);
            living.setRevengeTarget(owner);
            living.getNavigator().tryMoveToEntityLiving(owner, 1.1D);
            living.getLookHelper().setLookPositionWithEntity(owner, 30.0F, 30.0F);
            if (golem instanceof IGolemAPI) {
                ((IGolemAPI) golem).swingArm();
            }
        } else if (golem instanceof EntityLivingBase) {
            ((EntityLivingBase) golem).setRevengeTarget(owner);
        }
    }

    private void startGolemMutiny(Entity golem) {
        mutinousGolemId = golem.getEntityId();
        golemMutinyTicks = GOLEM_MUTINY_TIME;
        EntityPlayer owner = getGolemOwnerPlayer(golem);
        if (owner != null) {
            turnGolemAgainstOwner(golem, owner);
        }
        markDirty();
    }

    private void updateGolemMutiny() {
        if (golemMutinyTicks <= 0) {
            return;
        }

        Entity golem = world.getEntityByID(mutinousGolemId);
        EntityPlayer owner = getGolemOwnerPlayer(golem);
        if (!isValidGolemTarget(golem) || owner == null || owner.isDead || golem.getDistanceSq(pos) > GOLEM_MUTINY_RANGE_SQ) {
            clearGolemMutiny();
            return;
        }

        turnGolemAgainstOwner(golem, owner);
        golemMutinyTicks--;
        if (golemMutinyTicks <= 0) {
            clearGolemMutiny();
        }
    }

    private void clearGolemIncitement() {
        golemInciteTicks = 0;
        incitedGolemId = -1;
        inciteTargetX = 0.0D;
        inciteTargetY = 0.0D;
        inciteTargetZ = 0.0D;
        markDirty();
        sync();
    }

    private void clearGolemMutiny() {
        golemMutinyTicks = 0;
        mutinousGolemId = -1;
        markDirty();
    }

    private UUID extractOwnerUuid(Entity entity) {
        if (entity == null) {
            return null;
        }

        String[] methodCandidates = new String[] {
                "getOwnerUUID", "getOwnerId", "getOwner", "getOwnerEntity", "func_184753_b"
        };
        for (String methodName : methodCandidates) {
            try {
                Method method = entity.getClass().getMethod(methodName);
                Object result = method.invoke(entity);
                UUID uuid = toUuid(result);
                if (uuid != null) {
                    return uuid;
                }
            } catch (Exception ignored) {
            }
        }

        String[] fieldCandidates = new String[] {
                "ownerUUID", "ownerId", "owner", "field_184754_bv"
        };
        for (String fieldName : fieldCandidates) {
            try {
                Field field = entity.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object result = field.get(entity);
                UUID uuid = toUuid(result);
                if (uuid != null) {
                    return uuid;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private UUID toUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID) {
            return (UUID) value;
        }
        if (value instanceof EntityPlayer) {
            return ((EntityPlayer) value).getUniqueID();
        }
        if (value instanceof String) {
            try {
                return UUID.fromString((String) value);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
