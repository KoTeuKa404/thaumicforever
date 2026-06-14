package com.koteuka404.thaumicforever.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;

public class TileRechargePedestal extends TileEntity implements ITickable, IAspectContainer {

    private static final int CHARGE_RANGE = 10;
    private static final int CHARGE_TICK_INTERVAL = 5;
    private static final int CV_PER_PRIMAL_POINT = 10;
    private static final int DIRECT_CV_BUFFER_CAP = CV_PER_PRIMAL_POINT * 2;
    private static final float PORT_BOOST_PERCENT = 1.15f;

    private ItemStack wand = ItemStack.EMPTY;
    private final Map<Aspect, Integer> directCvBuffer = new HashMap<>();

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(2.0D, 2.0D, 2.0D);
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        if (wand.isEmpty()) return;
        if (world.getTotalWorldTime() % CHARGE_TICK_INTERVAL != 0) return;

        AspectList boost = collectPortBoost();
        int totalBoost = 0;
        for (Aspect a : boost.getAspects()) {
            int amt = boost.getAmount(a);
            if (amt > 0) {
                totalBoost += amt;
            }
        }
        if (totalBoost <= 0) return;

        boolean changed = false;
        if (wand.getItem() instanceof IWand) {
            // Keep speed from port throughput, but take ratio weights from nearby node aspects.
            AspectList weights = collectNearbyNodePrimalWeights();
            if (weights == null || weights.size() <= 0) {
                weights = boost;
            }
            changed = addPrimalChargeWeighted(wand, weights, totalBoost);
        } else {
            int before = RechargeHelper.getCharge(wand);
            RechargeHelper.rechargeItemBlindly(wand, null, totalBoost);
            if (RechargeHelper.getCharge(wand) != before) {
                changed = true;
            }
        }

        if (changed) {
            markDirty();
            if (world != null) {
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    public int acceptPrimalCentivis(Aspect aspect, int amountCv) {
        if (world == null || world.isRemote || aspect == null || amountCv <= 0 || wand.isEmpty()) return 0;

        int acceptedCv = bufferDirectCentivis(aspect, amountCv);
        if (acceptedCv <= 0) return 0;

        boolean changed = false;
        if (wand.getItem() instanceof IWand) {
            int freeRoom = getFreePrimalRoom(wand);
            if (freeRoom <= 0) return acceptedCv;

            int availableCv = directCvBuffer.getOrDefault(aspect, 0);
            int spendCv = (availableCv / CV_PER_PRIMAL_POINT) * CV_PER_PRIMAL_POINT;
            if (spendCv <= 0) return acceptedCv;

            AspectList weights = WandHelper.decomposeToPrimals(new AspectList().add(aspect, spendCv));
            if (weights == null || weights.size() <= 0) return acceptedCv;

            int totalPoints = Math.min(sumAspectAmounts(weights) / CV_PER_PRIMAL_POINT, freeRoom);
            while (totalPoints > 0) {
                AspectList add = buildWeightedDelta(wand, weights, totalPoints);
                if (add != null && add.size() > 0 && WandHelper.addPrimalCharge(wand, add, null)) {
                    drainDirectCentivis(aspect, totalPoints * CV_PER_PRIMAL_POINT);
                    changed = true;
                    break;
                }
                totalPoints /= 2;
            }
        } else {
            int availableCv = directCvBuffer.getOrDefault(aspect, 0);
            int spendCv = (availableCv / CV_PER_PRIMAL_POINT) * CV_PER_PRIMAL_POINT;
            if (spendCv <= 0) return acceptedCv;

            int before = RechargeHelper.getCharge(wand);
            RechargeHelper.rechargeItemBlindly(wand, null, spendCv / CV_PER_PRIMAL_POINT);
            changed = RechargeHelper.getCharge(wand) != before;
            if (changed) {
                drainDirectCentivis(aspect, spendCv);
            }
        }

        if (changed) {
            markDirty();
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            return acceptedCv;
        }
        return acceptedCv;
    }

    private int bufferDirectCentivis(Aspect aspect, int amountCv) {
        int current = directCvBuffer.getOrDefault(aspect, 0);
        int room = Math.max(0, DIRECT_CV_BUFFER_CAP - current);
        int accepted = Math.min(amountCv, room);
        if (accepted > 0) {
            directCvBuffer.put(aspect, current + accepted);
        }
        return accepted;
    }

    private void drainDirectCentivis(Aspect aspect, int amountCv) {
        int current = directCvBuffer.getOrDefault(aspect, 0);
        int remaining = current - amountCv;
        if (remaining > 0) {
            directCvBuffer.put(aspect, remaining);
        } else {
            directCvBuffer.remove(aspect);
        }
    }

    private AspectList collectPortBoost() {
        AspectList total = new AspectList();
        if (world == null) return total;

        Set<BlockPos> seen = new HashSet<>();
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        int r = CHARGE_RANGE;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    TileEntity te = world.getTileEntity(scan);
                    if (!(te instanceof TilePort)) continue;

                    BlockPos p = scan.toImmutable();
                    if (!seen.add(p)) continue;

                    AspectList local = ((TilePort) te).getBoostAspects(PORT_BOOST_PERCENT, 0);
                    if (local == null || local.size() <= 0) continue;

                    for (Aspect a : local.getAspects()) {
                        int amt = local.getAmount(a);
                        if (amt > 0) {
                            total.add(a, amt);
                        }
                    }
                }
            }
        }
        return total;
    }

    private AspectList collectNearbyNodePrimalWeights() {
        AspectList best = new AspectList();
        int bestTotal = 0;
        if (world == null) return best;

        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        int r = CHARGE_RANGE;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    TileEntity te = world.getTileEntity(scan);
                    if (!(te instanceof TileBuffNodeStabilizer)) continue;

                    EntityAuraNode node = ((TileBuffNodeStabilizer) te).getFirstNode();
                    if (node == null || node.isDead) continue;
                    AspectList nodeAspects = node.getNodeAspects();
                    if (nodeAspects == null || nodeAspects.size() <= 0) continue;

                    AspectList primal = new AspectList();
                    int sum = 0;
                    for (Aspect p : Aspect.getPrimalAspects()) {
                        int amt = Math.max(0, nodeAspects.getAmount(p));
                        if (amt > 0) {
                            primal.add(p, amt);
                            sum += amt;
                        }
                    }
                    if (sum > bestTotal) {
                        best = primal;
                        bestTotal = sum;
                    }
                }
            }
        }
        return best;
    }

    private boolean addPrimalChargeWeighted(ItemStack wandStack, AspectList sourceWeights, int totalBoost) {
        if (wandStack == null || wandStack.isEmpty() || sourceWeights == null || sourceWeights.size() <= 0) {
            return false;
        }
        if (!(wandStack.getItem() instanceof IWand)) {
            return false;
        }
        int cap = ((IWand) wandStack.getItem()).getMaxCharge(wandStack, null);
        if (cap <= 0) {
            return false;
        }

        List<Aspect> primals = new ArrayList<>();
        int weightSum = 0;
        for (Aspect p : Aspect.getPrimalAspects()) {
            int w = Math.max(0, sourceWeights.getAmount(p));
            if (w > 0) {
                primals.add(p);
                weightSum += w;
            }
        }
        if (primals.isEmpty() || weightSum <= 0) {
            return false;
        }

        // Convert large boost values to a controlled number of per-tick primal points.
        int points = Math.max(1, Math.round(totalBoost * 0.25f));
        AspectList current = WandHelper.getPrimalCharge(wandStack);
        AspectList delta = new AspectList();

        for (int i = 0; i < points; i++) {
            Aspect best = null;
            double bestScore = Double.MAX_VALUE;
            for (Aspect p : primals) {
                int w = Math.max(1, sourceWeights.getAmount(p));
                int have = current.getAmount(p) + delta.getAmount(p);
                if (have >= cap) continue;
                double score = (have + 1.0) / (double) w;
                if (score < bestScore) {
                    bestScore = score;
                    best = p;
                }
            }
            if (best == null) break;
            delta.add(best, 1);
        }

        return delta.size() > 0 && WandHelper.addPrimalCharge(wandStack, delta, null);
    }

    private AspectList buildWeightedDelta(ItemStack wandStack, AspectList sourceWeights, int points) {
        AspectList delta = new AspectList();
        if (wandStack == null || wandStack.isEmpty() || sourceWeights == null || sourceWeights.size() <= 0 || points <= 0) {
            return delta;
        }
        if (!(wandStack.getItem() instanceof IWand)) {
            return delta;
        }

        int cap = ((IWand) wandStack.getItem()).getMaxCharge(wandStack, null);
        if (cap <= 0) {
            return delta;
        }

        AspectList current = WandHelper.getPrimalCharge(wandStack);
        for (int i = 0; i < points; i++) {
            Aspect best = null;
            double bestScore = Double.MAX_VALUE;
            for (Aspect p : Aspect.getPrimalAspects()) {
                int w = Math.max(1, sourceWeights.getAmount(p));
                int have = current.getAmount(p) + delta.getAmount(p);
                if (have >= cap) continue;
                double fullness = (have + 1.0D) / (double) cap;
                double auraBias = Math.min(w, 256) * 0.0005D;
                double score = fullness - auraBias;
                if (score < bestScore) {
                    bestScore = score;
                    best = p;
                }
            }
            if (best == null) break;
            delta.add(best, 1);
        }
        return delta;
    }

    private int getFreePrimalRoom(ItemStack wandStack) {
        if (wandStack == null || wandStack.isEmpty() || !(wandStack.getItem() instanceof IWand)) return 0;

        int cap = ((IWand) wandStack.getItem()).getMaxCharge(wandStack, null);
        if (cap <= 0) return 0;

        AspectList current = WandHelper.getPrimalCharge(wandStack);
        int room = 0;
        for (Aspect p : Aspect.getPrimalAspects()) {
            room += Math.max(0, cap - current.getAmount(p));
        }
        return room;
    }

    private int sumAspectAmounts(AspectList aspects) {
        if (aspects == null || aspects.size() <= 0) return 0;
        int total = 0;
        for (Aspect aspect : aspects.getAspects()) {
            total += Math.max(0, aspects.getAmount(aspect));
        }
        return total;
    }

    public ItemStack getWand() {
        return wand;
    }

    public void setWand(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            this.wand = ItemStack.EMPTY;
        } else {
            this.wand = stack.copy();
            this.wand.setCount(1);
        }
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!wand.isEmpty()) {
            compound.setTag("Wand", wand.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Wand")) {
            wand = new ItemStack(compound.getCompoundTag("Wand"));
        } else {
            wand = ItemStack.EMPTY;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        if (pkt != null && pkt.getNbtCompound() != null) {
            handleUpdateTag(pkt.getNbtCompound());
        }
    }

    @Override
    public AspectList getAspects() {
        ItemStack s = (world == null || world.isRemote) ? wand : getWand();
        if (s.isEmpty()) {
            return null;
        }

        AspectList out = new AspectList();
        if (s.getItem() instanceof IWand) {
            AspectList primal = WandHelper.getPrimalCharge(s);
            for (Aspect p : Aspect.getPrimalAspects()) {
                int amt = Math.max(0, primal.getAmount(p));
                if (amt > 0) {
                    out.add(p, amt);
                }
            }
            int vis = Math.max(0, RechargeHelper.getCharge(s));
            out.add(Aspect.ENERGY, vis);
        } else if (s.getItem() instanceof IRechargable) {
            float c = RechargeHelper.getCharge(s);
            out.add(Aspect.ENERGY, Math.round(c));
        }
        return out.size() > 0 ? out : null;
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
}
