package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
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

public class TileEntityImmortalizer extends TileEntity implements ITickable, IAspectContainer, IEssentiaTransport {

    private final Set<UUID> immunePlayers = new HashSet<>();
    private final Set<UUID> usedPlayers = new HashSet<>();
    private boolean isAnimating = false;
    private int animationState = 3;
    private int animationCooldown = 20;

    public static final Aspect REQUIRED_ASPECT;
    public static final int ESSENTIA_COST = 500;
    private static final int MAX_ESSENTIA = 1000;
    private int storedEssentia = 0;

    static {
        Aspect temp = null;
        try {
            Class<?> tar = Class.forName("org.zeith.thaumicadditions.init.KnowledgeTAR");
            temp = (Aspect) tar.getField("CAELES").get(null);
        } catch (Exception ignored) {}
        REQUIRED_ASPECT = temp;
    }

    public TileEntityImmortalizer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean canAcceptPearl() {
        return REQUIRED_ASPECT != null && storedEssentia >= ESSENTIA_COST;
    }

    public boolean activateImmortality(EntityPlayer player) {
        UUID id = player.getUniqueID();

        if (!immunePlayers.contains(id) && storedEssentia >= ESSENTIA_COST) {
            storedEssentia -= ESSENTIA_COST;
            immunePlayers.add(id);
            usedPlayers.remove(id);
            markDirty();
            return true;
        }

        return false;
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntity();
        UUID id = player.getUniqueID();

        if (immunePlayers.contains(id) && !usedPlayers.contains(id)) {
            immunePlayers.remove(id);
            usedPlayers.add(id);

            event.setCanceled(true);
            player.setHealth(player.getMaxHealth());
            player.capabilities.disableDamage = true;
            player.sendPlayerAbilities();
            startAnimation();

            player.world.scheduleUpdate(player.getPosition(), player.world.getBlockState(player.getPosition()).getBlock(), 1);
            player.capabilities.disableDamage = false;
            player.sendPlayerAbilities();
        }
    }

    private void startAnimation() {
        isAnimating = true;
        animationState = 3;
        animationCooldown = 20;
    }

    @Override
    public void update() {
        if (isAnimating && --animationCooldown <= 0) {
            animationCooldown = 20;
            if (--animationState >= 0) {
                if (!world.isRemote) {
                    world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockImmortalizer.STATE, animationState), 3);
                }
            } else {
                isAnimating = false;
            }
        }

        if (REQUIRED_ASPECT != null && storedEssentia < MAX_ESSENTIA) {
            for (EnumFacing face : EnumFacing.values()) {
                TileEntity te = world.getTileEntity(pos.offset(face));
                if (te instanceof IEssentiaTransport) {
                    IEssentiaTransport t = (IEssentiaTransport) te;
                    if (t.canOutputTo(face.getOpposite()) && REQUIRED_ASPECT.equals(t.getEssentiaType(face.getOpposite()))) {
                        int taken = t.takeEssentia(REQUIRED_ASPECT, 1, face.getOpposite());
                        storedEssentia += taken;
                        if (storedEssentia >= MAX_ESSENTIA) {
                            storedEssentia = MAX_ESSENTIA;
                            break;
                        }
                    }
                }
            }
            markDirty();
        }
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return REQUIRED_ASPECT;
    }

    @Override
    public AspectList getAspects() {
        return new AspectList().add(REQUIRED_ASPECT, storedEssentia);
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect == REQUIRED_ASPECT;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect != REQUIRED_ASPECT || storedEssentia >= MAX_ESSENTIA) return 0;
        int added = Math.min(amount, MAX_ESSENTIA - storedEssentia);
        storedEssentia += added;
        markDirty();
        return added;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (aspect == REQUIRED_ASPECT && storedEssentia >= amount) {
            storedEssentia -= amount;
            markDirty();
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
        return ot.getAmount(REQUIRED_ASPECT) <= storedEssentia;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return aspect == REQUIRED_ASPECT && storedEssentia >= amount;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == REQUIRED_ASPECT ? storedEssentia : 0;
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
