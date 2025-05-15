// package com.koteuka404.thaumicforever;

// import java.util.Random;

// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.item.ItemStack;
// import net.minecraft.nbt.NBTTagCompound;
// import net.minecraft.util.EnumFacing;
// import net.minecraft.util.NonNullList;
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.world.World;
// import thaumcraft.api.golems.EnumGolemTrait;
// import thaumcraft.api.golems.IGolemAPI;
// import thaumcraft.api.golems.seals.ISeal;
// import thaumcraft.api.golems.seals.ISealConfigArea;
// import thaumcraft.api.golems.seals.ISealConfigFilter;
// import thaumcraft.api.golems.seals.ISealConfigToggles;
// import thaumcraft.api.golems.seals.ISealEntity;
// import thaumcraft.api.golems.seals.ISealGui;
// import thaumcraft.api.golems.tasks.Task;
// import thaumcraft.common.golems.client.gui.SealBaseContainer;
// import thaumcraft.common.golems.client.gui.SealBaseGUI;

// public class SealGolemCoreFishAdvanced implements ISeal, ISealGui, ISealConfigArea, ISealConfigFilter, ISealConfigToggles {

//     public static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "items/seals/seal_golem_core_fish_advanced");
//     private static final int COOLDOWN_TIME = 300;
//     private static final int DROP_DELAY = 10;
//     private static final ResourceLocation LOOT_TABLE = new ResourceLocation("minecraft", "gameplay/fishing/fish");
    
//     private int cooldownTimer = 0;
//     private int dropDelayTimer = -1;
//     private boolean blacklistMode = false;
//     private final Random random = new Random();
//     private NonNullList<ItemStack> filter = NonNullList.withSize(9, ItemStack.EMPTY);
//     private NonNullList<Integer> sizes = NonNullList.withSize(9, 64);
//     protected SealToggle[] toggles;

//     public SealGolemCoreFishAdvanced() {
//         toggles = new SealToggle[]{new SealToggle(false, "golem.toggle.blacklist", "golem.toggle.blacklist.desc")};
//     }

//     @Override
//     public String getKey() {
//         return "thaumicforever:golem_core_fish_advanced";
//     }

//     @Override
//     public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
//         return world.getBlockState(pos).getMaterial().isLiquid();
//     }

//     @Override
//     public ResourceLocation getSealIcon() {
//         return ICON;
//     }

//     @Override
//     public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
//         return !golem.isInCombat();
//     }

//     @Override
//     public void onRemoval(World world, BlockPos pos, EnumFacing side) {}

//     @Override
//     public void onTaskSuspension(World world, Task task) {}

//     @Override
//     public EnumGolemTrait[] getRequiredTags() {
//         return new EnumGolemTrait[]{EnumGolemTrait.SCOUT};
//     }

//     @Override
//     public EnumGolemTrait[] getForbiddenTags() {
//         return new EnumGolemTrait[0];
//     }

//     @Override
//     public void writeCustomNBT(NBTTagCompound nbt) {}

//     @Override
//     public void readCustomNBT(NBTTagCompound nbt) {}

//     @Override
//     public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
//         return new SealBaseGUI(player.inventory, world, seal);
//     }

//     @Override
//     public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
//         return new SealBaseContainer(player.inventory, world, seal);
//     }

//     @Override
//     public int[] getGuiCategories() {
//         return new int[]{2, 0, 4};
//     }

//     @Override
//     public NonNullList<ItemStack> getInv() {
//         return filter;
//     }

//     @Override
//     public void setFilterSlot(int slot, ItemStack stack) {
//         filter.set(slot, stack);
//     }

//     @Override
//     public NonNullList<Integer> getSizes() {
//         return sizes;
//     }

//     @Override
//     public boolean hasStacksizeLimiters() {
//         return true;
//     }

//     @Override
//     public void setFilterSlotSize(int slot, int size) {
//         sizes.set(slot, size);
//     }

//     @Override
//     public int getFilterSlotSize(int slot) {
//         return sizes.get(slot);
//     }

//     @Override
//     public int getFilterSize() {
//         return filter.size();
//     }

//     @Override
//     public boolean isBlacklist() {
//         return blacklistMode;
//     }

//     @Override
//     public void setBlacklist(boolean value) {
//         blacklistMode = value;
//     }

//     @Override
//     public ItemStack getFilterSlot(int slot) {
//         return filter.get(slot);
//     }

//     @Override
//     public SealToggle[] getToggles() {
//         return toggles;
//     }

//     @Override
//     public void setToggle(int index, boolean value) {
//         toggles[index].setValue(value);
//         blacklistMode = value;
//     }

//     @Override
//     public void onTaskStarted(World world, IGolemAPI golem, Task task) {}
// }
