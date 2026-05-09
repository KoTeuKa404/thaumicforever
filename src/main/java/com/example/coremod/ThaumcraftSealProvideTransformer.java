package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ThaumcraftSealProvideTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "thaumcraft.common.golems.seals.SealProvide";
    private static final String TARGET_METHOD = "tickSeal";
    private static final String TARGET_DESC = "(Lnet/minecraft/world/World;Lthaumcraft/api/golems/seals/ISealEntity;)V";
    private static final String COMPLETION_METHOD = "onTaskCompletion";
    private static final String COMPLETION_DESC = "(Lnet/minecraft/world/World;Lthaumcraft/api/golems/IGolemAPI;Lthaumcraft/api/golems/tasks/Task;)Z";
    private static final String GOLEM_HELPER = "thaumcraft/api/golems/GolemHelper";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !isTargetClass(transformedName, name)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);

            boolean patched = false;
            for (MethodNode mn : cn.methods) {
                if (TARGET_METHOD.equals(mn.name) && TARGET_DESC.equals(mn.desc)) {
                    patched |= patchOverbookingHook(mn);
                    patched |= patchScanIntervals(mn);
                } else if (COMPLETION_METHOD.equals(mn.name) && COMPLETION_DESC.equals(mn.desc)) {
                    patched |= patchRemainderRequests(mn);
                    patched |= patchDeliveryCallback(mn);
                }
            }

            if (!patched) {
                return basicClass;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            System.out.println("[ThaumicForever-Coremod] Patched SealProvide provisioning cleanup.");
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static boolean patchOverbookingHook(MethodNode mn) {
        if (mn.instructions == null || mn.instructions.getFirst() == null) {
            return false;
        }

        org.objectweb.asm.tree.InsnList hook = new org.objectweb.asm.tree.InsnList();
        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hook.add(new VarInsnNode(Opcodes.ALOAD, 2));
        hook.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "com/example/coremod/ThaumcraftLogisticsProvisionPatch",
            "releaseOverbooked",
            "(Lnet/minecraft/world/World;Lthaumcraft/api/golems/seals/ISealEntity;)V",
            false
        ));
        mn.instructions.insertBefore(mn.instructions.getFirst(), hook);
        return true;
    }

    private static boolean patchScanIntervals(MethodNode mn) {
        boolean patched = false;
        for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (!(insn instanceof IntInsnNode)) {
                continue;
            }

            IntInsnNode ii = (IntInsnNode) insn;
            if (ii.operand == 20) {
                ii.operand = 5;
                patched = true;
            }
        }
        return patched;
    }

    private static boolean patchRemainderRequests(MethodNode mn) {
        boolean patched = false;
        for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (!(insn instanceof MethodInsnNode)) {
                continue;
            }

            MethodInsnNode mi = (MethodInsnNode) insn;
            if (!GOLEM_HELPER.equals(mi.owner) || !"requestProvisioning".equals(mi.name)) {
                continue;
            }

            if ("(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V".equals(mi.desc)) {
                mi.owner = "com/example/coremod/ThaumcraftLogisticsProvisionPatch";
                mi.name = "requestRemainderForEntity";
                patched = true;
            } else if ("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/item/ItemStack;)V".equals(mi.desc)) {
                mi.owner = "com/example/coremod/ThaumcraftLogisticsProvisionPatch";
                mi.name = "requestRemainderForPos";
                patched = true;
            }
        }
        return patched;
    }

    private static boolean patchDeliveryCallback(MethodNode mn) {
        boolean patched = false;
        for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (!(insn instanceof MethodInsnNode)) {
                continue;
            }

            MethodInsnNode mi = (MethodInsnNode) insn;
            if (!"thaumcraft/api/golems/ProvisionRequest".equals(mi.owner) || !"setInvalid".equals(mi.name) || !"(Z)V".equals(mi.desc)) {
                continue;
            }

            org.objectweb.asm.tree.InsnList hook = new org.objectweb.asm.tree.InsnList();
            hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
            hook.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "com/example/coremod/ThaumcraftLogisticsRequestLifetimePatch",
                "markDelivered",
                "(Lthaumcraft/api/golems/ProvisionRequest;ZLnet/minecraft/world/World;)V",
                false
            ));
            mn.instructions.insertBefore(mi, hook);
            mn.instructions.remove(mi);
            patched = true;
        }
        return patched;
    }

    private static boolean isTargetClass(String transformedName, String name) {
        String tn = transformedName == null ? "" : transformedName;
        String n = name == null ? "" : name;
        return TARGET_CLASS.equals(tn) || TARGET_CLASS.equals(n) || tn.endsWith(".SealProvide") || n.endsWith(".SealProvide");
    }
}
