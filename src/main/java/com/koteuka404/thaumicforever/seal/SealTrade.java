package com.koteuka404.thaumicforever.seal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import com.koteuka404.thaumicforever.registry.ModItems;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.container.ContainerPech;
import thaumcraft.common.container.InventoryPech;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.seals.SealFiltered;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealTrade extends SealFiltered implements ISealConfigToggles, ISealConfigArea {
    private static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "items/seals/seal_trade");
    private static final int FILTER_SLOTS = 9;
    private static final int COOLDOWN_TICKS = 60;
    private static final int RANDOM_TRADE_COOLDOWN_TICKS = 10;
    private static final int REQUEST_INTERVAL = 80;
    private static final Map<String, Integer> COOLDOWNS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> REQUEST_DELAYS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> ACTIVE_RECIPE_INDEX = new ConcurrentHashMap<>();

    private final SealToggle[] toggles = new SealToggle[]{
            new SealToggle(true, "tf_repeat_trade", "thaumicforever.golem.prop.repeat_trade"),
            new SealToggle(true, "tf_match_nbt", "thaumicforever.golem.prop.match_nbt")
    };

    @Override
    public String getKey() {
        return "thaumicforever:trade";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public int getFilterSize() {
        return FILTER_SLOTS;
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (world.isRemote || seal == null || seal.getSealPos() == null) return;

        String key = makeKey(world, seal);
        int cooldown = COOLDOWNS.getOrDefault(key, 0);
        if (cooldown > 0) {
            COOLDOWNS.put(key, cooldown - 1);
            return;
        }

        int delay = REQUEST_DELAYS.getOrDefault(key, 0);
        if (delay > 0) {
            REQUEST_DELAYS.put(key, delay - 1);
        } else {
            requestTradeInputs(world, seal, key);
            REQUEST_DELAYS.put(key, REQUEST_INTERVAL);
        }

        if (hasTask(world, seal)) return;

        TradeMerchant merchant = findMerchant(world, seal);
        if (merchant != null && findDesiredRecipe(merchant, null) != null) {
            Task task = new Task(seal.getSealPos(), merchant.getEntity());
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
        }
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (golem == null || golem.isInCombat()) return false;
        TradeMerchant merchant = getTaskMerchant(golem.getGolemWorld(), task);
        return merchant != null && findDesiredRecipe(merchant, golem.getCarrying()) != null;
    }

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        if (world.isRemote || task == null || task.getSealPos() == null) return false;
        String key = makeKey(world, task);
        if (COOLDOWNS.getOrDefault(key, 0) > 0) return false;

        TradeMerchant merchant = getTaskMerchant(world, task);
        if (merchant == null) return false;

        TradeMatch match = findDesiredRecipe(merchant, golem.getCarrying());
        if (match == null) return false;

        List<ItemStack> results;
        if (merchant instanceof PechTradeMerchant) {
            results = ((PechTradeMerchant) merchant).performPechTrade(world, match);
            if (results == null) return false;
        } else if (merchant instanceof ThaumaturgeTradeMerchant) {
            results = ((ThaumaturgeTradeMerchant) merchant).performThaumaturgeTrade(world, match);
            if (results == null) return false;
        } else {
            consume(match.first, match.recipe.getItemToBuy().getCount());
            if (match.recipe.hasSecondItemToBuy()) {
                consume(match.second, match.recipe.getSecondItemToBuy().getCount());
            }

            merchant.useRecipe(match.recipe);
            merchant.verifySellingItem(match.recipe.getItemToSell());
            results = Collections.singletonList(match.recipe.getItemToSell().copy());
        }

        Entity entity = golem.getGolemEntity();
        for (ItemStack result : results) {
            if (result.isEmpty()) continue;
            ItemStack leftover = golem.holdItem(result.copy());
            if (!leftover.isEmpty()) {
                world.spawnEntity(new EntityItem(world, entity.posX, entity.posY + 0.5D, entity.posZ, leftover));
            }
        }

        golem.swingArm();
        golem.addRankXp(2);
        if (!toggles[0].getValue()) {
            COOLDOWNS.put(key, Integer.MAX_VALUE / 4);
        } else {
            COOLDOWNS.put(key, merchant instanceof ThaumaturgeTradeMerchant ? RANDOM_TRADE_COOLDOWN_TICKS : COOLDOWN_TICKS);
            if (merchant instanceof ThaumaturgeTradeMerchant) {
                REQUEST_DELAYS.remove(key);
            }
        }
        return true;
    }

    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
        if (!world.isRemote) {
            String key = world.provider.getDimension() + ":" + pos.toLong() + ":" + side.ordinal();
            COOLDOWNS.remove(key);
            REQUEST_DELAYS.remove(key);
            ACTIVE_RECIPE_INDEX.remove(key);
            TaskHandler.getTasks(world.provider.getDimension()).values().removeIf(t ->
                    t.getSealPos() != null && t.getSealPos().pos.equals(pos) && t.getSealPos().face == side);
            clearProvisionRequests(world, pos, side);
        }
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SMART, EnumGolemTrait.DEFT, EnumGolemTrait.SCOUT};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{2, 1, 0, 3, 4};
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
        super.writeCustomNBT(nbt);
        nbt.setBoolean("repeatTrade", toggles[0].getValue());
        nbt.setBoolean("matchNbt", toggles[1].getValue());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        toggles[0].setValue(!nbt.hasKey("repeatTrade") || nbt.getBoolean("repeatTrade"));
        toggles[1].setValue(!nbt.hasKey("matchNbt") || nbt.getBoolean("matchNbt"));
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {}

    @Override
    public void onTaskSuspension(World world, Task task) {}

    private void requestTradeInputs(World world, ISealEntity seal, String key) {
        TradeMerchant merchant = findMerchant(world, seal);
        if (merchant == null) {
            clearProvisionRequests(world, seal.getSealPos().pos, seal.getSealPos().face);
            return;
        }

        List<MerchantRecipe> desired = getDesiredRecipes(merchant);
        if (desired.isEmpty()) {
            clearProvisionRequests(world, seal.getSealPos().pos, seal.getSealPos().face);
            return;
        }

        clearProvisionRequests(world, seal.getSealPos().pos, seal.getSealPos().face);
        if (merchant instanceof PechTradeMerchant || merchant instanceof ThaumaturgeTradeMerchant) {
            for (MerchantRecipe recipe : desired) {
                requestRecipeInputs(world, seal, recipe);
            }
            return;
        }

        int index = Math.floorMod(ACTIVE_RECIPE_INDEX.getOrDefault(key, 0), desired.size());
        requestRecipeInputs(world, seal, desired.get(index));
        ACTIVE_RECIPE_INDEX.put(key, index + 1);
    }

    private void requestRecipeInputs(World world, ISealEntity seal, MerchantRecipe recipe) {
        if (recipe.hasSecondItemToBuy() && sameItem(recipe.getItemToBuy(), recipe.getSecondItemToBuy())) {
            ItemStack combined = recipe.getItemToBuy().copy();
            combined.grow(recipe.getSecondItemToBuy().getCount());
            GolemHelper.requestProvisioning(world, seal, combined);
        } else {
            GolemHelper.requestProvisioning(world, seal, recipe.getItemToBuy());
        }
        if (recipe.hasSecondItemToBuy() && !sameItem(recipe.getItemToBuy(), recipe.getSecondItemToBuy())) {
            GolemHelper.requestProvisioning(world, seal, recipe.getSecondItemToBuy());
        }
    }

    private TradeMerchant findMerchant(World world, ISealEntity seal) {
        return findMerchant(world, GolemHelper.getBoundsForArea(seal).grow(0.5D), seal.getSealPos().pos);
    }

    private TradeMerchant findMerchant(World world, AxisAlignedBB box, BlockPos origin) {
        List<TradeMerchant> merchants = new ArrayList<>();
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, box)) {
            TradeMerchant merchant = asTradeMerchant(entity);
            if (merchant != null) {
                merchants.add(merchant);
            }
        }
        if (merchants.isEmpty()) return null;
        return findClosestUsableMerchant(merchants, origin);
    }

    private TradeMerchant findClosestUsableMerchant(List<TradeMerchant> merchants, BlockPos origin) {
        TradeMerchant closest = null;
        double best = Double.MAX_VALUE;
        for (TradeMerchant merchant : merchants) {
            if (findDesiredRecipe(merchant, null) == null) continue;
            double distance = merchant.getEntity().getDistanceSq(origin);
            if (distance < best) {
                best = distance;
                closest = merchant;
            }
        }
        if (closest != null) return closest;

        closest = merchants.get(0);
        best = closest.getEntity().getDistanceSq(origin);
        for (TradeMerchant merchant : merchants) {
            double distance = merchant.getEntity().getDistanceSq(origin);
            if (distance < best) {
                best = distance;
                closest = merchant;
            }
        }
        return closest;
    }

    private TradeMerchant getTaskMerchant(World world, Task task) {
        if (task == null || task.getEntity() == null || !task.getEntity().isEntityAlive()) return null;
        return asTradeMerchant(task.getEntity());
    }

    private TradeMerchant asTradeMerchant(Entity entity) {
        if (!(entity instanceof EntityLivingBase) || !entity.isEntityAlive()) return null;
        if (entity instanceof IMerchant) {
            return new VanillaTradeMerchant((EntityLivingBase) entity, (IMerchant) entity);
        }
        PechTradeMerchant pech = PechTradeMerchant.create((EntityLivingBase) entity, this);
        if (pech != null) return pech;
        ThaumaturgeTradeMerchant thaumaturge = ThaumaturgeTradeMerchant.create((EntityLivingBase) entity, this);
        if (thaumaturge != null) return thaumaturge;
        return ReflectiveTradeMerchant.create((EntityLivingBase) entity);
    }

    private TradeMatch findDesiredRecipe(TradeMerchant merchant, List<ItemStack> carried) {
        if (merchant instanceof PechTradeMerchant) {
            return ((PechTradeMerchant) merchant).findMatch(carried);
        }
        if (merchant instanceof ThaumaturgeTradeMerchant) {
            return ((ThaumaturgeTradeMerchant) merchant).findMatch(carried);
        }

        MerchantRecipeList recipes = merchant.getRecipes();
        if (recipes == null) return null;

        for (MerchantRecipe recipe : recipes) {
            if (!isAllowedRecipe(recipe)) continue;
            if (carried == null) return new TradeMatch(recipe, ItemStack.EMPTY, ItemStack.EMPTY);

            ItemStack first = findStack(carried, recipe.getItemToBuy(), null);
            if (first.isEmpty()) continue;
            ItemStack second = ItemStack.EMPTY;
            if (recipe.hasSecondItemToBuy()) {
                if (sameItem(recipe.getItemToBuy(), recipe.getSecondItemToBuy())
                        && first.getCount() >= recipe.getItemToBuy().getCount() + recipe.getSecondItemToBuy().getCount()) {
                    second = first;
                } else {
                    second = findStack(carried, recipe.getSecondItemToBuy(), first);
                    if (second.isEmpty()) continue;
                }
            }
            return new TradeMatch(recipe, first, second);
        }
        return null;
    }

    private List<MerchantRecipe> getDesiredRecipes(TradeMerchant merchant) {
        List<MerchantRecipe> desired = new ArrayList<>();
        MerchantRecipeList recipes = merchant.getRecipes();
        if (recipes == null) return desired;

        for (MerchantRecipe recipe : recipes) {
            if (isAllowedRecipe(recipe)) {
                desired.add(recipe);
            }
        }
        return desired;
    }

    private boolean isAllowedRecipe(MerchantRecipe recipe) {
        return recipe != null && !recipe.isRecipeDisabled() && matchesOutputFilter(recipe.getItemToSell());
    }

    private boolean matchesOutputFilter(ItemStack output) {
        boolean hasFilter = false;
        boolean matched = false;
        for (ItemStack filter : getInv()) {
            if (filter.isEmpty()) continue;
            hasFilter = true;
            if (sameItem(filter, output)) {
                matched = true;
                break;
            }
        }
        if (!hasFilter) return true;
        return isBlacklist() ? !matched : matched;
    }

    private ItemStack findStack(List<ItemStack> stacks, ItemStack required, ItemStack excluded) {
        for (ItemStack stack : stacks) {
            if (stack == excluded || stack.isEmpty()) continue;
            if (stack.getCount() >= required.getCount() && sameItem(stack, required)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean sameItem(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) return false;
        if (!ItemStack.areItemsEqual(a, b)) return false;
        return !toggles[1].getValue() || ItemStack.areItemStackTagsEqual(a, b);
    }

    private void consume(ItemStack stack, int amount) {
        if (!stack.isEmpty() && amount > 0) {
            stack.shrink(amount);
        }
    }

    private boolean hasTask(World world, ISealEntity seal) {
        return TaskHandler.getTasks(world.provider.getDimension()).values().stream()
                .anyMatch(t -> t.getSealPos() != null && t.getSealPos().equals(seal.getSealPos()));
    }

    private void clearProvisionRequests(World world, BlockPos pos, EnumFacing side) {
        GolemHelper.provisionRequests
                .getOrDefault(world.provider.getDimension(), new ArrayList<>())
                .removeIf(request -> request.getSeal() != null
                        && request.getSeal().getSealPos() != null
                        && request.getSeal().getSealPos().pos.equals(pos)
                        && request.getSeal().getSealPos().face == side);
    }

    private static String makeKey(World world, ISealEntity seal) {
        return world.provider.getDimension() + ":" + seal.getSealPos().pos.toLong() + ":" + seal.getSealPos().face.ordinal();
    }

    private static String makeKey(World world, Task task) {
        return world.provider.getDimension() + ":" + task.getSealPos().pos.toLong() + ":" + task.getSealPos().face.ordinal();
    }

    private interface TradeMerchant {
        EntityLivingBase getEntity();
        MerchantRecipeList getRecipes();
        void useRecipe(MerchantRecipe recipe);
        void verifySellingItem(ItemStack stack);
    }

    private static class VanillaTradeMerchant implements TradeMerchant {
        private final EntityLivingBase entity;
        private final IMerchant merchant;

        private VanillaTradeMerchant(EntityLivingBase entity, IMerchant merchant) {
            this.entity = entity;
            this.merchant = merchant;
        }

        @Override
        public EntityLivingBase getEntity() {
            return entity;
        }

        @Override
        public MerchantRecipeList getRecipes() {
            return merchant.getRecipes((EntityPlayer) null);
        }

        @Override
        public void useRecipe(MerchantRecipe recipe) {
            merchant.useRecipe(recipe);
        }

        @Override
        public void verifySellingItem(ItemStack stack) {
            merchant.verifySellingItem(stack);
        }
    }

    private static class ReflectiveTradeMerchant implements TradeMerchant {
        private final EntityLivingBase entity;
        private final Method getRecipes;
        private final Method useRecipe;
        private final Method verifySellingItem;

        private ReflectiveTradeMerchant(EntityLivingBase entity, Method getRecipes, Method useRecipe, Method verifySellingItem) {
            this.entity = entity;
            this.getRecipes = getRecipes;
            this.useRecipe = useRecipe;
            this.verifySellingItem = verifySellingItem;
        }

        private static ReflectiveTradeMerchant create(EntityLivingBase entity) {
            Method getRecipes = findGetRecipes(entity.getClass());
            Method useRecipe = findMethod(entity.getClass(), "useRecipe", MerchantRecipe.class);
            if (getRecipes == null || useRecipe == null) return null;
            return new ReflectiveTradeMerchant(entity, getRecipes, useRecipe,
                    findMethod(entity.getClass(), "verifySellingItem", ItemStack.class));
        }

        @Override
        public EntityLivingBase getEntity() {
            return entity;
        }

        @Override
        public MerchantRecipeList getRecipes() {
            try {
                Object result = getRecipes.getParameterTypes().length == 0
                        ? getRecipes.invoke(entity)
                        : getRecipes.invoke(entity, new Object[]{null});
                return result instanceof MerchantRecipeList ? (MerchantRecipeList) result : null;
            } catch (Exception ignored) {
                return null;
            }
        }

        @Override
        public void useRecipe(MerchantRecipe recipe) {
            invoke(useRecipe, recipe);
        }

        @Override
        public void verifySellingItem(ItemStack stack) {
            if (verifySellingItem != null) {
                invoke(verifySellingItem, stack);
            }
        }

        private void invoke(Method method, Object arg) {
            try {
                method.invoke(entity, arg);
            } catch (Exception ignored) {}
        }
    }

    private static class PechTradeMerchant implements TradeMerchant {
        private static Field valuedItemsField;
        private static Field tradeInventoryField;
        private static boolean initialized;

        private final EntityPech pech;
        private final SealTrade seal;

        private PechTradeMerchant(EntityPech pech, SealTrade seal) {
            this.pech = pech;
            this.seal = seal;
        }

        private static PechTradeMerchant create(EntityLivingBase entity, SealTrade seal) {
            if (!(entity instanceof EntityPech)) return null;
            EntityPech pech = (EntityPech) entity;
            initialize();
            if (valuedItemsField == null || tradeInventoryField == null || !pech.isTamed()) return null;
            return new PechTradeMerchant(pech, seal);
        }

        private static void initialize() {
            if (initialized) return;
            initialized = true;
            try {
                valuedItemsField = EntityPech.class.getField("valuedItems");
                tradeInventoryField = EntityPech.class.getField("tradeInventory");
            } catch (Exception ignored) {
                valuedItemsField = null;
                tradeInventoryField = null;
            }
        }

        @Override
        public EntityLivingBase getEntity() {
            return pech;
        }

        @Override
        public MerchantRecipeList getRecipes() {
            try {
                Map<Integer, Integer> valuedItems = readValuedItems();
                List<ItemStack> rewards = readPossibleRewards();

                MerchantRecipeList recipes = new MerchantRecipeList();
                ItemStack displayReward = rewards.isEmpty() ? ItemStack.EMPTY : rewards.get(0).copy();
                for (ItemStack input : getInputRequests(valuedItems)) {
                    ItemStack output = displayReward.isEmpty() ? input.copy() : displayReward.copy();
                    recipes.add(new MerchantRecipe(input, output));
                }
                return recipes.isEmpty() ? null : recipes;
            } catch (Exception ignored) {
                return null;
            }
        }

        TradeMatch findMatch(List<ItemStack> carried) {
            ItemStack display = getDisplayReward();
            if (carried == null) {
                ItemStack request = getFirstValuedItem();
                return request.isEmpty() ? null : new TradeMatch(new MerchantRecipe(request, display.isEmpty() ? request.copy() : display), ItemStack.EMPTY, ItemStack.EMPTY);
            }
            for (ItemStack stack : carried) {
                if (stack.isEmpty() || !pech.isValued(stack)) continue;
                ItemStack payment = stack.copy();
                payment.setCount(1);
                return new TradeMatch(new MerchantRecipe(payment, display.isEmpty() ? payment.copy() : display), stack, ItemStack.EMPTY);
            }
            return null;
        }

        List<ItemStack> performPechTrade(World world, TradeMatch match) {
            if (!(world instanceof WorldServer) || match == null || match.first.isEmpty()) return null;
            boolean wasTamed = pech.isTamed();
            try {
                FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) world);
                ContainerPech container = new ContainerPech(fakePlayer.inventory, world, pech);
                InventoryPech inventory = container.getMerchantInventory();

                ItemStack payment = match.first.splitStack(match.recipe.getItemToBuy().getCount());
                if (payment.isEmpty()) return null;
                inventory.setInventorySlotContents(0, payment);
                container.enchantItem(fakePlayer, 0);
                if (wasTamed && !pech.isTamed()) {
                    pech.setTamed(true);
                    pech.setCombatTask();
                }

                List<ItemStack> results = new ArrayList<>();
                for (int slot = 1; slot <= 4; slot++) {
                    ItemStack stack = inventory.getStackInSlot(slot);
                    if (stack.isEmpty()) continue;
                    if (seal.matchesOutputFilter(stack)) {
                        results.add(stack.copy());
                    } else {
                        pech.entityDropItem(stack.copy(), 0.2F);
                    }
                    inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
                }
                inventory.setInventorySlotContents(0, ItemStack.EMPTY);
                container.onContainerClosed(fakePlayer);
                pech.trading = false;
                return results;
            } catch (Exception ignored) {
                return null;
            } finally {
                pech.trading = false;
                if (wasTamed && !pech.isTamed()) {
                    pech.setTamed(true);
                    pech.setCombatTask();
                }
            }
        }

        @Override
        public void useRecipe(MerchantRecipe recipe) {}

        @Override
        public void verifySellingItem(ItemStack stack) {}

        private ItemStack getDisplayReward() {
            try {
                List<ItemStack> rewards = readPossibleRewards();
                return rewards.isEmpty() ? ItemStack.EMPTY : rewards.get(0).copy();
            } catch (Exception ignored) {
                return ItemStack.EMPTY;
            }
        }

        private ItemStack getFirstValuedItem() {
            try {
                for (ItemStack stack : getInputRequests(readValuedItems())) {
                    if (!stack.isEmpty()) return stack.copy();
                }
            } catch (Exception ignored) {}
            return ItemStack.EMPTY;
        }

        private List<ItemStack> getInputRequests(Map<Integer, Integer> valuedItems) {
            List<ItemStack> requests = new ArrayList<>();
            for (Integer id : valuedItems.keySet()) {
                Item item = Item.getItemById(id);
                if (item != null) {
                    addValuedRequest(requests, new ItemStack(item));
                }
            }

            addValuedRequest(requests, new ItemStack(ModItems.coin));
            addValuedRequest(requests, new ItemStack(Items.GOLD_NUGGET));
            addValuedRequest(requests, new ItemStack(Items.GOLD_INGOT));
            addValuedRequest(requests, new ItemStack(Items.EMERALD));
            addValuedRequest(requests, new ItemStack(Items.DIAMOND));
            return requests;
        }

        private void addValuedRequest(List<ItemStack> requests, ItemStack stack) {
            if (stack.isEmpty() || !pech.isValued(stack)) return;
            stack = stack.copy();
            stack.setCount(1);
            for (ItemStack existing : requests) {
                if (ItemStack.areItemsEqual(existing, stack) && ItemStack.areItemStackTagsEqual(existing, stack)) {
                    return;
                }
            }
            requests.add(stack);
        }

        @SuppressWarnings("unchecked")
        private Map<Integer, Integer> readValuedItems() throws IllegalAccessException {
            Object value = valuedItemsField.get(null);
            return value instanceof Map ? (Map<Integer, Integer>) value : Collections.emptyMap();
        }

        private List<ItemStack> readPossibleRewards() throws IllegalAccessException {
            Object tradeInventory = tradeInventoryField.get(null);
            if (!(tradeInventory instanceof Map)) return Collections.emptyList();
            Object typedRewards = ((Map<?, ?>) tradeInventory).get(pech.getPechType());
            if (!(typedRewards instanceof Iterable)) return Collections.emptyList();

            List<ItemStack> rewards = new ArrayList<>();
            for (Object raw : (Iterable<?>) typedRewards) {
                if (!(raw instanceof List)) continue;
                List<?> entry = (List<?>) raw;
                if (entry.size() < 2 || !(entry.get(1) instanceof ItemStack)) continue;
                ItemStack reward = ((ItemStack) entry.get(1)).copy();
                if (!reward.isEmpty() && seal.matchesOutputFilter(reward)) {
                    rewards.add(reward);
                }
            }
            return rewards;
        }
    }

    private static class ThaumaturgeTradeMerchant implements TradeMerchant {
        private static final String ENTITY_CLASS = "com.keletu.thaumicconcilium.entity.EntityThaumaturge";
        private static final String CONTAINER_CLASS = "com.keletu.thaumicconcilium.container.ContainerThaumaturge";

        private static Class<?> thaumaturgeClass;
        private static Class<?> containerClass;
        private static java.lang.reflect.Constructor<?> containerConstructor;
        private static Method isValuedMethod;
        private static Method getValueMethod;
        private static Method getMerchantInventoryMethod;
        private static Method enchantItemMethod;
        private static Field tradeInventoryField;
        private static Field valuedItemsField;
        private static boolean initialized;

        private final EntityLivingBase thaumaturge;
        private final SealTrade seal;

        private ThaumaturgeTradeMerchant(EntityLivingBase thaumaturge, SealTrade seal) {
            this.thaumaturge = thaumaturge;
            this.seal = seal;
        }

        private static ThaumaturgeTradeMerchant create(EntityLivingBase entity, SealTrade seal) {
            initialize();
            if (thaumaturgeClass == null || !thaumaturgeClass.isInstance(entity)) return null;
            if (containerConstructor == null || isValuedMethod == null || getValueMethod == null || getMerchantInventoryMethod == null || enchantItemMethod == null) return null;
            return new ThaumaturgeTradeMerchant(entity, seal);
        }

        private static void initialize() {
            if (initialized) return;
            initialized = true;
            try {
                thaumaturgeClass = Class.forName(ENTITY_CLASS);
                containerClass = Class.forName(CONTAINER_CLASS);
                containerConstructor = containerClass.getConstructor(InventoryPlayer.class, World.class, thaumaturgeClass);
                isValuedMethod = thaumaturgeClass.getMethod("isValued", ItemStack.class);
                getValueMethod = thaumaturgeClass.getMethod("getValue", ItemStack.class);
                getMerchantInventoryMethod = containerClass.getMethod("getMerchantInventory");
                enchantItemMethod = findAnyMethod(containerClass, new String[]{"enchantItem", "func_75140_a"}, EntityPlayer.class, int.class);
                tradeInventoryField = thaumaturgeClass.getField("tradeInventory");
                valuedItemsField = thaumaturgeClass.getDeclaredField("valuedItems");
                valuedItemsField.setAccessible(true);
            } catch (Exception ignored) {
                thaumaturgeClass = null;
                containerClass = null;
                containerConstructor = null;
                isValuedMethod = null;
                getValueMethod = null;
                getMerchantInventoryMethod = null;
                enchantItemMethod = null;
                tradeInventoryField = null;
                valuedItemsField = null;
            }
        }

        @Override
        public EntityLivingBase getEntity() {
            return thaumaturge;
        }

        @Override
        public MerchantRecipeList getRecipes() {
            try {
                List<ItemStack> rewards = readPossibleRewards();
                MerchantRecipeList recipes = new MerchantRecipeList();
                ItemStack displayReward = rewards.isEmpty() ? ItemStack.EMPTY : rewards.get(0).copy();
                for (ItemStack input : getInputRequests()) {
                    ItemStack output = displayReward.isEmpty() ? input.copy() : displayReward.copy();
                    recipes.add(new MerchantRecipe(input, output));
                }
                return recipes.isEmpty() ? null : recipes;
            } catch (Exception ignored) {
                return null;
            }
        }

        TradeMatch findMatch(List<ItemStack> carried) {
            ItemStack display = getDisplayReward();
            if (carried == null) {
                ItemStack request = getFirstValuedItem();
                return request.isEmpty() ? null : new TradeMatch(new MerchantRecipe(request, display.isEmpty() ? request.copy() : display), ItemStack.EMPTY, ItemStack.EMPTY);
            }
            ItemStack best = ItemStack.EMPTY;
            int bestValue = Integer.MIN_VALUE;
            for (ItemStack stack : carried) {
                if (stack.isEmpty() || !isValued(stack)) continue;
                int value = getValue(stack);
                if (best.isEmpty() || value > bestValue) {
                    best = stack;
                    bestValue = value;
                }
            }
            if (best.isEmpty()) return null;
            ItemStack payment = best.copy();
            payment.setCount(1);
            return new TradeMatch(new MerchantRecipe(payment, display.isEmpty() ? payment.copy() : display), best, ItemStack.EMPTY);
        }

        List<ItemStack> performThaumaturgeTrade(World world, TradeMatch match) {
            if (!(world instanceof WorldServer) || match == null || match.first.isEmpty()) return null;
            try {
                FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) world);
                Object container = containerConstructor.newInstance(fakePlayer.inventory, world, thaumaturge);
                Object inventoryObject = getMerchantInventoryMethod.invoke(container);
                if (!(inventoryObject instanceof IInventory)) return null;
                IInventory inventory = (IInventory) inventoryObject;

                ItemStack payment = match.first.splitStack(match.recipe.getItemToBuy().getCount());
                if (payment.isEmpty()) return null;
                inventory.setInventorySlotContents(0, payment);
                enchantItemMethod.invoke(container, fakePlayer, 0);

                List<ItemStack> results = new ArrayList<>();
                for (int slot = 1; slot <= 4; slot++) {
                    ItemStack stack = inventory.getStackInSlot(slot);
                    if (stack.isEmpty()) continue;
                    if (seal.matchesOutputFilter(stack)) {
                        results.add(stack.copy());
                    } else {
                        thaumaturge.entityDropItem(stack.copy(), 0.2F);
                    }
                    inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
                }
                inventory.setInventorySlotContents(0, ItemStack.EMPTY);
                invokeContainerClose(container, fakePlayer);
                setTrading(false);
                return results;
            } catch (Exception ignored) {
                return null;
            } finally {
                setTrading(false);
            }
        }

        @Override
        public void useRecipe(MerchantRecipe recipe) {}

        @Override
        public void verifySellingItem(ItemStack stack) {}

        private ItemStack getDisplayReward() {
            try {
                List<ItemStack> rewards = readPossibleRewards();
                return rewards.isEmpty() ? ItemStack.EMPTY : rewards.get(0).copy();
            } catch (Exception ignored) {
                return ItemStack.EMPTY;
            }
        }

        private ItemStack getFirstValuedItem() {
            for (ItemStack stack : getInputRequests()) {
                if (!stack.isEmpty()) return stack.copy();
            }
            return ItemStack.EMPTY;
        }

        private List<ItemStack> getInputRequests() {
            List<ItemStack> requests = new ArrayList<>();
            try {
                for (Integer id : readValuedItems().keySet()) {
                    Item item = Item.getItemById(id);
                    if (item != null) {
                        addValuedRequest(requests, new ItemStack(item));
                    }
                }
            } catch (Exception ignored) {}

            for (int meta = 0; meta <= 5; meta++) {
                addValuedRequest(requests, new ItemStack(Items.SKULL, 1, meta));
            }
            addValuedRequest(requests, new ItemStack(Items.EXPERIENCE_BOTTLE));
            addValuedRequest(requests, new ItemStack(Items.ENCHANTED_BOOK));
            addValuedRequest(requests, new ItemStack(Items.BOOK));
            addValuedRequest(requests, new ItemStack(ItemsTC.salisMundus));
            addValuedRequest(requests, new ItemStack(ModItems.coin));
            addValuedRequest(requests, new ItemStack(Items.GOLD_NUGGET));
            addValuedRequest(requests, new ItemStack(Items.GOLD_INGOT));
            addValuedRequest(requests, new ItemStack(Items.EMERALD));
            addValuedRequest(requests, new ItemStack(Items.DIAMOND));
            for (int meta = 0; meta <= 6; meta++) {
                addValuedRequest(requests, new ItemStack(ItemsTC.curio, 1, meta));
            }
            requests.sort((a, b) -> Integer.compare(getValue(b), getValue(a)));
            return requests;
        }

        private void addValuedRequest(List<ItemStack> requests, ItemStack stack) {
            if (stack.isEmpty() || !isValued(stack)) return;
            stack = stack.copy();
            stack.setCount(1);
            for (ItemStack existing : requests) {
                if (ItemStack.areItemsEqual(existing, stack) && ItemStack.areItemStackTagsEqual(existing, stack)) {
                    return;
                }
            }
            requests.add(stack);
        }

        private boolean isValued(ItemStack stack) {
            try {
                return Boolean.TRUE.equals(isValuedMethod.invoke(thaumaturge, stack));
            } catch (Exception ignored) {
                return false;
            }
        }

        private int getValue(ItemStack stack) {
            try {
                Object value = getValueMethod.invoke(thaumaturge, stack);
                return value instanceof Integer ? (Integer) value : 0;
            } catch (Exception ignored) {
                return 0;
            }
        }

        @SuppressWarnings("unchecked")
        private Map<Integer, Integer> readValuedItems() throws IllegalAccessException {
            if (valuedItemsField == null) return Collections.emptyMap();
            Object value = valuedItemsField.get(null);
            return value instanceof Map ? (Map<Integer, Integer>) value : Collections.emptyMap();
        }

        private List<ItemStack> readPossibleRewards() throws IllegalAccessException {
            if (tradeInventoryField == null) return Collections.emptyList();
            Object rawRewards = tradeInventoryField.get(null);
            if (!(rawRewards instanceof Iterable)) return Collections.emptyList();

            List<ItemStack> rewards = new ArrayList<>();
            for (Object raw : (Iterable<?>) rawRewards) {
                if (!(raw instanceof List)) continue;
                List<?> entry = (List<?>) raw;
                if (entry.size() < 2 || !(entry.get(1) instanceof ItemStack)) continue;
                ItemStack reward = ((ItemStack) entry.get(1)).copy();
                if (!reward.isEmpty() && seal.matchesOutputFilter(reward)) {
                    rewards.add(reward);
                }
            }
            return rewards;
        }

        private void setTrading(boolean value) {
            try {
                Field trading = thaumaturgeClass.getField("trading");
                trading.setBoolean(thaumaturge, value);
            } catch (Exception ignored) {}
        }

        private void invokeContainerClose(Object container, EntityPlayer player) {
            try {
                Method close = findAnyMethod(containerClass, new String[]{"onContainerClosed", "func_75134_a"}, EntityPlayer.class);
                if (close == null) return;
                close.invoke(container, player);
            } catch (Exception ignored) {}
        }
    }

    private static Method findAnyMethod(Class<?> clazz, String[] names, Class<?>... params) {
        for (String name : names) {
            try {
                Method method = clazz.getMethod(name, params);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }

    private static Method findGetRecipes(Class<?> clazz) {
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (!"getRecipes".equals(method.getName())) continue;
                if (!MerchantRecipeList.class.isAssignableFrom(method.getReturnType())) continue;
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 0 || (params.length == 1 && EntityPlayer.class.isAssignableFrom(params[0]))) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        return null;
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?>... params) {
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            try {
                Method method = current.getDeclaredMethod(name, params);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }

    private static class TradeMatch {
        private final MerchantRecipe recipe;
        private final ItemStack first;
        private final ItemStack second;

        private TradeMatch(MerchantRecipe recipe, ItemStack first, ItemStack second) {
            this.recipe = recipe;
            this.first = first;
            this.second = second;
        }
    }
}
