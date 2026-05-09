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

public class ThaumcraftLogisticsRequestLifetimeTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "thaumcraft.common.lib.network.misc.PacketLogisticsRequestToServer";
    private static final String TARGET_METHOD = "onMessage";
    private static final String TARGET_DESC =
        "(Lthaumcraft/common/lib/network/misc/PacketLogisticsRequestToServer;" +
        "Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)" +
        "Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;";

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
                    InsnList insn = new InsnList();
                    insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    insn.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/coremod/ThaumcraftLogisticsRequestLifetimePatch",
                        "handle",
                        "(Ljava/lang/Object;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;",
                        false
                    ));
                    insn.add(new InsnNode(Opcodes.ARETURN));

                    mn.instructions.clear();
                    mn.tryCatchBlocks.clear();
                    mn.localVariables = null;
                    mn.instructions.add(insn);
                    mn.maxLocals = 3;
                    mn.maxStack = 2;
                    patched = true;
                    break;
                }
            }

            if (!patched) {
                return basicClass;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            System.out.println("[ThaumicForever-Coremod] Patched logistics request lifetime.");
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static boolean isTargetClass(String transformedName, String name) {
        String tn = transformedName == null ? "" : transformedName;
        String n = name == null ? "" : name;
        return TARGET_CLASS.equals(tn) || TARGET_CLASS.equals(n) || tn.endsWith(".PacketLogisticsRequestToServer") || n.endsWith(".PacketLogisticsRequestToServer");
    }
}
