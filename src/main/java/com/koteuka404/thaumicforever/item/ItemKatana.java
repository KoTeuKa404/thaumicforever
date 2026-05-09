package com.koteuka404.thaumicforever.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import com.koteuka404.thaumicforever.network.PacketKatanaSlashFX;
import com.koteuka404.thaumicforever.ThaumicForever;

public class ItemKatana extends ItemSword {

    private static final float ATTACK_DAMAGE = 6.0F;
    private static final float ATTACK_SPEED = -2.0F;
    private static final int DASH_COOLDOWN_TICKS = 22;
    private static final int DASH_DELAY_TICKS = 4;
    private static final float DASH_BONUS_DAMAGE = 7.0F;
    private static final double DASH_DISTANCE_BLOCKS = 8.0D;
    private static final String NBT_DASH_DELAY = "tf_katana_dash_delay";
    private static final String NBT_DASH_LOOK_X = "tf_katana_dash_look_x";
    private static final String NBT_DASH_LOOK_Y = "tf_katana_dash_look_y";
    private static final String NBT_DASH_LOOK_Z = "tf_katana_dash_look_z";
    private final Multimap<String, AttributeModifier> attributeModifiers;

    public ItemKatana() {
        super(ToolMaterial.IRON);
        setRegistryName("katana");
        setUnlocalizedName("katana");
        setMaxStackSize(1);

        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
            SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
            new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ATTACK_DAMAGE, 0)
        );
        builder.put(
            SharedMonsterAttributes.ATTACK_SPEED.getName(),
            new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0)
        );
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return slot == EntityEquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (hand != EnumHand.MAIN_HAND) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        Vec3d look = player.getLookVec();
        if (!world.isRemote) {
            NBTTagCompound data = player.getEntityData();
            data.setInteger(NBT_DASH_DELAY, DASH_DELAY_TICKS);
            data.setDouble(NBT_DASH_LOOK_X, look.x);
            data.setDouble(NBT_DASH_LOOK_Y, look.y);
            data.setDouble(NBT_DASH_LOOK_Z, look.z);
            player.getCooldownTracker().setCooldown(this, DASH_COOLDOWN_TICKS);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, net.minecraft.entity.Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        if (world.isRemote || !(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        if (player.getHeldItemMainhand() != stack) {
            return;
        }

        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(NBT_DASH_DELAY)) {
            return;
        }

        int delay = data.getInteger(NBT_DASH_DELAY);
        if (delay > 0) {
            data.setInteger(NBT_DASH_DELAY, delay - 1);
            return;
        }

        data.removeTag(NBT_DASH_DELAY);
        Vec3d look = new Vec3d(
            data.getDouble(NBT_DASH_LOOK_X),
            data.getDouble(NBT_DASH_LOOK_Y),
            data.getDouble(NBT_DASH_LOOK_Z)
        );
        data.removeTag(NBT_DASH_LOOK_X);
        data.removeTag(NBT_DASH_LOOK_Y);
        data.removeTag(NBT_DASH_LOOK_Z);
        triggerDash(world, player, look);
    }

    private void triggerDash(World world, EntityPlayer player, Vec3d look) {
        Vec3d horizontal = new Vec3d(look.x, 0.0D, look.z);
        if (horizontal.lengthSquared() > 1.0E-5D) {
            horizontal = horizontal.normalize();
        } else {
            horizontal = new Vec3d(0.0D, 0.0D, 1.0D);
        }

        double dashDistance = 0.0D;
        AxisAlignedBB box = player.getEntityBoundingBox();
        for (double d = DASH_DISTANCE_BLOCKS; d >= 0.0D; d -= 0.5D) {
            AxisAlignedBB moved = box.offset(horizontal.x * d, 0.0D, horizontal.z * d);
            if (world.getCollisionBoxes(player, moved).isEmpty()) {
                dashDistance = d;
                break;
            }
        }

        double dx = horizontal.x * dashDistance;
        double dz = horizontal.z * dashDistance;

        if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
            ((net.minecraft.entity.player.EntityPlayerMP) player).connection.setPlayerLocation(player.posX + dx, player.posY, player.posZ + dz, player.rotationYaw, player.rotationPitch);
        } else {
            player.setPosition(player.posX + dx, player.posY, player.posZ + dz);
        }

        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;
        player.velocityChanged = true;
        player.fallDistance = 0.0F;

        SoundEvent dashSound = net.minecraft.init.SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP;
        world.playSound(null, player.posX, player.posY, player.posZ, dashSound, SoundCategory.PLAYERS, 1.0F, 0.9F + world.rand.nextFloat() * 0.2F);

        AxisAlignedBB hitBox = player.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D).expand(horizontal.x * 1.8D, 0.0D, horizontal.z * 1.8D);
        for (EntityLivingBase target : world.getEntitiesWithinAABB(EntityLivingBase.class, hitBox, e -> e != player && e.isEntityAlive())) {
            if (target.attackEntityFrom(DamageSource.causePlayerDamage(player), DASH_BONUS_DAMAGE + ATTACK_DAMAGE)) {
                target.knockBack(player, 0.8F, horizontal.x, horizontal.z);
                target.hurtResistantTime = 0;
            }
        }

        if (world instanceof WorldServer) {
            WorldServer ws = (WorldServer) world;
            double px = player.posX + horizontal.x * 0.7D;
            double py = player.posY + player.getEyeHeight() * 0.65D;
            double pz = player.posZ + horizontal.z * 0.7D;
            ws.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, px, py, pz, 8, 0.18D, 0.14D, 0.18D, 0.0D);
            ws.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 18, 0.25D, 0.18D, 0.25D, 0.02D);
        }

        ThaumicForever.network.sendToAllAround(
            new PacketKatanaSlashFX(
                player.posX,
                player.posY + player.getEyeHeight() * 0.72D,
                player.posZ,
                look.x, look.y, look.z
            ),
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), player.posX, player.posY, player.posZ, 48.0D)
        );
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.GRAY + "Katana";
    }
}
