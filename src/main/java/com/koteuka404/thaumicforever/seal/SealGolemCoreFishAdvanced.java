package com.koteuka404.thaumicforever.seal;

import com.koteuka404.thaumicforever.registry.ModItems;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import com.koteuka404.thaumicforever.item.ItemGoldenFish;

public class SealGolemCoreFishAdvanced extends SealGolemCoreFish implements ISealConfigToggles, ISealConfigFilter {

    public static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "items/seals/seal_golem_core_fish_advanced");
    private static final int REDSTONE_PULSE_TICKS = 8;
    private static final int FILTER_SLOTS = 9;
    private static final Map<String, Integer> REDSTONE_PULSE = new ConcurrentHashMap<>();

    private final SealToggle[] toggles = new SealToggle[]{
            new SealToggle(true, "thaumicforever.golem.toggle.catch_fish", "Catch Fish"),
            new SealToggle(true, "thaumicforever.golem.toggle.catch_junk", "Catch Junk"),
            new SealToggle(true, "thaumicforever.golem.toggle.catch_treasure", "Catch Treasure"),
            new SealToggle(false, "thaumicforever.golem.toggle.redstone_signal", "Redstone Signal")
    };
    private final NonNullList<ItemStack> filterInv = NonNullList.withSize(FILTER_SLOTS, ItemStack.EMPTY);
    private final NonNullList<Integer> filterSizes = NonNullList.withSize(FILTER_SLOTS, 0);
    private boolean blacklist = false;

    @Override
    public String getKey() {
        return "thaumicforever:golem_core_fish_advanced";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (!world.isRemote) {
            tickRedstonePulse(world, seal);
        }
        super.tickSeal(world, seal);
    }

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        if (world.isRemote || task == null || task.getSealPos() == null) return false;

        String key = makeKey(world, task);
        if (COOLDOWNS.getOrDefault(key, 0) > 0) return false;

        ItemStack loot = generateFilteredLoot(world, task.getPos());
        if (loot.isEmpty()) return false;

        if (golem.canCarry(loot, false)) {
            ItemStack leftover = golem.holdItem(loot.copy());
            if (!leftover.isEmpty()) {
                world.spawnEntity(new EntityItem(world, task.getPos().getX() + 0.5, task.getPos().getY() + 0.5, task.getPos().getZ() + 0.5, leftover));
            }
        } else {
            world.spawnEntity(new EntityItem(world, task.getPos().getX() + 0.5, task.getPos().getY() + 0.5, task.getPos().getZ() + 0.5, loot.copy()));
        }

        COOLDOWNS.put(key, COOLDOWN_TIME);
        boolean done = true;
        if (done && toggles[3].getValue() && task != null && task.getSealPos() != null) {
            REDSTONE_PULSE.put(makeKey(world, task), REDSTONE_PULSE_TICKS);
            notifyNeighbors(world, task.getSealPos().pos);
        }
        return done;
    }

    @Override
    protected ItemStack getSimpleFishingLoot(World world, BlockPos pos) {
        boolean fish = toggles[0].getValue();
        boolean junk = toggles[1].getValue();
        boolean treasure = toggles[2].getValue();

        // Small rare bonus catch for advanced seal.
        if (fish && world.rand.nextFloat() < 0.01F) {
            return new ItemStack(ModItems.ItemGoldenFish);
        }

        int enabled = (fish ? 1 : 0) + (junk ? 1 : 0) + (treasure ? 1 : 0);
        if (enabled <= 0) {
            return ItemStack.EMPTY;
        }

        int pick = world.rand.nextInt(enabled);
        if (fish) {
            if (pick == 0) return generateLootFromTable(world, pos, LootTableList.GAMEPLAY_FISHING_FISH);
            pick--;
        }
        if (junk) {
            if (pick == 0) return generateLootFromTable(world, pos, LootTableList.GAMEPLAY_FISHING_JUNK);
            pick--;
        }
        return generateLootFromTable(world, pos, LootTableList.GAMEPLAY_FISHING_TREASURE);
    }

    @Override
    public SealToggle[] getToggles() {
        return toggles;
    }

    @Override
    public void setToggle(int indx, boolean value) {
        if (indx >= 0 && indx < toggles.length) {
            toggles[indx].setValue(value);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("catchFish", toggles[0].getValue());
        nbt.setBoolean("catchJunk", toggles[1].getValue());
        nbt.setBoolean("catchTreasure", toggles[2].getValue());
        nbt.setBoolean("redstoneSignal", toggles[3].getValue());
        nbt.setBoolean("blacklist", blacklist);

        NBTTagList fl = new NBTTagList();
        for (int i = 0; i < filterInv.size(); i++) {
            ItemStack s = filterInv.get(i);
            if (!s.isEmpty()) {
                NBTTagCompound tc = new NBTTagCompound();
                tc.setByte("slot", (byte) i);
                s.writeToNBT(tc);
                fl.appendTag(tc);
            }
        }
        nbt.setTag("filter", fl);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        // Backward compatibility with old "onlyFish" toggle.
        if (nbt.hasKey("onlyFish")) {
            boolean onlyFish = nbt.getBoolean("onlyFish");
            toggles[0].setValue(true);
            toggles[1].setValue(!onlyFish);
            toggles[2].setValue(!onlyFish);
        } else {
            toggles[0].setValue(!nbt.hasKey("catchFish") || nbt.getBoolean("catchFish"));
            toggles[1].setValue(!nbt.hasKey("catchJunk") || nbt.getBoolean("catchJunk"));
            toggles[2].setValue(!nbt.hasKey("catchTreasure") || nbt.getBoolean("catchTreasure"));
        }
        toggles[3].setValue(nbt.getBoolean("redstoneSignal"));
        blacklist = nbt.getBoolean("blacklist");

        for (int i = 0; i < filterInv.size(); i++) {
            filterInv.set(i, ItemStack.EMPTY);
        }
        NBTTagList fl = nbt.getTagList("filter", 10);
        for (int i = 0; i < fl.tagCount(); i++) {
            NBTTagCompound tc = fl.getCompoundTagAt(i);
            int slot = tc.getByte("slot") & 255;
            if (slot >= 0 && slot < filterInv.size()) {
                filterInv.set(slot, new ItemStack(tc));
            }
        }
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{2, 1, 0, 3, 4};
    }

    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
        if (!world.isRemote) {
            REDSTONE_PULSE.remove(world.provider.getDimension() + ":" + pos.toLong() + ":" + side.ordinal());
        }
        super.onRemoval(world, pos, side);
    }

    private void tickRedstonePulse(World world, ISealEntity seal) {
        if (seal == null || seal.getSealPos() == null) return;
        String key = makeKey(world, seal);
        int ticks = REDSTONE_PULSE.getOrDefault(key, 0);
        if (ticks <= 0) return;

        notifyNeighbors(world, seal.getSealPos().pos);
        if (ticks == 1) {
            REDSTONE_PULSE.remove(key);
        } else {
            REDSTONE_PULSE.put(key, ticks - 1);
        }
    }

    private void notifyNeighbors(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        world.notifyNeighborsOfStateChange(pos, block, false);
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos p = pos.offset(facing);
            world.notifyNeighborsOfStateChange(p, world.getBlockState(p).getBlock(), false);
        }
    }

    private ItemStack generateFilteredLoot(World world, BlockPos pos) {
        for (int i = 0; i < 32; i++) {
            ItemStack roll = getSimpleFishingLoot(world, pos);
            if (!roll.isEmpty() && passesFilter(roll)) {
                return roll;
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean passesFilter(ItemStack loot) {
        boolean hasFilter = false;
        boolean matched = false;

        for (ItemStack f : filterInv) {
            if (!f.isEmpty()) {
                hasFilter = true;
                if (ItemStack.areItemsEqual(f, loot)) {
                    matched = true;
                    break;
                }
            }
        }

        if (!hasFilter) return true;
        return blacklist ? !matched : matched;
    }

    @Override
    public NonNullList<ItemStack> getInv() {
        return filterInv;
    }

    @Override
    public NonNullList<Integer> getSizes() {
        return filterSizes;
    }

    @Override
    public int getFilterSize() {
        return FILTER_SLOTS;
    }

    @Override
    public ItemStack getFilterSlot(int i) {
        return i >= 0 && i < filterInv.size() ? filterInv.get(i) : ItemStack.EMPTY;
    }

    @Override
    public int getFilterSlotSize(int i) {
        return 0;
    }

    @Override
    public void setFilterSlot(int i, ItemStack stack) {
        if (i >= 0 && i < filterInv.size()) {
            filterInv.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
    }

    @Override
    public void setFilterSlotSize(int i, int size) {
    }

    @Override
    public boolean isBlacklist() {
        return blacklist;
    }

    @Override
    public void setBlacklist(boolean black) {
        blacklist = black;
    }

    @Override
    public boolean hasStacksizeLimiters() {
        return false;
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SCOUT, EnumGolemTrait.DEFT, EnumGolemTrait.SMART};
    }
}
