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

public class ThaumcraftLogisticsSearchTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "thaumcraft.common.container.ContainerLogistics";
    private static final String TARGET_METHOD = "refreshItemList";
    private static final String TARGET_DESC = "(Z)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);

            for (MethodNode mn : cn.methods) {
                if (TARGET_METHOD.equals(mn.name) && TARGET_DESC.equals(mn.desc)) {
                    InsnList insn = new InsnList();
                    insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insn.add(new VarInsnNode(Opcodes.ILOAD, 1));
                    insn.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/coremod/ThaumcraftLogisticsSearchPatch",
                        "refreshItemList",
                        "(Ljava/lang/Object;Z)V",
                        false
                    ));
                    insn.add(new InsnNode(Opcodes.RETURN));

                    mn.instructions.clear();
                    mn.tryCatchBlocks.clear();
                    mn.localVariables = null;
                    mn.instructions.add(insn);
                    mn.maxLocals = 2;
                    mn.maxStack = 2;
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
