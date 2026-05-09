package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class GolemRangedKitingTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "thaumcraft.common.golems.ai.AIArrowAttack";
    private static final String TARGET_DESC = "()V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);

            for (MethodNode mn : cn.methods) {
                if (TARGET_DESC.equals(mn.desc) && ("updateTask".equals(mn.name) || "func_75246_d".equals(mn.name))) {
                    InsnList insn = new InsnList();
                    insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insn.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/coremod/GolemRangedKitingPatch",
                        "updateTask",
                        "(Ljava/lang/Object;)V",
                        false
                    ));
                    insn.add(new InsnNode(Opcodes.RETURN));

                    mn.instructions.clear();
                    mn.tryCatchBlocks.clear();
                    mn.localVariables = null;
                    mn.instructions.add(insn);
                    mn.maxLocals = 1;
                    mn.maxStack = 1;
                    break;
                }
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }
}
