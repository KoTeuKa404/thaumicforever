package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.items.ItemsTC;

public class DeconstructionTableTileEntity extends TileEntity implements ITickable, IInventory {

    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY); // Два слота
    private int burnTime = 0;
    private Random random = new Random();
    private boolean oldResearchLoaded;

    // Рецепти для деконструкції
    private Map<Item, ItemStack[]> recipes = new HashMap<>();
    private Map<Item, Float> chances = new HashMap<>(); // Шанси для крафту

    public DeconstructionTableTileEntity() {
        this.oldResearchLoaded = net.minecraftforge.fml.common.Loader.isModLoaded("oldresearch");

        // Додавання рецептів і шансів
        recipes.put(Item.getItemFromBlock(net.minecraft.init.Blocks.CRAFTING_TABLE), 
            new ItemStack[] { new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("thaumicbases", "knowledge_shard"))) });
        chances.put(Item.getItemFromBlock(net.minecraft.init.Blocks.CRAFTING_TABLE), 0.35f);

        recipes.put(net.minecraft.init.Items.ENDER_PEARL, 
            new ItemStack[] { new ItemStack(ItemsTC.curio, 1, 6) });
        chances.put(net.minecraft.init.Items.ENDER_PEARL, 0.45f);

        recipes.put(net.minecraft.init.Items.GHAST_TEAR, 
            new ItemStack[] { new ItemStack(ItemsTC.curio, 1, 6) });
        chances.put(net.minecraft.init.Items.GHAST_TEAR, 0.95f);

        recipes.put(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "condensed_crystal_cluster")),
            new ItemStack[] { 
                new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "fundamental_curiosity"))), 
                new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "dimensional_curiosity"))) 
            });
        chances.put(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "condensed_crystal_cluster")), 0.50f);

        // Додамо рецепт для книги з урахуванням шансів
        recipes.put(net.minecraft.init.Items.BOOK,
            new ItemStack[] { new ItemStack(ItemsTC.curio, 1, random.nextInt(6)) });
        if (oldResearchLoaded) {
            chances.put(net.minecraft.init.Items.BOOK, 0.85f); // Шанс для oldresearch
        } else {
            chances.put(net.minecraft.init.Items.BOOK, 0.60f); // Шанс без oldresearch
        }
    }

    @Override
    public void update() {
        if (!world.isRemote && !inventory.get(0).isEmpty()) {
            burnTime++;

            // Перевіряємо чи минуло 4 секунди (80 тік)
            if (burnTime >= 80) {
                ItemStack inputStack = inventory.get(0);
                ItemStack[] results = getResults(inputStack);

                if (results != null && results.length > 0) {
                    float chance = chances.getOrDefault(inputStack.getItem(), 1.0f);

                    // Якщо крафт спрацював
                    if (random.nextFloat() <= chance) {
                        ItemStack result = results[random.nextInt(results.length)];

                        // Логіка для книги з oldresearch
                        if (inputStack.getItem() == net.minecraft.init.Items.BOOK && oldResearchLoaded) {
                            result = new ItemStack(ItemsTC.curio, 1, 7); // Якщо мод завантажений, видаємо релікт з метаданими 7
                        } else if (inputStack.getItem() == net.minecraft.init.Items.BOOK) {
                            // Стара логіка для книги без oldresearch
                            result = new ItemStack(ItemsTC.curio, 1, random.nextInt(6));
                        }

                        // Нова логіка: для жемчуга, сльози, верстака або книги з oldresearch
                        if ((inputStack.getItem() == net.minecraft.init.Items.ENDER_PEARL ||
                            inputStack.getItem() == net.minecraft.init.Items.GHAST_TEAR ||
                            inputStack.getItem() == Item.getItemFromBlock(net.minecraft.init.Blocks.CRAFTING_TABLE) ||
                            (inputStack.getItem() == net.minecraft.init.Items.BOOK && oldResearchLoaded)) &&
                            !inventory.get(1).isEmpty() &&
                            inventory.get(1).getItem() == result.getItem() &&
                            inventory.get(1).getCount() < inventory.get(1).getMaxStackSize()) {
                            // Додаємо результат до стека, якщо це один із визначених предметів і можна додати до стека
                            inventory.get(1).grow(1);
                        } else if (inventory.get(1).isEmpty()) {
                            // Якщо слот порожній, просто встановлюємо результат
                            inventory.set(1, result.copy());
                        } else {
                            // Якщо слот не порожній, і не можна додати результат — зупиняємо крафт
                            burnTime = 0;
                            return;
                        }

                        inputStack.shrink(1);  // Зменшуємо кількість предметів у верхньому слоті
                        burnTime = 0;
                        markDirty();
                    } else {
                        // Якщо крафт не спрацював, просто зменшуємо кількість предметів у верхньому слоті
                        inputStack.shrink(1);
                        burnTime = 0;
                        markDirty();
                    }
                } else {
                    burnTime = 0; // Скидання таймера, якщо немає відповідного результату
                }
            }
        }
    }




    private ItemStack[] getResults(ItemStack input) {
        ItemStack[] result = recipes.get(input.getItem());
        if (result != null) {
            return result;
        }

        // Якщо рецепт не знайдений і це curio, повертаємо рандомний curio
        if (input.getItem() == ItemsTC.curio) {
            int meta = random.nextInt(6); // Рандом від 0 до 5
            return new ItemStack[] { new ItemStack(ItemsTC.curio, 1, meta) };
        }
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.inventory);
        this.burnTime = compound.getInteger("BurnTime"); // Зчитування burnTime
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, this.inventory);
        compound.setInteger("BurnTime", this.burnTime); // Збереження burnTime
        return compound;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.inventory, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0; // Дозволяємо класти тільки в перший слот (інгредієнти)
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public String getName() {
        return "container.deconstruction_table";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public net.minecraft.util.text.ITextComponent getDisplayName() {
        return new net.minecraft.util.text.TextComponentString(this.getName());
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this &&
               player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }
}
