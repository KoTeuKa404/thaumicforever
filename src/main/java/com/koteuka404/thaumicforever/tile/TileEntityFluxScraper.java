package com.koteuka404.thaumicforever.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import thaumcraft.api.aura.AuraHelper;

public class TileEntityFluxScraper extends TileEntity implements ITickable {

    private static final int INTERVAL_TICKS = 400; // 20 seconds (nerfed)
    private static final float DRAIN_AMOUNT = 0.5F; // nerfed
    private int tickCounter = 0;

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }

        tickCounter++;
        if (tickCounter < INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;

        boolean drainFlux = world.rand.nextBoolean();
        if (drainFlux) {
            AuraHelper.drainFlux(world, pos, DRAIN_AMOUNT, false);
        } else {
            AuraHelper.drainVis(world, pos, DRAIN_AMOUNT, false);
        }
    }
}
