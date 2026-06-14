package com.koteuka404.thaumicforever.seal;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.koteuka404.thaumicforever.api.golemcore.GolemCoreHelper;
import com.koteuka404.thaumicforever.golemcore.ArcaneGolemCore;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.seals.SealFiltered;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.casters.ItemFocus;

public class SealArcaneCaster extends SealFiltered implements ISealConfigToggles, ISealConfigArea {
    private static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "items/seals/seal_magical");
    private static final int FILTER_SLOTS = 3;
    private static final int TASK_LIFESPAN = 40;
    private static final int REQUEST_INTERVAL = 80;
    private static final double CAST_RANGE = 16.0D;
    private static final double MIN_OPENING_CAST_RANGE = 6.0D;
    private static final double STANDOFF_RANGE = 10.0D;
    private static final Map<String, Integer> REQUEST_DELAYS = new ConcurrentHashMap<>();

    private final SealToggle[] toggles = new SealToggle[]{
            new SealToggle(true, "tf_arcane_targets_enemies", "thaumicforever.golem.prop.arcane_targets_enemies"),
            new SealToggle(false, "tf_arcane_targets_friends", "thaumicforever.golem.prop.arcane_targets_friends"),
            new SealToggle(false, "tf_arcane_targets_animals", "thaumicforever.golem.prop.arcane_targets_animals"),
            new SealToggle(false, "tf_arcane_targets_players", "thaumicforever.golem.prop.arcane_targets_players")
    };

    @Override
    public String getKey() {
        return "thaumicforever:arcane_caster";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public int getFilterSize() {
        return FILTER_SLOTS;
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (world.isRemote || seal == null || seal.getSealPos() == null) return;

        String key = makeKey(world, seal.getSealPos().pos, seal.getSealPos().face);
        int delay = REQUEST_DELAYS.getOrDefault(key, 0);
        if (delay > 0) {
            REQUEST_DELAYS.put(key, delay - 1);
        } else {
            requestCaster(world, seal);
            REQUEST_DELAYS.put(key, REQUEST_INTERVAL);
        }

        AxisAlignedBB area = GolemHelper.getBoundsForArea(seal).grow(0.5D);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase target : targets) {
            if (!isValidTarget(target) || hasTask(world, seal, target)) continue;

            Task task = new Task(seal.getSealPos(), findCastPosition(world, seal, target));
            task.setData(target.getEntityId());
            task.setPriority(seal.getPriority());
            task.setLifespan((short) TASK_LIFESPAN);
            TaskHandler.addTask(world.provider.getDimension(), task);
        }
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {
        tryCastAtTask(world, golem, task, false);
    }

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        return tryCastAtTask(world, golem, task, true);
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (golem == null || task == null) return false;
        EntityLivingBase target = resolveTarget(golem.getGolemWorld(), task);
        return hasArcaneCore(golem) && isValidTarget(target) && !findCaster(golem).isEmpty();
    }

    @Override
    public void onTaskSuspension(World world, Task task) {}

    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SMART, EnumGolemTrait.DEFT, EnumGolemTrait.SCOUT,};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{2, 1, 0, 3, 4};
    }

    @Override
    public SealToggle[] getToggles() {
        return toggles;
    }

    @Override
    public void setToggle(int indx, boolean value) {
        if (indx >= 0 && indx < toggles.length) {
            toggles[indx].setValue(value);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setBoolean("targetsEnemies", toggles[0].getValue());
        nbt.setBoolean("targetsFriends", toggles[1].getValue());
        nbt.setBoolean("targetsAnimals", toggles[2].getValue());
        nbt.setBoolean("targetsPlayers", toggles[3].getValue());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        toggles[0].setValue(!nbt.hasKey("targetsEnemies") || nbt.getBoolean("targetsEnemies"));
        toggles[1].setValue(nbt.getBoolean("targetsFriends"));
        toggles[2].setValue(nbt.getBoolean("targetsAnimals"));
        toggles[3].setValue(nbt.getBoolean("targetsPlayers"));
    }

    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
        if (!world.isRemote) {
            REQUEST_DELAYS.remove(world.provider.getDimension() + ":" + pos.toLong() + ":" + side.ordinal());
            TaskHandler.getTasks(world.provider.getDimension()).values().removeIf(t ->
                    t.getSealPos() != null && t.getSealPos().pos.equals(pos) && t.getSealPos().face == side);
        }
    }

    private boolean tryCastAtTask(World world, IGolemAPI golem, Task task, boolean complete) {
        if (world.isRemote || golem == null || task == null) {
            return false;
        }
        if (!hasArcaneCore(golem)) {
            return false;
        }

        EntityLivingBase target = resolveTarget(world, task);
        if (!isValidTarget(target)) return false;

        EntityLivingBase golemEntity = golem.getGolemEntity();
        double distanceSq = golemEntity.getDistanceSq(target);
        if (distanceSq > CAST_RANGE * CAST_RANGE) {
            return false;
        }
        if (!complete && distanceSq < MIN_OPENING_CAST_RANGE * MIN_OPENING_CAST_RANGE) {
            return false;
        }

        ItemStack caster = findCaster(golem);
        if (caster.isEmpty() || !(caster.getItem() instanceof ICaster)) return false;

        ICaster casterItem = (ICaster) caster.getItem();
        ItemStack focusStack = casterItem.getFocusStack(caster);
        if (focusStack.isEmpty() || !(focusStack.getItem() instanceof ItemFocus)) return false;

        lookAt(golemEntity, target);
        CasterManager.setCooldown(golemEntity, 10);
        FocusPackage pack = ItemFocus.getPackage(focusStack);
        if (pack == null) return false;
        castPackageAtTarget(golemEntity, target, pack);
        golem.swingArm();
        golem.addRankXp(1);
        task.setSuspended(true);
        if (complete) task.setCompletion(true);
        return true;
    }

    private void requestCaster(World world, ISealEntity seal) {
        for (ItemStack filter : getInv()) {
            if (!filter.isEmpty() && filter.getItem() instanceof ICaster) {
                ItemStack request = filter.copy();
                request.setCount(1);
                GolemHelper.requestProvisioning(world, seal, request);
                return;
            }
        }
    }

    private ItemStack findCaster(IGolemAPI golem) {
        for (ItemStack stack : golem.getCarrying()) {
            if (!stack.isEmpty() && stack.getItem() instanceof ICaster && matchesCasterFilter(stack)) {
                ICaster caster = (ICaster) stack.getItem();
                if (!caster.getFocusStack(stack).isEmpty()) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean hasArcaneCore(IGolemAPI golem) {
        ResourceLocation coreId = GolemCoreHelper.getActiveCoreId(golem);
        return ArcaneGolemCore.ID.equals(coreId);
    }

    private boolean matchesCasterFilter(ItemStack caster) {
        boolean hasFilter = false;
        boolean matched = false;
        for (ItemStack filter : getInv()) {
            if (filter.isEmpty()) continue;
            hasFilter = true;
            if (sameItem(filter, caster)) {
                matched = true;
                break;
            }
        }
        if (!hasFilter) return true;
        return isBlacklist() ? !matched : matched;
    }

    private boolean sameItem(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) return false;
        return ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    private void castPackageAtTarget(EntityLivingBase caster, EntityLivingBase target, FocusPackage sourcePackage) {
        FocusPackage pack = sourcePackage.copy(caster);
        pack.initialize(caster);
        pack.setUniqueID(UUID.randomUUID());
        for (FocusEffect effect : pack.getFocusEffects()) {
            effect.onCast(caster);
        }

        Vec3d source = caster.getPositionVector().addVector(0.0D, caster.getEyeHeight() - 0.1D, 0.0D);
        Vec3d hit = target.getPositionVector().addVector(0.0D, target.height * 0.5D, 0.0D);
        Vec3d direction = hit.subtract(source).normalize();
        Trajectory trajectory = new Trajectory(source, direction);
        RayTraceResult ray = new RayTraceResult(target, hit);

        FocusEngine.runFocusPackage(pack, new Trajectory[]{trajectory}, new RayTraceResult[]{ray});

        if (needsDirectTargetFallback(pack)) {
            int index = 0;
            for (FocusEffect effect : pack.getFocusEffects()) {
                target.hurtResistantTime = 0;
                effect.execute(ray, trajectory, pack.getPower(), index++);
            }
        }
    }

    private boolean needsDirectTargetFallback(FocusPackage pack) {
        for (IFocusElement node : pack.nodes) {
            if (node instanceof FocusMedium && !((FocusMedium) node).hasIntermediary()) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidTarget(EntityLivingBase target) {
        if (target == null || !target.isEntityAlive()) return false;
        if (target instanceof EntityPlayer && !toggles[3].getValue()) return false;

        boolean valid = false;
        if (toggles[0].getValue() && (target instanceof IMob || target instanceof EntityMob)) valid = true;
        if (toggles[1].getValue() && isFriendlyTarget(target)) valid = true;
        if (toggles[2].getValue() && (target instanceof EntityAnimal || target instanceof IAnimals)) valid = true;
        if (toggles[3].getValue() && target instanceof EntityPlayer) valid = true;
        return valid;
    }

    private boolean isFriendlyTarget(EntityLivingBase target) {
        return target instanceof EntityVillager
                || target instanceof EntityIronGolem
                || target instanceof EntitySnowman
                || target instanceof EntityTameable
                || target instanceof IEntityOwnable;
    }

    private boolean hasTask(World world, ISealEntity seal, EntityLivingBase target) {
        return TaskHandler.getTasks(world.provider.getDimension()).values().stream()
                .anyMatch(t -> !t.isSuspended()
                        && !t.isCompleted()
                        && t.getSealPos() != null
                        && t.getSealPos().equals(seal.getSealPos())
                        && (t.getData() == target.getEntityId()
                        || (t.getEntity() != null && t.getEntity().getUniqueID().equals(target.getUniqueID()))));
    }

    private EntityLivingBase resolveTarget(World world, Task task) {
        if (task == null) return null;
        if (task.getEntity() instanceof EntityLivingBase) {
            return (EntityLivingBase) task.getEntity();
        }
        if (world == null || task.getData() == 0) return null;
        Entity entity = world.getEntityByID(task.getData());
        return entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
    }

    private BlockPos findCastPosition(World world, ISealEntity seal, EntityLivingBase target) {
        BlockPos sealPos = seal.getSealPos().pos;
        double dx = sealPos.getX() + 0.5D - target.posX;
        double dz = sealPos.getZ() + 0.5D - target.posZ;
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001D) {
            dx = 1.0D;
            dz = 0.0D;
            len = 1.0D;
        }

        double range = Math.min(STANDOFF_RANGE, CAST_RANGE - 2.0D);
        BlockPos preferred = new BlockPos(
                target.posX + dx / len * range,
                target.posY,
                target.posZ + dz / len * range);
        return findNearestStandable(world, preferred, sealPos);
    }

    private BlockPos findNearestStandable(World world, BlockPos preferred, BlockPos fallback) {
        for (int radius = 0; radius <= 3; radius++) {
            for (int y = -1; y <= 2; y++) {
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.abs(x) != radius && Math.abs(z) != radius && radius != 0) continue;
                        BlockPos pos = preferred.add(x, y, z);
                        if (isStandable(world, pos)) {
                            return pos;
                        }
                    }
                }
            }
        }
        return fallback;
    }

    private boolean isStandable(World world, BlockPos pos) {
        return !world.getBlockState(pos).getMaterial().blocksMovement()
                && !world.getBlockState(pos.up()).getMaterial().blocksMovement()
                && world.getBlockState(pos.down()).getMaterial().blocksMovement();
    }

    private void lookAt(EntityLivingBase golem, EntityLivingBase target) {
        double dx = target.posX - golem.posX;
        double dz = target.posZ - golem.posZ;
        double dy = target.posY + target.getEyeHeight() - (golem.posY + golem.getEyeHeight());
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(dy, horizontal) * 180.0D / Math.PI);
        golem.rotationYaw = yaw;
        golem.rotationYawHead = yaw;
        golem.rotationPitch = pitch;
        if (golem instanceof EntityLiving) {
            ((EntityLiving) golem).getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        }
    }

    private static String makeKey(World world, BlockPos pos, EnumFacing side) {
        return world.provider.getDimension() + ":" + pos.toLong() + ":" + side.ordinal();
    }
}
