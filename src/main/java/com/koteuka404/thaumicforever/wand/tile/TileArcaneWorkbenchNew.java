package com.koteuka404.thaumicforever.wand.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koteuka404.thaumicforever.aura.PrimalAuraHandler;
import com.koteuka404.thaumicforever.item.Primal;
import com.koteuka404.thaumicforever.registry.ModBlocks;
import com.koteuka404.thaumicforever.tile.TilePort;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.inventory.InventoryArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class TileArcaneWorkbenchNew extends TileArcaneWorkbench implements ITickable {

    public int auraChunkX, auraChunkZ = -1;
    private static final int VIS_RECHARGE_MIN_PER_TICK = 1;
    private static final int VIS_RECHARGE_MAX_PER_TICK = 8;
    private static final int PORT_BOOST_RANGE = 8;
    private static final int CHARGER_PORT_SCAN_RANGE = 4;
    private static final int CV_PER_PRIMAL_POINT = 10;
    private static final int DIRECT_CV_BUFFER_CAP = CV_PER_PRIMAL_POINT * 2;
    private static final int AURA_PRIMAL_RECHARGE_DIVISOR = 6;
    // With port CV buffer cap (20 units), 1 + round(20 * 1.15) = 24 max aspect boost.
    private static final float PORT_BOOST_PERCENT = 1.15f;
    private NBTTagCompound lastSentWandNbt;
    private final Map<Aspect, Integer> directCvBuffer = new HashMap<>();

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

    public int acceptPrimalCentivis(Aspect aspect, int amountCv) {
        if (world == null || world.isRemote || aspect == null || amountCv <= 0) return 0;
        if (!hasCharger() || getWand().isEmpty()) return 0;

        int acceptedCv = bufferDirectCentivis(aspect, amountCv);
        if (acceptedCv <= 0) return 0;

        int availableCv = directCvBuffer.getOrDefault(aspect, 0);
        int spendCv = (availableCv / CV_PER_PRIMAL_POINT) * CV_PER_PRIMAL_POINT;
        if (spendCv <= 0) return acceptedCv;

        int freeRoom = getFreePrimalRoom(getWand());
        if (freeRoom <= 0) return acceptedCv;

        AspectList weights = WandHelper.decomposeToPrimals(new AspectList().add(aspect, spendCv));
        if (weights == null || weights.size() <= 0) return acceptedCv;

        int totalPoints = Math.min(sumAspectAmounts(weights) / CV_PER_PRIMAL_POINT, freeRoom);
        while (totalPoints > 0) {
            AspectList add = distributeWeightedPoints(getWand(), weights, totalPoints);
            if (add != null && add.size() > 0) {
                WandHelper.addPrimalCharge(getWand(), add, null);
                drainDirectCentivis(aspect, totalPoints * CV_PER_PRIMAL_POINT);
                syncWand();
                return acceptedCv;
            }
            totalPoints /= 2;
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
        TilePort cvPort = findConnectedCvPort();

        if (cvPort != null) {
            rechargeWandFromCvPort(cvPort);
        } else {
            rechargeWandFromPrimalAura(drainPos);
        }

        advanceAuraScanChunk();
    }

    private void rechargeWandFromCvPort(TilePort cvPort) {
        AspectList weights = cvPort.getPrimalCvWeights();
        if (weights == null || weights.size() <= 0) return;

        int availablePoints = cvPort.getAvailablePrimalCvPoints(CV_PER_PRIMAL_POINT);
        if (availablePoints <= 0) return;

        int totalPoints = Math.min(availablePoints, getFreePrimalRoom(getWand()));
        while (totalPoints > 0) {
            AspectList add = distributeWeightedPoints(getWand(), weights, totalPoints);
            if (add != null && add.size() > 0 && cvPort.drainPrimalCvForRecharge(add, CV_PER_PRIMAL_POINT)) {
                WandHelper.addPrimalCharge(getWand(), add, null);
                return;
            }
            totalPoints /= 2;
        }
    }

    private void rechargeWandFromPrimalAura(BlockPos drainPos) {
        AspectList weights = buildRechargeWeights(drainPos, null);

        int visPerTick = computeDynamicVisPerTick(weights);
        RechargeHelper.rechargeItem(getWorld(), getWand(), drainPos, null, visPerTick);

        int totalPoints = Math.min(getAvailablePrimalAuraPoints(drainPos) / AURA_PRIMAL_RECHARGE_DIVISOR, getFreePrimalRoom(getWand()));
        while (totalPoints > 0) {
            AspectList add = distributeWeightedPoints(getWand(), weights, totalPoints);
            if (add != null && add.size() > 0 && drainPrimalForRecharge(drainPos, add)) {
                WandHelper.addPrimalCharge(getWand(), add, null);
                return;
            }
            totalPoints /= 2;
        }
    }

    private void advanceAuraScanChunk() {
        auraChunkX++;
        if (auraChunkX > 1) {
            auraChunkX = -1;
            auraChunkZ++;
            if (auraChunkZ > 1)
                auraChunkZ = auraChunkX;
        }
    }

    private TilePort findConnectedCvPort() {
        if (world == null) return null;
        BlockPos chargerPos = getPos().up();

        TilePort best = null;
        double bestDistance = Double.MAX_VALUE;
        int r = CHARGER_PORT_SCAN_RANGE;
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    scan.setPos(chargerPos.getX() + dx, chargerPos.getY() + dy, chargerPos.getZ() + dz);
                    TileEntity te = world.getTileEntity(scan);
                    if (!(te instanceof TilePort) || te.isInvalid()) continue;

                    TilePort port = (TilePort) te;
                    if (port.isLocalPedestalEndpoint()) continue;

                    double distance = chargerPos.distanceSq(scan);
                    if (best == null || distance < bestDistance) {
                        best = port;
                        bestDistance = distance;
                    }
                }
            }
        }

        return best;
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
                double fullness = (have + 1.0D) / (double) cap;
                double auraBias = Math.min(w, 256) * 0.0005D;
                double score = fullness - auraBias;
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

    private int getFreePrimalRoom(ItemStack wand) {
        if (wand == null || wand.isEmpty()) return 0;
        if (!(wand.getItem() instanceof IWand)) return 0;

        int cap = ((IWand) wand.getItem()).getMaxCharge(wand, null);
        if (cap <= 0) return 0;

        AspectList current = WandHelper.getPrimalCharge(wand);
        int room = 0;
        for (Aspect a : getAllPrimalAspects()) {
            room += Math.max(0, cap - current.getAmount(a));
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

    private int getAvailablePrimalAuraPoints(BlockPos drainPos) {
        if (world == null || drainPos == null) return 0;

        int available = 0;
        for (Aspect a : getAllPrimalAspects()) {
            Primal p = primalFromAspect(a);
            if (p == null) continue;
            available += Math.max(0, (int) Math.floor(PrimalAuraHandler.get(world, drainPos, p)));
        }
        return available;
    }

    private boolean drainPrimalForRecharge(BlockPos drainPos, AspectList delta) {
        if (world == null || delta == null || delta.size() <= 0) return false;

        for (Aspect a : getAllPrimalAspects()) {
            int need = delta.getAmount(a);
            if (need <= 0) continue;

            Primal p = primalFromAspect(a);
            if (p == null) return false;

            float simulated = PrimalAuraHandler.drain(world, drainPos, p, (float) need, true);
            if (simulated + 0.0001F < (float) need) return false;
        }

        for (Aspect a : getAllPrimalAspects()) {
            int need = delta.getAmount(a);
            if (need <= 0) continue;

            Primal p = primalFromAspect(a);
            if (p != null) {
                PrimalAuraHandler.drain(world, drainPos, p, (float) need, false);
            }
        }

        return true;
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
