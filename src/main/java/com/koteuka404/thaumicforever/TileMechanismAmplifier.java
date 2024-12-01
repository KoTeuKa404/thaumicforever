package com.koteuka404.thaumicforever;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.devices.TileBellows;

public class TileMechanismAmplifier extends TileThaumcraft implements ITickable {

    private int delay = 0;

    @Override
    public void update() {
        // Виконуємо лише на сервері
        if (this.getWorld().isRemote) return;

        delay++;
        if (delay >= 20) { // Раз на секунду
            delay = 0;

            // Логування
            // System.out.println("[MechanismAmplifier] Updating at position: " + this.getPos());

            // Підраховуємо boost
            int totalBoost = calculateTotalBoost();
            // System.out.println("[MechanismAmplifier] Calculated total boost: " + totalBoost);

            // Застосовуємо boost до механізму
            applyBoostToMechanism(totalBoost);
        }
    }

    private int calculateTotalBoost() {
        int totalBoost = 0;

        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos neighborPos = this.getPos().offset(direction);
            TileEntity neighborTile = getWorld().getTileEntity(neighborPos);

            if (neighborTile instanceof TileMechanismAmplifier) {
                totalBoost += 2; // Інший Mechanism Amplifier
            } else if (neighborTile instanceof TileEntityFurnace) {
                int bellowsBoost = countConnectedBellows(neighborPos);
                totalBoost += bellowsBoost;
            }
        }

        return totalBoost;
    }

    private int countConnectedBellows(BlockPos pos) {
        int count = 0;

        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos bellowPos = pos.offset(direction);
            TileEntity tile = getWorld().getTileEntity(bellowPos);

            if (tile instanceof TileBellows) {
                boolean isEnabled = BlockStateUtils.isEnabled(tile.getBlockMetadata());
                boolean isCorrectFacing = BlockStateUtils.getFacing(tile.getBlockMetadata()) == direction.getOpposite();

                if (isEnabled && isCorrectFacing) {
                    count++;
                }
            }
        }

        return count;
    }

    private void applyBoostToMechanism(int boost) {
        EnumFacing facing = BlockStateUtils.getFacing(this.getBlockMetadata());
        BlockPos targetPos = this.getPos().offset(facing);
        TileEntity targetTile = getWorld().getTileEntity(targetPos);

        if (targetTile instanceof TileEntityFurnace) {
            TileEntityFurnace furnace = (TileEntityFurnace) targetTile;

            int cookTime = furnace.getField(2); // Отримуємо поточний cookTime
            if (cookTime > 0 && cookTime < 199) {
                furnace.setField(2, cookTime + boost);
            }
        }
    }
}
