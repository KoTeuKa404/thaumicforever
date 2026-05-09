package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.registry.ModGuiHandler;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.lib.potions.PotionWarpWard;
import com.koteuka404.thaumicforever.client.fx.FXDispatcher;
import com.koteuka404.thaumicforever.ThaumicForever;

public class ItemPotionGun extends Item implements IRechargable {

    private static final int MAX_VIS = 250;
    private static final double SPRAY_RANGE = 9.0D;
    private static final double SPRAY_HALF_ANGLE_DEG = 45.0D; // full cone = 90 deg
    private static final int FLUID_PER_POTION = 1000;
    private static final int FLUID_PER_SECOND = 100;
    private static final int FLUID_CAPACITY = 10000;
    private static final String NBT_FLUID = "PotionGunFluid";
    private static final String NBT_TEMPLATE = "PotionGunTemplate";
    private static final float SPECIAL_AURA_PER_SEC = 8.0F;

    private enum AmmoKind {
        POTION,
        TAINT,
        PURIFYING,
        VIS,
        CLEAN_MIND,
        DEATH,
        UNKNOWN
    }

    public ItemPotionGun() {
        setUnlocalizedName("potion_gun");
        setRegistryName("potion_gun");
        setMaxStackSize(1);
    }

    @Override
    public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
        return MAX_VIS;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
        return EnumChargeDisplay.NORMAL;
    }

    @Override
    public void getSubItems(net.minecraft.creativetab.CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) return;

        ItemStack empty = new ItemStack(this);
        items.add(empty);

        ItemStack charged = new ItemStack(this);
        RechargeHelper.rechargeItemBlindly(charged, null, MAX_VIS);
        items.add(charged);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (!world.isRemote) {
                int handId = hand == EnumHand.MAIN_HAND ? 0 : 1;
                player.openGui(ThaumicForever.instance, ModGuiHandler.GUI_POTION_GUN, world, handId, 0, 0);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        if (getFluidAmount(stack) <= 0 || getPotionTemplate(stack).isEmpty()) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (RechargeHelper.getCharge(stack) <= 0) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof EntityPlayer)) return;
        if (!isSelected) return;
        if (((EntityPlayer) entity).ticksExisted % 20 != 0) return;
        if (RechargeHelper.getCharge(stack) >= getMaxCharge(stack, (EntityPlayer) entity)) return;
        RechargeHelper.rechargeItem(world, stack, entity.getPosition(), (EntityPlayer) entity, 1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) return true;
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
        if (!(living instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) living;

        if (player.world.isRemote) {
            if (player.ticksExisted % 2 == 0) {
                spawnSprayParticlesClient(player, stack);
            }
            return;
        }

        // Resource drain stays 1 vis per second while holding.
        if (player.ticksExisted % 20 == 0) {
            if (!RechargeHelper.consumeCharge(stack, player, 1)) {
                player.stopActiveHand();
                return;
            }
            if (!consumeFluid(stack, FLUID_PER_SECOND)) {
                player.stopActiveHand();
                return;
            }
        }

        ItemStack potionTemplate = getPotionTemplate(stack);
        if (potionTemplate.isEmpty()) return;
        AmmoKind kind = getAmmoKind(potionTemplate);
        List<EntityLivingBase> targets = findTargetsInCone(player, SPRAY_RANGE, SPRAY_HALF_ANGLE_DEG);

        if (kind != AmmoKind.POTION) {
            applySpecialAmmoServer(player, potionTemplate, kind, targets);
            return;
        }

        List<PotionEffect> effects = PotionUtils.getEffectsFromStack(potionTemplate);
        if (effects.isEmpty()) return;
        if (targets.isEmpty()) return;

        boolean instantPulse = player.ticksExisted % 20 == 0;
        boolean normalPulse = player.ticksExisted % 4 == 0;

        for (EntityLivingBase target : targets) {
            for (PotionEffect effect : effects) {
                if (effect == null || effect.getPotion() == null) continue;
                if (effect.getPotion().isInstant()) {
                    if (instantPulse) {
                        effect.getPotion().affectEntity(player, player, target, effect.getAmplifier(), 1.0D);
                    }
                } else {
                    if (normalPulse) {
                        int dur = Math.max(10, Math.min(40, effect.getDuration()));
                        target.addPotionEffect(new PotionEffect(effect.getPotion(), dur, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int fluid = getFluidAmount(stack);
        int cap = getFluidCapacity(stack);
        tooltip.add(TextFormatting.AQUA + I18n.translateToLocalFormatted("tooltip.potion_gun.tank", fluid, cap));

        ItemStack template = getPotionTemplate(stack);
        if (template.isEmpty()) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("tooltip.potion_gun.empty"));
        } else {
            AmmoKind kind = getAmmoKind(template);
            if (kind == AmmoKind.POTION) {
                PotionType type = PotionUtils.getPotionFromItem(template);
                String key = type == null ? "effect.none" : type.getNamePrefixed("potion.effect.");
                tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.translateToLocalFormatted("tooltip.potion_gun.potion", I18n.translateToLocal(key)));
            } else {
                tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.translateToLocalFormatted("tooltip.potion_gun.potion", template.getDisplayName()));
            }
        }

        tooltip.add(TextFormatting.DARK_AQUA + I18n.translateToLocal("tooltip.potion_gun.mode"));
    }

    public static boolean isValidAmmo(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (stack.getItem() == Items.POTIONITEM
                || stack.getItem() == Items.SPLASH_POTION
                || stack.getItem() == Items.LINGERING_POTION) return true;

        ResourceLocation rl = stack.getItem().getRegistryName();
        if (rl == null) return false;
        String id = rl.toString();
        if ("thaumcraft:bottle_taint".equals(id)
                || "thaumicforever:purifying_bottle".equals(id)
                || "thaumicforever:vis_bottle".equals(id)
                || "thaumicforever:clean_mind".equals(id)) return true;

        return isLiquidDeathContainer(stack);
    }

    public static ItemStack processInputSlot(ItemStack gun, ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;
        if (!isValidAmmo(input)) return input;
        if (!canAcceptPotion(gun, input)) return input;
        if (!addPotion(gun, input)) return input;
        return getEmptyContainer(input);
    }

    public static int getFluidAmount(ItemStack gun) {
        if (gun.isEmpty()) return 0;
        NBTTagCompound tag = gun.getTagCompound();
        return tag == null ? 0 : tag.getInteger(NBT_FLUID);
    }

    public static int getFluidCapacity(ItemStack gun) {
        return FLUID_CAPACITY;
    }

    public static ItemStack getPotionTemplate(ItemStack gun) {
        NBTTagCompound tag = gun.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_TEMPLATE, 10)) return ItemStack.EMPTY;
        return new ItemStack(tag.getCompoundTag(NBT_TEMPLATE));
    }

    private static boolean addPotion(ItemStack gun, ItemStack potionInput) {
        if (gun.isEmpty() || potionInput.isEmpty()) return false;
        if (getFluidAmount(gun) + FLUID_PER_POTION > getFluidCapacity(gun)) return false;

        ItemStack one = potionInput.copy();
        one.setCount(1);
        if (!canAcceptPotion(gun, one)) return false;

        NBTTagCompound tag = getOrCreateTag(gun);
        tag.setInteger(NBT_FLUID, getFluidAmount(gun) + FLUID_PER_POTION);
        if (getPotionTemplate(gun).isEmpty()) {
            NBTTagCompound templateTag = new NBTTagCompound();
            one.writeToNBT(templateTag);
            tag.setTag(NBT_TEMPLATE, templateTag);
        }
        gun.setTagCompound(tag);
        return true;
    }

    private static boolean consumeFluid(ItemStack gun, int amount) {
        if (gun.isEmpty()) return false;
        int fluid = getFluidAmount(gun);
        if (fluid < amount) return false;

        NBTTagCompound tag = getOrCreateTag(gun);
        int newAmount = fluid - amount;
        tag.setInteger(NBT_FLUID, newAmount);
        if (newAmount <= 0) {
            tag.removeTag(NBT_TEMPLATE);
        }
        gun.setTagCompound(tag);
        return true;
    }

    private static boolean canAcceptPotion(ItemStack gun, ItemStack potionInput) {
        if (!isValidAmmo(potionInput)) return false;
        if (getFluidAmount(gun) + FLUID_PER_POTION > getFluidCapacity(gun)) return false;

        ItemStack existing = getPotionTemplate(gun);
        if (existing.isEmpty()) return true;

        return getPotionSignature(existing).equals(getPotionSignature(potionInput));
    }

    private static String getPotionSignature(ItemStack stack) {
        ResourceLocation rl = stack.getItem().getRegistryName();
        String id = rl == null ? "unknown" : rl.toString();
        int meta = stack.getMetadata();
        NBTTagCompound tag = stack.getTagCompound();
        String nbt = tag == null ? "" : tag.toString();
        return id + "@" + meta + "|" + nbt;
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        return tag;
    }

    private static List<EntityLivingBase> findTargetsInCone(EntityPlayer player, double range, double halfAngleDeg) {
        Vec3d eye = player.getPositionEyes(1.0F);
        Vec3d look = player.getLookVec().normalize();
        AxisAlignedBB box = player.getEntityBoundingBox().grow(range);
        double tanHalf = Math.tan(Math.toRadians(halfAngleDeg));
        List<EntityLivingBase> targets = new ArrayList<>();
        List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(player, box);
        for (Entity e : list) {
            if (!(e instanceof EntityLivingBase) || !e.canBeCollidedWith()) continue;
            EntityLivingBase living = (EntityLivingBase) e;
            Vec3d to = new Vec3d(
                    living.posX - eye.x,
                    (living.posY + living.getEyeHeight() * 0.5D) - eye.y,
                    living.posZ - eye.z
            );
            double proj = to.dotProduct(look);
            if (proj <= 0.0D || proj > range) continue;

            double radialSq = to.lengthSquared() - proj * proj;
            double baseRadius = proj * tanHalf;
            double hitRadius = baseRadius + Math.max(0.35D, living.width * 0.5D);
            if (radialSq > hitRadius * hitRadius) continue;

            targets.add(living);
        }
        return targets;
    }

    @SideOnly(Side.CLIENT)
    private static void spawnSprayParticlesClient(EntityPlayer player, ItemStack gun) {
        Vec3d eye = player.getPositionEyes(1.0F);
        Vec3d look = player.getLookVec().normalize();
        AmmoKind kind = getAmmoKind(getPotionTemplate(gun));
        int color = getPotionFogParticleColor(gun);
        double tanHalf = Math.tan(Math.toRadians(SPRAY_HALF_ANGLE_DEG));
        boolean deathFog = kind == AmmoKind.DEATH;

        // Build an orthonormal basis for cone offsets.
        Vec3d worldUp = new Vec3d(0.0D, 1.0D, 0.0D);
        Vec3d right = look.crossProduct(worldUp);
        if (right.lengthSquared() < 1.0E-6D) {
            right = look.crossProduct(new Vec3d(1.0D, 0.0D, 0.0D));
        }
        right = right.normalize();
        Vec3d up = right.crossProduct(look).normalize();

        // Cone spray cloud (visually widening cone).
        int coneParticles = deathFog ? 52 : 18;
        for (int i = 0; i < coneParticles; i++) {
            double d = 0.25D + player.world.rand.nextDouble() * SPRAY_RANGE;
            double radius = d * tanHalf * (deathFog
                    ? (0.22D + player.world.rand.nextDouble() * 0.60D)
                    : (0.15D + player.world.rand.nextDouble() * 0.45D));
            double ang = player.world.rand.nextDouble() * Math.PI * 2.0D;
            Vec3d offset = right.scale(Math.cos(ang) * radius).add(up.scale(Math.sin(ang) * radius));
            Vec3d pos = eye.add(look.scale(d)).add(offset);
            FXDispatcher.INSTANCE.drawFocusCloudParticle(
                    pos.x, pos.y, pos.z,
                    look.x * (deathFog ? 0.015D : 0.03D),
                    look.y * (deathFog ? 0.015D : 0.03D),
                    look.z * (deathFog ? 0.015D : 0.03D),
                    color
            );
        }

        // Impact cloud on all targets in cone.
        List<EntityLivingBase> targets = findTargetsInCone(player, SPRAY_RANGE, SPRAY_HALF_ANGLE_DEG);
        for (EntityLivingBase target : targets) {
            int impactParticles = deathFog ? 16 : 6;
            for (int i = 0; i < impactParticles; i++) {
                double ox = (player.world.rand.nextDouble() - 0.5D) * 0.4D;
                double oy = player.world.rand.nextDouble() * 0.5D + 0.2D;
                double oz = (player.world.rand.nextDouble() - 0.5D) * 0.4D;
                FXDispatcher.INSTANCE.drawFocusCloudParticle(
                        target.posX + ox, target.posY + oy, target.posZ + oz,
                        0.0D, deathFog ? 0.003D : 0.01D, 0.0D,
                        color
                );
            }
        }
    }

    private static int getPotionParticleColor(ItemStack gun) {
        ItemStack potionTemplate = getPotionTemplate(gun);
        AmmoKind kind = getAmmoKind(potionTemplate);
        if (kind == AmmoKind.TAINT) return 0xAA22CC;
        if (kind == AmmoKind.PURIFYING) return 0x99FFF0;
        if (kind == AmmoKind.VIS) return 0x88CCFF;
        if (kind == AmmoKind.CLEAN_MIND) return 0xFFFFFF;
        if (kind == AmmoKind.DEATH) return 0x23042F;

        List<PotionEffect> effects = PotionUtils.getEffectsFromStack(potionTemplate);
        if (effects == null || effects.isEmpty()) {
            return 0xAA44FF;
        }

        int r = 0;
        int g = 0;
        int b = 0;
        int c = 0;
        for (PotionEffect effect : effects) {
            if (effect == null || effect.getPotion() == null) continue;
            int col = effect.getPotion().getLiquidColor();
            r += (col >> 16) & 0xFF;
            g += (col >> 8) & 0xFF;
            b += col & 0xFF;
            c++;
        }
        if (c <= 0) return 0xAA44FF;
        r /= c;
        g /= c;
        b /= c;
        return (r << 16) | (g << 8) | b;
    }

    private static int getPotionFogParticleColor(ItemStack gun) {
        if (getAmmoKind(getPotionTemplate(gun)) == AmmoKind.CLEAN_MIND) return 0xFFFFFF;

        int base = getPotionParticleColor(gun);
        int r = (base >> 16) & 0xFF;
        int g = (base >> 8) & 0xFF;
        int b = base & 0xFF;

        // Fog particles are additive-ish visually; darken and clamp so they match liquid color better.
        r = Math.min(130, (int) (r * 0.52f));
        g = Math.min(130, (int) (g * 0.52f));
        b = Math.min(130, (int) (b * 0.52f));
        return (r << 16) | (g << 8) | b;
    }

    private static AmmoKind getAmmoKind(ItemStack ammo) {
        if (ammo.isEmpty()) return AmmoKind.UNKNOWN;
        if (isLiquidDeathContainer(ammo)) return AmmoKind.DEATH;
        ResourceLocation rl = ammo.getItem().getRegistryName();
        if (rl == null) return AmmoKind.UNKNOWN;
        String id = rl.toString();
        if ("minecraft:potion".equals(id) || "minecraft:splash_potion".equals(id) || "minecraft:lingering_potion".equals(id)) {
            return AmmoKind.POTION;
        }
        if ("thaumcraft:bottle_taint".equals(id)) return AmmoKind.TAINT;
        if ("thaumicforever:purifying_bottle".equals(id)) return AmmoKind.PURIFYING;
        if ("thaumicforever:vis_bottle".equals(id)) return AmmoKind.VIS;
        if ("thaumicforever:clean_mind".equals(id)) return AmmoKind.CLEAN_MIND;
        return AmmoKind.UNKNOWN;
    }

    private static void applySpecialAmmoServer(EntityPlayer player, ItemStack ammo, AmmoKind kind, List<EntityLivingBase> targets) {
        World world = player.world;
        if (world.isRemote) return;

        // Keep special spray logic on 1-second pulse to match vis/fluid consumption.
        if (player.ticksExisted % 20 != 0) return;
        BlockPos pos = player.getPosition();

        switch (kind) {
            case TAINT:
                AuraHelper.polluteAura(world, pos, SPECIAL_AURA_PER_SEC, true);
                applyEffectIfExists(targets, "thaumcraft:thaumarhia", 120, 0);
                if (PotionFluxTaint.instance != null) {
                    applyPotionDirect(targets, PotionFluxTaint.instance, 120, 0);
                } else {
                    applyEffectIfExists(targets, "thaumcraft:fluxtaint", 120, 0);
                }
                applyEffectIfExists(targets, "thaumcraft:infectiousvisexhaust", 120, 0);
                applyPotionDirect(targets, PotionVisExhaust.instance, 120, 0);
                break;
            case PURIFYING:
                AuraHelper.drainFlux(world, pos, SPECIAL_AURA_PER_SEC, false);
                applyPotionDirect(targets, PotionWarpWard.instance, 120, 0);
                if (PotionWarpWard.instance != null) {
                    player.addPotionEffect(new PotionEffect(PotionWarpWard.instance, 120, 0, true, true));
                }
                break;
            case VIS:
                AuraHelper.addVis(world, pos, SPECIAL_AURA_PER_SEC);
                break;
            case CLEAN_MIND:
                for (EntityLivingBase target : targets) {
                    if (target instanceof EntityPlayer) {
                        ItemCleanMind.applyToPlayer((EntityPlayer) target);
                    }
                }
                break;
            case DEATH:
                for (EntityLivingBase target : targets) {
                    if (target == null || !target.isEntityAlive()) continue;
                    // Match liquid_death behavior:
                    // damage = (4 - meta + 1) => 5..2 and horizontal motion damping by quanta.
                    double dist = player.getDistance(target);
                    int meta = Math.min(3, Math.max(0, (int) Math.floor((dist / SPRAY_RANGE) * 4.0D)));
                    float quantaPct = (4.0F - meta) / 4.0F; // ~1.0, 0.75, 0.5, 0.25
                    float dmg = 5.0F - meta;                // 5, 4, 3, 2
                    float damp = 1.0F - quantaPct / 2.0F;  // same formula as BlockFluidDeath
                    target.motionX *= damp;
                    target.motionZ *= damp;
                    target.attackEntityFrom(DamageSourceThaumcraft.dissolve, dmg);
                }
                break;
            default:
                break;
        }
    }

    private static boolean isLiquidDeathContainer(ItemStack stack) {
        if (stack.isEmpty()) return false;
        FluidStack fs = FluidUtil.getFluidContained(stack);
        if (fs == null || fs.getFluid() == null) return false;
        String fluidName = fs.getFluid().getName();
        return "liquid_death".equals(fluidName);
    }

    private static ItemStack getEmptyContainer(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;

        // Vanilla potion items always leave a glass bottle.
        if (input.getItem() == Items.POTIONITEM
                || input.getItem() == Items.SPLASH_POTION
                || input.getItem() == Items.LINGERING_POTION) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        ResourceLocation rl = input.getItem().getRegistryName();
        if (rl != null) {
            String id = rl.toString();
            if ("thaumcraft:bottle_taint".equals(id)
                    || "thaumicforever:purifying_bottle".equals(id)
                    || "thaumicforever:vis_bottle".equals(id)
                    || "thaumicforever:clean_mind".equals(id)) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
        }

        if (input.getItem().hasContainerItem(input)) {
            ItemStack container = input.getItem().getContainerItem(input);
            return container.isEmpty() ? ItemStack.EMPTY : container;
        }
        return ItemStack.EMPTY;
    }

    private static void applyEffectIfExists(List<EntityLivingBase> targets, String potionId, int duration, int amp) {
        Potion p = Potion.getPotionFromResourceLocation(potionId);
        if (p == null) return;
        applyPotionDirect(targets, p, duration, amp);
    }

    private static void applyPotionDirect(List<EntityLivingBase> targets, Potion potion, int duration, int amp) {
        if (potion == null || targets == null || targets.isEmpty()) return;
        for (EntityLivingBase t : targets) {
            if (t == null) continue;
            t.addPotionEffect(new PotionEffect(potion, duration, amp, false, true));
        }
    }
}
