package com.koteuka404.thaumicforever.entity;

import java.util.List;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.registry.ModGuiHandler;
import com.koteuka404.thaumicforever.registry.ModItems;
import com.koteuka404.thaumicforever.util.NonPlayerFocusFix;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.foci.FocusMediumBolt;
import thaumcraft.common.items.casters.foci.FocusMediumTouch;
import thaumcraft.common.lib.SoundsTC;

public class EntityArcaneTurret extends EntityOwnedConstruct {
    private static final String TAG_LENS = "Lens";
    private static final String TAG_FLAGS = "TargetFlags";
    private static final String TAG_COOLDOWN = "AttackCooldown";
    private static final DataParameter<Byte> FLAGS = EntityDataManager.createKey(EntityArcaneTurret.class, DataSerializers.BYTE);
    private static final int FLAG_ANIMALS = 0;
    private static final int FLAG_MOBS = 1;
    private static final int FLAG_PLAYERS = 2;
    private static final int FLAG_FRIENDLY = 3;
    private static final int FLAG_FORWARD_FIRE = 4;
    private static final double DEFAULT_MEDIUM_RANGE = 16.0D;
    private static final double BOLT_RANGE = 16.0D;
    private static final double TOUCH_RANGE = 4.0D;
    private static final double LENS_HEIGHT = 0.92D;
    private static final double LENS_FORWARD_OFFSET = 0.72D;

    private int attackCooldown;

    private final ItemStackHandler lensHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return EntityArcaneTurret.this.isLens(stack);
        }
    };

    public EntityArcaneTurret(World worldIn) {
        super(worldIn);
        this.setSize(0.9F, 1.6F);
        this.stepHeight = 0.0F;
        this.experienceValue = 0;
    }

    public EntityArcaneTurret(World worldIn, BlockPos pos) {
        this(worldIn);
        this.setPositionAndRotation(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
    }

    public IItemHandler getLensHandler() {
        return this.lensHandler;
    }

    public ItemStack getLens() {
        return this.lensHandler.getStackInSlot(0);
    }

    public boolean hasLens() {
        return !this.getLens().isEmpty();
    }

    private boolean isLens(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemFocus;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FLAGS, (byte) (1 << FLAG_MOBS));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public void onUpdate() {
        if (this.isAIDisabled()) {
            this.setNoAI(false);
        }
        super.onUpdate();
        this.motionX = 0.0D;
        this.motionZ = 0.0D;
        if (this.motionY > 0.0D) {
            this.motionY = 0.0D;
        }
        this.rotationYaw = this.rotationYawHead;
        if (!this.world.isRemote && !this.isRiding()) {
            tryRideNearbyMinecart();
        }
        if (!this.world.isRemote) {
            if (this.attackCooldown > 0) {
                this.attackCooldown--;
            }
            updateTargetAndShoot();
        }
    }

    private void tryRideNearbyMinecart() {
        List<EntityMinecart> carts = this.world.getEntitiesWithinAABB(
            EntityMinecart.class,
            this.getEntityBoundingBox().grow(0.6D, 0.4D, 0.6D)
        );
        EntityMinecart closest = null;
        double best = Double.MAX_VALUE;

        for (EntityMinecart cart : carts) {
            if (cart == null || cart.isDead || !cart.getPassengers().isEmpty()) {
                continue;
            }

            double distance = this.getDistanceSq(cart);
            if (distance < best) {
                best = distance;
                closest = cart;
            }
        }

        if (closest != null) {
            this.startRiding(closest, true);
        }
    }

    private void updateTargetAndShoot() {
        if (!this.hasLens()) {
            this.setAttackTarget(null);
            return;
        }

        if (this.getForwardFire()) {
            this.setAttackTarget(null);
            if (this.attackCooldown <= 0) {
                shootLens();
            }
            return;
        }

        EntityLivingBase target = this.getAttackTarget();
        if (!isValidTarget(target)) {
            target = findTarget();
            this.setAttackTarget(target);
        }

        if (target == null) {
            return;
        }

        faceTarget(target);
        if (this.attackCooldown <= 0 && canSeeTarget(target)) {
            shootLens();
        }
    }

    private EntityLivingBase findTarget() {
        double range = getLensRange();
        AxisAlignedBB search = this.getEntityBoundingBox().grow(range, range * 0.5D, range);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, search);
        EntityLivingBase best = null;
        double bestDistance = range * range;

        for (EntityLivingBase candidate : entities) {
            if (!isValidTarget(candidate, range)) {
                continue;
            }

            double distance = this.getDistanceSq(candidate);
            if (distance < bestDistance && canSeeTarget(candidate)) {
                bestDistance = distance;
                best = candidate;
            }
        }

        return best;
    }

    private boolean isValidTarget(EntityLivingBase target) {
        return isValidTarget(target, getLensRange());
    }

    private boolean isValidTarget(EntityLivingBase target, double range) {
        if (target == null || target == this || target.isDead || target.getHealth() <= 0.0F) {
            return false;
        }
        if (this.getDistanceSq(target) > range * range) {
            return false;
        }
        if (target instanceof EntityPlayer && ((EntityPlayer) target).isSpectator()) {
            return false;
        }
        if (isFriendlyTarget(target) && !getTargetFriendly()) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            return getTargetPlayer();
        }
        if (target instanceof IMob) {
            return getTargetMob();
        }
        return target instanceof IAnimals && getTargetAnimal();
    }

    private double getLensRange() {
        ItemStack lens = this.getLens();
        if (lens.isEmpty() || !(lens.getItem() instanceof ItemFocus)) {
            return DEFAULT_MEDIUM_RANGE;
        }

        FocusPackage focusPackage = ItemFocus.getPackage(lens);
        if (focusPackage == null) {
            return DEFAULT_MEDIUM_RANGE;
        }

        double range = getPackageRange(focusPackage, 0.0D);
        return range > 0.0D ? range : DEFAULT_MEDIUM_RANGE;
    }

    private double getPackageRange(FocusPackage focusPackage, double currentRange) {
        double range = currentRange;

        for (IFocusElement element : focusPackage.nodes) {
            if (element instanceof FocusMediumRoot) {
                continue;
            }

            if (element instanceof FocusModSplit) {
                double branchRange = range;
                for (FocusPackage splitPackage : ((FocusModSplit) element).getSplitPackages()) {
                    branchRange = Math.max(branchRange, getPackageRange(splitPackage, range));
                }
                return branchRange;
            }

            if (element instanceof FocusPackage) {
                return getPackageRange((FocusPackage) element, range);
            }

            if (element instanceof FocusMedium) {
                range += getMediumRange((FocusMedium) element);
            }
        }

        return range;
    }

    private double getMediumRange(FocusMedium medium) {
        if (medium instanceof FocusMediumBolt) {
            return BOLT_RANGE;
        }
        if (medium.getClass() == FocusMediumTouch.class) {
            return TOUCH_RANGE;
        }

        FocusNode node = medium;
        if (node.getSettingList() != null && node.getSettingList().contains("range")) {
            int configuredRange = node.getSettingValue("range");
            if (configuredRange > 0) {
                return configuredRange;
            }
        }

        return DEFAULT_MEDIUM_RANGE;
    }

    private boolean isFriendlyTarget(EntityLivingBase target) {
        EntityLivingBase owner = this.getOwnerEntity();
        if (owner != null) {
            if (target == owner) {
                return true;
            }
            Team ownerTeam = owner.getTeam();
            Team targetTeam = target.getTeam();
            if (ownerTeam != null && ownerTeam == targetTeam) {
                return true;
            }
        }
        if (target instanceof IEntityOwnable && owner != null) {
            return owner.getUniqueID().equals(((IEntityOwnable) target).getOwnerId());
        }
        return false;
    }

    private boolean canSeeTarget(EntityLivingBase target) {
        Vec3d start = getBeamStart();
        Vec3d end = new Vec3d(target.posX, target.posY + target.height * 0.55D, target.posZ);
        RayTraceResult ray = this.world.rayTraceBlocks(start, end, false, true, false);
        return ray == null || ray.typeOfHit == RayTraceResult.Type.MISS;
    }

    private void faceTarget(EntityLivingBase target) {
        double dx = target.posX - this.posX;
        double dz = target.posZ - this.posZ;
        double dy = target.posY + target.getEyeHeight() - (this.posY + LENS_HEIGHT);
        double horizontal = MathHelper.sqrt(dx * dx + dz * dz);
        this.rotationYaw = this.rotationYawHead = (float) (MathHelper.atan2(dz, dx) * 57.295776D) - 90.0F;
        this.rotationPitch = (float) (-(MathHelper.atan2(dy, horizontal) * 57.295776D));
    }

    private void shootLens() {
        ItemStack lens = this.getLens();
        if (lens.getItem() instanceof ItemFocus) {
            FocusPackage sourcePackage = ItemFocus.getPackage(lens);
            if (sourcePackage != null) {
                castFocus(sourcePackage);
                return;
            }
        }
    }

    private void castFocus(FocusPackage sourcePackage) {
        if (this.world.isRemote) {
            return;
        }

        ItemStack lens = this.getLens();
        if (lens.isEmpty() || !(lens.getItem() instanceof ItemFocus)) {
            return;
        }

        ItemFocus focus = (ItemFocus) lens.getItem();
        float visCost = focus.getVisCost(lens);
        float availableVis = AuraHelper.drainVis(this.world, this.getPosition(), visCost, true);

        if (Math.abs(availableVis - visCost) > 0.00001F) {
            // Do not retry every tick when the local aura cannot pay the cost.
            this.attackCooldown = 10;
            return;
        }

        AuraHelper.drainVis(this.world, this.getPosition(), visCost, false);

        FocusPackage pack = sourcePackage;
        pack.setCasterUUID(this.getUniqueID());
        NonPlayerFocusFix.apply(pack, this);

        ItemStack previousHeldItem = this.getHeldItemMainhand();
        ItemStack casterStack = createCasterStack(lens);
        try {
            // FocusEngine expects the casting entity to actually hold an ICaster.
            this.setHeldItem(EnumHand.MAIN_HAND, casterStack);
            FocusEngine.castFocusPackage(this, pack, true);
            this.attackCooldown = Math.max(1, focus.getActivationTime(lens));
        } finally {
            this.setHeldItem(EnumHand.MAIN_HAND, previousHeldItem);
        }
    }

    private ItemStack createCasterStack(ItemStack lens) {
        ItemStack casterStack = new ItemStack(ItemsTC.casterBasic);
        if (casterStack.getItem() instanceof ICaster) {
            ((ICaster) casterStack.getItem()).setFocus(casterStack, lens.copy());
        }
        return casterStack;
    }

    private Vec3d getBeamStart() {
        Vec3d look = this.getLook(1.0F);
        return new Vec3d(
            this.posX + look.x * LENS_FORWARD_OFFSET,
            this.posY + LENS_HEIGHT + look.y * 0.18D,
            this.posZ + look.z * LENS_FORWARD_OFFSET
        );
    }

    @Override
    public float getEyeHeight() {
        return (float) (LENS_HEIGHT);
    }

    public boolean getTargetAnimal() {
        return getTargetFlag(FLAG_ANIMALS);
    }

    public void setTargetAnimal(boolean value) {
        setTargetFlag(FLAG_ANIMALS, value);
    }

    public boolean getTargetMob() {
        return getTargetFlag(FLAG_MOBS);
    }

    public void setTargetMob(boolean value) {
        setTargetFlag(FLAG_MOBS, value);
    }

    public boolean getTargetPlayer() {
        return getTargetFlag(FLAG_PLAYERS);
    }

    public void setTargetPlayer(boolean value) {
        setTargetFlag(FLAG_PLAYERS, value);
    }

    public boolean getTargetFriendly() {
        return getTargetFlag(FLAG_FRIENDLY);
    }

    public void setTargetFriendly(boolean value) {
        setTargetFlag(FLAG_FRIENDLY, value);
    }

    public boolean getForwardFire() {
        return getTargetFlag(FLAG_FORWARD_FIRE);
    }

    public void setForwardFire(boolean value) {
        setTargetFlag(FLAG_FORWARD_FIRE, value);
    }

    private boolean getTargetFlag(int bit) {
        return (this.dataManager.get(FLAGS) & (1 << bit)) != 0;
    }

    private void setTargetFlag(int bit, boolean value) {
        byte flags = this.dataManager.get(FLAGS);
        if (value) {
            flags = (byte) (flags | (1 << bit));
        } else {
            flags = (byte) (flags & ~(1 << bit));
        }
        this.dataManager.set(FLAGS, flags);
        this.setAttackTarget(null);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() instanceof EntityLivingBase
                && this.isOwner((EntityLivingBase) source.getTrueSource())) {
            EntityLivingBase owner = (EntityLivingBase) source.getTrueSource();
            this.faceTarget(owner);
            this.setAttackTarget(null);
            return false;
        }

        this.rotationYaw += this.getRNG().nextGaussian() * 25.0D;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!this.isOwner(player) || this.isDead) {
            return super.processInteract(player, hand);
        }

        if (!this.world.isRemote) {
            if (player.isSneaking()) {
                this.playSound(SoundsTC.zap, 1.0F, 1.0F);
                this.dropLens();
                if (!player.capabilities.isCreativeMode) {
                    this.entityDropItem(new ItemStack(ModItems.itemArcaneTurret), 0.5F);
                }
                this.setDead();
            } else {
                player.openGui(ThaumicForever.instance, ModGuiHandler.GUI_ARCANE_TURRET, this.world, this.getEntityId(), 0, 0);
            }
        }

        player.swingArm(hand);
        return true;
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        super.move(type, x * 0.1D, y, z * 0.1D);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (!this.world.isRemote) {
            this.dropLens();
            float lootingChance = lootingModifier * 0.15F;

            if (this.rand.nextFloat() < 0.2F + lootingChance) {
                this.entityDropItem(new ItemStack(ItemsTC.mind, 1, 2), 0.5F);
            }
            if (this.rand.nextFloat() < 0.5F + lootingChance) {
                this.entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5F);
            }
            if (this.rand.nextFloat() < 0.5F + lootingChance) {
                this.entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5F);
            }
            if (this.rand.nextFloat() < 0.5F + lootingChance) {
                this.entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5F);
            }
            if (this.rand.nextFloat() < 0.3F + lootingChance) {
                this.entityDropItem(new ItemStack(ItemsTC.plate, 1, 0), 0.5F);
            }
            if (this.rand.nextFloat() < 0.4F + lootingChance) {
                this.entityDropItem(new ItemStack(ItemsTC.plate, 1, 1), 0.5F);
            }
            if (this.rand.nextFloat() < 0.4F + lootingChance) {
                this.entityDropItem(new ItemStack(ItemsTC.plate, 1, 1), 0.5F);
            }
        }
    }

    private void dropLens() {
        ItemStack lens = this.lensHandler.getStackInSlot(0);
        if (!lens.isEmpty()) {
            this.entityDropItem(lens.copy(), 0.5F);
            this.lensHandler.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag(TAG_LENS, this.lensHandler.serializeNBT());
        compound.setByte(TAG_FLAGS, this.dataManager.get(FLAGS));
        compound.setInteger(TAG_COOLDOWN, this.attackCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey(TAG_LENS, 10)) {
            this.lensHandler.deserializeNBT(compound.getCompoundTag(TAG_LENS));
        }
        if (compound.hasKey(TAG_FLAGS)) {
            this.dataManager.set(FLAGS, compound.getByte(TAG_FLAGS));
        }
        this.attackCooldown = compound.getInteger(TAG_COOLDOWN);
    }
}
