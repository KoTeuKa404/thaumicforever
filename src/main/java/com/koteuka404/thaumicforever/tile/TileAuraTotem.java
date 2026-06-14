package com.koteuka404.thaumicforever.tile;

import com.koteuka404.thaumicforever.aura.PrimalAuraChunk;
import com.koteuka404.thaumicforever.aura.PrimalAuraHandler;
import com.koteuka404.thaumicforever.aura.PrimalAuraHandler.PrimalAuraWorldData;
import com.koteuka404.thaumicforever.block.BlockAuraTotem;
import com.koteuka404.thaumicforever.block.BlockAuraTotem.TotemType;
import com.koteuka404.thaumicforever.block.BlockAuraTotemPole;
import com.koteuka404.thaumicforever.block.BlockAuraTotemPole.PoleType;
import com.koteuka404.thaumicforever.item.Primal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.inventory.ItemStackHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;

public class TileAuraTotem extends TileEntity implements ITickable, IInventory {
    private static final int MAX_POLES = 5;
    private static final int MAX_TIME = 500;
    private static final float TARGET_SOFT_CAP_MULTIPLIER = 1.35F;
    private static final float TARGET_SOFT_CAP_FLAT_BONUS = 8.0F;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private int tickCounter;
    private int time;
    private byte activePrimal = -1;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockAuraTotem)) return;

        TotemType type = state.getValue(BlockAuraTotem.TYPE);
        if (type != TotemType.PUSH && type != TotemType.PULL) return;

        Structure structure = scanStructure();
        int cooldown = Math.max(1, 10 - structure.inner - structure.outer);
        if (++tickCounter < cooldown) return;
        tickCounter = 0;

        if (time <= 0 && !consumeCrystalCharge()) return;

        time--;
        performMagnet(type, structure);
        markDirty();
    }

    private boolean consumeCrystalCharge() {
        ItemStack stack = inventory.get(0);
        if (!isValidCrystal(stack)) return false;

        Primal primal = primalFromCrystal(stack);
        activePrimal = primal == null ? -1 : (byte) primal.id;
        stack.shrink(1);
        if (stack.isEmpty()) inventory.set(0, ItemStack.EMPTY);
        time = MAX_TIME;
        return true;
    }

    private Structure scanStructure() {
        Structure structure = new Structure();
        for (int i = 1; i <= MAX_POLES; i++) {
            BlockPos check = pos.down(i);
            IBlockState state = world.getBlockState(check);
            if (!(state.getBlock() instanceof BlockAuraTotemPole)) break;

            PoleType poleType = state.getValue(BlockAuraTotemPole.TYPE);
            if (poleType == PoleType.POLE_INNER) structure.inner++;
            else if (poleType == PoleType.POLE_OUTER) structure.outer++;
            else if (poleType == PoleType.POLE_PURE) structure.pure++;
        }
        return structure;
    }

    private void performMagnet(TotemType type, Structure structure) {
        TargetPair pair = selectTargets(structure);
        if (pair == null) return;

        BlockPos source = type == TotemType.PULL ? pair.outer : pair.inner;
        BlockPos target = type == TotemType.PULL ? pair.inner : pair.outer;
        if (!world.isBlockLoaded(source) || !world.isBlockLoaded(target)) return;

        Primal primal = getActivePrimal();
        if (getCapacity(target, primal) < 1.0F) return;

        float taken = PrimalAuraHandler.drain(world, source, primal, 1.0F, false);
        if (taken <= 0.0F) return;

        float pureReduction = type == TotemType.PULL ? 0.025F : 0.1F;
        float fluxChance = Math.max(0.0F, 0.0125F * (structure.inner + structure.outer + 2) - pureReduction * structure.pure);
        if (fluxChance > 0.0F && world.rand.nextFloat() < fluxChance) {
            AuraHelper.polluteAura(world, pos, 1.0F, true);
            return;
        }

        PrimalAuraHandler.add(world, target, primal, Math.min(taken, getCapacity(target, primal)));
    }

    private TargetPair selectTargets(Structure structure) {
        int innerRadius = Math.max(0, structure.inner);
        int outerRadius = innerRadius + Math.max(0, structure.outer) + 1;

        int innerX = randomBetween(-innerRadius, innerRadius);
        int innerZ = randomBetween(-innerRadius, innerRadius);

        int outerX = 0;
        int outerZ = 0;
        int tries = 0;
        do {
            outerX = randomBetween(-outerRadius, outerRadius);
            outerZ = randomBetween(-outerRadius, outerRadius);
            tries++;
        } while (tries < 100 && Math.abs(outerX) <= innerRadius && Math.abs(outerZ) <= innerRadius);

        if (tries >= 100) return null;

        BlockPos inner = pos.add(innerX * 16, 0, innerZ * 16);
        BlockPos outer = pos.add(outerX * 16, 0, outerZ * 16);
        return new TargetPair(inner, outer);
    }

    private int randomBetween(int min, int max) {
        return min + world.rand.nextInt(max - min + 1);
    }

    private Primal getActivePrimal() {
        if (activePrimal >= 0 && activePrimal < Primal.COUNT) {
            return Primal.values()[activePrimal];
        }
        return Primal.values()[world.rand.nextInt(Primal.COUNT)];
    }

    private float getCapacity(BlockPos target, Primal primal) {
        PrimalAuraChunk chunk = PrimalAuraWorldData.get(world).getChunkAt(world, target);
        float cap = chunk.base[primal.id] * TARGET_SOFT_CAP_MULTIPLIER + TARGET_SOFT_CAP_FLAT_BONUS;
        return Math.max(0.0F, cap - chunk.vis[primal.id]);
    }

    public static boolean isValidCrystal(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ItemsTC.crystalEssence;
    }

    private static Primal primalFromCrystal(ItemStack stack) {
        if (!isValidCrystal(stack) || !(stack.getItem() instanceof ItemGenericEssentiaContainer)) return null;
        AspectList aspects = ((ItemGenericEssentiaContainer) stack.getItem()).getAspects(stack);
        if (aspects == null || aspects.size() <= 0) return null;
        Aspect aspect = aspects.getAspects()[0];
        return primalFromAspect(aspect);
    }

    private static Primal primalFromAspect(Aspect aspect) {
        if (aspect == Aspect.FIRE) return Primal.IGNIS;
        if (aspect == Aspect.EARTH) return Primal.TERRA;
        if (aspect == Aspect.AIR) return Primal.AER;
        if (aspect == Aspect.WATER) return Primal.AQUA;
        if (aspect == Aspect.ORDER) return Primal.ORDO;
        if (aspect == Aspect.ENTROPY) return Primal.PERDITIO;
        return null;
    }

    public boolean insertCrystal(ItemStack stack) {
        if (!isValidCrystal(stack)) return false;
        ItemStack current = inventory.get(0);
        if (current.isEmpty()) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            inventory.set(0, copy);
            markDirty();
            return true;
        }
        if (ItemStack.areItemsEqual(current, stack) && ItemStack.areItemStackTagsEqual(current, stack) && current.getCount() < getInventoryStackLimit()) {
            current.grow(1);
            markDirty();
            return true;
        }
        return false;
    }

    public ItemStack removeOneCrystal() {
        ItemStack removed = decrStackSize(0, 1);
        if (!removed.isEmpty()) markDirty();
        return removed;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.set(0, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventory);
        time = compound.getInteger("Time");
        activePrimal = compound.getByte("ActivePrimal");
        tickCounter = compound.getInteger("TickCounter");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        compound.setInteger("Time", time);
        compound.setByte("ActivePrimal", activePrimal);
        compound.setInteger("TickCounter", tickCounter);
        return compound;
    }

    @Override
    public String getName() {
        return "container.thaumicforever.aura_totem";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(getName());
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? inventory.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index != 0) return ItemStack.EMPTY;
        ItemStack result = ItemStackHelper.getAndSplit(inventory, index, count);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index != 0) return ItemStack.EMPTY;
        ItemStack result = inventory.get(0);
        inventory.set(0, ItemStack.EMPTY);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index != 0) return;
        inventory.set(0, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return player != null && world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && isValidCrystal(stack);
    }

    @Override
    public int getField(int id) {
        if (id == 0) return time;
        if (id == 1) return activePrimal;
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0) time = value;
        else if (id == 1) activePrimal = (byte) value;
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        inventory.set(0, ItemStack.EMPTY);
    }

    private static final class Structure {
        int inner;
        int outer;
        int pure;
    }

    private static final class TargetPair {
        final BlockPos inner;
        final BlockPos outer;

        TargetPair(BlockPos inner, BlockPos outer) {
            this.inner = inner;
            this.outer = outer;
        }
    }
}
