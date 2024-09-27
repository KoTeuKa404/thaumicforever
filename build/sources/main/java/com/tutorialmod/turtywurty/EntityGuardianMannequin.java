package com.tutorialmod.turtywurty;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityGuardianMannequin extends EntityCreature {

    private final EntityPlayer owner;

    // Унікальні UUID для модифікаторів
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("5d7e7c84-7b10-4c78-bd3f-fc6c7efddc71");
    private static final UUID ARMOR_TOUGHNESS_MODIFIER_UUID = UUID.fromString("9f38e78e-3f09-4f62-8802-34d3ff8ff747");
    private static final UUID WEAPON_MODIFIER_UUID = UUID.fromString("5a789a2c-6b6e-4b55-8cfc-2cfcdf3a8a7b");

    // Constructor with EntityPlayer
    public EntityGuardianMannequin(World worldIn, EntityPlayer owner) {
        super(worldIn);
        this.owner = owner;
        this.setSize(0.6F, 1.6F);
        this.experienceValue = 5;
        this.initEntityAI();
    }

    // Constructor without EntityPlayer
    public EntityGuardianMannequin(World worldIn) {
        super(worldIn);
        this.owner = null;
        this.setSize(0.6F, 1.6F);
        this.experienceValue = 5;
        this.initEntityAI();
    }

    // Initialize AI
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIWanderAvoidWater(this, 0.6D)); // Wander and avoid water
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true)); // Melee attack
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true) {
            @Override
            public boolean shouldExecute() {
                // Check if the attacker is not the owner before retaliating
                if (EntityGuardianMannequin.this.getRevengeTarget() == owner) {
                    return false;
                }
                return super.shouldExecute();
            }
        }); // Retaliate when attacked, but not by the owner
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> entity instanceof IMob)); // Attack hostile mobs (IMob)
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        applyArmorAttributes();
        applyArmorEffects(); // Застосування ефектів від броні
    }

    // Apply armor attributes without removing existing ones
    private void applyArmorAttributes() {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = this.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                // Додаємо атрибути захисту броні, перевіряючи наявність модифікатора
                if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getModifier(ARMOR_MODIFIER_UUID) == null) {
                    this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(
                        new AttributeModifier(ARMOR_MODIFIER_UUID, "Armor modifier", armor.damageReduceAmount, 0));
                }

                // Додаємо атрибут міцності броні, перевіряючи наявність модифікатора
                if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getModifier(ARMOR_TOUGHNESS_MODIFIER_UUID) == null) {
                    this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).applyModifier(
                        new AttributeModifier(ARMOR_TOUGHNESS_MODIFIER_UUID, "Armor toughness", armor.toughness, 0));
                }
            }
        }
    }

    // Apply unique armor effects, including modded items
    private void applyArmorEffects() {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = this.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.hasTagCompound()) {
                NBTTagCompound tagCompound = stack.getTagCompound();

                // Приклад додавання спеціальних ефектів для броні
                if (tagCompound != null && tagCompound.hasKey("CustomEffect")) {
                    this.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 1));
                }

                // Чарування, такі як Thorns, можуть також бути застосовані
                if (stack.isItemEnchanted()) {
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack) > 0) {
                        this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 1));
                    }
                }
            }
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        
        // Здоров'я (20 HP)
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        
        // Швидкість (0.25)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        // Base attack damage
        float attackDamage = 2.0F;

        // Adjust damage based on weapon (sword or other modded items)
        ItemStack heldItem = this.getHeldItemMainhand();
        if (!heldItem.isEmpty()) {
            Item item = heldItem.getItem();
            if (item instanceof ItemSword) {
                attackDamage = ((ItemSword) item).getAttackDamage() + 2.0F; // Add base damage to sword damage
            } else {
                attackDamage = 4.0F; // Default damage for non-sword items
            }
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
        return flag;
    }

    // Enable interaction with the player to give the mob a weapon and retrieve the old one
    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Get the item currently in the mob's main hand
        ItemStack currentItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

        if (!this.world.isRemote && !stack.isEmpty()) {
            // Give the player the current item in the main hand (if it exists)
            if (!currentItem.isEmpty()) {
                player.addItemStackToInventory(currentItem);
            }

            // Set the player's held item to the mob's main hand slot
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
            player.setHeldItem(hand, ItemStack.EMPTY); // Clear the player's hand
            
            return EnumActionResult.SUCCESS;
        } else if (!this.world.isRemote && stack.isEmpty() && !currentItem.isEmpty()) {
            // If the player's hand is empty and the mob is holding an item, give it to the player
            player.setHeldItem(hand, currentItem.copy());
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY); // Clear the mob's hand
            
            return EnumActionResult.SUCCESS;
        }
        
        return EnumActionResult.PASS;
    }

    // Immunity to poison and drowning
    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        // Immunity to poison
        if (potioneffectIn.getPotion() == MobEffects.POISON) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public void onLivingUpdate() {
        // Set air supply to maximum to prevent drowning
        this.setAir(300);
        super.onLivingUpdate();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack itemstack = this.getItemStackFromSlot(slot);
            if (!itemstack.isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemstack.writeToNBT(itemTag);

                // Зберігаємо специфічні для модів дані
                if (itemstack.hasTagCompound()) {
                    itemTag.setTag("ModdedData", itemstack.getTagCompound());
                }

                compound.setTag(slot.getName(), itemTag);
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (compound.hasKey(slot.getName())) {
                NBTTagCompound tag = compound.getCompoundTag(slot.getName());
                ItemStack stack = new ItemStack(tag);

                // Відновлюємо специфічні для модів дані
                if (tag.hasKey("ModdedData")) {
                    stack.setTagCompound(tag.getCompoundTag("ModdedData"));
                }

                this.setItemStackToSlot(slot, stack);
            }
        }
    }
}
