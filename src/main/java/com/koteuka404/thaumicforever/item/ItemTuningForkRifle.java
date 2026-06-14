package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.potion.PotionResonanceDisruption;
import com.koteuka404.thaumicforever.potion.ResonanceDisruptionHandler;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemTuningForkRifle extends Item {

    private static final int COOLDOWN_TICKS = 28;
    private static final double RANGE = 48.0D;
    private static final float IMPACT_DAMAGE = 1.0F;

    public ItemTuningForkRifle() {
        setUnlocalizedName("tuning_fork_rifle");
        setRegistryName("tuning_fork_rifle");
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
        setMaxStackSize(1);
        setMaxDamage(768);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (!world.isRemote) {
            fireLaser(world, player);

            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.PLAYERS, 0.85F,
                    1.55F + world.rand.nextFloat() * 0.15F);

            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(1, player);
            }
        }

        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static void fireLaser(World world, EntityPlayer player) {
        Vec3d start = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d maxEnd = start.addVector(look.x * RANGE, look.y * RANGE, look.z * RANGE);

        RayTraceResult blockHit = world.rayTraceBlocks(start, maxEnd, false, true, false);
        Vec3d end = blockHit == null ? maxEnd : blockHit.hitVec;
        double maxDistanceSq = start.squareDistanceTo(end);

        EntityLivingBase target = findLaserTarget(world, player, start, maxEnd, maxDistanceSq);
        if (target != null) {
            end = new Vec3d(target.posX, target.posY + target.height * 0.55D, target.posZ);
            target.addPotionEffect(new PotionEffect(PotionResonanceDisruption.INSTANCE, ResonanceDisruptionHandler.MARK_DURATION_TICKS, 0, false, true));
            ResonanceDisruptionHandler.applyInitialResonanceHit(target);
            target.attackEntityFrom(new EntityDamageSource("thaumicforever.resonance", player).setMagicDamage(), IMPACT_DAMAGE);
        }

        spawnLaserParticles(world, start, end);
        world.playSound(null, end.x, end.y, end.z, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.PLAYERS, 0.75F, 0.65F);
    }

    private static EntityLivingBase findLaserTarget(World world, EntityPlayer player, Vec3d start, Vec3d end, double maxDistanceSq) {
        EntityLivingBase closest = null;
        double closestDistanceSq = maxDistanceSq;
        AxisAlignedBB searchBox = player.getEntityBoundingBox()
                .expand(end.x - start.x, end.y - start.y, end.z - start.z)
                .grow(1.0D);
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player, searchBox);

        for (Entity entity : entities) {
            if (!(entity instanceof EntityLivingBase) || entity == player || !entity.canBeCollidedWith()) {
                continue;
            }
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) {
                continue;
            }

            AxisAlignedBB box = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize() + 0.35D);
            RayTraceResult hit = box.calculateIntercept(start, end);
            if (hit == null && !box.contains(start)) {
                continue;
            }

            double distanceSq = hit == null ? 0.0D : start.squareDistanceTo(hit.hitVec);
            if (distanceSq < closestDistanceSq) {
                closestDistanceSq = distanceSq;
                closest = (EntityLivingBase) entity;
            }
        }

        return closest;
    }

    private static void spawnLaserParticles(World world, Vec3d start, Vec3d end) {
        if (!(world instanceof WorldServer)) {
            return;
        }

        WorldServer server = (WorldServer) world;
        Vec3d delta = end.subtract(start);
        double length = delta.lengthVector();
        int steps = Math.max(2, Math.min(64, (int) (length * 2.5D)));

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double x = start.x + delta.x * t;
            double y = start.y + delta.y * t;
            double z = start.z + delta.z * t;
            server.spawnParticle(EnumParticleTypes.SPELL_WITCH, x, y, z, 1, 0.01D, 0.01D, 0.01D, 0.0D);
            if (i % 4 == 0) {
                server.spawnParticle(EnumParticleTypes.CRIT_MAGIC, x, y, z, 1, 0.005D, 0.005D, 0.005D, 0.0D);
            }
        }
    }
}
