package com.koteuka404.thaumicforever.wand.tile;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.item.Primal;
import com.koteuka404.thaumicforever.tile.TilePort;
import com.koteuka404.thaumicforever.wand.inventory.InventoryArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import com.koteuka404.thaumicforever.aura.PrimalAuraHandler;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.tile.TileBuffNodeStabilizer;

public class TileArcaneWorkbenchNew extends TileArcaneWorkbench implements ITickable {

    public int auraChunkX, auraChunkZ = -1;
    private static final int VIS_RECHARGE_MIN_PER_TICK = 1;
    private static final int VIS_RECHARGE_MAX_PER_TICK = 8;
    private static final int PORT_BOOST_RANGE = 8;
    // With port CV buffer cap (20 units), 1 + round(20 * 1.15) = 24 max aspect boost.
    private static final float PORT_BOOST_PERCENT = 1.15f;
    private NBTTagCompound lastSentWandNbt;

    public TileArcaneWorkbenchNew() {
        inventoryCraft = new InventoryArcaneWorkbenchNew(this, new ContainerDummy());
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (world.getTotalWorldTime() % 30 == 0)
                if (hasCharger() && !getWand().isEmpty()) {
                    rechargeWand();
                }
            syncWand();

        }

    }

    private boolean hasCharger() {
        return world.getBlockState(getPos().up()).getBlock() == ModBlocks.ARCANE_WORKBENCH_WAND_CHARGER;
    }

    private ItemStack getWand() {
        return inventoryCraft.getStackInSlot(15);
    }

    private void syncWand() {
        ItemStack wand = inventoryCraft.getStackInSlot(15);
        NBTTagCompound wandNbt = wand.writeToNBT(new NBTTagCompound());
        if (lastSentWandNbt != null && lastSentWandNbt.equals(wandNbt)) {
            return;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("wandStack", wandNbt);
        sendMessageToClient(nbt, null);
        lastSentWandNbt = wandNbt.copy();
    }

    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        super.messageFromServer(nbt);
        if (!nbt.hasKey("wandStack")) return;
        ItemStack incoming = new ItemStack(nbt.getCompoundTag("wandStack"));
        ItemStack current = this.inventoryCraft.getStackInSlot(15);

        if (current.isEmpty() || incoming.isEmpty() || current.getItem() != incoming.getItem()) {
            this.inventoryCraft.setInventorySlotContents(15, incoming);
            return;
        }

        current.setCount(incoming.getCount());
        current.setItemDamage(incoming.getItemDamage());
        if (incoming.hasTagCompound()) {
            current.setTagCompound(incoming.getTagCompound().copy());
        } else {
            current.setTagCompound(null);
        }

    }

    public void rechargeWand() {
        BlockPos drainPos = getPos().add(auraChunkX * 16, 0, auraChunkZ * 16);
        AspectList boost = getPortBoostAspects();
        AspectList weights = collectNearbyNodePrimalWeights();
        if (weights == null || weights.size() <= 0) {
            weights = buildRechargeWeights(drainPos, boost);
        } else if (boost != null && boost.size() > 0) {
            // Preserve node ratio as primary source, but let active port boost bias it slightly.
            for (Aspect a : getAllPrimalAspects()) {
                int b = Math.max(0, boost.getAmount(a));
                if (b > 0) {
                    weights.add(a, b);
                }
            }
        }

        int visPerTick = computeDynamicVisPerTick(weights);
        RechargeHelper.rechargeItem(getWorld(), getWand(), drainPos, null, visPerTick);

        if (PrimalAuraHandler.consumeSet(getWorld(), drainPos, 1, false)) {
            int totalPoints = 6 + getTotalPrimalBoost(boost);
            AspectList add = distributeWeightedPoints(getWand(), weights, totalPoints);
            if (add != null && add.size() > 0) {
                WandHelper.addPrimalCharge(getWand(), add, null);
            }
        }
        auraChunkX++;
        if (auraChunkX > 1) {
            auraChunkX = -1;
            auraChunkZ++;
            if (auraChunkZ > 1)
                auraChunkZ = auraChunkX;
        }
    }

    private AspectList getPortBoostAspects() {
        if (world == null) return null;
        BlockPos chargerPos = getPos().up();
        int r = PORT_BOOST_RANGE;
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(getPos().getX() + dx, getPos().getY() + dy, getPos().getZ() + dz);
                    if (scan.equals(chargerPos)) continue;
                    if (!(world.getTileEntity(scan) instanceof TilePort)) continue;
                    TilePort port = (TilePort) world.getTileEntity(scan);
                    if (port == null || port.isInvalid()) continue;
                    if (chargerPos.equals(port.getChargerTarget()) && port.isPrimaryChargerLink()) {
                        AspectList boost = port.getBoostAspects(PORT_BOOST_PERCENT, 1);
                        if (boost != null && boost.size() > 0) return boost;
                    }
                }
            }
        }
        return null;
    }

    private int getTotalPrimalBoost(AspectList boost) {
        if (boost == null || boost.size() <= 0) return 0;
        int total = 0;
        for (Aspect a : getAllPrimalAspects()) {
            int amt = boost.getAmount(a);
            if (amt > 0) total += amt;
        }
        return total;
    }

    private AspectList buildRechargeWeights(BlockPos drainPos, AspectList boost) {
        AspectList out = new AspectList();
        for (Aspect a : getAllPrimalAspects()) {
            Primal p = primalFromAspect(a);
            if (p == null) continue;

            int auraWeight = (int) Math.floor(Math.max(0f, PrimalAuraHandler.get(world, drainPos, p)));
            int boostWeight = (boost != null) ? Math.max(0, boost.getAmount(a)) : 0;
            int w = auraWeight + boostWeight;
            if (w > 0) {
                out.add(a, w);
            }
        }
        return out;
    }

    private int computeDynamicVisPerTick(AspectList weights) {
        if (weights == null || weights.size() <= 0) {
            return VIS_RECHARGE_MIN_PER_TICK;
        }

        int sum = 0;
        int count = 0;
        for (Aspect a : getAllPrimalAspects()) {
            sum += Math.max(0, weights.getAmount(a));
            count++;
        }
        if (count <= 0) {
            return VIS_RECHARGE_MIN_PER_TICK;
        }

        float avg = (float) sum / (float) count;
        // Keep old baseline close to 2 at common node values, but scale dynamically by aura/node balance.
        int dynamic = Math.round(avg / 6.0f);
        if (dynamic < VIS_RECHARGE_MIN_PER_TICK) dynamic = VIS_RECHARGE_MIN_PER_TICK;
        if (dynamic > VIS_RECHARGE_MAX_PER_TICK) dynamic = VIS_RECHARGE_MAX_PER_TICK;
        return dynamic;
    }

    private AspectList collectNearbyNodePrimalWeights() {
        AspectList best = new AspectList();
        int bestTotal = 0;
        if (world == null) return best;

        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        int r = PORT_BOOST_RANGE;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(getPos().getX() + dx, getPos().getY() + dy, getPos().getZ() + dz);
                    if (!(world.getTileEntity(scan) instanceof com.koteuka404.thaumicforever.tile.TileBuffNodeStabilizer)) continue;

                    com.koteuka404.thaumicforever.tile.TileBuffNodeStabilizer stab =
                            (com.koteuka404.thaumicforever.tile.TileBuffNodeStabilizer) world.getTileEntity(scan);
                    if (stab == null || stab.isInvalid()) continue;

                    com.koteuka404.thaumicforever.entity.EntityAuraNode node = stab.getFirstNode();
                    if (node == null || node.isDead) continue;

                    AspectList nodeAspects = node.getNodeAspects();
                    if (nodeAspects == null || nodeAspects.size() <= 0) continue;

                    AspectList primal = new AspectList();
                    int sum = 0;
                    for (Aspect p : getAllPrimalAspects()) {
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

    private AspectList distributeWeightedPoints(ItemStack wand, AspectList weights, int points) {
        AspectList delta = new AspectList();
        if (wand == null || wand.isEmpty() || points <= 0) return delta;
        if (!(wand.getItem() instanceof IWand)) return delta;

        int cap = ((IWand) wand.getItem()).getMaxCharge(wand, null);
        if (cap <= 0) return delta;

        List<Aspect> primals = new ArrayList<>();
        int weightSum = 0;
        if (weights != null && weights.size() > 0) {
            for (Aspect a : getAllPrimalAspects()) {
                int w = Math.max(0, weights.getAmount(a));
                if (w > 0) {
                    primals.add(a);
                    weightSum += w;
                }
            }
        }

        if (primals.isEmpty() || weightSum <= 0) {
            for (Aspect a : getAllPrimalAspects()) {
                primals.add(a);
            }
        }

        AspectList current = WandHelper.getPrimalCharge(wand);
        for (int i = 0; i < points; i++) {
            Aspect best = null;
            double bestScore = Double.MAX_VALUE;
            for (Aspect a : primals) {
                int w = Math.max(1, (weights != null) ? weights.getAmount(a) : 1);
                int have = current.getAmount(a) + delta.getAmount(a);
                if (have >= cap) continue;
                double score = (have + 1.0D) / (double) w;
                if (score < bestScore) {
                    bestScore = score;
                    best = a;
                }
            }
            if (best == null) break;
            delta.add(best, 1);
        }

        return delta;
    }

    public boolean hasEnoughPrimalForCrystals(AspectList crystals) {
        if (world == null) return false;
        if (crystals == null || crystals.size() <= 0) return true;

        for (Aspect a : getAllPrimalAspects()) {
            int needInt = crystals.getAmount(a);
            if (needInt <= 0) continue;

            Primal p = primalFromAspect(a);
            if (p == null) return false;

            float have = PrimalAuraHandler.get(world, pos, p);
            if (have + 0.0001f < (float) needInt) return false;
        }
        return true;
    }

    public boolean drainPrimalForCrystals(AspectList crystals) {
        if (world == null) return false;
        if (crystals == null || crystals.size() <= 0) return true;

        for (Aspect a : getAllPrimalAspects()) {
            int needInt = crystals.getAmount(a);
            if (needInt <= 0) continue;

            Primal p = primalFromAspect(a);
            if (p == null) return false;

            float need = (float) needInt;
            float sim = PrimalAuraHandler.drain(world, pos, p, need, true);
            if (sim + 0.0001f < need) return false;
        }

        for (Aspect a : getAllPrimalAspects()) {
            int needInt = crystals.getAmount(a);
            if (needInt <= 0) continue;

            Primal p = primalFromAspect(a);
            if (p == null) return false;

            PrimalAuraHandler.drain(world, pos, p, (float) needInt, false);
        }

        return true;
    }

    private static Aspect[] getAllPrimalAspects() {
        return new Aspect[]{Aspect.FIRE, Aspect.EARTH, Aspect.AIR, Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY};
    }

    private static Primal primalFromAspect(Aspect a) {
        if (a == null) return null;
        if (a == Aspect.FIRE) return Primal.IGNIS;
        if (a == Aspect.EARTH) return Primal.TERRA;
        if (a == Aspect.AIR) return Primal.AER;
        if (a == Aspect.WATER) return Primal.AQUA;
        if (a == Aspect.ORDER) return Primal.ORDO;
        if (a == Aspect.ENTROPY) return Primal.PERDITIO;
        return null;
    }

}
