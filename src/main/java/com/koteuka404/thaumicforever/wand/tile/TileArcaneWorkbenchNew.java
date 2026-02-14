package com.koteuka404.thaumicforever.wand.tile;

import com.koteuka404.thaumicforever.Primal;
import com.koteuka404.thaumicforever.PrimalAuraHandler;
import com.koteuka404.thaumicforever.wand.inventory.InventoryArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class TileArcaneWorkbenchNew extends TileArcaneWorkbench implements ITickable {

    public int auraChunkX, auraChunkZ = -1;

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
        return world.getBlockState(getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger;
    }

    private ItemStack getWand() {
        return inventoryCraft.getStackInSlot(15);
    }

    private void syncWand() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("wandStack", inventoryCraft.getStackInSlot(15).writeToNBT(new NBTTagCompound()));
        sendMessageToClient(nbt, null);
    }

    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        super.messageFromServer(nbt);
        if (nbt.hasKey("wandStack"))
            this.inventoryCraft.setInventorySlotContents(15, new ItemStack(nbt.getCompoundTag("wandStack")));

    }

    public void rechargeWand() {
        BlockPos drainPos = getPos().add(auraChunkX * 16, 0, auraChunkZ * 16);
        RechargeHelper.rechargeItem(getWorld(), getWand(), drainPos, null, 1);
        if (PrimalAuraHandler.consumeSet(getWorld(), drainPos, 1, false)) {
            WandHelper.addPrimalChargeDistributed(getWand(), 1, null);
        }
        auraChunkX++;
        if (auraChunkX > 1) {
            auraChunkX = -1;
            auraChunkZ++;
            if (auraChunkZ > 1)
                auraChunkZ = auraChunkX;
        }
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
