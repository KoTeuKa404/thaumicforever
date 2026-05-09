package com.koteuka404.thaumicforever.tile;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.items.IRechargable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;

public class TileRechargePedestal extends TileEntity implements ITickable, IAspectContainer {

    private static final int CHARGE_RANGE = 10;
    private static final int CHARGE_TICK_INTERVAL = 5;
    private static final float PORT_BOOST_PERCENT = 1.15f;

    private ItemStack wand = ItemStack.EMPTY;

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
