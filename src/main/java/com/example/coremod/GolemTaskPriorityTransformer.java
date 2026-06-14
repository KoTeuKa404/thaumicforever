package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class GolemTaskPriorityTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "thaumcraft.common.golems.tasks.TaskHandler";
    private static final int VANILLA_PRIORITY_WEIGHT = 256;
    private static final int STRONG_PRIORITY_WEIGHT = 1000000;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);
            boolean changed = false;

            for (Object rawMethod : cn.methods) {
                MethodNode mn = (MethodNode) rawMethod;
                if (!isTaskSortMethod(mn)) continue;

                for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                    if (insn instanceof IntInsnNode) {
                        IntInsnNode value = (IntInsnNode) insn;
                        if (value.operand == VANILLA_PRIORITY_WEIGHT) {
                            mn.instructions.set(value, new LdcInsnNode(STRONG_PRIORITY_WEIGHT));
                            changed = true;
                        }
                    }
                }
            }

            if (!changed) {
                return basicClass;
            }

            System.out.println("[ThaumicForever-Coremod] Strengthened golem task priority sorting.");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static boolean isTaskSortMethod(MethodNode mn) {
        return ("getBlockTasksSorted".equals(mn.name) || "getEntityTasksSorted".equals(mn.name))
                && mn.desc.startsWith("(ILjava/util/UUID;Lnet/minecraft/entity/Entity;)");
    }
}
