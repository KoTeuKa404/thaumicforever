package com.koteuka404.thaumicforever.tile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileCvDefender extends TileEntity implements ITickable, TileNodeTransducer.ICentivisAcceptorAspect {
    private static final Logger LOGGER = LogManager.getLogger("ThaumicForever/CvDefender");

    public static final double MIN_DOME_RADIUS = 4.0D;
    public static final double MAX_DOME_RADIUS = 12.0D;

    /**
     * Compatibility alias for older code. New code should use
     * getCurrentShieldRadius() because the radius is now dynamic.
     */
    @Deprecated
    public static final double DOME_RADIUS = MAX_DOME_RADIUS;

    private static final int ACTIVATION_TICKS = 40;
    private static final int MAX_CV = 100000;

    /**
     * Actual CV/t received by the defender that is required to reach the
     * maximum radius. The transducer already applies OUTPUT_SCALE = 0.20,
     * therefore a node displaying about 50 total primal CV supplies roughly
     * 10 CV/t to the defender.
     *
     * Linear radius table:
     *  0 CV/t   -> 4.0 blocks
     *  2.5 CV/t -> 6.0 blocks
     *  5 CV/t   -> 8.0 blocks
     *  7.5 CV/t -> 10.0 blocks
     * 10 CV/t   -> 12.0 blocks
     */
    private static final float CV_INPUT_FOR_MAX_RADIUS = 10.0F;
    private static final float CV_RATE_SMOOTHING = 0.20F;
    private static final float CV_RATE_DECAY = 0.05F;
    private static final int CV_INPUT_GRACE_TICKS = 40;

    private static final int PROJECTILE_CV_COST = 100;
    private static final int PROJECTILE_ESSENTIA_COST = 300;

    private static final int MAGIC_CV_COST = 250;
    private static final int MAGIC_ESSENTIA_COST = 500;

    private static final int BLOCK_EFFECT_CV_COST = 200;
    private static final int BLOCK_EFFECT_ESSENTIA_COST = 400;

    private static final int EXPLOSION_CV_COST = 1000;
    private static final int EXPLOSION_ESSENTIA_COST = 2000;

    private static final int PASSIVE_CV_COST = 1;
    private static final int PASSIVE_ESSENTIA_COST = 1;
    private static final int ATTACKED_EXTRA_ESSENTIA_COST = 3;

    /*
     * Impetus is consumed directly from the connected Thaumic Augmentation
     * network. There is no fake internal Impetus buffer anymore.
     */
    private static final int MIN_PASSIVE_IMPETUS_COST = 5;
    private static final int MAX_PASSIVE_IMPETUS_COST = 20;
    private static final int PROJECTILE_IMPETUS_COST = 100;
    private static final int MAGIC_IMPETUS_COST = 250;
    private static final int BLOCK_EFFECT_IMPETUS_COST = 200;
    private static final int EXPLOSION_IMPETUS_COST = 1000;
    private static final int IMPETUS_VALIDATE_INTERVAL = 20;

    private static final int PROJECTILE_STRESS_TICKS = 40;
    private static final int MAGIC_STRESS_TICKS = 60;
    private static final int BLOCK_EFFECT_STRESS_TICKS = 50;
    private static final int EXPLOSION_STRESS_TICKS = 100;
    private static final int MAX_ATTACK_STRESS_TICKS = 200;

    private static final int PRAEMUNIO_ESSENTIA_RANGE = 4;
    private static final int CV_PER_PRAEMUNIO = 100;

    private int praemunioCv;
    private int praemunioAspectCv;
    private int activationTicks;
    private int cvReceivedSinceLastUpdate;
    private int ticksSinceCvInput;
    private float smoothedCvPerTick;
    private int attackStressTicks;
    private boolean cvSupplyConnected;
    private boolean impetusSupplyConnected;
    private Object impetusNode;
    private NBTTagCompound savedImpetusNode;

    private transient Method impetusConsumeMethod;
    private transient Field impetusEnergyConsumedField;
    private transient Field impetusPathsField;
    private transient Method impetusValidateMethod;
    private transient Method impetusSyncTransactionsMethod;
    private transient Method impetusDamageTransactionMethod;
    private transient Method impetusSyncDestroyedMethod;

    private long lastTick = -1L;
    private long lastImpetusValidationTick = Long.MIN_VALUE;
    private long lastImpetusConsumed;
    private boolean loggedImpetusError;

    @Override
    public void update() {
        if (world == null || world.isRemote
                || lastTick == world.getTotalWorldTime()) {
            return;
        }

        lastTick = world.getTotalWorldTime();
        updateCvInputRate();

        if (attackStressTicks > 0) {
            attackStressTicks--;
        }

        validateImpetusNetwork(false);

        int passiveEssentiaCost = getPassiveEssentiaCost();
        ensurePraemunioReserve(passiveEssentiaCost);

        boolean nonImpetusReady = cvSupplyConnected
                && praemunioCv >= PASSIVE_CV_COST
                && praemunioAspectCv >= passiveEssentiaCost;

        boolean impetusReady = !requiresImpetus();

        if (requiresImpetus() && nonImpetusReady) {
            impetusReady = consumeImpetusDirect(
                    getPassiveImpetusCost()
            );
        }

        if (nonImpetusReady && impetusReady) {
            praemunioCv = Math.max(
                    0,
                    praemunioCv - PASSIVE_CV_COST
            );
            praemunioAspectCv = Math.max(
                    0,
                    praemunioAspectCv - passiveEssentiaCost
            );

            if (activationTicks < ACTIVATION_TICKS) {
                activationTicks++;
            }

            syncState();
        } else if (activationTicks != 0) {
            activationTicks = 0;
            syncState();
        }
    }

    private int getPassiveImpetusCost() {
        double suppliedRadius = getSuppliedDomeRadius();

        if (suppliedRadius <= MIN_DOME_RADIUS) {
            return MIN_PASSIVE_IMPETUS_COST;
        }

        double normalized = (
                suppliedRadius - MIN_DOME_RADIUS
        ) / (MAX_DOME_RADIUS - MIN_DOME_RADIUS);

        normalized = Math.max(
                0.0D,
                Math.min(1.0D, normalized)
        );

        return (int) Math.ceil(
                MIN_PASSIVE_IMPETUS_COST
                        + normalized
                        * (MAX_PASSIVE_IMPETUS_COST
                        - MIN_PASSIVE_IMPETUS_COST)
        );
    }

    private void updateCvInputRate() {
        float sample = Math.max(
                0,
                cvReceivedSinceLastUpdate
        );
        cvReceivedSinceLastUpdate = 0;

        if (sample > 0.0F) {
            ticksSinceCvInput = 0;

            smoothedCvPerTick += (
                    sample - smoothedCvPerTick
            ) * CV_RATE_SMOOTHING;
            return;
        }

        /*
         * The transducer can deliver fractional CV as occasional pulses
         * instead of one non-zero call every tick. Hold the last measured
         * rate for two seconds so the dome does not disappear between pulses.
         */
        ticksSinceCvInput++;

        if (ticksSinceCvInput > CV_INPUT_GRACE_TICKS) {
            smoothedCvPerTick += (
                    0.0F - smoothedCvPerTick
            ) * CV_RATE_DECAY;

            if (smoothedCvPerTick < 0.001F) {
                smoothedCvPerTick = 0.0F;
            }
        }
    }

    private int getPassiveEssentiaCost() {
        return PASSIVE_ESSENTIA_COST
                + (attackStressTicks > 0
                ? ATTACKED_EXTRA_ESSENTIA_COST
                : 0);
    }

    @Override
    public int acceptCentivis(Aspect aspect, int amount, TileNodeTransducer source) {
        if (amount <= 0) {
            return 0;
        }

        /*
         * Radius depends on the CV offered by the network, not only on how
         * much fits into the internal buffer. Otherwise a full buffer would
         * incorrectly make the shield shrink to zero.
         */
        cvSupplyConnected = true;
        ticksSinceCvInput = 0;
        cvReceivedSinceLastUpdate = Math.min(
                MAX_CV,
                cvReceivedSinceLastUpdate + amount
        );

        int accepted = Math.min(
                amount,
                MAX_CV - praemunioCv
        );

        if (accepted > 0) {
            praemunioCv += accepted;
            syncState();
        }

        return accepted;
    }

    public boolean isShieldActive() {
        return hasRequiredSupplies() && activationTicks >= ACTIVATION_TICKS;
    }

    public boolean isActivating() {
        return hasRequiredSupplies() && activationTicks < ACTIVATION_TICKS;
    }

    /**
     * Radius determined linearly by the recent incoming CV rate.
     *
     * The previous square-root curve compressed the difference between
     * medium and strong nodes. For example, 5 CV/t and 10 CV/t produced
     * radii differing by only about 1.7 blocks.
     */
    public double getSuppliedDomeRadius() {
        /*
         * Never hide an otherwise powered shield just because CV arrives in
         * sparse pulses. Zero measured rate means the minimum radius; larger
         * measured input smoothly increases it toward the maximum.
         */
        if (!cvSupplyConnected || praemunioCv <= 0) {
            return 0.0D;
        }

        double normalized = Math.max(
                0.0D,
                Math.min(
                        1.0D,
                        smoothedCvPerTick
                                / CV_INPUT_FOR_MAX_RADIUS
                )
        );

        return MIN_DOME_RADIUS
                + (MAX_DOME_RADIUS - MIN_DOME_RADIUS)
                * normalized;
    }

    public double getDomeRadius() {
        if (activationTicks <= 0) {
            return 0.0D;
        }

        double activationProgress = Math.min(
                1.0D,
                activationTicks / (double) ACTIVATION_TICKS
        );

        return getSuppliedDomeRadius()
                * activationProgress;
    }

    public double getCurrentShieldRadius() {
        if (!isShieldActive()) {
            return 0.0D;
        }

        return getSuppliedDomeRadius();
    }

    public float getCvInputPerTick() {
        return smoothedCvPerTick;
    }

    public boolean isUnderAttack() {
        return attackStressTicks > 0;
    }

    /**
     * Returns true when the point is inside the shield volume.
     *
     * This is useful for determining which side of the barrier a source or
     * target is on. Do not use this method by itself to delete projectiles,
     * because projectiles created inside the dome must be allowed to move
     * freely until they actually touch the boundary.
     */
    public boolean isInsideDome(BlockPos target) {
        return isInsideDome(
                target.getX() + 0.5D,
                target.getY() + 0.5D,
                target.getZ() + 0.5D
        );
    }

    public boolean isInsideDome(double x, double y, double z) {
        double radius = getCollisionRadius();
        if (radius <= 0.0D) return false;

        double dx = x - (pos.getX() + 0.5D);
        double dy = y - (pos.getY() + 0.5D);
        double dz = z - (pos.getZ() + 0.5D);

        return dx * dx + dy * dy + dz * dz
                <= radius * radius;
    }

    /**
     * Kept for compatibility with older callers.
     *
     * "Protects" means that a position is inside the protected volume. For
     * projectile interception use shouldBlockProjectile/crossesDome instead.
     */
    public boolean protects(BlockPos target) {
        return isInsideDome(target);
    }

    public boolean protects(double x, double y, double z) {
        return isInsideDome(x, y, z);
    }

    /**
     * Checks whether two points are on different sides of the barrier.
     *
     * Source inside + target inside  -> false, action is allowed.
     * Source outside + target outside -> false, action is allowed.
     * Different sides                 -> true, barrier separates them.
     *
     * Use this for instant/raycast spells that do not create a projectile
     * entity.
     */
    public boolean separates(double sourceX, double sourceY, double sourceZ,
                             double targetX, double targetY, double targetZ) {
        return isInsideDome(sourceX, sourceY, sourceZ)
                != isInsideDome(targetX, targetY, targetZ);
    }

    /**
     * Returns true only when the movement segment intersects the spherical
     * boundary. Movement that stays completely inside the dome is allowed.
     *
     * It also catches very fast projectiles that travel from outside to
     * outside but pass through the whole sphere during one tick.
     */
    public boolean crossesDome(double oldX, double oldY, double oldZ,
                               double newX, double newY, double newZ) {
        double radius = getCollisionRadius();
        if (radius <= 0.0D) return false;

        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;

        double startX = oldX - centerX;
        double startY = oldY - centerY;
        double startZ = oldZ - centerZ;

        double endX = newX - centerX;
        double endY = newY - centerY;
        double endZ = newZ - centerZ;

        double radiusSquared = radius * radius;
        double startDistanceSquared = startX * startX
                + startY * startY
                + startZ * startZ;
        double endDistanceSquared = endX * endX
                + endY * endY
                + endZ * endZ;

        boolean startInside = startDistanceSquared <= radiusSquared;
        boolean endInside = endDistanceSquared <= radiusSquared;

        // The projectile/spell remains inside the barrier, so do not block it.
        if (startInside && endInside) {
            return false;
        }

        // One point is inside and the other is outside: the shell was crossed.
        if (startInside != endInside) {
            return true;
        }

        /*
         * Both points are outside. Check the closest point on the movement
         * segment so high-speed projectiles cannot tunnel through the dome.
         */
        double movementX = endX - startX;
        double movementY = endY - startY;
        double movementZ = endZ - startZ;
        double movementLengthSquared = movementX * movementX
                + movementY * movementY
                + movementZ * movementZ;

        if (movementLengthSquared <= 1.0E-10D) {
            return false;
        }

        double projection = -(
                startX * movementX
                        + startY * movementY
                        + startZ * movementZ
        ) / movementLengthSquared;

        if (projection <= 0.0D || projection >= 1.0D) {
            return false;
        }

        double closestX = startX + movementX * projection;
        double closestY = startY + movementY * projection;
        double closestZ = startZ + movementZ * projection;
        double closestDistanceSquared = closestX * closestX
                + closestY * closestY
                + closestZ * closestZ;

        return closestDistanceSquared <= radiusSquared;
    }

    /**
     * Ready-to-use projectile check for handlers that do not keep their own
     * projectile position history.
     *
     * The first tick must not be ignored: a projectile fired close to the
     * shield can cross the complete boundary during that first update.
     */
    public boolean shouldBlockProjectile(Entity projectile) {
        if (projectile == null || projectile.isDead || !isShieldActive()) {
            return false;
        }

        return crossesDome(
                projectile.prevPosX,
                projectile.prevPosY,
                projectile.prevPosZ,
                projectile.posX,
                projectile.posY,
                projectile.posZ
        );
    }

    private double getCollisionRadius() {
        return getCurrentShieldRadius();
    }

    public AxisAlignedBB getDomeBounds() {
        return new AxisAlignedBB(pos).grow(MAX_DOME_RADIUS);
    }

    /**
     * One blocked projectile consumes 100 CV and 300 aspect-reserve CV,
     * equivalent to 3 Praemunio essentia.
     */
    public boolean consumeForProjectile() {
        return consumeAttackCharge(
                PROJECTILE_CV_COST,
                PROJECTILE_ESSENTIA_COST,
                PROJECTILE_IMPETUS_COST,
                PROJECTILE_STRESS_TICKS
        );
    }

    /**
     * Absorbs a direct spell, beam, magical cloud or similar effect.
     * Cost: 250 CV, 5 Praemunio and 250 Impetus.
     */
    public boolean consumeForMagic() {
        return consumeAttackCharge(
                MAGIC_CV_COST,
                MAGIC_ESSENTIA_COST,
                MAGIC_IMPETUS_COST,
                MAGIC_STRESS_TICKS
        );
    }

    /**
     * Absorbs a block-changing focus effect or fluid/fire propagation.
     * Cost: 200 CV, 4 Praemunio and 200 Impetus.
     */
    public boolean consumeForBlockEffect() {
        return consumeAttackCharge(
                BLOCK_EFFECT_CV_COST,
                BLOCK_EFFECT_ESSENTIA_COST,
                BLOCK_EFFECT_IMPETUS_COST,
                BLOCK_EFFECT_STRESS_TICKS
        );
    }

    /**
     * One blocked explosion consumes 1000 CV and 2000 aspect-reserve CV,
     * equivalent to 20 Praemunio essentia.
     */
    public boolean consumeForExplosion() {
        return consumeAttackCharge(
                EXPLOSION_CV_COST,
                EXPLOSION_ESSENTIA_COST,
                EXPLOSION_IMPETUS_COST,
                EXPLOSION_STRESS_TICKS
        );
    }

    private boolean consumeAttackCharge(
            int cvAmount,
            int essentiaReserveAmount,
            int impetusAmount,
            int stressTicks) {
        if (cvAmount <= 0
                || essentiaReserveAmount <= 0
                || impetusAmount < 0) {
            return false;
        }

        ensurePraemunioReserve(essentiaReserveAmount);

        if (praemunioCv < cvAmount
                || praemunioAspectCv < essentiaReserveAmount) {
            return false;
        }

        if (requiresImpetus()
                && !consumeImpetusDirect(impetusAmount)) {
            return false;
        }

        praemunioCv -= cvAmount;
        praemunioAspectCv -= essentiaReserveAmount;

        attackStressTicks = Math.min(
                MAX_ATTACK_STRESS_TICKS,
                attackStressTicks + Math.max(0, stressTicks)
        );

        syncState();
        return true;
    }

    private void ensurePraemunioReserve(int requiredAmount) {
        if (requiredAmount <= 0) {
            return;
        }

        int missing = requiredAmount - praemunioAspectCv;
        int essentiaNeeded = (
                missing + CV_PER_PRAEMUNIO - 1
        ) / CV_PER_PRAEMUNIO;

        while (essentiaNeeded > 0
                && drainNearbyPraemunio()) {
            essentiaNeeded--;
        }
    }

    private boolean drainNearbyPraemunio() {
        int rangeSq = PRAEMUNIO_ESSENTIA_RANGE * PRAEMUNIO_ESSENTIA_RANGE;
        for (int dx = -PRAEMUNIO_ESSENTIA_RANGE; dx <= PRAEMUNIO_ESSENTIA_RANGE; dx++) {
            for (int dy = -PRAEMUNIO_ESSENTIA_RANGE; dy <= PRAEMUNIO_ESSENTIA_RANGE; dy++) {
                for (int dz = -PRAEMUNIO_ESSENTIA_RANGE; dz <= PRAEMUNIO_ESSENTIA_RANGE; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (dx * dx + dy * dy + dz * dz > rangeSq) continue;
                    if (drainPraemunioAt(pos.add(dx, dy, dz))) return true;
                }
            }
        }
        return false;
    }

    private boolean drainPraemunioAt(BlockPos targetPos) {
        TileEntity te = world.getTileEntity(targetPos);
        if (te instanceof IEssentiaTransport) {
            IEssentiaTransport transport = (IEssentiaTransport) te;
            for (EnumFacing face : EnumFacing.values()) {
                if (!transport.canOutputTo(face)) continue;
                if (!isPraemunio(transport.getEssentiaType(face))) continue;
                if (transport.takeEssentia(Aspect.PROTECT, 1, face) >= 1) {
                    praemunioAspectCv = Math.min(MAX_CV, praemunioAspectCv + CV_PER_PRAEMUNIO);
                    syncState();
                    return true;
                }
            }
        }

        if (te instanceof IAspectContainer) {
            IAspectContainer container = (IAspectContainer) te;
            if (container.doesContainerContainAmount(Aspect.PROTECT, 1)
                    && container.takeFromContainer(Aspect.PROTECT, 1)) {
                praemunioAspectCv = Math.min(MAX_CV, praemunioAspectCv + CV_PER_PRAEMUNIO);
                syncState();
                return true;
            }
        }
        return false;
    }

    /**
     * Consumes Impetus directly from connected providers.
     *
     * The first call simulates the transaction so a partial payment does not
     * consume energy while still failing to power the shield. The second call
     * performs the real transaction and synchronizes its visual/network data.
     */
    private boolean consumeImpetusDirect(long amount) {
        if (!requiresImpetus()) {
            return true;
        }
        if (amount <= 0L) {
            return true;
        }
        if (!ensureImpetusNode()) {
            impetusSupplyConnected = false;
            lastImpetusConsumed = 0L;
            return false;
        }

        validateImpetusNetwork(false);

        try {
            Object simulatedResult = impetusConsumeMethod.invoke(
                    impetusNode,
                    amount,
                    true
            );

            long available = readConsumedImpetus(simulatedResult);
            if (available < amount) {
                impetusSupplyConnected = false;
                lastImpetusConsumed = 0L;
                return false;
            }

            Object realResult = impetusConsumeMethod.invoke(
                    impetusNode,
                    amount,
                    false
            );

            long consumed = readConsumedImpetus(realResult);
            lastImpetusConsumed = consumed;
            impetusSupplyConnected = consumed >= amount;

            if (consumed > 0L) {
                syncImpetusTransaction(realResult);
            }

            return consumed >= amount;
        } catch (ReflectiveOperationException
                | RuntimeException error) {
            impetusSupplyConnected = false;
            lastImpetusConsumed = 0L;
            logImpetusError("consume", error);
            return false;
        }
    }

    private long readConsumedImpetus(Object result)
            throws IllegalAccessException {
        if (result == null || impetusEnergyConsumedField == null) {
            return 0L;
        }

        return impetusEnergyConsumedField.getLong(result);
    }

    @SuppressWarnings("unchecked")
    private void syncImpetusTransaction(Object result) {
        if (result == null || impetusPathsField == null) {
            return;
        }

        try {
            Object pathsObject = impetusPathsField.get(result);
            if (!(pathsObject instanceof Map)) {
                return;
            }

            Map<Object, Object> paths =
                    (Map<Object, Object>) pathsObject;

            if (paths.isEmpty()) {
                return;
            }

            impetusSyncTransactionsMethod.invoke(
                    null,
                    paths.keySet()
            );

            for (Map.Entry<Object, Object> entry
                    : paths.entrySet()) {
                if (!(entry.getKey() instanceof Deque)
                        || !(entry.getValue() instanceof Number)) {
                    continue;
                }

                impetusDamageTransactionMethod.invoke(
                        null,
                        entry.getKey(),
                        ((Number) entry.getValue()).longValue()
                );
            }
        } catch (ReflectiveOperationException
                | RuntimeException error) {
            /*
             * Energy was already consumed successfully. A transaction sync
             * failure must not refund or disable the shield.
             */
            logImpetusError("transaction sync", error);
        }
    }

    private void validateImpetusNetwork(boolean force) {
        if (!requiresImpetus()
                || world == null
                || world.isRemote
                || !ensureImpetusNode()) {
            return;
        }

        long now = world.getTotalWorldTime();
        if (!force
                && now - lastImpetusValidationTick
                < IMPETUS_VALIDATE_INTERVAL) {
            return;
        }

        lastImpetusValidationTick = now;

        try {
            impetusValidateMethod.invoke(
                    null,
                    impetusNode,
                    world
            );
        } catch (ReflectiveOperationException
                | RuntimeException error) {
            logImpetusError("validation", error);
        }
    }

    private void logImpetusError(
            String operation,
            Throwable error) {
        if (loggedImpetusError) {
            return;
        }

        loggedImpetusError = true;
        LOGGER.warn(
                "CV Defender Impetus {} failed at {}",
                operation,
                pos,
                error
        );
    }

    public long getLastImpetusConsumed() {
        return lastImpetusConsumed;
    }

    private boolean isImpetusAvailable() {
        return ensureImpetusNode();
    }

    private boolean ensureImpetusNode() {
        if (!Loader.isModLoaded("thaumicaugmentation")
                || world == null) {
            return false;
        }

        try {
            if (impetusNode == null) {
                Class<?> dimensionalPos = Class.forName(
                        "thecodex6824.thaumicaugmentation.api.util.DimensionalBlockPos"
                );
                Constructor<?> dimensionalCtor =
                        dimensionalPos.getConstructor(
                                BlockPos.class,
                                int.class
                        );
                Object location = dimensionalCtor.newInstance(
                        pos,
                        world.provider.getDimension()
                );

                Class<?> consumer = Class.forName(
                        "thecodex6824.thaumicaugmentation.api.impetus.node.prefab.SimpleImpetusConsumer"
                );
                Constructor<?> ctor = consumer.getConstructor(
                        int.class,
                        int.class,
                        dimensionalPos
                );
                impetusNode = ctor.newInstance(
                        8,
                        0,
                        location
                );

                if (savedImpetusNode != null) {
                    Method deserialize = impetusNode.getClass()
                            .getMethod(
                                    "deserializeNBT",
                                    NBTTagCompound.class
                            );
                    deserialize.invoke(
                            impetusNode,
                            savedImpetusNode
                    );
                    savedImpetusNode = null;
                }

                Method init = impetusNode.getClass()
                        .getMethod(
                                "init",
                                net.minecraft.world.World.class
                        );
                init.invoke(impetusNode, world);

                cacheImpetusReflection();
                loggedImpetusError = false;

                if (!world.isRemote) {
                    validateImpetusNetwork(true);
                }
            }

            return true;
        } catch (ReflectiveOperationException
                | LinkageError
                | RuntimeException error) {
            logImpetusError("initialization", error);
            clearImpetusRuntimeState();
            return false;
        }
    }

    private void cacheImpetusReflection()
            throws ReflectiveOperationException {
        Class<?> consumeResult = Class.forName(
                "thecodex6824.thaumicaugmentation.api.impetus.node.ConsumeResult"
        );
        Class<?> impetusNodeInterface = Class.forName(
                "thecodex6824.thaumicaugmentation.api.impetus.node.IImpetusNode"
        );
        Class<?> nodeHelper = Class.forName(
                "thecodex6824.thaumicaugmentation.api.impetus.node.NodeHelper"
        );

        impetusConsumeMethod = impetusNode.getClass()
                .getMethod(
                        "consume",
                        long.class,
                        boolean.class
                );
        impetusEnergyConsumedField = consumeResult.getField(
                "energyConsumed"
        );
        impetusPathsField = consumeResult.getField("paths");

        impetusValidateMethod = nodeHelper.getMethod(
                "validate",
                impetusNodeInterface,
                net.minecraft.world.World.class
        );
        impetusSyncTransactionsMethod = nodeHelper.getMethod(
                "syncAllImpetusTransactions",
                Collection.class
        );
        impetusDamageTransactionMethod = nodeHelper.getMethod(
                "damageEntitiesFromTransaction",
                Deque.class,
                long.class
        );
        impetusSyncDestroyedMethod = nodeHelper.getMethod(
                "syncDestroyedImpetusNode",
                impetusNodeInterface
        );
    }

    private void clearImpetusRuntimeState() {
        impetusNode = null;
        impetusConsumeMethod = null;
        impetusEnergyConsumedField = null;
        impetusPathsField = null;
        impetusValidateMethod = null;
        impetusSyncTransactionsMethod = null;
        impetusDamageTransactionMethod = null;
        impetusSyncDestroyedMethod = null;
        lastImpetusValidationTick = Long.MIN_VALUE;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (isImpetusCapability(capability)) return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (isImpetusCapability(capability) && ensureImpetusNode()) return (T) impetusNode;
        return super.getCapability(capability, facing);
    }

    private boolean isImpetusCapability(Capability<?> capability) {
        if (!Loader.isModLoaded("thaumicaugmentation")) return false;
        try {
            Class<?> capClass = Class.forName("thecodex6824.thaumicaugmentation.api.impetus.node.CapabilityImpetusNode");
            Field field = capClass.getField("IMPETUS_NODE");
            return field.get(null) == capability;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("PraemunioCV", praemunioCv);
        compound.setInteger("PraemunioAspectCV", praemunioAspectCv);
        compound.setLong("LastImpetusConsumed", lastImpetusConsumed);
        compound.setInteger("ActivationTicks", activationTicks);
        compound.setFloat("SmoothedCvPerTick", smoothedCvPerTick);
        compound.setInteger("TicksSinceCvInput", ticksSinceCvInput);
        compound.setInteger("AttackStressTicks", attackStressTicks);
        compound.setBoolean("CvSupplyConnected", cvSupplyConnected);
        compound.setBoolean("ImpetusSupplyConnected", impetusSupplyConnected);
        if (impetusNode != null) {
            try {
                Method serialize = impetusNode.getClass().getMethod("serializeNBT");
                Object data = serialize.invoke(impetusNode);
                if (data instanceof NBTTagCompound) {
                    compound.setTag("ImpetusNode", (NBTTagCompound) data);
                }
            } catch (ReflectiveOperationException ignored) {
                // The optional Thaumic Augmentation API may not expose node persistence.
            }
        }
        return compound;
    }

    private void syncState() {
        markDirty();
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    private boolean requiresImpetus() {
        return Loader.isModLoaded("thaumicaugmentation");
    }

    private boolean isPraemunio(Aspect aspect) {
        return aspect == Aspect.PROTECT
                || (aspect != null && "praemunio".equals(aspect.getTag()));
    }

    private boolean hasRequiredSupplies() {
        boolean impetusReady = !requiresImpetus()
                || impetusSupplyConnected;

        return cvSupplyConnected
                && praemunioCv > 0
                && praemunioAspectCv > 0
                && impetusReady;
    }

    private boolean hasImpetusInput() {
        if (!requiresImpetus() || !ensureImpetusNode()) return false;
        try {
            Method getInputs = impetusNode.getClass().getMethod("getInputLocations");
            Object inputs = getInputs.invoke(impetusNode);
            return inputs instanceof Collection && !((Collection<?>) inputs).isEmpty();
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        praemunioCv = Math.max(0, Math.min(MAX_CV, compound.getInteger("PraemunioCV")));
        praemunioAspectCv = Math.max(0, Math.min(MAX_CV, compound.getInteger("PraemunioAspectCV")));
        lastImpetusConsumed = Math.max(
                0L,
                compound.getLong("LastImpetusConsumed")
        );
        activationTicks = Math.max(
                0,
                Math.min(
                        ACTIVATION_TICKS,
                        compound.getInteger("ActivationTicks")
                )
        );
        smoothedCvPerTick = Math.max(
                0.0F,
                compound.getFloat("SmoothedCvPerTick")
        );
        ticksSinceCvInput = Math.max(
                0,
                compound.getInteger("TicksSinceCvInput")
        );
        attackStressTicks = Math.max(
                0,
                Math.min(
                        MAX_ATTACK_STRESS_TICKS,
                        compound.getInteger("AttackStressTicks")
                )
        );
        cvSupplyConnected = compound.getBoolean("CvSupplyConnected")
                || praemunioCv > 0;
        impetusSupplyConnected = compound.getBoolean("ImpetusSupplyConnected");
        savedImpetusNode = compound.hasKey("ImpetusNode", 10)
                ? compound.getCompoundTag("ImpetusNode")
                : null;

        if (impetusNode != null && savedImpetusNode != null) {
            try {
                Method deserialize = impetusNode.getClass()
                        .getMethod(
                                "deserializeNBT",
                                NBTTagCompound.class
                        );
                deserialize.invoke(
                        impetusNode,
                        savedImpetusNode
                );
                savedImpetusNode = null;
            } catch (ReflectiveOperationException error) {
                logImpetusError("NBT reload", error);
            }
        }
    }

    @Override
    public void invalidate() {
        destroyImpetusNode();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        unloadImpetusNode();
        super.onChunkUnload();
    }

    private void unloadImpetusNode() {
        if (impetusNode == null) {
            return;
        }

        try {
            impetusNode.getClass()
                    .getMethod("unload")
                    .invoke(impetusNode);
        } catch (ReflectiveOperationException error) {
            logImpetusError("chunk unload", error);
        } finally {
            clearImpetusRuntimeState();
        }
    }

    private void destroyImpetusNode() {
        if (impetusNode == null) {
            return;
        }

        try {
            if (world != null
                    && !world.isRemote
                    && impetusSyncDestroyedMethod != null) {
                impetusSyncDestroyedMethod.invoke(
                        null,
                        impetusNode
                );
            }

            impetusNode.getClass()
                    .getMethod("destroy")
                    .invoke(impetusNode);
        } catch (ReflectiveOperationException error) {
            logImpetusError("destroy", error);
        } finally {
            clearImpetusRuntimeState();
        }
    }
}

