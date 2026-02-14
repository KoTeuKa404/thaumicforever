package com.koteuka404.thaumicforever;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class TileEntityMatteryDuplicator extends TileEntity
        implements IInventory, ITickable, IAspectContainer, IEssentiaTransport {

    private ItemStack[] inventory = new ItemStack[10];
    private String customName;

    private final InventoryCrafting craftMatrix3 = new InventoryCrafting(new ContainerDummy(), 3, 3);

    private final InventoryArcaneWorkbench craftMatrixArcane = new InventoryArcaneWorkbench(this, new ContainerDummy());

    private static final Aspect REQUIRED_ESSENTIA = AspectRegistry.MATTERYA;
    private static final int ESSENTIA_COST = 100;
    private static final int MAX_ESSENTIA_AMOUNT = 500;
    private int essentiaAmount = 0;

    private ItemStack cachedResult = ItemStack.EMPTY;
    private boolean cachedIsArcane = false;
    private float cachedEffectiveVis = 0f;
    private AspectList cachedCrystals = null;

    private UUID lastUser = null;

    public TileEntityMatteryDuplicator() {
        for (int i = 0; i < inventory.length; i++) inventory[i] = ItemStack.EMPTY;
    }

    public void setLastUser(EntityPlayer player) {
        if (player != null) this.lastUser = player.getUniqueID();
    }

    private EntityPlayer getLastUserEntity() {
        if (world == null || world.playerEntities == null || lastUser == null) return null;
        for (EntityPlayer p : world.playerEntities) {
            if (p != null && lastUser.equals(p.getUniqueID())) return p;
        }
        return null;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        fillWithEssentia();
        updateGuiFieldsServer();

        ItemStack oldOut = inventory[9] == null ? ItemStack.EMPTY : inventory[9];

        CraftPreview preview = computePreview();
        cachedResult = preview.result;
        cachedIsArcane = preview.isArcane;
        cachedEffectiveVis = preview.effectiveVis;
        cachedCrystals = preview.crystals;

        ItemStack newOut = ItemStack.EMPTY;
        if (!cachedResult.isEmpty() && hasEnoughEssentia()) {
            if (!cachedIsArcane || (hasEnoughVis(cachedEffectiveVis) && hasEnoughPrimalForCrystals(cachedCrystals))) {
                newOut = cachedResult.copy();
            }
        }

        if (!ItemStack.areItemStacksEqual(oldOut, newOut)) {
            inventory[9] = newOut;
            markForUpdate();
        }
    }

    private static final class CraftPreview {
        final ItemStack result;
        final boolean isArcane;
        final float effectiveVis;
        final AspectList crystals;

        CraftPreview(ItemStack r, boolean a, float v, AspectList c) {
            result = r;
            isArcane = a;
            effectiveVis = v;
            crystals = c;
        }
    }

    private CraftPreview computePreview() {
        EntityPlayer player = getLastUserEntity();

        for (int i = 0; i < 9; i++) {
            ItemStack s = inventory[i];
            if (s == null) s = ItemStack.EMPTY;
            craftMatrix3.setInventorySlotContents(i, s);
            craftMatrixArcane.setInventorySlotContents(i, s);
        }

        for (int i = 9; i < 15; i++) {
            craftMatrixArcane.setInventorySlotContents(i, ItemStack.EMPTY);
        }

        if (player != null) {
            fillVirtualCrystalsFromPrimalAura(craftMatrixArcane);

            IArcaneRecipe ar = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMatrixArcane, player);
            if (ar != null) {
                String research = ar.getResearch();
                if (research != null && !research.isEmpty()) {
                    if (ThaumcraftCapabilities.getKnowledge(player) == null) {
                        return new CraftPreview(ItemStack.EMPTY, true, 0f, null);
                    }
                    if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
                        return new CraftPreview(ItemStack.EMPTY, true, 0f, null);
                    }
                }

                int visCost = Math.max(0, ar.getVis());

                float disc = 0f;
                try { disc = CasterManager.getTotalVisDiscount(player); }
                catch (Throwable t) { disc = 0f; }
                if (disc < 0f) disc = 0f;
                if (disc > 0.99f) disc = 0.99f;

                float effectiveVis = visCost * (1.0f - disc);
                AspectList crystals = ar.getCrystals();

                if (!hasEnoughVis(effectiveVis)) {
                    return new CraftPreview(ItemStack.EMPTY, true, effectiveVis, crystals);
                }
                if (!hasEnoughPrimalForCrystals(crystals)) {
                    return new CraftPreview(ItemStack.EMPTY, true, effectiveVis, crystals);
                }

                ItemStack out = ar.getCraftingResult(craftMatrixArcane);
                if (out == null) out = ItemStack.EMPTY;
                return new CraftPreview(out, true, effectiveVis, crystals);
            }
        }

        IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix3, world);
        if (recipe != null && recipe.matches(craftMatrix3, world)) {
            ItemStack out = recipe.getCraftingResult(craftMatrix3);
            if (out == null) out = ItemStack.EMPTY;
            return new CraftPreview(out, false, 0f, null);
        }

        return new CraftPreview(ItemStack.EMPTY, false, 0f, null);
    }

    private void fillVirtualCrystalsFromPrimalAura(InventoryCrafting arcaneMat) {
        Aspect[] order = new Aspect[] {
                Aspect.FIRE, Aspect.EARTH, Aspect.AIR, Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY
        };

        for (int i = 0; i < 6; i++) {
            Aspect a = order[i];
            float available = getPrimalAmountForAspect(a, pos);

            int count = (int) Math.floor(available);
            if (count > 64) count = 64;
            if (count < 0) count = 0;

            ItemStack crystal = ItemStack.EMPTY;
            if (count > 0) crystal = ThaumcraftApiHelper.makeCrystal(a, count);

            arcaneMat.setInventorySlotContents(9 + i, crystal);
        }
    }

    private boolean hasEnoughVis(float effectiveVis) {
        if (effectiveVis <= 0.0001f) return true;
        float have = AuraHelper.getVis(world, pos);
        return have + 0.0001f >= effectiveVis;
    }

    private boolean drainVis(float effectiveVis) {
        if (effectiveVis <= 0.0001f) return true;

        float sim = AuraHelper.drainVis(world, pos, effectiveVis, true);
        if (sim + 0.0001f < effectiveVis) return false;

        AuraHelper.drainVis(world, pos, effectiveVis, false);
        return true;
    }

    private boolean hasEnoughPrimalForCrystals(AspectList crystals) {
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

    private boolean drainPrimalForCrystals(AspectList crystals) {
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

            float need = (float) needInt;
            PrimalAuraHandler.drain(world, pos, p, need, false);
        }

        return true;
    }

    private static Aspect[] getAllPrimalAspects() {
        return new Aspect[] { Aspect.FIRE, Aspect.EARTH, Aspect.AIR, Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY };
    }

    private float getPrimalAmountForAspect(Aspect a, BlockPos at) {
        Primal p = primalFromAspect(a);
        if (p == null) return 0f;
        return PrimalAuraHandler.get(world, at, p);
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

    public void onCraftTaken(EntityPlayer player) {
        if (world == null || world.isRemote) return;
        if (player == null) return;

        if (!hasEnoughEssentia()) return;

        setLastUser(player);
        CraftPreview preview = computePreview();
        if (preview.result.isEmpty()) return;

        if (preview.isArcane) {
            if (!hasEnoughVis(preview.effectiveVis)) return;
            if (!hasEnoughPrimalForCrystals(preview.crystals)) return;
        }

        consumeEssentia();

        if (preview.isArcane) {
            if (!drainVis(preview.effectiveVis)) return;
            if (!drainPrimalForCrystals(preview.crystals)) return;
        }

        markForUpdate();
    }

    public boolean hasEnoughEssentia() {
        return essentiaAmount >= ESSENTIA_COST;
    }

    public void consumeEssentia() {
        if (essentiaAmount >= ESSENTIA_COST) {
            essentiaAmount -= ESSENTIA_COST;
            if (essentiaAmount < 0) essentiaAmount = 0;
            markForUpdate();
        }
    }

    private void fillWithEssentia() {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (!(te instanceof IEssentiaTransport)) continue;

            IEssentiaTransport tr = (IEssentiaTransport) te;

            if (!tr.canOutputTo(facing.getOpposite())) continue;
            if (essentiaAmount >= MAX_ESSENTIA_AMOUNT) continue;

            Aspect type = tr.getEssentiaType(facing.getOpposite());
            if (type == null || type != REQUIRED_ESSENTIA) continue;

            int taken = tr.takeEssentia(type, 1, facing.getOpposite());
            if (taken > 0) {
                essentiaAmount += taken;
                if (essentiaAmount > MAX_ESSENTIA_AMOUNT) essentiaAmount = MAX_ESSENTIA_AMOUNT;
                markForUpdate();
            }
        }
    }

    // ==============================
    // Sync helpers
    // ==============================

    private void markForUpdate() {
        markDirty();
        if (world != null) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setInteger("EssentiaAmount", essentiaAmount);
        if (lastUser != null) tag.setString("LastUser", lastUser.toString());
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        essentiaAmount = tag.getInteger("EssentiaAmount");
        if (tag.hasKey("LastUser")) {
            try { lastUser = UUID.fromString(tag.getString("LastUser")); }
            catch (Exception ignored) { lastUser = null; }
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    // ==============================
    // NBT
    // ==============================

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("EssentiaAmount", essentiaAmount);
        if (lastUser != null) compound.setString("LastUser", lastUser.toString());

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack s = inventory[i];
            if (s != null && !s.isEmpty()) {
                NBTTagCompound it = new NBTTagCompound();
                it.setByte("Slot", (byte) i);
                s.writeToNBT(it);
                list.appendTag(it);
            }
        }
        compound.setTag("Items", list);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        essentiaAmount = compound.getInteger("EssentiaAmount");

        if (compound.hasKey("LastUser")) {
            try { lastUser = UUID.fromString(compound.getString("LastUser")); }
            catch (Exception ignored) { lastUser = null; }
        }

        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < inventory.length; i++) inventory[i] = ItemStack.EMPTY;

        NBTTagList list = compound.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound it = list.getCompoundTagAt(i);
            int slot = it.getByte("Slot") & 255;
            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = new ItemStack(it);
            }
        }
    }

    // ==============================
    // IAspectContainer
    // ==============================

    @Override
    public AspectList getAspects() {
        AspectList al = new AspectList();
        al.add(REQUIRED_ESSENTIA, essentiaAmount);
        return al;
    }

    @Override public void setAspects(AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect == REQUIRED_ESSENTIA;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == REQUIRED_ESSENTIA && amount > 0) {
            int add = Math.min(amount, MAX_ESSENTIA_AMOUNT - essentiaAmount);
            if (add > 0) {
                essentiaAmount += add;
                markForUpdate();
            }
            return add;
        }
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (aspect == REQUIRED_ESSENTIA && amount > 0 && essentiaAmount >= amount) {
            essentiaAmount -= amount;
            markForUpdate();
            return true;
        }
        return false;
    }

    @Override public boolean doesContainerContainAmount(Aspect aspect, int amount) { return aspect == REQUIRED_ESSENTIA && essentiaAmount >= amount; }
    @Override public int containerContains(Aspect aspect) { return essentiaAmount; }
    @Override public boolean doesContainerContain(AspectList ot) { return ot.getAmount(REQUIRED_ESSENTIA) <= essentiaAmount; }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        if (doesContainerContain(ot)) {
            takeFromContainer(REQUIRED_ESSENTIA, ot.getAmount(REQUIRED_ESSENTIA));
            return true;
        }
        return false;
    }

    // ==============================
    // IEssentiaTransport
    // ==============================

    @Override public boolean isConnectable(EnumFacing face) { return true; }
    @Override public boolean canInputFrom(EnumFacing face) { return true; }
    @Override public boolean canOutputTo(EnumFacing face) { return false; }

    @Override public int getMinimumSuction() { return 128; }
    @Override public int getSuctionAmount(EnumFacing face) { return essentiaAmount < MAX_ESSENTIA_AMOUNT ? 128 : 0; }
    @Override public Aspect getSuctionType(EnumFacing face) { return REQUIRED_ESSENTIA; }

    @Override public int addEssentia(Aspect aspect, int amount, EnumFacing face) { return addToContainer(aspect, amount); }
    @Override public int takeEssentia(Aspect aspect, int amount, EnumFacing face) { return takeFromContainer(aspect, amount) ? amount : 0; }
    @Override public int getEssentiaAmount(EnumFacing face) { return essentiaAmount; }
    @Override public Aspect getEssentiaType(EnumFacing face) { return REQUIRED_ESSENTIA; }
    @Override public void setSuction(Aspect aspect, int amount) {}

    // ==============================
    // IInventory
    // ==============================

    @Override public boolean hasCustomName() { return customName != null && !customName.isEmpty(); }
    @Override public String getName() { return hasCustomName() ? customName : "container.mattery_duplicator"; }
    @Override public ITextComponent getDisplayName() { return new TextComponentString(getName()); }
    public void setCustomName(String name) { customName = name; }

    @Override public int getSizeInventory() { return inventory.length; }
    @Override public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index >= 0 && index <= 8;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(pos) == this &&
                player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0 || index >= inventory.length) return ItemStack.EMPTY;
        ItemStack s = inventory[index];
        return s == null ? ItemStack.EMPTY : s;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < 0 || index >= inventory.length) return ItemStack.EMPTY;
        ItemStack s = getStackInSlot(index);
        if (s.isEmpty()) return ItemStack.EMPTY;

        ItemStack ret;
        if (s.getCount() <= count) {
            ret = s;
            inventory[index] = ItemStack.EMPTY;
        } else {
            ret = s.splitStack(count);
            if (s.getCount() <= 0) inventory[index] = ItemStack.EMPTY;
        }

        markDirty();
        return ret;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index < 0 || index >= inventory.length) return ItemStack.EMPTY;
        ItemStack s = getStackInSlot(index);
        inventory[index] = ItemStack.EMPTY;
        markDirty();
        return s;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= inventory.length) return;
        inventory[index] = (stack == null) ? ItemStack.EMPTY : stack;
        markDirty();
    }

    @Override public void openInventory(EntityPlayer player) {}
    @Override public void closeInventory(EntityPlayer player) {}
    private static final int F_ESSENTIA = 0;
    private static final int F_VIS_X100 = 1;
    private static final int F_IGNIS_X100 = 2;
    private static final int F_TERRA_X100 = 3;
    private static final int F_AER_X100 = 4;
    private static final int F_AQUA_X100 = 5;
    private static final int F_ORDO_X100 = 6;
    private static final int F_PERDITIO_X100 = 7;

    private final int[] fields = new int[8];

    private void updateGuiFieldsServer() {
        fields[F_ESSENTIA] = essentiaAmount;

        float vis = 0f;
        try { vis = AuraHelper.getVis(world, pos); } catch (Throwable t) { vis = 0f; }
        fields[F_VIS_X100] = clampInt(Math.round(vis * 100f));

        fields[F_IGNIS_X100] = clampInt(Math.round(PrimalAuraHandler.get(world, pos, Primal.IGNIS) * 100f));
        fields[F_TERRA_X100] = clampInt(Math.round(PrimalAuraHandler.get(world, pos, Primal.TERRA) * 100f));
        fields[F_AER_X100] = clampInt(Math.round(PrimalAuraHandler.get(world, pos, Primal.AER) * 100f));
        fields[F_AQUA_X100] = clampInt(Math.round(PrimalAuraHandler.get(world, pos, Primal.AQUA) * 100f));
        fields[F_ORDO_X100] = clampInt(Math.round(PrimalAuraHandler.get(world, pos, Primal.ORDO) * 100f));
        fields[F_PERDITIO_X100] = clampInt(Math.round(PrimalAuraHandler.get(world, pos, Primal.PERDITIO) * 100f));
    }

    private static int clampInt(int v) {
        if (v < 0) return 0;
        if (v > 2_000_000_000) return 2_000_000_000;
        return v;
    }

    // ======= IInventory fields =======
    @Override
    public int getField(int id) {
        if (id < 0 || id >= fields.length) return 0;
        return fields[id];
    }

    @Override
    public void setField(int id, int value) {
        if (id < 0 || id >= fields.length) return;
        fields[id] = value;
    }

    @Override
    public int getFieldCount() {
        return fields.length;
    }


        @Override
        public void clear() {
            for (int i = 0; i < inventory.length; i++) inventory[i] = ItemStack.EMPTY;
            markDirty();
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack s : inventory) {
                if (s != null && !s.isEmpty()) return false;
            }
            return true;
        }
    }
