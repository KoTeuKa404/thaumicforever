// package com.koteuka404.thaumicforever;

// import net.minecraft.block.Block;
// import net.minecraft.init.Blocks;
// import net.minecraft.item.Item;
// import net.minecraft.item.ItemStack;
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.world.World;
// import thaumcraft.api.golems.EnumGolemTrait;
// import thaumcraft.api.golems.IGolemAPI;
// import thaumcraft.api.golems.seals.ISealConfigToggles;
// import thaumcraft.api.golems.tasks.Task;
// import thaumcraft.common.golems.seals.SealUse;

// public class SealLumberAdvanced extends SealUse implements ISealConfigToggles {

//     private ResourceLocation icon;
//     private static final int FILTER_SIZE = 12;
//     private SealToggle[] props = new SealToggle[] { 
//         new SealToggle(true, "replant", "Replant Trees"),
//         new SealToggle(true, "collect_drops", "Collect Drops")
//     };
    
//     public SealLumberAdvanced() {
//         this.icon = new ResourceLocation("fishermod", "items/seals/seal_lumber_advanced");
//     }
    
//     @Override
//     public void onTaskCompleted(World world, Task task, IGolemAPI golem) {
        
        
//         BlockPos pos = ((Task) task).getPos();
//         Block choppedBlock = world.getBlockState(pos).getBlock();
        
//         if (choppedBlock == Blocks.AIR && props[0].getValue()) {
//             ItemStack sapling = findSaplingInInventory(golem, choppedBlock);
//             if (!sapling.isEmpty()) {
//                 world.setBlockState(pos, Block.getBlockFromItem(sapling.getItem()).getDefaultState());
//                 sapling.shrink(1);
//             }
//         }
        
//         if (props[1].getValue()) {
//             collectDroppedItems(world, golem, pos);
//         }
//     }
    
//     private ItemStack findSaplingInInventory(IGolemAPI golem, Block choppedBlock) {
//         for (int i = 0; i < golem.getInventory().getSizeInventory(); i++) {
//             ItemStack stack = golem.getInventory().getStackInSlot(i);
//             if (stack.getItem() == Item.getItemFromBlock(Blocks.SAPLING)) {
//                 return stack;
//             }
//         }
//         return ItemStack.EMPTY;
//     }
    
//     private void collectDroppedItems(World world, IGolemAPI golem, BlockPos pos) {
//         // Логіка збору дропів після зрубу дерева
//     }
    
//     @Override
//     public String getKey() {
//         return "golem.seal.lumber_advanced";
//     }
    
//     @Override
//     public ResourceLocation getSealIcon() {
//         return icon;
//     }
    
//     @Override
//     public int getFilterSize() {
//         return FILTER_SIZE;
//     }
    
//     @Override
//     public int[] getGuiCategories() {
//         return new int[] { 1, 3, 0, 4 };
//     }
    
//     @Override
//     public SealToggle[] getToggles() {
//         return props;
//     }
    
//     @Override
//     public void setToggle(int indx, boolean value) {
//         props[indx].setValue(value);
//     }
    
//     @Override
//     public EnumGolemTrait[] getRequiredTags() {
//         return new EnumGolemTrait[] { EnumGolemTrait.SMART, EnumGolemTrait.TOUGH };
//     }
// }
