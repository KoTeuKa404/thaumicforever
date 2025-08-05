// package com.koteuka404.thaumicforever;

// public class NTPrimal extends NodeType {
//     public NTPrimal(int id) {
//         super(id);
//     }

//     @Override
//     void performTickEvent(EntityAuraNode node) {
//     }

//     @Override
//     void performPeriodicEvent(EntityAuraNode node) {
//     }

//     @Override
//     int calculateStrength(EntityAuraNode node) {
//         int m = node.world.provider.getMoonPhase(node.world.getWorldInfo().getWorldTime());
//         float b = 1.0f + (float)(Math.abs(m - 4) - 2) / 5.0f;
//         return (int)Math.max(1.0, Math.sqrt((float)node.getNodeSize() / 3.0f) * (double)b);
//     }
// }
