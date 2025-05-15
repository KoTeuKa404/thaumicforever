// package com.koteuka404.thaumicforever;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;

// import net.minecraft.entity.Entity;
// import net.minecraft.item.ItemStack;
// import net.minecraft.tileentity.TileEntity;
// import net.minecraft.util.EnumFacing;
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.world.World;
// import thaumcraft.api.aspects.Aspect;
// import thaumcraft.api.aspects.AspectList;
// import thaumcraft.api.golems.EnumGolemTrait;
// import thaumcraft.api.golems.GolemHelper;
// import thaumcraft.api.golems.IGolemAPI;
// import thaumcraft.api.golems.seals.ISealEntity;
// import thaumcraft.api.golems.tasks.Task;
// import thaumcraft.api.items.ItemsTC;
// import thaumcraft.common.container.InventoryArcaneWorkbench;
// import thaumcraft.common.golems.seals.SealFiltered;
// import thaumcraft.common.golems.tasks.TaskHandler;
// import thaumcraft.common.items.resources.ItemCrystalEssence;
// import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

// public class SealRefill extends SealFiltered {
//     private ResourceLocation icon;
//     private static final Map<Aspect, Integer> ASPECT_SLOT_MAP = new HashMap<>();
//     private static final Map<BlockPos, Integer> cooldowns = new HashMap<>();
//     private static final Map<BlockPos, Map<Integer, Integer>> lastSlotCounts = new HashMap<>();

//     static {
//         ASPECT_SLOT_MAP.put(Aspect.AIR, 9);
//         ASPECT_SLOT_MAP.put(Aspect.EARTH, 12);
//         ASPECT_SLOT_MAP.put(Aspect.FIRE, 10);
//         ASPECT_SLOT_MAP.put(Aspect.WATER, 11);
//         ASPECT_SLOT_MAP.put(Aspect.ORDER, 13);
//         ASPECT_SLOT_MAP.put(Aspect.ENTROPY, 14);
//     }

//     public SealRefill() {
//         this.icon = new ResourceLocation("thaumicforever", "items/seals/seal_golem_core_essentia");
//     }

//     @Override
//     public String getKey() {
//         return "thaumicforever:golem_core_essentia";
//     }

//     @Override
//     public int getFilterSize() {
//         return 6;
//     }

//     private static final Map<UUID, Integer> golemDelays = new HashMap<>();

//     @Override
//     public void tickSeal(World world, ISealEntity seal) {
//         BlockPos pos = seal.getSealPos().pos;
    
//         int ticks = cooldowns.getOrDefault(pos, 0);
//         if (ticks > 0) {
//             cooldowns.put(pos, ticks - 1);
//             return;
//         }
//         cooldowns.put(pos, 100);
    
//         TileEntity tile = world.getTileEntity(pos);
//         if (!(tile instanceof TileArcaneWorkbench)) return;
    
//         TileArcaneWorkbench workbench = (TileArcaneWorkbench) tile;
//         InventoryArcaneWorkbench inventory = workbench.inventoryCraft;
    
//         Map<Integer, Integer> lastCounts = lastSlotCounts.computeIfAbsent(pos, p -> new HashMap<>());
    
//         for (Map.Entry<Aspect, Integer> entry : ASPECT_SLOT_MAP.entrySet()) {
//             Aspect aspect = entry.getKey();
//             int slotIndex = entry.getValue();
//             ItemStack slotStack = inventory.getStackInSlot(slotIndex);
    
//             int current = slotStack.isEmpty() ? 0 : slotStack.getCount();
//             Aspect slotAspect = getAspectFromCrystal(slotStack);
//             boolean wrongType = slotAspect != null && !slotAspect.equals(aspect);
//             int needed = (slotStack.isEmpty() || wrongType) ? 64 : 64 - current;
    
//             int last = lastCounts.getOrDefault(slotIndex, -1);
//             if (last == current && current >= 64) continue;
//             lastCounts.put(slotIndex, current);
    
//             // Видаляємо старі запити, якщо слот уже заповнений
//             if (needed <= 0) {
//                 GolemHelper.provisionRequests
//                     .getOrDefault(world.provider.getDimension(), new java.util.ArrayList<>())
//                     .removeIf(r ->
//                         r.getSeal() != null &&
//                         r.getSeal().getSealPos().equals(seal.getSealPos()) &&
//                         getAspectFromCrystal(r.getStack()) == aspect
//                     );
//                 continue;
//             }
    
//             // Пробуємо поповнити з голема
//             boolean refilledFromGolem = false;
//             for (Entity entity : world.getEntitiesWithinAABB(Entity.class, new net.minecraft.util.math.AxisAlignedBB(pos).grow(8))) {
//                 if (entity instanceof IGolemAPI) {
//                     IGolemAPI golem = (IGolemAPI) entity;
//                     UUID uuid = ((Entity) golem).getUniqueID();
    
//                     for (ItemStack stack : golem.getCarrying()) {
//                         Aspect golemAspect = getAspectFromCrystal(stack);
//                         if (golemAspect != null && golemAspect.equals(aspect)) {
//                             if (placeCrystalInSlot(golem, inventory, stack, slotIndex, pos, world)) {
//                                 golem.swingArm();
//                                 golemDelays.put(uuid, 20);
//                                 refilledFromGolem = true;
//                                 break;
//                             }
                            
//                         }
//                     }
//                 }
//                 if (refilledFromGolem) break;
//             }
//             if (refilledFromGolem) continue;
    
//             // Створюємо новий ProvisionRequest тільки якщо немає задачі або запиту
//             boolean taskExists = TaskHandler.getTasks(world.provider.getDimension())
//                 .values().stream()
//                 .anyMatch(t -> t.getSealPos().equals(seal.getSealPos()) && t.getData() == slotIndex);
    
//             boolean requestExists = GolemHelper.provisionRequests
//                 .getOrDefault(world.provider.getDimension(), new java.util.ArrayList<>())
//                 .stream()
//                 .anyMatch(r ->
//                     r.getSeal() != null &&
//                     r.getSeal().getSealPos().equals(seal.getSealPos()) &&
//                     getAspectFromCrystal(r.getStack()) == aspect
//                 );
    
//                 if (!taskExists && !requestExists) {
//                     int amountNeeded = Math.min(64, needed); // Безпека: максимум 64
//                     ItemStack crystal = new ItemStack(ItemsTC.crystalEssence, amountNeeded);
//                     ((ItemCrystalEssence) crystal.getItem()).setAspects(crystal, new AspectList().add(aspect, 1));
                
//                     // Ось так ти вже передаєш конкретну кількість — це працює!
//                     GolemHelper.requestProvisioning(world, seal, crystal);
//                 }  
//         }
//     }
    
    
//     @Override
//     public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
//         Aspect requiredAspect = getAspectBySlot(task.getData());
//         for (ItemStack stack : golem.getCarrying()) {
//             if (stack.getItem() instanceof ItemCrystalEssence) {
//                 Aspect aspect = getAspectFromCrystal(stack);
//                 if (aspect != null && requiredAspect == aspect) return true;
//             }
//         }
//         return false;
//     }

//     @Override
//     public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
//         BlockPos pos = task.getPos();
//         TileEntity tile = world.getTileEntity(pos);
//         if (!(tile instanceof TileArcaneWorkbench)) return false;
    
//         InventoryArcaneWorkbench inventory = ((TileArcaneWorkbench) tile).inventoryCraft;
//         int slot = task.getData();
//         Aspect requiredAspect = getAspectBySlot(slot);
    
//         for (ItemStack stack : golem.getCarrying()) {
//             if (stack.getItem() instanceof ItemCrystalEssence) {
//                 Aspect aspect = getAspectFromCrystal(stack);
//                 if (aspect != null && requiredAspect == aspect) {
//                     if (placeCrystalInSlot(golem, inventory, stack, slot, pos, world)) {
//                         golem.swingArm();
//                         golem.addRankXp(1);
    
//                         // ✨ оновити кеш останньої кількості після вставлення
//                         ItemStack newStack = inventory.getStackInSlot(slot);
//                         int count = newStack.isEmpty() ? 0 : newStack.getCount();
//                         lastSlotCounts.computeIfAbsent(pos, p -> new HashMap<>()).put(slot, count);
    
//                         return true;
//                     }
//                 }
//             }
//         }
    
//         return false;
//     }
    


//     private boolean placeCrystalInSlot(IGolemAPI golem, InventoryArcaneWorkbench inventory, ItemStack crystal, int slot, BlockPos sealPos, World world) {
//         ItemStack existing = inventory.getStackInSlot(slot);
//         int existingCount = existing.isEmpty() ? 0 : existing.getCount();
//         int needed = 64 - existingCount;
//         int transfer = Math.min(needed, crystal.getCount());
    
//         if (transfer > 0) {
//             if (existing.isEmpty()) {
//                 ItemStack copy = crystal.copy();
//                 copy.setCount(transfer);
//                 inventory.setInventorySlotContents(slot, copy);
//             } else {
//                 existing.grow(transfer);
//             }
    
//             crystal.shrink(transfer);
//             golem.addRankXp(transfer);
    
//             // ✨ оновлюємо кеш, щоб tickSeal знав, що слот заповнений
//             lastSlotCounts.computeIfAbsent(sealPos, p -> new HashMap<>()).put(slot, existingCount + transfer);
    
//             return true;
//         }
    
//         return false;
//     }

//     private Aspect getAspectFromCrystal(ItemStack stack) {
//         if (stack.getItem() instanceof ItemCrystalEssence) {
//             AspectList list = ((ItemCrystalEssence) stack.getItem()).getAspects(stack);
//             if (list != null && list.size() > 0) return list.getAspects()[0];
//         }
//         return null;
//     }

//     private Aspect getAspectBySlot(int slot) {
//         for (Map.Entry<Aspect, Integer> entry : ASPECT_SLOT_MAP.entrySet()) {
//             if (entry.getValue() == slot) return entry.getKey();
//         }
//         return null;
//     }

//     @Override public void onRemoval(World world, BlockPos pos, EnumFacing side) {}
//     @Override public void onTaskSuspension(World world, Task task) {}
//     @Override public void onTaskStarted(World world, IGolemAPI golem, Task task) {}
//     @Override public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
//         return world.getTileEntity(pos) instanceof TileArcaneWorkbench;
//     }
//     @Override public ResourceLocation getSealIcon() { return this.icon; }
//     @Override public EnumGolemTrait[] getForbiddenTags() { return new EnumGolemTrait[]{EnumGolemTrait.CLUMSY}; }
//     @Override public EnumGolemTrait[] getRequiredTags() { return new EnumGolemTrait[]{EnumGolemTrait.SMART, EnumGolemTrait.DEFT}; }
// }