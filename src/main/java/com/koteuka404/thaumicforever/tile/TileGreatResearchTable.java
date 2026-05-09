package com.koteuka404.thaumicforever.tile;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.common.lib.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.lib.research.ResearchNoteData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.items.IScribeTools;
import com.koteuka404.thaumicforever.block.GreatResearchTableBlock;

public class TileGreatResearchTable extends TileEntity implements IInventory, ITickable {
    private static final int SLOT_INK = 0;
    private static final int SLOT_NOTE = 1;
    private static final int RESOURCE_STEP_COUNT = 20;
    private static final int XP_COST_PER_STEP = 1;
    private static final int ESSENTIA_COST_PER_STEP = 1;
    private static final int PLAYER_XP_RANGE = 8;
    private static final int MIND_ESSENTIA_RANGE = 8;
    public static final int BASE_SOLVE_TICKS = 20 * 60 * 10;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private int progress;
    private int progressRemainder;
    private int nextInkProgress = BASE_SOLVE_TICKS / RESOURCE_STEP_COUNT;
    private int efficiencyPercent;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        ItemStack note = inventory.get(SLOT_NOTE);
        if (!canWork(note) || !canConsumeInk()) {
            resetProgress();
            return;
        }

        TileBigJar jar = findLinkedJar();
        if (jar == null) {
            resetProgress();
            return;
        }

        int brains = Math.min(TileBigJar.MAX_BRAINS, jar.getBrainCount());
        if (brains <= 0) {
            resetProgress();
            return;
        }

        int efficiency = getEffectiveEfficiencyPercent(brains, note);
        setEfficiencyPercent(efficiency);

        int nextProgress = progress + efficiency / 100;
        int nextRemainder = progressRemainder + efficiency % 100;
        if (nextRemainder >= 100) {
            nextProgress += nextRemainder / 100;
            nextRemainder %= 100;
        }

        if (nextProgress >= nextInkProgress && !consumeStepResources(jar)) {
            if (world.getTotalWorldTime() % 20 == 0) sync();
            return;
        }

        progress = nextProgress;
        progressRemainder = nextRemainder;

        if (progress < BASE_SOLVE_TICKS) {
            if (world.getTotalWorldTime() % 20 == 0) sync();
            return;
        }

        progress = 0;
        progressRemainder = 0;
        nextInkProgress = BASE_SOLVE_TICKS / RESOURCE_STEP_COUNT;
        completeResearchNote(note);
        markDirty();
        sync();
    }

    private boolean canWork(ItemStack note) {
        if (note.isEmpty() || !(note.getItem() instanceof ItemResearchNote) || note.getItemDamage() >= 64) return false;
        ResearchNoteData data = OldResearchManager.getData(note);
        return data != null && !data.isComplete();
    }

    private boolean canConsumeInk() {
        ItemStack ink = inventory.get(SLOT_INK);
        return !ink.isEmpty() && ink.getItem() instanceof IScribeTools && ink.getItemDamage() < ink.getMaxDamage();
    }

    private void consumeInk() {
        ItemStack ink = inventory.get(SLOT_INK);
        if (ink.isEmpty() || !(ink.getItem() instanceof IScribeTools)) return;
        ink.setItemDamage(ink.getItemDamage() + 1);
    }

    private void completeResearchNote(ItemStack note) {
        ResearchNoteData data = OldResearchManager.getData(note);
        if (data == null) return;
        data.complete = true;
        OldResearchManager.updateData(note, data);
        note.setItemDamage(64);
    }

    private void resetProgress() {
        int firstInkProgress = BASE_SOLVE_TICKS / RESOURCE_STEP_COUNT;
        if (progress == 0 && progressRemainder == 0 && nextInkProgress == firstInkProgress && efficiencyPercent == 0) return;
        progress = 0;
        progressRemainder = 0;
        nextInkProgress = firstInkProgress;
        efficiencyPercent = 0;
        markDirty();
        sync();
    }

    private boolean consumeStepResources(TileBigJar jar) {
        EntityPlayer player = findNearestPlayerWithExperience();
        if (player == null) return false;
        if (!drainMindEssentiaFromJar(jar) && !drainMindEssentiaNearby()) return false;

        consumePlayerExperience(player, XP_COST_PER_STEP);
        consumeInk();
        nextInkProgress += BASE_SOLVE_TICKS / RESOURCE_STEP_COUNT;
        markDirty();
        return true;
    }

    private boolean drainMindEssentiaFromJar(TileBigJar jar) {
        return jar != null && jar.takeEssentia(Aspect.MIND, ESSENTIA_COST_PER_STEP, EnumFacing.UP) >= ESSENTIA_COST_PER_STEP;
    }

    private EntityPlayer findNearestPlayerWithExperience() {
        AxisAlignedBB range = new AxisAlignedBB(pos).grow(PLAYER_XP_RANGE);
        EntityPlayer best = null;
        double bestDistance = Double.MAX_VALUE;

        for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, range)) {
            if (player == null || player.isSpectator()) continue;
            if (!player.capabilities.isCreativeMode && player.experienceTotal < XP_COST_PER_STEP) continue;

            double distance = player.getDistanceSq(pos);
            if (distance < bestDistance) {
                best = player;
                bestDistance = distance;
            }
        }

        return best;
    }

    private void consumePlayerExperience(EntityPlayer player, int amount) {
        if (player == null || player.capabilities.isCreativeMode || amount <= 0) return;
        player.addExperience(-amount);
    }

    private boolean drainMindEssentiaNearby() {
        int rangeSq = MIND_ESSENTIA_RANGE * MIND_ESSENTIA_RANGE;
        for (int dx = -MIND_ESSENTIA_RANGE; dx <= MIND_ESSENTIA_RANGE; dx++) {
            for (int dy = -MIND_ESSENTIA_RANGE; dy <= MIND_ESSENTIA_RANGE; dy++) {
                for (int dz = -MIND_ESSENTIA_RANGE; dz <= MIND_ESSENTIA_RANGE; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (dx * dx + dy * dy + dz * dz > rangeSq) continue;
                    if (drainMindEssentiaAt(pos.add(dx, dy, dz))) return true;
                }
            }
        }
        return false;
    }

    private boolean drainMindEssentiaAt(BlockPos targetPos) {
        TileEntity te = world.getTileEntity(targetPos);
        if (te instanceof IEssentiaTransport) {
            IEssentiaTransport transport = (IEssentiaTransport) te;
            for (EnumFacing face : EnumFacing.values()) {
                if (!transport.canOutputTo(face)) continue;
                if (!Aspect.MIND.equals(transport.getEssentiaType(face))) continue;
                int taken = transport.takeEssentia(Aspect.MIND, ESSENTIA_COST_PER_STEP, face);
                if (taken >= ESSENTIA_COST_PER_STEP) return true;
            }
        }

        if (te instanceof IAspectContainer) {
            IAspectContainer container = (IAspectContainer) te;
            if (container.doesContainerContainAmount(Aspect.MIND, ESSENTIA_COST_PER_STEP)) {
                return container.takeFromContainer(Aspect.MIND, ESSENTIA_COST_PER_STEP);
            }
        }
        return false;
    }

    private int getEffectiveEfficiencyPercent(int brains, ItemStack note) {
        ResearchNoteData data = OldResearchManager.getData(note);
        int aspectCount = data != null && data.aspects != null ? data.aspects.size() : 0;
        return Math.max(20, brains * 100 - aspectCount * 20);
    }

    private TileBigJar findLinkedJar() {
        EnumFacing facing = EnumFacing.NORTH;
        if (world.getBlockState(pos).getBlock() instanceof GreatResearchTableBlock) {
            facing = world.getBlockState(pos).getValue(GreatResearchTableBlock.FACING);
        }

        EnumFacing side = facing.rotateY();
        for (int forward = 1; forward <= 3; forward++) {
            for (int sideways = -1; sideways <= 2; sideways++) {
                for (int dy = -1; dy <= 1; dy++) {
                    BlockPos candidate = pos.offset(facing, forward).offset(side, sideways).add(0, dy, 0);
                    TileBigJar jar = getJarAt(candidate);
                    if (jar != null) return jar;
                }
            }
        }

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > 4) continue;
                    TileBigJar jar = getJarAt(pos.add(dx, dy, dz));
                    if (jar != null) return jar;
                }
            }
        }
        return null;
    }

    private TileBigJar getJarAt(BlockPos candidate) {
        if (world.getBlockState(candidate).getBlock() == ModBlocks.BIG_JAR) {
            TileEntity te = world.getTileEntity(candidate);
            return te instanceof TileBigJar ? (TileBigJar) te : null;
        }
        if (world.getBlockState(candidate).getBlock() == ModBlocks.BIG_JAR_PART) {
            TileEntity te = world.getTileEntity(candidate);
            if (te instanceof TileBigJarPart) {
                BlockPos master = ((TileBigJarPart) te).getMaster();
                if (master != null && world.getBlockState(master).getBlock() == ModBlocks.BIG_JAR) {
                    TileEntity masterTe = world.getTileEntity(master);
                    return masterTe instanceof TileBigJar ? (TileBigJar) masterTe : null;
                }
            }
        }
        return null;
    }

    public int getProgress() {
        return progress;
    }

    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < inventory.size() ? inventory.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(inventory, index, count);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(inventory, index);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= inventory.size()) return;
        inventory.set(index, stack);
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
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == SLOT_INK) return !stack.isEmpty() && stack.getItem() instanceof IScribeTools;
        if (index == SLOT_NOTE) return !stack.isEmpty() && stack.getItem() instanceof ItemResearchNote;
        return false;
    }

    @Override
    public int getField(int id) {
        if (id == 0) return progress;
        if (id == 1) return efficiencyPercent;
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0) {
            progress = value;
        } else if (id == 1) {
            efficiencyPercent = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public String getName() {
        return "tile.thaumicforever.great_research_table.name";
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
    public void clear() {
        inventory.clear();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isRemote) sync();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        compound.setInteger("Progress", progress);
        compound.setInteger("ProgressRemainder", progressRemainder);
        compound.setInteger("NextInkProgress", nextInkProgress);
        compound.setInteger("EfficiencyPercent", efficiencyPercent);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.clear();
        ItemStackHelper.loadAllItems(compound, inventory);
        progress = compound.getInteger("Progress");
        progressRemainder = compound.getInteger("ProgressRemainder");
        int firstResourceProgress = BASE_SOLVE_TICKS / RESOURCE_STEP_COUNT;
        nextInkProgress = compound.hasKey("NextInkProgress") ? compound.getInteger("NextInkProgress") : firstResourceProgress;
        if (nextInkProgress <= 0) nextInkProgress = firstResourceProgress;
        efficiencyPercent = compound.getInteger("EfficiencyPercent");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public void sync() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    private void setEfficiencyPercent(int value) {
        if (efficiencyPercent == value) return;
        efficiencyPercent = value;
        markDirty();
    }
}
