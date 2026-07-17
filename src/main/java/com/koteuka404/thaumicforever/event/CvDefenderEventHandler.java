package com.koteuka404.thaumicforever.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import com.koteuka404.thaumicforever.tile.TileCvDefender;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class CvDefenderEventHandler {

    /*
     * Exact positions captured at the beginning of the server tick.
     * Weak keys ensure removed entities are not retained in memory.
     */
    private final Map<Entity, Vec3d> projectileTickStartPositions =
            new WeakHashMap<Entity, Vec3d>();

    private final Map<EntityLivingBase, Vec3d> mobTickStartPositions =
            new WeakHashMap<EntityLivingBase, Vec3d>();

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        World world = event.getWorld();

        if (world == null || world.isRemote || entity == null) {
            return;
        }

        if (isBarrierEffectEntity(entity)) {
            projectileTickStartPositions.put(
                    entity,
                    getEntityCenter(entity)
            );
        }

        if (isBarrierMob(entity)) {
            mobTickStartPositions.put(
                    (EntityLivingBase) entity,
                    getEntityFeet(entity)
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getEntity();
        World world = projectile == null ? null : projectile.world;

        if (world == null
                || world.isRemote
                || projectile.isDead
                || !isProjectileEntity(projectile)) {
            return;
        }

        List<TileCvDefender> defenders = findDefenders(world);
        if (defenders.isEmpty()) {
            return;
        }

        Vec3d previous = getProjectileTickStartPosition(projectile);
        Vec3d impact = getImpactPosition(event, projectile);

        for (TileCvDefender defender : defenders) {
            if (!defender.crossesDome(
                    previous.x,
                    previous.y,
                    previous.z,
                    impact.x,
                    impact.y,
                    impact.z
            )) {
                continue;
            }

            boolean absorbed = isMagicEffectEntity(projectile)
                    ? defender.consumeForMagic()
                    : defender.consumeForProjectile();

            if (absorbed) {
                event.setCanceled(true);
                projectile.setDead();
                projectileTickStartPositions.remove(projectile);
            }

            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        World world = target == null ? null : target.world;

        if (world == null || world.isRemote) {
            return;
        }

        DamageSource damageSource = event.getSource();
        if (damageSource == null) {
            return;
        }

        Vec3d sourcePosition = getDamageOrigin(damageSource);
        if (sourcePosition == null) {
            return;
        }

        Vec3d targetPosition = getEntityCenter(target);

        for (TileCvDefender defender : findDefenders(world)) {
            if (!defender.separates(
                    sourcePosition.x, sourcePosition.y, sourcePosition.z,
                    targetPosition.x, targetPosition.y, targetPosition.z
            )) {
                continue;
            }

            boolean absorbed;
            if (damageSource.isExplosion()) {
                absorbed = defender.consumeForExplosion();
            } else if (isMagicDamage(damageSource)) {
                absorbed = defender.consumeForMagic();
            } else {
                absorbed = defender.consumeForProjectile();
            }

            if (absorbed) {
                event.setCanceled(true);
                removeImmediateEffectEntity(damageSource);
            }
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = event.getWorld();
        Entity source = event.getPlayer();
        if (world == null || world.isRemote || source == null) {
            return;
        }
        if (shouldBlockWorldChange(world, source, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        World world = event.getWorld();
        Entity source = event.getEntity();
        if (world == null || world.isRemote || source == null) {
            return;
        }
        if (shouldBlockWorldChange(world, source, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        World world = event.getWorld();
        if (world == null || world.isRemote) {
            return;
        }

        Vec3d source = blockCenter(event.getLiquidPos());
        Vec3d target = blockCenter(event.getPos());

        for (TileCvDefender defender : findDefenders(world)) {
            if (!defender.separates(
                    source.x, source.y, source.z,
                    target.x, target.y, target.z
            )) {
                continue;
            }
            if (defender.consumeForBlockEffect()) {
                event.setCanceled(true);
            }
            return;
        }
    }

    private boolean shouldBlockWorldChange(World world,
                                           Entity source,
                                           BlockPos targetPos) {
        Vec3d sourcePosition = getAttackSourcePosition(source);
        Vec3d targetPosition = blockCenter(targetPos);

        for (TileCvDefender defender : findDefenders(world)) {
            if (!defender.separates(
                    sourcePosition.x, sourcePosition.y, sourcePosition.z,
                    targetPosition.x, targetPosition.y, targetPosition.z
            )) {
                continue;
            }
            return defender.consumeForBlockEffect();
        }
        return false;
    }

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        if (event.world == null || event.world.isRemote) {
            return;
        }

        if (event.phase == Phase.START) {
            captureTickStartPositions(event.world);
            return;
        }

        if (event.phase != Phase.END) {
            return;
        }

        World world = event.world;
        List<TileCvDefender> defenders = findDefenders(world);

        if (defenders.isEmpty()) {
            return;
        }

        List<Entity> entities =
                new ArrayList<Entity>(world.loadedEntityList);

        for (Entity entity : entities) {
            if (entity == null || entity.isDead) {
                projectileTickStartPositions.remove(entity);

                if (entity instanceof EntityLivingBase) {
                    mobTickStartPositions.remove(
                            (EntityLivingBase) entity
                    );
                }

                continue;
            }

            if (isBarrierEffectEntity(entity)) {
                handleBarrierEffect(entity, defenders);
                continue;
            }

            if (isBarrierMob(entity)) {
                handleMob(
                        (EntityLivingBase) entity,
                        defenders
                );
            }
        }
    }

    private void captureTickStartPositions(World world) {
        for (Entity entity
                : new ArrayList<Entity>(world.loadedEntityList)) {
            if (entity == null || entity.isDead) {
                continue;
            }

            if (isBarrierEffectEntity(entity)) {
                projectileTickStartPositions.put(
                        entity,
                        getEntityCenter(entity)
                );
            }

            if (isBarrierMob(entity)) {
                mobTickStartPositions.put(
                        (EntityLivingBase) entity,
                        getEntityFeet(entity)
                );
            }
        }
    }

    private void handleBarrierEffect(
            Entity effect,
            List<TileCvDefender> defenders) {
        Vec3d previous = getProjectileTickStartPosition(effect);
        Vec3d current = getEntityCenter(effect);

        for (TileCvDefender defender : defenders) {
            boolean crossesWall = defender.crossesDome(
                    previous.x, previous.y, previous.z,
                    current.x, current.y, current.z
            );
            boolean areaTouchesWall = isAreaMagicEffect(effect)
                    && effectAreaIntersectsBarrier(effect, defender, current);

            if (!crossesWall && !areaTouchesWall) {
                continue;
            }

            boolean absorbed = isMagicEffectEntity(effect)
                    ? defender.consumeForMagic()
                    : defender.consumeForProjectile();

            if (absorbed) {
                effect.setDead();
                projectileTickStartPositions.remove(effect);
            }
            return;
        }
    }

    private void handleMob(
            EntityLivingBase mob,
            List<TileCvDefender> defenders) {
        Vec3d previousFeet = mobTickStartPositions.get(mob);

        if (previousFeet == null) {
            previousFeet = new Vec3d(
                    mob.prevPosX,
                    mob.prevPosY,
                    mob.prevPosZ
            );
        }

        Vec3d currentFeet = getEntityFeet(mob);

        for (TileCvDefender defender : defenders) {
            if (!shouldBlockMobMovement(
                    mob,
                    defender,
                    previousFeet,
                    currentFeet
            )) {
                continue;
            }

            /*
             * Return to the exact position occupied at tick start.
             * Do not radially scale Y: doing that can teleport a mob down into
             * the terrain and make it fall through blocks.
             */
            restoreMobToTickStart(mob, previousFeet);
            mobTickStartPositions.put(mob, previousFeet);
            return;
        }

        mobTickStartPositions.put(mob, currentFeet);
    }

    private boolean shouldBlockMobMovement(
            EntityLivingBase mob,
            TileCvDefender defender,
            Vec3d previousFeet,
            Vec3d currentFeet) {
        Vec3d previousCenter =
                toMobCenter(mob, previousFeet);
        Vec3d currentCenter =
                toMobCenter(mob, currentFeet);

        boolean wasInside = defender.isInsideDome(
                previousCenter.x,
                previousCenter.y,
                previousCenter.z
        );

        boolean isInside = defender.isInsideDome(
                currentCenter.x,
                currentCenter.y,
                currentCenter.z
        );

        /*
         * The center changed sides, so the wall was crossed.
         */
        if (wasInside != isInside) {
            return true;
        }

        /*
         * Also catch high-speed movement that starts and ends on the same
         * side but passes through the sphere during one tick.
         */
        if (defender.crossesDome(
                previousCenter.x,
                previousCenter.y,
                previousCenter.z,
                currentCenter.x,
                currentCenter.y,
                currentCenter.z
        )) {
            return true;
        }

        double previousDistance = distanceFromDomeCenter(
                defender,
                previousCenter
        );
        double currentDistance = distanceFromDomeCenter(
                defender,
                currentCenter
        );

        /*
         * Block before the mob's center reaches the wall so the mob's body
         * cannot visually pass halfway through it.
         */
        double bodyMargin = Math.max(
                0.25D,
                Math.min(1.50D, mob.width * 0.5D + 0.12D)
        );

        double shieldRadius =
                defender.getCurrentShieldRadius();

        if (shieldRadius <= 0.0D) {
            return false;
        }

        if (wasInside) {
            double maximumInsideDistance =
                    Math.max(
                            0.10D,
                            shieldRadius - bodyMargin
                    );

            return currentDistance >= maximumInsideDistance
                    && currentDistance
                    > previousDistance + 1.0E-5D;
        }

        double minimumOutsideDistance =
                shieldRadius + bodyMargin;

        return currentDistance <= minimumOutsideDistance
                && currentDistance
                < previousDistance - 1.0E-5D;
    }

    private void restoreMobToTickStart(
            EntityLivingBase mob,
            Vec3d safeFeetPosition) {
        mob.setPositionAndUpdate(
                safeFeetPosition.x,
                safeFeetPosition.y,
                safeFeetPosition.z
        );

        /*
         * Stop only the attempted movement. The saved Y coordinate is kept
         * unchanged, so the mob is never projected below the floor.
         */
        mob.motionX = 0.0D;
        mob.motionY = 0.0D;
        mob.motionZ = 0.0D;
        mob.fallDistance = 0.0F;
        mob.velocityChanged = true;

        if (mob instanceof EntityLiving) {
            ((EntityLiving) mob).getNavigator().clearPath();
        }
    }

    private double distanceFromDomeCenter(
            TileCvDefender defender,
            Vec3d point) {
        double dx = point.x
                - (defender.getPos().getX() + 0.5D);
        double dy = point.y
                - (defender.getPos().getY() + 0.5D);
        double dz = point.z
                - (defender.getPos().getZ() + 0.5D);

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private Vec3d toMobCenter(
            EntityLivingBase mob,
            Vec3d feetPosition) {
        return new Vec3d(
                feetPosition.x,
                feetPosition.y + mob.height * 0.5D,
                feetPosition.z
        );
    }

    private boolean isBarrierMob(Entity entity) {
        /*
         * Explicitly exclude players. Using EntityLivingBase also catches
         * modded mobs that do not directly extend EntityLiving.
         */
        return entity instanceof EntityLivingBase
                && !(entity instanceof EntityPlayer);
    }

    private Vec3d getProjectileTickStartPosition(
            Entity projectile) {
        Vec3d tracked =
                projectileTickStartPositions.get(projectile);

        if (tracked != null) {
            return tracked;
        }

        return new Vec3d(
                projectile.prevPosX,
                projectile.prevPosY
                        + projectile.height * 0.5D,
                projectile.prevPosZ
        );
    }

    private Vec3d getImpactPosition(
            ProjectileImpactEvent event,
            Entity projectile) {
        RayTraceResult result = event.getRayTraceResult();

        if (result != null && result.hitVec != null) {
            return result.hitVec;
        }

        return getEntityCenter(projectile);
    }

    private boolean isBarrierEffectEntity(Entity entity) {
        return isProjectileEntity(entity)
                || isMagicEffectEntity(entity)
                || entity instanceof EntityAreaEffectCloud
                || entity instanceof EntityTNTPrimed
                || entity instanceof EntityFireworkRocket;
    }

    private boolean isMagicEffectEntity(Entity entity) {
        if (entity == null) {
            return false;
        }

        String className = entity.getClass().getName().toLowerCase(Locale.ROOT);
        String simpleName = entity.getClass().getSimpleName().toLowerCase(Locale.ROOT);

        return (className.startsWith("thaumcraft.")
                && (className.contains(".projectile.")
                    || simpleName.contains("focus")
                    || simpleName.contains("spell")
                    || simpleName.contains("cloud")
                    || simpleName.contains("orb")
                    || simpleName.contains("bolt")
                    || simpleName.contains("beam")
                    || simpleName.contains("ray")
                    || simpleName.contains("blast")
                    || simpleName.contains("shock")
                    || simpleName.contains("alumentum")
                    || simpleName.contains("collapser")
                    || simpleName.contains("bottletaint")))
                || simpleName.contains("magicprojectile")
                || simpleName.contains("spellprojectile")
                || simpleName.endsWith("magicbolt")
                || simpleName.endsWith("spellbolt")
                || simpleName.endsWith("energybeam");
    }

    private boolean isAreaMagicEffect(Entity entity) {
        if (entity instanceof EntityAreaEffectCloud) {
            return true;
        }
        String name = entity.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        return name.contains("cloud")
                || name.contains("field")
                || name.contains("vortex")
                || name.contains("aura");
    }

    private boolean effectAreaIntersectsBarrier(Entity effect,
                                                TileCvDefender defender,
                                                Vec3d center) {
        double effectRadius = getEffectRadius(effect);
        double shieldRadius = defender.getCurrentShieldRadius();
        if (effectRadius <= 0.0D || shieldRadius <= 0.0D) {
            return false;
        }

        double dx = center.x - (defender.getPos().getX() + 0.5D);
        double dy = center.y - (defender.getPos().getY() + 0.5D);
        double dz = center.z - (defender.getPos().getZ() + 0.5D);
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return Math.abs(distance - shieldRadius) <= effectRadius + 0.20D;
    }

    private double getEffectRadius(Entity effect) {
        if (effect instanceof EntityAreaEffectCloud) {
            return ((EntityAreaEffectCloud) effect).getRadius();
        }
        try {
            Method method = effect.getClass().getMethod("getRadius");
            Object value = method.invoke(effect);
            if (value instanceof Number) {
                return Math.max(0.0D, ((Number) value).doubleValue());
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return Math.max(0.50D, Math.max(effect.width, effect.height) * 0.5D);
    }

    private boolean isProjectileEntity(Entity entity) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof IProjectile
                || entity instanceof EntityArrow
                || entity instanceof EntityThrowable
                || entity instanceof EntityFireball
                || entity instanceof EntityShulkerBullet
                || entity instanceof EntityLlamaSpit) {
            return true;
        }

        String className = entity.getClass()
                .getName()
                .toLowerCase(Locale.ROOT);

        String simpleName = entity.getClass()
                .getSimpleName()
                .toLowerCase(Locale.ROOT);

        return className.contains(".projectile.")
                || simpleName.contains("projectile")
                || simpleName.endsWith("bullet")
                || simpleName.endsWith("missile")
                || simpleName.endsWith("rocket")
                || simpleName.endsWith("grenade")
                || simpleName.endsWith("orb")
                || simpleName.endsWith("bolt")
                || simpleName.endsWith("dart")
                || simpleName.endsWith("beam");
    }

    private Vec3d getDamageOrigin(DamageSource source) {
        Entity actual = source.getTrueSource();
        if (actual != null) {
            return getAttackSourcePosition(actual);
        }
        Entity immediate = source.getImmediateSource();
        if (immediate != null) {
            return getEntityCenter(immediate);
        }
        return source.getDamageLocation();
    }

    private boolean isMagicDamage(DamageSource source) {
        if (source.isMagicDamage()) {
            return true;
        }
        String type = source.damageType == null
                ? ""
                : source.damageType.toLowerCase(Locale.ROOT);
        return type.contains("magic")
                || type.contains("thaum")
                || type.contains("flux")
                || type.contains("vis")
                || type.contains("warp")
                || type.contains("eldritch")
                || type.contains("focus")
                || type.contains("spell")
                || type.contains("arcane")
                || type.contains("impetus")
                || isMagicEffectEntity(source.getImmediateSource())
                || isMagicEffectEntity(source.getTrueSource());
    }

    private void removeImmediateEffectEntity(DamageSource source) {
        Entity immediate = source.getImmediateSource();
        if (immediate == null || immediate == source.getTrueSource()) {
            return;
        }
        if (isBarrierEffectEntity(immediate)) {
            immediate.setDead();
            projectileTickStartPositions.remove(immediate);
        }
    }

    private Vec3d blockCenter(BlockPos pos) {
        return new Vec3d(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D
        );
    }

    private Vec3d getAttackSourcePosition(Entity source) {
        if (source instanceof EntityLivingBase) {
            EntityLivingBase living =
                    (EntityLivingBase) source;

            return new Vec3d(
                    living.posX,
                    living.posY + living.getEyeHeight(),
                    living.posZ
            );
        }

        return getEntityCenter(source);
    }

    private Vec3d getEntityFeet(Entity entity) {
        return new Vec3d(
                entity.posX,
                entity.posY,
                entity.posZ
        );
    }

    private Vec3d getEntityCenter(Entity entity) {
        return new Vec3d(
                entity.posX,
                entity.posY + entity.height * 0.5D,
                entity.posZ
        );
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        World world = event.getWorld();

        if (world == null || world.isRemote) {
            return;
        }

        List<TileCvDefender> defenders =
                findDefenders(world);

        if (defenders.isEmpty()) {
            return;
        }

        Vec3d explosionPosition =
                event.getExplosion().getPosition();

        for (TileCvDefender defender : defenders) {
            final boolean explosionInside =
                    defender.isInsideDome(
                            explosionPosition.x,
                            explosionPosition.y,
                            explosionPosition.z
                    );

            boolean crossesBoundary =
                    event.getAffectedBlocks()
                            .stream()
                            .anyMatch(
                                    blockPos ->
                                            defender.isInsideDome(
                                                    blockPos
                                            ) != explosionInside
                            )
                    || event.getAffectedEntities()
                            .stream()
                            .anyMatch(
                                    entity ->
                                            defender.isInsideDome(
                                                    entity.posX,
                                                    entity.posY,
                                                    entity.posZ
                                            ) != explosionInside
                            );

            if (!crossesBoundary
                    || !defender.consumeForExplosion()) {
                continue;
            }

            event.getAffectedBlocks().removeIf(
                    blockPos ->
                            defender.isInsideDome(blockPos)
                                    != explosionInside
            );

            event.getAffectedEntities().removeIf(
                    entity ->
                            defender.isInsideDome(
                                    entity.posX,
                                    entity.posY,
                                    entity.posZ
                            ) != explosionInside
            );
        }
    }

    private List<TileCvDefender> findDefenders(
            World world) {
        List<TileCvDefender> result =
                new ArrayList<TileCvDefender>();

        for (Object tile : world.loadedTileEntityList) {
            if (!(tile instanceof TileCvDefender)) {
                continue;
            }

            TileCvDefender defender =
                    (TileCvDefender) tile;

            if (defender.isShieldActive()) {
                result.add(defender);
            }
        }

        return result;
    }
}
