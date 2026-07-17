package com.koteuka404.thaumicforever.entity;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.registry.ModGuiHandler;
import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityVoidTraider extends EntityCreature implements IEntityAdditionalSpawnData {
    private static final DataParameter<String> SALE_ITEMS_SYNC =
        EntityDataManager.createKey(EntityVoidTraider.class, DataSerializers.STRING);
    private final ItemStackHandler saleItems = new ItemStackHandler(9);
    private boolean saleItemsInitialized;

    public EntityVoidTraider(World worldIn) {
        super(worldIn);
        this.setSize(3.75F, 3.375F);
        this.experienceValue = 0;
        this.setNoAI(true);
        this.setNoGravity(true);
        this.noClip = true;
        if (!worldIn.isRemote) {
            this.initializeSaleItems();
        }
    }

    public ItemStackHandler getSaleItems() {
        return this.saleItems;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SALE_ITEMS_SYNC, "");
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, this.saleItems.serializeNBT());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        NBTTagCompound saleItemsData = ByteBufUtils.readTag(buffer);
        if (saleItemsData != null) {
            this.saleItems.deserializeNBT(saleItemsData);
            this.saleItemsInitialized = this.hasSaleItems();
        }
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (SALE_ITEMS_SYNC.equals(key) && this.world.isRemote) {
            String serialized = this.dataManager.get(SALE_ITEMS_SYNC);
            if (!serialized.isEmpty()) {
                try {
                    this.saleItems.deserializeNBT(JsonToNBT.getTagFromJson(serialized));
                    this.saleItemsInitialized = this.hasSaleItems();
                } catch (Exception ignored) {
                    // Ignore an invalid sync payload and keep the current client pool.
                }
            }
        }
    }

    public void rerollSaleItems() {
        if (this.world.isRemote) {
            return;
        }

        for (int slot = 0; slot < this.saleItems.getSlots(); slot++) {
            this.saleItems.setStackInSlot(slot, ItemStack.EMPTY);
        }
        VoidTraiderPool.fill(this.saleItems, this.rand);
        this.saleItemsInitialized = true;
        this.dataManager.set(SALE_ITEMS_SYNC, this.saleItems.serializeNBT().toString());
    }

    @Override
    protected void initEntityAI() {
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (hand != EnumHand.MAIN_HAND) {
            return true;
        }

        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() == ModItems.RE_TRADE) {
            if (!this.world.isRemote) {
                rerollSaleItems();
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
            }
            return true;
        }

        if (!this.world.isRemote) {
            this.initializeSaleItems();
            player.openGui(
                ThaumicForever.instance,
                ModGuiHandler.GUI_VOID_TRAIDER,
                this.world,
                this.getEntityId(),
                0,
                0
            );
        }

        return true;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.world.isRemote) {
            // Populate offers before the client starts rendering the display items.
            this.initializeSaleItems();
        }

        this.noClip = true;
        this.setNoGravity(true);
        this.setNoAI(true);
        this.setHealth(this.getMaxHealth());

        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;

        this.getNavigator().clearPath();

        super.onLivingUpdate();

        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("SaleItems", this.saleItems.serializeNBT());
        compound.setBoolean("SaleItemsInitialized", this.saleItemsInitialized);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("SaleItems")) {
            this.saleItems.deserializeNBT(compound.getCompoundTag("SaleItems"));
            this.saleItemsInitialized = compound.hasKey("SaleItemsInitialized")
                ? compound.getBoolean("SaleItemsInitialized")
                : this.hasSaleItems();
        }
    }

    private boolean hasSaleItems() {
        for (int slot = 0; slot < this.saleItems.getSlots(); slot++) {
            if (!this.saleItems.getStackInSlot(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void initializeSaleItems() {
        if (this.saleItemsInitialized) {
            return;
        }

        VoidTraiderPool.fill(this.saleItems, this.rand);
        this.saleItemsInitialized = true;
        this.dataManager.set(SALE_ITEMS_SYNC, this.saleItems.serializeNBT().toString());
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public boolean isAIDisabled() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
    }

    @Override
    public void addVelocity(double x, double y, double z) {
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }
}
