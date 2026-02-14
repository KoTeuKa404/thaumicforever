package com.koteuka404.thaumicforever;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.world.aura.AuraHandler;

public class NTNormal extends NodeType {
    public NTNormal(int id) {
        super(id);
    }

    @Override
    void performTickEvent(EntityAuraNode node) {
        if (node.stablized) return;

    }

    @Override
    void performPeriodicEvent(EntityAuraNode node) {
        if (node.stablized) return;

        if (node.world.isRemote) {
            return;
        }
        Aspect aspect = node.getAspect();
        if (aspect != null && AuraHandler.getVis(node.world, node.getPosition()) < AuraHandler.getAuraBase(node.world, node.getPosition())) {
            AuraHandler.addVis(node.world, node.getPosition(), this.calculateStrength(node));
        }
    }

    @Override
    int calculateStrength(EntityAuraNode node) {
        int m = node.world.provider.getMoonPhase(node.world.getWorldInfo().getWorldTime());
        float b = 1.0f + (float)(Math.abs(m - 4) - 2) / 5.0f;
        return (int)Math.max(1.0, Math.sqrt((float)node.getNodeSize() / 3.0f) * (double)b * 2.5);
    }
}
