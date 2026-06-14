package com.koteuka404.thaumicforever.tile;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import com.koteuka404.thaumicforever.block.BlockImmortalizer;

public class TileEntityImmortalizer extends TileEntity implements ITickable, IAspectContainer, IEssentiaTransport {

    private final Set<UUID> immunePlayers = new HashSet<>();
    private final Set<UUID> usedPlayers = new HashSet<>();
    private boolean isAnimating = false;
    private int animationTargetState = 0;
    private int animationCooldown = 20;

    public static final Aspect REQUIRED_ASPECT;
    public static final int ESSENTIA_COST = 500;
    private static final int MAX_ESSENTIA = 1000;
    private int storedEssentia = 0;
    private static Method drainEssentiaMethod;
    private static boolean drainEssentiaMethodUnavailable;

    static {
        Aspect temp = null;
        try {
            Class<?> tar = Class.forName("org.zeith.thaumicadditions.init.KnowledgeTAR");
            temp = (Aspect) tar.getField("CAELES").get(null);
        } catch (Exception ignored) {}
        if (temp == null) {
            temp = Aspect.getAspect("caeles");
        }
        if (temp == null) {
            temp = Aspect.getAspect("CAELES");
        }
        REQUIRED_ASPECT = temp;
    }

    public TileEntityImmortalizer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean canAcceptPearl() {
        return REQUIRED_ASPECT != null && storedEssentia >= ESSENTIA_COST;
    }

    public int getStoredEssentia() {
        return storedEssentia;
    }

    public boolean activateImmortality(EntityPlayer player) {
        return activateImmortality(player.getUniqueID());
    }

    public boolean activateImmortality(UUID id) {
        if (id == null) {
            return false;
        }

        if (!immunePlayers.contains(id) && storedEssentia >= ESSENTIA_COST) {
            immunePlayers.add(id);
            usedPlayers.remove(id);
            startAnimationTo(3);
            markDirty();
            return true;
        }

        return false;
    }

    public boolean hasImmortalityFor(UUID id) {
        return id != null && immunePlayers.contains(id);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntity();
        UUID id = player.getUniqueID();

        if (immunePlayers.contains(id) && !usedPlayers.contains(id)) {
            if (storedEssentia < ESSENTIA_COST) {
                return;
            }

            storedEssentia -= ESSENTIA_COST;
            immunePlayers.remove(id);
            usedPlayers.add(id);

            event.setCanceled(true);
            player.setHealth(player.getMaxHealth());
            player.capabilities.disableDamage = true;
            player.sendPlayerAbilities();
            startAnimationTo(0);

            player.world.scheduleUpdate(player.getPosition(), player.world.getBlockState(player.getPosition()).getBlock(), 1);
            player.capabilities.disableDamage = false;
            player.sendPlayerAbilities();

            player.world.playSound(
                    null,
                    pos,
                    net.minecraft.init.SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    net.minecraft.util.SoundCategory.BLOCKS,
                    1.0F,
                    0.85F
            );
            syncStoredEssentia();
        }
    }

    private void startAnimationTo(int targetState) {
        isAnimating = true;
        animationTargetState = Math.max(0, Math.min(3, targetState));
        animationCooldown = 2;
    }

    private int getCurrentBlockStateFrame() {
        if (world == null) return 0;
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockImmortalizer) {
            return state.getValue(BlockImmortalizer.STATE);
        }
        return 0;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }

        if (isAnimating && --animationCooldown <= 0) {
            animationCooldown = 2;

            int current = getCurrentBlockStateFrame();
            int next = current;
            if (current < animationTargetState) {
                next = current + 1;
            } else if (current > animationTargetState) {
                next = current - 1;
            }

            if (next != current) {
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockImmortalizer.STATE, next), 3);
            } else {
                isAnimating = false;
            }
        }

        if (REQUIRED_ASPECT != null && storedEssentia < MAX_ESSENTIA) {
            if (drainOneEssentiaLegacyCompatible()) {
                storedEssentia = Math.min(MAX_ESSENTIA, storedEssentia + 1);
                syncStoredEssentia();
            }
        }
    }

    private void syncStoredEssentia() {
        markDirty();
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private boolean drainOneEssentiaLegacyCompatible() {
        // 1) Keep old behavior that worked in your pack: direct pull from adjacent transports.
        for (EnumFacing face : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(face));
            if (!(te instanceof IEssentiaTransport)) {
                continue;
            }

            IEssentiaTransport t = (IEssentiaTransport) te;
            EnumFacing opposite = face.getOpposite();

            Aspect neighborType = t.getEssentiaType(opposite);
            if (neighborType == null || !REQUIRED_ASPECT.equals(neighborType)) {
                continue;
            }

            int taken = 0;

            // Expected Thaumcraft flow.
            if (t.canOutputTo(opposite)) {
                taken = t.takeEssentia(REQUIRED_ASPECT, 1, opposite);
            }

            // Fallback for addons with unconventional sided flags.
            if (taken <= 0) {
                taken = t.takeEssentia(REQUIRED_ASPECT, 1, face);
            }

            if (taken > 0) {
                return true;
            }
        }

        // 2) Fallback to Thaumcraft network search. Do not use AspectSourceHelper here:
        // its legacy 4-arg reflection signature is wrong for TC6 and spams FML warnings.
        for (EnumFacing direction : EnumFacing.values()) {
            if (drainEssentiaNetworkSilent(direction)) {
                return true;
            }
        }

        return false;
    }

    private boolean drainEssentiaNetworkSilent(EnumFacing direction) {
        if (drainEssentiaMethodUnavailable || REQUIRED_ASPECT == null) {
            return false;
        }

        try {
            if (drainEssentiaMethod == null) {
                Class<?> handler = Class.forName("thaumcraft.common.lib.events.EssentiaHandler");
                drainEssentiaMethod = handler.getMethod(
                        "drainEssentia",
                        TileEntity.class,
                        Aspect.class,
                        EnumFacing.class,
                        int.class,
                        int.class
                );
            }
            Object result = drainEssentiaMethod.invoke(null, this, REQUIRED_ASPECT, direction, 8, 0);
            return result instanceof Boolean && (Boolean) result;
        } catch (Throwable ignored) {
            drainEssentiaMethodUnavailable = true;
            return false;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("storedEssentia", storedEssentia);
        compound.setBoolean("isAnimating", isAnimating);
        compound.setInteger("animationTargetState", animationTargetState);
        compound.setInteger("animationCooldown", animationCooldown);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        storedEssentia = Math.max(0, Math.min(MAX_ESSENTIA, compound.getInteger("storedEssentia")));
        isAnimating = compound.getBoolean("isAnimating");
        animationTargetState = compound.hasKey("animationTargetState")
                ? compound.getInteger("animationTargetState")
                : (compound.hasKey("animationState") ? compound.getInteger("animationState") : 0);
        animationCooldown = compound.hasKey("animationCooldown") ? compound.getInteger("animationCooldown") : 20;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                                 net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return REQUIRED_ASPECT;
    }

    @Override
    public AspectList getAspects() {
        if (REQUIRED_ASPECT == null) return new AspectList();
        return new AspectList().add(REQUIRED_ASPECT, storedEssentia);
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return REQUIRED_ASPECT != null && REQUIRED_ASPECT.equals(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (REQUIRED_ASPECT == null || !REQUIRED_ASPECT.equals(aspect) || storedEssentia >= MAX_ESSENTIA) return 0;
        int added = Math.min(amount, MAX_ESSENTIA - storedEssentia);
        storedEssentia += added;
        if (added > 0) {
            syncStoredEssentia();
        }
        return added;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (REQUIRED_ASPECT != null && REQUIRED_ASPECT.equals(aspect) && storedEssentia >= amount) {
            storedEssentia -= amount;
            syncStoredEssentia();
            return true;
        }
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        if (!doesContainerContain(ot)) return false;
        for (Aspect a : ot.getAspects()) takeFromContainer(a, ot.getAmount(a));
        return true;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        if (REQUIRED_ASPECT == null) return false;
        return ot.getAmount(REQUIRED_ASPECT) <= storedEssentia;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return REQUIRED_ASPECT != null && REQUIRED_ASPECT.equals(aspect) && storedEssentia >= amount;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return REQUIRED_ASPECT != null && REQUIRED_ASPECT.equals(aspect) ? storedEssentia : 0;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return REQUIRED_ASPECT;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return storedEssentia < MAX_ESSENTIA ? 128 : 0;
    }

    @Override
    public int getMinimumSuction() {
        return 32;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return takeFromContainer(aspect, amount) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return addToContainer(aspect, amount);
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return storedEssentia;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
