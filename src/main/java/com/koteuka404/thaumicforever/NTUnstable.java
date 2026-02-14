package com.koteuka404.thaumicforever;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;

public class NTUnstable extends NTNormal {
    public NTUnstable(int id) {
        super(id);
    }

    @Override
    void performPeriodicEvent(EntityAuraNode node) {
        if (node.stablized) {
            if (node.world.rand.nextFloat() < 0.0001f) { // 0.01% 
                node.setNodeType(0);
            }
            return;
        }

        if (node.world.rand.nextInt(2) == 0) {
            Aspect[] aspects = node.getNodeAspects().getAspects();
            if (aspects.length > 0) {
                Aspect aspect = aspects[node.world.rand.nextInt(aspects.length)];
                int amt = node.getNodeAspects().getAmount(aspect);

                if (amt < 2) return;

                if (!aspect.isPrimal()) {
                    Aspect[] comps = aspect.getComponents();
                    if (comps != null && comps.length > 0) {
                        Aspect primal = comps[node.world.rand.nextInt(comps.length)];
                        spawnAspectWisp(node, primal);
                    }
                } else {
                    spawnAspectWisp(node, aspect);
                }
                node.getNodeAspects().reduce(aspect, 1);
                node.getOriginalAspects().reduce(aspect, 1);
                node.updateSyncAspects();
            }

        }
    }

    void spawnAspectWisp(EntityAuraNode node, Aspect aspect) {
        EntityWisp wisp = new EntityWisp(node.world);
        wisp.setLocationAndAngles(node.posX, node.posY, node.posZ, 0.0f, 0.0f);
        wisp.setType(aspect.getTag());
        node.world.spawnEntity(wisp);
    }

    @Override
    void performTickEvent(EntityAuraNode node) {
        // no-op
    }
}
