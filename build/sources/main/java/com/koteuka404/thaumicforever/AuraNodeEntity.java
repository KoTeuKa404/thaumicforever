package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemsTC;

public class AuraNodeEntity extends Entity {
    private static final float MAX_AURA = 300.0f; 
    private static final float INCREMENT_AMOUNT = 5.0f; 
    private static final int CHUNK_RADIUS = 3;
    private static final int TICKS_PER_SECOND = 20; 
    private int tickCounter = 0; 

    public AuraNodeEntity(World world) {
        super(world);
        this.setSize(0.6F, 0.6F); // Установка розміру сутності
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        tickCounter++;

        if (tickCounter >= TICKS_PER_SECOND) {
            tickCounter = 0;
            increaseAura();
        }
    }

    private void increaseAura() {
        BlockPos nodePos = this.getPosition();

        for (int xOffset = -CHUNK_RADIUS * 16; xOffset <= CHUNK_RADIUS * 16; xOffset += 16) {
            for (int zOffset = -CHUNK_RADIUS * 16; zOffset <= CHUNK_RADIUS * 16; zOffset += 16) {
                BlockPos pos = nodePos.add(xOffset, 0, zOffset);
                float currentVis = AuraHelper.getVis(world, pos);

                if (currentVis < MAX_AURA) {
                    float increaseAmount = Math.min(INCREMENT_AMOUNT, MAX_AURA - currentVis);
                    AuraHelper.addVis(world, pos, increaseAmount);
                }
            }
        }
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            System.out.println("Interaction triggered");

            ItemStack heldItem = player.getHeldItem(hand);
            if (!heldItem.isEmpty() && heldItem.getItem() == ItemsTC.phial) {
                System.out.println("Player is holding a valid phial");

                // Створюємо новий предмет AuraPhial
                ItemStack auraPhial = new ItemStack(ModItems.AuraPhial);
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setFloat("storedAura", MAX_AURA);
                auraPhial.setTagCompound(nbt);

                // Замінюємо предмет у руці
                heldItem.shrink(1); // Зменшуємо кількість порожніх phial
                if (!player.addItemStackToInventory(auraPhial)) {
                    this.entityDropItem(auraPhial, 0.5F); // Якщо інвентар повний, скидаємо предмет
                }

                System.out.println("Exchanged empty phial for aura phial");
                return true;
            } else if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.AuraPhial) {
                System.out.println("Player is holding an aura phial");

                // Логіка взаємодії із заповненим aura phial
                NBTTagCompound nbt = heldItem.getTagCompound();
                if (nbt != null && nbt.hasKey("storedAura")) {
                    float storedAura = nbt.getFloat("storedAura");
                    System.out.println("Stored aura: " + storedAura);

                    // Додаємо ауру в локацію
                    AuraHelper.addVis(world, this.getPosition(), storedAura);

                    // Забираємо використаний предмет
                    heldItem.shrink(1);
                    System.out.println("Aura returned to the node");
                }

                return true;
            } else {
                System.out.println("Player is not holding a valid phial");
            }
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true; // Дозволяємо взаємодію з сутністю
    }
}
