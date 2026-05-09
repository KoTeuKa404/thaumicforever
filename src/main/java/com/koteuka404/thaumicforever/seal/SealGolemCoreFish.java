package com.koteuka404.thaumicforever.seal;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealGolemCoreFish implements ISeal, ISealGui, ISealConfigArea {

    public static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "items/seals/seal_golem_core_fish");
    protected static final int COOLDOWN_TIME = 600;
    protected static final Map<String, Integer> COOLDOWNS = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

    @Override
    public String getKey() {
        return "thaumicforever:golem_core_fish";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
        return world.getBlockState(pos).getMaterial().isLiquid()
                || world.getBlockState(pos.offset(side)).getMaterial().isLiquid()
                || world.getBlockState(pos.offset(side.getOpposite())).getMaterial().isLiquid();
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (world.isRemote || seal == null || seal.getSealPos() == null) return;

        String key = makeKey(world, seal);
        int cd = COOLDOWNS.getOrDefault(key, 0);
        if (cd > 0) {
            COOLDOWNS.put(key, cd - 1);
            return;
        }

        boolean taskExists = TaskHandler.getTasks(world.provider.getDimension()).values().stream()
                .anyMatch(t -> t.getSealPos() != null && t.getSealPos().equals(seal.getSealPos()));

        if (!taskExists) {
            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
        }
    }

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        if (world.isRemote || task == null || task.getSealPos() == null) return false;

        String key = makeKey(world, task);
        if (COOLDOWNS.getOrDefault(key, 0) > 0) return false;

        ItemStack loot = getSimpleFishingLoot(world, task.getPos());
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
        return true;
    }

    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
        if (world.isRemote) return;
        String prefix = world.provider.getDimension() + ":" + pos.toLong() + ":" + side.ordinal();
        COOLDOWNS.remove(prefix);
        TaskHandler.getTasks(world.provider.getDimension()).values()
                .removeIf(t -> t.getSealPos() != null && t.getSealPos().pos.equals(pos) && t.getSealPos().face == side);
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return !golem.isInCombat();
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SCOUT, EnumGolemTrait.DEFT};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {}

    @Override
    public void onTaskSuspension(World world, Task task) {}

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {}

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {}

    @Override
    public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }

    @Override
    public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{2, 0, 4};
    }

    protected ItemStack getSimpleFishingLoot(World world, BlockPos pos) {
        return generateLootFromTable(world, pos, LootTableList.GAMEPLAY_FISHING);
    }

    protected ItemStack generateLootFromTable(World world, BlockPos pos, ResourceLocation tableId) {
        if (!(world instanceof WorldServer)) {
            return ItemStack.EMPTY;
        }

        LootTable table = ((WorldServer) world).getLootTableManager().getLootTableFromLocation(tableId);
        LootContext context = new LootContext.Builder((WorldServer) world)
                .withLuck(0.0F)
                .build();

        for (ItemStack stack : table.generateLootForPools(RANDOM, context)) {
            if (!stack.isEmpty()) {
                return stack.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    protected static String makeKey(World world, ISealEntity seal) {
        return world.provider.getDimension() + ":" + seal.getSealPos().pos.toLong() + ":" + seal.getSealPos().face.ordinal();
    }

    protected static String makeKey(World world, Task task) {
        return world.provider.getDimension() + ":" + task.getSealPos().pos.toLong() + ":" + task.getSealPos().face.ordinal();
    }
}
