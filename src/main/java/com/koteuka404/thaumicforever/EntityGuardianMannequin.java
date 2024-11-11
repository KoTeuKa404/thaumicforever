package com.koteuka404.thaumicforever;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class EntityGuardianMannequin extends EntityCreature {
    private UUID ownerUUID;
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("5d7e7c84-7b10-4c78-bd3f-fc6c7efddc71");
    private static final UUID ARMOR_TOUGHNESS_MODIFIER_UUID = UUID.fromString("9f38e78e-3f09-4f62-8802-34d3ff8ff747");

    public EntityGuardianMannequin(World worldIn, EntityPlayer owner) {
        super(worldIn);
        this.ownerUUID = owner.getUniqueID();
        this.setSize(0.6F, 1.6F);
        this.experienceValue = 5;
        this.initEntityAI();
    }

    public EntityGuardianMannequin(World worldIn) {
        super(worldIn);
        this.ownerUUID = null;
        this.setSize(0.6F, 1.6F);
        this.experienceValue = 5;
        this.initEntityAI();
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 0.6D));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true) {
            @Override
            public boolean shouldExecute() {
                if (EntityGuardianMannequin.this.getRevengeTarget() instanceof EntityPlayer) {
                    EntityPlayer attacker = (EntityPlayer) EntityGuardianMannequin.this.getRevengeTarget();
                    if (EntityGuardianMannequin.this.isOwner(attacker)) {
                        return false;
                    }
                }
                return super.shouldExecute();
            }
        });
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> {
            if (entity instanceof EntityPlayer && EntityGuardianMannequin.this.isOwner((EntityPlayer) entity)) {
                return false;
            }
            return entity instanceof IMob;
        }));
    }
    
    private boolean isOwner(EntityPlayer player) {
        return this.ownerUUID != null && this.ownerUUID.equals(player.getUniqueID());
    }

    public EntityPlayer getOwner() {
        if (this.ownerUUID == null) {
            return null;
        }
        return this.world.getPlayerEntityByUUID(this.ownerUUID);
    }

    @Override
    public void onLivingUpdate() {
        this.setAir(9999999);

        EntityPlayer owner = this.getOwner();
        if (owner != null && !this.world.isRemote) {
            if (this.getDistance(owner) > 10.0D) {
                this.getNavigator().tryMoveToEntityLiving(owner, 1.0D);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        applyArmorAttributes();
        applyArmorEffects();
    }

    private boolean isSRParasitesInstalled() {
        return Loader.isModLoaded("srparasites");
    }

    private void applyArmorAttributes() {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = this.getItemStackFromSlot(slot);
            if (!stack.isEmpty()) {
                double armorValue = getArmorValue(stack);
                double toughnessValue = getArmorToughness(stack);

                if (!isSRParasitesInstalled()) {
                    armorValue /= 2;
                    toughnessValue /= 2;
                }

                if (armorValue > 0) {
                    if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getModifier(ARMOR_MODIFIER_UUID) == null) {
                        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(
                            new AttributeModifier(ARMOR_MODIFIER_UUID, "Armor modifier", armorValue, 0));
                    }
                }

                if (toughnessValue > 0) {
                    if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getModifier(ARMOR_TOUGHNESS_MODIFIER_UUID) == null) {
                        this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).applyModifier(
                            new AttributeModifier(ARMOR_TOUGHNESS_MODIFIER_UUID, "Armor toughness", toughnessValue, 0));
                    }
                }
            }
        }
    }

    private double getArmorValue(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            return ((ItemArmor) stack.getItem()).damageReduceAmount;
        }
        return 0;
    }

    private double getArmorToughness(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            return ((ItemArmor) stack.getItem()).toughness;
        }
        return 0;
    }

    private void applyArmorEffects() {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = this.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.isItemEnchanted()) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack) > 0) {
                    this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 1));
                }
            }
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage()) {
            amount *= 2.0F; 
        }

        if (source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) source.getTrueSource();
            if (this.isOwner(attacker)) {
                return super.attackEntityFrom(source, amount); 
            }
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
        if (livingBase instanceof EntityPlayer && this.isOwner((EntityPlayer) livingBase)) {
            return;
        }
        super.setRevengeTarget(livingBase);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (entityIn instanceof EntityPlayer && this.isOwner((EntityPlayer) entityIn)) {
            return false;
        }

        float attackDamage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        ItemStack heldItem = this.getHeldItemMainhand();

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemSword) {
            attackDamage += ((ItemSword) heldItem.getItem()).getAttackDamage();
        } else if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemTool) {
            attackDamage += 4.0F;
        }

        if (!isSRParasitesInstalled()) {
            attackDamage /= 2;
        }

        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        ItemStack currentItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        boolean isArmor = heldItem.getItem() instanceof ItemArmor;
        EntityEquipmentSlot slot = isArmor ? ((ItemArmor) heldItem.getItem()).armorType : EntityEquipmentSlot.MAINHAND;

        if (!this.world.isRemote) {
            if (!heldItem.isEmpty()) {
                if (!currentItem.isEmpty()) {
                    player.addItemStackToInventory(currentItem);
                }
                this.setItemStackToSlot(slot, heldItem.copy());
                player.setHeldItem(hand, ItemStack.EMPTY);
                return EnumActionResult.SUCCESS;
            } else if (heldItem.isEmpty() && !currentItem.isEmpty()) {
                player.setHeldItem(hand, currentItem.copy());
                this.setItemStackToSlot(slot, ItemStack.EMPTY);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if (potioneffectIn.getPotion() == MobEffects.POISON || potioneffectIn.getPotion() == MobEffects.WITHER) {
            return false;
        }
        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.ownerUUID != null) {
            compound.setString("OwnerUUID", this.ownerUUID.toString());
        }
        ItemStack mainHandItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        if (!mainHandItem.isEmpty()) {
            compound.setTag("MainHandItem", mainHandItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("OwnerUUID")) {
            this.ownerUUID = UUID.fromString(compound.getString("OwnerUUID"));
        }
        if (compound.hasKey("MainHandItem")) {
            ItemStack mainHandItem = new ItemStack(compound.getCompoundTag("MainHandItem"));
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, mainHandItem);
        }
    }
}
