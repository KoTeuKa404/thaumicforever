package com.koteuka404.thaumicforever.tile;

import java.util.List;

import com.koteuka404.thaumicforever.api.VoidBeaconRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.EntityUtils;

public class TileVoidBeacon extends TileEntity implements ITickable, IAspectContainer, IEssentiaTransport {
    private static final int CAPACITY = 100;
    private static final int REQUIRED_PROGRESS = 200;
    private static final double RIFT_RANGE = 16.0D;

    private Aspect essentiaType;
    private int essentiaAmount;
    private int progress;
    private int levels;
    private int ticks;
    private boolean clearSky;

    @Override
    public void update() {
        if (world == null) return;
        ticks++;

        if (!world.isRemote && ticks % 5 == 0) fillFromPipes();
        if (ticks % 80 == 0) updateStructure();
        if (world.isRemote || ticks % 20 != 0 || !isOperational()) return;

        if (progress < REQUIRED_PROGRESS && hasRequiredEssentia()) drainRifts();
        while (progress >= REQUIRED_PROGRESS && hasRequiredEssentia()) {
            if (!hasEmptyOutputSlot()) break;
            ItemStack output = VoidBeaconRegistry.choose(essentiaType, world.rand);
            if (output.isEmpty() || !eject(output, true)) break;
            eject(output, false);
            progress -= REQUIRED_PROGRESS;
            essentiaAmount -= getRequiredEssentia();
            if (essentiaAmount <= 0) {
                essentiaAmount = 0;
                essentiaType = null;
            }
            sync();
        }
    }

    private boolean isOperational() {
        return clearSky && !world.isBlockPowered(pos) && levels >= 0;
    }

    private boolean hasRequiredEssentia() {
        return essentiaType != null && essentiaAmount >= getRequiredEssentia();
    }

    public int getRequiredEssentia() {
        switch (levels) {
            case 4: return 1;
            case 3: return 2;
            case 2: return 5;
            case 1: return 10;
            default: return 20;
        }
    }

    private void drainRifts() {
        boolean changed = false;
        for (EntityFluxRift rift : getValidRifts()) {
            double drain = Math.sqrt(rift.getRiftSize());
            int gained = (int) drain;
            if (gained <= 0) continue;
            progress += gained;
            rift.setRiftStability(rift.getRiftStability() - (float) (drain / 15.0D));
            if (world.rand.nextInt(33) == 0) rift.setRiftSize(Math.max(1, rift.getRiftSize() - 1));
            changed = true;
        }
        if (changed) {
            world.addBlockEvent(pos, getBlockType(), 4, ticks);
            sync();
        }
    }

    private List<EntityFluxRift> getValidRifts() {
        AxisAlignedBB area = new AxisAlignedBB(pos).grow(RIFT_RANGE);
        List<EntityFluxRift> rifts = world.getEntitiesWithinAABB(EntityFluxRift.class, area);
        rifts.removeIf(rift -> rift.isDead || rift.getRiftSize() <= 1 || !canSeeBeacon(rift));
        return rifts;
    }

    private boolean canSeeBeacon(EntityFluxRift rift) {
        Vec3d beacon = new Vec3d(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
        Vec3d riftPosition = new Vec3d(rift.posX, rift.posY, rift.posZ);
        Vec3d sightTarget = beacon.add(riftPosition.subtract(beacon).normalize());
        return EntityUtils.canEntityBeSeen(rift, sightTarget.x, sightTarget.y, sightTarget.z);
    }

    private void updateStructure() {
        boolean oldSky = clearSky;
        int oldLevels = levels;
        clearSky = world.canSeeSky(pos.up());
        levels = 0;
        if (clearSky) {
            for (int level = 1; level <= 4 && isLevelComplete(level); level++) levels = level;
        }
        if (!world.isRemote && (oldSky != clearSky || oldLevels != levels)) sync();
    }

    private boolean isLevelComplete(int level) {
        int y = pos.getY() - level;
        for (int x = pos.getX() - level; x <= pos.getX() + level; x++) {
            for (int z = pos.getZ() - level; z <= pos.getZ() + level; z++) {
                if (world.getBlockState(new net.minecraft.util.math.BlockPos(x, y, z)).getBlock() != BlocksTC.metalBlockVoid) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean eject(ItemStack stack, boolean simulate) {
        ItemStack remaining = stack.copy();
        for (EnumFacing side : EnumFacing.values()) {
            TileEntity target = world.getTileEntity(pos.offset(side));
            if (target == null || !target.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) continue;
            IItemHandler handler = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
            if (handler != null) remaining = ItemHandlerHelper.insertItem(handler, remaining, simulate);
            if (remaining.isEmpty()) return true;
        }
        return false;
    }

    private boolean hasEmptyOutputSlot() {
        for (EnumFacing side : EnumFacing.values()) {
            TileEntity target = world.getTileEntity(pos.offset(side));
            if (target == null || !target.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) continue;
            IItemHandler handler = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
            if (handler == null) continue;
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (handler.getStackInSlot(slot).isEmpty()) return true;
            }
        }
        return false;
    }

    private void fillFromPipes() {
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (essentiaAmount >= CAPACITY) return;
            TileEntity tile = ThaumcraftApiHelper.getConnectableTile(world, pos, side);
            if (!(tile instanceof IEssentiaTransport)) continue;
            IEssentiaTransport transport = (IEssentiaTransport) tile;
            EnumFacing input = side.getOpposite();
            if (!transport.canOutputTo(input)) continue;
            Aspect type = transport.getEssentiaType(input);
            if (type == null || (essentiaType != null && essentiaType != type)) continue;
            if (getSuctionAmount(side) <= transport.getSuctionAmount(input)
                    || getSuctionAmount(side) < transport.getMinimumSuction()) continue;
            int taken = transport.takeEssentia(type, 1, input);
            if (taken > 0) addToContainer(type, taken);
        }
    }

    public void clearEssentia() {
        if (world != null && !world.isRemote && essentiaAmount > 0) {
            AuraHelper.polluteAura(world, pos, essentiaAmount, true);
        }
        essentiaType = null;
        essentiaAmount = 0;
        sync();
    }

    public boolean isBeamActive() {
        return clearSky && world != null && !world.isBlockPowered(pos);
    }

    public int getProgress() { return progress; }
    public int getLevels() { return levels; }
    public int getEssentiaAmount() { return essentiaAmount; }
    public Aspect getEssentiaType() { return essentiaType; }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (essentiaType != null) tag.setString("EssentiaType", essentiaType.getTag());
        tag.setInteger("EssentiaAmount", essentiaAmount);
        tag.setInteger("Progress", progress);
        tag.setInteger("Levels", levels);
        tag.setBoolean("ClearSky", clearSky);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        essentiaType = tag.hasKey("EssentiaType") ? Aspect.getAspect(tag.getString("EssentiaType")) : null;
        essentiaAmount = tag.getInteger("EssentiaAmount");
        progress = tag.getInteger("Progress");
        levels = tag.getInteger("Levels");
        clearSky = tag.getBoolean("ClearSky");
    }

    private void sync() {
        markDirty();
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(super.getUpdateTag()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 1, getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean receiveClientEvent(int id, int type) {
        if (id == 4) {
            for (EntityFluxRift rift : getValidRifts()) {
                FXDispatcher.INSTANCE.voidStreak(rift.posX, rift.posY, rift.posZ,
                        pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, type, 0.04F);
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override public boolean isConnectable(EnumFacing face) { return canInputFrom(face); }
    @Override public boolean canInputFrom(EnumFacing face) { return face != EnumFacing.UP && face != EnumFacing.DOWN; }
    @Override public boolean canOutputTo(EnumFacing face) { return false; }
    @Override public void setSuction(Aspect aspect, int amount) {}
    @Override public Aspect getSuctionType(EnumFacing face) { return essentiaType; }
    @Override public int getSuctionAmount(EnumFacing face) { return essentiaAmount < CAPACITY ? 128 : 0; }
    @Override public int takeEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }
    @Override public int getMinimumSuction() { return 0; }
    @Override public Aspect getEssentiaType(EnumFacing face) { return essentiaType; }
    @Override public int getEssentiaAmount(EnumFacing face) { return essentiaAmount; }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!canInputFrom(face) || (essentiaType != null && essentiaType != aspect)) return 0;
        return amount - addToContainer(aspect, amount);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (amount <= 0 || (essentiaType != null && essentiaType != aspect)) return amount;
        int accepted = Math.min(amount, CAPACITY - essentiaAmount);
        if (accepted > 0) {
            essentiaType = aspect;
            essentiaAmount += accepted;
            sync();
        }
        return amount - accepted;
    }

    @Override public int containerContains(Aspect aspect) { return essentiaType == aspect ? essentiaAmount : 0; }
    @Override public boolean doesContainerAccept(Aspect aspect) { return essentiaType == null || essentiaType == aspect; }
    @Override public boolean doesContainerContainAmount(Aspect aspect, int amount) { return containerContains(aspect) >= amount; }
    @Override public boolean doesContainerContain(AspectList list) {
        for (Aspect aspect : list.getAspects()) if (!doesContainerContainAmount(aspect, list.getAmount(aspect))) return false;
        return true;
    }
    @Override public AspectList getAspects() {
        AspectList list = new AspectList();
        if (essentiaType != null) list.add(essentiaType, essentiaAmount);
        return list;
    }
    @Override public void setAspects(AspectList list) {
        essentiaType = null;
        essentiaAmount = 0;
        if (list != null && list.size() > 0) {
            essentiaType = list.getAspectsSortedByAmount()[0];
            essentiaAmount = Math.min(CAPACITY, list.getAmount(essentiaType));
        }
        sync();
    }
    @Override public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!doesContainerContainAmount(aspect, amount)) return false;
        essentiaAmount -= amount;
        if (essentiaAmount <= 0) { essentiaAmount = 0; essentiaType = null; }
        sync();
        return true;
    }
    @Override public boolean takeFromContainer(AspectList list) {
        if (!doesContainerContain(list)) return false;
        for (Aspect aspect : list.getAspects()) takeFromContainer(aspect, list.getAmount(aspect));
        return true;
    }

    @Override public double getMaxRenderDistanceSquared() { return 65536.0D; }
    @Override public AxisAlignedBB getRenderBoundingBox() { return INFINITE_EXTENT_AABB; }
}
