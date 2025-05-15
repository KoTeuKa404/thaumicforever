package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.items.ItemsTC;

public class DeconstructionTableTileEntity extends TileEntity implements ITickable {

    private final ItemStackHandler inputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }
    
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }
    };

    private final ItemStackHandler outputHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };

    public int burnTime = 0;
    private final Random random = new Random();
    private final boolean oldResearchLoaded;

    private final Map<Item, ItemStack[]> recipes = new HashMap<>();
    private final Map<Item, Float> chances = new HashMap<>();

    public DeconstructionTableTileEntity() {
        this.oldResearchLoaded = net.minecraftforge.fml.common.Loader.isModLoaded("oldresearch");

        recipes.put(Item.getItemFromBlock(Blocks.CRAFTING_TABLE),
            new ItemStack[]{ new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("thaumicbases", "knowledge_shard"))) } );
        chances.put(Item.getItemFromBlock(Blocks.CRAFTING_TABLE), 0.35f);

        recipes.put(net.minecraft.init.Items.ENDER_PEARL,
            new ItemStack[]{ new ItemStack(ItemsTC.curio, 1, 6) });
        chances.put(net.minecraft.init.Items.ENDER_PEARL, 0.45f);

        recipes.put(net.minecraft.init.Items.GHAST_TEAR,
            new ItemStack[]{ new ItemStack(ItemsTC.curio, 1, 6) });
        chances.put(net.minecraft.init.Items.GHAST_TEAR, 0.95f);

        recipes.put(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "condensed_crystal_cluster")),
            new ItemStack[]{
                new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "fundamental_curiosity"))),
                new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "dimensional_curiosity")))
            });
        chances.put(Item.REGISTRY.getObject(new ResourceLocation("planarartifice", "condensed_crystal_cluster")), 0.50f);

        if (oldResearchLoaded) {
            recipes.put(Item.getItemFromBlock(Blocks.BOOKSHELF),
                new ItemStack[]{ new ItemStack(ItemsTC.curio, 1, 7) });
            chances.put(Item.getItemFromBlock(Blocks.BOOKSHELF), 0.85f);
        } else {
            chances.put(Item.getItemFromBlock(Blocks.BOOKSHELF), 0.60f);
        }
    }


    public Map<Item, ItemStack[]> getRecipes() {
        return this.recipes;
    }
    
    public Map<Item, Float> getChances() {
        return this.chances;
    }
    
    public boolean isOldResearchLoaded() {
        return this.oldResearchLoaded;
    }

    
    @Override
    public void update() {
        if (!world.isRemote && !inputHandler.getStackInSlot(0).isEmpty()) {
            burnTime++;

            if (burnTime >= 80) {
                ItemStack inputStack = inputHandler.getStackInSlot(0);
                ItemStack[] results = getResults(inputStack);

                if (results != null && results.length > 0) {
                    float chance = chances.getOrDefault(inputStack.getItem(), 1.0f);
                    ItemStack outputStack = outputHandler.getStackInSlot(0);

                    boolean stackable = true;
                    if (inputStack.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF) && !oldResearchLoaded) {
                        stackable = false;
                    }

                    if (!outputStack.isEmpty()) {
                        if (stackable) {
                            ItemStack canonical = results[0];
                            if (!(outputStack.getItem() == canonical.getItem() && outputStack.getItemDamage() == canonical.getItemDamage())) {
                                burnTime = 0;
                                return;
                            }
                        } else {
                            burnTime = 0;
                            return;
                        }
                    }

                    if (random.nextFloat() <= chance) {
                        ItemStack result = results[random.nextInt(results.length)];

                        if (outputStack.isEmpty()) {
                            outputHandler.setStackInSlot(0, result.copy());
                        } else {
                            if (stackable) {
                                if (outputStack.getCount() < outputStack.getMaxStackSize()) {
                                    outputStack.grow(1);
                                } else {
                                    burnTime = 0;
                                    return;
                                }
                            } else {
                                burnTime = 0;
                                return;
                            }
                        }

                        inputHandler.getStackInSlot(0).shrink(1);
                    } else {
                        inputHandler.getStackInSlot(0).shrink(1);
                    }

                    burnTime = 0;
                    markDirty();
                } else {
                    burnTime = 0;
                }
            }
        }
    }

    public boolean isProcessing() {
        return burnTime > 0;
    }

    private ItemStack[] getResults(ItemStack input) {
        if (input.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF)) {
            if (oldResearchLoaded) {
                return new ItemStack[]{ new ItemStack(ItemsTC.curio, 1, 7) };
            } else {
                return new ItemStack[]{ new ItemStack(ItemsTC.curio, 1, random.nextInt(6)) };
            }
        }
        ItemStack[] result = recipes.get(input.getItem());
        if (result != null) {
            return result;
        }
        if (input.getItem() == ItemsTC.curio) {
            int meta = random.nextInt(6);
            return new ItemStack[]{ new ItemStack(ItemsTC.curio, 1, meta) };
        }
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("InputHandler")) {
            inputHandler.deserializeNBT(compound.getCompoundTag("InputHandler"));
        }

        if (compound.hasKey("OutputHandler")) {
            outputHandler.deserializeNBT(compound.getCompoundTag("OutputHandler"));
        }

        this.burnTime = compound.getInteger("BurnTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setTag("InputHandler", inputHandler.serializeNBT());
        compound.setTag("OutputHandler", outputHandler.serializeNBT());
        compound.setInteger("BurnTime", this.burnTime);

        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.DOWN) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(outputHandler);
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inputHandler);
            }
        }
        return super.getCapability(capability, facing);
    }

    public ItemStackHandler getInputHandler() {
        return inputHandler;
    }

    public ItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this &&
            player.getDistanceSq((double) this.pos.getX() + 0.5D,
                                 (double) this.pos.getY() + 0.5D,
                                 (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public static boolean isValidInput(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }
}
