// package com.koteuka404.thaumicforever;

// public class NTEnergy extends NTNormal {
//     public NTEnergy(int id) {
//         super(id);
//     }

//     @Override
//     void performPeriodicEvent(EntityAuraNode node) {
//         super.performPeriodicEvent(node);

        
//     }

//     @Override
//     int calculateStrength(EntityAuraNode node) {
//         int moon = node.world.provider.getMoonPhase(node.world.getWorldInfo().getWorldTime());
//         float phaseFactor = 1.0f - (Math.abs(moon - 4) - 2) / 5.0f;
//         phaseFactor += (node.getBrightness() - 0.5f) / 3.0f;
//         return Math.max(1,(int)(Math.sqrt(node.getNodeSize() / 3.0f) * phaseFactor));
//     }
// }
