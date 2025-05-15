package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealGolemCoreFish implements ISeal, ISealGui, ISealConfigArea, ISealConfigFilter, ISealConfigToggles {

    public static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "items/seals/seal_golem_core_fish");
    private static final int COOLDOWN_TIME = 800; 
    private static final int DROP_DELAY = 10; 
    private static final double GOLDEN_FISH_CHANCE = 0.01; 

    private int cooldownTimer = 0;
    private int dropDelayTimer = -1;
    private ItemStack pendingDrop = ItemStack.EMPTY;
    private boolean blacklistMode = false;
    private final Random random = new Random();
    private NonNullList<ItemStack> filter = NonNullList.withSize(9, ItemStack.EMPTY);
    private NonNullList<Integer> sizes = NonNullList.withSize(9, 64);
    protected SealToggle[] toggles;

    public SealGolemCoreFish() {
        toggles = new SealToggle[]{ new SealToggle(false, "golem.toggle.blacklist", "golem.toggle.blacklist.desc") };
    }
    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
        if (!world.isRemote) {
            System.out.println("[SealGolemCoreFish] Печатку видалено з " + pos);
            
            TaskHandler.getTasks(world.provider.getDimension()).values().removeIf(task -> task.getPos().equals(pos));
        }
    }
    
    @Override
    public String getKey() {
        return "thaumicforever:golem_core_fish";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
        return world.getBlockState(pos).getMaterial().isLiquid() || world.getBlockState(pos).isFullBlock();
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (world.isRemote) return;

        BlockPos pos = seal.getSealPos().pos;

        if (cooldownTimer > 0) {
            cooldownTimer--;
            return;
        }

        if (dropDelayTimer > 0) {
            dropDelayTimer--;
            return;
        } else if (dropDelayTimer == 0) {
            dropDelayTimer = -1; 

            if (!pendingDrop.isEmpty()) {
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), pendingDrop));
                pendingDrop = ItemStack.EMPTY;
            }
        }

        boolean taskExists = TaskHandler.getTasks(world.provider.getDimension()).values().stream()
                .anyMatch(task -> task.getPos().equals(pos));

        if (!taskExists) {
            Task task = new Task(seal.getSealPos(), pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
        }
    }

    @Override
public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
    if (cooldownTimer > 0) return false;

    ItemStack fish = getRandomFish();

    if (golem.canCarry(fish, false)) {
        ItemStack remaining = golem.holdItem(fish); 

        if (!remaining.isEmpty()) {
            world.spawnEntity(new EntityItem(world, task.getPos().getX(), task.getPos().getY(), task.getPos().getZ(), remaining));
        }

        dropDelayTimer = DROP_DELAY;
        pendingDrop = fish;
        System.out.println("[DEBUG] Гolem тримає рибу у `getCarrying()`: " + fish.getItem().getRegistryName());
    } else {
        world.spawnEntity(new EntityItem(world, task.getPos().getX(), task.getPos().getY(), task.getPos().getZ(), fish));
    }

    cooldownTimer = COOLDOWN_TIME;
    return true;
}


    private ItemStack getRandomFish() {
        if (random.nextDouble() < GOLDEN_FISH_CHANCE) {
            return new ItemStack(ModItems.ItemGoldenFish);
        }

        List<ItemStack> fishTypes = new ArrayList<>();
        fishTypes.add(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.COD.getMetadata())); 
        fishTypes.add(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.SALMON.getMetadata())); 
        fishTypes.add(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.CLOWNFISH.getMetadata())); 
        fishTypes.add(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata())); 

        return fishTypes.get(random.nextInt(fishTypes.size()));
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public boolean isBlacklist() {
        return blacklistMode;
    }

    @Override
    public void setBlacklist(boolean value) {
        blacklistMode = value;
    }

    @Override
    public NonNullList<ItemStack> getInv() {
        return filter;
    }

    @Override
    public int getFilterSize() {
        return filter.size();
    }

    @Override
    public ItemStack getFilterSlot(int slot) {
        return filter.get(slot);
    }

    @Override
    public void setFilterSlot(int slot, ItemStack stack) {
        filter.set(slot, stack);
    }

    @Override
    public boolean hasStacksizeLimiters() {
        return true;
    }

    @Override
    public void setFilterSlotSize(int slot, int size) {
        sizes.set(slot, size);
    }

    @Override
    public int getFilterSlotSize(int slot) {
        return sizes.get(slot);
    }

    @Override
    public NonNullList<Integer> getSizes() {
        return sizes;
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {}

    @Override
    public void onTaskSuspension(World world, Task task) {}

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return !golem.isInCombat();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("blacklistMode", blacklistMode);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        blacklistMode = nbt.getBoolean("blacklistMode");
    }

    @Override
    public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }

    @Override
    public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SCOUT};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{2, 0, 4};
    }

    @Override
    public SealToggle[] getToggles() {
        return toggles;
    }

    @Override
    public void setToggle(int index, boolean value) {
        toggles[index].setValue(value);
        blacklistMode = value;
    }
}
