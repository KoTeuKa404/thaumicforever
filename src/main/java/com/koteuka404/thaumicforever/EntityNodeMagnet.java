package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.other.FXSonic;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.world.aura.AuraHandler;

public class EntityNodeMagnet extends EntityOwnedConstruct {

    private static final DataParameter<Float> CHARGE = EntityDataManager.createKey(EntityNodeMagnet.class, DataSerializers.FLOAT);

    EntityAuraNode nodeTarget = null;
    float rs = 0.0f;
    public float rot = 0.0f;

    public EntityNodeMagnet(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 0.9f);
    }

    public EntityNodeMagnet(World worldIn, BlockPos pos) {
        this(worldIn);
        this.setPositionAndRotation((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, 0.0f, 0.0f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CHARGE, 0.0F);
    }

    public float getEyeHeight() {
        return 0.8125f;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }

    @Override
    public void onUpdate() {
        List<Entity> list;
        super.onUpdate();
        if (!this.world.isRemote) {
            this.rotationYaw = this.rotationYawHead;
            if (this.ticksExisted % 50 == 0) {
                this.heal(1.0f);
                if (this.getOwnerEntity() != null && this.getOwnerEntity() instanceof EntityPlayer && !ThaumcraftCapabilities.getKnowledge((EntityPlayer) this.getOwnerEntity()).isResearchComplete("!NODEMAGNETDANGER")) {
                    ResearchManager.completeResearch((EntityPlayer)this.getOwnerEntity(), "!NODEMAGNETDANGER");
                }
            }
            if (this.ticksExisted % 10 == 0 && this.getCharge() < 10) {
                this.rechargeVis();
            }
        }
        if (this.nodeTarget != null && this.nodeTarget.isDead) {
            this.nodeTarget = null;
        }
        if (this.nodeTarget != null && this.getCharge() > 0) {
            if (!this.world.isRemote) {
                this.getLookHelper().setLookPosition(this.nodeTarget.posX, this.nodeTarget.posY + (double)this.nodeTarget.getEyeHeight(), this.nodeTarget.posZ, 10.0f, (float)this.getVerticalFaceSpeed());
            } else if (this.ticksExisted % 10 == 0) {
                this.showFX();
            }
            double gap = this.getDistance(this.nodeTarget);
            if (gap >= 1.0 && gap <= 32.0 && !this.nodeTarget.stablized) {
                double mx = this.posX - this.nodeTarget.posX;
                double my = this.posY - this.nodeTarget.posY;
                double mz = this.posZ - this.nodeTarget.posZ;
                this.nodeTarget.motionX += (mx /= gap * (double)(200 + this.nodeTarget.getNodeSize() * 3));
                this.nodeTarget.motionY += (my /= gap * (double)(200 + this.nodeTarget.getNodeSize() * 3));
                this.nodeTarget.motionZ += (mz /= gap * (double)(200 + this.nodeTarget.getNodeSize() * 3));
                if (!this.world.isRemote) {
                    this.setCharge(this.getCharge() - 1);
                    if (this.rand.nextFloat() < 0.01f) {
                        if (this.rand.nextFloat() < 0.2f) {
                            EntityWisp wisp = new EntityWisp(this.world);
                            wisp.setLocationAndAngles(this.nodeTarget.posX, this.nodeTarget.posY, this.nodeTarget.posZ, 0.0f, 0.0f);
                            wisp.setType(this.nodeTarget.getAspectTag());
                            this.world.spawnEntity(wisp);
                        } else {
                            AuraHelper.polluteAura(this.world, this.nodeTarget.getPosition(), 1, true);
                        }
                    }
                }
                this.rs += 0.36f;
            } else {
                this.nodeTarget = null;
            }
        }
        if (this.nodeTarget == null) {
            this.rs -= 0.5f;
        }
        if (this.nodeTarget == null && this.ticksExisted % 20 == 0) {
            list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityAuraNode.class, 32.0);
            Entity closest = null;
            double d = Double.MAX_VALUE;
            for (Entity e : list) {
                double gap;
                if (((EntityAuraNode)e).stablized || !((gap = this.getDistance(e)) < d)) continue;
                d = gap;
                closest = e;
            }
            if (closest != null) {
                this.nodeTarget = (EntityAuraNode)closest;
            }
        }
        if (this.rs > 36.0f) {
            this.rs = 36.0f;
        }
        if (this.rs < 0.0f) {
            this.rs = 0.0f;
        }
        this.rot += this.rs;
        if (this.rot > 360.0f) {
            this.rot -= 360.0f;
            if (this.world.isRemote) {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundsTC.pump, SoundCategory.BLOCKS, 0.7f, 1.0f, false);
            }
        }
        if (!this.world.isRemote && this.ticksExisted % 40 == 0) {
            EntityPlayer owner = null;
            if (this.getOwnerEntity() instanceof EntityPlayer) {
                owner = (EntityPlayer)this.getOwnerEntity();
            }
    
            List<EntityWisp> wisps = this.world.getEntitiesWithinAABB(EntityWisp.class, this.getEntityBoundingBox().grow(24));
            for (EntityWisp wisp : wisps) {
                if (owner != null) {
                    if (!owner.isCreative() && !owner.isSpectator() && !owner.isDead) {
                        wisp.setAttackTarget(owner);
                        wisp.setRevengeTarget(owner);
                        wisp.getNavigator().tryMoveToEntityLiving(owner, 1.1D);
                        wisp.setLastAttackedEntity(owner);
                        try {
                            java.lang.reflect.Field f = EntityWisp.class.getDeclaredField("target");
                            f.setAccessible(true);
                            f.set(wisp, owner);
                        } catch (Exception ex) {}
                    }
                } else {
                    wisp.setAttackTarget(this);
                    wisp.setRevengeTarget(this);
                    wisp.getNavigator().tryMoveToEntityLiving(this, 1.1D);
                    wisp.setLastAttackedEntity(this);
                    try {
                        java.lang.reflect.Field f = EntityWisp.class.getDeclaredField("target");
                        f.setAccessible(true);
                        f.set(wisp, this);
                    } catch (Exception ex) {}
                }
                // System.out.println("Wisp " + wisp.getEntityId() + " aggros " + (owner != null ? owner.getName() : "magnet"));
            }

        }
    }

    @SideOnly(value=Side.CLIENT)
    private void showFX() {
        FXSonic fb = new FXSonic(Thaumcraft.proxy.getClientWorld(), this.nodeTarget.posX, this.nodeTarget.posY, this.nodeTarget.posZ, this, 10);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fb);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        this.rotationYaw += this.getRNG().nextGaussian() * 45.0;
        this.rotationPitch += this.getRNG().nextGaussian() * 20.0;
        return super.attackEntityFrom(source, amount);
    }

    protected void rechargeVis() {
        this.setCharge(getCharge() + AuraHandler.drainVis(this.world, this.getPosition(), 1, false) * 10);
    }

    public boolean canBePushed() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && isOwner(player) && !isDead) {
            if (player.isSneaking()) {
                playSound(SoundsTC.zap, 1.0f, 1.0f);
                entityDropItem(new ItemStack(ModItems.itemNodeMagnet), 0.5f);
                setDead();
                player.swingArm(hand);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_ / 10.0, p_70653_5_ / 10.0);
        if (this.motionY > 0.1) {
            this.motionY = 0.1;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.dataManager.set(CHARGE, nbt.getFloat("charge"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("charge", this.dataManager.get(CHARGE));
    }

    public float getCharge() {
        return this.dataManager.get(CHARGE);
    }

    public void setCharge(float c) {
        this.dataManager.set(CHARGE, c);
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        super.move(type, x / 5.0, y, z / 5.0);
        float c = (float)((Math.abs(this.motionX) + Math.abs(this.motionY) + Math.abs(this.motionZ)) / 3.0);
        if ((double)c > 0.25) {
            this.kill();
        } else if (this.rand.nextFloat() < c / 20.0f) {
            this.kill();
        }
    }

    protected void kill() {
        this.attackEntityFrom(DamageSource.OUT_OF_WORLD, 40.0f);
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        float b = (float)p_70628_2_ * 0.15f;
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.mind, 1, 1), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.morphicResonator), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalAir), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalFire), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalWater), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalEarth), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalOrder), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalEntropy), 0.5f);
        }
        if (this.rand.nextFloat() < 0.2f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.crystalTaint), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.mechanismComplex), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(ItemsTC.plate), 0.5f);
        }
        if (this.rand.nextFloat() < 0.5f + b) {
            this.entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 20;
    }
}
